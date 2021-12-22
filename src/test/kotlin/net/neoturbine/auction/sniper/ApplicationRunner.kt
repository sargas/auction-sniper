package net.neoturbine.auction.sniper

class ApplicationRunner(private val auctionServer: FakeAuctionServer) {
    private val driver = AuctionSniperDriver()

    fun startBiddingIn(auctions: List<FakeAuction>) {
        driver.openWindow(auctionServer.hostname,
            auctionServer.port.toString(),
            auctionServer.sniperUserName,
            auctionServer.sniperPassword,
            *auctions.map { it.itemId }.toTypedArray()
        )

        for (auction in auctions) {
            driver.showsSniperStatus(auction.itemId, SniperStatus.JOINING)
        }
    }

    fun stop() {
        driver.close()
    }

    fun showsBidSnapshot(itemId: String, expectedStatus: SniperStatus, expectedLastPrice: Int? = null, expectedLastBid: Int? = null) {
        driver.showsSniperStatus(itemId, expectedStatus, expectedLastPrice, expectedLastBid)
    }

}
