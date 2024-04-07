package br.com.joaovq.firstwearapp.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.joaovq.data.user.UserRepository
import br.com.joaovq.firstwearapp.presentation.event.MainEvent
import br.com.joaovq.firstwearapp.presentation.state.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state: MutableSharedFlow<MainState> = MutableStateFlow(MainState())
    private val isNewUser = userRepository.getIsNewUser()
    val state = isNewUser.map {
        MainState(it)
    }.shareIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000))
    private val _timerState: MutableStateFlow<TimerState> = MutableStateFlow(TimerState.RESET)
    val timerState = _timerState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS")

    private val _time = MutableStateFlow(0L)
    val time = _time.map { millis ->
        LocalTime.ofNanoOfDay(millis * 1_000_000).format(formatter)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(
            5_000
        ),
        "00:00:00:000"
    )

    init {
        _timerState
            .flatMapLatest { timerState ->
                getTimerFlow(isRunning = timerState == TimerState.RUNNING)
            }
            .onEach { timeDiff ->
                _time.update { it + timeDiff }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.ChangeIsNewUserPreference -> {
                viewModelScope.launch(Dispatchers.IO) {
                    userRepository.setIsNewUser(event.value)
                }
            }

            is MainEvent.StartTimer -> {
                _timerState.update { TimerState.RUNNING }
            }

            is MainEvent.PauseTimer -> {
                _timerState.update { TimerState.PAUSED }
            }

            MainEvent.ResetTimer -> {
                _timerState.update { TimerState.RESET }
                _time.update { 0L }
            }
        }
    }

    private fun getTimerFlow(isRunning: Boolean): Flow<Long> {
        return flow {
            var startMillis = System.currentTimeMillis()
            while(isRunning) {
                val currentMillis = System.currentTimeMillis()
                val timeDiff = if(currentMillis > startMillis) {
                    currentMillis - startMillis
                } else 0L
                emit(timeDiff)
                startMillis = System.currentTimeMillis()
                delay(10L)
            }
        }
    }
}

enum class TimerState {
    PAUSED, RUNNING, RESET
}