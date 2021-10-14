package net.neoturbine.auction.sniper

import io.mockk.*
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.StanzaBuilder
import org.junit.jupiter.api.Test
import org.jxmpp.jid.EntityBareJid

private val UNUSED_CHAT: Chat? = null
private val UNUSED_JID: EntityBareJid? = null

internal class AuctionMessageTranslatorTest {
    private val listener = mockk<AuctionEventListener>()
    private val translator = AuctionMessageTranslator(listener)

    @Test
    fun notifiesAuctionClosedWhenCloseMessageReceived() {
        val message = buildMessage("SOLVersion: 1.1; Event: CLOSE;")
        every { listener.auctionClosed() } just Runs

        translator.newIncomingMessage(UNUSED_JID, message, UNUSED_CHAT)

        verify(exactly = 1) {
            listener.auctionClosed()
        }
    }

    @Test
    fun notifiesBidDetailsWhenCurrentPriceMessageReceived() {
        val message = buildMessage("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;")
        every { listener.currentPrice(192, 7, "Someone else") } just Runs

        translator.newIncomingMessage(UNUSED_JID, message, UNUSED_CHAT)

        verify(exactly = 1) {
            listener.currentPrice(192, 7, "Someone else")
        }
    }


    private fun buildMessage(bodyText: String) = StanzaBuilder.buildMessage().apply {
        body = bodyText
    }.build()
}
