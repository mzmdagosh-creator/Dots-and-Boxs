package com.example

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.PathEffect
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

val ObsidianBackground = Color(0xFF121212)
val SlateSurface = Color(0xFF1A1E29)
val SlateSurfaceElevated = Color(0xFF222838)
val SlateBorder = Color(0xFF2E3A52)
val DotWhite = Color(0xFFFFFFFF)
val MutedText = Color(0xFF94A3B8)

val Player1Cyan = Color(0xFF00E5FF)
val Player1CyanFill = Color(0x6600E5FF)
val Player2Coral = Color(0xFFFF007F)
val Player2CoralFill = Color(0x66FF007F)
val Player3Emerald = Color(0xFF00E676)
val Player3EmeraldFill = Color(0x6600E676)
val Player4Violet = Color(0xFFA855F7)
val Player4VioletFill = Color(0x66A855F7)
val StreakAmber = Color(0xFFFACC15)
val UnclaimedEdgeIdle = Color(0x339E9E9E)

val SpaceGroteskFontFamily = FontFamily.SansSerif
val JetBrainsMonoFontFamily = FontFamily.Monospace

enum class ThemeColor(val label: String, val primary: Color) {
    CYBER_CYAN("Cyber Cyan", Color(0xFF00E5FF)),
    PLASMA_PURPLE("Plasma Purple", Color(0xFFA855F7)),
    TOXIC_GREEN("Toxic Green", Color(0xFF00E676)),
    SOLAR_FLARE("Solar Flare", Color(0xFFFFEA00));

    val fill: Color get() = primary.copy(alpha = 0.4f)

