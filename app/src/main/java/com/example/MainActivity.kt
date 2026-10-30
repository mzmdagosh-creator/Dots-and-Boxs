package action potential 

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

// ============================================================================
// 1. NEON GRID PROTOCOL — STRICTLY DARK MODE THEME (Color(0xFF121212))
//    No light mode, no theme pickers.
// ============================================================================

val ObsidianBackground = Color(0xFF121212)
val SlateSurface = Color(0xFF1A1E29)
val SlateSurfaceElevated = Color(0xFF222838)
val SlateBorder = Color(0xFF2E3A52)
val DotWhite = Color(0xFFFFFFFF)
val MutedText = Color(0xFF94A3B8)

// Player 1: Neon Cyan (#00E5FF) & 40% Opacity Node Fill
val Player1Cyan = Color(0xFF00E5FF)
val Player1CyanFill = Color(0x6600E5FF)

// AI CPU: Neon Magenta (#FF007F) & 40% Opacity Node Fill
val Player2Coral = Color(0xFFFF007F)
val Player2CoralFill = Color(0x66FF007F)

// Additional Accent Colors
val Player3Emerald = Color(0xFF00E676)
val Player3EmeraldFill = Color(0x6600E676)
val Player4Violet = Color(0xFFA855F7)
val Player4VioletFill = Color(0x66A855F7)
val StreakAmber = Color(0xFFFACC15)

// Unselected lines: faint grey at 20% opacity
val UnclaimedEdgeIdle = Color(0x339E9E9E)

val SpaceGroteskFontFamily = FontFamily.SansSerif
val JetBrainsMonoFontFamily = FontFamily.Monospace

@Immutable
data class GamePalette(
    val backgroundDeep: Color = ObsidianBackground,
    val backgroundRadialTop: Color = ObsidianBackground,
    val surface: Color = SlateSurface,
    val surfaceElevated: Color = SlateSurfaceElevated,
    val border: Color = SlateBorder,
    val primaryText: Color = DotWhite,
    val mutedText: Color = MutedText,
    val dotColor: Color = DotWhite,
    val idleEdge: Color = UnclaimedEdgeIdle,
    val streakAccent: Color = StreakAmber,
    val p1Accent: Color = Player1Cyan,
    val p1Fill: Color = Player1CyanFill,
    val p2Accent: Color = Player2Coral,
    val p2Fill: Color = Player2CoralFill,
    val p3Accent: Color = Player3Emerald,
    val p3Fill: Color = Player3EmeraldFill,
    val p4Accent: Color = Player4Violet,
    val p4Fill: Color = Player4VioletFill
)

val DarkProtocolPalette = GamePalette()
val LocalGamePalette = staticCompositionLocalOf { DarkProtocolPalette }

@Composable
fun playerAccentColor(playerNumber: Int): Color {
    val palette = LocalGamePalette.current
    return when (playerNumber) {
        1 -> palette.p1Accent
        2 -> palette.p2Accent
        3 -> palette.p3Accent
        4 -> palette.p4Accent
        else -> palette.p1Accent
    }
}

@Composable
fun playerFillColor(playerNumber: Int): Color {
    val palette = LocalGamePalette.current
    return when (playerNumber) {
        1 -> palette.p1Fill
        2 -> palette.p2Fill
        3 -> palette.p3Fill
        4 -> palette.p4Fill
        else -> palette.p1Fill
    }
}

private val DarkProtocolColorScheme = darkColorScheme(
    primary = Player1Cyan,
    onPrimary = ObsidianBackground,
    primaryContainer = SlateSurfaceElevated,
    onPrimaryContainer = DotWhite,
    secondary = Player2Coral,
    onSecondary = ObsidianBackground,
    secondaryContainer = SlateSurfaceElevated,
    onSecondaryContainer = DotWhite,
    tertiary = StreakAmber,
    background = ObsidianBackground,
    onBackground = DotWhite,
    surface = SlateSurface,
    onSurface = DotWhite,
    surfaceVariant = SlateSurfaceElevated,
    onSurfaceVariant = MutedText,
    outline = SlateBorder,
    error = Color(0xFFF87171)
)

private val ProtocolTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.6.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp
    )
)

@Composable
fun NeonGridProtocolTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalGamePalette provides DarkProtocolPalette) {
        MaterialTheme(
            colorScheme = DarkProtocolColorScheme,
            typography = ProtocolTypography,
            content = content
        )
    }
}

// ============================================================================
// 2. HARDWARE HAPTICS & PROCEDURAL AUDIO ENGINES
// ============================================================================

object HapticEngine {
    var isHapticsEnabled: Boolean = true

    private fun getVibrator(context: Context): Vibrator? {
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        }.getOrNull()
    }

    fun triggerLineTick(context: Context?, hapticFeedback: HapticFeedback? = null) {
        if (!isHapticsEnabled) return
        runCatching {
            hapticFeedback?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        val vibrator = context?.let { getVibrator(it) } ?: return
        runCatching {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(16L, 85))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(16L)
            }
        }
    }

    fun triggerBoxCaptureBuzz(
        context: Context?,
        hapticFeedback: HapticFeedback? = null,
        capturedCount: Int = 1
    ) {
        if (!isHapticsEnabled) return
        runCatching {
            hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        val vibrator = context?.let { getVibrator(it) } ?: return
        runCatching {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (capturedCount >= 2) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0L, 45L, 35L, 70L),
                            intArrayOf(0, 210, 0, 255),
                            -1
                        )
                    )
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(55L, 235))
                }
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(55L)
            }
        }
    }

    fun triggerVictoryCelebration(context: Context?, hapticFeedback: HapticFeedback? = null) {
        if (!isHapticsEnabled) return
        runCatching {
            hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        val vibrator = context?.let { getVibrator(it) } ?: return
        runCatching {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0L, 40L, 40L, 55L, 45L, 90L),
                        intArrayOf(0, 180, 0, 220, 0, 255),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(90L)
            }
        }
    }
}

object SoundEngine {
    private const val SAMPLE_RATE = 22050
    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    var isSoundEnabled: Boolean = true

    fun playLineClick() {
        if (!isSoundEnabled) return
        audioScope.launch {
            runCatching {
                val samples = synthesizeFrequencySweep(
                    startFreqHz = 540.0,
                    endFreqHz = 170.0,
                    durationMs = 38,
                    amplitude = 0.28,
                    decayRate = 45.0
                )
                playPcmBuffer(samples)
            }
        }
    }

    fun playBoxPop(comboCount: Int = 1) {
        if (!isSoundEnabled) return
        audioScope.launch {
            runCatching {
                val pitchMultiplier = (1.0 + (comboCount.coerceAtLeast(1) - 1) * 0.12).coerceAtMost(1.85)
                val samples = synthesizeFrequencySweep(
                    startFreqHz = 310.0 * pitchMultiplier,
                    endFreqHz = 660.0 * pitchMultiplier,
                    durationMs = 125,
                    amplitude = 0.42,
                    decayRate = 18.0
                )
                playPcmBuffer(samples)
            }
        }
    }

    fun playGameEndFanfare() {
        if (!isSoundEnabled) return
        audioScope.launch {
            runCatching {
                val notes = listOf(
                    523.25 to 110,
                    659.25 to 110,
                    783.99 to 110,
                    1046.50 to 290
                )
                val totalSamples = notes.sumOf { (_, ms) -> (SAMPLE_RATE * ms) / 1000 }
                val combined = ShortArray(totalSamples)
                var offset = 0

                for ((freq, durationMs) in notes) {
                    val noteSamples = synthesizeFrequencySweep(
                        startFreqHz = freq,
                        endFreqHz = freq * 1.01,
                        durationMs = durationMs,
                        amplitude = 0.38,
                        decayRate = 8.5
                    )
                    val copyLen = noteSamples.size.coerceAtMost(combined.size - offset)
                    System.arraycopy(noteSamples, 0, combined, offset, copyLen)
                    offset += copyLen
                }
                playPcmBuffer(combined)
            }
        }
    }

    private fun synthesizeFrequencySweep(
        startFreqHz: Double,
        endFreqHz: Double,
        durationMs: Int,
        amplitude: Double,
        decayRate: Double
    ): ShortArray {
        val numSamples = ((SAMPLE_RATE * durationMs) / 1000).coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val instantFreq = startFreqHz + (endFreqHz - startFreqHz) * progress
            phase += (2.0 * PI * instantFreq) / SAMPLE_RATE

            val attack = (i / (SAMPLE_RATE * 0.004)).coerceAtMost(1.0)
            val envelope = attack * exp(-decayRate * progress)
            val sampleValue = (sin(phase) * amplitude * envelope * Short.MAX_VALUE).toInt()
            buffer[i] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun playPcmBuffer(samples: ShortArray) {
        if (samples.isEmpty()) return
        val byteSize = samples.size * 2
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(byteSize)
            .build()

        track.write(samples, 0, samples.size)
        track.play()
        val sleepMs = ((samples.size * 1000L) / SAMPLE_RATE) + 45L
        Thread.sleep(sleepMs)
        runCatching {
            track.stop()
            track.release()
        }
    }
}

// ============================================================================
// 3. GRID ARENA ENGINE & AI CPU (EASY RANDOM, MEDIUM GREEDY, HARD MINIMAX)
// ============================================================================

enum class EdgeOrientation {
    HORIZONTAL,
    VERTICAL
}

enum class GameMode {
    PVP,
    CPU
}

enum class CpuDifficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

enum class GridSize(
    val boxRows: Int,
    val boxCols: Int,
    val label: String
) {
    SIZE_3X3(3, 3, "3×3"),
    SIZE_4X4(4, 4, "4×4"),
    SIZE_5X5(5, 5, "5×5"),
    SIZE_6X6(6, 6, "6×6");

    val dotRows: Int get() = boxRows + 1
    val dotCols: Int get() = boxCols + 1
    val totalBoxes: Int get() = boxRows * boxCols

    companion object {
        fun fromDimensions(rows: Int, cols: Int): GridSize =
            entries.firstOrNull { it.boxRows == rows && it.boxCols == cols } ?: SIZE_4X4
    }
}

/**
 * Time Limits for Quick Match Config:
 * - RELAXED: Relaxed (Untimed)
 * - BULLET: 3s
 * - BLITZ: 5s
 * - RAPID: 10s
 */
enum class TimerMode(val durationSeconds: Int, val label: String) {
    RELAXED(0, "Relaxed"),
    BULLET(3, "3s"),
    BLITZ(5, "5s"),
    RAPID(10, "10s");

    val isTimed: Boolean get() = durationSeconds > 0
}

/**
 * Campaign Mode sequential progression from Level 1 (3x3, Easy AI) to Level 6 (6x6, Hard AI),
 * saved via SharedPreferences.
 */
enum class CampaignLevel(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val gridSize: GridSize,
    val difficulty: CpuDifficulty
) {
    LEVEL_1(1, "Level 1", "3×3 Grid • Easy AI", GridSize.SIZE_3X3, CpuDifficulty.EASY),
    LEVEL_2(2, "Level 2", "4×4 Grid • Easy AI", GridSize.SIZE_4X4, CpuDifficulty.EASY),
    LEVEL_3(3, "Level 3", "4×4 Grid • Medium AI", GridSize.SIZE_4X4, CpuDifficulty.MEDIUM),
    LEVEL_4(4, "Level 4", "5×5 Grid • Medium AI", GridSize.SIZE_5X5, CpuDifficulty.MEDIUM),
    LEVEL_5(5, "Level 5", "5×5 Grid • Hard AI", GridSize.SIZE_5X5, CpuDifficulty.HARD),
    LEVEL_6(6, "Level 6", "6×6 Grid • Hard AI", GridSize.SIZE_6X6, CpuDifficulty.HARD);

    fun nextLevel(): CampaignLevel? =
        entries.firstOrNull { it.levelNumber == levelNumber + 1 }

    companion object {
        fun fromNumber(levelNumber: Int): CampaignLevel =
            entries.firstOrNull { it.levelNumber == levelNumber } ?: LEVEL_1
    }
}

data class EdgeCoordinate(
    val orientation: EdgeOrientation,
    val row: Int,
    val col: Int
)

data class BoxCoordinate(
    val row: Int,
    val col: Int
)

data class PlayerConfig(
    val playerNumber: Int,
    val letter: String,
    val isAi: Boolean = false
)

data class MoveOutcome(
    val newState: BoardState,
    val capturedBoxes: List<BoxCoordinate>,
    val grantedExtraTurn: Boolean
)

data class BoardState(
    val boxRows: Int = DEFAULT_BOX_ROWS,
    val boxCols: Int = DEFAULT_BOX_COLS,
    val gameMode: GameMode = GameMode.CPU,
    val cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    val humanPlayerCount: Int = 1,
    val players: List<PlayerConfig> = listOf(
        PlayerConfig(1, "P1", isAi = false),
        PlayerConfig(2, "CPU", isAi = true)
    ),
    val currentPlayer: Int = 1,
    val scores: Map<Int, Int> = mapOf(1 to 0, 2 to 0),
    val horizontalEdges: List<List<Int>> = List(boxRows + 1) { List(boxCols) { 0 } },
    val verticalEdges: List<List<Int>> = List(boxRows) { List(boxCols + 1) { 0 } },
    val boxes: List<List<Int>> = List(boxRows) { List(boxCols) { 0 } },
    val lastClaimedEdge: EdgeCoordinate? = null,
    val lastCapturedBoxes: Set<BoxCoordinate> = emptySet(),
    val comboChainBoxes: Set<BoxCoordinate> = emptySet(),
    val currentComboCount: Int = 0,
    val comboTriggerId: Int = 0,
    val hasExtraTurnStreak: Boolean = false,
    val totalMovesPlayed: Int = 0
) {
    val dotRows: Int get() = boxRows + 1
    val dotCols: Int get() = boxCols + 1
    val totalBoxes: Int get() = boxRows * boxCols
    val gridSize: GridSize get() = GridSize.fromDimensions(boxRows, boxCols)
    val activePlayerCount: Int get() = players.size
    val player1Letter: String get() = letterForPlayer(1)
    val player2Letter: String get() = letterForPlayer(2)
    val player1Score: Int get() = scoreForPlayer(1)
    val player2Score: Int get() = scoreForPlayer(2)
    val totalClaimedBoxes: Int get() = scores.values.sum()
    val isGameOver: Boolean get() = totalClaimedBoxes == totalBoxes
    val isMultiSquareCombo: Boolean get() = currentComboCount >= 2
    val isCurrentPlayerAi: Boolean
        get() = !isGameOver && players.firstOrNull { it.playerNumber == currentPlayer }?.isAi == true

    val winner: Int?
        get() {
            if (!isGameOver) return null
            val maxScore = scores.values.maxOrNull() ?: 0
            val topPlayers = scores.filterValues { it == maxScore }.keys
            return if (topPlayers.size == 1) topPlayers.first() else 0
        }

    val maxScore: Int get() = scores.values.maxOrNull() ?: 0

    fun letterForPlayer(player: Int): String =
        players.firstOrNull { it.playerNumber == player }?.letter ?: player.toString()

    fun scoreForPlayer(player: Int): Int = scores[player] ?: 0

    fun isPlayerAi(player: Int): Boolean =
        players.firstOrNull { it.playerNumber == player }?.isAi == true

    companion object {
        const val DEFAULT_BOX_ROWS = 4
        const val DEFAULT_BOX_COLS = 4

        fun create(
            humanPlayerCount: Int = 1,
            letters: List<String> = listOf("A", "C"),
            cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
            boxRows: Int = DEFAULT_BOX_ROWS,
            boxCols: Int = DEFAULT_BOX_COLS
        ): BoardState {
            val safeRows = boxRows.coerceIn(2, 10)
            val safeCols = boxCols.coerceIn(2, 10)
            val coercedCount = humanPlayerCount.coerceIn(1, 4)
            val mode = if (coercedCount == 1) GameMode.CPU else GameMode.PVP
            val playerConfigs = if (coercedCount == 1) {
                val p1Letter = letters.getOrNull(0)?.ifBlank { "A" } ?: "A"
                val cpuLetter = if (p1Letter.equals("C", ignoreCase = true)) "🤖" else "C"
                listOf(
                    PlayerConfig(1, p1Letter, isAi = false),
                    PlayerConfig(2, cpuLetter, isAi = true)
                )
            } else {
                (1..coercedCount).map { idx ->
                    val fallback = ('A' + (idx - 1)).toString()
                    PlayerConfig(
                        playerNumber = idx,
                        letter = letters.getOrNull(idx - 1)?.ifBlank { fallback } ?: fallback,
                        isAi = false
                    )
                }
            }

            return BoardState(
                boxRows = safeRows,
                boxCols = safeCols,
                gameMode = mode,
                cpuDifficulty = cpuDifficulty,
                humanPlayerCount = coercedCount,
                players = playerConfigs,
                currentPlayer = 1,
                scores = playerConfigs.associate { it.playerNumber to 0 },
                horizontalEdges = List(safeRows + 1) { List(safeCols) { 0 } },
                verticalEdges = List(safeRows) { List(safeCols + 1) { 0 } },
                boxes = List(safeRows) { List(safeCols) { 0 } }
            )
        }
    }
}

object DotsAndBoxesEngine {

