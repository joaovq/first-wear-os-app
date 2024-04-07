package br.com.joaovq.firstwearapp.presentation.event

sealed class MainEvent {
    data class ChangeIsNewUserPreference(val value: Boolean) : MainEvent()
    data object StartTimer : MainEvent()
    data object PauseTimer : MainEvent()
    data object ResetTimer : MainEvent()
}
