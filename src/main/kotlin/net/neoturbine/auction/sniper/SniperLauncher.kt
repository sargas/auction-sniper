package net.neoturbine.auction.sniper

import org.apache.logging.log4j.kotlin.logger

class SniperLauncher(private val auctionHouse: AuctionHouse, private val collector: SniperCollector) : UserRequestListener {
    companion object {
        private val logger = logger()
    }
    override fun joinAuction(itemId: String) {
        logger.info {"Joining auction for $itemId" }
        val auction = auctionHouse.auctionFor(itemId = itemId)
        val sniper = AuctionSniper(itemId = itemId, auction = auction) //, listener = TornadoFxSniperListener(ui))
        collector.addSniper(sniper)
        auction.addListener(sniper)
        auction.join()
    }
}
