/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package br.com.joaovq.firstwearapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.expandableButton
import androidx.wear.compose.foundation.expandableItems
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberExpandableState
import androidx.wear.compose.foundation.rememberRevealState
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedCompactChip
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.SwipeToRevealCard
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import br.com.joaovq.firstwearapp.R
import br.com.joaovq.firstwearapp.presentation.event.MainEvent
import br.com.joaovq.firstwearapp.presentation.state.MainState
import br.com.joaovq.firstwearapp.presentation.theme.FirstWearAppTheme
import br.com.joaovq.firstwearapp.presentation.vm.MainViewModel
import br.com.joaovq.firstwearapp.presentation.vm.TimerState
import br.com.joaovq.firstwearapp.presentation.vm.TimerState.RESET
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent { WearApp() }
    }
}

@Composable
fun WearApp() {
    FirstWearAppTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        val mainViewModel = hiltViewModel<MainViewModel>()
        val state by mainViewModel.state.collectAsState(initial = MainState())
        val timerState by mainViewModel.timerState.collectAsState()
        val time by mainViewModel.time.collectAsState()
        MainScreen(
            state = state,
            timerState = timerState,
            onEvent = mainViewModel::onEvent,
            time = time
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class, ExperimentalWearFoundationApi::class)
@Composable
private fun MainScreen(
    state: MainState = MainState(),
    timerState: TimerState = RESET,
    time: String = "",
    onEvent: (MainEvent) -> Unit
) {
    Scaffold(
        timeText = { TimeText() }
    ) {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(navController = navController, startDestination = "home") {
            composable("home") {
                StopWatchScreen(
                    timerState = timerState,
                    onEvent = onEvent,
                    state = state,
                    time = time,
                    stopWatchNavigator = object : StopWatchNavigator {
                        override fun onNavigateToNotifications() {
                            navController.navigate("notifications_list")
                        }

                        override fun onNavigateToRecognizer() {
                            navController.navigate("recognizer_voice")
                        }
                    }
                )
            }
            composable("recognizer_voice") { RecognizerVoiceScreen() }
            composable("notifications_list") {
                val listState = rememberScalingLazyListState()
                val expandableState = rememberExpandableState()
                val list = List(10) { "Notification" }
                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    autoCentering = AutoCenteringParams(0),
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
                                onClick = {
                                    navController.navigate("notification_details/App Notification")
                                },
                                appName = {},
                                time = { Text(text = "now") },
                                title = { Text(text = "App Notification") }
                            ) {
                                Text(text = "Notification description")
                            }
                        }
                    }
                    items(list.take(3).size) {
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
                            TitleCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navController.navigate("notification_details/Notification")
                                },
                                title = {
                                    Text(text = list[it])
                                },
                                time = {
                                    Text(text = "now")
                                }
                            ) {
                                Text(
                                    text = "Notification description",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    expandableItems(expandableState, list.drop(3).size) {
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
                            TitleCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navController.navigate("notification_details/Notification")
                                },
                                title = {
                                    Text(text = list[it])
                                },
                                time = {
                                    Text(text = "now")
                                }
                            ) {
                                Text(
                                    text = "Notification description",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    expandableButton(expandableState) {
                        OutlinedCompactChip(
                            label = {
                                Text("Show More")
                                Spacer(Modifier.size(6.dp))
                                Icon(
                                    painterResource(R.drawable.ic_expand_more_24),
                                    "Expand"
                                )
                            },
                            onClick = { expandableState.expanded = true }
                        )
                    }
                }
            }
            composable(
                "notification_details/{name}",
                arguments = listOf(
                    navArgument("name") {
                        type = NavType.StringType
                        defaultValue = "Notification default title"
                        nullable = false
                    }
                )
            ) {
                val name = it.arguments?.getString("name")
                name?.let {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterVertically
                        )
                    ) {
                        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "description")
                    }
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}