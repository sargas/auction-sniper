package net.neoturbine.auction.sniper.xmpp

import io.mockk.*
import net.neoturbine.auction.sniper.AuctionEventListener
import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.StanzaBuilder
import org.junit.jupiter.api.Test
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate

private val UNUSED_CHAT: Chat? = null
private val UNUSED_JID_OF_AUCTION_SERVER: EntityBareJid? = null
private val SNIPER_ID = JidCreate.entityBareFrom("test@localhost")

internal class AuctionMessageTranslatorTest {
    private val listener = mockk<AuctionEventListener>()
    private val translator = AuctionMessageTranslator(SNIPER_ID).apply { addListener(listener) }

    @Test
    fun notifiesAuctionClosedWhenCloseMessageReceived() {
        val message = buildMessage("SOLVersion: 1.1; Event: CLOSE;")
        every { listener.auctionClosed() } just Runs

        translator.newIncomingMessage(UNUSED_JID_OF_AUCTION_SERVER, message, UNUSED_CHAT)

        verify(exactly = 1) {
            listener.auctionClosed()
        }
    }

    @Test
    fun notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        val message = buildMessage("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone@else;")
        every { listener.currentPrice(192, 7, PriceSource.FromOtherBidder) } just Runs

        translator.newIncomingMessage(UNUSED_JID_OF_AUCTION_SERVER, message, UNUSED_CHAT)

        verify(exactly = 1) {
            listener.currentPrice(192, 7, PriceSource.FromOtherBidder)
        }
    }

    @Test
    fun notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        val message = buildMessage("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: $SNIPER_ID;")
        every { listener.currentPrice(192, 7, PriceSource.FromSniper) } just Runs

        translator.newIncomingMessage(UNUSED_JID_OF_AUCTION_SERVER, message, UNUSED_CHAT)

        verify(exactly = 1) {
            listener.currentPrice(192, 7, PriceSource.FromSniper)
        }
    }


    private fun buildMessage(bodyText: String) = StanzaBuilder.buildMessage().apply {
        body = bodyText
    }.build()
}
