package com.vanish.classnetwork.data.relay

import org.junit.Assert.*
import org.junit.Test

class RelayProtocolRulesTest {

    @Test
    fun joinAcceptsValidIdentity() {
        assertTrue(RelayProtocolRules.isValidJoin("device-123", "Sawaira"))
    }

    @Test
    fun joinRejectsBlankDeviceId() {
        assertFalse(RelayProtocolRules.isValidJoin("", "Sawaira"))
    }

    @Test
    fun joinRejectsBlankName() {
        assertFalse(RelayProtocolRules.isValidJoin("device-123", " "))
    }

    @Test
    fun joinRejectsNameLongerThanThirtyCharacters() {
        assertFalse(RelayProtocolRules.isValidJoin("device-123", "A".repeat(31)))
    }

    @Test
    fun welcomeAcceptsProtocolVersionOneAndValidServerTime() {
        assertTrue(RelayProtocolRules.isValidWelcome(1, 1794839291139L))
    }

    @Test
    fun welcomeRejectsUnsupportedProtocolVersion() {
        assertFalse(RelayProtocolRules.isValidWelcome(2, 1794839291139L))
    }

    @Test
    fun welcomeRejectsInvalidServerTime() {
        assertFalse(RelayProtocolRules.isValidWelcome(1, 0L))
    }

    @Test
    fun outgoingMessageAcceptsValidMessage() {
        assertTrue(RelayProtocolRules.isValidMessage("msg-1", "Sawaira", "Hello"))
    }

    @Test
    fun messageRejectsBlankIdAndSender() {
        assertFalse(RelayProtocolRules.isValidMessage("", "Sawaira", "Hello"))
        assertFalse(RelayProtocolRules.isValidMessage("msg-1", "", "Hello"))
    }

    @Test
    fun messageAcceptsOneAndFiveHundredCharacters() {
        assertTrue(RelayProtocolRules.isValidMessage("1", "A", "x"))
        assertTrue(RelayProtocolRules.isValidMessage("2", "A", "x".repeat(500)))
    }

    @Test
    fun messageRejectsMoreThanFiveHundredCharacters() {
        assertFalse(RelayProtocolRules.isValidMessage("msg-1", "Sawaira", "x".repeat(501)))
    }

    @Test
    fun receivedMessageRequiresThirtySecondExpiry() {
        val sentAt = 1_000_000L
        assertTrue(
            RelayProtocolRules.isValidReceivedMessage(
                "msg-1", "Sawaira", "Hello", sentAt, sentAt + 30_000L
            )
        )
    }

    @Test
    fun receivedMessageRejectsIncorrectExpiry() {
        val sentAt = 1_000_000L
        assertFalse(
            RelayProtocolRules.isValidReceivedMessage(
                "msg-1", "Sawaira", "Hello", sentAt, sentAt + 29_000L
            )
        )
    }

    @Test
    fun receivedMessageRejectsInvalidTimestamp() {
        assertFalse(
            RelayProtocolRules.isValidReceivedMessage(
                "msg-1", "Sawaira", "Hello", 0L, 30_000L
            )
        )
    }

    @Test
    fun deliveredAcknowledgementRequiresIdAndPositiveCount() {
        assertTrue(RelayProtocolRules.isValidDelivered("msg-1", 2))
        assertFalse(RelayProtocolRules.isValidDelivered("", 2))
        assertFalse(RelayProtocolRules.isValidDelivered("msg-1", 0))
    }

    @Test
    fun expiredEventRequiresMessageId() {
        assertTrue(RelayProtocolRules.isValidExpired("msg-1"))
        assertFalse(RelayProtocolRules.isValidExpired(""))
    }

    @Test
    fun serverClockOffsetConvertsServerTimeToLocalTime() {
        assertEquals(
            1_000_000L,
            RelayProtocolRules.serverTimeToLocal(1_005_000L, 5_000L)
        )
    }

    @Test
    fun protocolTimingMatchesSpecification() {
        assertEquals(15_000L, RelayProtocolRules.HEARTBEAT_INTERVAL_MS)
        assertEquals(30_000L, RelayProtocolRules.PONG_TIMEOUT_MS)
        assertEquals(30_000L, RelayProtocolRules.MESSAGE_TTL_MS)
    }

    @Test
    fun reconnectDelaysMatchProtocol() {
        assertArrayEquals(
            longArrayOf(1_000L, 2_000L, 4_000L, 8_000L),
            RelayProtocolRules.RECONNECT_DELAYS_MS
        )
    }
}
