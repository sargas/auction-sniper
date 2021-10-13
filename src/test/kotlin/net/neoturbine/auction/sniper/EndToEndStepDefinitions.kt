package net.neoturbine.auction.sniper

import io.cucumber.java8.En

@Suppress("unused")
class EndToEndStepDefinitions : En {
    init {
        val auctionServer = FakeAuctionServer()
        lateinit var auction: FakeAuction
        val application = ApplicationRunner(auctionServer)

        Before { ->
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

        Then(
            "we show the auction for item {string} as {string}"
        ) { itemId: String, auctionStatus: String ->
            application.showsSniperHasLostAuction()
        }

        After { -> auction.stop() }
        After { -> application.stop() }
        After { -> auctionServer.stop() }
    }
}
