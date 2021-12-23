package net.neoturbine.auction.sniper

import javafx.application.Application
import net.neoturbine.auction.sniper.ui.AUCTION_TABLE_ID
import net.neoturbine.auction.sniper.ui.JOIN_BUTTON_ID
import net.neoturbine.auction.sniper.ui.NEW_ITEM_TEXT_FIELD_ID
import net.neoturbine.auction.sniper.ui.STOP_PRICE_TEXT_FIELD
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
            assertThat(lookup("#$AUCTION_TABLE_ID")?.queryTableView<Map<String, String>>())
                .containsRow(itemId, expectedStatus, expectedLastPrice, expectedLastBid)
        }
    }

    fun startBiddingFor(itemId: String, stopPrice: Int = Int.MAX_VALUE) {
        clickOn(lookup("#$NEW_ITEM_TEXT_FIELD_ID")?.queryTextInputControl()).write(itemId)
        clickOn(lookup("#$STOP_PRICE_TEXT_FIELD")?.queryTextInputControl()).write(stopPrice.toString())
        clickOn(lookup("#$JOIN_BUTTON_ID")?.queryButton())
    }
}