    fun minimaxSearchDepth(boxRows: Int, boxCols: Int): Int =
        if (boxRows <= 4 && boxCols <= 4) 3 else 2

    fun isEdgeAvailable(state: BoardState, edge: EdgeCoordinate): Boolean {
        return when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                edge.row in 0 until state.dotRows &&
                    edge.col in 0 until state.boxCols &&
                    state.horizontalEdges[edge.row][edge.col] == 0
            }
            EdgeOrientation.VERTICAL -> {
                edge.row in 0 until state.boxRows &&
                    edge.col in 0 until state.dotCols &&
                    state.verticalEdges[edge.row][edge.col] == 0
            }
        }
    }

    fun forfeitTurn(state: BoardState): BoardState {
        if (state.isGameOver) return state
        val nextPlayer = (state.currentPlayer % state.activePlayerCount) + 1
        return state.copy(
            currentPlayer = nextPlayer,
            currentComboCount = 0,
            comboChainBoxes = emptySet(),
            lastCapturedBoxes = emptySet(),
            hasExtraTurnStreak = false
        )
    }

    /**
     * Draws a line between two dots. If the 4th line forming a closed square is drawn,
     * the player captures the node, scores a point, and receives a mandatory extra turn.
     */
    fun makeMove(state: BoardState, edge: EdgeCoordinate): MoveOutcome? {
        if (state.isGameOver) return null

        val hMutable = state.horizontalEdges.map { it.toMutableList() }.toMutableList()
        val vMutable = state.verticalEdges.map { it.toMutableList() }.toMutableList()
        val boxesMutable = state.boxes.map { it.toMutableList() }.toMutableList()

        val candidateBoxes = mutableListOf<BoxCoordinate>()

        when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                if (edge.row !in 0 until state.dotRows || edge.col !in 0 until state.boxCols) {
                    return null
                }
                if (hMutable[edge.row][edge.col] != 0) return null

                hMutable[edge.row][edge.col] = state.currentPlayer

                if (edge.row > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row - 1, edge.col))
                }
                if (edge.row < state.boxRows) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
                }
            }

            EdgeOrientation.VERTICAL -> {
                if (edge.row !in 0 until state.boxRows || edge.col !in 0 until state.dotCols) {
                    return null
                }
                if (vMutable[edge.row][edge.col] != 0) return null

                vMutable[edge.row][edge.col] = state.currentPlayer

                if (edge.col > 0) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col - 1))
                }
                if (edge.col < state.boxCols) {
                    candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
                }
            }
        }

        val newlyCaptured = mutableListOf<BoxCoordinate>()
        for (box in candidateBoxes) {
            if (boxesMutable[box.row][box.col] == 0 &&
                isBoxCompleted(box.row, box.col, hMutable, vMutable)
            ) {
                boxesMutable[box.row][box.col] = state.currentPlayer
                newlyCaptured.add(box)
            }
        }

        val capturedCount = newlyCaptured.size
        val updatedScores = state.scores.toMutableMap()
        updatedScores[state.currentPlayer] =
            (updatedScores[state.currentPlayer] ?: 0) + capturedCount

        val grantedExtraTurn = capturedCount > 0
        val nextPlayer = if (grantedExtraTurn) {
            state.currentPlayer
        } else {
            (state.currentPlayer % state.activePlayerCount) + 1
        }

        val newComboCount = if (grantedExtraTurn) state.currentComboCount + capturedCount else 0
        val newComboChain = if (grantedExtraTurn) {
            state.comboChainBoxes + newlyCaptured
        } else {
            emptySet()
        }
        val newComboTriggerId = if (grantedExtraTurn) {
            state.comboTriggerId + 1
        } else {
            state.comboTriggerId
        }

        val updatedState = state.copy(
            currentPlayer = nextPlayer,
            scores = updatedScores,
            horizontalEdges = hMutable.map { it.toList() },
            verticalEdges = vMutable.map { it.toList() },
            boxes = boxesMutable.map { it.toList() },
            lastClaimedEdge = edge,
            lastCapturedBoxes = newlyCaptured.toSet(),
            comboChainBoxes = newComboChain,
            currentComboCount = newComboCount,
            comboTriggerId = newComboTriggerId,
            hasExtraTurnStreak = grantedExtraTurn,
            totalMovesPlayed = state.totalMovesPlayed + 1
        )

        return MoveOutcome(
            newState = updatedState,
            capturedBoxes = newlyCaptured,
            grantedExtraTurn = grantedExtraTurn
        )
    }

    /**
     * AI CPU Opponent:
     * - EASY: Random valid unclaimed line
     * - MEDIUM: Greedy/Safe (completes 3-sided nodes; avoids drawing the 3rd side)
     * - HARD: Minimax with Alpha-Beta pruning
     */
    fun cpuTakeTurn(
        state: BoardState,
        difficulty: CpuDifficulty = state.cpuDifficulty
    ): EdgeCoordinate? {
        if (state.isGameOver) return null

        val availableEdges = getAllAvailableEdges(
            boxRows = state.boxRows,
            boxCols = state.boxCols,
            hEdges = state.horizontalEdges,
            vEdges = state.verticalEdges
        )
        if (availableEdges.isEmpty()) return null

        return when (difficulty) {
            CpuDifficulty.EASY -> chooseEasyCpuMove(availableEdges)
            CpuDifficulty.MEDIUM -> chooseMediumCpuMove(state, availableEdges)
            CpuDifficulty.HARD -> chooseHardMinimaxMove(state, availableEdges)
        }
    }

    private fun chooseEasyCpuMove(availableEdges: List<EdgeCoordinate>): EdgeCoordinate =
        availableEdges.random()

    private fun chooseMediumCpuMove(
        state: BoardState,
        availableEdges: List<EdgeCoordinate>
    ): EdgeCoordinate {
        val doubleCaptures = mutableListOf<EdgeCoordinate>()
        val singleCaptures = mutableListOf<EdgeCoordinate>()
        val safeMoves = mutableListOf<EdgeCoordinate>()
        val riskyMoves = mutableListOf<EdgeCoordinate>()

        for (edge in availableEdges) {
            val adj = getAdjacentBoxes(edge, state.boxRows, state.boxCols)
            var completable = 0
            var thirdSidesCreated = 0

            for (box in adj) {
                val sides = countClaimedSides(
                    box.row,
                    box.col,
                    state.horizontalEdges,
                    state.verticalEdges
                )
                if (sides == 3) completable++
                else if (sides == 2) thirdSidesCreated++
            }

            when {
                completable >= 2 -> doubleCaptures.add(edge)
                completable == 1 -> singleCaptures.add(edge)
                thirdSidesCreated == 0 -> safeMoves.add(edge)
                else -> riskyMoves.add(edge)
            }
        }

        if (doubleCaptures.isNotEmpty()) return doubleCaptures.random()
        if (singleCaptures.isNotEmpty()) return singleCaptures.random()
        if (safeMoves.isNotEmpty()) return safeMoves.random()
        return riskyMoves.random()
    }

    private fun chooseHardMinimaxMove(
        state: BoardState,
        availableEdges: List<EdgeCoordinate>
    ): EdgeCoordinate {
        val cpuPlayer = state.currentPlayer
        val humanPlayer = if (cpuPlayer == 2) 1 else 2
        val searchDepth = minimaxSearchDepth(state.boxRows, state.boxCols)

        val orderedMoves = orderMovesForPruning(state, availableEdges).shuffled()
            .sortedByDescending { moveOrderingPriority(state, it) }

        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE
        val bestCandidates = mutableListOf<EdgeCoordinate>()

        for (edge in orderedMoves) {
            val outcome = makeMove(state, edge) ?: continue
            val nextIsMaximizing = outcome.newState.currentPlayer == cpuPlayer
            val eval = minimaxAlphaBeta(
                state = outcome.newState,
                depth = searchDepth - 1,
                alpha = alpha,
                beta = beta,
                isMaximizing = nextIsMaximizing,
                cpuPlayer = cpuPlayer,
                humanPlayer = humanPlayer
            )

            if (eval > bestScore) {
                bestScore = eval
                bestCandidates.clear()
                bestCandidates.add(edge)
            } else if (eval == bestScore) {
                bestCandidates.add(edge)
            }
            alpha = max(alpha, bestScore)
        }

        return if (bestCandidates.isNotEmpty()) bestCandidates.random() else availableEdges.random()
    }

    private fun minimaxAlphaBeta(
        state: BoardState,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMaximizing: Boolean,
        cpuPlayer: Int,
        humanPlayer: Int
    ): Int {
        if (depth <= 0 || state.isGameOver) {
            return evaluateBoardHeuristic(state, cpuPlayer, humanPlayer)
        }

        val available = getAllAvailableEdges(
            boxRows = state.boxRows,
            boxCols = state.boxCols,
            hEdges = state.horizontalEdges,
            vEdges = state.verticalEdges
        )
        if (available.isEmpty()) {
            return evaluateBoardHeuristic(state, cpuPlayer, humanPlayer)
        }

        val ordered = orderMovesForPruning(state, available)
        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (edge in ordered) {
                val outcome = makeMove(state, edge) ?: continue
                val nextIsMax = outcome.newState.currentPlayer == cpuPlayer
                val eval = minimaxAlphaBeta(
                    state = outcome.newState,
                    depth = depth - 1,
                    alpha = currentAlpha,
                    beta = currentBeta,
                    isMaximizing = nextIsMax,
                    cpuPlayer = cpuPlayer,
                    humanPlayer = humanPlayer
                )
                maxEval = max(maxEval, eval)
                currentAlpha = max(currentAlpha, eval)
                if (currentBeta <= currentAlpha) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (edge in ordered) {
                val outcome = makeMove(state, edge) ?: continue
                val nextIsMax = outcome.newState.currentPlayer == cpuPlayer
                val eval = minimaxAlphaBeta(
                    state = outcome.newState,
                    depth = depth - 1,
                    alpha = currentAlpha,
                    beta = currentBeta,
                    isMaximizing = nextIsMax,
                    cpuPlayer = cpuPlayer,
                    humanPlayer = humanPlayer
                )
                minEval = min(minEval, eval)
                currentBeta = min(currentBeta, eval)
                if (currentBeta <= currentAlpha) break
            }
            return minEval
        }
    }

    private fun evaluateBoardHeuristic(
        state: BoardState,
        cpuPlayer: Int,
        humanPlayer: Int
    ): Int {
        val cpuScore = state.scoreForPlayer(cpuPlayer)
        val humanScore = state.scoreForPlayer(humanPlayer)
        var scoreDiff = (cpuScore - humanScore) * 100

        if (!state.isGameOver) {
            var threeSidedCount = 0
            for (r in 0 until state.boxRows) {
                for (c in 0 until state.boxCols) {
                    if (state.boxes[r][c] == 0 &&
                        countClaimedSides(r, c, state.horizontalEdges, state.verticalEdges) == 3
                    ) {
                        threeSidedCount++
                    }
                }
            }
            if (threeSidedCount > 0) {
                if (state.currentPlayer == cpuPlayer) {
                    scoreDiff += threeSidedCount * 35
                } else {
                    scoreDiff -= threeSidedCount * 35
                }
            }
        }
        return scoreDiff
    }

    private fun orderMovesForPruning(
        state: BoardState,
        moves: List<EdgeCoordinate>
    ): List<EdgeCoordinate> =
        moves.sortedByDescending { moveOrderingPriority(state, it) }

    private fun moveOrderingPriority(state: BoardState, edge: EdgeCoordinate): Int {
        val adj = getAdjacentBoxes(edge, state.boxRows, state.boxCols)
        var completable = 0
        var createsThird = 0
        for (box in adj) {
            val sides = countClaimedSides(
                box.row,
                box.col,
                state.horizontalEdges,
                state.verticalEdges
            )
            if (sides == 3) completable++
            else if (sides == 2) createsThird++
        }
        return when {
            completable >= 2 -> 30
            completable == 1 -> 20
            createsThird == 0 -> 10
            createsThird == 1 -> -10
            else -> -20
        }
    }

    private fun getAllAvailableEdges(
        boxRows: Int,
        boxCols: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): List<EdgeCoordinate> {
        val list = mutableListOf<EdgeCoordinate>()
        for (r in 0..boxRows) {
            for (c in 0 until boxCols) {
                if (hEdges[r][c] == 0) list.add(EdgeCoordinate(EdgeOrientation.HORIZONTAL, r, c))
            }
        }
        for (r in 0 until boxRows) {
            for (c in 0..boxCols) {
                if (vEdges[r][c] == 0) list.add(EdgeCoordinate(EdgeOrientation.VERTICAL, r, c))
            }
        }
        return list
    }

    private fun getAdjacentBoxes(
        edge: EdgeCoordinate,
        boxRows: Int,
        boxCols: Int
    ): List<BoxCoordinate> {
        val result = mutableListOf<BoxCoordinate>()
        when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                if (edge.row > 0) result.add(BoxCoordinate(edge.row - 1, edge.col))
                if (edge.row < boxRows) result.add(BoxCoordinate(edge.row, edge.col))
            }
            EdgeOrientation.VERTICAL -> {
                if (edge.col > 0) result.add(BoxCoordinate(edge.row, edge.col - 1))
                if (edge.col < boxCols) result.add(BoxCoordinate(edge.row, edge.col))
            }
        }
        return result
    }

    private fun countClaimedSides(
        boxRow: Int,
        boxCol: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): Int {
        var count = 0
        if (hEdges[boxRow][boxCol] != 0) count++
        if (hEdges[boxRow + 1][boxCol] != 0) count++
        if (vEdges[boxRow][boxCol] != 0) count++
        if (vEdges[boxRow][boxCol + 1] != 0) count++
        return count
    }

    private fun isBoxCompleted(
        boxRow: Int,
        boxCol: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>
    ): Boolean = countClaimedSides(boxRow, boxCol, hEdges, vEdges) == 4
}

// ============================================================================
// 4. VIEWMODEL & SHAREDPREFERENCES STATE MANAGEMENT
// ============================================================================

enum class ScreenDestination {
    SETUP,          // Screen 1: The Command Center (Main Menu)
    QUICK_CONFIG,   // Screen 3: Quick Match Config
    GAME,           // Screen 2: The Grid Arena (Gameplay)
    CREDITS
}

enum class MainMenuSection {
    CAMPAIGN,
    QUICK_MATCH
}

enum class HapticEventType {
    LINE_TICK,
    BOX_CAPTURE_BUZZ,
    VICTORY_CELEBRATION
}

data class HapticEvent(
    val id: Long,
    val type: HapticEventType,
    val capturedCount: Int = 0
)

data class PlayerSessionStats(
    val wins: Int = 0,
    val losses: Int = 0,
    val ties: Int = 0
)

data class MatchHistoryItem(
    val matchNumber: Int,
    val gridSizeLabel: String,
    val scoreSummary: String,
    val winnerLabel: String,
    val winnerPlayerNumber: Int
)

