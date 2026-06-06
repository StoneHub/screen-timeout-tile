package com.stonecode.screentimeouttile

sealed interface TimeoutChangeResult {
    data class Changed(val timeoutMs: Int) : TimeoutChangeResult
    data object WriteFailed : TimeoutChangeResult
}
