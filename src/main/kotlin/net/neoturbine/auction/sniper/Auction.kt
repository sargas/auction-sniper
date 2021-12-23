package net.neoturbine.auction.sniper

interface Auction {
    fun bid(amount: Int)

    fun join()

    fun addListener(auctionEventListener: AuctionEventListener)
}
