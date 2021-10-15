package net.neoturbine.auction.sniper

interface AuctionEventListener {
    fun auctionClosed()
    fun currentPrice(currentPrice: Int, increment: Int, bidder: PriceSource)

    enum class PriceSource { FromSniper, FromOtherBidder }
}
