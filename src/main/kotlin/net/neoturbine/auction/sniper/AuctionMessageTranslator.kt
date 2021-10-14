package net.neoturbine.auction.sniper

import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid

class AuctionMessageTranslator(private val listener: AuctionEventListener) : IncomingChatMessageListener {
    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        val event = AuctionEvent(message)
        if ("CLOSE" == event.eventType) {
            listener.auctionClosed()
        } else if ("PRICE" == event.eventType) {
            listener.currentPrice(event.currentPrice, event.increment, event.bidder)
        }
    }
}

class AuctionEvent(message: Message?) {
    private val fields = (message?.body ?: "")
        .split(";").associateBy(
            { it.substringBefore(':').trim() },
            { it.substringAfter(':').trim() }
        )

    val eventType: String get() = fields["Event"]!!
    val currentPrice get() = fields["CurrentPrice"]!!.toInt()
    val increment get() = fields["Increment"]!!.toInt()
    val bidder get() = fields["Bidder"]!!
}
