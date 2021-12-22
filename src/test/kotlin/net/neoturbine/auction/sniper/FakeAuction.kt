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
    private val messageListener = SimpleMessageListener()

    private val chat = ChatManager.getInstanceFor(connection)
        .chatWith(JidCreate.entityBareFrom("${auctionServer.sniperUserName}@localhost"))

    fun startSellingItem() {
        auctionServer.createAccountForItem(itemId)

        connection.connect()
        connection.login()
        ChatManager.getInstanceFor(connection).addIncomingListener(messageListener)
    }

    fun hasReceivedJoinRequestFromSniper() {
        messageListener.receivesAMessage {
            assertThat(body).isEqualTo(joinCommand())
            assertThat(from.asBareJid()).isEqualTo(auctionServer.sniperId)
        }
    }

    fun announceClosed() {
        chat.send(bidClosedMessage())
    }

    fun stop() {
        connection.disconnect()
    }

    fun reportPrice(highestBid: Int, incrementalBid: Int, winner: String) {
        chat.send(currentPriceMessage(currentPrice = highestBid, incrementalBid = incrementalBid, bidder = winner))
    }

    fun hasReceivedBid(amount: Int, jid: CharSequence) {
        messageListener.receivesAMessage {
            assertThat(body).isEqualTo(bidCommand(amount))
            assertThat(from.asBareJid()).isEqualTo(jid)
        }
    }
}

class SimpleMessageListener: IncomingChatMessageListener {
    private val messages = ArrayBlockingQueue<Message>(1)
    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        messages += message
    }

    fun receivesAMessage(messageAsserter: Message.() -> Unit) {
        val message = messages.poll(5, TimeUnit.SECONDS)

        assertThat(message).`as`("Expecting to get a message").isNotNull
        message!!.messageAsserter()
    }
}
