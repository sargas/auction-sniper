package net.neoturbine.auction.sniper.ui

import io.mockk.*
import javafx.scene.Scene
import javafx.stage.Stage
import net.neoturbine.auction.sniper.AuctionSniperDriver
import net.neoturbine.auction.sniper.UserRequestListener
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.framework.junit5.Stop
import tornadofx.*

private const val ITEM_ID = "an-item-id"

@ExtendWith(ApplicationExtension::class)
internal class RootViewTest {
    private lateinit var rootView: RootView
    private val driver = AuctionSniperDriver()

    @Suppress("unused")
    @Start
    fun start(stage: Stage) {
        rootView = FX.find(RootView::class.java, Scope())
        stage.scene = Scene(rootView.root)
        stage.show()
    }

    @Suppress("unused")
    @Stop
    fun stop() {
        FxToolkit.cleanupStages()
    }

    @Test
    fun `Makes User Request When Join Button Clicked`() {
        val listener = mockk<UserRequestListener>()
        every { listener.joinAuction(any()) } just runs

        rootView.addUserRequestListener(listener)
        driver.startBiddingFor(ITEM_ID)

        verifyAll {
            listener.joinAuction(ITEM_ID)
        }
    }
}
