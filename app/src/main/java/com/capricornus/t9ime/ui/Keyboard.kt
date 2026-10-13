package com.capricornus.t9ime.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 一顆按鍵的資料：主字（大號）＋右下角的字母提示（gboard／搜狗都是這樣塞的）。 */
data class KeySpec(val main: String, val legend: String = "", val code: String = main)

private val t9Rows = listOf(
    listOf(KeySpec("1", ""), KeySpec("2", "abc"), KeySpec("3", "def"), KeySpec("4", "ghi")),
    listOf(KeySpec("5", "jkl"), KeySpec("6", "mno"), KeySpec("7", "pqrs"), KeySpec("8", "tuv")),
    listOf(KeySpec("9", "wxyz"), KeySpec("0", ""), KeySpec("⌫"), KeySpec("⏎")),
)

@Composable
fun NineKeyKeyboard(
    code: String,
    candidates: List<String>,
    onKey: (String) -> Unit,
    onCandidate: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        CandidateStrip(code = code, candidates = candidates, onCandidate = onCandidate)
        t9Rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                row.forEach { spec ->
                    Key(spec = spec, onClick = { onKey(spec.code) })
                }
            }
        }
    }
}

@Composable
private fun CandidateStrip(
    code: String,
    candidates: List<String>,
    onCandidate: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(44.dp).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (candidates.isEmpty()) {
            Text(
                text = code,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Row
        }
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
        ) {
            itemsIndexed(candidates) { index, word ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable { onCandidate(index) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = word,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.Key(spec: KeySpec, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    // 按壓用彈簧而不是固定時長：M3E 的「手感」就是這個回彈，規格照 gboard 的按鍵縮放。
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = 900f),
        label = "keyScale",
    )
    Box(
        modifier = Modifier
            .weight(1f)
            .height(54.dp)
            .scale(scale)
            .clip(MaterialTheme.shapes.small)
            .background(
                if (pressed) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceContainerLow
            )
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = spec.main,
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        if (spec.legend.isNotEmpty()) {
            Text(
                text = spec.legend,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp),
            )
        }
    }
}
