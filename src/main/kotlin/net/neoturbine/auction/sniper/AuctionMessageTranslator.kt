package net.neoturbine.auction.sniper

import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate

class AuctionMessageTranslator(private val sniperJid: EntityBareJid, private val listener: AuctionEventListener) : IncomingChatMessageListener {
    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        val event = AuctionEvent(message)
        if ("CLOSE" == event.eventType) {
            listener.auctionClosed()
        } else if ("PRICE" == event.eventType) {
            listener.currentPrice(event.currentPrice, event.increment, event.isFrom(sniperJid))
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
    private val bidder get() = fields["Bidder"]!!

    fun isFrom(sniperId: EntityBareJid) : PriceSource =
        if (sniperId == JidCreate.entityBareFrom(bidder)) {
            PriceSource.FromSniper
        } else
            PriceSource.FromOtherBidder
}