    companion object {
        fun fromName(name: String?): ThemeColor =
            entries.firstOrNull { it.name == name } ?: CYBER_CYAN
    }
}

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
    selectedTheme: ThemeColor = ThemeColor.CYBER_CYAN,
    content: @Composable () -> Unit
) {
    val dynamicPalette = remember(selectedTheme) {
        DarkProtocolPalette.copy(
            p1Accent = selectedTheme.primary,
            p1Fill = selectedTheme.fill
        )
    }
    val dynamicColorScheme = remember(selectedTheme) {
        DarkProtocolColorScheme.copy(
            primary = selectedTheme.primary
        )
    }
    CompositionLocalProvider(LocalGamePalette provides dynamicPalette) {
        MaterialTheme(
            colorScheme = dynamicColorScheme,
            typography = ProtocolTypography,
            content = content
        )
    }
}

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
        runCatching { hapticFeedback?.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
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

    fun triggerBoxCaptureBuzz(context: Context?, hapticFeedback: HapticFeedback? = null, capturedCount: Int = 1) {
        if (!isHapticsEnabled) return
        runCatching { hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress) }
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
        runCatching { hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress) }
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

    @Volatile
    var masterVolume: Float = 1.0f

    fun playLineClick() {
        if (!isSoundEnabled || masterVolume <= 0f) return
        val currentVol = masterVolume
        audioScope.launch {
            runCatching {
                val samples = synthesizeFrequencySweep(540.0, 170.0, 38, 0.28, 45.0, currentVol)
                playPcmBuffer(samples)
            }
        }
    }

    fun playBoxPop(comboCount: Int = 1) {
        if (!isSoundEnabled || masterVolume <= 0f) return
        val currentVol = masterVolume
        audioScope.launch {
            runCatching {
                val pitchMultiplier = (1.0 + (comboCount.coerceAtLeast(1) - 1) * 0.12).coerceAtMost(1.85)
                val samples = synthesizeFrequencySweep(310.0 * pitchMultiplier, 660.0 * pitchMultiplier, 125, 0.42, 18.0, currentVol)
                playPcmBuffer(samples)
            }
        }
    }

    fun playGameEndFanfare() {
        if (!isSoundEnabled || masterVolume <= 0f) return
        val currentVol = masterVolume
        audioScope.launch {
            runCatching {
                val notes = listOf(523.25 to 110, 659.25 to 110, 783.99 to 110, 1046.50 to 290)
                val totalSamples = notes.sumOf { (_, ms) -> (SAMPLE_RATE * ms) / 1000 }
                val combined = ShortArray(totalSamples)
                var offset = 0
                for ((freq, durationMs) in notes) {
                    val noteSamples = synthesizeFrequencySweep(freq, freq * 1.01, durationMs, 0.38, 8.5, currentVol)
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
        decayRate: Double,
        volume: Float = masterVolume
    ): ShortArray {
        val numSamples = ((SAMPLE_RATE * durationMs) / 1000).coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        val gain = volume.coerceIn(0f, 1f).toDouble()
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val instantFreq = startFreqHz + (endFreqHz - startFreqHz) * progress
            phase += (2.0 * PI * instantFreq) / SAMPLE_RATE
            val attack = (i / (SAMPLE_RATE * 0.004)).coerceAtMost(1.0)
            val envelope = attack * exp(-decayRate * progress)
            val sampleValue = (sin(phase) * (amplitude * gain) * envelope * Short.MAX_VALUE).toInt()
            buffer[i] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun playPcmBuffer(samples: ShortArray) {
        if (samples.isEmpty() || masterVolume <= 0f) return
        val minBufSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(samples.size * 2)
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
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(minBufSize)
            .build()
        try {
            track.play()
            track.write(samples, 0, samples.size)
        } finally {
            runCatching { track.stop() }
            runCatching { track.release() }
        }
    }
}

enum class EdgeOrientation { HORIZONTAL, VERTICAL }
enum class GameMode { PVP, CPU }
enum class CpuDifficulty(val label: String) { EASY("Easy"), MEDIUM("Medium"), HARD("Hard") }

enum class GridSize(val boxRows: Int, val boxCols: Int, val label: String) {
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

enum class TimerMode(val durationSeconds: Int, val label: String) {
    RELAXED(0, "Relaxed"),
    BULLET(3, "3s"),
    BLITZ(5, "5s"),
    RAPID(10, "10s");

    val isTimed: Boolean get() = durationSeconds > 0
}

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

    fun nextLevel(): CampaignLevel? = entries.firstOrNull { it.levelNumber == levelNumber + 1 }

    companion object {
        fun fromNumber(levelNumber: Int): CampaignLevel = entries.firstOrNull { it.levelNumber == levelNumber } ?: LEVEL_1
    }
}

enum class RankedDivision(
    val title: String,
    val minRp: Int,
    val maxRp: Int?,
    val difficulty: CpuDifficulty,
    val timerMode: TimerMode,
    val gridSize: GridSize,
    val color: Color
) {
    BRONZE("Bronze Division", 0, 99, CpuDifficulty.EASY, TimerMode.RELAXED, GridSize.SIZE_3X3, Color(0xFFCD7F32)),
    SILVER("Silver Division", 100, 199, CpuDifficulty.MEDIUM, TimerMode.RELAXED, GridSize.SIZE_4X4, Color(0xFFC0C0C0)),
    GOLD("Gold Division", 200, 299, CpuDifficulty.HARD, TimerMode.RELAXED, GridSize.SIZE_5X5, Color(0xFFFFEA00)),
    PLATINUM("Platinum Division", 300, 399, CpuDifficulty.HARD, TimerMode.RAPID, GridSize.SIZE_5X5, Color(0xFF00E676)),
    NEON("Neon Division", 400, null, CpuDifficulty.HARD, TimerMode.BLITZ, GridSize.SIZE_6X6, Color(0xFF00E5FF));

    fun nextDivision(): RankedDivision? = entries.getOrNull(ordinal + 1)

    fun progressToNext(rp: Int): Float {
        val clamped = rp.coerceAtLeast(0)
        return when (this) {
            BRONZE -> (clamped / 100f).coerceIn(0f, 1f)
            SILVER -> ((clamped - 100) / 100f).coerceIn(0f, 1f)
            GOLD -> ((clamped - 200) / 100f).coerceIn(0f, 1f)
            PLATINUM -> ((clamped - 300) / 100f).coerceIn(0f, 1f)
            NEON -> 1.0f
        }
    }

    companion object {
        fun fromRp(rp: Int): RankedDivision = when {
            rp >= 400 -> NEON
            rp >= 300 -> PLATINUM
            rp >= 200 -> GOLD
            rp >= 100 -> SILVER
            else -> BRONZE
        }
    }
}

enum class MutationMode(val label: String, val subtitle: String) {
    STANDARD("Standard", "Classic Pure Grid — No Mutations"),
    MAZE_ONLY("Maze", "Blocked Maze Edges (5–15%)"),
    GOLD_ONLY("Gold", "Golden Nodes (+2 Points)"),
    TOXIC_ONLY("Toxic", "Toxic Traps (-1 Pt & Breaks Combo)"),
    CHAOS("Chaos", "Mixed — Maze, Golden & Toxic Nodes");

    val hasMaze: Boolean get() = this == MAZE_ONLY || this == CHAOS
    val hasGold: Boolean get() = this == GOLD_ONLY || this == CHAOS
    val hasToxic: Boolean get() = this == TOXIC_ONLY || this == CHAOS
}

enum class AchievementProtocol(val id: String, val title: String, val description: String, val isSecret: Boolean = false) {
    INITIATION("ach_initiation", "Initiation", "Win your first Ranked Circuit or Campaign match"),
    FIRST_BLOOD("ach_first_blood", "First Blood", "Capture the very first node in a match."),
    TURING_TEST("ach_turing_test", "Turing Test Passed", "Defeat the Hard AI (Minimax) in a match"),
    CHAIN_REACTION("ach_chain_reaction", "Chain Reaction", "Capture 4 or more nodes in a single unbroken turn"),
    COMEBACK_KID("ach_comeback_kid", "Comeback Kid", "Win a match after trailing by 5 or more nodes."),
    GHOST_IN_MACHINE("ach_ghost_machine", "Ghost in the Machine", "Shut out the CPU (Win a match where the AI scores 0)"),
    GOLD_RUSH("ach_gold_rush", "Gold Rush", "Capture a Golden Node."),
    MAZE_RUNNER("ach_maze_runner", "Maze Runner", "Win a mutated maze match on Hard difficulty."),
    GRID_MASTER("ach_grid_master", "Grid Master", "Play a total of 50 matches"),
    SYSTEM_FREEZE("ach_system_freeze", "System Freeze", "Win a Blitz match without letting the timer expire", true),
    PACIFIST("ach_pacifist", "Calculated Pacifist", "Win a match without ever triggering a combo higher than x2.", true),
    SILVER_CIRCUIT("ach_silver_circuit", "Silver Circuit", "Reach Silver Division (100+ RP) in Ranked Circuit"),
    GOLD_STANDARD("ach_gold_standard", "Gold Standard", "Reach Gold Division (200+ RP) in Ranked Circuit"),
    PLATINUM_OVERCLOCK("ach_platinum_overclock", "Platinum Overclock", "Reach Platinum Division (300+ RP) in Ranked Circuit"),
    NEON_APEX("ach_neon_apex", "Neon Apex", "Reach Neon Division (400+ RP) in Ranked Circuit", true),
    NEON_GOD("ach_neon_god", "Neon God", "Reach 1,000 Rank Points in the Neon League.", true),
    MAX_GRID_CONQUEROR("ach_max_grid", "6×6 Conqueror", "Win a match on the 6×6 Grid"),
    CHAMELEON_PROTOCOL("ach_chameleon", "Chameleon Protocol", "Customize your Neon Theme in System Settings")
}

data class EdgeCoordinate(val orientation: EdgeOrientation, val row: Int, val col: Int)
data class BoxCoordinate(val row: Int, val col: Int)
data class PlayerConfig(val playerNumber: Int, val letter: String, val isAi: Boolean = false)
data class MoveOutcome(val newState: BoardState, val capturedBoxes: List<BoxCoordinate>, val grantedExtraTurn: Boolean)

data class BoardState(
    val boxRows: Int = DEFAULT_BOX_ROWS,
    val boxCols: Int = DEFAULT_BOX_COLS,
    val gameMode: GameMode = GameMode.CPU,
    val cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    val humanPlayerCount: Int = 1,
    val players: List<PlayerConfig> = listOf(PlayerConfig(1, "P1", isAi = false), PlayerConfig(2, "CPU", isAi = true)),
    val currentPlayer: Int = 1,
    val scores: Map<Int, Int> = mapOf(1 to 0, 2 to 0),
    val horizontalEdges: List<List<Int>> = List(boxRows + 1) { List(boxCols) { 0 } },
    val verticalEdges: List<List<Int>> = List(boxRows) { List(boxCols + 1) { 0 } },
    val boxes: List<List<Int>> = List(boxRows) { List(boxCols) { 0 } },
    val mutationMode: MutationMode = MutationMode.STANDARD,
    val mutationsEnabled: Boolean = mutationMode != MutationMode.STANDARD,
    val blockedEdges: Set<EdgeCoordinate> = emptySet(),
    val goldenNodes: Set<BoxCoordinate> = emptySet(),
    val toxicNodes: Set<BoxCoordinate> = emptySet(),
    val lastClaimedEdge: EdgeCoordinate? = null,
    val lastCapturedBoxes: Set<BoxCoordinate> = emptySet(),
    val comboChainBoxes: Set<BoxCoordinate> = emptySet(),
    val currentComboCount: Int = 0,
    val comboTriggerId: Int = 0,
    val hasExtraTurnStreak: Boolean = false,
    val totalMovesPlayed: Int = 0,
    val matchStartTimeMs: Long = System.currentTimeMillis(),
    val longestComboChain: Int = 0,
    val firstBoxCapturer: Int? = null,
    val maxPlayer1Deficit: Int = 0,
    val maxPlayer1Combo: Int = 0,
    val player1CapturedGoldenNode: Boolean = false
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
    val totalClaimedBoxes: Int get() = boxes.sumOf { row -> row.count { it != 0 } }
    val isGameOver: Boolean get() = totalClaimedBoxes == totalBoxes
    val isMultiSquareCombo: Boolean get() = currentComboCount >= 2
    val isCurrentPlayerAi: Boolean get() = !isGameOver && players.firstOrNull { it.playerNumber == currentPlayer }?.isAi == true

    val winner: Int?
        get() {
            if (!isGameOver) return null
            val maxScore = scores.values.maxOrNull() ?: 0
            val topPlayers = scores.filterValues { it == maxScore }.keys
            return if (topPlayers.size == 1) topPlayers.first() else 0
        }

    val maxScore: Int get() = scores.values.maxOrNull() ?: 0

    fun letterForPlayer(player: Int): String = players.firstOrNull { it.playerNumber == player }?.letter ?: player.toString()
    fun scoreForPlayer(player: Int): Int = scores[player] ?: 0
    fun isPlayerAi(player: Int): Boolean = players.firstOrNull { it.playerNumber == player }?.isAi == true

    companion object {
        const val DEFAULT_BOX_ROWS = 4
        const val DEFAULT_BOX_COLS = 4

        fun create(
            humanPlayerCount: Int = 1,
            letters: List<String> = listOf("A", "C"),
            cpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
            boxRows: Int = DEFAULT_BOX_ROWS,
            boxCols: Int = DEFAULT_BOX_COLS,
            mutationMode: MutationMode = MutationMode.STANDARD,
            mutationsEnabled: Boolean = mutationMode != MutationMode.STANDARD
        ): BoardState {
            val safeRows = boxRows.coerceIn(2, 10)
            val safeCols = boxCols.coerceIn(2, 10)
            val coercedCount = humanPlayerCount.coerceIn(1, 4)
            val mode = if (coercedCount == 1) GameMode.CPU else GameMode.PVP
            val playerConfigs = if (coercedCount == 1) {
                val p1Letter = letters.getOrNull(0)?.ifBlank { "A" } ?: "A"
                val cpuLetter = if (p1Letter.equals("C", ignoreCase = true)) "🤖" else "C"
                listOf(PlayerConfig(1, p1Letter, isAi = false), PlayerConfig(2, cpuLetter, isAi = true))
            } else {
                (1..coercedCount).map { idx ->
                    val fallback = ('A' + (idx - 1)).toString()
                    PlayerConfig(playerNumber = idx, letter = letters.getOrNull(idx - 1)?.ifBlank { fallback } ?: fallback, isAi = false)
                }
            }

            val effectiveMutationMode = if (mutationMode != MutationMode.STANDARD) {
                mutationMode
            } else if (mutationsEnabled) {
                MutationMode.CHAOS
            } else {
                MutationMode.STANDARD
            }

            val generatedBlockedEdges = mutableSetOf<EdgeCoordinate>()
            val generatedGoldenNodes = mutableSetOf<BoxCoordinate>()
            val generatedToxicNodes = mutableSetOf<BoxCoordinate>()

            if (effectiveMutationMode.hasMaze) {
                val internalEdges = mutableListOf<EdgeCoordinate>()
                for (r in 1 until safeRows) {
                    for (c in 0 until safeCols) {
                        internalEdges.add(EdgeCoordinate(EdgeOrientation.HORIZONTAL, r, c))
                    }
                }
                for (r in 0 until safeRows) {
                    for (c in 1 until safeCols) {
                        internalEdges.add(EdgeCoordinate(EdgeOrientation.VERTICAL, r, c))
                    }
                }

                if (internalEdges.isNotEmpty()) {
                    val fraction = Random.nextDouble(0.05, 0.15)
                    val targetBlockedCount = (internalEdges.size * fraction).toInt().coerceAtLeast(1)
                    val blockedCountPerBox = Array(safeRows) { IntArray(safeCols) }

                    for (candidate in internalEdges.shuffled()) {
                        if (generatedBlockedEdges.size >= targetBlockedCount) break
                        val adjBoxes = mutableListOf<BoxCoordinate>()
                        when (candidate.orientation) {
                            EdgeOrientation.HORIZONTAL -> {
                                if (candidate.row > 0) adjBoxes.add(BoxCoordinate(candidate.row - 1, candidate.col))
                                if (candidate.row < safeRows) adjBoxes.add(BoxCoordinate(candidate.row, candidate.col))
                            }
                            EdgeOrientation.VERTICAL -> {
                                if (candidate.col > 0) adjBoxes.add(BoxCoordinate(candidate.row, candidate.col - 1))
                                if (candidate.col < safeCols) adjBoxes.add(BoxCoordinate(candidate.row, candidate.col))
                            }
                        }
                        if (adjBoxes.all { blockedCountPerBox[it.row][it.col] < 2 }) {
                            generatedBlockedEdges.add(candidate)
                            for (box in adjBoxes) {
                                blockedCountPerBox[box.row][box.col]++
                            }
                        }
                    }
                }
            }

            val allBoxes = mutableListOf<BoxCoordinate>()
            for (r in 0 until safeRows) {
                for (c in 0 until safeCols) {
                    allBoxes.add(BoxCoordinate(r, c))
                }
            }

            if (effectiveMutationMode.hasGold) {
                val goldenCount = Random.nextInt(1, 4).coerceAtMost(allBoxes.size)
                generatedGoldenNodes.addAll(allBoxes.shuffled().take(goldenCount))
            }

            if (effectiveMutationMode.hasToxic) {
                val remainingBoxes = allBoxes.filterNot { generatedGoldenNodes.contains(it) }
                if (remainingBoxes.isNotEmpty()) {
                    val toxicCount = Random.nextInt(1, 4).coerceAtMost(remainingBoxes.size)
                    generatedToxicNodes.addAll(remainingBoxes.shuffled().take(toxicCount))
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
                boxes = List(safeRows) { List(safeCols) { 0 } },
                mutationMode = effectiveMutationMode,
                mutationsEnabled = effectiveMutationMode != MutationMode.STANDARD,
                blockedEdges = generatedBlockedEdges,
                goldenNodes = generatedGoldenNodes,
                toxicNodes = generatedToxicNodes,
                matchStartTimeMs = System.currentTimeMillis(),
                longestComboChain = 0
            )
        }
    }
}

object DotsAndBoxesEngine {
    fun minimaxSearchDepth(boxRows: Int, boxCols: Int): Int = if (boxRows <= 4 && boxCols <= 4) 3 else 2

    fun isEdgeAvailable(state: BoardState, edge: EdgeCoordinate): Boolean {
        if (state.blockedEdges.contains(edge)) return false
        return when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> edge.row in 0 until state.dotRows && edge.col in 0 until state.boxCols && state.horizontalEdges[edge.row][edge.col] == 0
            EdgeOrientation.VERTICAL -> edge.row in 0 until state.boxRows && edge.col in 0 until state.dotCols && state.verticalEdges[edge.row][edge.col] == 0
        }
    }

    fun forfeitTurn(state: BoardState): BoardState {
        if (state.isGameOver) return state
        val nextPlayer = (state.currentPlayer % state.activePlayerCount) + 1
        return state.copy(currentPlayer = nextPlayer, currentComboCount = 0, comboChainBoxes = emptySet(), lastCapturedBoxes = emptySet(), hasExtraTurnStreak = false)
    }

    fun makeMove(state: BoardState, edge: EdgeCoordinate): MoveOutcome? {
        if (state.isGameOver || state.blockedEdges.contains(edge)) return null
        val hMutable = state.horizontalEdges.map { it.toMutableList() }.toMutableList()
        val vMutable = state.verticalEdges.map { it.toMutableList() }.toMutableList()
        val boxesMutable = state.boxes.map { it.toMutableList() }.toMutableList()
        val candidateBoxes = mutableListOf<BoxCoordinate>()

        when (edge.orientation) {
            EdgeOrientation.HORIZONTAL -> {
                if (edge.row !in 0 until state.dotRows || edge.col !in 0 until state.boxCols) return null
                if (hMutable[edge.row][edge.col] != 0) return null
                hMutable[edge.row][edge.col] = state.currentPlayer
                if (edge.row > 0) candidateBoxes.add(BoxCoordinate(edge.row - 1, edge.col))
                if (edge.row < state.boxRows) candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
            }
            EdgeOrientation.VERTICAL -> {
                if (edge.row !in 0 until state.boxRows || edge.col !in 0 until state.dotCols) return null
                if (vMutable[edge.row][edge.col] != 0) return null
                vMutable[edge.row][edge.col] = state.currentPlayer
                if (edge.col > 0) candidateBoxes.add(BoxCoordinate(edge.row, edge.col - 1))
                if (edge.col < state.boxCols) candidateBoxes.add(BoxCoordinate(edge.row, edge.col))
            }
        }

        val newlyCaptured = mutableListOf<BoxCoordinate>()
        for (box in candidateBoxes) {
            if (boxesMutable[box.row][box.col] == 0 && isBoxCompleted(box.row, box.col, hMutable, vMutable, state.blockedEdges)) {
                boxesMutable[box.row][box.col] = state.currentPlayer
                newlyCaptured.add(box)
            }
        }

        val capturedCount = newlyCaptured.size
        val capturedToxic = newlyCaptured.any { state.toxicNodes.contains(it) }
        val capturedGolden = newlyCaptured.any { state.goldenNodes.contains(it) }
        val pointsEarned = newlyCaptured.sumOf { box ->
            when {
                state.goldenNodes.contains(box) -> 2
                state.toxicNodes.contains(box) -> -1
                else -> 1
            }
        }
        val updatedScores = state.scores.toMutableMap()
        updatedScores[state.currentPlayer] = (updatedScores[state.currentPlayer] ?: 0) + pointsEarned
        val grantedExtraTurn = capturedCount > 0 && !capturedToxic
        val nextPlayer = if (grantedExtraTurn) state.currentPlayer else (state.currentPlayer % state.activePlayerCount) + 1
        val newComboCount = if (grantedExtraTurn) state.currentComboCount + capturedCount else 0
        val newLongestCombo = max(state.longestComboChain, newComboCount)
        val newComboChain = if (grantedExtraTurn) state.comboChainBoxes + newlyCaptured else emptySet()
        val newComboTriggerId = if (capturedCount > 0) state.comboTriggerId + 1 else state.comboTriggerId

        val newFirstCapturer = state.firstBoxCapturer ?: if (capturedCount > 0) state.currentPlayer else null
        val p1Score = updatedScores[1] ?: 0
        val maxOpponentScore = updatedScores.filterKeys { it != 1 }.values.maxOrNull() ?: 0
        val currentP1Deficit = (maxOpponentScore - p1Score).coerceAtLeast(0)
        val newMaxP1Deficit = max(state.maxPlayer1Deficit, currentP1Deficit)
        val newMaxP1Combo = if (state.currentPlayer == 1) max(state.maxPlayer1Combo, newComboCount) else state.maxPlayer1Combo
        val newP1CapturedGolden = state.player1CapturedGoldenNode || (state.currentPlayer == 1 && capturedGolden)

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
            totalMovesPlayed = state.totalMovesPlayed + 1,
            longestComboChain = newLongestCombo,
            firstBoxCapturer = newFirstCapturer,
            maxPlayer1Deficit = newMaxP1Deficit,
            maxPlayer1Combo = newMaxP1Combo,
            player1CapturedGoldenNode = newP1CapturedGolden
        )
        return MoveOutcome(newState = updatedState, capturedBoxes = newlyCaptured, grantedExtraTurn = grantedExtraTurn)
    }

    fun cpuTakeTurn(state: BoardState, difficulty: CpuDifficulty = state.cpuDifficulty): EdgeCoordinate? {
        if (state.isGameOver) return null
        val availableEdges = getAllAvailableEdges(state.boxRows, state.boxCols, state.horizontalEdges, state.verticalEdges, state.blockedEdges)
        if (availableEdges.isEmpty()) return null
        return when (difficulty) {
            CpuDifficulty.EASY -> chooseEasyCpuMove(availableEdges)
            CpuDifficulty.MEDIUM -> chooseMediumCpuMove(state, availableEdges)
            CpuDifficulty.HARD -> chooseHardMinimaxMove(state, availableEdges)
        }
    }

    private fun chooseEasyCpuMove(availableEdges: List<EdgeCoordinate>): EdgeCoordinate = availableEdges.random()

    private fun chooseMediumCpuMove(state: BoardState, availableEdges: List<EdgeCoordinate>): EdgeCoordinate {
        val goldenCaptures = mutableListOf<EdgeCoordinate>()
        val doubleCaptures = mutableListOf<EdgeCoordinate>()
        val singleCaptures = mutableListOf<EdgeCoordinate>()
        val toxicTrapSetups = mutableListOf<EdgeCoordinate>()
        val safeMoves = mutableListOf<EdgeCoordinate>()
        val riskyMoves = mutableListOf<EdgeCoordinate>()
        val goldenRiskyMoves = mutableListOf<EdgeCoordinate>()
        val toxicCaptures = mutableListOf<EdgeCoordinate>()

        for (edge in availableEdges) {
            val adj = getAdjacentBoxes(edge, state.boxRows, state.boxCols)
            var completable = 0
            var goldenCompletable = 0
            var toxicCompletable = 0
            var thirdSidesCreated = 0
            var goldenThirdCreated = 0
            var toxicThirdCreated = 0
            for (box in adj) {
                val sides = countClaimedSides(box.row, box.col, state.horizontalEdges, state.verticalEdges, state.blockedEdges)
                val isGolden = state.goldenNodes.contains(box)
                val isToxic = state.toxicNodes.contains(box)
                if (sides == 3) {
                    if (isToxic) {
                        toxicCompletable++
                    } else {
                        completable++
                        if (isGolden) goldenCompletable++
                    }
                } else if (sides == 2) {
                    if (isToxic) {
                        toxicThirdCreated++
                    } else {
                        thirdSidesCreated++
                        if (isGolden) goldenThirdCreated++
                    }
                }
            }
            when {
                toxicCompletable > 0 && goldenCompletable == 0 -> toxicCaptures.add(edge)
                goldenCompletable > 0 -> goldenCaptures.add(edge)
                completable >= 2 -> doubleCaptures.add(edge)
                completable == 1 -> singleCaptures.add(edge)
                toxicThirdCreated > 0 && thirdSidesCreated == 0 -> toxicTrapSetups.add(edge)
                thirdSidesCreated == 0 -> safeMoves.add(edge)
                goldenThirdCreated == 0 -> riskyMoves.add(edge)
                else -> goldenRiskyMoves.add(edge)
            }
        }
        if (goldenCaptures.isNotEmpty()) return goldenCaptures.random()
        if (doubleCaptures.isNotEmpty()) return doubleCaptures.random()
        if (singleCaptures.isNotEmpty()) return singleCaptures.random()
        if (toxicTrapSetups.isNotEmpty()) return toxicTrapSetups.random()
        if (safeMoves.isNotEmpty()) return safeMoves.random()
        if (riskyMoves.isNotEmpty()) return riskyMoves.random()
        if (goldenRiskyMoves.isNotEmpty()) return goldenRiskyMoves.random()
        return toxicCaptures.ifEmpty { availableEdges }.random()
    }

    private fun chooseHardMinimaxMove(state: BoardState, availableEdges: List<EdgeCoordinate>): EdgeCoordinate {
        val cpuPlayer = state.currentPlayer
        val humanPlayer = if (cpuPlayer == 2) 1 else 2
        val searchDepth = minimaxSearchDepth(state.boxRows, state.boxCols)
        val orderedMoves = orderMovesForPruning(state, availableEdges).shuffled().sortedByDescending { moveOrderingPriority(state, it) }

        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE
        val bestCandidates = mutableListOf<EdgeCoordinate>()

        for (edge in orderedMoves) {
            val outcome = makeMove(state, edge) ?: continue
            val nextIsMaximizing = outcome.newState.currentPlayer == cpuPlayer
            val eval = minimaxAlphaBeta(outcome.newState, searchDepth - 1, alpha, beta, nextIsMaximizing, cpuPlayer, humanPlayer)
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

    private fun minimaxAlphaBeta(state: BoardState, depth: Int, alpha: Int, beta: Int, isMaximizing: Boolean, cpuPlayer: Int, humanPlayer: Int): Int {
        if (depth <= 0 || state.isGameOver) return evaluateBoardHeuristic(state, cpuPlayer, humanPlayer)
        val available = getAllAvailableEdges(state.boxRows, state.boxCols, state.horizontalEdges, state.verticalEdges, state.blockedEdges)
        if (available.isEmpty()) return evaluateBoardHeuristic(state, cpuPlayer, humanPlayer)

        val ordered = orderMovesForPruning(state, available)
        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (edge in ordered) {
                val outcome = makeMove(state, edge) ?: continue
                val nextIsMax = outcome.newState.currentPlayer == cpuPlayer
                val eval = minimaxAlphaBeta(outcome.newState, depth - 1, currentAlpha, currentBeta, nextIsMax, cpuPlayer, humanPlayer)
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
                val eval = minimaxAlphaBeta(outcome.newState, depth - 1, currentAlpha, currentBeta, nextIsMax, cpuPlayer, humanPlayer)
                minEval = min(minEval, eval)
                currentBeta = min(currentBeta, eval)
                if (currentBeta <= currentAlpha) break
            }
            return minEval
        }
    }

    private fun evaluateBoardHeuristic(state: BoardState, cpuPlayer: Int, humanPlayer: Int): Int {
        val cpuScore = state.scoreForPlayer(cpuPlayer)
        val humanScore = state.scoreForPlayer(humanPlayer)
        var scoreDiff = (cpuScore - humanScore) * 100
        if (!state.isGameOver) {
            var threeSidedWeight = 0
            for (r in 0 until state.boxRows) {
                for (c in 0 until state.boxCols) {
                    if (state.boxes[r][c] == 0 && countClaimedSides(r, c, state.horizontalEdges, state.verticalEdges, state.blockedEdges) == 3) {
                        val boxCoord = BoxCoordinate(r, c)
                        val isGolden = state.goldenNodes.contains(boxCoord)
                        val isToxic = state.toxicNodes.contains(boxCoord)
                        threeSidedWeight += when {
                            isGolden -> 85
                            isToxic -> -80
                            else -> 35
                        }
                    }
                }
            }
            if (threeSidedWeight != 0) {
                if (state.currentPlayer == cpuPlayer) scoreDiff += threeSidedWeight
                else scoreDiff -= threeSidedWeight
            }
        }
        return scoreDiff
    }

    private fun orderMovesForPruning(state: BoardState, moves: List<EdgeCoordinate>): List<EdgeCoordinate> =
        moves.sortedByDescending { moveOrderingPriority(state, it) }

    private fun moveOrderingPriority(state: BoardState, edge: EdgeCoordinate): Int {
        val adj = getAdjacentBoxes(edge, state.boxRows, state.boxCols)
        var completable = 0
        var goldenCompletable = 0
        var toxicCompletable = 0
        var createsThird = 0
        var goldenCreatesThird = 0
        var toxicCreatesThird = 0
        for (box in adj) {
            val sides = countClaimedSides(box.row, box.col, state.horizontalEdges, state.verticalEdges, state.blockedEdges)
            val isGolden = state.goldenNodes.contains(box)
            val isToxic = state.toxicNodes.contains(box)
            if (sides == 3) {
                if (isToxic) {
                    toxicCompletable++
                } else {
                    completable++
                    if (isGolden) goldenCompletable++
                }
            } else if (sides == 2) {
                if (isToxic) {
                    toxicCreatesThird++
                } else {
                    createsThird++
                    if (isGolden) goldenCreatesThird++
                }
            }
        }
        val basePriority = when {
            completable >= 2 -> 30
            completable == 1 -> 20
            createsThird == 0 -> 10
            createsThird == 1 -> -10
            else -> -20
        }
        return basePriority + (goldenCompletable * 60) - (goldenCreatesThird * 55) - (toxicCompletable * 110) + (toxicCreatesThird * 35)
    }

    private fun getAllAvailableEdges(
        boxRows: Int,
        boxCols: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>,
        blockedEdges: Set<EdgeCoordinate> = emptySet()
    ): List<EdgeCoordinate> {
        val list = mutableListOf<EdgeCoordinate>()
        for (r in 0..boxRows) {
            for (c in 0 until boxCols) {
                val edge = EdgeCoordinate(EdgeOrientation.HORIZONTAL, r, c)
                if (hEdges[r][c] == 0 && !blockedEdges.contains(edge)) list.add(edge)
            }
        }
        for (r in 0 until boxRows) {
            for (c in 0..boxCols) {
                val edge = EdgeCoordinate(EdgeOrientation.VERTICAL, r, c)
                if (vEdges[r][c] == 0 && !blockedEdges.contains(edge)) list.add(edge)
            }
        }
        return list
    }

    private fun getAdjacentBoxes(edge: EdgeCoordinate, boxRows: Int, boxCols: Int): List<BoxCoordinate> {
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
        vEdges: List<List<Int>>,
        blockedEdges: Set<EdgeCoordinate> = emptySet()
    ): Int {
        var count = 0
        if (hEdges[boxRow][boxCol] != 0 || blockedEdges.contains(EdgeCoordinate(EdgeOrientation.HORIZONTAL, boxRow, boxCol))) count++
        if (hEdges[boxRow + 1][boxCol] != 0 || blockedEdges.contains(EdgeCoordinate(EdgeOrientation.HORIZONTAL, boxRow + 1, boxCol))) count++
        if (vEdges[boxRow][boxCol] != 0 || blockedEdges.contains(EdgeCoordinate(EdgeOrientation.VERTICAL, boxRow, boxCol))) count++
        if (vEdges[boxRow][boxCol + 1] != 0 || blockedEdges.contains(EdgeCoordinate(EdgeOrientation.VERTICAL, boxRow, boxCol + 1))) count++
        return count
    }

    private fun isBoxCompleted(
        boxRow: Int,
        boxCol: Int,
        hEdges: List<List<Int>>,
        vEdges: List<List<Int>>,
        blockedEdges: Set<EdgeCoordinate> = emptySet()
    ): Boolean = countClaimedSides(boxRow, boxCol, hEdges, vEdges, blockedEdges) == 4
}

enum class ScreenDestination { SETUP, QUICK_CONFIG, GAME, CREDITS, SETTINGS, ACHIEVEMENTS }
enum class MainMenuSection { CAMPAIGN, QUICK_MATCH }
enum class HapticEventType { LINE_TICK, BOX_CAPTURE_BUZZ, VICTORY_CELEBRATION }

data class HapticEvent(val id: Long, val type: HapticEventType, val capturedCount: Int = 0)
data class PlayerSessionStats(val wins: Int = 0, val losses: Int = 0, val ties: Int = 0)
data class MatchHistoryItem(val matchNumber: Int, val gridSizeLabel: String, val scoreSummary: String, val winnerLabel: String, val winnerPlayerNumber: Int)

data class GameUiState(
    val currentScreen: ScreenDestination = ScreenDestination.SETUP,
    val unlockedAchievements: Set<String> = emptySet(),
    val hasSeenTutorial: Boolean = false,
    val achievementToastQueue: List<AchievementProtocol> = emptyList(),
    val mainMenuSection: MainMenuSection = MainMenuSection.CAMPAIGN,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 1.0f,
    val hapticsEnabled: Boolean = true,
    val performanceMode: Boolean = false,
    val selectedTheme: ThemeColor = ThemeColor.CYBER_CYAN,
    val rankPoints: Int = 0,
    val isRankedMatch: Boolean = false,
    val lastRpChange: Int? = null,
    val totalBoxesCaptured: Int = 0,
    val unlockedCampaignLevel: Int = 1,
    val selectedCampaignLevel: CampaignLevel = CampaignLevel.LEVEL_1,
    val activeCampaignLevel: CampaignLevel? = null,
    val justUnlockedNextCampaignLevel: Boolean = false,
    val selectedGridSize: GridSize = GridSize.SIZE_4X4,
    val selectedMutationMode: MutationMode = MutationMode.STANDARD,
    val mutationsEnabled: Boolean = false,
    val selectedGameMode: GameMode = GameMode.CPU,
    val selectedCpuDifficulty: CpuDifficulty = CpuDifficulty.MEDIUM,
    val selectedTimerMode: TimerMode = TimerMode.RELAXED,
    val selectedPlayerCount: Int = 1,
    val playerInputs: List<String> = listOf("P1", "CPU", "P3", "P4"),
    val setupErrorResId: Int? = null,
    val boardState: BoardState = BoardState.create(humanPlayerCount = 1, letters = listOf("P1", "CPU"), cpuDifficulty = CpuDifficulty.MEDIUM, boxRows = GridSize.SIZE_4X4.boxRows, boxCols = GridSize.SIZE_4X4.boxCols),
    val blitzRemainingSeconds: Float = 5.0f,
    val blitzRemainingFraction: Float = 1.0f,
    val lastTurnForfeitedByTimeout: Boolean = false,
    val blitzForfeitCount: Int = 0,
    val sessionStats: Map<Int, PlayerSessionStats> = mapOf(1 to PlayerSessionStats(), 2 to PlayerSessionStats(), 3 to PlayerSessionStats(), 4 to PlayerSessionStats()),
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
        const val KEY_PERFORMANCE_MODE = "performance_mode"
        const val KEY_SOUND_VOLUME = "sound_volume"
        const val KEY_SELECTED_THEME = "selected_neon_theme"
        const val KEY_RANK_POINTS = "rank_points"
        const val KEY_SESSION_STATS_JSON = "dots_and_boxes_session_stats"
        const val KEY_MATCH_HISTORY_JSON = "dots_and_boxes_match_history"
        const val KEY_TOTAL_MATCHES = "dots_and_boxes_total_matches"
        const val KEY_UNLOCKED_ACHIEVEMENTS = "dots_and_boxes_achievements"
        const val KEY_HAS_SEEN_TUTORIAL = "has_seen_tutorial"
    }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var sharedPreferences: SharedPreferences? = null
    private var aiTurnJob: Job? = null
    private var blitzTimerJob: Job? = null
    private var hapticEventCounter: Long = 0L

    fun calculateRank(rp: Int = _uiState.value.rankPoints): RankedDivision = RankedDivision.fromRp(rp)

    fun attachSharedPreferences(prefs: SharedPreferences) {
        sharedPreferences = prefs
        val savedLevel = prefs.getInt(KEY_UNLOCKED_LEVEL, 1).coerceIn(1, CampaignLevel.entries.size)
        val savedBoxes = prefs.getInt(KEY_TOTAL_BOXES_CAPTURED, 0).coerceAtLeast(0)
        val savedHaptics = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        val savedPerformanceMode = prefs.getBoolean(KEY_PERFORMANCE_MODE, false)
        val savedSoundVolume = prefs.getFloat(KEY_SOUND_VOLUME, 1.0f).coerceIn(0f, 1f)
        val savedTheme = ThemeColor.fromName(prefs.getString(KEY_SELECTED_THEME, ThemeColor.CYBER_CYAN.name))
        val savedRankPoints = prefs.getInt(KEY_RANK_POINTS, 0).coerceAtLeast(0)
        val loadedStats = loadSessionStatsFromPrefs(prefs)
        val loadedHistory = loadMatchHistoryFromPrefs(prefs)
        val savedTotalMatches = prefs.getInt(KEY_TOTAL_MATCHES, loadedHistory.maxOfOrNull { it.matchNumber } ?: 0).coerceAtLeast(0)
        val savedAchievements = prefs.getStringSet(KEY_UNLOCKED_ACHIEVEMENTS, emptySet()) ?: emptySet()
        val savedTutorial = prefs.getBoolean(KEY_HAS_SEEN_TUTORIAL, false)

        HapticEngine.isHapticsEnabled = savedHaptics
        SoundEngine.masterVolume = savedSoundVolume
        SoundEngine.isSoundEnabled = savedSoundVolume > 0.001f
        _uiState.update { state ->
            val effectiveLevel = max(state.unlockedCampaignLevel, savedLevel)
            state.copy(
                unlockedCampaignLevel = effectiveLevel,
                selectedCampaignLevel = CampaignLevel.fromNumber(effectiveLevel),
                totalBoxesCaptured = max(state.totalBoxesCaptured, savedBoxes),
                hapticsEnabled = savedHaptics,
                performanceMode = savedPerformanceMode,
                soundVolume = savedSoundVolume,
                soundEnabled = savedSoundVolume > 0.001f,
                selectedTheme = savedTheme,
                rankPoints = max(state.rankPoints, savedRankPoints),
                sessionStats = loadedStats ?: state.sessionStats,
                matchHistory = if (loadedHistory.isNotEmpty()) loadedHistory else state.matchHistory,
                totalMatchesCompleted = max(state.totalMatchesCompleted, savedTotalMatches),
                unlockedAchievements = savedAchievements,
                hasSeenTutorial = savedTutorial
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
                    PlayerSessionStats(wins = obj.optInt("wins", 0), losses = obj.optInt("losses", 0), ties = obj.optInt("ties", 0))
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

    private fun savePerformanceDataToPrefs(sessionStats: Map<Int, PlayerSessionStats>, matchHistory: List<MatchHistoryItem>, totalMatchesCompleted: Int) {
        val prefs = sharedPreferences ?: return
        runCatching {
            val statsJson = JSONObject()
            for ((pNum, stat) in sessionStats) {
                val obj = JSONObject().put("wins", stat.wins).put("losses", stat.losses).put("ties", stat.ties)
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

    private fun saveRankPointsToPrefs(rankPoints: Int) {
        val clamped = rankPoints.coerceAtLeast(0)
        sharedPreferences?.edit()?.putInt(KEY_RANK_POINTS, clamped)?.apply()
    }

    private fun unlockAchievement(achievement: AchievementProtocol) {
        val currentUnlocked = _uiState.value.unlockedAchievements
        if (!currentUnlocked.contains(achievement.id)) {
            val updated = currentUnlocked + achievement.id
            sharedPreferences?.edit()?.putStringSet(KEY_UNLOCKED_ACHIEVEMENTS, updated)?.apply()
            _uiState.update {
                it.copy(
                    unlockedAchievements = updated,
                    achievementToastQueue = it.achievementToastQueue + achievement
                )
            }
        }
    }

    fun dismissAchievementToast() {
        _uiState.update { state ->
            if (state.achievementToastQueue.isEmpty()) state
            else state.copy(achievementToastQueue = state.achievementToastQueue.drop(1))
        }
    }

    private fun checkAchievementsOnMatchEnd(boardState: BoardState, updatedRankPoints: Int) {
        val winner = boardState.winner
        val cpuScore = boardState.scoreForPlayer(2)

        // 1. Initiation: Win first match or Campaign Level 1
        if (winner == 1) {
            unlockAchievement(AchievementProtocol.INITIATION)
        }

        // 2. First Blood: Capture the very first node in a match
        if (boardState.firstBoxCapturer == 1) {
            unlockAchievement(AchievementProtocol.FIRST_BLOOD)
        }

        // 3. Turing Test: Defeat Hard AI
        if (boardState.cpuDifficulty == CpuDifficulty.HARD && winner == 1) {
            unlockAchievement(AchievementProtocol.TURING_TEST)
        }

        // 4. Chain Reaction: Captured 4+ boxes in a single unbroken turn
        if (boardState.longestComboChain >= 4 || boardState.currentComboCount >= 4 || boardState.lastCapturedBoxes.size >= 4) {
            unlockAchievement(AchievementProtocol.CHAIN_REACTION)
        }

        // 5. Comeback Kid: Win a match after trailing by 5 or more nodes
        if (winner == 1 && boardState.maxPlayer1Deficit >= 5) {
            unlockAchievement(AchievementProtocol.COMEBACK_KID)
        }

        // 6. Ghost in the Machine: Shut out the CPU (CPU score <= 0)
        if (winner == 1 && cpuScore <= 0) {
            unlockAchievement(AchievementProtocol.GHOST_IN_MACHINE)
        }

        // 7. Gold Rush: Capture a Golden Node
        if (boardState.player1CapturedGoldenNode) {
            unlockAchievement(AchievementProtocol.GOLD_RUSH)
        }

        // 8. Maze Runner: Win a mutated maze match on Hard difficulty
        if (winner == 1 && boardState.cpuDifficulty == CpuDifficulty.HARD && (boardState.mutationMode.hasMaze || boardState.blockedEdges.isNotEmpty())) {
            unlockAchievement(AchievementProtocol.MAZE_RUNNER)
        }

        // 9. Grid Master: Play 50 total matches
        if (_uiState.value.totalMatchesCompleted >= 50) {
            unlockAchievement(AchievementProtocol.GRID_MASTER)
        }

        // 10. System Freeze: Win a Blitz match without timer expiry
        if (_uiState.value.selectedTimerMode == TimerMode.BLITZ && winner == 1 && !_uiState.value.lastTurnForfeitedByTimeout) {
            unlockAchievement(AchievementProtocol.SYSTEM_FREEZE)
        }

        // 11. Calculated Pacifist: Win a match without ever triggering a combo higher than x2
        if (winner == 1 && boardState.maxPlayer1Combo <= 2) {
            unlockAchievement(AchievementProtocol.PACIFIST)
        }

        // 12-16. Ranked Circuit Division Achievements
        if (updatedRankPoints >= 100) unlockAchievement(AchievementProtocol.SILVER_CIRCUIT)
        if (updatedRankPoints >= 200) unlockAchievement(AchievementProtocol.GOLD_STANDARD)
        if (updatedRankPoints >= 300) unlockAchievement(AchievementProtocol.PLATINUM_OVERCLOCK)
        if (updatedRankPoints >= 400) unlockAchievement(AchievementProtocol.NEON_APEX)
        if (updatedRankPoints >= 1000) unlockAchievement(AchievementProtocol.NEON_GOD)

        // 17. 6x6 Conqueror
        if (winner == 1 && boardState.boxRows == 6 && boardState.boxCols == 6) {
            unlockAchievement(AchievementProtocol.MAX_GRID_CONQUEROR)
        }
    }

    private fun emitHaptic(type: HapticEventType, capturedCount: Int = 0) {
        if (!_uiState.value.hapticsEnabled) return
        hapticEventCounter++
        _uiState.update { it.copy(lastHapticEvent = HapticEvent(id = hapticEventCounter, type = type, capturedCount = capturedCount)) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        HapticEngine.isHapticsEnabled = enabled
        sharedPreferences?.edit()?.putBoolean(KEY_HAPTICS_ENABLED, enabled)?.apply()
        _uiState.update { it.copy(hapticsEnabled = enabled) }
        if (enabled) emitHaptic(HapticEventType.LINE_TICK)
    }

    fun toggleHaptics() { setHapticsEnabled(!_uiState.value.hapticsEnabled) }
    
    fun completeTutorial() {
        SoundEngine.playLineClick()
        sharedPreferences?.edit()?.putBoolean(KEY_HAS_SEEN_TUTORIAL, true)?.apply()
        _uiState.update { it.copy(hasSeenTutorial = true) }
    }

    fun replayTutorial() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        sharedPreferences?.edit()?.putBoolean(KEY_HAS_SEEN_TUTORIAL, false)?.apply()
        _uiState.update { it.copy(hasSeenTutorial = false, currentScreen = ScreenDestination.SETUP) }
    }

    fun openSettingsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.SETTINGS) }
    }

    fun closeSettingsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.SETUP) }
    }

    fun openAchievementsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.ACHIEVEMENTS) }
    }

    fun closeAchievementsScreen() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.SETUP) }
    }

    fun setSoundVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        SoundEngine.masterVolume = clamped
        val enabled = clamped > 0.001f
        SoundEngine.isSoundEnabled = enabled
        sharedPreferences?.edit()?.putFloat(KEY_SOUND_VOLUME, clamped)?.apply()
        _uiState.update { it.copy(soundVolume = clamped, soundEnabled = enabled) }
    }

    fun setPerformanceMode(enabled: Boolean) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        sharedPreferences?.edit()?.putBoolean(KEY_PERFORMANCE_MODE, enabled)?.apply()
        _uiState.update { it.copy(performanceMode = enabled) }
    }

    fun setSelectedTheme(theme: ThemeColor) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        sharedPreferences?.edit()?.putString(KEY_SELECTED_THEME, theme.name)?.apply()
        _uiState.update { it.copy(selectedTheme = theme) }
        if (theme != ThemeColor.CYBER_CYAN) {
            unlockAchievement(AchievementProtocol.CHAMELEON_PROTOCOL)
        }
    }

    fun selectPlayerCount(count: Int) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            state.copy(
                selectedPlayerCount = count,
                selectedGameMode = if (count == 1) GameMode.CPU else GameMode.PVP
            )
        }
    }

    fun clearPersistentData() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        sharedPreferences?.edit()
            ?.putInt(KEY_UNLOCKED_LEVEL, 1)
            ?.putInt(KEY_TOTAL_BOXES_CAPTURED, 0)
            ?.putInt(KEY_RANK_POINTS, 0)
            ?.putString(KEY_SELECTED_THEME, ThemeColor.CYBER_CYAN.name)
            ?.remove(KEY_SESSION_STATS_JSON)
            ?.remove(KEY_MATCH_HISTORY_JSON)
            ?.putInt(KEY_TOTAL_MATCHES, 0)
            ?.remove(KEY_UNLOCKED_ACHIEVEMENTS)
            ?.remove(KEY_HAS_SEEN_TUTORIAL)
            ?.apply()
        _uiState.update { state ->
            state.copy(
                totalBoxesCaptured = 0,
                rankPoints = 0,
                isRankedMatch = false,
                lastRpChange = null,
                selectedTheme = ThemeColor.CYBER_CYAN,
                unlockedCampaignLevel = 1,
                selectedCampaignLevel = CampaignLevel.LEVEL_1,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                unlockedAchievements = emptySet(),
                achievementToastQueue = emptyList(),
                hasSeenTutorial = false,
                sessionStats = mapOf(1 to PlayerSessionStats(), 2 to PlayerSessionStats(), 3 to PlayerSessionStats(), 4 to PlayerSessionStats()),
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
        _uiState.update { it.copy(currentScreen = ScreenDestination.QUICK_CONFIG, mainMenuSection = MainMenuSection.QUICK_MATCH, setupErrorResId = null) }
    }

    fun closeQuickMatchConfig() {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.SETUP, mainMenuSection = MainMenuSection.CAMPAIGN, setupErrorResId = null) }
    }

    fun selectMainMenuSection(section: MainMenuSection) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(mainMenuSection = section, setupErrorResId = null) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        SoundEngine.isSoundEnabled = enabled
        val effectiveVol = if (enabled && _uiState.value.soundVolume <= 0.001f) 1.0f else _uiState.value.soundVolume
        SoundEngine.masterVolume = if (enabled) effectiveVol else 0f
        _uiState.update { it.copy(soundEnabled = enabled, soundVolume = if (enabled) effectiveVol else it.soundVolume) }
        if (enabled) SoundEngine.playLineClick()
    }

    fun toggleSound() { setSoundEnabled(!_uiState.value.soundEnabled) }

    fun selectTimerMode(timerMode: TimerMode) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        val durationSec = if (timerMode.durationSeconds > 0) timerMode.durationSeconds.toFloat() else 5.0f
        _uiState.update { state -> state.copy(selectedTimerMode = timerMode, blitzRemainingSeconds = durationSec, blitzRemainingFraction = 1f, lastTurnForfeitedByTimeout = false) }
        if (_uiState.value.currentScreen == ScreenDestination.GAME) restartBlitzTimerIfNeeded()
    }

    fun selectGridSize(gridSize: GridSize) {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            val currentBoard = state.boardState
            val letters = currentBoard.players.map { it.letter }
            val durationSec = if (state.selectedTimerMode.durationSeconds > 0) state.selectedTimerMode.durationSeconds.toFloat() else 5.0f
            state.copy(
                selectedGridSize = gridSize,
                isRankedMatch = false,
                lastRpChange = null,
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
                    boxCols = gridSize.boxCols,
                    mutationMode = state.selectedMutationMode
                )
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun selectMutationMode(mode: MutationMode) {
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state ->
            state.copy(
                selectedMutationMode = mode,
                mutationsEnabled = mode != MutationMode.STANDARD,
                setupErrorResId = null
            )
        }
    }

    fun selectMutationsEnabled(enabled: Boolean) {
        selectMutationMode(if (enabled) MutationMode.CHAOS else MutationMode.STANDARD)
    }

    fun selectGameMode(mode: GameMode) {
        _uiState.update { state -> state.copy(selectedGameMode = mode, selectedPlayerCount = if (mode == GameMode.CPU) 1 else state.selectedPlayerCount.coerceIn(2, 4), setupErrorResId = null) }
    }

    fun selectCpuDifficulty(difficulty: CpuDifficulty) {
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { state -> state.copy(selectedCpuDifficulty = difficulty, boardState = state.boardState.copy(cpuDifficulty = difficulty), setupErrorResId = null) }
    }

    fun selectCampaignLevel(level: CampaignLevel) {
        if (level.levelNumber > _uiState.value.unlockedCampaignLevel) return
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(selectedCampaignLevel = level, setupErrorResId = null) }
    }

    fun resumeTournament() {
        startRankedMatch()
    }

    fun startRankedMatch() {
        val state = _uiState.value
        val division = calculateRank(state.rankPoints)
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)

        val durationSec = if (division.timerMode.durationSeconds > 0) division.timerMode.durationSeconds.toFloat() else 5.0f
        val rankedBoard = BoardState.create(
            humanPlayerCount = 1,
            letters = listOf("P1", "CPU"),
            cpuDifficulty = division.difficulty,
            boxRows = division.gridSize.boxRows,
            boxCols = division.gridSize.boxCols,
            mutationMode = MutationMode.STANDARD
        )
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                mainMenuSection = MainMenuSection.CAMPAIGN,
                isRankedMatch = true,
                lastRpChange = null,
                activeCampaignLevel = null,
                justUnlockedNextCampaignLevel = false,
                selectedGridSize = division.gridSize,
                selectedCpuDifficulty = division.difficulty,
                selectedTimerMode = division.timerMode,
                selectedGameMode = GameMode.CPU,
                selectedPlayerCount = 1,
                setupErrorResId = null,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                boardState = rankedBoard
            )
        }
        restartBlitzTimerIfNeeded()
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
            boxCols = level.gridSize.boxCols,
            mutationMode = MutationMode.STANDARD
        )
        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                mainMenuSection = MainMenuSection.CAMPAIGN,
                isRankedMatch = false,
                lastRpChange = null,
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
        val durationSec = if (state.selectedTimerMode.durationSeconds > 0) state.selectedTimerMode.durationSeconds.toFloat() else 5.0f
        
        val humanCount = if (state.selectedGameMode == GameMode.CPU) 1 else state.selectedPlayerCount
        val letters = if (humanCount == 1) listOf("P1", "CPU") else (1..humanCount).map { "P" + it }
        val initialBoard = BoardState.create(
            humanPlayerCount = humanCount,
            letters = letters,
            cpuDifficulty = state.selectedCpuDifficulty,
            boxRows = state.selectedGridSize.boxRows,
            boxCols = state.selectedGridSize.boxCols,
            mutationMode = state.selectedMutationMode
        )

        _uiState.update {
            it.copy(
                currentScreen = ScreenDestination.GAME,
                isRankedMatch = false,
                lastRpChange = null,
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
            if (newBoard.currentComboCount >= 4 || outcome.capturedBoxes.size >= 4) {
                unlockAchievement(AchievementProtocol.CHAIN_REACTION)
            }
            if (wasHumanMove && newBoard.firstBoxCapturer == 1) {
                unlockAchievement(AchievementProtocol.FIRST_BLOOD)
            }
            if (wasHumanMove && newBoard.player1CapturedGoldenNode) {
                unlockAchievement(AchievementProtocol.GOLD_RUSH)
            }
        } else {
            emitHaptic(HapticEventType.LINE_TICK)
        }

        val justFinished = newBoard.isGameOver && !_uiState.value.boardState.isGameOver
        var rankPointsToPersist: Int? = null
        var unlockedToPersist: Int? = null
        var boxesCapturedToPersist: Int? = null
        var performanceToPersist: Triple<Map<Int, PlayerSessionStats>, List<MatchHistoryItem>, Int>? = null

        _uiState.update { state ->
            val updatedTotalBoxes = if (wasHumanMove && capturedCount > 0) {
                (state.totalBoxesCaptured + capturedCount).also { boxesCapturedToPersist = it }
            } else {
                state.totalBoxesCaptured
            }
            val updatedStats = if (justFinished) recordMatchResult(state.sessionStats, newBoard) else state.sessionStats
            val nextMatchCount = if (justFinished) state.totalMatchesCompleted + 1 else state.totalMatchesCompleted
            val updatedHistory = if (justFinished) {
                val entry = buildMatchHistoryItem(nextMatchCount, newBoard)
                (listOf(entry) + state.matchHistory).take(5)
            } else {
                state.matchHistory
            }

            if (justFinished) {
                performanceToPersist = Triple(updatedStats, updatedHistory, nextMatchCount)
            }

            val rpDelta: Int? = if (justFinished && state.isRankedMatch) {
                if (newBoard.winner == 1) 25 else -15
            } else {
                state.lastRpChange
            }
            val updatedRankPoints = if (justFinished && state.isRankedMatch && rpDelta != null) {
                (state.rankPoints + rpDelta).coerceAtLeast(0).also { rankPointsToPersist = it }
            } else {
                state.rankPoints
            }

            val campaignLevel = state.activeCampaignLevel
            val playerWonCampaign = justFinished && campaignLevel != null && newBoard.winner == 1
            val newUnlockedLevel = if (playerWonCampaign && campaignLevel != null) {
                val candidateNext = (campaignLevel.levelNumber + 1).coerceAtMost(CampaignLevel.entries.size)
                max(state.unlockedCampaignLevel, candidateNext)
            } else {
                state.unlockedCampaignLevel
            }
            val didUnlockNew = playerWonCampaign && campaignLevel != null && campaignLevel.levelNumber < CampaignLevel.entries.size
            if (newUnlockedLevel > state.unlockedCampaignLevel) unlockedToPersist = newUnlockedLevel

            val durationSec = if (state.selectedTimerMode.durationSeconds > 0) state.selectedTimerMode.durationSeconds.toFloat() else 5.0f

            state.copy(
                boardState = newBoard,
                totalBoxesCaptured = updatedTotalBoxes,
                rankPoints = updatedRankPoints,
                lastRpChange = rpDelta,
                unlockedCampaignLevel = newUnlockedLevel,
                selectedCampaignLevel = if (newUnlockedLevel > state.unlockedCampaignLevel) CampaignLevel.fromNumber(newUnlockedLevel) else state.selectedCampaignLevel,
                justUnlockedNextCampaignLevel = if (justFinished) didUnlockNew else state.justUnlockedNextCampaignLevel,
                sessionStats = updatedStats,
                matchHistory = updatedHistory,
                totalMatchesCompleted = nextMatchCount,
                hoveredEdge = null,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                victoryAnimationTriggerId = if (justFinished) state.victoryAnimationTriggerId + 1 else state.victoryAnimationTriggerId
            )
        }

        if (justFinished) {
            blitzTimerJob?.cancel()
            SoundEngine.playGameEndFanfare()
            emitHaptic(HapticEventType.VICTORY_CELEBRATION, capturedCount)
            checkAchievementsOnMatchEnd(newBoard, _uiState.value.rankPoints)
        }

        rankPointsToPersist?.let { saveRankPointsToPrefs(it) }
        unlockedToPersist?.let { saveUnlockedLevelToPrefs(it) }
        boxesCapturedToPersist?.let { saveTotalBoxesCapturedToPrefs(it) }
        performanceToPersist?.let { (stats, history, total) -> savePerformanceDataToPrefs(stats, history, total) }
    }

    private fun restartBlitzTimerIfNeeded() {
        blitzTimerJob?.cancel()
        val durationSec = _uiState.value.selectedTimerMode.durationSeconds.let { if (it > 0) it.toFloat() else 5.0f }
        _uiState.update { it.copy(blitzRemainingSeconds = durationSec, blitzRemainingFraction = 1f) }
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
            val cpuEdge = DotsAndBoxesEngine.cpuTakeTurn(forfeitedBoard, forfeitedBoard.cpuDifficulty)
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

    private fun buildMatchHistoryItem(matchNumber: Int, finishedBoard: BoardState): MatchHistoryItem {
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
        return MatchHistoryItem(matchNumber = matchNumber, gridSizeLabel = "${finishedBoard.boxRows}×${finishedBoard.boxCols}", scoreSummary = scoreSummary, winnerLabel = winnerLabel, winnerPlayerNumber = winnerNum)
    }

    private fun recordMatchResult(currentStats: Map<Int, PlayerSessionStats>, finishedBoard: BoardState): Map<Int, PlayerSessionStats> {
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

                val cpuEdge = DotsAndBoxesEngine.cpuTakeTurn(latestBoard, latestBoard.cpuDifficulty) ?: break
                val outcome = DotsAndBoxesEngine.makeMove(latestBoard, cpuEdge) ?: break
                applyMoveOutcomeWithAudioAndStats(outcome, wasHumanMove = false)
            }

            if (!_uiState.value.boardState.isGameOver && !_uiState.value.boardState.isCurrentPlayerAi) {
                restartBlitzTimerIfNeeded()
            }
        }
    }

    fun adjustZoom(delta: Float) {
        _uiState.update { state -> state.copy(boardZoom = (state.boardZoom + delta).coerceIn(0.65f, 1.35f)) }
    }

    fun setZoom(zoom: Float) {
        _uiState.update { state -> state.copy(boardZoom = zoom.coerceIn(0.65f, 1.35f)) }
    }

    fun restartMatch() {
        if (_uiState.value.isRankedMatch) {
            startRankedMatch()
            return
        }
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        SoundEngine.playLineClick()
        emitHaptic(HapticEventType.LINE_TICK)
        val state = _uiState.value
        val currentBoard = state.boardState
        val letters = currentBoard.players.map { it.letter }
        val durationSec = if (state.selectedTimerMode.durationSeconds > 0) state.selectedTimerMode.durationSeconds.toFloat() else 5.0f
        _uiState.update {
            it.copy(
                hoveredEdge = null,
                lastRpChange = null,
                justUnlockedNextCampaignLevel = false,
                lastTurnForfeitedByTimeout = false,
                blitzRemainingSeconds = durationSec,
                blitzRemainingFraction = 1f,
                boardState = BoardState.create(
                    humanPlayerCount = currentBoard.humanPlayerCount,
                    letters = letters,
                    cpuDifficulty = state.selectedCpuDifficulty,
                    boxRows = state.selectedGridSize.boxRows,
                    boxCols = state.selectedGridSize.boxCols,
                    mutationMode = currentBoard.mutationMode
                )
            )
        }
        restartBlitzTimerIfNeeded()
    }

    fun returnToSetup() {
        aiTurnJob?.cancel()
        blitzTimerJob?.cancel()
        emitHaptic(HapticEventType.LINE_TICK)
        _uiState.update { it.copy(currentScreen = ScreenDestination.SETUP, hoveredEdge = null, lastTurnForfeitedByTimeout = false) }
    }
}

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
    val xFraction: Float, val yFraction: Float, val radiusDp: Float,
    val speedX: Float, val speedY: Float, val phaseOffset: Float,
    val isCyan: Boolean, val baseAlpha: Float
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { NeonGridProtocolApp() }
    }
}

