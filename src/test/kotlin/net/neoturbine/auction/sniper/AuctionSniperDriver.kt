package net.neoturbine.auction.sniper

import javafx.application.Application
import net.neoturbine.auction.sniper.ui.AUCTION_TABLE
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class AuctionSniperDriver: FxRobot() {
    private var application: Application? = null

    fun openWindow(vararg args: String) {
        FxToolkit.registerPrimaryStage()
        application = FxToolkit.setupApplication(AuctionSniperApplication::class.java, *args)
    }

    fun close() {
        FxToolkit.cleanupStages()
        FxToolkit.cleanupApplication(application)
    }

    fun showsSniperStatus(
        itemId: String,
        expectedStatus: SniperStatus,
        expectedLastPrice: Int? = null,
        expectedLastBid: Int? = null
    ) {
        await untilAsserted {
            assertThat(lookup("#$AUCTION_TABLE")?.queryTableView<Map<String, String>>())
                .containsRow(itemId, expectedStatus, expectedLastPrice, expectedLastBid)
        }
    }

    fun startBiddingFor(itemId: String) {
        clickOn(lookup("#newItemId")?.queryTextInputControl()).write(itemId)
        clickOn(lookup("#joinButton")?.queryButton())
    }
}
