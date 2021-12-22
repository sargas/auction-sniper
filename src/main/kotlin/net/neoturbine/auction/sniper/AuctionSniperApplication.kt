package net.neoturbine.auction.sniper

import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import tornadofx.*

const val AUCTION_TABLE = "auctionTableId"

fun main(args: Array<String>) {
    launch<AuctionSniperApplication>(args)
}

/**
 * Corresponds to the Main class in Growing Object-Oriented Software
 */
class AuctionSniperApplication: App(RootView::class) {
    private lateinit var connection: XMPPTCPConnection
    private var rootView: RootView? = null

    override fun init() {
        connection = connectTo(parameters.raw[0], parameters.raw[1].toInt(), parameters.raw[2], parameters.raw[3])
    }

    override fun onBeforeShow(view: UIComponent) {
        rootView = view as RootView
        val ui = rootView ?: throw IllegalStateException("")
        ui.addUserRequestListener(object : UserRequestListener {
            override fun joinAuction(itemId: String) {
                runAsync {
                    joinAuction(
                        connection = connection,
                        itemId = itemId,
                        ui = ui
                    )
                }
            }
        })
    }

    override fun stop() {
        super.stop()
        connection.disconnect()
    }
}

private class TornadoFxSniperListener(private val listener: SniperListener) : SniperListener {
    override fun sniperStateChange(sniperSnapshot: SniperSnapshot) {
        runLater { listener.sniperStateChange(sniperSnapshot) }
    }
}

private fun joinAuction(connection: XMPPConnection, itemId: String, ui: SniperListener) {
    val chatManager = ChatManager.getInstanceFor(connection)
    val auctionJid = JidCreate.entityBareFrom("auction-$itemId@localhost")
    val chat = chatManager.chatWith(auctionJid)
    val auction = XMPPAuction(chat)

    val sniper = AuctionSniper(itemId, auction, TornadoFxSniperListener(ui))
    chatManager.addIncomingListener(ChatSpecificListener(auctionJid,
        AuctionMessageTranslator(
            connection.user.asEntityBareJid(),
            sniper
        )
    ))
    auction.join()
}

private fun connectTo(hostname: String, port: Int, username: String, password: String): XMPPTCPConnection {
    val connection = XMPPTCPConnection(
        XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(username, password)
            .setHost(hostname)
            .setPort(port)
            .setXmppDomain("localhost")
            .setResource("AuctionSniper")
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()
    )
    connection.connect()
    connection.login()
    return connection
}
