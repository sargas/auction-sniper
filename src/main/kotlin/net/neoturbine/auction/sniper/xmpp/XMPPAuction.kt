package net.neoturbine.auction.sniper.xmpp

import net.neoturbine.auction.sniper.Auction
import net.neoturbine.auction.sniper.AuctionEventListener
import net.neoturbine.auction.sniper.bidCommand
import net.neoturbine.auction.sniper.joinCommand
import org.apache.logging.log4j.kotlin.logger
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jxmpp.jid.impl.JidCreate

class XMPPAuction(connection : XMPPConnection, private val itemId: String): Auction {
    companion object {
        private val logger = logger()
    }

    private val chatManager = ChatManager.getInstanceFor(connection)
    private val auctionJid = JidCreate.entityBareFrom("auction-$itemId@localhost")
    private val chat = chatManager.chatWith(auctionJid)
    private val translator = AuctionMessageTranslator(connection.user.asEntityBareJid())

    init {
        chatManager.addIncomingListener(ChatSpecificListener(auctionJid, translator))
    }

    override fun bid(amount: Int) {
        logger.info { "Sending Bid Command for $itemId" }
        logger.trace { "Bidding $amount for $itemId" }
        chat.send(bidCommand(amount))
    }

    override fun join() {
        logger.info { "Sending Join Command for $itemId" }
        chat.send(joinCommand())
    }

    override fun addListener(auctionEventListener: AuctionEventListener) {
        translator.addListener(auctionEventListener)
    }
}
