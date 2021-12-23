package net.neoturbine.auction.sniper

/**
 * An item that is being bidded on
 */
data class Item(val identifier: String, val stopPrice: Int) {
    fun allowsBid(bid: Int): Boolean {
        return bid <= stopPrice
    }
}
