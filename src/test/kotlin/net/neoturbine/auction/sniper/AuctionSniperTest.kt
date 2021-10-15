package net.neoturbine.auction.sniper

import io.mockk.confirmVerified
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifyAll
import io.mockk.verifySequence
import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class AuctionSniperTest {
    private val auction = mockk<Auction>(relaxed = true)
    private val listener = mockk<SniperListener>(relaxed = true)

    private val sniper = AuctionSniper(auction, listener)

    @Test
    fun reportsLostWhenAuctionClosedImmediately() {
        sniper.auctionClosed()

        verifyAll {
            listener.sniperLost()
        }
    }

    @Test
    fun reportsLostWhenAuctionClosesWhenBidding() {
        sniper.currentPrice(111, 222, PriceSource.FromOtherBidder)
        sniper.auctionClosed()

        verifySequence {
            listener.sniperBidding()
            listener.sniperLost()
        }
        confirmVerified(listener)
    }

    @Test
    fun reportsWonWhenAuctionClosesWhenWinning() {
        sniper.currentPrice(111, 222, PriceSource.FromSniper)
        sniper.auctionClosed()

        verifySequence {
            listener.sniperWinning()
            listener.sniperWon()
        }
        confirmVerified(listener)
    }

    @Test
    fun bidHigherAndReportsBiddingWhenNewPriceArrives() {
        val amount = 1001
        val increment = 25

        sniper.currentPrice(amount, increment, PriceSource.FromOtherBidder)

        verifyAll {
            listener.sniperBidding()
            auction.bid(amount + increment)
        }
    }

    @Test
    fun reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(111, 222, PriceSource.FromSniper)

        verifyAll {
            listener.sniperWinning()
        }
    }
}
