package net.neoturbine.auction.sniper

import io.cucumber.java8.En

@Suppress("unused")
class EndToEndStepDefinitions : En {
    init {
        val auctionServer = FakeAuctionServer()
        lateinit var auction: FakeAuction
        lateinit var application : ApplicationRunner

        Before { ->
            application = ApplicationRunner(auctionServer)
            auctionServer.start()
        }

        Given("an auction for item {string} has started") { itemId: String ->
            auction = FakeAuction(itemId, auctionServer)
            auction.startSellingItem()
        }

        When("we start bidding for item {string}") { itemId: String ->
            application.startBiddingIn(auction)
        }

        Then("server receives join request from sniper for item {string}") { itemId: String ->
            auction.hasReceivedJoinRequestFromSniper()
        }

        When("the auction for item {string} closes") { itemId: String ->
            auction.announceClosed()
        }

        When(
            "the auction reports another bidder has the highest bid at \${int} with an increment of \${int}"
        ) { highestBid: Int, increment: Int ->
            auction.reportPrice(highestBid, increment, "other bidder")
        }

        Then("the sniper places a bid") {
            auction.hasReceivedBid(1098, auctionServer.sniperId)
        }

        Then(
            "we show the auction for item {string} as \"{}\""
        ) { itemId: String, auctionStatus: SniperStatus ->
            application.showsSniperHasStatus(auctionStatus)
        }

        After { -> auction.stop() }
        After { -> application.stop() }
        After { -> auctionServer.stop() }
    }
}
