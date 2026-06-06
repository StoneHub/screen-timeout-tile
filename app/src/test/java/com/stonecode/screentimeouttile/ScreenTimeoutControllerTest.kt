package com.stonecode.screentimeouttile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenTimeoutControllerTest {

    @Test
    fun shouldReturnUpdatedTimeout_whenStoreAcceptsWrite() {
        val store = FakeScreenTimeoutSettingsStore(currentTimeoutMs = 30_000)
        val controller = ScreenTimeoutController(store, TimeoutTogglePolicy())

        val result = controller.toggleTimeout()

        assertEquals(TimeoutChangeResult.Changed(600_000), result)
        assertEquals(600_000, store.currentTimeoutMs)
    }

    @Test
    fun shouldReturnWriteFailed_whenStoreRejectsWrite() {
        val store = FakeScreenTimeoutSettingsStore(currentTimeoutMs = 30_000, acceptsWrites = false)
        val controller = ScreenTimeoutController(store, TimeoutTogglePolicy())

        val result = controller.toggleTimeout()

        assertEquals(TimeoutChangeResult.WriteFailed, result)
        assertEquals(30_000, store.currentTimeoutMs)
    }

    @Test
    fun shouldDelegatePermissionCheckToStore() {
        val writableStore = FakeScreenTimeoutSettingsStore(canWrite = true)
        val readOnlyStore = FakeScreenTimeoutSettingsStore(canWrite = false)

        assertTrue(ScreenTimeoutController(writableStore).canModifySystemSettings())
        assertFalse(ScreenTimeoutController(readOnlyStore).canModifySystemSettings())
    }

    private class FakeScreenTimeoutSettingsStore(
        var currentTimeoutMs: Int = 30_000,
        private val canWrite: Boolean = true,
        private val acceptsWrites: Boolean = true,
    ) : ScreenTimeoutSettingsStore {

        override fun canWrite(): Boolean = canWrite

        override fun getTimeoutMs(): Int = currentTimeoutMs

        override fun setTimeoutMs(timeoutMs: Int): Boolean {
            if (!acceptsWrites) return false
            currentTimeoutMs = timeoutMs
            return true
        }
    }
}
