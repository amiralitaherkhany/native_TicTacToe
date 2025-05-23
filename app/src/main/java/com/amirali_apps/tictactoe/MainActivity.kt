package com.amirali_apps.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amirali_apps.tictactoe.models.UpdateModel
import com.amirali_apps.tictactoe.network.RetrofitClient
import com.amirali_apps.tictactoe.ui.components.UpdateDialog
import com.amirali_apps.tictactoe.ui.game.GameScreen
import com.amirali_apps.tictactoe.ui.game_mode_selection.GameModeSelectionScreen
import com.amirali_apps.tictactoe.ui.navigation.GameScreens
import com.amirali_apps.tictactoe.ui.theme.TicTacToeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var keepOnScreenCondition = mutableStateOf(true)
        installSplashScreen().setKeepOnScreenCondition {
            keepOnScreenCondition.value
        }
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(null) {
                delay(1000L)
                keepOnScreenCondition.value = false
            }
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                TicTacToeTheme {
                    AppNavHost()
                    var showUpdateDialog by remember { mutableStateOf(false) }
                    var response by remember { mutableStateOf<UpdateModel?>(null) }
                    val context = LocalContext.current
                    LaunchedEffect(null) {
                        withContext(Dispatchers.IO) {
                            try {
                                response = RetrofitClient.retrofitService.checkForUpdate()
                                val latestVersion = response?.latestVersion ?: ""
                                if (latestVersion.isNotEmpty() && latestVersion != BuildConfig.VERSION_NAME && savedInstanceState == null) {
                                    showUpdateDialog = true
                                }
                            } catch (_: Exception) {
                            }
                        }
                    }


                    UpdateDialog(
                        showDialog = showUpdateDialog,
                        onDismiss = { showUpdateDialog = false },
                        onDownloadClick = {
                            when (BuildConfig.FLAVOR) {
                                "myket" -> {
                                    val intent = context.packageManager
                                        .getLaunchIntentForPackage("ir.mservices.market")

                                    if (intent != null) {
                                        val url = "myket://details?id=${context.packageName}"
                                        val intent = Intent()
                                        intent.action = Intent.ACTION_VIEW
                                        intent.data = url.toUri()
                                        context.startActivity(intent)
                                    } else {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            ("https://myket.ir").toUri()
                                        )
                                        context.startActivity(intent)
                                    }
                                }

                                "bazaar" -> {
                                    val intent = context.packageManager
                                        .getLaunchIntentForPackage("com.farsitel.bazaar")
                                    if (intent != null) {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data =
                                            ("bazaar://details?id=${context.packageName}").toUri()
                                        intent.setPackage("com.farsitel.bazaar")
                                        context.startActivity(intent)
                                    } else {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            ("https://cafebazaar.ir").toUri()
                                        )
                                        context.startActivity(intent)
                                    }
                                }
                            }
                            showUpdateDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = GameScreens.GameModeSelection.name
    ) {
        composable(GameScreens.GameModeSelection.name) { GameModeSelectionScreen(navController) }
        composable(
            "${GameScreens.Game.name}/{isPro}/{isAi}/{level}",
            arguments = listOf(
                navArgument("isPro") { type = NavType.BoolType },
                navArgument("isAi") { type = NavType.BoolType },
                navArgument("level") { type = NavType.IntType },
            )
        ) { backStackEntry ->
            GameScreen(
                navController = navController,
            )
        }
    }
}
