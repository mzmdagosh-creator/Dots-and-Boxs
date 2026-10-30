package com.example.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Hardware Haptics Engine combining Android's `Vibrator` / `VibratorManager`
 * and Jetpack Compose `LocalHapticFeedback`:
 * - `triggerLineTick`: Subtle crisp haptic "tick" when a line is drawn.
 * - `triggerBoxCaptureBuzz`: Heavier, distinct haptic "buzz" when one or more boxes are captured.
 * - `triggerVictoryCelebration`: Celebratory multi-pulse vibration when a match ends.
 */
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

    /**
     * Subtle haptic "tick" when a line is drawn.
     */
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

    /**
     * Heavier, distinct haptic "buzz" when a box is successfully captured.
     */
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

    /**
     * Celebratory haptic pulse when the match ends and a winner is declared.
     */
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
