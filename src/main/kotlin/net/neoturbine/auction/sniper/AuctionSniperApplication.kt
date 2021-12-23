package net.neoturbine.auction.sniper

import net.neoturbine.auction.sniper.ui.RootView
import net.neoturbine.auction.sniper.xmpp.connectToAuctionHouse
import org.apache.logging.log4j.kotlin.logger
import tornadofx.*

fun main(args: Array<String>) {
    launch<AuctionSniperApplication>(args)
}

/**
 * Corresponds to the Main class in Growing Object-Oriented Software
 */
class AuctionSniperApplication: App(RootView::class) {
    companion object {
        private val logger = logger()
    }
    private lateinit var auctionHouse: AuctionHouse
    private var rootView: RootView? = null
    private val portfolio = SniperPortfolio()

    override fun init() {
        logger.info { "Connecting to ${parameters.raw[2]}@${parameters.raw[0]}:${parameters.raw[1]}" }
        auctionHouse = connectToAuctionHouse(parameters.raw[0], parameters.raw[1].toInt(), parameters.raw[2], parameters.raw[3])
    }

    override fun onBeforeShow(view: UIComponent) {
        rootView = view as RootView
        val ui = rootView ?: throw IllegalStateException("")
        portfolio.addPortfolioListener(ui)
        ui.addUserRequestListener(SniperLauncher(auctionHouse, portfolio))
    }

    override fun stop() {
        super.stop()
        logger.info("Closing connection")
        auctionHouse.disconnect()
    }
}

