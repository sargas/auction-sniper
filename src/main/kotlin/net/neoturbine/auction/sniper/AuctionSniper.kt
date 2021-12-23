package net.neoturbine.auction.sniper

import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import org.apache.logging.log4j.kotlin.logger

class AuctionSniper(
    private val itemId: String,
    private val auction: Auction
) : AuctionEventListener {
    companion object {
        private val logger = logger()
    }

    var sniperSnapshot = SniperSnapshot.joining(itemId)
    private val listeners = mutableListOf<SniperListener>()

    override fun auctionClosed() {
        logger.info { "Auction for $itemId is closed" }
        sniperSnapshot = sniperSnapshot.closed()
        notifyChange()
    }

    override fun currentPrice(currentPrice: Int, increment: Int, bidder: PriceSource) {
        logger.info { "Received new price for $itemId" }
        if (bidder == PriceSource.FromSniper) {
            sniperSnapshot = sniperSnapshot.winning(currentPrice)
        } else {
            val bid = currentPrice + increment
            sniperSnapshot = sniperSnapshot.bidding(currentPrice, bid)
            auction.bid(bid)
        }
        notifyChange()
    }

    private fun notifyChange() {
        logger.info { "Notifying ${listeners.size} listeners of updates"}
        listeners.forEach { it.sniperStateChange(sniperSnapshot) }
    }

    fun addSniperListener(listener: SniperListener) {
        logger.info { "Adding listener to notify of changes"}
        listeners += listener
        notifyChange()
    }
}

interface SniperListener {
    fun sniperStateChange(sniperSnapshot: SniperSnapshot)
}
