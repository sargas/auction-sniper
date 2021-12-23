package net.neoturbine.auction.sniper

import org.apache.logging.log4j.kotlin.logger

class SniperLauncher(private val auctionHouse: AuctionHouse, private val collector: SniperCollector) : UserRequestListener {
    companion object {
        private val logger = logger()
    }
    override fun joinAuction(item: Item) {
        logger.info {"Joining auction for $item" }
        val auction = auctionHouse.auctionFor(item)
        val sniper = AuctionSniper(item=item, auction = auction) //, listener = TornadoFxSniperListener(ui))
        collector.addSniper(sniper)
        auction.addListener(sniper)
        auction.join()
    }
}
