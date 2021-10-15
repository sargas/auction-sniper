package net.neoturbine.auction.sniper

import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import java.util.concurrent.atomic.AtomicBoolean

class AuctionSniper(private val auction: Auction, private val listener: SniperListener) : AuctionEventListener {
    private val isWinning: AtomicBoolean = AtomicBoolean(false)

    override fun auctionClosed() {
        if (isWinning.get()) {
            listener.sniperWon()
        } else {
            listener.sniperLost()
        }
    }

    override fun currentPrice(currentPrice: Int, increment: Int, bidder: PriceSource) {
        isWinning.set(bidder == PriceSource.FromSniper)
        if (isWinning.get()) {
            listener.sniperWinning()
        } else {
            listener.sniperBidding()
            auction.bid(currentPrice + increment)
        }
    }
}

interface SniperListener {
    fun sniperLost()
    fun sniperBidding()
    fun sniperWinning()
    fun sniperWon()
}
