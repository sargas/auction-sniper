package net.neoturbine.auction.sniper

interface AuctionHouse {
    fun auctionFor(itemId: String) : Auction
    fun disconnect()
}