@Composable
fun NeonGridProtocolApp(viewModel: GameViewModel = viewModel()) {
    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        val prefs = context.getSharedPreferences(GameViewModel.PREFS_NAME, Context.MODE_PRIVATE)
        viewModel.attachSharedPreferences(prefs)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NeonGridProtocolTheme(selectedTheme = uiState.selectedTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ProtocolDarkBackground,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Crossfade(
                targetState = uiState.currentScreen,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                label = "neonGridProtocolScreenTransition"
            ) { destination ->
                when (destination) {
                    ScreenDestination.SETUP -> {
                        CommandCenterScreen(
                            performanceMode = uiState.performanceMode,
                            unlockedAchievements = uiState.unlockedAchievements,
                            nodesCaptured = uiState.totalBoxesCaptured,
                            rankPoints = uiState.rankPoints,
                            currentDivision = viewModel.calculateRank(uiState.rankPoints),
                            sessionStats = uiState.sessionStats,
                            matchHistory = uiState.matchHistory,
                            onStartRankedMatch = viewModel::startRankedMatch,
                            onOpenQuickMatch = viewModel::openQuickMatchConfig,
                            onOpenAchievements = viewModel::openAchievementsScreen,
                            onOpenSettings = viewModel::openSettingsScreen
                        )
                    }
                    ScreenDestination.QUICK_CONFIG -> {
                        QuickMatchConfigScreen(
                            performanceMode = uiState.performanceMode,
                            selectedPlayerCount = uiState.selectedPlayerCount,
                            onSelectPlayerCount = viewModel::selectPlayerCount,
                            selectedGridSize = uiState.selectedGridSize,
                            selectedMutationMode = uiState.selectedMutationMode,
                            onSelectMutationMode = viewModel::selectMutationMode,
                            mutationsEnabled = uiState.mutationsEnabled,
                            onSelectMutationsEnabled = viewModel::selectMutationsEnabled,
                            selectedCpuDifficulty = uiState.selectedCpuDifficulty,
                            selectedTimerMode = uiState.selectedTimerMode,
                            onSelectGridSize = viewModel::selectGridSize,
                            onSelectCpuDifficulty = viewModel::selectCpuDifficulty,
                            onSelectTimerMode = viewModel::selectTimerMode,
                            onStartMatch = { viewModel.startGame() },
                            onBackToCommandCenter = viewModel::closeQuickMatchConfig
                        )
                    }
                    ScreenDestination.GAME -> {
                        GridArenaScreen(
                            hasSeenTutorial = uiState.hasSeenTutorial,
                            onCompleteTutorial = viewModel::completeTutorial,
                            soundEnabled = uiState.soundEnabled,
                            hapticsEnabled = uiState.hapticsEnabled,
                            performanceMode = uiState.performanceMode,
                            isRankedMatch = uiState.isRankedMatch,
                            lastRpChange = uiState.lastRpChange,
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
                            performanceMode = uiState.performanceMode,
                            onBack = viewModel::closeCreditsScreen
                        )
                    }
                    ScreenDestination.SETTINGS -> {
                        SettingsScreen(
                            soundVolume = uiState.soundVolume,
                            hapticsEnabled = uiState.hapticsEnabled,
                            performanceMode = uiState.performanceMode,
                            selectedTheme = uiState.selectedTheme,
                            onSoundVolumeChange = viewModel::setSoundVolume,
                            onHapticsEnabledChange = viewModel::setHapticsEnabled,
                            onPerformanceModeChange = viewModel::setPerformanceMode,
                            onSelectTheme = viewModel::setSelectedTheme,
                            onReplayTutorial = viewModel::replayTutorial,
                            onWipeSaveData = viewModel::clearPersistentData,
                            onOpenCredits = viewModel::openCreditsScreen,
                            onBackToCommandCenter = viewModel::closeSettingsScreen
                        )
                    }
                    ScreenDestination.ACHIEVEMENTS -> {
                        AchievementsScreen(
                            unlockedIds = uiState.unlockedAchievements,
                            performanceMode = uiState.performanceMode,
                            onBackToCommandCenter = viewModel::closeAchievementsScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommandCenterScreen(
    performanceMode: Boolean = false,
    unlockedAchievements: Set<String> = emptySet(),
    nodesCaptured: Int = 0,
    rankPoints: Int = 0,
    currentDivision: RankedDivision = RankedDivision.fromRp(rankPoints),
    sessionStats: Map<Int, PlayerSessionStats> = emptyMap(),
    matchHistory: List<MatchHistoryItem> = emptyList(),
    onStartRankedMatch: () -> Unit = {},
    onOpenQuickMatch: () -> Unit = {},
    onOpenAchievements: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "commandCenterTransition")
    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0.65f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "headerGlow"
    )
    val driftProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 24000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
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
        modifier = modifier.fillMaxSize().background(ObsidianBackground).testTag("command_center_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        if (!performanceMode) {
            Canvas(modifier = Modifier.fillMaxSize().testTag("drifting_neon_dots_background")) {
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
                    val dotColor = if (dot.isCyan) palette.p1Accent else ProtocolMagenta
                    val radiusPx = dot.radiusDp.dp.toPx()

                    drawCircle(color = dotColor.copy(alpha = dot.baseAlpha * 0.4f), radius = radiusPx * 2.5f, center = Offset(centerX, centerY))
                    drawCircle(color = dotColor.copy(alpha = dot.baseAlpha), radius = radiusPx, center = Offset(centerX, centerY))
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 580.dp).verticalScroll(scrollState).padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CommandCenterHeader(
                glowAlpha = headerGlow,
                onOpenAchievements = onOpenAchievements,
                onOpenSettings = onOpenSettings
            )

            CommandCenterStatsRow(
                nodesCaptured = nodesCaptured,
                rankPoints = rankPoints,
                currentDivision = currentDivision
            )

            RankedCircuitCard(
                rankPoints = rankPoints,
                division = currentDivision,
                glowAlpha = headerGlow,
                onFindRankedMatch = onStartRankedMatch
            )

            Button(
                onClick = onOpenQuickMatch,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ProtocolMagenta, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(18.dp), spotColor = ProtocolMagenta.copy(alpha = headerGlow))
                    .border(width = 2.dp, color = ProtocolMagenta, shape = RoundedCornerShape(18.dp))
                    .testTag("quick_match_button")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(text = "Quick Match", style = MaterialTheme.typography.titleSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, fontSize = 14.sp), textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Custom Grid • Mutations • Difficulty • Timers", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontSize = 10.sp), color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
                }
            }

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
    onOpenAchievements: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ProtocolCardSurface)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(colors = listOf(palette.p1Accent.copy(alpha = glowAlpha), ProtocolMagenta.copy(alpha = glowAlpha))),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        IconButton(
            onClick = onOpenAchievements,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ProtocolElevatedSurface)
                .border(1.dp, ProtocolAmber.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
                .testTag("open_achievements_button")
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = "Trophy Room",
                tint = ProtocolAmber,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "DOTS AND BOXES",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 2.0.sp,
                    shadow = Shadow(color = palette.p1Accent.copy(alpha = glowAlpha), offset = Offset.Zero, blurRadius = 28f)
                ),
                color = ProtocolTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("neon_protocol_title")
            )
            Text(
                text = "THE COMMAND CENTER • NEON LEAGUE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    shadow = Shadow(color = ProtocolMagenta.copy(alpha = 0.75f), offset = Offset.Zero, blurRadius = 14f)
                ),
                color = ProtocolMagenta,
                textAlign = TextAlign.Center
            )
        }

        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ProtocolElevatedSurface)
                .border(1.dp, palette.p1Accent.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                .testTag("open_settings_button")
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = palette.p1Accent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun CommandCenterStatsRow(
    nodesCaptured: Int,
    rankPoints: Int,
    currentDivision: RankedDivision,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Row(modifier = modifier.fillMaxWidth().testTag("player_stats_header_row"), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp), spotColor = palette.p1Accent.copy(alpha = 0.45f))
                .clip(RoundedCornerShape(16.dp))
                .background(ProtocolCardSurface)
                .border(1.5.dp, palette.p1Accent, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("nodes_captured_stat"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(imageVector = Icons.Filled.GridView, contentDescription = null, tint = palette.p1Accent, modifier = Modifier.size(15.dp))
                Text(text = "Nodes Captured", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp), color = ProtocolTextPrimary, textAlign = TextAlign.Center)
            }
            Text(text = nodesCaptured.toString(), style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, shadow = Shadow(color = palette.p1Accent, offset = Offset.Zero, blurRadius = 14f)), color = palette.p1Accent, modifier = Modifier.testTag("nodes_captured_value"))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp), spotColor = currentDivision.color.copy(alpha = 0.45f))
                .clip(RoundedCornerShape(16.dp))
                .background(ProtocolCardSurface)
                .border(1.5.dp, currentDivision.color, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .testTag("ranked_rp_stat"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = currentDivision.color, modifier = Modifier.size(15.dp))
                Text(text = "League Rating", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp), color = ProtocolTextPrimary, textAlign = TextAlign.Center)
            }
            Text(text = "$rankPoints RP", style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, shadow = Shadow(color = currentDivision.color, offset = Offset.Zero, blurRadius = 14f)), color = currentDivision.color, modifier = Modifier.testTag("ranked_rp_value"))
        }
    }
}

