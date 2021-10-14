package net.neoturbine.auction.sniper

fun joinCommand() = "SOLVersion: 1.1; Command: JOIN"

fun bidCommand(bidAmount: Int) = "SOLVersion: 1.1; Command: BID; Price: $bidAmount"

fun currentPriceMessage(currentPrice: Int, incrementalBid: Int, bidder: String) =
    "SOLVersion: 1.1; Event: PRICE; CurrentPrice: $currentPrice; Increment: $incrementalBid; Bidder: $bidder"

fun bidClosedMessage() = "SOLVersion: 1.1; Event: CLOSE;"