data class GameUiState(
    val currentScreen: ScreenDestination = ScreenDestination.SETUP,
    val mainMenuSection: MainMenuSection = MainMenuSection.CAMPAIGN,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val totalBoxesCaptured: Int = 0,
    val unlockedCampaignLevel: Int = 1,
    val selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    val activeCampaignLevel: CampaignLevel? = null,
    val justUnlockedNextCampaignLevel: Boolean = false,
    val selectedGridSize: GridSize = GridSize.SIZE_4X4,
    val selectedGameMode: GameMode = GameMode.CPU,
    val selectedCpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    val selectedTimerMode: TimerMode = TimerMode.RELAXED,
    val selectedPlayerCount: Int = 1,
    val playerInputs: List<String> = listOf("P1", "CPU", "P3", "P4"),
    val setupErrorResId: Int? = null,
    val boardState: BoardState = BoardState.create(
        humanPlayerCount = 1,
        letters = listOf("P1", "CPU"),
        cpuDifficulty = CpuDifficulty.MEDIUM,
        boxRows = GridSize.SIZE_4X4.boxRows,
        boxCols = GridSize.SIZE_4X4.boxCols
    ),
    val blitzRemainingSeconds: Float = 5.0f,
    val blitzRemainingFraction: Float = 1.0f,
    val lastTurnForfeitedByTimeout: Boolean = false,
    val blitzForfeitCount: Int = 0,
    val sessionStats: Map<Int, PlayerSessionStats> = mapOf(
        1 to PlayerSessionStats(),
        2 to PlayerSessionStats(),
        3 to PlayerSessionStats(),
        4 to PlayerSessionStats()
    ),
    val matchHistory: List<MatchHistoryItem> = emptyList(),
    val totalMatchesCompleted: Int = 0,
    val hoveredEdge: EdgeCoordinate? = null,
    val boardZoom: Float = 0.95f,
    val showHtmlDialog: Boolean = false,
    val lastHapticEvent: HapticEvent? = null,
    val victoryAnimationTriggerId: Int = 0
)

class GameViewModel : ViewModel() {

    companion object {
        const val PREFS_NAME = "dots_and_boxes_campaign_prefs"
        const val KEY_UNLOCKED_LEVEL = "unlocked_campaign_level"
        const val KEY_TOTAL_BOXES_CAPTURED = "total_boxes_captured"
        const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        const val KEY_SESSION_STATS_JSON = "dots_and_boxes_session_stats"
        const val KEY_MATCH_HISTORY_JSON = "dots_and_boxes_match_history"
        const val KEY_TOTAL_MATCHES = "dots_and_boxes_total_matches"
    }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var sharedPreferences: SharedPreferences? = null
    private var aiTurnJob: Job? = null
    private var blitzTimerJob: Job? = null
    private var hapticEventCounter: Long = 0L

    fun attachSharedPreferences(prefs: SharedPreferences) {
        sharedPreferences = prefs
        val savedLevel = prefs.getInt(KEY_UNLOCKED_LEVEL, 1).coerceIn(1, CampaignLevel.entries.size)
        val savedBoxes = prefs.getInt(KEY_TOTAL_BOXES_CAPTURED, 0).coerceAtLeast(0)
        val savedHaptics = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        val loadedStats = loadSessionStatsFromPrefs(prefs)
        val loadedHistory = loadMatchHistoryFromPrefs(prefs)
        val savedTotalMatches = prefs.getInt(
            KEY_TOTAL_MATCHES,
            loadedHistory.maxOfOrNull { it.matchNumber } ?: 0
        ).coerceAtLeast(0)

        HapticEngine.isHapticsEnabled = savedHaptics
        _uiState.update { state ->
            val effectiveLevel = max(state.unlockedCampaignLevel, savedLevel)
            state.copy(
                unlockedCampaignLevel = effectiveLevel,
                selectedCampaignLevel = CampaignLevel.fromNumber(effectiveLevel),
                totalBoxesCaptured = max(state.totalBoxesCaptured, savedBoxes),
                hapticsEnabled = savedHaptics,
                sessionStats = loadedStats ?: state.sessionStats,
                matchHistory = if (loadedHistory.isNotEmpty()) loadedHistory else state.matchHistory,
                totalMatchesCompleted = max(state.totalMatchesCompleted, savedTotalMatches)
            )
        }
    }

    private fun loadSessionStatsFromPrefs(prefs: SharedPreferences): Map<Int, PlayerSessionStats>? {
        val raw = prefs.getString(KEY_SESSION_STATS_JSON, null) ?: return null
        return runCatching {
            val json = JSONObject(raw)
            val result = mutableMapOf<Int, PlayerSessionStats>()
            for (pNum in 1..4) {
                val obj = json.optJSONObject(pNum.toString())
                result[pNum] = if (obj != null) {
                    PlayerSessionStats(
                        wins = obj.optInt("wins", 0),
                        losses = obj.optInt("losses", 0),
                        ties = obj.optInt("ties", 0)
                    )
                } else {
                    PlayerSessionStats()
                }
            }
            result
        }.getOrNull()
    }

    private fun loadMatchHistoryFromPrefs(prefs: SharedPreferences): List<MatchHistoryItem> {
        val raw = prefs.getString(KEY_MATCH_HISTORY_JSON, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            val items = mutableListOf<MatchHistoryItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                items.add(
                    MatchHistoryItem(
                        matchNumber = obj.optInt("matchNumber", i + 1),
                        gridSizeLabel = obj.optString("gridSizeLabel", "4×4"),
                        scoreSummary = obj.optString("scoreSummary", ""),
                        winnerLabel = obj.optString("winnerLabel", ""),
                        winnerPlayerNumber = obj.optInt("winnerPlayerNumber", 0)
                    )
                )
            }
            items.take(5)
        }.getOrDefault(emptyList())
    }

    private fun savePerformanceDataToPrefs(
        sessionStats: Map<Int, PlayerSessionStats>,
        matchHistory: List<MatchHistoryItem>,
        totalMatchesCompleted: Int
    ) {
        val prefs = sharedPreferences ?: return
        runCatching {
            val statsJson = JSONObject()
            for ((pNum, stat) in sessionStats) {
                val obj = JSONObject()
                    .put("wins", stat.wins)
                    .put("losses", stat.losses)
                    .put("ties", stat.ties)
                statsJson.put(pNum.toString(), obj)
            }

            val historyArr = JSONArray()
            for (item in matchHistory.take(5)) {
                val obj = JSONObject()
                    .put("matchNumber", item.matchNumber)
                    .put("gridSizeLabel", item.gridSizeLabel)
                    .put("scoreSummary", item.scoreSummary)
                    .put("winnerLabel", item.winnerLabel)
                    .put("winnerPlayerNumber", item.winnerPlayerNumber)
                historyArr.put(obj)
            }

            prefs.edit()
                .putString(KEY_SESSION_STATS_JSON, statsJson.toString())
                .putString(KEY_MATCH_HISTORY_JSON, historyArr.toString())
                .putInt(KEY_TOTAL_MATCHES, totalMatchesCompleted)
                .apply()
        }
    }

    private fun saveUnlockedLevelToPrefs(unlockedLevel: Int) {
        val clamped = unlockedLevel.coerceIn(1, CampaignLevel.entries.size)
        sharedPreferences?.edit()?.putInt(KEY_UNLOCKED_LEVEL, clamped)?.apply()
    }

    private fun saveTotalBoxesCapturedToPrefs(totalBoxes: Int) {
        val clamped = totalBoxes.coerceAtLeast(0)
        sharedPreferences?.edit()?.putInt(KEY_TOTAL_BOXES_CAPTURED, clamped)?.apply()
    }

    private fun emitHaptic(type: HapticEventType, capturedCount: Int = 0) {
        if (!_uiState.value.hapticsEnabled) return
        hapticEventCounter++
        _uiState.update {
            it.copy(
                lastHapticEvent = HapticEvent(
                    id = hapticEventCounter,
                    type = type,
                    capturedCount = capturedCount
                )
            )
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        HapticEngine.isHapticsEnabled = enabled
        sharedPreferences?.edit()?.putBoolean(KEY_HAPTICS_ENABLED, enabled)?.apply()
        _uiState.update { it.copy(hapticsEnabled = enabled) }
        if (enabled) {
            emitHaptic(HapticEventType.LINE_TICK)
        }
    }

    fun toggleHaptics() {
        setHapticsEnabled(!_uiState.value.hapticsEnabled)
    }

    fun clearPersistentData() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        sharedPreferences?.edit()
            ?.putInt(KEY_UNLOCKED_LEVEL, 1)
            ?.putInt(KEY_TOTAL_BOXES_CAPTURED, 0)
            ?.remove(KEY_SESSION_STATS_JSON)
            ?.remove(KEY_MATCH_HISTORY_JSON)
            ?.putInt(KEY_TOTAL_MATCHES, 0)
            ?.apply()
        _uiState.update { state ->
            state.copy(
                totalBoxesCaptured = 0,
                unlockedCampaignLevel = 1,
                selectedCampaignLevel = CampaignLevel.LEVEL_1,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                sessionStats = mapOf(
                    1 to PlayerSessionStats(),
                    2 to PlayerSessionStats(),
                    3 to PlayerSessionStats(),
                    4 to PlayerSessionStats()
                ),
                matchHistory = emptyList(),
                totalMatchesCompleted = 0
            )
        }
    }

    fun openCreditsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.CREDITS) }
    }

    fun closeCreditsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.SETUP) }
    }

    fun openQuickMatchConfig() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.QUICK_CONFIG,
                mainMenuSection = MainMenuSection.QUICK_MATCH,
                setupErrorResId = null
            )
        }
    }

    fun closeQuickMatchConfig() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.SETUP,
                mainMenuSection = MainMenuSection.CAMPAIGN,
                setupErrorResId = null
            )
        }
    }

    fun selectMainMenuSection(section: MainMenuSection) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(mainMenuSection = section, setupErrorResId = null) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        SoundEngine.isSoundEnabled = enabled
        _uiState.update { it.copy(soundEnabled = enabled) }
        if (enabled) {
            SoundEngine.playLineClick()
        }
    }

    fun toggleSound() {
        setSoundEnabled(!_uiState.value.soundEnabled)
    }

    fun selectTimerMode(timerMode: TimerMode) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        val durationSec = if (timerMode.durationSeconds > 0) timerMode.durationSeconds.toFloat() else 5.0f
        _uiState.update { state ->
            state.copy(
                selectedTimerMode = timerMode,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                lastTurnForfeitedByTimeout = false
            )
        }
        if (_uiState.value.currentScreen == ScreenDestination.GAME) {
            restartBlitzTimerIfNeeded()
        }
    }

    fun selectGridSize(gridSize: GridSize) {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            val currentBoard = state.boardState
            val letters = currentBoard.players.map { it.letter }
            val durationSec = if (state.selectedTimerMode.durationSeconds > 0) {
                state.selectedTimerMode.durationSeconds.toFloat()
            } else {
                5.0f
            }
            state.copy(
                selectedGridSize = gridSize,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                boardState = BoardState.create(
                    humanPlayerCount = currentBoard.humanPlayerCount,
                    letters = letters,
                    cpuDifficulty = state.selectedCpuDifficulty,
                    boxRows = gridSize.boxRows,
                    boxCols = gridSize.boxCols
                )
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun selectGameMode(mode: GameMode) {
        _uiState.update { state ->
            state.copy(
                selectedGameMode = mode,
                selectedPlayerCount = if (mode == GameMode.CPU) 1 else state.selectedPlayerCount.coerceIn(2, 4),
                setupErrorResId = null
            )
        }
    }

    fun selectCpuDifficulty(difficulty: CpuDifficulty) {
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            state.copy(
                selectedCpuDifficulty = difficulty,
                boardState = state.boardState.copy(cpuDifficulty = difficulty),
                setupErrorResId = null
            )
        }
    }

    fun selectCampaignLevel(level: CampaignLevel) {
        if (level.levelNumber > _uiState.value.unlockedCampaignLevel) return
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update {
            it.copy(
                selectedCampaignLevel = level,
                setupErrorResId = null
            )
        }
    }

    fun resumeTournament() {
        val highestUnlocked = CampaignLevel.fromNumber(_uiState.value.unlockedCampaignLevel)
        startCampaignLevel(highestUnlocked)
    }

    fun startCampaignLevel(level: CampaignLevel = _uiState.value.selectedCampaignLevel) {
        val state = _uiState.value
        if (level.levelNumber > state.unlockedCampaignLevel) return

        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)

        val campaignBoard = BoardState.create(
            humanPlayerCount = 1,
            letters = listOf("P1", "CPU"),
            cpuDifficulty = level.difficulty,
            boxRows = level.gridSize.boxRows,
            boxCols = level.gridSize.boxCols
        )

        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                mainMenuSection = MainMenuSection.CAMPAIGN,
                selectedCampaignLevel = level,
                activeCampaignLevel = level,
                justUnlockedNextCampaignLevel = false,
                selectedGridSize = level.gridSize,
                selectedCpuDifficulty = level.difficulty,
                selectedGameMode = GameMode.CPU,
                selectedPlayerCount = 1,
                setupErrorResId = null,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = 5.0f,
                blitzRemainingFraction = 1f,
                boardState = campaignBoard
            )
        }
    }

    fun advanceToNextCampaignLevel() {
        val currentLevel = _uiState.value.activeCampaignLevel ?: return
        val nextNumber = (currentLevel.levelNumber + 1).coerceAtMost(CampaignLevel.entries.size)
        if (nextNumber <= _uiState.value.unlockedCampaignLevel) {
            startCampaignLevel(CampaignLevel.fromNumber(nextNumber))
        }
    }

    fun startGame(emptyErrorResId: Int = 0, sameLetterErrorResId: Int = 0) {
        val state = _uiState.value
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)

        val durationSec = if (state.selectedTimerMode.durationSeconds > 0) {
            state.selectedTimerMode.durationSeconds.toFloat()
        } else {
            5.0f
        }

        val initialBoard = BoardState.create(
            humanPlayerCount = 1,
            letters = listOf("P1", "CPU"),
            cpuDifficulty = state.selectedCpuDifficulty,
            boxRows = state.selectedGridSize.boxRows,
            boxCols = state.selectedGridSize.boxCols
        )

        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                setupErrorResId = null,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                boardState = initialBoard
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun onEdgeHovered(edge: EdgeCoordinate?) {
        if (_uiState.value.boardState.isCurrentPlayerAi) return
        _uiState.update { it.copy(hoveredEdge = edge) }
    }

    fun onEdgeClicked(edge: EdgeCoordinate) {
        val currentBoard = _uiState.value.boardState
        if (currentBoard.isCurrentPlayerAi || currentBoard.isGameOver) return

        val outcome = DotsAndBoxesEngine.makeMove(currentBoard, edge) ?: return
        applyMoveOutcomeWithAudioAndStats(outcome, wasHumanMove = true)
        if (_uiState.value.boardState.isCurrentPlayerAi) {
            blitzTimerJob?.cancel()
            scheduleCpuTurnIfNeeded()
        } else {
            restartBlitzTimerIfNeeded()
        }
    }

    private fun applyMoveOutcomeWithAudioAndStats(outcome: MoveOutcome, wasHumanMove: Boolean) {
        val newBoard = outcome.newState
        val capturedCount = outcome.capturedBoxes.size

        SoundEngine.playLineClick()
        if (capturedCount > 0) {
            SoundEngine.playBoxPop(newBoard.currentComboCount)
            emitHaptic(HapticEventType.BOX_CAPTURE_BUZZ, capturedCount)
        } else {
            emitHaptic(HapticEventType.LINE_TICK)
        }

        val justFinished = newBoard.isGameOver && !_uiState.value.boardState.isGameOver
        if (justFinished) {
            blitzTimerJob?.cancel()
            SoundEngine.playGameEndFanfare()
            emitHaptic(HapticEventType.VICTORY_CELEBRATION, capturedCount)
        }

        var unlockedToPersist: Int? = null
        var boxesCapturedToPersist: Int? = null
        var performanceToPersist: Triple<Map<Int, PlayerSessionStats>, List<MatchHistoryItem>, Int>? = null

        _uiState.update { state ->
            val updatedTotalBoxes = if (wasHumanMove && capturedCount > 0) {
                (state.totalBoxesCaptured + capturedCount).also { boxesCapturedToPersist = it }
            } else {
                state.totalBoxesCaptured
            }

            val updatedStats = if (justFinished) {
                recordMatchResult(state.sessionStats, newBoard)
            } else {
                state.sessionStats
            }
            val nextMatchCount = if (justFinished) {
                state.totalMatchesCompleted + 1
            } else {
                state.totalMatchesCompleted
            }
            val updatedHistory = if (justFinished) {
                val entry = buildMatchHistoryItem(nextMatchCount, newBoard)
                (listOf(entry) + state.matchHistory).take(5)
            } else {
                state.matchHistory
            }

            if (justFinished) {
                performanceToPersist = Triple(updatedStats, updatedHistory, nextMatchCount)
            }

            val campaignLevel = state.activeCampaignLevel
            val playerWonCampaign = justFinished && campaignLevel != null && newBoard.winner == 1
            val newUnlockedLevel = if (playerWonCampaign && campaignLevel != null) {
                val candidateNext = (campaignLevel.levelNumber + 1).coerceAtMost(CampaignLevel.entries.size)
                max(state.unlockedCampaignLevel, candidateNext)
            } else {
                state.unlockedCampaignLevel
            }
            val didUnlockNew = playerWonCampaign &&
                campaignLevel != null &&
                campaignLevel.levelNumber < CampaignLevel.entries.size

            if (newUnlockedLevel > state.unlockedCampaignLevel) {
                unlockedToPersist = newUnlockedLevel
            }

            val durationSec = if (state.selectedTimerMode.durationSeconds > 0) {
                state.selectedTimerMode.durationSeconds.toFloat()
            } else {
                5.0f
            }

            state.copy(
                boardState = newBoard,
                totalBoxesCaptured = updatedTotalBoxes,
                unlockedCampaignLevel = newUnlockedLevel,
                selectedCampaignLevel = if (newUnlockedLevel > state.unlockedCampaignLevel) {
                    CampaignLevel.fromNumber(newUnlockedLevel)
                } else {
                    state.selectedCampaignLevel
                },
                justUnlockedNextCampaignLevel = if (justFinished) didUnlockNew else state.justUnlockedNextCampaignLevel,
                sessionStats = updatedStats,
                matchHistory = updatedHistory,
                totalMatchesCompleted = nextMatchCount,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                victoryAnimationTriggerId = if (justFinished) {
                    state.victoryAnimationTriggerId + 1
                } else {
                    state.victoryAnimationTriggerId
                }
            )
        }

        unlockedToPersist?.let { saveUnlockedLevelToPrefs(it) }
        boxesCapturedToPersist?.let { saveTotalBoxesCapturedToPrefs(it) }
        performanceToPersist?.let { (stats, history, total) ->
            savePerformanceDataToPrefs(stats, history, total)
        }
    }

    private fun restartBlitzTimerIfNeeded() {
        blitzTimerJob?.cancel()
        val durationSec = _uiState.value.selectedTimerMode.durationSeconds.let {
            if (it > 0) it.toFloat() else 5.0f
        }
        _uiState.update {
            it.copy(
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f
            )
        }
    }

    fun onBlitzTimeoutForfeitTurn() {
        val currentBoard = _uiState.value.boardState
        if (currentBoard.isGameOver || currentBoard.isCurrentPlayerAi) return

        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.BOX_CAPTURE_BUZZ, 1)

        val forfeitedBoard = DotsAndBoxesEngine.forfeitTurn(currentBoard)
        _uiState.update { state ->
            state.copy(
                boardState = forfeitedBoard,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = true,
                blitzForfeitCount = state.blitzForfeitCount + 1,
                blitzRemainingSeconds = 0f,
                blitzRemainingFraction = 0f
            )
        }

        if (forfeitedBoard.isCurrentPlayerAi) {
            val cpuEdge = DotsAndBoxesEngine.cpuTakeTurn(
                state = forfeitedBoard,
                difficulty = forfeitedBoard.cpuDifficulty
            )
            if (cpuEdge != null) {
                val outcome = DotsAndBoxesEngine.makeMove(forfeitedBoard, cpuEdge)
                if (outcome != null) {
                    applyMoveOutcomeWithAudioAndStats(outcome, wasHumanMove = false)
                    _uiState.update { it.copy(lastTurnForfeitedByTimeout = true) }
                }
            }
            if (_uiState.value.boardState.isCurrentPlayerAi && !_uiState.value.boardState.isGameOver) {
                scheduleCpuTurnIfNeeded(immediateFirstMove = false)
            } else if (!_uiState.value.boardState.isGameOver) {
                restartBlitzTimerIfNeeded()
            }
        } else {
            restartBlitzTimerIfNeeded()
        }
    }

    private fun buildMatchHistoryItem(
        matchNumber: Int,
        finishedBoard: BoardState
    ): MatchHistoryItem {
        val winnerNum = finishedBoard.winner ?: 0
        val winnerLabel = when {
            winnerNum == 0 -> "Tie Game"
            finishedBoard.isPlayerAi(winnerNum) -> "CPU (${finishedBoard.letterForPlayer(winnerNum)})"
            else -> "Player $winnerNum (${finishedBoard.letterForPlayer(winnerNum)})"
        }
        val scoreSummary = finishedBoard.players.joinToString(" - ") { player ->
            val pName = if (player.isAi) "CPU" else "P${player.playerNumber}"
            "$pName: ${finishedBoard.scoreForPlayer(player.playerNumber)}"
        }
        return MatchHistoryItem(
            matchNumber = matchNumber,
            gridSizeLabel = "${finishedBoard.boxRows}×${finishedBoard.boxCols}",
            scoreSummary = scoreSummary,
            winnerLabel = winnerLabel,
            winnerPlayerNumber = winnerNum
        )
    }

    private fun recordMatchResult(
        currentStats: Map<Int, PlayerSessionStats>,
        finishedBoard: BoardState
    ): Map<Int, PlayerSessionStats> {
        val mutable = currentStats.toMutableMap()
        val winnerNum = finishedBoard.winner ?: return currentStats

        for (player in finishedBoard.players) {
            val pNum = player.playerNumber
            val prev = mutable[pNum] ?: PlayerSessionStats()
            mutable[pNum] = when {
                winnerNum == 0 -> prev.copy(ties = prev.ties + 1)
                winnerNum == pNum -> prev.copy(wins = prev.wins + 1)
                else -> prev.copy(losses = prev.losses + 1)
            }
        }
        return mutable
    }

    private fun scheduleCpuTurnIfNeeded(immediateFirstMove: Boolean = false) {
        aiTurnJob?.cancel()
        if (!_uiState.value.boardState.isCurrentPlayerAi) return

        aiTurnJob = viewModelScope.launch {
            var isFirstMove = immediateFirstMove
            while (_uiState.value.boardState.isCurrentPlayerAi && !_uiState.value.boardState.isGameOver) {
                val naturalDelayMs = if (isFirstMove) 260L else Random.nextLong(420L, 680L)
                isFirstMove = false
                delay(naturalDelayMs)
                val latestBoard = _uiState.value.boardState
                if (!latestBoard.isCurrentPlayerAi || latestBoard.isGameOver) break

                val cpuEdge = DotsAndBoxesEngine.cpuTakeTurn(
                    state = latestBoard,
                    difficulty = latestBoard.cpuDifficulty
                ) ?: break
                val outcome = DotsAndBoxesEngine.makeMove(latestBoard, cpuEdge) ?: break
                applyMoveOutcomeWithAudioAndStats(outcome, wasHumanMove = false)
            }

            if (!_uiState.value.boardState.isGameOver && !_uiState.value.boardState.isCurrentPlayerAi) {
                restartBlitzTimerIfNeeded()
            }
        }
    }

    fun adjustZoom(delta: Float) {
        _uiState.update { state ->
            state.copy(boardZoom = (state.boardZoom + delta).coerceIn(0.65f, 1.35f))
        }
    }

    fun setZoom(zoom: Float) {
        _uiState.update { state ->
            state.copy(boardZoom = zoom.coerceIn(0.65f, 1.35f))
        }
    }

    fun restartMatch() {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        val state = _uiState.value
        val currentBoard = state.boardState
        val letters = currentBoard.players.map { it.letter }
        val durationSec = if (state.selectedTimerMode.durationSeconds > 0) {
            state.selectedTimerMode.durationSeconds.toFloat()
        } else {
            5.0f
        }
        _uiState.update {
            it.copy(
                hoveredEdge = null,
                justUnlockedNextCampaignLevel = false,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                boardState = BoardState.create(
                    humanPlayerCount = currentBoard.humanPlayerCount,
                    letters = letters,
                    cpuDifficulty = state.selectedCpuDifficulty,
                    boxRows = state.selectedGridSize.boxRows,
                    boxCols = state.selectedGridSize.boxCols
                )
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun returnToSetup() {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.SETUP,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false
            )
        }
    }
}