@Composable
fun RankedCircuitCard(
    rankPoints: Int,
    division: RankedDivision,
    glowAlpha: Float = 0.85f,
    onFindRankedMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val nextDivision = division.nextDivision()
    val progress = division.progressToNext(rankPoints)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "rankedProgressAnim"
    )
    val timerRuleText = if (division.timerMode.isTimed) "${division.timerMode.durationSeconds}s Timer" else "Relaxed Timer"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 18.dp, shape = RoundedCornerShape(22.dp), spotColor = division.color.copy(alpha = glowAlpha * 0.55f))
            .border(width = 2.dp, color = division.color.copy(alpha = 0.85f), shape = RoundedCornerShape(22.dp))
            .testTag("ranked_circuit_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(division.color.copy(alpha = 0.18f))
                            .border(2.dp, division.color, CircleShape)
                            .testTag("ranked_division_icon"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = division.title,
                            tint = division.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "RANKED CIRCUIT • NEON LEAGUE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            ),
                            color = ProtocolTextMuted
                        )
                        Text(
                            text = division.title.uppercase(Locale.US),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Black,
                                shadow = Shadow(color = division.color.copy(alpha = 0.7f), offset = Offset.Zero, blurRadius = 14f)
                            ),
                            color = division.color,
                            modifier = Modifier.testTag("ranked_division_title")
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$rankPoints RP",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black
                        ),
                        color = ProtocolTextPrimary,
                        modifier = Modifier.testTag("ranked_card_rp_text")
                    )
                    Text(
                        text = "+25 W / -15 L",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontSize = 10.sp),
                        color = ProtocolTextMuted
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${division.gridSize.label} Grid • ${division.difficulty.label} AI • $timerRuleText",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = palette.p1Accent
                    )
                    Text(
                        text = if (nextDivision != null) {
                            "${(nextDivision.minRp - rankPoints).coerceAtLeast(0)} RP to ${nextDivision.title}"
                        } else {
                            "MAX TIER (400+ RP)"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontSize = 10.sp),
                        color = division.color
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = division.color,
                    trackColor = ProtocolElevatedSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .testTag("ranked_division_progress_bar")
                )
            }

            Button(
                onClick = onFindRankedMatch,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.p1Accent,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(14.dp), spotColor = palette.p1Accent.copy(alpha = glowAlpha))
                    .testTag("find_ranked_match_button")
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Find Ranked Match",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Black
                    )
                )
            }
        }
    }
}

