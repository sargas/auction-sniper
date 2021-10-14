package net.neoturbine.auction.sniper

import javafx.scene.control.Label
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.impl.JidCreate
import tornadofx.*

const val STATUS_ID = "auctionStatusId"

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
        runAsync {
            joinAuction(
                connection = connection,
                itemId = parameters.raw[4],
                ui = SniperStateDisplayer()
            )
        }
    }

    override fun stop() {
        super.stop()
        connection.disconnect()
    }

    inner class SniperStateDisplayer: SniperListener {
        private fun showStatus(status: SniperStatus) {
            runLater {
                rootView?.outputLabel?.text = status.name
            }
        }

        override fun sniperLost() {
            showStatus(SniperStatus.LOST)
        }

        override fun sniperBidding() {
            showStatus(SniperStatus.BIDDING)
        }
    }
}

private fun joinAuction(connection: XMPPConnection, itemId: String, ui: SniperListener) {
    val chatManager = ChatManager.getInstanceFor(connection)
    val auctionJid = JidCreate.entityBareFrom("auction-$itemId@localhost")
    val auction = XMPPAuction(chatManager.chatWith(auctionJid))

    chatManager.addIncomingListener(
        AuctionMessageTranslator(
            AuctionSniper(auction, ui)
        )
    )
    auction.join()
}

class RootView: View() {
    var outputLabel: Label? = null

    override val root = vbox {
        label(SniperStatus.JOINING.name).apply {
            this.id = STATUS_ID
            outputLabel = this
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
