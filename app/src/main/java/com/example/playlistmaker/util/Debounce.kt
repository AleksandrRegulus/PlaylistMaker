package com.example.playlistmaker.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(delayMillis: Long,
                 coroutineScope: CoroutineScope,
                 reusableDebounce: Boolean,
                 action: (T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (reusableDebounce) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || reusableDebounce) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(param)
            }
        }
    }
}