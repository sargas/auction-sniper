package net.neoturbine.auction.sniper

import org.assertj.core.api.Assertions.assertThat
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class FakeAuction(val itemId: String, private val auctionServer: FakeAuctionServer) {
    private val connection = XMPPTCPConnection(XMPPTCPConnectionConfiguration.builder()
        .setUsernameAndPassword(auctionServer.userNameForItem(itemId), auctionServer.passwordForItem)
        .setHost(auctionServer.hostname)
        .setPort(auctionServer.port)
        .setXmppDomain("localhost")
        .setResource("AuctionTest")
        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
        .build())

    fun startSellingItem() {
        auctionServer.createAccountForItem(itemId)

        connection.connect()
        connection.login()
        ChatManager.getInstanceFor(connection).addIncomingListener(SimpleMessageListener)
    }

    fun hasReceivedJoinRequestFromSniper() {
        SimpleMessageListener.receivesAMessage()
    }

    fun announceClosed() {
        ChatManager.getInstanceFor(connection)
            .chatWith(JidCreate.entityBareFrom("${auctionServer.sniperUserName}@localhost"))
            .send("")
    }

    fun stop() {
        connection.disconnect()
    }
}

object SimpleMessageListener: IncomingChatMessageListener {
    private val messages = ArrayBlockingQueue<Message>(1)
    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        messages += message
    }
    fun receivesAMessage() {
        assertThat(messages.poll(5, TimeUnit.SECONDS)).`as`("Expecting to get a message").isNotNull
    }
}