@Composable
fun AchievementsScreen(
    unlockedIds: Set<String>,
    performanceMode: Boolean = false,
    onBackToCommandCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val totalCount = AchievementProtocol.entries.size
    val unlockedCount = unlockedIds.size
    val completionFraction = if (totalCount > 0) (unlockedCount.toFloat() / totalCount).coerceIn(0f, 1f) else 0f

    BackHandler { onBackToCommandCenter() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .testTag("achievements_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        if (!performanceMode) {
            DriftingNeonDotsBackground()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 580.dp)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBackToCommandCenter,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolTextPrimary),
                    modifier = Modifier.height(48.dp).testTag("achievements_back_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Command Center", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Command Center", style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    text = "TROPHY ROOM",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Black,
                        color = ProtocolAmber
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, ProtocolAmber.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    .testTag("achievements_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = ProtocolAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "PROTOCOL TROPHIES UNLOCKED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = palette.p1Accent
                                )
                                Text(
                                    text = "$unlockedCount / $totalCount Trophies (${(completionFraction * 100).roundToInt()}%)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = ProtocolTextPrimary,
                                    modifier = Modifier.testTag("achievements_unlocked_count_text")
                                )
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { completionFraction },
                        color = ProtocolAmber,
                        trackColor = ProtocolElevatedSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .testTag("achievements_progress_bar")
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("achievements_lazy_column"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(items = AchievementProtocol.entries, key = { it.id }) { ach ->
                    val isUnlocked = unlockedIds.contains(ach.id)
                    val accent = if (isUnlocked) palette.p1Accent else ProtocolTextMuted
                    val bgColor = if (isUnlocked) palette.p1Accent.copy(alpha = 0.12f) else ProtocolCardSurface

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isUnlocked) 1.5.dp else 1.dp,
                                color = if (isUnlocked) palette.p1Accent.copy(alpha = 0.75f) else ProtocolBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("achievement_item_${ach.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) palette.p1Accent.copy(alpha = 0.2f) else ProtocolElevatedSurface)
                                    .border(1.5.dp, if (isUnlocked) palette.p1Accent else ProtocolBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = if (isUnlocked) ProtocolAmber else accent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = if (!isUnlocked && ach.isSecret) "???" else ach.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = if (isUnlocked) ProtocolTextPrimary else ProtocolTextMuted
                                )
                                Text(
                                    text = if (!isUnlocked && ach.isSecret) "Secret Protocol — Unlock condition hidden" else ach.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isUnlocked) palette.p1Accent else ProtocolTextMuted
                                )
                            }
                            if (isUnlocked) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Unlocked",
                                    tint = ProtocolLime,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickMatchConfigScreen(
    performanceMode: Boolean = false,
    selectedPlayerCount: Int, onSelectPlayerCount: (Int) -> Unit,
    selectedGridSize: GridSize,
    selectedMutationMode: MutationMode = MutationMode.STANDARD,
    onSelectMutationMode: (MutationMode) -> Unit = {},
    mutationsEnabled: Boolean = selectedMutationMode != MutationMode.STANDARD,
    onSelectMutationsEnabled: (Boolean) -> Unit = {},
    selectedCpuDifficulty: CpuDifficulty, selectedTimerMode: TimerMode,
    onSelectGridSize: (GridSize) -> Unit, onSelectCpuDifficulty: (CpuDifficulty) -> Unit, onSelectTimerMode: (TimerMode) -> Unit,
    onStartMatch: () -> Unit, onBackToCommandCenter: () -> Unit, modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var gridDropdownExpanded by remember { mutableStateOf(false) }
    var mutationDropdownExpanded by remember { mutableStateOf(false) }
    var diffDropdownExpanded by remember { mutableStateOf(false) }
    var timerDropdownExpanded by remember { mutableStateOf(false) }

    BackHandler { onBackToCommandCenter() }

    Box(modifier = modifier.fillMaxSize().background(ObsidianBackground).testTag("quick_match_config_screen"), contentAlignment = Alignment.TopCenter) {
        if (!performanceMode) {
            DriftingNeonDotsBackground()
        }
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp).verticalScroll(scrollState).padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    onClick = onBackToCommandCenter,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolTextPrimary),
                    modifier = Modifier.height(48.dp).testTag("back_to_command_center_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Command Center", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Command Center", style = MaterialTheme.typography.labelLarge)
                }
                Text(text = "QUICK MATCH CONFIG", style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, color = ProtocolMagenta))
            }

            Card(
                modifier = Modifier.fillMaxWidth().border(1.5.dp, ProtocolMagenta.copy(alpha = 0.7f), RoundedCornerShape(22.dp)).testTag("quick_match_section"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "PROTOCOL PARAMETERS", style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black), color = ProtocolTextPrimary)
                        Text(text = "Select Player Mode, Grid Size, Grid Mutations Sandbox, CPU Difficulty, and Time Limit.", style = MaterialTheme.typography.bodyMedium, color = ProtocolTextMuted)
                    }
                    
                    // 0. PLAYERS SECTION
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "MULTIPLAYER CONFIG", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ProtocolLime)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(1, 2, 3, 4).forEach { pCount ->
                                val isSelected = selectedPlayerCount == pCount
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) ProtocolLime else ProtocolElevatedSurface)
                                        .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) ProtocolLime else ProtocolBorder, shape = RoundedCornerShape(12.dp))
                                        .clickable { onSelectPlayerCount(pCount) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = if (pCount == 1) "1P vs CPU" else "${pCount}P", style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = if (isSelected) Color.Black else ProtocolTextPrimary)
                                }
                            }
                        }
                    }

                    // 1. GRID SIZE DROPDOWN + TOGGLE CHIPS (3x3, 4x4, 5x5, 6x6)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "GRID SIZE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ProtocolCyan)
                            Box {
                                OutlinedButton(
                                    onClick = { gridDropdownExpanded = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolCyan),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp).testTag("grid_size_dropdown_button")
                                ) {
                                    Text(text = "${selectedGridSize.label} (${selectedGridSize.totalBoxes} Nodes)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Select Grid Size")
                                }
                                DropdownMenu(expanded = gridDropdownExpanded, onDismissRequest = { gridDropdownExpanded = false }, modifier = Modifier.background(ProtocolElevatedSurface)) {
                                    GridSize.entries.forEach { sizeOption ->
                                        DropdownMenuItem(
                                            text = { Text(text = "${sizeOption.label} Grid (${sizeOption.totalBoxes} Nodes)", color = ProtocolTextPrimary, fontFamily = JetBrainsMonoFontFamily) },
                                            onClick = { onSelectGridSize(sizeOption); gridDropdownExpanded = false }
                                        )
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GridSize.entries.forEach { sizeOption ->
                                val isSelected = selectedGridSize == sizeOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) ProtocolCyan else ProtocolElevatedSurface)
                                        .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) ProtocolCyan else ProtocolBorder, shape = RoundedCornerShape(12.dp))
                                        .clickable { onSelectGridSize(sizeOption) }
                                        .testTag("setup_grid_${sizeOption.boxRows}x${sizeOption.boxCols}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = sizeOption.label, style = MaterialTheme.typography.labelLarge, color = if (isSelected) Color.Black else ProtocolTextPrimary)
                                }
                            }
                        }
                    }

                    // 1.5 GRID MUTATIONS SANDBOX (Dropdown + 5 Selectable Chips)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val mutationAccent = when (selectedMutationMode) {
                            MutationMode.STANDARD -> ProtocolCyan
                            MutationMode.MAZE_ONLY -> Color(0xFFEF4444)
                            MutationMode.GOLD_ONLY -> ThemeColor.SOLAR_FLARE.primary
                            MutationMode.TOXIC_ONLY -> ProtocolLime
                            MutationMode.CHAOS -> ProtocolMagenta
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "GRID MUTATIONS SANDBOX", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = mutationAccent)
                                Text(text = selectedMutationMode.subtitle, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ProtocolTextMuted)
                            }
                            Box {
                                OutlinedButton(
                                    onClick = { mutationDropdownExpanded = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = mutationAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp).testTag("mutation_mode_dropdown_button")
                                ) {
                                    Text(text = selectedMutationMode.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Select Mutation Mode")
                                }
                                DropdownMenu(expanded = mutationDropdownExpanded, onDismissRequest = { mutationDropdownExpanded = false }, modifier = Modifier.background(ProtocolElevatedSurface)) {
                                    MutationMode.entries.forEach { modeOption ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(text = modeOption.label, color = ProtocolTextPrimary, fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold)
                                                    Text(text = modeOption.subtitle, color = ProtocolTextMuted, style = MaterialTheme.typography.labelSmall)
                                                }
                                            },
                                            onClick = {
                                                onSelectMutationMode(modeOption)
                                                mutationDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MutationMode.entries.forEach { modeOption ->
                                val isSelected = selectedMutationMode == modeOption
                                val chipAccent = when (modeOption) {
                                    MutationMode.STANDARD -> ProtocolCyan
                                    MutationMode.MAZE_ONLY -> Color(0xFFEF4444)
                                    MutationMode.GOLD_ONLY -> ThemeColor.SOLAR_FLARE.primary
                                    MutationMode.TOXIC_ONLY -> ProtocolLime
                                    MutationMode.CHAOS -> ProtocolMagenta
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) chipAccent else ProtocolElevatedSurface)
                                        .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) chipAccent else ProtocolBorder, shape = RoundedCornerShape(12.dp))
                                        .clickable { onSelectMutationMode(modeOption) }
                                        .testTag("mutation_mode_${modeOption.name.lowercase(Locale.US)}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = modeOption.label,
                                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.Black else ProtocolTextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // 2. CPU DIFFICULTY DROPDOWN + TOGGLE CHIPS (Easy, Medium, Hard)
                    AnimatedVisibility(visible = selectedPlayerCount == 1) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "CPU DIFFICULTY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ProtocolAmber)
                            Box {
                                OutlinedButton(
                                    onClick = { diffDropdownExpanded = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolAmber),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp).testTag("cpu_difficulty_dropdown_button")
                                ) {
                                    Text(text = selectedCpuDifficulty.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Select CPU Difficulty")
                                }
                                DropdownMenu(expanded = diffDropdownExpanded, onDismissRequest = { diffDropdownExpanded = false }, modifier = Modifier.background(ProtocolElevatedSurface)) {
                                    CpuDifficulty.entries.forEach { diff ->
                                        DropdownMenuItem(
                                            text = { Text(text = when (diff) { CpuDifficulty.EASY -> "Easy (Random AI)"; CpuDifficulty.MEDIUM -> "Medium (Greedy AI)"; CpuDifficulty.HARD -> "Hard (Minimax AI)" }, color = ProtocolTextPrimary, fontFamily = JetBrainsMonoFontFamily) },
                                            onClick = { onSelectCpuDifficulty(diff); diffDropdownExpanded = false }
                                        )
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CpuDifficulty.entries.forEach { diff ->
                                val isSelected = selectedCpuDifficulty == diff
                                val diffAccent = when (diff) { CpuDifficulty.EASY -> ProtocolLime; CpuDifficulty.MEDIUM -> ProtocolAmber; CpuDifficulty.HARD -> ProtocolMagenta }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) diffAccent else ProtocolElevatedSurface)
                                        .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) diffAccent else ProtocolBorder, shape = RoundedCornerShape(12.dp))
                                        .clickable { onSelectCpuDifficulty(diff) }
                                        .testTag("cpu_diff_${diff.name.lowercase()}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = diff.label, style = MaterialTheme.typography.labelLarge, color = if (isSelected) Color.Black else ProtocolTextPrimary)
                                }
                            }
                        }
                    }
                    } // End AnimatedVisibility

                    // 3. TIME LIMIT DROPDOWN + TOGGLE CHIPS (Relaxed, 3s, 5s, 10s)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "TIME LIMIT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ProtocolMagenta)
                            Box {
                                OutlinedButton(
                                    onClick = { timerDropdownExpanded = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolMagenta),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(36.dp).testTag("time_limit_dropdown_button")
                                ) {
                                    Text(text = selectedTimerMode.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Select Time Limit")
                                }
                                DropdownMenu(expanded = timerDropdownExpanded, onDismissRequest = { timerDropdownExpanded = false }, modifier = Modifier.background(ProtocolElevatedSurface)) {
                                    TimerMode.entries.forEach { mode ->
                                        DropdownMenuItem(
                                            text = { Text(text = mode.label, color = ProtocolTextPrimary, fontFamily = JetBrainsMonoFontFamily) },
                                            onClick = { onSelectTimerMode(mode); timerDropdownExpanded = false }
                                        )
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                        .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) modeAccent else ProtocolBorder, shape = RoundedCornerShape(12.dp))
                                        .clickable { onSelectTimerMode(mode) }
                                        .testTag("timer_${mode.name.lowercase()}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(imageVector = modeIcon, contentDescription = null, tint = if (isSelected) Color.Black else modeAccent, modifier = Modifier.size(14.dp))
                                        Text(text = mode.label, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), color = if (isSelected) Color.Black else ProtocolTextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onStartMatch,
                        modifier = Modifier.fillMaxWidth().height(54.dp).testTag("start_game_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ProtocolMagenta, contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "LAUNCH GRID ARENA", style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black))
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSessionHistoryCard(sessionStats: Map<Int, PlayerSessionStats>, matchHistory: List<MatchHistoryItem>) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, ProtocolBorder, RoundedCornerShape(18.dp)).testTag("dashboard_stats_history_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Filled.History, contentDescription = null, tint = ProtocolAmber, modifier = Modifier.size(16.dp))
                    Text(text = "PROTOCOL TELEMETRY", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold), color = ProtocolTextMuted)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val p1Stat = sessionStats[1] ?: PlayerSessionStats()
                    val cpuStat = sessionStats[2] ?: PlayerSessionStats()
                    Text(text = "USER: ${p1Stat.wins}W-${p1Stat.losses}L", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold), color = ProtocolCyan)
                    Text(text = "CPU: ${cpuStat.wins}W-${cpuStat.losses}L", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold), color = ProtocolMagenta)
                }
            }

            if (matchHistory.isEmpty()) {
                Text(text = "No completed matches in this session yet.", style = MaterialTheme.typography.labelSmall, color = ProtocolTextMuted)
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 96.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(items = matchHistory, key = { it.matchNumber }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(ProtocolElevatedSurface).padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "#${item.matchNumber} (${item.gridSizeLabel}) • ${item.scoreSummary}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily), color = ProtocolTextPrimary)
                            Text(text = item.winnerLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (item.winnerPlayerNumber == 1) ProtocolCyan else ProtocolMagenta)
                        }
                    }
                }
            }
        }
    }
}

private data class DriftingDotSpec(
    val initialXFraction: Float, val initialYFraction: Float, val radiusDp: Float,
    val driftSpeedX: Float, val driftSpeedY: Float, val phaseOffset: Float,
    val isCyan: Boolean, val baseAlpha: Float
)

@Composable
fun DriftingNeonDotsBackground(modifier: Modifier = Modifier) {
    val palette = LocalGamePalette.current
    val infiniteTransition = rememberInfiniteTransition(label = "driftingNeonDotsTransition")
    val driftPhase by infiniteTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 22000, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "driftPhase")
    val shimmerPulse by infiniteTransition.animateFloat(initialValue = 0.65f, targetValue = 1.0f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "shimmerPulse")

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

    Canvas(modifier = modifier.fillMaxSize().testTag("drifting_neon_dots_background")) {
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

            drawCircle(color = dotColor.copy(alpha = alpha * 0.38f), radius = radiusPx * 2.4f, center = Offset(px, py))
            drawCircle(color = dotColor.copy(alpha = alpha), radius = radiusPx, center = Offset(px, py))
        }
    }
}

@Composable
fun SettingsScreen(
    soundVolume: Float,
    hapticsEnabled: Boolean,
    performanceMode: Boolean,
    selectedTheme: ThemeColor = ThemeColor.CYBER_CYAN,
    onSoundVolumeChange: (Float) -> Unit,
    onHapticsEnabledChange: (Boolean) -> Unit,
    onPerformanceModeChange: (Boolean) -> Unit,
    onSelectTheme: (ThemeColor) -> Unit = {},
    onReplayTutorial: () -> Unit,
    onWipeSaveData: () -> Unit,
    onOpenCredits: () -> Unit = {},
    onBackToCommandCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val scrollState = rememberScrollState()
    var wipeConfirmTriggered by remember { mutableStateOf(false) }

    BackHandler { onBackToCommandCenter() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .testTag("settings_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        if (!performanceMode) {
            DriftingNeonDotsBackground()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBackToCommandCenter,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolTextPrimary),
                    modifier = Modifier.height(48.dp).testTag("settings_top_back_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Command Center", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Command Center", style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    text = "SYSTEM SETTINGS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Black,
                        color = palette.p1Accent
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, palette.p1Accent.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                    .testTag("settings_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // 0. NEON THEME: 4 Circular Color Swatches
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ProtocolElevatedSurface)
                            .border(1.dp, palette.p1Accent.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("settings_neon_theme_section"),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEON THEME",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = palette.p1Accent
                            )
                            Text(
                                text = selectedTheme.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = selectedTheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ThemeColor.entries.forEach { themeOption ->
                                val isSelected = selectedTheme == themeOption
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .shadow(
                                                elevation = if (isSelected) 12.dp else 2.dp,
                                                shape = CircleShape,
                                                spotColor = themeOption.primary
                                            )
                                            .clip(CircleShape)
                                            .background(themeOption.primary)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.5.dp,
                                                color = if (isSelected) Color.White else ProtocolDarkBackground,
                                                shape = CircleShape
                                            )
                                            .clickable { onSelectTheme(themeOption) }
                                            .testTag("theme_swatch_${themeOption.name.lowercase()}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = "${themeOption.label} Selected",
                                                tint = Color.Black,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = themeOption.label.substringBefore(" "),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) themeOption.primary else ProtocolTextMuted
                                    )
                                }
                            }
                        }
                    }

                    // 1. AUDIO: Continuous Volume Slider (0f..1f)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ProtocolElevatedSurface)
                            .border(1.dp, ProtocolBorder, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (soundVolume > 0.001f) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                                    contentDescription = null,
                                    tint = palette.p1Accent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "AUDIO SYNTHESIZER VOLUME",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ProtocolTextPrimary
                                    )
                                    Text(
                                        text = "Master procedural sound engine gain",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ProtocolTextMuted
                                    )
                                }
                            }
                            Text(
                                text = "${(soundVolume * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Black
                                ),
                                color = palette.p1Accent,
                                modifier = Modifier.testTag("settings_volume_percentage")
                            )
                        }

                        Slider(
                            value = soundVolume.coerceIn(0f, 1f),
                            onValueChange = onSoundVolumeChange,
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = palette.p1Accent,
                                activeTrackColor = palette.p1Accent,
                                inactiveTrackColor = ProtocolDarkBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_volume_slider")
                        )
                    }

                    // 2. HAPTICS: Toggle for Haptics
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ProtocolElevatedSurface)
                            .border(
                                width = 1.dp,
                                color = if (hapticsEnabled) palette.p1Accent.copy(alpha = 0.6f) else ProtocolBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onHapticsEnabledChange(!hapticsEnabled) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("settings_haptics_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Vibration,
                                contentDescription = null,
                                tint = if (hapticsEnabled) palette.p1Accent else ProtocolTextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Haptics",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ProtocolTextPrimary
                                )
                                Text(
                                    text = "Tactile feedback on line draws and node captures",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProtocolTextMuted
                                )
                            }
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = onHapticsEnabledChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = palette.p1Accent,
                                uncheckedThumbColor = ProtocolTextMuted,
                                uncheckedTrackColor = ProtocolDarkBackground
                            ),
                            modifier = Modifier.testTag("settings_haptics_switch")
                        )
                    }

                    // 3. PERFORMANCE: Switch for "Performance Mode (Disable particles & background)"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ProtocolElevatedSurface)
                            .border(
                                width = 1.dp,
                                color = if (performanceMode) ProtocolMagenta.copy(alpha = 0.6f) else ProtocolBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onPerformanceModeChange(!performanceMode) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("settings_performance_mode_row"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = if (performanceMode) ProtocolMagenta else ProtocolTextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Performance Mode (Disable particles & background)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ProtocolTextPrimary
                                )
                                Text(
                                    text = "Disables background dots, screen shake, particle bursts, and heavy overlays",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProtocolTextMuted
                                )
                            }
                        }
                        Switch(
                            checked = performanceMode,
                            onCheckedChange = onPerformanceModeChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ProtocolMagenta,
                                uncheckedThumbColor = ProtocolTextMuted,
                                uncheckedTrackColor = ProtocolDarkBackground
                            ),
                            modifier = Modifier.testTag("settings_performance_mode_switch")
                        )
                    }

                    // 4. DATA & TUTORIAL: "Replay Tutorial" and "Wipe Save Data"
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "DATA & PROTOCOL MANAGEMENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ProtocolAmber
                        )

                        Button(
                            onClick = onReplayTutorial,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.p1Accent,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("replay_tutorial_button")
                        ) {
                            Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Replay Tutorial",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        Button(
                            onClick = {
                                onWipeSaveData()
                                wipeConfirmTriggered = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ProtocolMagenta,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("wipe_save_data_button")
                        ) {
                            Icon(imageVector = Icons.Filled.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (wipeConfirmTriggered) "Save Data Wiped!" else "Wipe Save Data",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenCredits,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ProtocolTextPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("settings_open_credits_button")
                        ) {
                            Icon(imageVector = Icons.Filled.Code, contentDescription = null, tint = palette.p1Accent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "System Credits",
                                style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFontFamily)
                            )
                        }
                    }

                    // 5. Back to Command Center Button
                    OutlinedButton(
                        onClick = onBackToCommandCenter,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.p1Accent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.5.dp, palette.p1Accent, RoundedCornerShape(14.dp))
                            .testTag("settings_back_to_command_center_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Back to Command Center",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
            }

            // 6. Version Label at the very bottom
            Text(
                text = "v3.0.0 - Neon League",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    letterSpacing = 1.2.sp
                ),
                color = palette.mutedText.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 8.dp)
                    .testTag("settings_version_label")
            )
        }
    }
}

