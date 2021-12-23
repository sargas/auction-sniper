package net.neoturbine.auction.sniper

enum class SniperStatus {
    JOINING {
        override fun whenAuctionClosed(): SniperStatus {
            return LOST
        }
    }, BIDDING {
        override fun whenAuctionClosed(): SniperStatus {
            return LOST
        }
    }, LOSING {
        override fun whenAuctionClosed(): SniperStatus {
            return LOST
        }
    }, LOST, WINNING {
        override fun whenAuctionClosed(): SniperStatus {
            return WON
        }
    }, WON;

    open fun whenAuctionClosed(): SniperStatus {
        error("Cannot close an already closed auction")
    }
}
