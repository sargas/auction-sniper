package net.neoturbine.auction.sniper.xmpp

import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.EntityBareJid

class ChatSpecificListener(private val chatJid: EntityBareJid, private val wrappedListener: IncomingChatMessageListener) : IncomingChatMessageListener {
    override fun newIncomingMessage(from: EntityBareJid?, message: Message?, chat: Chat?) {
        if (from == chatJid)
            wrappedListener.newIncomingMessage(from, message, chat)
    }

}
