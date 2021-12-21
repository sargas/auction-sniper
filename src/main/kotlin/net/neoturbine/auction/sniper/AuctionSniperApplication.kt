package net.neoturbine.auction.sniper

import javafx.collections.ObservableList
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

class AuctionSniperApplication: App(RootView::class) {
    private lateinit var connection: XMPPTCPConnection
    private var rootView: RootView? = null

    override fun init() {
        connection = connectTo(parameters.raw[0], parameters.raw[1].toInt(), parameters.raw[2], parameters.raw[3])
    }

    override fun onBeforeShow(view: UIComponent) {
        rootView = view as RootView
        val ui = rootView ?: throw IllegalStateException("")
        runAsync {
            joinAuction(
                connection = connection,
                itemId = parameters.raw[4],
                ui = ui
            )
        }
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
    val auction = XMPPAuction(chatManager.chatWith(auctionJid))

    val sniper = AuctionSniper(itemId, auction, TornadoFxSniperListener(ui))
    chatManager.addIncomingListener(
        AuctionMessageTranslator(
            connection.user.asEntityBareJid(),
            sniper
        )
    )
    auction.join()
}

class RootView: View(), SniperListener {
    private var currentSnapshots: ObservableList<SniperSnapshot> =
        mutableListOf<SniperSnapshot>().asObservable()

    override val root = vbox {
        tableview(currentSnapshots) {
            id = AUCTION_TABLE
            readonlyColumn("Item ID", SniperSnapshot::itemId)
            readonlyColumn("Status", SniperSnapshot::status) {
                cellFormat { text = it.name }
            }
            readonlyColumn("Last Price", SniperSnapshot::lastPrice)
            readonlyColumn("Bid Price", SniperSnapshot::lastBid)
        }
    }

    override fun sniperStateChange(sniperSnapshot: SniperSnapshot) {
        if (currentSnapshots.isEmpty()) {
            currentSnapshots += sniperSnapshot
        } else {
            currentSnapshots[0] = sniperSnapshot
        }
    }
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
