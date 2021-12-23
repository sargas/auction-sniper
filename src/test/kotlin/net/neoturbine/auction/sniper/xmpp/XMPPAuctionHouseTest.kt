package net.neoturbine.auction.sniper.xmpp

import net.neoturbine.auction.sniper.AuctionEventListener
import net.neoturbine.auction.sniper.FakeAuction
import net.neoturbine.auction.sniper.FakeAuctionServer
import net.neoturbine.auction.sniper.Item
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val ITEM_ID = "myItemId"

internal class XMPPAuctionTest {
    private lateinit var server: FakeAuctionServer
    private lateinit var auctionHouse: XMPPAuctionHouse
    private lateinit var auctionServer: FakeAuction

    @BeforeEach
    fun startConnection() {
        server = FakeAuctionServer().apply {
            start()
        }
        auctionHouse = connectToAuctionHouse(server.hostname, server.port, server.sniperUserName, server.sniperPassword)
        auctionServer = FakeAuction(ITEM_ID, server).apply {
            startSellingItem()
        }
    }

    @Test
    fun `Receives Events From Auction Server After Joining`() {
        val auctionWasClosed = CountDownLatch(1)

        val auction = auctionHouse.auctionFor(Item(ITEM_ID, Int.MAX_VALUE))
        auction.addListener(object: AuctionEventListener {
            override fun auctionClosed() {
                auctionWasClosed.countDown()
            }

            override fun currentPrice(currentPrice: Int, increment: Int, bidder: AuctionEventListener.PriceSource) {
                // Not Implemented
            }
        })

        auction.join()
        auctionServer.hasReceivedJoinRequestFromSniper()
        auctionServer.announceClosed()

        assertThat(auctionWasClosed.await(2, TimeUnit.SECONDS)).`as`("should have been closed").isTrue
    }

    @AfterEach
    fun cleanup() {
        auctionHouse.disconnect()
        auctionServer.stop()
        server.stop()
    }
}
