package net.neoturbine.auction.sniper

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

@Suppress("unused")
class EndToEndStepDefinitions : En {
    init {
        val auctionServer = FakeAuctionServer()
        val auctions = mutableMapOf<String, FakeAuction>()
        lateinit var application : ApplicationRunner

        Before { ->
            application = ApplicationRunner(auctionServer)
            auctionServer.start()
            auctions.clear()
        }

        Given("an auction for item {string} has started") { itemId: String ->
            auctions[itemId] = FakeAuction(itemId, auctionServer).apply {
                startSellingItem()
            }
        }

        When("we start bidding for item {string}") { itemId: String ->
            application.startBiddingIn(listOf(auctions[itemId]!!))
        }

        When("we start bidding for item(s):") { table: DataTable ->
            application.startBiddingIn(table.transpose().column(0).mapNotNull { auctions[it] })
        }

        Then("server receives join request from sniper for item {string}") { itemId: String ->
            auctions[itemId]!!.hasReceivedJoinRequestFromSniper()
        }

        When("the auction for item {string} closes") { itemId: String ->
            auctions[itemId]!!.announceClosed()
        }

        When(
            "the auction for {string} reports {} have/has the highest bid at {int} USD with an increment of {int} USD"
        ) { itemId: String, bidderName: String, highestBid: Int, increment: Int ->
            val bidder = when(bidderName) {
                "we" -> auctionServer.sniperId.toString()
                "another bidder" -> "someone@else"
                else -> error("Unknown bidder: $bidderName")
            }
            auctions[itemId]!!.reportPrice(highestBid, increment, bidder)
        }

        Then("the sniper places a bid on {string} for {int} USD") { itemId: String, bidAmount: Int ->
            auctions[itemId]!!.hasReceivedBid(bidAmount, auctionServer.sniperId)
        }

        Then(
            "we show the auction for item {string} with status {}"
        ) {
                itemId: String, auctionStatus: SniperStatus ->
            application.showsBidSnapshot(itemId, auctionStatus)
        }

        Then(
            "we show the auction for item {string} as {} having the winning bid {int} with our last bid {int}"
        ) {
            itemId: String, auctionStatus: SniperStatus, lastPrice: Int, lastBid: Int ->
            application.showsBidSnapshot(itemId, auctionStatus, lastPrice, lastBid)
        }

        After { -> auctions.values.forEach { it.stop() } }
        After { -> application.stop() }
        After { -> auctionServer.stop() }
    }
}
