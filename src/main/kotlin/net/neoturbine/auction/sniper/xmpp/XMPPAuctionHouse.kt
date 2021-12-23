package net.neoturbine.auction.sniper.xmpp

import net.neoturbine.auction.sniper.Auction
import net.neoturbine.auction.sniper.AuctionHouse
import net.neoturbine.auction.sniper.Item
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration

class XMPPAuctionHouse(private var connection: XMPPTCPConnection) : AuctionHouse {
    override fun auctionFor(item: Item): Auction {
        return XMPPAuction(connection, item.identifier)
    }

    override fun disconnect() {
        connection.disconnect()
    }
}

fun connectToAuctionHouse(hostname: String, port: Int, username: String, password: String) : XMPPAuctionHouse
    = XMPPAuctionHouse(connectTo(hostname, port, username, password))

private fun connectTo(hostname: String, port: Int, username: String, password: String): XMPPTCPConnection {
    val connection = XMPPTCPConnection(
        XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(username, password)
            .setHost(hostname)
            .setPort(port)
            .setXmppDomain("localhost")
            .setResource("AuctionSniper")
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build()
    )
    connection.connect()
    connection.login()
    return connection
}