@Composable
fun CreditsScreen(performanceMode: Boolean = false, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalGamePalette.current
    val scrollState = rememberScrollState()
    BackHandler { onBack() }

    val infiniteTransition = rememberInfiniteTransition(label = "creditsGlowTransition")
    val borderGlow by infiniteTransition.animateFloat(initialValue = 0.6f, targetValue = 1.0f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "creditsBorderGlow")

    Box(modifier = modifier.fillMaxSize().background(ObsidianBackground).testTag("credits_screen"), contentAlignment = Alignment.Center) {
        if (!performanceMode) {
            DriftingNeonDotsBackground()
        }
        Column(modifier = Modifier.fillMaxWidth().widthIn(max = 540.dp).verticalScroll(scrollState).padding(horizontal = 20.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().shadow(24.dp, RoundedCornerShape(26.dp), spotColor = palette.p1Accent.copy(alpha = borderGlow)).border(width = 2.5.dp, brush = Brush.linearGradient(colors = listOf(palette.p1Accent.copy(alpha = borderGlow), palette.p2Accent.copy(alpha = borderGlow), palette.p1Accent.copy(alpha = borderGlow))), shape = RoundedCornerShape(26.dp)).testTag("credits_screen_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface.copy(alpha = 0.94f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(palette.p1Accent.copy(alpha = 0.16f)).border(2.dp, palette.p1Accent, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = palette.p1Accent, modifier = Modifier.size(32.dp))
                    }
                    Text(text = "SYSTEM CREDITS", style = MaterialTheme.typography.headlineMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, letterSpacing = 2.sp, shadow = Shadow(color = palette.p1Accent, offset = Offset.Zero, blurRadius = 20f)), color = palette.primaryText, textAlign = TextAlign.Center)
                    CreditSectionCard(icon = Icons.Filled.Code, text = "Design & Development: Muzammil S. Dagosh", accentColor = palette.p1Accent, testTag = "credits_design_dev_text")
                    CreditSectionCard(icon = Icons.Filled.Memory, text = "AI Co-Pilot & Logic: Gemini", accentColor = palette.p2Accent, testTag = "credits_ai_copilot_text")
                    CreditSectionCard(icon = Icons.Filled.AutoAwesome, text = "Framework: Kotlin & Jetpack Compose via Google AI Studio", accentColor = palette.p3Accent, testTag = "credits_framework_text")
                    CreditSectionCard(icon = Icons.Filled.CloudDone, text = "Build Pipeline: Cloud CI/CD Assembly", accentColor = palette.streakAccent, testTag = "credits_build_pipeline_text")

                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("credits_back_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette.p1Accent, contentColor = Color.Black)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "BACK", style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp))
                        }
                    }

                    Text(
                        text = "v3.0.0 - Neon League",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            letterSpacing = 1.2.sp
                        ),
                        color = palette.mutedText.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("credits_version_label")
                    )
                }
            }
        }
    }
}

@Composable
private fun CreditSectionCard(icon: ImageVector, text: String, accentColor: Color, testTag: String) {
    val palette = LocalGamePalette.current
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(palette.backgroundDeep.copy(alpha = 0.85f)).border(1.5.dp, accentColor.copy(alpha = 0.65f), RoundedCornerShape(16.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
        Text(text = text, style = MaterialTheme.typography.titleMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, shadow = Shadow(color = accentColor.copy(alpha = 0.65f), offset = Offset.Zero, blurRadius = 12f)), color = palette.primaryText, textAlign = TextAlign.Center, modifier = Modifier.testTag(testTag))
    }
}

@Composable
fun GridArenaScreen(
    hasSeenTutorial: Boolean = false,
    onCompleteTutorial: () -> Unit = {},
    soundEnabled: Boolean, hapticsEnabled: Boolean, performanceMode: Boolean = false,
    isRankedMatch: Boolean = false, lastRpChange: Int? = null,
    activeCampaignLevel: CampaignLevel?, justUnlockedNextCampaignLevel: Boolean,
    selectedGridSize: GridSize, selectedCpuDifficulty: CpuDifficulty, selectedTimerMode: TimerMode,
    blitzRemainingSeconds: Float, blitzRemainingFraction: Float, lastTurnForfeitedByTimeout: Boolean,
    boardState: BoardState, hoveredEdge: EdgeCoordinate?, boardZoom: Float, lastHapticEvent: HapticEvent?, victoryAnimationTriggerId: Int,
    onSelectGridSize: (GridSize) -> Unit, onSelectCpuDifficulty: (CpuDifficulty) -> Unit, onSelectTimerMode: (TimerMode) -> Unit,
    onBlitzTimeout: () -> Unit, onAdvanceToNextCampaignLevel: () -> Unit, onToggleSound: () -> Unit, onToggleHaptics: () -> Unit,
    onEdgeHover: (EdgeCoordinate?) -> Unit, onEdgeClick: (EdgeCoordinate) -> Unit, onAdjustZoom: (Float) -> Unit, onSetZoom: (Float) -> Unit,
    onRestartMatch: () -> Unit, onReturnToCommandCenter: () -> Unit, modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val isTimedMode = selectedTimerMode.isTimed
    val timerDurationSeconds = selectedTimerMode.durationSeconds
    val isPlayerTurn = !boardState.isGameOver && !boardState.isCurrentPlayerAi
    var timerProgress by remember { mutableFloatStateOf(blitzRemainingFraction.coerceIn(0f, 1f)) }
    var timerSecondsLeft by remember { mutableFloatStateOf(if (timerDurationSeconds > 0) timerDurationSeconds.toFloat() else 5f) }

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

    LaunchedEffect(lastHapticEvent?.id) {
        val event = lastHapticEvent ?: return@LaunchedEffect
        if (!hapticsEnabled) return@LaunchedEffect
        when (event.type) {
            HapticEventType.LINE_TICK -> { hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove); HapticEngine.triggerLineTick(context, null) }
            HapticEventType.BOX_CAPTURE_BUZZ -> { hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress); HapticEngine.triggerBoxCaptureBuzz(context, null, event.capturedCount) }
            HapticEventType.VICTORY_CELEBRATION -> { hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress); HapticEngine.triggerVictoryCelebration(context, null) }
        }
    }

    BackHandler { onReturnToCommandCenter() }

    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val neonGlowPulse by infiniteTransition.animateFloat(initialValue = 0.65f, targetValue = 1.0f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "neonGlowPulse")
    val comboHeartbeatScale by infiniteTransition.animateFloat(initialValue = 1.0f, targetValue = 1.05f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 380, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "comboHeartbeatScale")

    val boardComboZoomAnim = remember { Animatable(1f) }
    val comboPulseWaveAnim = remember { Animatable(0f) }
    val cameraShakeAnim = remember { Animatable(0f) }
    val overlayBurstScale = remember { Animatable(0f) }
    val overlayBurstAlpha = remember { Animatable(0f) }

    LaunchedEffect(boardState.comboTriggerId, performanceMode) {
        if (performanceMode) {
            cameraShakeAnim.snapTo(0f)
            overlayBurstAlpha.snapTo(0f)
        }
        if (boardState.comboTriggerId > 0) {
            val isCombo = boardState.currentComboCount >= 2 || boardState.lastCapturedBoxes.size >= 2
            val targetZoomPunch = if (isCombo) 1.04f + (boardState.currentComboCount.coerceAtMost(6) * 0.007f) else 1.015f

            if (!performanceMode) {
                launch {
                    val shakeMagnitude = if (isCombo) 8f else 3.5f
                    for (step in listOf(1f, -0.85f, 0.7f, -0.5f, 0.3f, 0f)) {
                        cameraShakeAnim.animateTo(targetValue = step * shakeMagnitude, animationSpec = tween(durationMillis = 40))
                    }
                }
            } else {
                cameraShakeAnim.snapTo(0f)
            }

            launch {
                boardComboZoomAnim.snapTo(1f)
                boardComboZoomAnim.animateTo(targetValue = targetZoomPunch, animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing))
                boardComboZoomAnim.animateTo(targetValue = 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow))
            }

            launch {
                comboPulseWaveAnim.snapTo(1f)
                comboPulseWaveAnim.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing))
            }

            if (isCombo && !performanceMode) {
                launch {
                    overlayBurstScale.snapTo(0.3f)
                    overlayBurstAlpha.snapTo(1f)
                    overlayBurstScale.animateTo(targetValue = 1.10f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
                    overlayBurstScale.animateTo(targetValue = 1.0f, animationSpec = tween(durationMillis = 200))
                    overlayBurstAlpha.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 340))
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

    Box(modifier = modifier.fillMaxSize().background(ObsidianBackground).padding(horizontal = 10.dp, vertical = 6.dp).testTag("grid_arena_screen"), contentAlignment = Alignment.Center) {
        if (!performanceMode) {
            DriftingNeonDotsBackground()
        }
        Column(
            modifier = Modifier.fillMaxSize().widthIn(max = 820.dp).graphicsLayer { translationX = cameraShakeAnim.value; translationY = -cameraShakeAnim.value * 0.6f },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TopGameToolbar(soundEnabled = soundEnabled, hapticsEnabled = hapticsEnabled, claimedBoxes = boardState.totalClaimedBoxes, totalBoxes = boardState.totalBoxes, onToggleSound = onToggleSound, onToggleHaptics = onToggleHaptics, onReturnToSetup = onReturnToCommandCenter)

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

            AnimatedVisibility(visible = !hasSeenTutorial) {
                val hasThreeSidedBox = (0 until boardState.boxRows).any { r ->
                    (0 until boardState.boxCols).any { c ->
                        var sides = 0
                        if (boardState.horizontalEdges[r][c] != 0) sides++
                        if (boardState.horizontalEdges[r + 1][c] != 0) sides++
                        if (boardState.verticalEdges[r][c] != 0) sides++
                        if (boardState.verticalEdges[r][c + 1] != 0) sides++
                        sides == 3
                    }
                }
                val tutorialMessage = when {
                    boardState.currentComboCount > 0 -> "NODE CAPTURED! You earned an EXTRA TURN."
                    hasThreeSidedBox -> "CRITICAL: Draw the 4th line to capture the node!"
                    boardState.totalMovesPlayed == 0 -> "TUTORIAL: Tap between dots to draw a line."
                    else -> "Take turns drawing lines. Capture the most nodes to win."
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(14.dp), spotColor = ProtocolCyan.copy(alpha = neonGlowPulse))
                        .border(1.5.dp, ProtocolCyan, RoundedCornerShape(14.dp))
                        .testTag("holographic_tutorial_banner"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ProtocolCardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = tutorialMessage,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                shadow = Shadow(color = ProtocolCyan.copy(alpha = 0.65f), offset = Offset.Zero, blurRadius = 10f)
                            ),
                            color = ProtocolCyan,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("holographic_tutorial_text")
                        )
                        IconButton(
                            onClick = onCompleteTutorial,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("dismiss_tutorial_banner_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Complete Tutorial",
                                tint = ProtocolCyan
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = boardState.isGameOver, enter = fadeIn() + scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))) {
                GameOverBanner(boardState = boardState, activeCampaignLevel = activeCampaignLevel, justUnlockedNextCampaignLevel = justUnlockedNextCampaignLevel, onAdvanceToNextCampaignLevel = onAdvanceToNextCampaignLevel, onPlayAgain = onRestartMatch, modifier = Modifier.fillMaxWidth())
            }

            val boardBorderColor = if (pulseGlowAlpha > 0.02f) activeComboColor.copy(alpha = (0.45f + pulseGlowAlpha * 0.55f).coerceIn(0f, 1f)) else palette.border
            val boardBorderWidth = if (pulseGlowAlpha > 0.02f) (1.dp + (pulseGlowAlpha * 2.5f).dp) else 1.dp

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 4.dp).graphicsLayer { scaleX = boardComboZoomAnim.value; scaleY = boardComboZoomAnim.value }.drawBehind { if (pulseGlowAlpha > 0.01f) drawCircle(brush = Brush.radialGradient(colors = listOf(activeComboColor.copy(alpha = 0.34f * pulseGlowAlpha), Color.Transparent)), radius = size.maxDimension * (0.55f + (1f - pulseGlowAlpha) * 0.25f)) }.border(boardBorderWidth, boardBorderColor, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                    BoardZoomToolbar(gridLabel = "${boardState.boxRows}×${boardState.boxCols}", boardZoom = boardZoom, comboCount = boardState.currentComboCount, comboAccentColor = activeComboColor, comboPulseFraction = pulseGlowAlpha, onAdjustZoom = onAdjustZoom, onResetZoom = { onSetZoom(0.95f) })

                    BoxWithConstraints(modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTransformGestures { _, _, zoomChange, _ -> if (zoomChange != 1f) onSetZoom(boardZoom * zoomChange) } }, contentAlignment = Alignment.Center) {
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
                        val baseDotDiameter = if (rawBoxSize < 24.dp && maxGridDim >= 5) 12.dp else idealDotDiameter
                        val recomputedBoxW = (safeStageWidth - (baseDotDiameter * dotCols)) / boxCols.toFloat()
                        val recomputedBoxH = (safeStageHeight - (baseDotDiameter * dotRows)) / boxRows.toFloat()
                        val baseBoxSize = min(recomputedBoxW, recomputedBoxH).coerceIn(20.dp, 76.dp)
                        val scaledDotSlotSize = (baseDotDiameter * boardZoom).coerceIn(10.dp, 22.dp)
                        val scaledBoxSize = (baseBoxSize * boardZoom).coerceIn(18.dp, 88.dp)

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            DotsAndBoxesGrid(
                                boardState = boardState, hoveredEdge = hoveredEdge,
                                neonGlowPulse = neonGlowPulse, comboPulseFraction = pulseGlowAlpha,
                                performanceMode = performanceMode,
                                dotSlotSize = scaledDotSlotSize, boxSize = scaledBoxSize,
                                onEdgeHover = onEdgeHover, onEdgeClick = onHumanMoveExecuted
                            )
                        }
                    }
                }
            }

            BottomControlsAndFooter(
                activeCampaignLevel = activeCampaignLevel, selectedGridSize = selectedGridSize,
                selectedCpuDifficulty = selectedCpuDifficulty, selectedTimerMode = selectedTimerMode,
                onSelectGridSize = onSelectGridSize, onSelectCpuDifficulty = onSelectCpuDifficulty,
                onSelectTimerMode = onSelectTimerMode, onRestartMatch = onRestartMatch, modifier = Modifier.fillMaxWidth()
            )
        }

        if (!performanceMode && overlayBurstAlpha.value > 0.02f && boardState.currentComboCount >= 2) {
            val isDouble = boardState.lastCapturedBoxes.size >= 2
            val mainTitle = if (isDouble) "DOUBLE NODE CAPTURE!" else "COMBO x${boardState.currentComboCount}!"
            val subTitle = if (isDouble) "COMBO x${boardState.currentComboCount} STREAK!" else "PROTOCOL OVERDRIVE!"

            Box(modifier = Modifier.graphicsLayer { scaleX = overlayBurstScale.value; scaleY = overlayBurstScale.value; alpha = overlayBurstAlpha.value }.clip(RoundedCornerShape(20.dp)).background(palette.backgroundDeep.copy(alpha = 0.92f)).border(2.5.dp, activeComboColor, RoundedCornerShape(20.dp)).padding(horizontal = 24.dp, vertical = 14.dp).testTag("combo_burst_overlay"), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = mainTitle, style = MaterialTheme.typography.headlineMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, fontSize = 26.sp, shadow = Shadow(color = activeComboColor, offset = Offset.Zero, blurRadius = 24f)), color = palette.primaryText, textAlign = TextAlign.Center)
                    Text(text = subTitle, style = MaterialTheme.typography.labelLarge.copy(shadow = Shadow(color = palette.streakAccent, offset = Offset.Zero, blurRadius = 16f)), color = palette.streakAccent, textAlign = TextAlign.Center)
                }
            }
        }

        if (boardState.isGameOver) {
            FullScreenVictoryAnimationOverlay(
                boardState = boardState, triggerId = victoryAnimationTriggerId,
                performanceMode = performanceMode,
                isRankedMatch = isRankedMatch, lastRpChange = lastRpChange,
                activeCampaignLevel = activeCampaignLevel, justUnlockedNextCampaignLevel = justUnlockedNextCampaignLevel,
                onAdvanceToNextCampaignLevel = onAdvanceToNextCampaignLevel, onPlayAgain = onRestartMatch, onReturnToSetup = onReturnToCommandCenter
            )
        }
    }
}

@Composable
private fun BottomControlsAndFooter(
    activeCampaignLevel: CampaignLevel?, selectedGridSize: GridSize, selectedCpuDifficulty: CpuDifficulty, selectedTimerMode: TimerMode,
    onSelectGridSize: (GridSize) -> Unit, onSelectCpuDifficulty: (CpuDifficulty) -> Unit, onSelectTimerMode: (TimerMode) -> Unit, onRestartMatch: () -> Unit, modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Card(modifier = modifier.border(1.dp, palette.border, RoundedCornerShape(18.dp)).testTag("bottom_footer_card"), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = palette.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "GRID:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = palette.mutedText)
                GridSize.entries.forEach { sizeOption ->
                    val isSelected = selectedGridSize == sizeOption
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) palette.p1Accent else palette.surfaceElevated)
                            .border(width = if (isSelected) 1.5.dp else 1.dp, color = if (isSelected) palette.p1Accent else palette.border, shape = RoundedCornerShape(9.dp))
                            .clickable { onSelectGridSize(sizeOption) }
                            .testTag("grid_size_${sizeOption.boxRows}x${sizeOption.boxCols}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = sizeOption.label, style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 10.5.sp), color = if (isSelected) Color.Black else palette.primaryText)
                    }
                }
                Button(
                    onClick = onRestartMatch, shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.p2Accent, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(34.dp).testTag("restart_match_button")
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Restart Game", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "Restart", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = if (activeCampaignLevel != null) "LVL ${activeCampaignLevel.levelNumber}:" else "AI:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = if (activeCampaignLevel != null) palette.p1Accent else palette.mutedText)
                CpuDifficulty.entries.forEach { diff ->
                    val isSelected = selectedCpuDifficulty == diff
                    val diffColor = when (diff) { CpuDifficulty.EASY -> palette.p3Accent; CpuDifficulty.MEDIUM -> palette.streakAccent; CpuDifficulty.HARD -> palette.p2Accent }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) diffColor else palette.surfaceElevated)
                            .border(width = if (isSelected) 1.5.dp else 1.dp, color = if (isSelected) diffColor else palette.border, shape = RoundedCornerShape(9.dp))
                            .clickable { onSelectCpuDifficulty(diff) }
                            .testTag("game_diff_${diff.name.lowercase()}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = diff.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = if (isSelected) Color.Black else palette.primaryText)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = "TIME:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = palette.mutedText)
                TimerMode.entries.forEach { mode ->
                    val isSelected = selectedTimerMode == mode
                    val modeColor = if (mode == TimerMode.RELAXED) palette.p1Accent else palette.p2Accent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (isSelected) modeColor else palette.surfaceElevated)
                            .border(width = if (isSelected) 1.5.dp else 1.dp, color = if (isSelected) modeColor else palette.border, shape = RoundedCornerShape(9.dp))
                            .clickable { onSelectTimerMode(mode) }
                            .testTag("game_timer_${mode.name.lowercase()}_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = mode.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp), color = if (isSelected) Color.Black else palette.primaryText, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardZoomToolbar(gridLabel: String, boardZoom: Float, comboCount: Int, comboAccentColor: Color, comboPulseFraction: Float, onAdjustZoom: (Float) -> Unit, onResetZoom: () -> Unit) {
    val palette = LocalGamePalette.current
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "THE GRID ARENA • $gridLabel NODES", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = palette.mutedText)
            AnimatedVisibility(visible = comboCount >= 2) {
                Box(modifier = Modifier.scale(1f + comboPulseFraction * 0.14f).clip(RoundedCornerShape(50)).background(comboAccentColor.copy(alpha = 0.25f)).border(1.dp, comboAccentColor, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp).testTag("combo_pulse_chip")) {
                    Text(text = "COMBO x$comboCount!", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = comboAccentColor)
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(7.dp)).background(palette.surfaceElevated).border(1.dp, palette.border, RoundedCornerShape(7.dp)).clickable { onAdjustZoom(-0.1f) }.testTag("zoom_out_button"), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.ZoomOut, contentDescription = "Zoom out board", tint = palette.primaryText, modifier = Modifier.size(15.dp))
            }
            Box(modifier = Modifier.height(28.dp).clip(RoundedCornerShape(7.dp)).background(palette.surfaceElevated).border(1.dp, palette.border, RoundedCornerShape(7.dp)).clickable { onResetZoom() }.padding(horizontal = 7.dp).testTag("zoom_reset_button"), contentAlignment = Alignment.Center) {
                Text(text = "${(boardZoom * 100).roundToInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = palette.p1Accent)
            }
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(7.dp)).background(palette.surfaceElevated).border(1.dp, palette.border, RoundedCornerShape(7.dp)).clickable { onAdjustZoom(0.1f) }.testTag("zoom_in_button"), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Filled.ZoomIn, contentDescription = "Zoom in board", tint = palette.primaryText, modifier = Modifier.size(15.dp))
            }
        }
    }
}

