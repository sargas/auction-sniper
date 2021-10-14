package net.neoturbine.auction.sniper

import javafx.application.Application
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils.waitFor
import java.util.concurrent.TimeUnit

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

    fun showsSniperStatus(sniperStatus: SniperStatus) {
        waitFor(5, TimeUnit.SECONDS) { lookup("#$STATUS_ID")?.queryLabeled()?.text == sniperStatus.name }
    }
}
