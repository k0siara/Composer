package com.patrykkosieradzki.composer.delegates

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SnackbarState(
    val isShown: Boolean = false,
    val message: String = ""
)

interface DisplaysSnackbar {
    val initialSnackbarState: SnackbarState
    val _snackbarState: MutableStateFlow<SnackbarState>
    val snackbarState: StateFlow<SnackbarState>

    fun showSnackbar(message: String)

    fun dismissSnackbar()
}

class DisplaysSnackbarDelegate(
    override val initialSnackbarState: SnackbarState = SnackbarState()
) : DisplaysSnackbar {
    override val _snackbarState: MutableStateFlow<SnackbarState> by lazy {
        MutableStateFlow(initialSnackbarState)
    }
    override val snackbarState: StateFlow<SnackbarState> = _snackbarState.asStateFlow()

    override fun showSnackbar(message: String) {
        _snackbarState.update { it.copy(isShown = true, message = message) }
    }

    override fun dismissSnackbar() {
        _snackbarState.update { it.copy(isShown = false) }
    }
}