@Composable
private fun TopGameToolbar(soundEnabled: Boolean, hapticsEnabled: Boolean, claimedBoxes: Int, totalBoxes: Int, onToggleSound: () -> Unit, onToggleHaptics: () -> Unit, onReturnToSetup: () -> Unit) {
    val palette = LocalGamePalette.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        OutlinedButton(onClick = onReturnToSetup, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.primaryText), modifier = Modifier.height(48.dp).testTag("back_to_setup_button")) {
            Icon(imageVector = Icons.Filled.Tune, contentDescription = "Command Center", modifier = Modifier.size(17.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Command Center", style = MaterialTheme.typography.labelLarge)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$claimedBoxes / $totalBoxes nodes", style = MaterialTheme.typography.labelSmall, color = palette.mutedText)
            Spacer(modifier = Modifier.height(3.dp))
            LinearProgressIndicator(
                progress = { if (totalBoxes > 0) claimedBoxes.toFloat() / totalBoxes else 0f },
                modifier = Modifier.width(96.dp).height(5.dp).clip(RoundedCornerShape(50)),
                color = palette.p1Accent,
                trackColor = palette.surfaceElevated
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onToggleHaptics, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(palette.surfaceElevated).border(1.dp, palette.border, RoundedCornerShape(12.dp)).testTag("game_haptics_toggle_button")) {
                Icon(imageVector = Icons.Filled.Vibration, contentDescription = if (hapticsEnabled) "Haptics: On" else "Haptics: Off", tint = if (hapticsEnabled) palette.p1Accent else palette.mutedText)
            }
            IconButton(onClick = onToggleSound, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(palette.surfaceElevated).border(1.dp, palette.border, RoundedCornerShape(12.dp)).testTag("game_sound_toggle_button")) {
                Icon(imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff, contentDescription = "Sound Engine", tint = if (soundEnabled) palette.p3Accent else palette.mutedText)
            }
        }
    }
}

@Composable
private fun ScoreboardHeader(
    boardState: BoardState, activeCampaignLevel: CampaignLevel?, selectedTimerMode: TimerMode,
    blitzRemainingSeconds: Float, blitzRemainingFraction: Float, lastTurnForfeitedByTimeout: Boolean,
    neonGlowPulse: Float, comboHeartbeatScale: Float, modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Card(modifier = modifier.border(1.dp, palette.border, RoundedCornerShape(18.dp)).testTag("scoreboard_card"), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = palette.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (activeCampaignLevel != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(palette.surfaceElevated).padding(horizontal = 10.dp, vertical = 3.dp).testTag("active_campaign_level_banner"),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = palette.streakAccent, modifier = Modifier.size(14.dp))
                        Text(text = "${activeCampaignLevel.title} — ${activeCampaignLevel.subtitle}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.primaryText)
                    }
                    Text(text = "${activeCampaignLevel.gridSize.label} • ${activeCampaignLevel.difficulty.label.uppercase()}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontSize = 10.sp), color = palette.p1Accent)
                }
            }

            val players = boardState.players
            val chunkedPlayers = players.chunked(2)
            for (rowPlayers in chunkedPlayers) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    for (player in rowPlayers) {
                        val isTurn = !boardState.isGameOver && boardState.currentPlayer == player.playerNumber
                        val isComboStreak = isTurn && boardState.isMultiSquareCombo
                        PlayerScorePill(
                            player = player, score = boardState.scoreForPlayer(player.playerNumber),
                            isCurrentTurn = isTurn, isComboStreak = isComboStreak, neonGlowPulse = neonGlowPulse,
                            heartbeatScale = if (isComboStreak) comboHeartbeatScale else 1f,
                            accentColor = playerAccentColor(player.playerNumber), accentFill = playerFillColor(player.playerNumber),
                            testTag = "player${player.playerNumber}_score_card", modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowPlayers.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }

            val showTimerBar = selectedTimerMode.isTimed && !boardState.isGameOver && !boardState.isCurrentPlayerAi
            AnimatedVisibility(visible = showTimerBar) {
                val animatedFraction by animateFloatAsState(targetValue = blitzRemainingFraction.coerceIn(0f, 1f), animationSpec = tween(durationMillis = 80), label = "blitzProgressFraction")
                val barColor = when { animatedFraction > 0.5f -> palette.p1Accent; animatedFraction > 0.25f -> palette.streakAccent; else -> palette.p2Accent }

                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(palette.backgroundDeep).border(1.dp, barColor.copy(alpha = 0.65f), RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 5.dp).testTag("blitz_timer_container"),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Filled.Timer, contentDescription = null, tint = barColor, modifier = Modifier.size(13.dp))
                            Text(text = "TIME LIMIT (${selectedTimerMode.label.uppercase()})", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontSize = 9.5.sp, fontWeight = FontWeight.Bold), color = barColor)
                        }
                        Text(text = String.format(Locale.US, "%.1fs", blitzRemainingSeconds), style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Bold), color = palette.primaryText, modifier = Modifier.testTag("blitz_timer_seconds_text"))
                    }
                    LinearProgressIndicator(progress = { animatedFraction }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).testTag("blitz_timer_progress_bar"), color = barColor, trackColor = palette.surfaceElevated)
                }
            }

            AnimatedVisibility(visible = lastTurnForfeitedByTimeout && !boardState.isGameOver) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(palette.p2Accent.copy(alpha = 0.18f)).border(1.dp, palette.p2Accent, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 3.dp).testTag("blitz_timeout_banner"),
                    horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Time Limit (${selectedTimerMode.label}) Expired — Turn Forfeited to CPU!", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = palette.p2Accent)
                }
            }

            AnimatedVisibility(visible = boardState.hasExtraTurnStreak && !boardState.isGameOver) {
                val activeColor = playerAccentColor(boardState.currentPlayer)
                val activeLabel = if (boardState.isPlayerAi(boardState.currentPlayer)) "CPU" else "USER"
                val badgeText = if (boardState.isMultiSquareCombo) "${boardState.currentComboCount}× NODE CHAIN! EXTRA TURN" else "NODE CAPTURED! EXTRA TURN"

                Row(
                    modifier = Modifier.fillMaxWidth().scale(if (boardState.isMultiSquareCombo) comboHeartbeatScale else 1f).clip(RoundedCornerShape(10.dp)).background(activeColor.copy(alpha = 0.24f)).border(1.5.dp, activeColor, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp).testTag("combo_streak_banner"),
                    horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Bolt, contentDescription = null, tint = palette.streakAccent, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "$badgeText — $activeLabel", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.primaryText)
                }
            }
        }
    }
}

@Composable
private fun PlayerScorePill(
    player: PlayerConfig, score: Int, isCurrentTurn: Boolean, isComboStreak: Boolean,
    neonGlowPulse: Float, heartbeatScale: Float, accentColor: Color, accentFill: Color, testTag: String, modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val borderColor by animateColorAsState(targetValue = if (isCurrentTurn) accentColor.copy(alpha = (0.55f + neonGlowPulse * 0.45f).coerceIn(0f, 1f)) else palette.border.copy(alpha = 0.45f), label = "scoreBorderColor")
    val containerColor by animateColorAsState(targetValue = if (isCurrentTurn) accentFill else palette.surfaceElevated, label = "scoreContainerColor")

    Row(
        modifier = modifier
            .graphicsLayer { scaleX = heartbeatScale; scaleY = heartbeatScale }
            .drawBehind { if (isCurrentTurn) { val glowStrength = if (isComboStreak) 0.42f else 0.24f * neonGlowPulse; drawRoundRect(color = accentColor.copy(alpha = glowStrength), cornerRadius = CornerRadius(14.dp.toPx())) } }
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(width = if (isCurrentTurn) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(9.dp)).background(palette.backgroundDeep).border(1.5.dp, accentColor, RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
                Text(text = if (player.isAi) "AI" else "P${player.playerNumber}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, fontSize = 11.sp), color = accentColor)
            }
            Column {
                Text(text = if (player.isAi) "CPU (MAGENTA)" else "USER P${player.playerNumber}", style = MaterialTheme.typography.labelSmall, color = accentColor)
                Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(if (isCurrentTurn) accentColor else palette.backgroundDeep.copy(alpha = 0.6f)).padding(horizontal = 6.dp, vertical = 1.dp)) {
                    Text(text = when { isCurrentTurn && player.isAi -> "CPU THINKING…"; isCurrentTurn -> "YOUR TURN"; else -> "WAITING" }, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = if (isCurrentTurn) Color.Black else palette.mutedText)
                }
            }
        }
        Text(text = score.toString(), style = MaterialTheme.typography.headlineMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp), color = palette.primaryText, modifier = Modifier.testTag("player${player.playerNumber}_score_value"))
    }
}

