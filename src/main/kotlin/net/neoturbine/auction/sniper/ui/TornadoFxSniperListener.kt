package net.neoturbine.auction.sniper.ui

import net.neoturbine.auction.sniper.SniperListener
import net.neoturbine.auction.sniper.SniperSnapshot
import tornadofx.*

class TornadoFxSniperListener(private val listener: SniperListener) : SniperListener {
    override fun sniperStateChange(sniperSnapshot: SniperSnapshot) {
        runLater { listener.sniperStateChange(sniperSnapshot) }
    }
}
