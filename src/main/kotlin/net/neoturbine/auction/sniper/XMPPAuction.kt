package net.neoturbine.auction.sniper

import org.jivesoftware.smack.chat2.Chat

class XMPPAuction(private val chat: Chat): Auction {
    override fun bid(amount: Int) {
        chat.send(bidCommand(amount))
    }

    override fun join() {
        chat.send(joinCommand())
    }
}
