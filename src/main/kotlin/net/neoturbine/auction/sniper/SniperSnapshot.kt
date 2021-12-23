package net.neoturbine.auction.sniper

/**
 * A snapshot of the Sniper's relationship with an auction at one moment of time
 */
data class SniperSnapshot(
    val itemId: String,
    val status: SniperStatus,
    val lastPrice: Int? = null,
    val lastBid: Int? = null
) {
    fun bidding(newLastPrice: Int, newLastBid: Int) =
        SniperSnapshot(itemId, SniperStatus.BIDDING, newLastPrice, newLastBid)
    fun winning(newLastPrice: Int) =
        SniperSnapshot(itemId, SniperStatus.WINNING, newLastPrice, newLastPrice)

    fun closed() =
        SniperSnapshot(itemId, status.whenAuctionClosed(), lastPrice, lastBid)

    fun losing(newLastPrice: Int) =
        SniperSnapshot(itemId, SniperStatus.LOSING, newLastPrice, lastBid)

    companion object {
        fun joining(itemId: String) =
            SniperSnapshot(itemId, SniperStatus.JOINING)
    }
}
