package net.neoturbine.auction.sniper.xmpp

import com.github.javafaker.Faker
import io.mockk.*
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.junit.jupiter.api.Test
import org.jxmpp.jid.impl.JidCreate

private val JID_FOR_TESTS = JidCreate.entityBareFrom(Faker().internet().emailAddress())

internal class ChatSpecificListenerTest {
    private val unusedChat = mockk<Chat>()
    private val unusedMessage = mockk<Message>()
    private val wrappedMessageListener = mockk<IncomingChatMessageListener>()
    private val chatSpecificListener = ChatSpecificListener(JID_FOR_TESTS, wrappedMessageListener)

    @Test
    fun `Passes Messages If From Right User`() {
        every { wrappedMessageListener.newIncomingMessage(any(), any(), any()) } just runs

        chatSpecificListener.newIncomingMessage(JID_FOR_TESTS, unusedMessage, unusedChat)

        verifyAll {
            wrappedMessageListener.newIncomingMessage(JID_FOR_TESTS, unusedMessage, unusedChat)
        }
    }

    @Test
    fun `Skips Message If From Wrong User`() {
        val anotherUser = JidCreate.entityBareFrom(Faker().internet().emailAddress())

        chatSpecificListener.newIncomingMessage(anotherUser, unusedMessage, unusedChat)

        verify { wrappedMessageListener wasNot called }
    }
}
