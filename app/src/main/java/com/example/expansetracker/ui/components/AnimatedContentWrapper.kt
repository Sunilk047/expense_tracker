package com.example.expansetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimatedContentWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 2 }
        ),
        modifier = modifier
    ) {
        content()
    }
}
