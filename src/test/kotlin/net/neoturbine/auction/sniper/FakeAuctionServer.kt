package net.neoturbine.auction.sniper

import org.apache.logging.log4j.kotlin.logger
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

private const val EJABBERD_IMAGE = "ejabberd/ecs:21.07"
private const val EJABBERD_PORT = 5222

private const val SNIPER_ID = "sniper"
private const val SNIPER_PASSWORD = "sniper"

class FakeAuctionServer {
    companion object {
        private val logger = logger()
    }

    private val xmppServer = GenericContainer(EJABBERD_IMAGE)
        .withExposedPorts(EJABBERD_PORT)
        .waitingFor(Wait.forLogMessage(".*Start accepting TCP connections at .::.:5222 for ejabberd_c2s.*\\n", 1))

    val hostname: String get() = xmppServer.host
    val port: Int get() = xmppServer.getMappedPort(EJABBERD_PORT)

    val sniperUserName = SNIPER_ID
    val sniperPassword = SNIPER_PASSWORD
    val sniperId: EntityBareJid = JidCreate.entityBareFrom("$sniperUserName@localhost")

    fun userNameForItem(itemId: String) = "auction-$itemId"
    val passwordForItem = SNIPER_PASSWORD

    fun start() {
        logger.info { "Starting XMPP server" }
        xmppServer.start()
        logger.info { "Registering sniper account and disabling TLS" }
        xmppServer.execInContainer("bin/ejabberdctl", "register", sniperUserName, "localhost", sniperPassword)
        xmppServer.execInContainer("sed", "-i", "s#starttls_required: true#starttls_required: false#", "conf/ejabberd.yml")
        xmppServer.execInContainer("bin/ejabberdctl", "restart")
        Wait.forLogMessage(".*Start accepting TCP connections at .::.:5222 for ejabberd_c2s.*\\n", 2).waitUntilReady(xmppServer)
    }

    fun stop() {
        logger.info { "Stopping XMPP server server" }
        xmppServer.stop()
    }

    fun createAccountForItem(itemId: String) {
        logger.info { "Creating account for auction $itemId" }
        xmppServer.execInContainer("bin/ejabberdctl", "register", userNameForItem(itemId), "localhost", passwordForItem)
    }

}
