package net.neoturbine.auction.sniper

class SniperPortfolio : SniperCollector {
    private val snipers = mutableListOf<AuctionSniper>()
    private val listeners = mutableListOf<PortfolioListener>()

    override fun addSniper(sniper: AuctionSniper) {
        snipers += sniper
        listeners.forEach { it.addSniper(sniper) }
    }

    fun addPortfolioListener(listener: PortfolioListener) {
        listeners += listener
    }
}

interface PortfolioListener {
    fun addSniper(sniper: AuctionSniper)
}
