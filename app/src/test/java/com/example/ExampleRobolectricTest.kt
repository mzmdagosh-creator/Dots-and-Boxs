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
            .putInt(GameViewModel.KEY_RANK_POINTS, 225)
            .putString(GameViewModel.KEY_SELECTED_THEME, ThemeColor.PLASMA_PURPLE.name)
            .commit()

        val viewModel = GameViewModel()
        viewModel.attachSharedPreferences(prefs)

        assertEquals(4, viewModel.uiState.value.unlockedCampaignLevel)
        assertEquals(27, viewModel.uiState.value.totalBoxesCaptured)
        assertEquals(CampaignLevel.LEVEL_4, viewModel.uiState.value.selectedCampaignLevel)
        assertEquals(225, viewModel.uiState.value.rankPoints)
        assertEquals(RankedDivision.GOLD, viewModel.calculateRank())
        assertEquals(ThemeColor.PLASMA_PURPLE, viewModel.uiState.value.selectedTheme)

        // Toggle Haptics
        assertTrue(viewModel.uiState.value.hapticsEnabled)
        viewModel.toggleHaptics()
        assertFalse(viewModel.uiState.value.hapticsEnabled)

        // Volume, Performance Mode, & Neon Theme Settings
        viewModel.openSettingsScreen()
        assertEquals(ScreenDestination.SETTINGS, viewModel.uiState.value.currentScreen)
        viewModel.setSoundVolume(0.45f)
        assertEquals(0.45f, viewModel.uiState.value.soundVolume, 0.001f)
        assertEquals(0.45f, SoundEngine.masterVolume, 0.001f)
        assertFalse(viewModel.uiState.value.performanceMode)
        viewModel.setPerformanceMode(true)
        assertTrue(viewModel.uiState.value.performanceMode)
        viewModel.setSelectedTheme(ThemeColor.SOLAR_FLARE)
        assertEquals(ThemeColor.SOLAR_FLARE, viewModel.uiState.value.selectedTheme)
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

        // Clear Data resets Total Boxes Captured to 0, Rank Points to 0, and Theme to CYBER_CYAN
        viewModel.clearPersistentData()
        assertEquals(1, viewModel.uiState.value.unlockedCampaignLevel)
        assertEquals(0, viewModel.uiState.value.totalBoxesCaptured)
        assertEquals(0, viewModel.uiState.value.rankPoints)
        assertEquals(RankedDivision.BRONZE, viewModel.calculateRank())
        assertEquals(ThemeColor.CYBER_CYAN, viewModel.uiState.value.selectedTheme)
        assertEquals(1, prefs.getInt(GameViewModel.KEY_UNLOCKED_LEVEL, -1))
        assertEquals(0, prefs.getInt(GameViewModel.KEY_TOTAL_BOXES_CAPTURED, -1))
        assertEquals(0, prefs.getInt(GameViewModel.KEY_RANK_POINTS, -1))
        assertEquals(ThemeColor.CYBER_CYAN.name, prefs.getString(GameViewModel.KEY_SELECTED_THEME, null))
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

    @Test
    fun `grid mutations generate blocked edges and golden nodes and award +2 points`() {
        val mutatedBoard = BoardState.create(boxRows = 4, boxCols = 4, mutationsEnabled = true)
        assertTrue(mutatedBoard.mutationsEnabled)
        assertTrue(mutatedBoard.blockedEdges.isNotEmpty())
        assertTrue(mutatedBoard.goldenNodes.size in 1..3)

        val blockedEdge = mutatedBoard.blockedEdges.first()
        assertFalse(DotsAndBoxesEngine.isEdgeAvailable(mutatedBoard, blockedEdge))
        assertEquals(null, DotsAndBoxesEngine.makeMove(mutatedBoard, blockedEdge))

        // Verify Golden Node awards +2 points and game still ends when all boxes are claimed
        val customBoard = BoardState.create(boxRows = 2, boxCols = 2, mutationsEnabled = false).copy(
            goldenNodes = setOf(BoxCoordinate(0, 0))
        )
        var state = customBoard
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0))!!.newState
        // Player 2 completes the golden node (0,0)
        val outcome = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1))!!
        assertEquals(2, outcome.newState.scoreForPlayer(2))
        assertEquals(1, outcome.newState.totalClaimedBoxes)
        assertFalse(outcome.newState.isGameOver)

        // Verify Ranked Match forces mutationsEnabled = false and MutationMode.STANDARD
        val viewModel = GameViewModel()
        viewModel.selectMutationMode(MutationMode.CHAOS)
        assertTrue(viewModel.uiState.value.mutationsEnabled)
        assertEquals(MutationMode.CHAOS, viewModel.uiState.value.selectedMutationMode)
        viewModel.startRankedMatch()
        assertFalse(viewModel.uiState.value.boardState.mutationsEnabled)
        assertEquals(MutationMode.STANDARD, viewModel.uiState.value.boardState.mutationMode)
        assertTrue(viewModel.uiState.value.boardState.blockedEdges.isEmpty())
        assertTrue(viewModel.uiState.value.boardState.goldenNodes.isEmpty())
        assertTrue(viewModel.uiState.value.boardState.toxicNodes.isEmpty())
    }

    @Test
    fun `mutation modes and toxic traps subtract 1 point and break extra turn combo`() {
        val mazeBoard = BoardState.create(boxRows = 4, boxCols = 4, mutationMode = MutationMode.MAZE_ONLY)
        assertTrue(mazeBoard.blockedEdges.isNotEmpty())
        assertTrue(mazeBoard.goldenNodes.isEmpty())
        assertTrue(mazeBoard.toxicNodes.isEmpty())

        val goldBoard = BoardState.create(boxRows = 4, boxCols = 4, mutationMode = MutationMode.GOLD_ONLY)
        assertTrue(goldBoard.blockedEdges.isEmpty())
        assertTrue(goldBoard.goldenNodes.isNotEmpty())
        assertTrue(goldBoard.toxicNodes.isEmpty())

        val toxicBoard = BoardState.create(boxRows = 4, boxCols = 4, mutationMode = MutationMode.TOXIC_ONLY)
        assertTrue(toxicBoard.blockedEdges.isEmpty())
        assertTrue(toxicBoard.goldenNodes.isEmpty())
        assertTrue(toxicBoard.toxicNodes.size in 1..3)

        val chaosBoard = BoardState.create(boxRows = 4, boxCols = 4, mutationMode = MutationMode.CHAOS)
        assertTrue(chaosBoard.blockedEdges.isNotEmpty())
        assertTrue(chaosBoard.goldenNodes.isNotEmpty())
        assertTrue(chaosBoard.toxicNodes.isNotEmpty())
        assertTrue(chaosBoard.goldenNodes.intersect(chaosBoard.toxicNodes).isEmpty())

        // Verify Toxic Node subtracts 1 point (-1) and forces grantedExtraTurn = false
        var state = BoardState.create(boxRows = 2, boxCols = 2, mutationMode = MutationMode.STANDARD).copy(
            mutationMode = MutationMode.TOXIC_ONLY,
            toxicNodes = setOf(BoxCoordinate(0, 0))
        )
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 0, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.HORIZONTAL, 1, 0))!!.newState
        state = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 0))!!.newState
        // Now it is Player 2's turn; Player 2 completes the toxic node (0,0)
        val toxicOutcome = DotsAndBoxesEngine.makeMove(state, EdgeCoordinate(EdgeOrientation.VERTICAL, 0, 1))!!
        assertEquals(-1, toxicOutcome.newState.scoreForPlayer(2))
        assertFalse(toxicOutcome.grantedExtraTurn)
        // Turn advances to Player 1 because toxic trap broke the extra-turn combo
        assertEquals(1, toxicOutcome.newState.currentPlayer)
    }

    @Test
    fun `trophy room navigation and expanded achievements unlock properly`() {
        assertEquals(18, AchievementProtocol.entries.size)
        val viewModel = GameViewModel()
        viewModel.openAchievementsScreen()
        assertEquals(ScreenDestination.ACHIEVEMENTS, viewModel.uiState.value.currentScreen)
        viewModel.closeAchievementsScreen()
        assertEquals(ScreenDestination.SETUP, viewModel.uiState.value.currentScreen)
    }
}

