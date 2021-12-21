package net.neoturbine.auction.sniper

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class SniperStatusTest {
    @ParameterizedTest
    @EnumSource(value=SniperStatus::class, names = ["JOINING", "BIDDING"])
    fun `Auction closed resulting in losing`(sniperStatus: SniperStatus) {
        assertThat(sniperStatus.whenAuctionClosed()).isEqualTo(SniperStatus.LOST)
    }

    @ParameterizedTest
    @EnumSource(value=SniperStatus::class, names = ["WON", "LOST"])
    fun `Cannot close already closed auction`(sniperStatus: SniperStatus) {
        assertThatThrownBy {
            sniperStatus.whenAuctionClosed()
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `Auction closed resulting in winning`() {
        assertThat(SniperStatus.WINNING.whenAuctionClosed()).isEqualTo(SniperStatus.WON)
    }
}
