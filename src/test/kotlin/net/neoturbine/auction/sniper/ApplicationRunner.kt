package net.neoturbine.auction.sniper

class ApplicationRunner(private val auctionServer: FakeAuctionServer) {
    private val driver = AuctionSniperDriver()

    fun startBiddingIn(auction: FakeAuction) {
        driver.openWindow(auctionServer.hostname,
            auctionServer.port.toString(),
            auctionServer.sniperUserName,
            auctionServer.sniperPassword,
            auction.itemId
        )
        driver.showsSniperStatus(auction.itemId, SniperStatus.JOINING)
    }

    fun stop() {
        driver.close()
    }

    fun showsBidSnapshot(itemId: String, expectedStatus: SniperStatus, expectedLastPrice: Int? = null, expectedLastBid: Int? = null) {
        driver.showsSniperStatus(itemId, expectedStatus, expectedLastPrice, expectedLastBid)
    }

}
