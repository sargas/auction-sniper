package net.neoturbine.auction.sniper

interface AuctionHouse {
    fun auctionFor(item: Item) : Auction
    fun disconnect()
}
