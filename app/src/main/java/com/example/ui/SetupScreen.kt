package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CpuDifficulty
import com.example.model.GameMode
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalGamePalette
import com.example.viewmodel.PlayerSessionStats

@Composable
fun SetupScreen(
    themeMode: AppThemeMode,
    soundEnabled: Boolean,
    selectedGameMode: GameMode,
    selectedCpuDifficulty: CpuDifficulty,
    selectedPlayerCount: Int,
    playerInputs: List<String>,
    sessionStats: Map<Int, PlayerSessionStats>,
    errorResId: Int?,
    onSelectThemeMode: (AppThemeMode) -> Unit,
    onSetSoundEnabled: (Boolean) -> Unit,
    onSelectGameMode: (GameMode) -> Unit,
    onSelectCpuDifficulty: (CpuDifficulty) -> Unit,
    onSelectPlayerCount: (Int) -> Unit,
    onPlayerInputChange: (Int, String) -> Unit,
    onStartGame: () -> Unit,
    onOpenHtmlViewer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    val scrollState = rememberScrollState()
    val isCpuMode = selectedGameMode == GameMode.CPU

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
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 580.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, palette.border, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_hero_dots_boxes),
                            contentDescription = stringResource(R.string.hero_banner_desc),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(50))
                                .background(palette.backgroundDeep.copy(alpha = 0.88f))
                                .border(1.dp, palette.border, RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.GridOn,
                                    contentDescription = null,
                                    tint = palette.p1Accent,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "5×9 DOTS • PVP & CPU",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.primaryText
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.setup_title),
                            style = MaterialTheme.typography.displayLarge,
                            color = palette.primaryText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.setup_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.mutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Settings & Match Setup Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, palette.border, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.setup_card_header),
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.primaryText
                    )

                    // Theme Picker ('Classic Dark' vs 'High Contrast') & Sound Engine Toggle
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.theme_picker_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.mutedText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModeTogglePill(
                                title = stringResource(R.string.theme_classic_dark),
                                icon = Icons.Filled.DarkMode,
                                isSelected = themeMode == AppThemeMode.CLASSIC_DARK,
                                accentColor = palette.p1Accent,
                                testTag = "theme_classic_dark_button",
                                onClick = { onSelectThemeMode(AppThemeMode.CLASSIC_DARK) },
                                modifier = Modifier.weight(1f)
                            )

                            ModeTogglePill(
                                title = stringResource(R.string.theme_high_contrast),
                                icon = Icons.Filled.Contrast,
                                isSelected = themeMode == AppThemeMode.HIGH_CONTRAST,
                                accentColor = palette.p2Accent,
                                testTag = "theme_high_contrast_button",
                                onClick = { onSelectThemeMode(AppThemeMode.HIGH_CONTRAST) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = stringResource(R.string.sound_effects_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.mutedText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModeTogglePill(
                                title = stringResource(R.string.sound_on),
                                icon = Icons.Filled.VolumeUp,
                                isSelected = soundEnabled,
                                accentColor = palette.p3Accent,
                                testTag = "sound_on_button",
                                onClick = { onSetSoundEnabled(true) },
                                modifier = Modifier.weight(1f)
                            )

                            ModeTogglePill(
                                title = stringResource(R.string.sound_muted),
                                icon = Icons.Filled.VolumeOff,
                                isSelected = !soundEnabled,
                                accentColor = palette.p2Accent,
                                testTag = "sound_mute_button",
                                onClick = { onSetSoundEnabled(false) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 1. Game Mode Segmented Control: Player vs Player or Player vs Computer (CPU)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.game_mode_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.mutedText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ModeTogglePill(
                                title = stringResource(R.string.mode_pvp_button),
                                icon = Icons.Filled.Group,
                                isSelected = !isCpuMode,
                                accentColor = palette.p1Accent,
                                testTag = "mode_pvp_button",
                                onClick = { onSelectGameMode(GameMode.PVP) },
                                modifier = Modifier.weight(1f)
                            )

                            ModeTogglePill(
                                title = stringResource(R.string.mode_cpu_button),
                                icon = Icons.Filled.SmartToy,
                                isSelected = isCpuMode,
                                accentColor = palette.p2Accent,
                                testTag = "mode_cpu_button",
                                onClick = { onSelectGameMode(GameMode.CPU) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 2A. CPU Difficulty Segmented Control
                    AnimatedVisibility(visible = isCpuMode) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.cpu_difficulty_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.mutedText
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CpuDifficulty.entries.forEach { diff ->
                                    val isSelected = selectedCpuDifficulty == diff
                                    val diffAccent = when (diff) {
                                        CpuDifficulty.EASY -> palette.p3Accent
                                        CpuDifficulty.MEDIUM -> palette.streakAccent
                                        CpuDifficulty.HARD -> palette.p2Accent
                                    }
                                    val diffLabel = when (diff) {
                                        CpuDifficulty.EASY -> stringResource(R.string.diff_easy)
                                        CpuDifficulty.MEDIUM -> stringResource(R.string.diff_medium)
                                        CpuDifficulty.HARD -> stringResource(R.string.diff_hard)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (isSelected) diffAccent else palette.surfaceElevated
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) diffAccent else palette.border,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable { onSelectCpuDifficulty(diff) }
                                            .testTag("cpu_diff_${diff.name.lowercase()}_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = diffLabel,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) Color.White else palette.primaryText
                                        )
                                    }
                                }
                            }

                            val diffDescription = when (selectedCpuDifficulty) {
                                CpuDifficulty.EASY -> stringResource(R.string.diff_easy_desc)
                                CpuDifficulty.MEDIUM -> stringResource(R.string.diff_medium_desc)
                                CpuDifficulty.HARD -> stringResource(R.string.diff_hard_desc)
                            }
                            Text(
                                text = diffDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.mutedText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // 2B. PvP Player Count Selector
                    AnimatedVisibility(visible = !isCpuMode) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.player_count_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.mutedText
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (count in 2..4) {
                                    val isSelected = selectedPlayerCount == count
                                    val pillAccent = when (count) {
                                        2 -> palette.p1Accent
                                        3 -> palette.p3Accent
                                        else -> palette.p4Accent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (isSelected) pillAccent else palette.surfaceElevated
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) pillAccent else palette.border,
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            .clickable { onSelectPlayerCount(count) }
                                            .testTag("player_count_${count}_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$count Players",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) Color.White else palette.primaryText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Row 1: Player 1 and Player 2 (or Locked CPU in CPU Mode)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlayerInitialInputCard(
                            label = stringResource(R.string.player_one_label),
                            value = playerInputs.getOrElse(0) { "A" },
                            isLocked = false,
                            accentColor = palette.p1Accent,
                            accentFill = palette.p1Fill,
                            quickOptions = listOf("A", "X", "1", "P"),
                            testTag = "player1_letter_input",
                            onValueChange = { onPlayerInputChange(0, it) },
                            modifier = Modifier.weight(1f)
                        )

                        PlayerInitialInputCard(
                            label = if (isCpuMode) {
                                stringResource(R.string.player_two_ai_label)
                            } else {
                                stringResource(R.string.player_two_label)
                            },
                            value = playerInputs.getOrElse(1) { if (isCpuMode) "C" else "B" },
                            isLocked = isCpuMode,
                            accentColor = palette.p2Accent,
                            accentFill = palette.p2Fill,
                            quickOptions = if (isCpuMode) emptyList() else listOf("B", "O", "2", "Z"),
                            testTag = "player2_letter_input",
                            onValueChange = { onPlayerInputChange(1, it) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Player 3 and Player 4 (shown when PvP 3P or 4P is selected)
                    AnimatedVisibility(visible = !isCpuMode && selectedPlayerCount >= 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PlayerInitialInputCard(
                                label = stringResource(R.string.player_three_label),
                                value = playerInputs.getOrElse(2) { "D" },
                                isLocked = false,
                                accentColor = palette.p3Accent,
                                accentFill = palette.p3Fill,
                                quickOptions = listOf("D", "M", "3", "E"),
                                testTag = "player3_letter_input",
                                onValueChange = { onPlayerInputChange(2, it) },
                                modifier = Modifier.weight(1f)
                            )

                            if (selectedPlayerCount == 4) {
                                PlayerInitialInputCard(
                                    label = stringResource(R.string.player_four_label),
                                    value = playerInputs.getOrElse(3) { "E" },
                                    isLocked = false,
                                    accentColor = palette.p4Accent,
                                    accentFill = palette.p4Fill,
                                    quickOptions = listOf("E", "W", "4", "V"),
                                    testTag = "player4_letter_input",
                                    onValueChange = { onPlayerInputChange(3, it) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    AnimatedVisibility(visible = errorResId != null) {
                        if (errorResId != null) {
                            Text(
                                text = stringResource(id = errorResId),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_error_text")
                            )
                        }
                    }

                    Button(
                        onClick = onStartGame,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_game_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = palette.p1Accent,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.start_game_button),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Persistent Session Stats & Standalone Web HTML Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, palette.border, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val activeCount = if (isCpuMode) 2 else selectedPlayerCount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.session_stats_title).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = palette.mutedText
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (pNum in 1..activeCount) {
                                val stat = sessionStats[pNum] ?: PlayerSessionStats()
                                val label = if (isCpuMode && pNum == 2) "CPU" else "P$pNum"
                                Text(
                                    text = "$label: ${stat.wins}W-${stat.losses}L",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = palette.primaryText
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = palette.p1Accent,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.rules_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.mutedText
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenHtmlViewer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_html_viewer_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.primaryText)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = null,
                            tint = palette.p2Accent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.html_dialog_title),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTogglePill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) accentColor else palette.surfaceElevated)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else palette.border,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else accentColor,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Color.White else palette.primaryText
            )
        }
    }
}

@Composable
private fun PlayerInitialInputCard(
    label: String,
    value: String,
    isLocked: Boolean,
    accentColor: Color,
    accentFill: Color,
    quickOptions: List<String>,
    testTag: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalGamePalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surfaceElevated)
            .border(1.5.dp, accentColor.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isLocked) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                textAlign = TextAlign.Center
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = !isLocked,
            readOnly = isLocked,
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                color = if (isLocked) accentColor else palette.primaryText
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = palette.border,
                disabledBorderColor = accentColor.copy(alpha = 0.7f),
                focusedContainerColor = accentFill,
                unfocusedContainerColor = palette.backgroundDeep,
                disabledContainerColor = accentFill,
                disabledTextColor = accentColor,
                cursorColor = accentColor
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .size(width = 76.dp, height = 64.dp)
                .testTag(testTag)
        )

        if (quickOptions.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                quickOptions.forEach { preset ->
                    val isSelected = value.equals(preset, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) accentColor else palette.backgroundDeep)
                            .border(1.dp, if (isSelected) accentColor else palette.border, CircleShape)
                            .clickable { onValueChange(preset) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) Color.White else palette.mutedText
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.height(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CPU AUTO",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )
            }
        }
    }
}
