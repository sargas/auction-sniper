package net.neoturbine.auction.sniper.xmpp

import net.neoturbine.auction.sniper.AuctionEventListener
import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import org.apache.logging.log4j.kotlin.logger
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate

class AuctionMessageTranslator(private val sniperJid: EntityBareJid) : IncomingChatMessageListener {
    companion object {
        private val logger = logger()
    }
    private val listeners = mutableListOf<AuctionEventListener>()

    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        val event = AuctionEvent(message)
        logger.info { "Received $event" }
        if ("CLOSE" == event.eventType) {
            listeners.forEach { it.auctionClosed() }
        } else if ("PRICE" == event.eventType) {
            listeners.forEach { it.currentPrice(event.currentPrice, event.increment, event.isFrom(sniperJid)) }
        }
    }

    fun addListener(listener: AuctionEventListener) {
        logger.info { "Adding $listener to listeners"}
        listeners += listener
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

    override fun toString(): String {
        return "AuctionEvent(fields='$fields')"
    }

}