@Composable
private fun DotsAndBoxesGrid(
    boardState: BoardState, hoveredEdge: EdgeCoordinate?, neonGlowPulse: Float, comboPulseFraction: Float,
    performanceMode: Boolean = false,
    dotSlotSize: Dp, boxSize: Dp, onEdgeHover: (EdgeCoordinate?) -> Unit, onEdgeClick: (EdgeCoordinate) -> Unit
) {
    val activePlayerColor = playerAccentColor(boardState.currentPlayer)
    Column(modifier = Modifier.testTag("dots_and_boxes_grid"), horizontalAlignment = Alignment.CenterHorizontally) {
        for (dotRow in 0 until boardState.dotRows) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (dotCol in 0 until boardState.dotCols) {
                    DotNode(slotSize = dotSlotSize)
                    if (dotCol < boardState.boxCols) {
                        val edgeCoord = EdgeCoordinate(orientation = EdgeOrientation.HORIZONTAL, row = dotRow, col = dotCol)
                        val claimedBy = boardState.horizontalEdges[dotRow][dotCol]
                        val isBlocked = boardState.blockedEdges.contains(edgeCoord)
                        HorizontalEdgeCell(
                            edge = edgeCoord, claimedBy = claimedBy, isBlocked = isBlocked,
                            isLastClaimed = boardState.lastClaimedEdge == edgeCoord,
                            isHoveredExternal = hoveredEdge == edgeCoord, neonGlowPulse = neonGlowPulse, activePlayerColor = activePlayerColor,
                            width = boxSize, slotHeight = (dotSlotSize * 1.4f).coerceAtLeast(18.dp), onHover = onEdgeHover, onClick = { onEdgeClick(edgeCoord) }
                        )
                    }
                }
            }
            if (dotRow < boardState.boxRows) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (dotCol in 0 until boardState.dotCols) {
                        val vEdgeCoord = EdgeCoordinate(orientation = EdgeOrientation.VERTICAL, row = dotRow, col = dotCol)
                        val claimedBy = boardState.verticalEdges[dotRow][dotCol]
                        val isBlocked = boardState.blockedEdges.contains(vEdgeCoord)
                        VerticalEdgeCell(
                            edge = vEdgeCoord, claimedBy = claimedBy, isBlocked = isBlocked,
                            isLastClaimed = boardState.lastClaimedEdge == vEdgeCoord,
                            isHoveredExternal = hoveredEdge == vEdgeCoord, neonGlowPulse = neonGlowPulse, activePlayerColor = activePlayerColor,
                            slotWidth = dotSlotSize, height = boxSize, onHover = onEdgeHover, onClick = { onEdgeClick(vEdgeCoord) }
                        )
                        if (dotCol < boardState.boxCols) {
                            val boxOwner = boardState.boxes[dotRow][dotCol]
                            val boxCoord = BoxCoordinate(dotRow, dotCol)
                            val isGoldenNode = boardState.goldenNodes.contains(boxCoord)
                            val isToxicNode = boardState.toxicNodes.contains(boxCoord)
                            val isNewlyCaptured = boardState.lastCapturedBoxes.contains(boxCoord)
                            val isInComboChain = boardState.isMultiSquareCombo && boardState.comboChainBoxes.contains(boxCoord)
                            PlayableBoxCell(
                                row = dotRow, col = dotCol, owner = boxOwner,
                                isGoldenNode = isGoldenNode, isToxicNode = isToxicNode,
                                neonGlowPulse = neonGlowPulse, isNewlyCaptured = isNewlyCaptured,
                                isInComboChain = isInComboChain, comboPulseFraction = if (isNewlyCaptured || isInComboChain) comboPulseFraction else 0f,
                                performanceMode = performanceMode, size = boxSize
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
    Box(modifier = Modifier.size(slotSize), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(slotSize).shadow(elevation = 3.dp, shape = CircleShape, spotColor = palette.dotColor).clip(CircleShape).background(palette.dotColor))
    }
}

@Composable
private fun HorizontalEdgeCell(
    edge: EdgeCoordinate, claimedBy: Int, isBlocked: Boolean = false, isLastClaimed: Boolean, isHoveredExternal: Boolean,
    neonGlowPulse: Float, activePlayerColor: Color, width: Dp, slotHeight: Dp,
    onHover: (EdgeCoordinate?) -> Unit, onClick: () -> Unit
) {
    val palette = LocalGamePalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = !isBlocked && claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed, isBlocked) { if (!isBlocked && (isHovered || isPressed)) onHover(edge) }

    val ownerColor = if (claimedBy != 0) playerAccentColor(claimedBy) else Color.Transparent
    val trackColor by animateColorAsState(targetValue = if (showHover) activePlayerColor.copy(alpha = 0.65f) else palette.idleEdge, animationSpec = tween(durationMillis = 180), label = "hEdgeTrackColor")
    val drawProgress by animateFloatAsState(targetValue = if (claimedBy != 0) 1f else 0f, animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing), label = "hEdgeDrawProgress")
    val barThickness = 6.dp

    Box(
        modifier = Modifier
            .width(width)
            .height(slotHeight)
            .then(
                if (!isBlocked) {
                    Modifier
                        .hoverable(interactionSource = interactionSource)
                        .then(if (claimedBy == 0) Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick) else Modifier)
                } else {
                    Modifier
                }
            )
            .semantics { contentDescription = if (isBlocked) "Blocked horizontal line row ${edge.row} column ${edge.col}" else "Horizontal line row ${edge.row} column ${edge.col}" }
            .testTag("h_edge_${edge.row}_${edge.col}"),
        contentAlignment = Alignment.Center
    ) {
        if (isBlocked) {
            Canvas(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp).height(barThickness)) {
                drawLine(
                    color = Color(0xFFEF4444).copy(alpha = 0.3f),
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = barThickness.toPx() * 0.75f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp).height(barThickness).clip(RoundedCornerShape(50)).background(trackColor), contentAlignment = Alignment.CenterStart) {
                if (claimedBy != 0 || drawProgress > 0.01f) {
                    Box(
                        modifier = Modifier.fillMaxWidth(drawProgress.coerceIn(0f, 1f)).height(barThickness)
                            .drawBehind { drawRoundRect(color = ownerColor.copy(alpha = 0.45f * neonGlowPulse), size = size.copy(height = size.height * 2.2f), topLeft = Offset(0f, -size.height * 0.6f)) }
                            .clip(RoundedCornerShape(50)).background(ownerColor).then(if (isLastClaimed) Modifier.border(1.dp, palette.primaryText, RoundedCornerShape(50)) else Modifier)
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalEdgeCell(
    edge: EdgeCoordinate, claimedBy: Int, isBlocked: Boolean = false, isLastClaimed: Boolean, isHoveredExternal: Boolean,
    neonGlowPulse: Float, activePlayerColor: Color, slotWidth: Dp, height: Dp,
    onHover: (EdgeCoordinate?) -> Unit, onClick: () -> Unit
) {
    val palette = LocalGamePalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val showHover = !isBlocked && claimedBy == 0 && (isHovered || isPressed || isHoveredExternal)
    LaunchedEffect(isHovered, isPressed, isBlocked) { if (!isBlocked && (isHovered || isPressed)) onHover(edge) }

    val ownerColor = if (claimedBy != 0) playerAccentColor(claimedBy) else Color.Transparent
    val trackColor by animateColorAsState(targetValue = if (showHover) activePlayerColor.copy(alpha = 0.65f) else palette.idleEdge, animationSpec = tween(durationMillis = 180), label = "vEdgeTrackColor")
    val drawProgress by animateFloatAsState(targetValue = if (claimedBy != 0) 1f else 0f, animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing), label = "vEdgeDrawProgress")
    val barThickness = 6.dp

    Box(
        modifier = Modifier
            .width(slotWidth)
            .height(height)
            .then(
                if (!isBlocked) {
                    Modifier
                        .hoverable(interactionSource = interactionSource)
                        .then(if (claimedBy == 0) Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick) else Modifier)
                } else {
                    Modifier
                }
            )
            .semantics { contentDescription = if (isBlocked) "Blocked vertical line row ${edge.row} column ${edge.col}" else "Vertical line row ${edge.row} column ${edge.col}" }
            .testTag("v_edge_${edge.row}_${edge.col}"),
        contentAlignment = Alignment.Center
    ) {
        if (isBlocked) {
            Canvas(modifier = Modifier.fillMaxHeight().padding(vertical = 2.dp).width(barThickness)) {
                drawLine(
                    color = Color(0xFFEF4444).copy(alpha = 0.3f),
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height),
                    strokeWidth = barThickness.toPx() * 0.75f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxHeight().padding(vertical = 1.dp).width(barThickness).clip(RoundedCornerShape(50)).background(trackColor), contentAlignment = Alignment.TopCenter) {
                if (claimedBy != 0 || drawProgress > 0.01f) {
                    Box(
                        modifier = Modifier.fillMaxHeight(drawProgress.coerceIn(0f, 1f)).width(barThickness)
                            .drawBehind { drawRoundRect(color = ownerColor.copy(alpha = 0.45f * neonGlowPulse), size = size.copy(width = size.width * 2.2f), topLeft = Offset(-size.width * 0.6f, 0f)) }
                            .clip(RoundedCornerShape(50)).background(ownerColor).then(if (isLastClaimed) Modifier.border(1.dp, palette.primaryText, RoundedCornerShape(50)) else Modifier)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayableBoxCell(
    row: Int, col: Int, owner: Int,
    isGoldenNode: Boolean = false, isToxicNode: Boolean = false,
    neonGlowPulse: Float = 0.8f,
    isNewlyCaptured: Boolean, isInComboChain: Boolean, comboPulseFraction: Float,
    performanceMode: Boolean = false, size: Dp
) {
    val palette = LocalGamePalette.current
    val solarFlareGold = ThemeColor.SOLAR_FLARE.primary
    val toxicRed = Color(0xFFEF4444)
    val toxicGreen = Color(0xFF00E676)
    val toxicPulseColor = if (neonGlowPulse >= 0.78f) toxicRed else toxicGreen
    val accentColor = if (owner != 0) playerAccentColor(owner) else Color.Transparent
    val targetFillColor = when {
        owner != 0 && isGoldenNode -> solarFlareGold.copy(alpha = 0.28f)
        owner != 0 && isToxicNode -> toxicRed.copy(alpha = 0.30f)
        owner != 0 -> accentColor.copy(alpha = 0.40f)
        isGoldenNode -> solarFlareGold.copy(alpha = (0.08f + 0.12f * neonGlowPulse).coerceIn(0f, 1f))
        isToxicNode -> toxicPulseColor.copy(alpha = (0.08f + 0.14f * neonGlowPulse).coerceIn(0f, 1f))
        else -> Color.Transparent
    }
    val boxFillColor by animateColorAsState(targetValue = targetFillColor, animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing), label = "boxFillColorAnim")

    val singleCapturePulse = remember { Animatable(1f) }
    val charJumpScale = remember { Animatable(0.2f) }
    val floatingDeltaOffset = remember { Animatable(0f) }
    LaunchedEffect(owner) {
        if (owner != 0) {
            launch {
                singleCapturePulse.snapTo(0.85f)
                singleCapturePulse.animateTo(targetValue = 1.16f, animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing))
                singleCapturePulse.animateTo(targetValue = 1.0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
            }
            launch {
                floatingDeltaOffset.snapTo(12f)
                floatingDeltaOffset.animateTo(targetValue = -4f, animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing))
                floatingDeltaOffset.animateTo(targetValue = 0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            }
            charJumpScale.snapTo(0.25f)
            charJumpScale.animateTo(targetValue = 1.75f, animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing))
            charJumpScale.animateTo(targetValue = 1.0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        } else {
            singleCapturePulse.snapTo(1f)
            charJumpScale.snapTo(0.2f)
            floatingDeltaOffset.snapTo(0f)
        }
    }

    val comboBoxScale = if (isNewlyCaptured || isInComboChain) singleCapturePulse.value * (1f + (comboPulseFraction * 0.12f)) else singleCapturePulse.value

    Box(
        modifier = Modifier
            .size(size)
            .padding(1.5.dp)
            .graphicsLayer { scaleX = comboBoxScale; scaleY = comboBoxScale }
            .clip(RoundedCornerShape(6.dp))
            .background(boxFillColor)
            .then(
                when {
                    isGoldenNode && owner == 0 -> Modifier.border(
                        width = 1.5.dp,
                        color = solarFlareGold.copy(alpha = (0.45f + 0.55f * neonGlowPulse).coerceIn(0f, 1f)),
                        shape = RoundedCornerShape(6.dp)
                    )
                    isToxicNode && owner == 0 -> Modifier.border(
                        width = 1.5.dp,
                        color = toxicPulseColor.copy(alpha = (0.45f + 0.55f * neonGlowPulse).coerceIn(0f, 1f)),
                        shape = RoundedCornerShape(6.dp)
                    )
                    (isNewlyCaptured || isInComboChain) && owner != 0 -> Modifier.border(
                        width = if (isInComboChain) 2.dp else 1.5.dp,
                        color = when {
                            isGoldenNode -> solarFlareGold
                            isToxicNode -> toxicRed
                            comboPulseFraction > 0.2f -> palette.primaryText
                            else -> accentColor
                        },
                        shape = RoundedCornerShape(6.dp)
                    )
                    isGoldenNode && owner != 0 -> Modifier.border(
                        width = 1.5.dp,
                        color = solarFlareGold.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    isToxicNode && owner != 0 -> Modifier.border(
                        width = 1.5.dp,
                        color = toxicRed.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    else -> Modifier
                }
            )
            .testTag("box_${row}_${col}"),
        contentAlignment = Alignment.Center
    ) {
        if (!performanceMode) {
            if (isNewlyCaptured && comboPulseFraction > 0.02f && owner != 0) {
                val particleProgress = 1f - comboPulseFraction
                val particleColor = when {
                    isGoldenNode -> solarFlareGold
                    isToxicNode -> toxicRed
                    else -> accentColor
                }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(this.size.width / 2f, this.size.height / 2f)
                    val maxRadius = this.size.maxDimension * 0.85f
                    val particleCount = 12
                    for (i in 0 until particleCount) {
                        val angle = (2.0 * PI * i) / particleCount
                        val distance = maxRadius * particleProgress
                        val px = center.x + (cos(angle) * distance).toFloat()
                        val py = center.y + (sin(angle) * distance).toFloat()
                        drawCircle(color = particleColor.copy(alpha = comboPulseFraction), radius = (3.5.dp.toPx() * comboPulseFraction).coerceAtLeast(1f), center = Offset(px, py))
                    }
                }
            }
        }

        if (owner == 0 && isGoldenNode) {
            Text(
                text = "★",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = when { size >= 44.dp -> 13.sp; size >= 28.dp -> 10.sp; else -> 8.sp },
                    shadow = Shadow(color = solarFlareGold, offset = Offset.Zero, blurRadius = 12f)
                ),
                color = solarFlareGold.copy(alpha = (0.55f + 0.45f * neonGlowPulse).coerceIn(0f, 1f))
            )
        } else if (owner == 0 && isToxicNode) {
            Text(
                text = "☣",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = when { size >= 44.dp -> 13.sp; size >= 28.dp -> 10.sp; else -> 8.sp },
                    shadow = Shadow(color = toxicPulseColor, offset = Offset.Zero, blurRadius = 12f)
                ),
                color = toxicPulseColor.copy(alpha = (0.55f + 0.45f * neonGlowPulse).coerceIn(0f, 1f))
            )
        } else if (owner != 0) {
            when {
                isGoldenNode -> {
                    Text(
                        text = "+2",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = when { size >= 44.dp -> 16.sp; size >= 28.dp -> 13.sp; else -> 10.sp },
                            shadow = Shadow(color = solarFlareGold, offset = Offset.Zero, blurRadius = 16f)
                        ),
                        color = solarFlareGold,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = charJumpScale.value
                                scaleY = charJumpScale.value
                                translationY = floatingDeltaOffset.value
                            }
                            .testTag("golden_bonus_${row}_${col}")
                    )
                }
                isToxicNode -> {
                    Text(
                        text = "-1",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = when { size >= 44.dp -> 16.sp; size >= 28.dp -> 13.sp; else -> 10.sp },
                            shadow = Shadow(color = toxicRed, offset = Offset.Zero, blurRadius = 16f)
                        ),
                        color = toxicRed,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = charJumpScale.value
                                scaleY = charJumpScale.value
                                translationY = floatingDeltaOffset.value
                            }
                            .testTag("toxic_penalty_${row}_${col}")
                    )
                }
                else -> {
                    Text(
                        text = if (owner == 1) "P1" else if (owner == 2 && playerAccentColor(2) == ProtocolMagenta) "CPU" else "P$owner",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = when { size >= 44.dp -> 15.sp; size >= 28.dp -> 12.sp; else -> 9.sp },
                            shadow = Shadow(color = accentColor, offset = Offset.Zero, blurRadius = 14f)
                        ),
                        color = palette.primaryText,
                        modifier = Modifier.scale(charJumpScale.value)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameOverBanner(
    boardState: BoardState, activeCampaignLevel: CampaignLevel?, justUnlockedNextCampaignLevel: Boolean,
    onAdvanceToNextCampaignLevel: () -> Unit, onPlayAgain: () -> Unit, modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val winner = boardState.winner
    val accentColor = if (winner != null && winner > 0) playerAccentColor(winner) else palette.streakAccent
    val titleText = if (winner != null && winner > 0) {
        val winnerName = if (boardState.isPlayerAi(winner)) "CPU" else "Player $winner"
        "$winnerName Wins with ${boardState.scoreForPlayer(winner)} Nodes!"
    } else {
        "Match Drawn — Tied at ${boardState.maxScore}!"
    }
    
    val summaryScores = boardState.players.joinToString("  •  ") { p -> 
        val name = if (p.isAi) "CPU" else "P${p.playerNumber}"
        "$name: ${boardState.scoreForPlayer(p.playerNumber)}" 
    }
    val nextLevel = activeCampaignLevel?.nextLevel()
    val canAdvanceCampaign = activeCampaignLevel != null && winner == 1 && nextLevel != null

    Card(modifier = modifier.border(2.dp, accentColor, RoundedCornerShape(16.dp)).testTag("game_over_banner"), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = palette.surfaceElevated)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
                    Column {
                        Text(text = titleText, style = MaterialTheme.typography.titleMedium, color = palette.primaryText)
                        Text(text = summaryScores, style = MaterialTheme.typography.labelSmall, color = accentColor)
                        if (justUnlockedNextCampaignLevel && nextLevel != null) {
                            Text(text = "NEW STAGE UNLOCKED: LEVEL ${nextLevel.levelNumber}!", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = palette.streakAccent, modifier = Modifier.testTag("campaign_unlocked_notice"))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (canAdvanceCampaign) {
                        Button(onClick = onAdvanceToNextCampaignLevel, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = palette.streakAccent, contentColor = Color.Black), modifier = Modifier.testTag("next_campaign_level_button")) {
                            Text(text = "Next Level", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Button(onClick = onPlayAgain, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.Black), modifier = Modifier.testTag("play_again_button")) {
                        Text(text = "Play Again", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

private data class ConfettiParticleSpec(val angleRad: Float, val speedFactor: Float, val startXFraction: Float, val fallSpeed: Float, val driftAmplitude: Float, val rotationSpeed: Float, val sizeDp: Float, val colorIndex: Int)

@Composable
private fun FullScreenVictoryAnimationOverlay(
    boardState: BoardState, triggerId: Int, performanceMode: Boolean = false,
    isRankedMatch: Boolean = false, lastRpChange: Int? = null,
    activeCampaignLevel: CampaignLevel?, justUnlockedNextCampaignLevel: Boolean,
    onAdvanceToNextCampaignLevel: () -> Unit, onPlayAgain: () -> Unit, onReturnToSetup: () -> Unit
) {
    val palette = LocalGamePalette.current
    val winner = boardState.winner
    val winnerAccent = if (winner != null && winner > 0) playerAccentColor(winner) else palette.streakAccent
    var isCardDismissed by remember(triggerId, boardState.isGameOver) { mutableStateOf(false) }

    val matchEndTimeMs = remember(triggerId, boardState.isGameOver) { System.currentTimeMillis() }
    val durationTotalSeconds = ((matchEndTimeMs - boardState.matchStartTimeMs).coerceAtLeast(0L) / 1000L).toInt()
    val durationMinutes = durationTotalSeconds / 60
    val durationSeconds = durationTotalSeconds % 60

    val burstProgress = remember(triggerId) { Animatable(0f) }
    val glowPulseTransition = rememberInfiniteTransition(label = "victoryGlowTransition")
    val glowPulse by glowPulseTransition.animateFloat(initialValue = 0.45f, targetValue = 1.0f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 900, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "victoryGlowPulse")
    val continuousFallPhase by glowPulseTransition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Restart), label = "victoryContinuousFall")

    LaunchedEffect(triggerId, boardState.isGameOver) {
        if (boardState.isGameOver) {
            burstProgress.snapTo(0f)
            burstProgress.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing))
        }
    }

    val particles = remember(performanceMode) {
        if (performanceMode) {
            emptyList()
        } else {
            List(76) { idx ->
                val goldenAngle = idx * 2.3999632f
                ConfettiParticleSpec(angleRad = goldenAngle, speedFactor = 0.25f + ((idx * 37) % 100) / 100f * 0.85f, startXFraction = ((idx * 19) % 100) / 100f, fallSpeed = 0.4f + ((idx * 29) % 70) / 100f, driftAmplitude = 18f + (idx % 5) * 9f, rotationSpeed = (idx % 7 - 3) * 120f, sizeDp = 5f + (idx % 4) * 2.5f, colorIndex = idx % 5)
            }
        }
    }
    val confettiColors = listOf(palette.p1Accent, palette.p2Accent, palette.streakAccent, palette.p3Accent, palette.p4Accent)

    Box(modifier = Modifier.fillMaxSize().testTag("victory_confetti_overlay"), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("victory_confetti_canvas")) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val maxDim = size.maxDimension
            drawCircle(brush = Brush.radialGradient(colors = listOf(winnerAccent.copy(alpha = 0.28f * glowPulse), palette.streakAccent.copy(alpha = 0.12f * glowPulse), Color.Transparent), center = center, radius = maxDim * (0.48f + 0.14f * glowPulse)), center = center, radius = maxDim * 0.65f)

            val ringProgress = burstProgress.value
            if (ringProgress < 0.98f) {
                drawCircle(color = winnerAccent.copy(alpha = (1f - ringProgress) * 0.55f), center = center, radius = maxDim * 0.55f * ringProgress, style = Stroke(width = 4.dp.toPx()))
            }

            val t = burstProgress.value
            for (p in particles) {
                val cColor = confettiColors[p.colorIndex]
                val burstRadius = maxDim * 0.52f * p.speedFactor * sin(t * (PI / 2.0)).toFloat()
                val gravityY = (t * t) * size.height * 0.55f * p.fallSpeed + continuousFallPhase * size.height * 0.22f
                val swayX = sin((t * 6f + p.startXFraction * 10f).toDouble()).toFloat() * p.driftAmplitude
                val px = (center.x + cos(p.angleRad) * burstRadius + swayX).coerceIn(0f, size.width)
                val py = ((center.y + sin(p.angleRad) * burstRadius * 0.7f + gravityY) % size.height).let { if (it < 0f) it + size.height else it }
                val particleAlpha = (0.92f - t * 0.25f).coerceIn(0.35f, 0.95f)
                val rectW = p.sizeDp.dp.toPx()
                val rectH = (p.sizeDp * 0.58f).dp.toPx()

                rotate(degrees = (t * p.rotationSpeed + continuousFallPhase * 180f) % 360f, pivot = Offset(px, py)) {
                    drawRect(color = cColor.copy(alpha = particleAlpha), topLeft = Offset(px - rectW / 2f, py - rectH / 2f), size = Size(rectW, rectH))
                }
            }
        }

        AnimatedVisibility(visible = !isCardDismissed, enter = fadeIn(tween(240)) + scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow))) {
            val headlineText = when {
                winner == null || winner == 0 -> "Match Drawn — Tied at ${boardState.maxScore}!"
                boardState.isPlayerAi(winner) -> "CPU WINS (${boardState.scoreForPlayer(winner)} NODES)"
                else -> "PLAYER $winner VICTORY! (${boardState.scoreForPlayer(winner)} NODES)"
            }
            val nextLevel = activeCampaignLevel?.nextLevel()
            val canAdvanceCampaign = activeCampaignLevel != null && winner == 1 && nextLevel != null

            Card(
                modifier = Modifier.widthIn(max = 380.dp).padding(horizontal = 20.dp).shadow(24.dp, RoundedCornerShape(24.dp), spotColor = winnerAccent).border(2.5.dp, winnerAccent, RoundedCornerShape(24.dp)).testTag("victory_celebration_modal"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = palette.backgroundDeep.copy(alpha = 0.94f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(56.dp).scale(0.95f + glowPulse * 0.12f).clip(CircleShape).background(winnerAccent.copy(alpha = 0.22f)).border(2.dp, winnerAccent, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = winnerAccent, modifier = Modifier.size(32.dp))
                    }
                    Text(text = "PROTOCOL COMPLETE • WINNER DECLARED", style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold), color = palette.streakAccent, textAlign = TextAlign.Center)
                    Text(text = headlineText, style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Black, shadow = Shadow(color = winnerAccent, offset = Offset.Zero, blurRadius = 18f)), color = palette.primaryText, textAlign = TextAlign.Center)

                    val scoreBreakdown = boardState.players.joinToString("   •   ") { p -> 
                        val name = if (p.isAi) "CPU" else "P${p.playerNumber}"
                        "$name: ${boardState.scoreForPlayer(p.playerNumber)}" 
                    }
                    Text(text = scoreBreakdown, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMonoFontFamily, fontWeight = FontWeight.Bold), color = winnerAccent, textAlign = TextAlign.Center)

                    // ADVANCED TELEMETRY SECTION (v3.0.0)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceElevated)
                            .border(1.dp, winnerAccent.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("victory_advanced_telemetry_section"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ADVANCED TELEMETRY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = palette.streakAccent
                        )
                        Text(
                            text = "Match Duration: ${durationMinutes}m ${durationSeconds}s",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                            color = palette.primaryText,
                            modifier = Modifier.testTag("telemetry_match_duration")
                        )
                        Text(
                            text = "Longest Combo: ${boardState.longestComboChain} Nodes",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFontFamily),
                            color = palette.primaryText,
                            modifier = Modifier.testTag("telemetry_longest_combo")
                        )
                        if (isRankedMatch) {
                            val rpChangeVal = lastRpChange ?: if (winner == 1) 25 else -15
                            val rpFormatted = if (rpChangeVal >= 0) "+$rpChangeVal" else "$rpChangeVal"
                            val rpColor = if (rpChangeVal >= 0) ProtocolLime else ProtocolMagenta
                            Text(
                                text = "RP Change: $rpFormatted",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Black
                                ),
                                color = rpColor,
                                modifier = Modifier.testTag("telemetry_rp_change")
                            )
                        }
                    }

                    if (activeCampaignLevel != null) {
                        val campaignStatusText = when {
                            winner == 1 && nextLevel == null -> "CHAMPION! ALL 6 CAMPAIGN STAGES CLEARED!"
                            justUnlockedNextCampaignLevel && nextLevel != null -> "NEW STAGE UNLOCKED: LEVEL ${nextLevel.levelNumber}!"
                            winner == 1 && nextLevel != null -> "Level ${activeCampaignLevel.levelNumber} Cleared!"
                            else -> "Beat the CPU on ${activeCampaignLevel.title} to unlock the next stage!"
                        }
                        Text(text = campaignStatusText, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = palette.streakAccent, textAlign = TextAlign.Center)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (canAdvanceCampaign) {
                            Button(onClick = onAdvanceToNextCampaignLevel, modifier = Modifier.weight(1f).testTag("victory_next_level_button"), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = palette.streakAccent, contentColor = Color.Black)) {
                                Text(text = "Next Level", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                        Button(onClick = onPlayAgain, modifier = Modifier.weight(1f).testTag("victory_play_again_button"), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = winnerAccent, contentColor = Color.Black)) {
                            Text(text = "Play Again", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onReturnToSetup, modifier = Modifier.testTag("victory_main_menu_button")) { Text(text = "Command Center", color = palette.mutedText) }
                        TextButton(onClick = { isCardDismissed = true }, modifier = Modifier.testTag("dismiss_victory_overlay_button")) { Text(text = "Inspect Grid", color = palette.primaryText) }
                    }
                }
            }
        }
    }
}
