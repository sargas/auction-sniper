package net.neoturbine.auction.sniper

import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource

class AuctionSniper(
    itemId: String,
    private val auction: Auction,
    private val listener: SniperListener) : AuctionEventListener {
    private var sniperSnapshot = SniperSnapshot.joining(itemId)

    init {
        notifyChange()
    }

    override fun auctionClosed() {
        sniperSnapshot = sniperSnapshot.closed()
        notifyChange()
    }

    override fun currentPrice(currentPrice: Int, increment: Int, bidder: PriceSource) {
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
        listener.sniperStateChange(sniperSnapshot)
    }
}

interface SniperListener {
    fun sniperStateChange(sniperSnapshot: SniperSnapshot)
}
