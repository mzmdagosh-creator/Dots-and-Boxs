package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GameScreen
import com.example.ui.SetupScreen
import com.example.ui.SingleFileHtmlDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DotsAndBoxesApp()
        }
    }
}

@Composable
fun DotsAndBoxesApp(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyApplicationTheme(themeMode = uiState.themeMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Crossfade(
                targetState = uiState.currentScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "screenTransition"
            ) { destination ->
                when (destination) {
                    ScreenDestination.SETUP -> {
                        SetupScreen(
                            themeMode = uiState.themeMode,
                            soundEnabled = uiState.soundEnabled,
                            selectedGameMode = uiState.selectedGameMode,
                            selectedCpuDifficulty = uiState.selectedCpuDifficulty,
                            selectedPlayerCount = uiState.selectedPlayerCount,
                            playerInputs = uiState.playerInputs,
                            sessionStats = uiState.sessionStats,
                            errorResId = uiState.setupErrorResId,
                            onSelectThemeMode = viewModel::selectThemeMode,
                            onSetSoundEnabled = viewModel::setSoundEnabled,
                            onSelectGameMode = viewModel::selectGameMode,
                            onSelectCpuDifficulty = viewModel::selectCpuDifficulty,
                            onSelectPlayerCount = viewModel::selectPlayerCount,
                            onPlayerInputChange = viewModel::updatePlayerInput,
                            onStartGame = {
                                viewModel.startGame(
                                    emptyErrorResId = R.string.error_empty_letter,
                                    sameLetterErrorResId = R.string.error_same_letter
                                )
                            },
                            onOpenHtmlViewer = { viewModel.toggleHtmlDialog(true) }
                        )
                    }

                    ScreenDestination.GAME -> {
                        GameScreen(
                            themeMode = uiState.themeMode,
                            soundEnabled = uiState.soundEnabled,
                            boardState = uiState.boardState,
                            sessionStats = uiState.sessionStats,
                            hoveredEdge = uiState.hoveredEdge,
                            boardZoom = uiState.boardZoom,
                            onToggleTheme = viewModel::toggleThemeMode,
                            onToggleSound = viewModel::toggleSound,
                            onEdgeHover = viewModel::onEdgeHovered,
                            onEdgeClick = viewModel::onEdgeClicked,
                            onAdjustZoom = viewModel::adjustZoom,
                            onSetZoom = viewModel::setZoom,
                            onRestartMatch = viewModel::restartMatch,
                            onReturnToSetup = viewModel::returnToSetup,
                            onOpenHtmlViewer = { viewModel.toggleHtmlDialog(true) }
                        )
                    }
                }
            }

            if (uiState.showHtmlDialog) {
                SingleFileHtmlDialog(
                    onDismiss = { viewModel.toggleHtmlDialog(false) }
                )
            }
        }
    }
}
