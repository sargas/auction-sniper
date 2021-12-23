package net.neoturbine.auction.sniper

import com.github.javafaker.Faker
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifySequence
import net.neoturbine.auction.sniper.AuctionEventListener.PriceSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

private val faker = Faker()

private val itemId = faker.number().digits(3)
private const val MAX_BID = 2000

@ExtendWith(MockKExtension::class)
internal class AuctionSniperTest {
    private val auction = mockk<Auction>(relaxed = true)
    private val listener = mockk<SniperListener>(relaxed = true)

    private val sniper = AuctionSniper(Item(itemId, MAX_BID), auction).apply { addSniperListener(listener) }

    @Test
    fun reportsLostWhenAuctionClosedImmediately() {
        sniper.auctionClosed()

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.LOST))
        }
    }

    @Test
    fun reportsLostWhenAuctionClosesWhenBidding() {
        sniper.currentPrice(111, 222, PriceSource.FromOtherBidder)
        sniper.auctionClosed()

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.BIDDING, 111, 333))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.LOST, 111, 333))
        }
    }

    @Test
    fun reportsWonWhenAuctionClosesWhenWinning() {
        sniper.currentPrice(111, 222, PriceSource.FromSniper)
        sniper.auctionClosed()

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.WINNING, 111, 111))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.WON, 111, 111))
        }
    }

    @Test
    fun bidHigherAndReportsBiddingWhenNewPriceArrives() {
        val price = 1001
        val increment = 25
        val bid = price + increment

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder)

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            auction.bid(bid)
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.BIDDING, price, bid))
        }
    }

    @Test
    fun reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(111, 222, PriceSource.FromSniper)

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.WINNING, 111, 111))
        }
    }

    @Test
    fun `Does not bid and reports losing if first price is above stop price`() {
        sniper.currentPrice(MAX_BID, 222, PriceSource.FromOtherBidder)

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.LOSING, lastPrice = MAX_BID))
        }
    }

    @Test
    fun `Does not bid and reports losing if subsequent price is above stop price`() {
        sniper.currentPrice(111, 222, PriceSource.FromOtherBidder)
        sniper.currentPrice(MAX_BID, 222, PriceSource.FromOtherBidder)

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.BIDDING, 111, 333))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.LOSING, MAX_BID, 333))
        }
    }


    @Test
    fun `Reports lost if auction closes while losing`() {
        sniper.currentPrice(MAX_BID, 222, PriceSource.FromOtherBidder)
        sniper.auctionClosed()

        verifySequence {
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.JOINING))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.LOSING, lastPrice = MAX_BID))
            listener.sniperStateChange(SniperSnapshot(itemId, SniperStatus.LOST, lastPrice = MAX_BID))
        }
    }
}
