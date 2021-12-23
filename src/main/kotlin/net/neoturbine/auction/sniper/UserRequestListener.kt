package net.neoturbine.auction.sniper

@FunctionalInterface
interface UserRequestListener {
    fun joinAuction(item: Item)
}
