package net.neoturbine.auction.sniper.ui

import javafx.collections.ObservableList
import net.neoturbine.auction.sniper.*
import tornadofx.*

const val AUCTION_TABLE_ID = "auctionTableId"
const val NEW_ITEM_TEXT_FIELD_ID = "newItemId"
const val STOP_PRICE_TEXT_FIELD = "stopPriceId"
const val JOIN_BUTTON_ID = "joinButtonId"

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
                id = NEW_ITEM_TEXT_FIELD_ID
                promptText = "New Item Id"
            }
            val stopPrice = textfield {
                id = STOP_PRICE_TEXT_FIELD
                promptText = "Stop Price (USD)"
            }
            button {
                id = JOIN_BUTTON_ID
                text = "Join Auction"
                action {
                    listeners.forEach {
                        it.joinAuction(Item(identifier = newItemId.text, stopPrice = stopPrice.text.toInt()))
                        newItemId.clear()
                        stopPrice.clear()
                    }
                }
            }
        }
        tableview(currentSnapshots) {
            id = AUCTION_TABLE_ID
            readonlyColumn("Item ID", SniperSnapshot::itemId)
            readonlyColumn("Status", SniperSnapshot::status) {
                cellFormat { text = it.name }
            }
            readonlyColumn("Last Price (USD)", SniperSnapshot::lastPrice)
            readonlyColumn("Bid Price (USD)", SniperSnapshot::lastBid)
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
