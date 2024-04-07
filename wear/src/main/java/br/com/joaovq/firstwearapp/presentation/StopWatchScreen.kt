package br.com.joaovq.firstwearapp.presentation

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberRevealState
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.SwipeToRevealCard
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.Text
import br.com.joaovq.firstwearapp.R
import br.com.joaovq.firstwearapp.presentation.event.MainEvent
import br.com.joaovq.firstwearapp.presentation.state.MainState
import br.com.joaovq.firstwearapp.presentation.vm.TimerState
import br.com.joaovq.firstwearapp.presentation.vm.TimerState.*
import java.time.Duration
import java.time.LocalDateTime


@OptIn(ExperimentalWearFoundationApi::class, ExperimentalWearMaterialApi::class)
@Composable
fun StopWatchScreen(
    modifier: Modifier = Modifier,
    state: MainState = MainState(),
    timerState: TimerState = RESET,
    time: String = "",
    onEvent: (MainEvent) -> Unit = {},
    stopWatchNavigator: StopWatchNavigator
) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        autoCentering = AutoCenteringParams(itemIndex = 0),
        state = listState,
        flingBehavior = ScalingLazyColumnDefaults.snapFlingBehavior(state = listState)
    ) {

        item {
            val revealState = rememberRevealState()
            SwipeToRevealCard(
                modifier = Modifier.fillMaxWidth(),
                primaryAction = {
                    SwipeToRevealPrimaryAction(
                        revealState = revealState,
                        onClick = { /*TODO*/ },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                            )
                        },
                        label = { /*TODO*/ }
                    )
                },
                revealState = revealState,
                onFullSwipe = { /*TODO*/ }
            ) {
                AppCard(
                    onClick = { /*TODO*/ }, appName = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    time = {
                        val duration = Duration.between(
                            LocalDateTime.now().minusMinutes(20),
                            LocalDateTime.now()
                        )
                        Text(text = duration.toMinutes().toString())
                    },
                    title = {
                        Text(text = stringResource(R.string.title_notification))
                    }
                ) {
                    Text(text = stringResource(R.string.label_notification_description))
                }
            }
        }
        item {
            val iconPlayer by remember(timerState) {
                derivedStateOf {
                    when (timerState) {
                        RESET, PAUSED -> Icons.Default.PlayArrow
                        RUNNING -> Icons.Default.Pause
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = time)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        modifier = Modifier.testTag("control-button"),
                        onClick = {
                            when (timerState) {
                                RUNNING -> onEvent(MainEvent.PauseTimer)
                                PAUSED,
                                RESET -> onEvent(MainEvent.StartTimer)
                            }
                        }
                    ) {
                        Icon(imageVector = iconPlayer, contentDescription = null)
                    }
                    OutlinedButton(
                        modifier = Modifier.testTag("reset-button"),
                        onClick = {
                            onEvent(MainEvent.ResetTimer)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                    }
                }
            }
        }
        item {
            CompactChip(
                onClick = stopWatchNavigator::onNavigateToRecognizer /*{
                    onEvent(
                        MainEvent.ChangeIsNewUserPreference(!state.isNewUser)
                    )
                }*/,
                colors = ChipDefaults.chipColors(),
                border = ChipDefaults.chipBorder(),
                label = {
                    Text(
                        text = "Recognizer voice",
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
        item {
            CompactChip(
                onClick = stopWatchNavigator::onNavigateToNotifications /*{
                    onEvent(
                        MainEvent.ChangeIsNewUserPreference(!state.isNewUser)
                    )
                }*/,
                colors = ChipDefaults.chipColors(),
                border = ChipDefaults.chipBorder(),
                label = {
                    Text(
                        text = "Notifications",
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}

interface StopWatchNavigator {
    fun onNavigateToNotifications()
    fun onNavigateToRecognizer()
}