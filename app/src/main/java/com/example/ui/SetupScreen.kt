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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
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
import com.example.ui.theme.DotWhite
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MutedText
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.Player1Cyan
import com.example.ui.theme.Player1CyanFill
import com.example.ui.theme.Player2Coral
import com.example.ui.theme.Player2CoralFill
import com.example.ui.theme.Player3Emerald
import com.example.ui.theme.Player3EmeraldFill
import com.example.ui.theme.Player4Violet
import com.example.ui.theme.Player4VioletFill
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceElevated

@Composable
fun SetupScreen(
    selectedPlayerCount: Int,
    playerInputs: List<String>,
    errorResId: Int?,
    onSelectPlayerCount: (Int) -> Unit,
    onPlayerInputChange: (Int, String) -> Unit,
    onStartGame: () -> Unit,
    onOpenHtmlViewer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF131F3A),
                        ObsidianBackground
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
                .padding(horizontal = 18.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Hero Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(132.dp)
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
                                .background(ObsidianBackground.copy(alpha = 0.85f))
                                .border(1.dp, SlateBorder, RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.GridOn,
                                    contentDescription = null,
                                    tint = Player1Cyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "5×9 DOTS • 1–4 PLAYERS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DotWhite
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.setup_title),
                            style = MaterialTheme.typography.displayLarge,
                            color = DotWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.setup_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Player Count (1–4) & Initials Configuration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.setup_card_header),
                        style = MaterialTheme.typography.titleLarge,
                        color = DotWhite
                    )

                    // 1 to 4 Players Selector Pills
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.player_count_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (count in 1..4) {
                                val isSelected = selectedPlayerCount == count
                                val pillAccent = when (count) {
                                    1 -> Player1Cyan
                                    2 -> Player2Coral
                                    3 -> Player3Emerald
                                    else -> Player4Violet
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isSelected) pillAccent else SlateSurfaceElevated
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) pillAccent else SlateBorder,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        .clickable { onSelectPlayerCount(count) }
                                        .testTag("player_count_${count}_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (count == 1) Icons.Filled.SmartToy else Icons.Filled.Group,
                                            contentDescription = null,
                                            tint = if (isSelected) ObsidianBackground else pillAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (count == 1) "1P vs AI" else "${count}P",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) ObsidianBackground else DotWhite
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = if (selectedPlayerCount == 1) {
                                stringResource(R.string.mode_1p_subtitle)
                            } else {
                                stringResource(R.string.mode_multi_subtitle, selectedPlayerCount)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Player1Cyan,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Row 1: Player 1 and Player 2 (or AI Bot in 1P mode)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlayerInitialInputCard(
                            label = stringResource(R.string.player_one_label),
                            value = playerInputs.getOrElse(0) { "A" },
                            accentColor = Player1Cyan,
                            accentFill = Player1CyanFill,
                            quickOptions = listOf("A", "X", "1", "P"),
                            testTag = "player1_letter_input",
                            onValueChange = { onPlayerInputChange(0, it) },
                            modifier = Modifier.weight(1f)
                        )

                        PlayerInitialInputCard(
                            label = if (selectedPlayerCount == 1) {
                                stringResource(R.string.player_two_ai_label)
                            } else {
                                stringResource(R.string.player_two_label)
                            },
                            value = playerInputs.getOrElse(1) { "B" },
                            accentColor = Player2Coral,
                            accentFill = Player2CoralFill,
                            quickOptions = listOf("B", "O", "2", "Z"),
                            testTag = "player2_letter_input",
                            onValueChange = { onPlayerInputChange(1, it) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Player 3 and Player 4 (shown when 3P or 4P is selected)
                    AnimatedVisibility(visible = selectedPlayerCount >= 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PlayerInitialInputCard(
                                label = stringResource(R.string.player_three_label),
                                value = playerInputs.getOrElse(2) { "C" },
                                accentColor = Player3Emerald,
                                accentFill = Player3EmeraldFill,
                                quickOptions = listOf("C", "M", "3", "E"),
                                testTag = "player3_letter_input",
                                onValueChange = { onPlayerInputChange(2, it) },
                                modifier = Modifier.weight(1f)
                            )

                            if (selectedPlayerCount == 4) {
                                PlayerInitialInputCard(
                                    label = stringResource(R.string.player_four_label),
                                    value = playerInputs.getOrElse(3) { "D" },
                                    accentColor = Player4Violet,
                                    accentFill = Player4VioletFill,
                                    quickOptions = listOf("D", "W", "4", "V"),
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
                            containerColor = Player1Cyan,
                            contentColor = ObsidianBackground
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

            // Rules & Standalone Web HTML Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SlateBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Player1Cyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.rules_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenHtmlViewer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_html_viewer_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DotWhite)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = null,
                            tint = Player2Coral,
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
private fun PlayerInitialInputCard(
    label: String,
    value: String,
    accentColor: Color,
    accentFill: Color,
    quickOptions: List<String>,
    testTag: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SlateSurfaceElevated)
            .border(1.5.dp, accentColor.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                color = DotWhite
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = accentFill,
                unfocusedContainerColor = ObsidianBackground,
                cursorColor = accentColor
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .size(width = 76.dp, height = 64.dp)
                .testTag(testTag)
        )

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
                        .background(if (isSelected) accentColor else ObsidianBackground)
                        .border(1.dp, if (isSelected) accentColor else SlateBorder, CircleShape)
                        .clickable { onValueChange(preset) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preset,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSelected) ObsidianBackground else MutedText
                    )
                }
            }
        }
    }
}
