package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Lightweight procedural audio synthesizer (Web Audio API equivalent for Android)
 * generating 16-bit PCM waveforms in-memory via [AudioTrack]:
 * - [playLineClick]: Subtle crisp frequency-swept click when a line is drawn
 * - [playBoxPop]: Harmonic pitch-scaled 'pop' sound when a box is captured
 * - [playGameEndFanfare]: Celebratory C-major arpeggio when the match ends
 */
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
                // C5 (523.25Hz), E5 (659.25Hz), G5 (783.99Hz), C6 (1046.50Hz)
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

            // Attack + exponential decay envelope to prevent speaker clicks
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