// ============================================================================
// 5. MAIN ACTIVITY & ROOT APP COMPOSABLE
// ============================================================================

private val ProtocolDarkBackground = Color(0xFF121212)
private val ProtocolCardSurface = Color(0xFF1A1E29)
private val ProtocolElevatedSurface = Color(0xFF222838)
private val ProtocolCyan = Color(0xFF00E5FF)
private val ProtocolMagenta = Color(0xFFFF007F)
private val ProtocolLime = Color(0xFF00E676)
private val ProtocolAmber = Color(0xFFFFEA00)
private val ProtocolTextPrimary = Color(0xFFF8FAFC)
private val ProtocolTextMuted = Color(0xFF94A3B8)
private val ProtocolBorder = Color(0xFF2D3748)

private data class FloatingDotParticle(
    val xFraction: Float,
    val yFraction: Float,
    val radiusDp: Float,
    val speedX: Float,
    val speedY: Float,
    val phaseOffset: Float,
    val isCyan: Boolean,
    val baseAlpha: Float
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeonGridProtocolApp()
        }
    }
}

@Composable
fun NeonGridProtocolApp(
    viewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        val prefs = context.getSharedPreferences(GameViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        viewModel.attachSharedPreferences(prefs)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NeonGridProtocolTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ProtocolDarkBackground,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Crossfade(
                targetState = uiState.currentScreen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "neonGridProtocolScreenTransition"
            ) { destination ->
                when (destination) {
                    // Screen 1: The Command Center (Main Menu)
                    ScreenDestination.SETUP -> {
                        CommandCenterScreen(
                            nodesCaptured = uiState.totalBoxesCaptured,
                            unlockedCampaignLevel = uiState.unlockedCampaignLevel,
                            selectedCampaignLevel = uiState.selectedCampaignLevel,
                            sessionStats = uiState.sessionStats,
                            matchHistory = uiState.matchHistory,
                            onResumeCampaign = viewModel::resumeTournament,
                            onOpenQuickMatch = viewModel::openQuickMatchConfig,
                            onSelectCampaignLevel = viewModel::selectCampaignLevel,
                            onStartCampaignLevel = viewModel::startCampaignLevel
                        )
                    }

                    // Screen 3: Quick Match Config
                    ScreenDestination.QUICK_CONFIG -> {
                        QuickMatchConfigScreen(
                            selectedGridSize = uiState.selectedGridSize,
                            selectedCpuDifficulty = uiState.selectedCpuDifficulty,
                            selectedTimerMode = uiState.selectedTimerMode,
                            onSelectGridSize = viewModel::selectGridSize,
                            onSelectCpuDifficulty = viewModel::selectCpuDifficulty,
                            onSelectTimerMode = viewModel::selectTimerMode,
                            onStartMatch = { viewModel.startGame() },
                            onBackToCommandCenter = viewModel::closeQuickMatchConfig
                        )
                    }

                    // Screen 2: The Grid Arena (Gameplay)
                    ScreenDestination.GAME -> {
                        GridArenaScreen(
                            soundEnabled = uiState.soundEnabled,
                            hapticsEnabled = uiState.hapticsEnabled,
                            activeCampaignLevel = uiState.activeCampaignLevel,
                            justUnlockedNextCampaignLevel = uiState.justUnlockedNextCampaignLevel,
                            selectedGridSize = uiState.selectedGridSize,
                            selectedCpuDifficulty = uiState.selectedCpuDifficulty,
                            selectedTimerMode = uiState.selectedTimerMode,
                            blitzRemainingSeconds = uiState.blitzRemainingSeconds,
                            blitzRemainingFraction = uiState.blitzRemainingFraction,
                            lastTurnForfeitedByTimeout = uiState.lastTurnForfeitedByTimeout,
                            boardState = uiState.boardState,
                            hoveredEdge = uiState.hoveredEdge,
                            boardZoom = uiState.boardZoom,
                            lastHapticEvent = uiState.lastHapticEvent,
                            victoryAnimationTriggerId = uiState.victoryAnimationTriggerId,
                            onSelectGridSize = viewModel::selectGridSize,
                            onSelectCpuDifficulty = viewModel::selectCpuDifficulty,
                            onSelectTimerMode = viewModel::selectTimerMode,
                            onBlitzTimeout = viewModel::onBlitzTimeoutForfeitTurn,
                            onAdvanceToNextCampaignLevel = viewModel::advanceToNextCampaignLevel,
                            onToggleSound = viewModel::toggleSound,
                            onToggleHaptics = viewModel::toggleHaptics,
                            onEdgeHover = viewModel::onEdgeHovered,
                            onEdgeClick = viewModel::onEdgeClicked,
                            onAdjustZoom = viewModel::adjustZoom,
                            onSetZoom = viewModel::setZoom,
                            onRestartMatch = viewModel::restartMatch,
                            onReturnToCommandCenter = viewModel::returnToSetup
                        )
                    }

                    ScreenDestination.CREDITS -> {
                        CreditsScreen(
                            onBack = viewModel::closeCreditsScreen
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 6. SCREEN 1: THE COMMAND CENTER (MAIN MENU)
//    - Glowing Cyan button: 'Resume Campaign'
//    - Glowing Magenta button: 'Quick Match'
//    - Stats row showing 'Nodes Captured' (Integer)
//    - Strictly NO text fields, NO user letters or names
// ============================================================================

@Composable
fun CommandCenterScreen(
    nodesCaptured: Int = 0,
    unlockedCampaignLevel: Int = 1,
    selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    sessionStats: Map<Int, PlayerSessionStats> = emptyMap(),
    matchHistory: List<MatchHistoryItem> = emptyList(),
    onResumeCampaign: () -> Unit = {},
    onOpenQuickMatch: () -> Unit = {},
    onSelectCampaignLevel: (CampaignLevel) -> Unit = {},
    onStartCampaignLevel: (CampaignLevel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "commandCenterTransition")
    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "headerGlow"
    )
    val driftProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "floatingDotsDrift"
    )

    val floatingDots = remember {
        listOf(
            FloatingDotParticle(0.10f, 0.12f, 5.0f, 1.0f, 1.0f, 0.00f, true, 0.24f),
            FloatingDotParticle(0.26f, 0.22f, 3.8f, -1.0f, 1.0f, 0.14f, false, 0.22f),
            FloatingDotParticle(0.45f, 0.09f, 5.4f, 1.0f, -1.0f, 0.28f, true, 0.20f),
            FloatingDotParticle(0.68f, 0.18f, 4.2f, -1.0f, -1.0f, 0.42f, false, 0.24f),
            FloatingDotParticle(0.88f, 0.14f, 5.2f, 1.0f, 1.0f, 0.56f, true, 0.19f),
            FloatingDotParticle(0.15f, 0.42f, 4.6f, -1.0f, 1.0f, 0.70f, false, 0.22f),
            FloatingDotParticle(0.35f, 0.48f, 3.5f, 1.0f, -1.0f, 0.84f, true, 0.20f),
            FloatingDotParticle(0.58f, 0.39f, 5.6f, -1.0f, 1.0f, 0.18f, false, 0.20f),
            FloatingDotParticle(0.82f, 0.46f, 4.0f, 1.0f, 1.0f, 0.33f, true, 0.24f),
            FloatingDotParticle(0.12f, 0.72f, 5.0f, 1.0f, -1.0f, 0.49f, true, 0.22f),
            FloatingDotParticle(0.32f, 0.81f, 4.2f, -1.0f, -1.0f, 0.63f, false, 0.23f),
            FloatingDotParticle(0.54f, 0.68f, 5.2f, 1.0f, 1.0f, 0.77f, true, 0.19f),
            FloatingDotParticle(0.75f, 0.78f, 4.4f, -1.0f, 1.0f, 0.91f, false, 0.24f),
            FloatingDotParticle(0.90f, 0.86f, 3.8f, 1.0f, -1.0f, 0.25f, true, 0.21f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .testTag("command_center_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("drifting_neon_dots_background")
        ) {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@Canvas

            val angle = driftProgress * 2f * PI.toFloat()
            floatingDots.forEach { dot ->
                val phaseAngle = angle + dot.phaseOffset * 2f * PI.toFloat()
                val offsetX = cos(phaseAngle * dot.speedX) * (width * 0.045f)
                val offsetY = sin(phaseAngle * dot.speedY) * (height * 0.045f)

                val centerX = ((dot.xFraction * width + offsetX) % width + width) % width
                val centerY = ((dot.yFraction * height + offsetY) % height + height) % height
                val dotColor = if (dot.isCyan) ProtocolCyan else ProtocolMagenta
                val radiusPx = dot.radiusDp.dp.toPx()

                drawCircle(
                    color = dotColor.copy(alpha = dot.baseAlpha * 0.4f),
                    radius = radiusPx * 2.5f,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = dotColor.copy(alpha = dot.baseAlpha),
                    radius = radiusPx,
                    center = Offset(centerX, centerY)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 580.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommandCenterHeader(glowAlpha = headerGlow)

            CommandCenterStatsRow(
                nodesCaptured = nodesCaptured,
                unlockedCampaignLevel = unlockedCampaignLevel,
                totalCampaignLevels = CampaignLevel.entries.size
            )

            CommandCenterActionButtons(
                unlockedCampaignLevel = unlockedCampaignLevel,
                glowAlpha = headerGlow,
                onResumeCampaignClick = onResumeCampaign,
                onQuickMatchClick = onOpenQuickMatch
            )

            CampaignStageSelectorCard(
                unlockedCampaignLevel = unlockedCampaignLevel,
                selectedCampaignLevel = selectedCampaignLevel,
                onSelectCampaignLevel = onSelectCampaignLevel,
                onStartCampaignLevel = onStartCampaignLevel
            )

            DashboardSessionHistoryCard(
                sessionStats = sessionStats,
                matchHistory = matchHistory
            )
        }
    }
}

@Composable
private fun CommandCenterHeader(
    glowAlpha: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ProtocolCardSurface)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ProtocolCyan.copy(alpha = glowAlpha),
                        ProtocolMagenta.copy(alpha = glowAlpha)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "NEON GRID PROTOCOL",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 27.sp,
                letterSpacing = 2.4.sp,
                shadow = Shadow(
                    color = ProtocolCyan.copy(alpha = glowAlpha),
                    offset = Offset.Zero,
                    blurRadius = 28f
                )
            ),
            color = ProtocolTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("neon_protocol_title")
        )

        Text(
            text = "THE COMMAND CENTER",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.6.sp,
                shadow = Shadow(
                    color = ProtocolMagenta.copy(alpha = 0.75f),
                    offset = Offset.Zero,
                    blurRadius = 14f
                )
            ),
            color = ProtocolMagenta,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CommandCenterStatsRow(
    nodesCaptured: Int,
    unlockedCampaignLevel: Int,
    totalCampaignLevels: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_stats_header_row"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Nodes Captured (Integer)
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = ProtocolCyan.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(ProtocolCardSurface)
                .border(1.5.dp, ProtocolCyan, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("nodes_captured_stat"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.GridView,
                    contentDescription = null,
                    tint = ProtocolCyan,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Nodes Captured",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = ProtocolTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = nodesCaptured.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(
                        color = ProtocolCyan,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = ProtocolCyan,
                modifier = Modifier.testTag("nodes_captured_value")
            )
        }

        // Campaign Level Progress
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = ProtocolMagenta.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(ProtocolCardSurface)
                .border(1.5.dp, ProtocolMagenta, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("campaign_level_reached_stat"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = ProtocolMagenta,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Campaign Stage",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = ProtocolTextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Lvl $unlockedCampaignLevel / $totalCampaignLevels",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(
                        color = ProtocolMagenta,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = ProtocolMagenta,
                modifier = Modifier.testTag("campaign_level_reached_value")
            )
        }
    }
}

@Composable
private fun CommandCenterActionButtons(
    unlockedCampaignLevel: Int,
    glowAlpha: Float,
    onResumeCampaignClick: () -> Unit,
    onQuickMatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLevel = CampaignLevel.fromNumber(unlockedCampaignLevel)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Glowing Cyan Button: 'Resume Campaign'
        Button(
            onClick = onResumeCampaignClick,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ProtocolCyan,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            modifier = Modifier
                .weight(1f)
                .height(84.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = ProtocolCyan.copy(alpha = glowAlpha)
                )
                .border(
                    width = 2.dp,
                    color = ProtocolCyan,
                    shape = RoundedCornerShape(18.dp)
                )
                .testTag("resume_campaign_button")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Resume Campaign",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lvl ${currentLevel.levelNumber} • ${currentLevel.gridSize.label} ${currentLevel.difficulty.label}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp
                    ),
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Glowing Magenta Button: 'Quick Match'
        Button(
            onClick = onQuickMatchClick,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ProtocolMagenta,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            modifier = Modifier
                .weight(1f)
                .height(84.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = ProtocolMagenta.copy(alpha = glowAlpha)
                )
                .border(
                    width = 2.dp,
                    color = ProtocolMagenta,
                    shape = RoundedCornerShape(18.dp)
                )
                .testTag("quick_match_button")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Quick Match",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Configure Arena & Timers",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CampaignStageSelectorCard(
    unlockedCampaignLevel: Int = 1,
    selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    onSelectCampaignLevel: (CampaignLevel) -> Unit = {},
    onStartCampaignLevel: (CampaignLevel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, ProtocolCyan.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
            .testTag("campaign_protocol_section"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CAMPAIGN PROTOCOL (6 STAGES)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ProtocolCyan
                )
                Text(
                    text = "Unlocked: $unlockedCampaignLevel / ${CampaignLevel.entries.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ProtocolAmber,
                    modifier = Modifier.testTag("campaign_unlocked_progress_text")
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampaignLevel.entries.forEach { level ->
                    val isUnlocked = level.levelNumber <= unlockedCampaignLevel
                    val isCleared = level.levelNumber < unlockedCampaignLevel
                    val isSelected = selectedCampaignLevel == level && isUnlocked
                    val diffColor = when (level.difficulty) {
                        CpuDifficulty.EASY -> ProtocolLime
                        CpuDifficulty.MEDIUM -> ProtocolAmber
                        CpuDifficulty.HARD -> ProtocolMagenta
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isUnlocked) 1f else 0.45f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) ProtocolCyan.copy(alpha = 0.14f) else ProtocolElevatedSurface
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = when {
                                    isSelected -> ProtocolCyan
                                    isCleared -> ProtocolLime.copy(alpha = 0.7f)
                                    else -> ProtocolBorder
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = isUnlocked) {
                                onSelectCampaignLevel(level)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("campaign_level_${level.levelNumber}_card"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) ProtocolDarkBackground else ProtocolBorder.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isUnlocked) ProtocolCyan else ProtocolBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!isUnlocked) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "LOCKED",
                                        tint = ProtocolTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isCleared) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Cleared",
                                        tint = ProtocolLime,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = level.levelNumber.toString(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ProtocolCyan
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = level.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isUnlocked) ProtocolTextPrimary else ProtocolTextMuted
                                )
                                Text(
                                    text = "${level.gridSize.label} Grid • ${level.difficulty.label} AI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = if (isUnlocked) diffColor else ProtocolTextMuted
                                )
                            }
                        }

                        if (isUnlocked) {
                            Button(
                                onClick = { onStartCampaignLevel(level) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProtocolCyan,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("campaign_level_${level.levelNumber}")
                            ) {
                                Text(
                                    text = "Deploy",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ProtocolDarkBackground)
                                    .border(1.dp, ProtocolBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("campaign_level_${level.levelNumber}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "LOCKED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ProtocolTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 7. SCREEN 3: QUICK MATCH CONFIG
//    - Simple dropdowns AND toggles for Grid Size (3x3 to 6x6),
//      CPU Difficulty (Easy, Medium, Hard), and Time Limit (Relaxed, 3s, 5s, 10s).
//    - Strictly NO text inputs.
// ============================================================================

@Composable
fun QuickMatchConfigScreen(
    selectedGridSize: GridSize,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onStartMatch: () -> Unit,
    onBackToCommandCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var gridDropdownExpanded by remember { mutableStateOf(false) }
    var diffDropdownExpanded by remember { mutableStateOf(false) }
    var timerDropdownExpanded by remember { mutableStateOf(false) }

    BackHandler {
        onBackToCommandCenter()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .testTag("quick_match_config_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        DriftingNeonDotsBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar with Back to Command Center
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBackToCommandCenter,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolTextPrimary),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("back_to_command_center_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Command Center",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Command Center",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Text(
                    text = "QUICK MATCH CONFIG",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Black,
                        color = ProtocolMagenta
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, ProtocolMagenta.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                    .testTag("quick_match_section"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "PROTOCOL PARAMETERS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = ProtocolTextPrimary
                        )
                        Text(
                            text = "Select Grid Size, CPU Difficulty, and Time Limit before entering The Grid Arena.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ProtocolTextMuted
                        )
                    }

                    // 1. GRID SIZE DROPDOWN + TOGGLE CHIPS (3x3, 4x4, 5x5, 6x6)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GRID SIZE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ProtocolCyan
                            )

                            Box {
                                OutlinedButton(
                                    onClick = { gridDropdownExpanded = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolCyan),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("grid_size_dropdown_button")
                                ) {
                                    Text(
                                        text = "${selectedGridSize.label} (${selectedGridSize.totalBoxes} Nodes)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select Grid Size"
                                    )
                                }
                                DropdownMenu(
                                    expanded = gridDropdownExpanded,
                                    onDismissRequest = { gridDropdownExpanded = false },
                                    modifier = Modifier.background(ProtocolElevatedSurface)
                                ) {
                                    GridSize.entries.forEach { sizeOption ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "${sizeOption.label} Grid (${sizeOption.totalBoxes} Nodes)",
                                                    color = ProtocolTextPrimary,
                                                    fontFamily = JetBrainsMonoFontFamily
                                                )
                                            },
                                            onClick = {
                                                onSelectGridSize(sizeOption)
                                                gridDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GridSize.entries.forEach { sizeOption ->
                                val isSelected = selectedGridSize == sizeOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) ProtocolCyan else ProtocolElevatedSurface)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) ProtocolCyan else ProtocolBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectGridSize(sizeOption) }
                                        .testTag("setup_grid_${sizeOption.boxRows}x${sizeOption.boxCols}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = sizeOption.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isSelected) Color.Black else ProtocolTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // 2. CPU DIFFICULTY DROPDOWN + TOGGLE CHIPS (Easy, Medium, Hard)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CPU DIFFICULTY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ProtocolAmber
                            )

                            Box {
                                OutlinedButton(
                                    onClick = { diffDropdownExpanded = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolAmber),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("cpu_difficulty_dropdown_button")
                                ) {
                                    Text(
                                        text = selectedCpuDifficulty.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select CPU Difficulty"
                                    )
                                }
                                DropdownMenu(
                                    expanded = diffDropdownExpanded,
                                    onDismissRequest = { diffDropdownExpanded = false },
                                    modifier = Modifier.background(ProtocolElevatedSurface)
                                ) {
                                    CpuDifficulty.entries.forEach { diff ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = when (diff) {
                                                        CpuDifficulty.EASY -> "Easy (Random AI)"
                                                        CpuDifficulty.MEDIUM -> "Medium (Greedy AI)"
                                                        CpuDifficulty.HARD -> "Hard (Minimax AI)"
                                                    },
                                                    color = ProtocolTextPrimary,
                                                    fontFamily = JetBrainsMonoFontFamily
                                                )
                                            },
                                            onClick = {
                                                onSelectCpuDifficulty(diff)
                                                diffDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CpuDifficulty.entries.forEach { diff ->
                                val isSelected = selectedCpuDifficulty == diff
                                val diffAccent = when (diff) {
                                    CpuDifficulty.EASY -> ProtocolLime
                                    CpuDifficulty.MEDIUM -> ProtocolAmber
                                    CpuDifficulty.HARD -> ProtocolMagenta
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) diffAccent else ProtocolElevatedSurface)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) diffAccent else ProtocolBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectCpuDifficulty(diff) }
                                        .testTag("cpu_diff_${diff.name.lowercase()}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = diff.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isSelected) Color.Black else ProtocolTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // 3. TIME LIMIT DROPDOWN + TOGGLE CHIPS (Relaxed, 3s, 5s, 10s)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TIME LIMIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ProtocolMagenta
                            )

                            Box {
                                OutlinedButton(
                                    onClick = { timerDropdownExpanded = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolMagenta),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("time_limit_dropdown_button")
                                ) {
                                    Text(
                                        text = selectedTimerMode.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Select Time Limit"
                                    )
                                }
                                DropdownMenu(
                                    expanded = timerDropdownExpanded,
                                    onDismissRequest = { timerDropdownExpanded = false },
                                    modifier = Modifier.background(ProtocolElevatedSurface)
                                ) {
                                    TimerMode.entries.forEach { mode ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = mode.label,
                                                    color = ProtocolTextPrimary,
                                                    fontFamily = JetBrainsMonoFontFamily
                                                )
                                            },
                                            onClick = {
                                                onSelectTimerMode(mode)
                                                timerDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TimerMode.entries.forEach { mode ->
                                val isSelected = selectedTimerMode == mode
                                val modeAccent = if (mode == TimerMode.RELAXED) ProtocolCyan else ProtocolMagenta
                                val modeIcon = if (mode == TimerMode.RELAXED) Icons.Filled.TimerOff else Icons.Filled.Timer
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) modeAccent else ProtocolElevatedSurface)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) modeAccent else ProtocolBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectTimerMode(mode) }
                                        .testTag("timer_${mode.name.lowercase()}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = modeIcon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.Black else modeAccent,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = mode.label,
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontSize = 12.sp
                                            ),
                                            color = if (isSelected) Color.Black else ProtocolTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Launch Grid Arena Button
                    Button(
                        onClick = onStartMatch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_game_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProtocolMagenta,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LAUNCH GRID ARENA",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSessionHistoryCard(
    sessionStats: Map<Int, PlayerSessionStats>,
    matchHistory: List<MatchHistoryItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ProtocolBorder, RoundedCornerShape(18.dp))
            .testTag("dashboard_stats_history_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        tint = ProtocolAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "PROTOCOL TELEMETRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ProtocolTextMuted
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val p1Stat = sessionStats[1] ?: PlayerSessionStats()
                    val cpuStat = sessionStats[2] ?: PlayerSessionStats()
                    Text(
                        text = "USER: ${p1Stat.wins}W-${p1Stat.losses}L",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ProtocolCyan
                    )
                    Text(
                        text = "CPU: ${cpuStat.wins}W-${cpuStat.losses}L",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ProtocolMagenta
                    )
                }
            }

            if (matchHistory.isEmpty()) {
                Text(
                    text = "No completed matches in this session yet.",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProtocolTextMuted
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items = matchHistory, key = { it.matchNumber }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ProtocolElevatedSurface)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${item.matchNumber} (${item.gridSizeLabel}) • ${item.scoreSummary}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily
                                ),
                                color = ProtocolTextPrimary
                            )
                            Text(
                                text = item.winnerLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (item.winnerPlayerNumber == 1) ProtocolCyan else ProtocolMagenta
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 8. CREDITS SCREEN & DRIFTING BACKGROUND DOTS
// ============================================================================

private data class DriftingDotSpec(
    val initialXFraction: Float,
    val initialYFraction: Float,
    val radiusDp: Float,
    val driftSpeedX: Float,
    val driftSpeedY: Float,
    val phaseOffset: Float,
    val isCyan: Boolean,
    val baseAlpha: Float
)

@Composable
fun DriftingNeonDotsBackground(
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val infiniteTransition = rememberInfiniteTransition(label = "driftingNeonDotsTransition")
    val driftPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "driftPhase"
    )
    val shimmerPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerPulse"
    )

    val dots = remember {
        List(38) { idx ->
            DriftingDotSpec(
                initialXFraction = ((idx * 37 + 13) % 100) / 100f,
                initialYFraction = ((idx * 53 + 29) % 100) / 100f,
                radiusDp = 2.5f + (idx % 5) * 1.4f,
                driftSpeedX = if (idx % 2 == 0) 0.16f else -0.14f,
                driftSpeedY = -0.22f - (idx % 4) * 0.06f,
                phaseOffset = (idx * 0.17f) % 1f,
                isCyan = idx % 2 == 0,
                baseAlpha = 0.10f + (idx % 4) * 0.045f
            )
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("drifting_neon_dots_background")
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        for (dot in dots) {
            val t = (driftPhase + dot.phaseOffset) % 1f
            val waveX = sin((t * 2f * PI).toDouble()).toFloat() * 24f
            val rawX = (dot.initialXFraction + t * dot.driftSpeedX) % 1f
            val rawY = (dot.initialYFraction + t * dot.driftSpeedY) % 1f
            val normalizedX = if (rawX < 0f) rawX + 1f else rawX
            val normalizedY = if (rawY < 0f) rawY + 1f else rawY

            val px = (normalizedX * w + waveX).coerceIn(0f, w)
            val py = (normalizedY * h).coerceIn(0f, h)

            val dotColor = if (dot.isCyan) palette.p1Accent else palette.p2Accent
            val alpha = (dot.baseAlpha * shimmerPulse).coerceIn(0.06f, 0.32f)
            val radiusPx = dot.radiusDp.dp.toPx()

            drawCircle(
                color = dotColor.copy(alpha = alpha * 0.38f),
                radius = radiusPx * 2.4f,
                center = Offset(px, py)
            )
            drawCircle(
                color = dotColor.copy(alpha = alpha),
                radius = radiusPx,
                center = Offset(px, py)
            )
        }
    }
}

@Composable
fun CreditsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val scrollState = rememberScrollState()

    BackHandler {
        onBack()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "creditsGlowTransition")
    val borderGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "creditsBorderGlow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .testTag("credits_screen"),
        contentAlignment = Alignment.Center
    ) {
        DriftingNeonDotsBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 540.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = palette.p1Accent.copy(alpha = borderGlow)
                    )
                    .border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                palette.p1Accent.copy(alpha = borderGlow),
                                palette.p2Accent.copy(alpha = borderGlow),
                                palette.p1Accent.copy(alpha = borderGlow)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .testTag("credits_screen_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = palette.surface.copy(alpha = 0.94f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(palette.p1Accent.copy(alpha = 0.16f))
                            .border(2.dp, palette.p1Accent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = palette.p1Accent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "SYSTEM CREDITS",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            shadow = Shadow(
                                color = palette.p1Accent,
                                offset = Offset.Zero,
                                blurRadius = 20f
                            )
                        ),
                        color = palette.primaryText,
                        textAlign = TextAlign.Center
                    )

                    CreditSectionCard(
                        icon = Icons.Filled.Code,
                        text = "Design & Development: [Your Name/Handle]",
                        accentColor = palette.p1Accent,
                        testTag = "credits_design_dev_text"
                    )

                    CreditSectionCard(
                        icon = Icons.Filled.Memory,
                        text = "AI Co-Pilot & Logic: Gemini",
                        accentColor = palette.p2Accent,
                        testTag = "credits_ai_copilot_text"
                    )

                    CreditSectionCard(
                        icon = Icons.Filled.AutoAwesome,
                        text = "Framework: Kotlin & Jetpack Compose via Google AI Studio",
                        accentColor = palette.p3Accent,
                        testTag = "credits_framework_text"
                    )

                    CreditSectionCard(
                        icon = Icons.Filled.CloudDone,
                        text = "Build Pipeline: Cloud CI/CD Assembly",
                        accentColor = palette.streakAccent,
                        testTag = "credits_build_pipeline_text"
                    )

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("credits_back_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.p1Accent,
                            contentColor = Color.Black
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BACK",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreditSectionCard(
    icon: ImageVector,
    text: String,
    accentColor: Color,
    testTag: String
) {
    val palette = LocalGamePalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.backgroundDeep.copy(alpha = 0.85f))
            .border(1.5.dp, accentColor.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                shadow = Shadow(
                    color = accentColor.copy(alpha = 0.65f),
                    offset = Offset.Zero,
                    blurRadius = 12f
                )
            ),
            color = palette.primaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(testTag)
        )
    }
}

// ============================================================================
// 9. SCREEN 2: THE GRID ARENA (GAMEPLAY)
//    - Dynamic grid of dots (3x3 up to 6x6)
//    - Tap between dots to draw glowing lines
//    - 4th line forming a closed square captures the node, scores +1, and grants extra turn
//    - Shrinking progress bar for the timer & LocalHapticFeedback for drawing lines
// ============================================================================

@Composable
fun GridArenaScreen(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    activeCampaignLevel: CampaignLevel?,
    justUnlockedNextCampaignLevel: Boolean,
    selectedGridSize: GridSize,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    blitzRemainingSeconds: Float,
    blitzRemainingFraction: Float,
    lastTurnForfeitedByTimeout: Boolean,
    boardState: BoardState,
    hoveredEdge: EdgeCoordinate?,
    boardZoom: Float,
    lastHapticEvent: HapticEvent?,
    victoryAnimationTriggerId: Int,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onBlitzTimeout: () -> Unit = {},
    onAdvanceToNextCampaignLevel: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit,
    onAdjustZoom: (Float) -> Unit,
    onSetZoom: (Float) -> Unit,
    onRestartMatch: () -> Unit,
    onReturnToCommandCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val isTimedMode = selectedTimerMode.isTimed
    val timerDurationSeconds = selectedTimerMode.durationSeconds
    val isPlayerTurn = !boardState.isGameOver && !boardState.isCurrentPlayerAi
    var timerProgress by remember { mutableFloatStateOf(blitzRemainingFraction.coerceIn(0f, 1f)) }
    var timerSecondsLeft by remember {
        mutableFloatStateOf(
            if (timerDurationSeconds > 0) timerDurationSeconds.toFloat() else 5f
        )
    }

    // Shrinking progress bar LaunchedEffect for timed modes (3s, 5s, 10s)
    LaunchedEffect(selectedTimerMode, isPlayerTurn, boardState.totalMovesPlayed, boardState.currentPlayer) {
        if (isTimedMode && isPlayerTurn) {
            val totalDurationMs = timerDurationSeconds * 1000L
            val tickMs = 50L
            var remainingMs = totalDurationMs
            timerProgress = 1f
            timerSecondsLeft = timerDurationSeconds.toFloat()
            while (remainingMs > 0L) {
                delay(tickMs)
                remainingMs = (remainingMs - tickMs).coerceAtLeast(0L)
                timerProgress = remainingMs.toFloat() / totalDurationMs.toFloat()
                timerSecondsLeft = remainingMs / 1000f
            }
            onBlitzTimeout()
        } else {
            timerProgress = 1f
            timerSecondsLeft = if (timerDurationSeconds > 0) timerDurationSeconds.toFloat() else 5f
        }
    }

    // Direct LocalHapticFeedback on line draws (light tick) and node captures (heavy buzz)
    val onHumanMoveExecuted: (EdgeCoordinate) -> Unit = { edge ->
        if (isPlayerTurn && DotsAndBoxesEngine.isEdgeAvailable(boardState, edge)) {
            val outcome = DotsAndBoxesEngine.makeMove(boardState, edge)
            if (outcome != null) {
                if (hapticsEnabled) {
                    if (outcome.capturedBoxes.isNotEmpty()) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    } else {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                }
                onEdgeClick(edge)
            }
        }
    }

    // Hardware & Compose Haptics for AI moves and general events
    LaunchedEffect(lastHapticEvent?.id) {
        val event = lastHapticEvent ?: return@LaunchedEffect
        if (!hapticsEnabled) return@LaunchedEffect
        when (event.type) {
            HapticEventType.LINE_TICK -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                HapticEngine.triggerLineTick(context, null)
            }
            HapticEventType.BOX_CAPTURE_BUZZ -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                HapticEngine.triggerBoxCaptureBuzz(context, null, event.capturedCount)
            }
            HapticEventType.VICTORY_CELEBRATION -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                HapticEngine.triggerVictoryCelebration(context, null)
            }
        }
    }

    BackHandler {
        onReturnToCommandCenter()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val neonGlowPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonGlowPulse"
    )
    val comboHeartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "comboHeartbeatScale"
    )

    val boardComboZoomAnim = remember { Animatable(1f) }
    val comboPulseWaveAnim = remember { Animatable(0f) }
    val cameraShakeAnim = remember { Animatable(0f) }
    val overlayBurstScale = remember { Animatable(0f) }
    val overlayBurstAlpha = remember { Animatable(0f) }

    LaunchedEffect(boardState.comboTriggerId) {
        if (boardState.comboTriggerId > 0) {
            val isCombo = boardState.currentComboCount >= 2 || boardState.lastCapturedBoxes.size >= 2
            val targetZoomPunch = if (isCombo) {
                1.04f + (boardState.currentComboCount.coerceAtMost(6) * 0.007f)
            } else {
                1.015f
            }

            launch {
                val shakeMagnitude = if (isCombo) 8f else 3.5f
                for (step in listOf(1f, -0.85f, 0.7f, -0.5f, 0.3f, 0f)) {
                    cameraShakeAnim.animateTo(
                        targetValue = step * shakeMagnitude,
                        animationSpec = tween(durationMillis = 40)
                    )
                }
            }

            launch {
                boardComboZoomAnim.snapTo(1f)
                boardComboZoomAnim.animateTo(
                    targetValue = targetZoomPunch,
                    animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
                )
                boardComboZoomAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }

            launch {
                comboPulseWaveAnim.snapTo(1f)
                comboPulseWaveAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing)
                )
            }

            if (isCombo) {
                launch {
                    overlayBurstScale.snapTo(0.3f)
                    overlayBurstAlpha.snapTo(1f)
                    overlayBurstScale.animateTo(
                        targetValue = 1.10f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    overlayBurstScale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = tween(durationMillis = 200)
                    )
                    overlayBurstAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 340)
                    )
                }
            }
        } else {
            boardComboZoomAnim.snapTo(1f)
            comboPulseWaveAnim.snapTo(0f)
            cameraShakeAnim.snapTo(0f)
            overlayBurstAlpha.snapTo(0f)
        }
    }

    val activeComboColor = playerAccentColor(boardState.currentPlayer)
    val pulseGlowAlpha = comboPulseWaveAnim.value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("grid_arena_screen"),
        contentAlignment = Alignment.Center
    ) {
        DriftingNeonDotsBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 820.dp)
                .graphicsLayer {
                    translationX = cameraShakeAnim.value
                    translationY = -cameraShakeAnim.value * 0.6f
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TopGameToolbar(
                soundEnabled = soundEnabled,
                hapticsEnabled = hapticsEnabled,
                claimedBoxes = boardState.totalClaimedBoxes,
                totalBoxes = boardState.totalBoxes,
                onToggleSound = onToggleSound,
                onToggleHaptics = onToggleHaptics,
                onReturnToSetup = onReturnToCommandCenter
            )

            ScoreboardHeader(
                boardState = boardState,
                activeCampaignLevel = activeCampaignLevel,
                selectedTimerMode = selectedTimerMode,
                blitzRemainingSeconds = timerSecondsLeft,
                blitzRemainingFraction = timerProgress,
                lastTurnForfeitedByTimeout = lastTurnForfeitedByTimeout,
                neonGlowPulse = neonGlowPulse,
                comboHeartbeatScale = comboHeartbeatScale,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = boardState.isGameOver,
                enter = fadeIn() + scaleIn(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )
            ) {
                GameOverBanner(
                    boardState = boardState,
                    activeCampaignLevel = activeCampaignLevel,
                    justUnlockedNextCampaignLevel = justUnlockedNextCampaignLevel,
                    onAdvanceToNextCampaignLevel = onAdvanceToNextCampaignLevel,
                    onPlayAgain = onRestartMatch,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val boardBorderColor = if (pulseGlowAlpha > 0.02f) {
                activeComboColor.copy(alpha = (0.45f + pulseGlowAlpha * 0.55f).coerceIn(0f, 1f))
            } else {
                palette.border
            }
            val boardBorderWidth = if (pulseGlowAlpha > 0.02f) {
                (1.dp + (pulseGlowAlpha * 2.5f).dp)
            } else {
                1.dp
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .graphicsLayer {
                        scaleX = boardComboZoomAnim.value
                        scaleY = boardComboZoomAnim.value
                    }
                    .drawBehind {
                        if (pulseGlowAlpha > 0.01f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        activeComboColor.copy(alpha = 0.34f * pulseGlowAlpha),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.maxDimension * (0.55f + (1f - pulseGlowAlpha) * 0.25f)
                            )
                        }
                    }
                    .border(boardBorderWidth, boardBorderColor, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    BoardZoomToolbar(
                        gridLabel = "${boardState.boxRows}×${boardState.boxCols}",
                        boardZoom = boardZoom,
                        comboCount = boardState.currentComboCount,
                        comboAccentColor = activeComboColor,
                        comboPulseFraction = pulseGlowAlpha,
                        onAdjustZoom = onAdjustZoom,
                        onResetZoom = { onSetZoom(0.95f) }
                    )

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, _, zoomChange, _ ->
                                    if (zoomChange != 1f) {
                                        onSetZoom(boardZoom * zoomChange)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val safeStageWidth = (maxWidth * 0.92f).coerceAtLeast(180.dp)
                        val safeStageHeight = (maxHeight * 0.92f).coerceAtLeast(140.dp)

                        val dotRows = boardState.dotRows
                        val dotCols = boardState.dotCols
                        val boxRows = boardState.boxRows
                        val boxCols = boardState.boxCols

                        val maxGridDim = max(boxRows, boxCols)
                        val idealDotDiameter = 16.dp
                        val maxBoxByWidth = (safeStageWidth - (idealDotDiameter * dotCols)) / boxCols.toFloat()
                        val maxBoxByHeight = (safeStageHeight - (idealDotDiameter * dotRows)) / boxRows.toFloat()
                        val rawBoxSize = min(maxBoxByWidth, maxBoxByHeight)

                        val baseDotDiameter = if (rawBoxSize < 24.dp && maxGridDim >= 5) {
                            12.dp
                        } else {
                            idealDotDiameter
                        }
                        val recomputedBoxW = (safeStageWidth - (baseDotDiameter * dotCols)) / boxCols.toFloat()
                        val recomputedBoxH = (safeStageHeight - (baseDotDiameter * dotRows)) / boxRows.toFloat()
                        val baseBoxSize = min(recomputedBoxW, recomputedBoxH).coerceIn(20.dp, 76.dp)

                        val scaledDotSlotSize = (baseDotDiameter * boardZoom).coerceIn(10.dp, 22.dp)
                        val scaledBoxSize = (baseBoxSize * boardZoom).coerceIn(18.dp, 88.dp)

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            DotsAndBoxesGrid(
                                boardState = boardState,
                                hoveredEdge = hoveredEdge,
                                neonGlowPulse = neonGlowPulse,
                                comboPulseFraction = pulseGlowAlpha,
                                dotSlotSize = scaledDotSlotSize,
                                boxSize = scaledBoxSize,
                                onEdgeHover = onEdgeHover,
                                onEdgeClick = onHumanMoveExecuted
                            )
                        }
                    }
                }
            }

            BottomControlsAndFooter(
                activeCampaignLevel = activeCampaignLevel,
                selectedGridSize = selectedGridSize,
                selectedCpuDifficulty = selectedCpuDifficulty,
                selectedTimerMode = selectedTimerMode,
                onSelectGridSize = onSelectGridSize,
                onSelectCpuDifficulty = onSelectCpuDifficulty,
                onSelectTimerMode = onSelectTimerMode,
                onRestartMatch = onRestartMatch,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (overlayBurstAlpha.value > 0.02f && boardState.currentComboCount >= 2) {
            val isDouble = boardState.lastCapturedBoxes.size >= 2
            val mainTitle = if (isDouble) "DOUBLE NODE CAPTURE!" else "COMBO x${boardState.currentComboCount}!"
            val subTitle = if (isDouble) "COMBO x${boardState.currentComboCount} STREAK!" else "PROTOCOL OVERDRIVE!"

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = overlayBurstScale.value
                        scaleY = overlayBurstScale.value
                        alpha = overlayBurstAlpha.value
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.backgroundDeep.copy(alpha = 0.92f))
                    .border(2.5.dp, activeComboColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
                    .testTag("combo_burst_overlay"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = mainTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            shadow = Shadow(
                                color = activeComboColor,
                                offset = Offset.Zero,
                                blurRadius = 24f
                            )
                        ),
                        color = palette.primaryText,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.labelLarge.copy(
                            shadow = Shadow(
                                color = palette.streakAccent,
                                offset = Offset.Zero,
                                blurRadius = 16f
                            )
                        ),
                        color = palette.streakAccent,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (boardState.isGameOver) {
            FullScreenVictoryAnimationOverlay(
                boardState = boardState,
                triggerId = victoryAnimationTriggerId,
                activeCampaignLevel = activeCampaignLevel,
                justUnlockedNextCampaignLevel = justUnlockedNextCampaignLevel,
                onAdvanceToNextCampaignLevel = onAdvanceToNextCampaignLevel,
                onPlayAgain = onRestartMatch,
                onReturnToSetup = onReturnToCommandCenter
            )
        }
    }
}

@Composable
private fun BottomControlsAndFooter(
    activeCampaignLevel: CampaignLevel?,
    selectedGridSize: GridSize,
    selectedCpuDifficulty: CpuDifficulty,
    selectedTimerMode: TimerMode,
    onSelectGridSize: (GridSize) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectTimerMode: (TimerMode) -> Unit,
    onRestartMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current

    Card(
        modifier = modifier
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .testTag("bottom_footer_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: Grid Size Segmented Selector (3x3, 4x4, 5x5, 6x6) + Restart Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "GRID:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = palette.mutedText
                )

                GridSize.entries.forEach { sizeOption ->
                    val isSelected = selectedGridSize == sizeOption
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) palette.p1Accent else palette.surfaceElevated)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) palette.p1Accent else palette.border,
                                shape = RoundedCornerShape(9.dp)
                            )
                            .clickable { onSelectGridSize(sizeOption) }
                            .testTag("grid_size_${sizeOption.boxRows}x${sizeOption.boxCols}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sizeOption.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            ),
                            color = if (isSelected) Color.Black else palette.primaryText
                        )
                    }
                }

                Button(
                    onClick = onRestartMatch,
                    shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.p2Accent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("restart_match_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Restart Game",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Restart",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Row 2: CPU Difficulty Selector (Easy, Medium, Hard)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = if (activeCampaignLevel != null) "LVL ${activeCampaignLevel.levelNumber}:" else "AI:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = if (activeCampaignLevel != null) palette.p1Accent else palette.mutedText
                )

                CpuDifficulty.entries.forEach { diff ->
                    val isSelected = selectedCpuDifficulty == diff
                    val diffColor = when (diff) {
                        CpuDifficulty.EASY -> palette.p3Accent
                        CpuDifficulty.MEDIUM -> palette.streakAccent
                        CpuDifficulty.HARD -> palette.p2Accent
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) diffColor else palette.surfaceElevated)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) diffColor else palette.border,
                                shape = RoundedCornerShape(9.dp)
                            )
                            .clickable { onSelectCpuDifficulty(diff) }
                            .testTag("game_diff_${diff.name.lowercase()}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = diff.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) Color.Black else palette.primaryText
                        )
                    }
                }
            }

            // Row 3: Time Limit Selector (Relaxed, 3s, 5s, 10s)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "TIME:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = palette.mutedText
                )

                TimerMode.entries.forEach { mode ->
                    val isSelected = selectedTimerMode == mode
                    val modeColor = if (mode == TimerMode.RELAXED) palette.p1Accent else palette.p2Accent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) modeColor else palette.surfaceElevated)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) modeColor else palette.border,
                                shape = RoundedCornerShape(9.dp)
                            )
                            .clickable { onSelectTimerMode(mode) }
                            .testTag("game_timer_${mode.name.lowercase()}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            ),
                            color = if (isSelected) Color.Black else palette.primaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardZoomToolbar(
    gridLabel: String,
    boardZoom: Float,
    comboCount: Int,
    comboAccentColor: Color,
    comboPulseFraction: Float,
    onAdjustZoom: (Float) -> Unit,
    onResetZoom: () -> Unit
) {
    val palette = LocalGamePalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "THE GRID ARENA • $gridLabel NODES",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = palette.mutedText
            )

            AnimatedVisibility(visible = comboCount >= 2) {
                Box(
                    modifier = Modifier
                        .scale(1f + comboPulseFraction * 0.14f)
                        .clip(RoundedCornerShape(50))
                        .background(comboAccentColor.copy(alpha = 0.25f))
                        .border(1.dp, comboAccentColor, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("combo_pulse_chip")
                ) {
                    Text(
                        text = "COMBO x$comboCount!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = comboAccentColor
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(7.dp))
                    .clickable { onAdjustZoom(-0.1f) }
                    .testTag("zoom_out_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = "Zoom out board",
                    tint = palette.primaryText,
                    modifier = Modifier.size(15.dp)
                )
            }

            Box(
                modifier = Modifier
                    .height(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(7.dp))
                    .clickable { onResetZoom() }
                    .padding(horizontal = 7.dp)
                    .testTag("zoom_reset_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(boardZoom * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = palette.p1Accent
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(7.dp))
                    .clickable { onAdjustZoom(0.1f) }
                    .testTag("zoom_in_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "Zoom in board",
                    tint = palette.primaryText,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun TopGameToolbar(
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    claimedBoxes: Int,
    totalBoxes: Int,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onReturnToSetup: () -> Unit
) {
    val palette = LocalGamePalette.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onReturnToSetup,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.primaryText),
            modifier = Modifier
                .height(48.dp)
                .testTag("back_to_setup_button")
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Command Center",
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Command Center",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$claimedBoxes / $totalBoxes nodes",
                style = MaterialTheme.typography.labelSmall,
                color = palette.mutedText
            )
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { if (totalBoxes > 0) claimedBoxes.toFloat() / totalBoxes else 0f },
                modifier = Modifier
                    .width(96.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50)),
                color = palette.p1Accent,
                trackColor = palette.surfaceElevated
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleHaptics,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .testTag("game_haptics_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Vibration,
                    contentDescription = if (hapticsEnabled) "Haptics: On" else "Haptics: Off",
                    tint = if (hapticsEnabled) palette.p1Accent else palette.mutedText
                )
            }

            IconButton(
                onClick = onToggleSound,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceElevated)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .testTag("game_sound_toggle_button")
            ) {
                Icon(
                    imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                    contentDescription = "Sound Engine",
                    tint = if (soundEnabled) palette.p3Accent else palette.mutedText
                )
            }
        }
    }
}

