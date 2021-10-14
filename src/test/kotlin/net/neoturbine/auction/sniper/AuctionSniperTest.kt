package net.neoturbine.auction.sniper

import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifyAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class AuctionSniperTest {
    private val auction = mockk<Auction>(relaxed = true)
    private val listener = mockk<SniperListener>(relaxed = true)

    private val sniper = AuctionSniper(auction, listener)

    @Test
    fun reportsLostWhenAuctionClosed() {
        sniper.auctionClosed()

        verifyAll {
            listener.sniperLost()
        }
    }

    @Test
    fun bidHigherAndReportsBiddingWhenNewPriceArrives() {
        val amount = 1001
        val increment = 25

        sniper.currentPrice(amount, increment, "other bidder")

        verifyAll {
            listener.sniperBidding()
            auction.bid(amount + increment)
        }
    }
}
