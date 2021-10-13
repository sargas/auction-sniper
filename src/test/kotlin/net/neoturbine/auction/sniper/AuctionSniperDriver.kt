package net.neoturbine.auction.sniper

import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.util.WaitForAsyncUtils.waitFor
import java.util.concurrent.TimeUnit

class AuctionSniperDriver: FxRobot() {
    fun openWindow(vararg args: String) {
        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication(AuctionSniperApplication::class.java, *args)
    }

    fun close() = FxToolkit.cleanupStages()

    fun showsSniperStatus(sniperStatus: SniperStatus) {
        waitFor(5, TimeUnit.SECONDS) { lookup("#$STATUS_ID")?.queryLabeled()?.text == sniperStatus.name }
    }
}
