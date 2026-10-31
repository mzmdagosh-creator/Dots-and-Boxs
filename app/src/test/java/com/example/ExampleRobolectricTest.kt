package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context and verify credits strings`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("Dots And Boxes", context.getString(R.string.app_name))
        assertEquals(
            "Design & Development: [Your Name/Handle]",
            context.getString(R.string.credits_design_dev)
        )
        assertEquals(
            "AI Co-Pilot & Logic: Gemini",
            context.getString(R.string.credits_ai_copilot)
        )
        assertEquals(
            "Framework: Kotlin & Jetpack Compose via Google AI Studio",
            context.getString(R.string.credits_framework)
        )
        assertEquals(
            "Build Pipeline: Cloud CI/CD Assembly",
            context.getString(R.string.credits_build_pipeline)
        )
    }

    @Test
    fun `sharedPreferences persists player stats and clearPersistentData resets them`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(GameViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(GameViewModel.KEY_UNLOCKED_LEVEL, 4)
            .putInt(GameViewModel.KEY_TOTAL_BOXES_CAPTURED, 27)
            .commit()

        val viewModel = GameViewModel()
        viewModel.attachSharedPreferences(prefs)

        assertEquals(4, viewModel.uiState.value.unlockedCampaignLevel)
        assertEquals(27, viewModel.uiState.value.totalBoxesCaptured)
        assertEquals(CampaignLevel.LEVEL_4, viewModel.uiState.value.selectedCampaignLevel)

        // Toggle Haptics
        assertTrue(viewModel.uiState.value.hapticsEnabled)
        viewModel.toggleHaptics()
        assertFalse(viewModel.uiState.value.hapticsEnabled)

        // Volume & Performance Mode Settings
        viewModel.openSettingsScreen()
        assertEquals(ScreenDestination.SETTINGS, viewModel.uiState.value.currentScreen)
        viewModel.setSoundVolume(0.45f)
        assertEquals(0.45f, viewModel.uiState.value.soundVolume, 0.001f)
        assertEquals(0.45f, SoundEngine.masterVolume, 0.001f)
        assertFalse(viewModel.uiState.value.performanceMode)
        viewModel.setPerformanceMode(true)
        assertTrue(viewModel.uiState.value.performanceMode)
        viewModel.closeSettingsScreen()
        assertEquals(ScreenDestination.SETUP, viewModel.uiState.value.currentScreen)

        // Replay Tutorial sets hasSeenTutorial to false and routes to SETUP
        viewModel.completeTutorial()
        assertTrue(viewModel.uiState.value.hasSeenTutorial)
        viewModel.openSettingsScreen()
        viewModel.replayTutorial()
        assertFalse(viewModel.uiState.value.hasSeenTutorial)
        assertEquals(ScreenDestination.SETUP, viewModel.uiState.value.currentScreen)

        // Navigate to Credits Screen and Back
        viewModel.openCreditsScreen()
        assertEquals(ScreenDestination.CREDITS, viewModel.uiState.value.currentScreen)
        viewModel.closeCreditsScreen()
        assertEquals(ScreenDestination.SETUP, viewModel.uiState.value.currentScreen)

        // Clear Data resets Total Boxes Captured to 0 and Tournament Level Reached to 1
        viewModel.clearPersistentData()
        assertEquals(1, viewModel.uiState.value.unlockedCampaignLevel)
        assertEquals(0, viewModel.uiState.value.totalBoxesCaptured)
        assertEquals(1, prefs.getInt(GameViewModel.KEY_UNLOCKED_LEVEL, -1))
        assertEquals(0, prefs.getInt(GameViewModel.KEY_TOTAL_BOXES_CAPTURED, -1))
    }

    @Test
    fun `sharedPreferences persists session stats and match history across app refreshes`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(GameViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val statsJson = """
            {
                "1": {"wins": 3, "losses": 1, "ties": 0},
                "2": {"wins": 1, "losses": 3, "ties": 0}
            }
        """.trimIndent()
        val historyJson = """
            [
                {
                    "matchNumber": 4,
                    "gridSizeLabel": "4×4",
                    "scoreSummary": "A: 10 - C: 6",
                    "winnerLabel": "Player 1 (A) Wins!",
                    "winnerPlayerNumber": 1
                }
            ]
        """.trimIndent()

        prefs.edit()
            .putString(GameViewModel.KEY_SESSION_STATS_JSON, statsJson)
            .putString(GameViewModel.KEY_MATCH_HISTORY_JSON, historyJson)
            .putInt(GameViewModel.KEY_TOTAL_MATCHES, 4)
            .commit()

        val refreshedViewModel = GameViewModel()
        refreshedViewModel.attachSharedPreferences(prefs)

        val state = refreshedViewModel.uiState.value
        assertEquals(3, state.sessionStats[1]?.wins)
        assertEquals(1, state.sessionStats[1]?.losses)
        assertEquals(1, state.sessionStats[2]?.wins)
        assertEquals(3, state.sessionStats[2]?.losses)
        assertEquals(4, state.totalMatchesCompleted)
        assertEquals(1, state.matchHistory.size)
        assertEquals(4, state.matchHistory[0].matchNumber)
        assertEquals("4×4", state.matchHistory[0].gridSizeLabel)
        assertEquals("A: 10 - C: 6", state.matchHistory[0].scoreSummary)
        assertEquals("Player 1 (A) Wins!", state.matchHistory[0].winnerLabel)
        assertEquals(1, state.matchHistory[0].winnerPlayerNumber)
    }
}
