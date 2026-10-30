package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGamePalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

/**
 * Subtle, infinite animation of faint cyan and magenta dots drifting slowly
 * across the dark arcade screen.
 */
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

            // Faint outer neon halo
            drawCircle(
                color = dotColor.copy(alpha = alpha * 0.38f),
                radius = radiusPx * 2.4f,
                center = Offset(px, py)
            )
            // Core drifting dot
            drawCircle(
                color = dotColor.copy(alpha = alpha),
                radius = radiusPx,
                center = Offset(px, py)
            )
        }
    }
}

/**
 * Credits Screen (Accessible via Info Button in Main Menu Settings Row):
 * - Sleek, scrolling screen with a glowing neon border.
 * - Includes centered arcade-styled sections:
 *   1. "Design & Development: [Your Name/Handle]"
 *   2. "AI Co-Pilot & Logic: Gemini"
 *   3. "Framework: Kotlin & Jetpack Compose via Google AI Studio"
 *   4. "Build Pipeline: Cloud CI/CD Assembly"
 * - "Back" button to return to the dashboard.
 */
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        palette.backgroundRadialTop,
                        palette.backgroundDeep
                    )
                )
            )
            .testTag("credits_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle drifting cyan and magenta dots in the background
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
                    // Glowing Arcade Header Badge
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

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.credits_title),
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
                        Text(
                            text = stringResource(R.string.credits_subtitle),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                letterSpacing = 1.4.sp
                            ),
                            color = palette.p2Accent,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 4 Required Centered Arcade Credits Sections
                    CreditSectionCard(
                        icon = Icons.Filled.Code,
                        text = stringResource(R.string.credits_design_dev),
                        accentColor = palette.p1Accent,
                        testTag = "credits_design_dev_text"
                    )

                    CreditSectionCard(
                        icon = Icons.Filled.Memory,
                        text = stringResource(R.string.credits_ai_copilot),
                        accentColor = palette.p2Accent,
                        testTag = "credits_ai_copilot_text"
                    )

                    CreditSectionCard(
                        icon = Icons.Filled.AutoAwesome,
                        text = stringResource(R.string.credits_framework),
                        accentColor = palette.p3Accent,
                        testTag = "credits_framework_text"
                    )

                    CreditSectionCard(
                        icon = Icons.Filled.CloudDone,
                        text = stringResource(R.string.credits_build_pipeline),
                        accentColor = palette.streakAccent,
                        testTag = "credits_build_pipeline_text"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Back Button to return to the Neon Arcade Dashboard
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
                                contentDescription = stringResource(R.string.credits_back_button),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.credits_back_button).uppercase(),
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