@Composable
private fun ScoreboardHeader(
    boardState: BoardState,
    activeCampaignLevel: CampaignLevel?,
    selectedTimerMode: TimerMode,
    blitzRemainingSeconds: Float,
    blitzRemainingFraction: Float,
    lastTurnForfeitedByTimeout: Boolean,
    neonGlowPulse: Float,
    comboHeartbeatScale: Float,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current

    Card(
        modifier = modifier
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .testTag("scoreboard_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (activeCampaignLevel != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.surfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                        .testTag("active_campaign_level_banner"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = palette.streakAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${activeCampaignLevel.title} — ${activeCampaignLevel.subtitle}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.primaryText
                        )
                    }
                    Text(
                        text = "${activeCampaignLevel.gridSize.label} • ${activeCampaignLevel.difficulty.label.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp
                        ),
                        color = palette.p1Accent
                    )
                }
            }

            val players = boardState.players
            val chunkedPlayers = players.chunked(2)
            for (rowPlayers in chunkedPlayers) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (player in rowPlayers) {
                        val isTurn = !boardState.isGameOver &&
                            boardState.currentPlayer == player.playerNumber
                        val isComboStreak = isTurn && boardState.isMultiSquareCombo
                        PlayerScorePill(
                            player = player,
                            score = boardState.scoreForPlayer(player.playerNumber),
                            isCurrentTurn = isTurn,
                            isComboStreak = isComboStreak,
                            neonGlowPulse = neonGlowPulse,
                            heartbeatScale = if (isComboStreak) comboHeartbeatScale else 1f,
                            accentColor = playerAccentColor(player.playerNumber),
                            accentFill = playerFillColor(player.playerNumber),
                            testTag = "player${player.playerNumber}_score_card",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowPlayers.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            val showTimerBar = selectedTimerMode.isTimed &&
                !boardState.isGameOver &&
                !boardState.isCurrentPlayerAi

            AnimatedVisibility(visible = showTimerBar) {
                val animatedFraction by animateFloatAsState(
                    targetValue = blitzRemainingFraction.coerceIn(0f, 1f),
                    animationSpec = tween(durationMillis = 80),
                    label = "blitzProgressFraction"
                )
                val barColor = when {
                    animatedFraction > 0.5f -> palette.p1Accent
                    animatedFraction > 0.25f -> palette.streakAccent
                    else -> palette.p2Accent
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.backgroundDeep)
                        .border(1.dp, barColor.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("blitz_timer_container"),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = barColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "TIME LIMIT (${selectedTimerMode.label.uppercase()})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = barColor
                            )
                        }
                        Text(
                            text = String.format(Locale.US, "%.1fs", blitzRemainingSeconds),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.primaryText,
                            modifier = Modifier.testTag("blitz_timer_seconds_text")
                        )
                    }
                    LinearProgressIndicator(
                        progress = { animatedFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .testTag("blitz_timer_progress_bar"),
                        color = barColor,
                        trackColor = palette.surfaceElevated
                    )
                }
            }

            AnimatedVisibility(visible = lastTurnForfeitedByTimeout && !boardState.isGameOver) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.p2Accent.copy(alpha = 0.18f))
                        .border(1.dp, palette.p2Accent, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                        .testTag("blitz_timeout_banner"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time Limit (${selectedTimerMode.label}) Expired — Turn Forfeited to CPU!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = palette.p2Accent
                    )
                }
            }

            AnimatedVisibility(visible = boardState.hasExtraTurnStreak && !boardState.isGameOver) {
                val activeColor = playerAccentColor(boardState.currentPlayer)
                val activeLabel = if (boardState.isPlayerAi(boardState.currentPlayer)) "CPU" else "USER"
                val badgeText = if (boardState.isMultiSquareCombo) {
                    "${boardState.currentComboCount}× NODE CHAIN! EXTRA TURN"
                } else {
                    "NODE CAPTURED! EXTRA TURN"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(if (boardState.isMultiSquareCombo) comboHeartbeatScale else 1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(activeColor.copy(alpha = 0.24f))
                        .border(1.5.dp, activeColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("combo_streak_banner"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = null,
                        tint = palette.streakAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$badgeText — $activeLabel",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.primaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerScorePill(
    player: PlayerConfig,
    score: Int,
    isCurrentTurn: Boolean,
    isComboStreak: Boolean,
    neonGlowPulse: Float,
    heartbeatScale: Float,
    accentColor: Color,
    accentFill: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val borderColor by animateColorAsState(
        targetValue = if (isCurrentTurn) {
            accentColor.copy(alpha = (0.55f + neonGlowPulse * 0.45f).coerceIn(0f, 1f))
        } else {
            palette.border.copy(alpha = 0.45f)
        },
        label = "scoreBorderColor"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isCurrentTurn) accentFill else palette.surfaceElevated,
        label = "scoreContainerColor"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = heartbeatScale
                scaleY = heartbeatScale
            }
            .drawBehind {
                if (isCurrentTurn) {
                    val glowStrength = if (isComboStreak) 0.42f else 0.24f * neonGlowPulse
                    drawRoundRect(
                        color = accentColor.copy(alpha = glowStrength),
                        cornerRadius = CornerRadius(14.dp.toPx())
                    )
                }
            }
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(palette.backgroundDeep)
                    .border(1.5.dp, accentColor, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (player.isAi) "AI" else "P1",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    ),
                    color = accentColor
                )
            }

            Column {
                Text(
                    text = if (player.isAi) "CPU (MAGENTA)" else "USER (CYAN)",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isCurrentTurn) accentColor else palette.backgroundDeep.copy(alpha = 0.6f)
                        )
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = when {
                            isCurrentTurn && player.isAi -> "CPU THINKING…"
                            isCurrentTurn -> "YOUR TURN"
                            else -> "WAITING"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isCurrentTurn) Color.Black else palette.mutedText
                    )
                }
            }
        }

        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = palette.primaryText,
            modifier = Modifier.testTag("player${player.playerNumber}_score_value")
        )
    }
}

