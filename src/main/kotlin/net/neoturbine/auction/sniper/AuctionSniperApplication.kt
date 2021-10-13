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
    override fun init() {
        joinAuction(
            connection = connectTo(parameters.raw[0], parameters.raw[1].toInt(), parameters.raw[2], parameters.raw[3]),
            itemId = parameters.raw[4]
        )
    }
    private fun joinAuction(connection: XMPPConnection, itemId: String) {
        val chatManager = ChatManager.getInstanceFor(connection)
        val auctionJid = JidCreate.entityBareFrom("auction-$itemId@localhost")
        chatManager.addIncomingListener { from, message, chat ->
            if (!from.equals(auctionJid)) {
                return@addIncomingListener
            }
            runLater {
                outputLabel?.text = SniperStatus.LOST.name
            }
        }
        val chat = chatManager.chatWith(auctionJid)
        chat.send("")
    }
}

private var outputLabel: Label? = null

class RootView: View() {
    override val root = vbox {
        label(SniperStatus.JOINING.name).apply {
            this.id = STATUS_ID
            outputLabel = this
        }
    }
}

enum class SniperStatus {
    JOINING, LOST
}

private fun connectTo(hostname: String, port: Int, username: String, password: String): XMPPConnection {
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
