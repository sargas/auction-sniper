package net.neoturbine.auction.sniper.ui

import javafx.collections.ObservableList
import net.neoturbine.auction.sniper.*
import tornadofx.*

const val AUCTION_TABLE = "auctionTableId"

/**
 * The "MainWindow" class in Growing Object-Oriented Software
 */
class RootView: View(), SniperListener, PortfolioListener {
    private var currentSnapshots: ObservableList<SniperSnapshot> =
        mutableListOf<SniperSnapshot>().asObservable()
    private val listeners = mutableListOf<UserRequestListener>()

    override val root = vbox {
        hbox {
            val newItemId = textfield {
                id = "newItemId"
            }
            button {
                id = "joinButton"
                text = "Join Auction"
                action {
                    listeners.forEach {
                        it.joinAuction(newItemId.text)
                        newItemId.clear()
                    }
                }
            }
        }
        tableview(currentSnapshots) {
            id = AUCTION_TABLE
            readonlyColumn("Item ID", SniperSnapshot::itemId)
            readonlyColumn("Status", SniperSnapshot::status) {
                cellFormat { text = it.name }
            }
            readonlyColumn("Last Price", SniperSnapshot::lastPrice)
            readonlyColumn("Bid Price", SniperSnapshot::lastBid)
        }
    }

    override fun sniperStateChange(sniperSnapshot: SniperSnapshot) {
        val existingIndex = currentSnapshots.indexOfFirst { it.itemId == sniperSnapshot.itemId }
        if (existingIndex == -1) {
            currentSnapshots += sniperSnapshot
        } else {
            currentSnapshots[existingIndex] = sniperSnapshot
        }
    }

    fun addUserRequestListener(listener: UserRequestListener) {
        listeners += listener
    }

    override fun addSniper(sniper: AuctionSniper) {
        currentSnapshots += sniper.sniperSnapshot
        sniper.addSniperListener(TornadoFxSniperListener(this))
    }
}
