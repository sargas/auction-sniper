package net.neoturbine.auction.sniper

class AuctionSniper(private val auction: Auction, private val listener: SniperListener) : AuctionEventListener {
    override fun auctionClosed() {
        listener.sniperLost()
    }

    override fun currentPrice(currentPrice: Int, increment: Int, bidder: String) {
        listener.sniperBidding()
        auction.bid(currentPrice + increment)
    }
}

interface SniperListener {
    fun sniperLost()
    fun sniperBidding()
}
