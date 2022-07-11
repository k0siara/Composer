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
package com.patrykkosieradzki.composer.core.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface UiStateManager {
    val initialState: UiState
    val uiState: StateFlow<UiState>
    val currentState: UiState

    fun updateUiStateToLoading()
    fun updateUiStateToSuccess()
    fun updateUiStateToFailure(error: Throwable)
}

class UiStateManagerImpl(
    override val initialState: UiState
) : UiStateManager {
    private val _uiState: MutableStateFlow<UiState> by lazy {
        MutableStateFlow(initialState)
    }
    override val uiState = _uiState.asStateFlow()
    override val currentState: UiState
        get() = uiState.value

    override fun updateUiStateToLoading() {
        update(UiState.Loading)
    }

    override fun updateUiStateToSuccess() {
        update(UiState.Success)
    }

    override fun updateUiStateToFailure(error: Throwable) {
        update(UiState.Failure(error))
    }

    private fun update(state: UiState) {
        _uiState.update { state }
    }
}

fun uiStateManagerDelegate(initialState: UiState) = UiStateManagerImpl(initialState = initialState)
