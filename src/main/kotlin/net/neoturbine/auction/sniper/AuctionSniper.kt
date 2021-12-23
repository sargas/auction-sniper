package net.neoturbine.auction.sniper

import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import org.apache.logging.log4j.kotlin.logger

class AuctionSniper(
    private val item: Item,
    private val auction: Auction
) : AuctionEventListener {
    companion object {
        private val logger = logger()
    }

    var sniperSnapshot = SniperSnapshot.joining(item.identifier)
    private val listeners = mutableListOf<SniperListener>()

    override fun auctionClosed() {
        logger.info { "Auction for $item is closed" }
        sniperSnapshot = sniperSnapshot.closed()
        notifyChange()
    }

    override fun currentPrice(currentPrice: Int, increment: Int, bidder: PriceSource) {
        logger.info { "Received new price for $item" }
        when (bidder) {
            PriceSource.FromSniper -> {
                sniperSnapshot = sniperSnapshot.winning(currentPrice)
            }
            PriceSource.FromOtherBidder -> {
                val bid = currentPrice + increment
                if (item.allowsBid(bid)) {
                    sniperSnapshot = sniperSnapshot.bidding(currentPrice, bid)
                    auction.bid(bid)
                } else {
                    sniperSnapshot = sniperSnapshot.losing(currentPrice)
                }
            }
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