@Composable
private fun DotsAndBoxesGrid(
    boardState: BoardState,
    hoveredEdge: EdgeCoordinate?,
    neonGlowPulse: Float,
    comboPulseFraction: Float,
    dotSlotSize: Dp,
    boxSize: Dp,
    onEdgeHover: (EdgeCoordinate?) -> Unit,
    onEdgeClick: (EdgeCoordinate) -> Unit
) {
    val activePlayerColor = playerAccentColor(boardState.currentPlayer)

    Column(
        modifier = Modifier.testTag("dots_and_boxes_grid"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (dotRow in 0 until boardState.dotRows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (dotCol in 0 until boardState.dotCols) {
                    DotNode(slotSize = dotSlotSize)

                    if (dotCol < boardState.boxCols) {
                        val edgeCoord = EdgeCoordinate(
                            orientation = EdgeOrientation.HORIZONTAL,
                            row = dotRow,
                            col = dotCol
                        )
                        val claimedBy = boardState.horizontalEdges[dotRow][dotCol]
                        HorizontalEdgeCell(
                            edge = edgeCoord,
                            claimedBy = claimedBy,
                            isLastClaimed = boardState.lastClaimedEdge == edgeCoord,
                            isHoveredExternal = hoveredEdge == edgeCoord,
                            neonGlowPulse = neonGlowPulse,
                            activePlayerColor = activePlayerColor,
                            width = boxSize,
                            slotHeight = (dotSlotSize * 1.4f).coerceAtLeast(18.dp),
                            onHover = onEdgeHover,
                            onClick = { onEdgeClick(edgeCoord) }
                        )
                    }
                }
            }

            if (dotRow < boardState.boxRows) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (dotCol in 0 until boardState.dotCols) {
                        val vEdgeCoord = EdgeCoordinate(
                            orientation = EdgeOrientation.VERTICAL,
                            row = dotRow,
                            col = dotCol
                        )
                        val claimedBy = boardState.verticalEdges[dotRow][dotCol]
                        VerticalEdgeCell(
                            edge = vEdgeCoord,
                            claimedBy = claimedBy,
                            isLastClaimed = boardState.lastClaimedEdge == vEdgeCoord,
                            isHoveredExternal = hoveredEdge == vEdgeCoord,
                            neonGlowPulse = neonGlowPulse,
                            activePlayerColor = activePlayerColor,
                            slotWidth = dotSlotSize,
                            height = boxSize,
                            onHover = onEdgeHover,
                            onClick = { onEdgeClick(vEdgeCoord) }
                        )

                        if (dotCol < boardState.boxCols) {
                            val boxOwner = boardState.boxes[dotRow][dotCol]
                            val boxCoord = BoxCoordinate(dotRow, dotCol)
                            val isNewlyCaptured = boardState.lastCapturedBoxes.contains(boxCoord)
                            val isInComboChain = boardState.isMultiSquareCombo &&
                                boardState.comboChainBoxes.contains(boxCoord)

                            PlayableBoxCell(
                                row = dotRow,
                                col = dotCol,
                                owner = boxOwner,
                                isNewlyCaptured = isNewlyCaptured,
                                isInComboChain = isInComboChain,
                                comboPulseFraction = if (isNewlyCaptured || isInComboChain) {
                                    comboPulseFraction
                                } else {
                                    0f
                                },
                                size = boxSize
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DotNode(slotSize: Dp) {
    val palette = LocalGamePalette.current
    Box(
        modifier = Modifier.size(slotSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(slotSize)
                .shadow(elevation = 3.dp, shape = CircleShape, spotColor = palette.dotColor)
                .clip(CircleShape)
                .background(palette.dotColor)
        )
    }
}

@Composable
private fun HorizontalEdgeCell(
    edge: EdgeCoordinate,
    claimedBy: Int,
    isLastClaimed: Boolean,
    isHoveredExternal: Boolean,
    neonGlowPulse: Float,
    activePlayerColor: Color,
    width: Dp,
    slotHeight: Dp,
    onHover: (EdgeCoordinate?) -> Unit,
    onClick: () -> Unit
) {
    val palette = LocalGamePalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed) {
        if (isHovered || isPressed) onHover(edge)
    }

    val ownerColor = if (claimedBy != 0) playerAccentColor(claimedBy) else Color.Transparent
    val trackColor by animateColorAsState(
        targetValue = if (showHover) {
            activePlayerColor.copy(alpha = 0.65f)
        } else {
            palette.idleEdge
        },
        animationSpec = tween(durationMillis = 180),
        label = "hEdgeTrackColor"
    )
    val drawProgress by animateFloatAsState(
        targetValue = if (claimedBy != 0) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "hEdgeDrawProgress"
    )

    val barThickness = 6.dp

    Box(
        modifier = Modifier
            .width(width)
            .height(slotHeight)
            .hoverable(interactionSource = interactionSource)
            .then(
                if (claimedBy == 0) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = "Horizontal line row ${edge.row} column ${edge.col}"
            }
            .testTag("h_edge_${edge.row}_${edge.col}"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
                .height(barThickness)
                .clip(RoundedCornerShape(50))
                .background(trackColor),
            contentAlignment = Alignment.CenterStart
        ) {
            if (claimedBy != 0 || drawProgress > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(drawProgress.coerceIn(0f, 1f))
                        .height(barThickness)
                        .drawBehind {
                            drawRoundRect(
                                color = ownerColor.copy(alpha = 0.45f * neonGlowPulse),
                                size = size.copy(height = size.height * 2.2f),
                                topLeft = Offset(0f, -size.height * 0.6f)
                            )
                        }
                        .clip(RoundedCornerShape(50))
                        .background(ownerColor)
                        .then(
                            if (isLastClaimed) {
                                Modifier.border(1.dp, palette.primaryText, RoundedCornerShape(50))
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun VerticalEdgeCell(
    edge: EdgeCoordinate,
    claimedBy: Int,
    isLastClaimed: Boolean,
    isHoveredExternal: Boolean,
    neonGlowPulse: Float,
    activePlayerColor: Color,
    slotWidth: Dp,
    height: Dp,
    onHover: (EdgeCoordinate?) -> Unit,
    onClick: () -> Unit
) {
    val palette = LocalGamePalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed) {
        if (isHovered || isPressed) onHover(edge)
    }

    val ownerColor = if (claimedBy != 0) playerAccentColor(claimedBy) else Color.Transparent
    val trackColor by animateColorAsState(
        targetValue = if (showHover) {
            activePlayerColor.copy(alpha = 0.65f)
        } else {
            palette.idleEdge
        },
        animationSpec = tween(durationMillis = 180),
        label = "vEdgeTrackColor"
    )
    val drawProgress by animateFloatAsState(
        targetValue = if (claimedBy != 0) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "vEdgeDrawProgress"
    )

    val barThickness = 6.dp

    Box(
        modifier = Modifier
            .width(slotWidth)
            .height(height)
            .hoverable(interactionSource = interactionSource)
            .then(
                if (claimedBy == 0) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = "Vertical line row ${edge.row} column ${edge.col}"
            }
            .testTag("v_edge_${edge.row}_${edge.col}"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 1.dp)
                .width(barThickness)
                .clip(RoundedCornerShape(50))
                .background(trackColor),
            contentAlignment = Alignment.TopCenter
        ) {
            if (claimedBy != 0 || drawProgress > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(drawProgress.coerceIn(0f, 1f))
                        .width(barThickness)
                        .drawBehind {
                            drawRoundRect(
                                color = ownerColor.copy(alpha = 0.45f * neonGlowPulse),
                                size = size.copy(width = size.width * 2.2f),
                                topLeft = Offset(-size.width * 0.6f, 0f)
                            )
                        }
                        .clip(RoundedCornerShape(50))
                        .background(ownerColor)
                        .then(
                            if (isLastClaimed) {
                                Modifier.border(1.dp, palette.primaryText, RoundedCornerShape(50))
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun PlayableBoxCell(
    row: Int,
    col: Int,
    owner: Int,
    isNewlyCaptured: Boolean,
    isInComboChain: Boolean,
    comboPulseFraction: Float,
    size: Dp
) {
    val palette = LocalGamePalette.current
    val accentColor = if (owner != 0) playerAccentColor(owner) else Color.Transparent
    val boxFillColor by animateColorAsState(
        targetValue = if (owner != 0) accentColor.copy(alpha = 0.40f) else Color.Transparent,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "boxFillColorAnim"
    )

    val singleCapturePulse = remember { Animatable(1f) }
    val charJumpScale = remember { Animatable(0.2f) }
    LaunchedEffect(owner) {
        if (owner != 0) {
            launch {
                singleCapturePulse.snapTo(0.85f)
                singleCapturePulse.animateTo(
                    targetValue = 1.16f,
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                )
                singleCapturePulse.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            charJumpScale.snapTo(0.25f)
            charJumpScale.animateTo(
                targetValue = 1.75f,
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            )
            charJumpScale.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            singleCapturePulse.snapTo(1f)
            charJumpScale.snapTo(0.2f)
        }
    }

    val comboBoxScale = if (isNewlyCaptured || isInComboChain) {
        singleCapturePulse.value * (1f + (comboPulseFraction * 0.12f))
    } else {
        singleCapturePulse.value
    }

    Box(
        modifier = Modifier
            .size(size)
            .padding(1.5.dp)
            .graphicsLayer {
                scaleX = comboBoxScale
                scaleY = comboBoxScale
            }
            .clip(RoundedCornerShape(6.dp))
            .background(boxFillColor)
            .then(
                if ((isNewlyCaptured || isInComboChain) && owner != 0) {
                    Modifier.border(
                        width = if (isInComboChain) 2.dp else 1.5.dp,
                        color = if (comboPulseFraction > 0.2f) palette.primaryText else accentColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                } else {
                    Modifier
                }
            )
            .testTag("box_${row}_${col}"),
        contentAlignment = Alignment.Center
    ) {
        if (isNewlyCaptured && comboPulseFraction > 0.02f && owner != 0) {
            val particleProgress = 1f - comboPulseFraction
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val maxRadius = this.size.maxDimension * 0.85f
                val particleCount = 12
                for (i in 0 until particleCount) {
                    val angle = (2.0 * PI * i) / particleCount
                    val distance = maxRadius * particleProgress
                    val px = center.x + (cos(angle) * distance).toFloat()
                    val py = center.y + (sin(angle) * distance).toFloat()
                    drawCircle(
                        color = accentColor.copy(alpha = comboPulseFraction),
                        radius = (3.5.dp.toPx() * comboPulseFraction).coerceAtLeast(1f),
                        center = Offset(px, py)
                    )
                }
            }
        }

        if (owner != 0) {
            Text(
                text = if (owner == 1) "P1" else "CPU",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = when {
                        size >= 44.dp -> 15.sp
                        size >= 28.dp -> 12.sp
                        else -> 9.sp
                    },
                    shadow = Shadow(
                        color = accentColor,
                        offset = Offset.Zero,
                        blurRadius = 14f
                    )
                ),
                color = palette.primaryText,
                modifier = Modifier.scale(charJumpScale.value)
            )
        }
    }
}

@Composable
private fun GameOverBanner(
    boardState: BoardState,
    activeCampaignLevel: CampaignLevel?,
    justUnlockedNextCampaignLevel: Boolean,
    onAdvanceToNextCampaignLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val winner = boardState.winner
    val accentColor = if (winner != null && winner > 0) {
        playerAccentColor(winner)
    } else {
        palette.streakAccent
    }

    val titleText = if (winner != null && winner > 0) {
        val winnerName = if (boardState.isPlayerAi(winner)) "CPU" else "User"
        "$winnerName Wins with ${boardState.scoreForPlayer(winner)} Nodes!"
    } else {
        "Match Drawn — Tied at ${boardState.maxScore}!"
    }

    val summaryScores = "User: ${boardState.player1Score}  •  CPU: ${boardState.player2Score}"

    val nextLevel = activeCampaignLevel?.nextLevel()
    val canAdvanceCampaign = activeCampaignLevel != null && winner == 1 && nextLevel != null

    Card(
        modifier = modifier
            .border(2.dp, accentColor, RoundedCornerShape(16.dp))
            .testTag("game_over_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.primaryText
                        )
                        Text(
                            text = summaryScores,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor
                        )
                        if (justUnlockedNextCampaignLevel && nextLevel != null) {
                            Text(
                                text = "NEW STAGE UNLOCKED: LEVEL ${nextLevel.levelNumber}!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = palette.streakAccent,
                                modifier = Modifier.testTag("campaign_unlocked_notice")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canAdvanceCampaign) {
                        Button(
                            onClick = onAdvanceToNextCampaignLevel,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.streakAccent,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("next_campaign_level_button")
                        ) {
                            Text(
                                text = "Next Level",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Button(
                        onClick = onPlayAgain,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("play_again_button")
                    ) {
                        Text(
                            text = "Play Again",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

private data class ConfettiParticleSpec(
    val angleRad: Float,
    val speedFactor: Float,
    val startXFraction: Float,
    val fallSpeed: Float,
    val driftAmplitude: Float,
    val rotationSpeed: Float,
    val sizeDp: Float,
    val colorIndex: Int
)

@Composable
private fun FullScreenVictoryAnimationOverlay(
    boardState: BoardState,
    triggerId: Int,
    activeCampaignLevel: CampaignLevel?,
    justUnlockedNextCampaignLevel: Boolean,
    onAdvanceToNextCampaignLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onReturnToSetup: () -> Unit
) {
    val palette = LocalGamePalette.current
    val winner = boardState.winner
    val winnerAccent = if (winner != null && winner > 0) {
        playerAccentColor(winner)
    } else {
        palette.streakAccent
    }

    var isCardDismissed by remember(triggerId, boardState.isGameOver) { mutableStateOf(false) }

    val burstProgress = remember(triggerId) { Animatable(0f) }
    val glowPulseTransition = rememberInfiniteTransition(label = "victoryGlowTransition")
    val glowPulse by glowPulseTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "victoryGlowPulse"
    )
    val continuousFallPhase by glowPulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "victoryContinuousFall"
    )

    LaunchedEffect(triggerId, boardState.isGameOver) {
        if (boardState.isGameOver) {
            burstProgress.snapTo(0f)
            burstProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
            )
        }
    }

    val particles = remember {
        List(76) { idx ->
            val goldenAngle = idx * 2.3999632f
            ConfettiParticleSpec(
                angleRad = goldenAngle,
                speedFactor = 0.25f + ((idx * 37) % 100) / 100f * 0.85f,
                startXFraction = ((idx * 19) % 100) / 100f,
                fallSpeed = 0.4f + ((idx * 29) % 70) / 100f,
                driftAmplitude = 18f + (idx % 5) * 9f,
                rotationSpeed = (idx % 7 - 3) * 120f,
                sizeDp = 5f + (idx % 4) * 2.5f,
                colorIndex = idx % 5
            )
        }
    }

    val confettiColors = listOf(
        palette.p1Accent,
        palette.p2Accent,
        palette.streakAccent,
        palette.p3Accent,
        palette.p4Accent
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("victory_confetti_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("victory_confetti_canvas")
        ) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val maxDim = size.maxDimension

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        winnerAccent.copy(alpha = 0.28f * glowPulse),
                        palette.streakAccent.copy(alpha = 0.12f * glowPulse),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxDim * (0.48f + 0.14f * glowPulse)
                ),
                center = center,
                radius = maxDim * 0.65f
            )

            val ringProgress = burstProgress.value
            if (ringProgress < 0.98f) {
                drawCircle(
                    color = winnerAccent.copy(alpha = (1f - ringProgress) * 0.55f),
                    center = center,
                    radius = maxDim * 0.55f * ringProgress,
                    style = Stroke(width = 4.dp.toPx())
                )
            }

            val t = burstProgress.value
            for (p in particles) {
                val cColor = confettiColors[p.colorIndex]
                val burstRadius = maxDim * 0.52f * p.speedFactor * sin(t * (PI / 2.0)).toFloat()
                val gravityY = (t * t) * size.height * 0.55f * p.fallSpeed +
                    continuousFallPhase * size.height * 0.22f
                val swayX = sin((t * 6f + p.startXFraction * 10f).toDouble()).toFloat() * p.driftAmplitude

                val px = (center.x + cos(p.angleRad) * burstRadius + swayX)
                    .coerceIn(0f, size.width)
                val py = ((center.y + sin(p.angleRad) * burstRadius * 0.7f + gravityY) % size.height)
                    .let { if (it < 0f) it + size.height else it }

                val particleAlpha = (0.92f - t * 0.25f).coerceIn(0.35f, 0.95f)
                val rectW = p.sizeDp.dp.toPx()
                val rectH = (p.sizeDp * 0.58f).dp.toPx()

                rotate(
                    degrees = (t * p.rotationSpeed + continuousFallPhase * 180f) % 360f,
                    pivot = Offset(px, py)
                ) {
                    drawRect(
                        color = cColor.copy(alpha = particleAlpha),
                        topLeft = Offset(px - rectW / 2f, py - rectH / 2f),
                        size = Size(rectW, rectH)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !isCardDismissed,
            enter = fadeIn(tween(240)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        ) {
            val headlineText = when {
                winner == null || winner == 0 -> "Match Drawn — Tied at ${boardState.maxScore}!"
                boardState.isPlayerAi(winner) -> "CPU WINS (${boardState.scoreForPlayer(winner)} NODES)"
                else -> "USER VICTORY! (${boardState.scoreForPlayer(winner)} NODES)"
            }
            val nextLevel = activeCampaignLevel?.nextLevel()
            val canAdvanceCampaign = activeCampaignLevel != null && winner == 1 && nextLevel != null

            Card(
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .padding(horizontal = 20.dp)
                    .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = winnerAccent)
                    .border(2.5.dp, winnerAccent, RoundedCornerShape(24.dp))
                    .testTag("victory_celebration_modal"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = palette.backgroundDeep.copy(alpha = 0.94f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .scale(0.95f + glowPulse * 0.12f)
                            .clip(CircleShape)
                            .background(winnerAccent.copy(alpha = 0.22f))
                            .border(2.dp, winnerAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = winnerAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "PROTOCOL COMPLETE • WINNER DECLARED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.streakAccent,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = headlineText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            shadow = Shadow(
                                color = winnerAccent,
                                offset = Offset.Zero,
                                blurRadius = 18f
                            )
                        ),
                        color = palette.primaryText,
                        textAlign = TextAlign.Center
                    )

                    val scoreBreakdown = "User (Cyan): ${boardState.player1Score}   •   CPU (Magenta): ${boardState.player2Score}"
                    Text(
                        text = scoreBreakdown,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = winnerAccent,
                        textAlign = TextAlign.Center
                    )

                    if (activeCampaignLevel != null) {
                        val campaignStatusText = when {
                            winner == 1 && nextLevel == null -> "CHAMPION! ALL 6 CAMPAIGN STAGES CLEARED!"
                            justUnlockedNextCampaignLevel && nextLevel != null ->
                                "NEW STAGE UNLOCKED: LEVEL ${nextLevel.levelNumber}!"
                            winner == 1 && nextLevel != null -> "Level ${activeCampaignLevel.levelNumber} Cleared!"
                            else -> "Beat the CPU on ${activeCampaignLevel.title} to unlock the next stage!"
                        }
                        Text(
                            text = campaignStatusText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.streakAccent,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (canAdvanceCampaign) {
                            Button(
                                onClick = onAdvanceToNextCampaignLevel,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("victory_next_level_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = palette.streakAccent,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = "Next Level",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = onPlayAgain,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("victory_play_again_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = winnerAccent,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Play Again",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onReturnToSetup,
                            modifier = Modifier.testTag("victory_main_menu_button")
                        ) {
                            Text(
                                text = "Command Center",
                                color = palette.mutedText
                            )
                        }
                        TextButton(
                            onClick = { isCardDismissed = true },
                            modifier = Modifier.testTag("dismiss_victory_overlay_button")
                        ) {
                            Text(
                                text = "Inspect Grid",
                                color = palette.primaryText
                            )
                        }
                    }
                }
            }
        }
    }
}
