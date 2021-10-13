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
        driver.showsSniperStatus(SniperStatus.JOINING)
    }

    fun showsSniperHasLostAuction() {
        driver.showsSniperStatus(SniperStatus.LOST)
    }

    fun stop() {
        driver.close()
    }

}
