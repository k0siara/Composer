/*
 * Copyright (C) 2022 Patryk Kosieradzki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.patrykkosieradzki.composer.core.event

import androidx.lifecycle.LifecycleOwner
import com.patrykkosieradzki.composer.utils.launchInLifecycle
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update


class StateFlowEvent<T> {
    private val _eventState by lazy { MutableStateFlow<EventState<T>?>(null) }
    private val _firedState by lazy { MutableStateFlow(false) }

    data class EventState<T>(
        val id: String = UUID.randomUUID().toString(),
        val eventValue: T
    )

    fun flow() = _eventState.asStateFlow()
    fun firedFlow() = _firedState.asStateFlow()

    fun fireEvent(t: T) {
        _eventState.update { EventState(eventValue = t) }
        _firedState.update { true }
    }

    fun onEventHandled() {
        _eventState.update { null }
        _firedState.update { false }
    }
}

fun StateFlowEvent<Unit>.fireEvent() = fireEvent(Unit)

fun <T> StateFlowEvent<T>.observe(
    lifecycleOwner: LifecycleOwner,
    handleEvent: (T) -> Unit
) {
    combine(
        firedFlow(),
        flow(),
        transform = { fired, state ->
            if (fired) state else null
        }
    ).filterNotNull()
        .onEach { eventState ->
            handleEvent.invoke(eventState.eventValue)
            onEventHandled()
        }
        .launchInLifecycle(lifecycleOwner)
}

fun <T> StateFlowEvent<T>.observe(
    scope: CoroutineScope,
    handleEvent: (T) -> Unit
) {
    combine(
        firedFlow(),
        flow(),
        transform = { fired, state ->
            if (fired) state else null
        }
    ).filterNotNull()
        .onEach { eventState ->
            handleEvent.invoke(eventState.eventValue)
            onEventHandled()
        }
        .launchIn(scope)
}
