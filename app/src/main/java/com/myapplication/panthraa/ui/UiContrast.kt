package com.myapplication.panthraa.ui

import androidx.compose.ui.graphics.Color

internal fun readableContentColor(
    background: Color,
    light: Color = Color.White,
    dark: Color = Color(0xFF0F172A),
): Color {
    val alpha = background.alpha.coerceIn(0f, 1f)
    val red = background.red * alpha + (1f - alpha)
    val green = background.green * alpha + (1f - alpha)
    val blue = background.blue * alpha + (1f - alpha)
    val luma = (0.299f * red) + (0.587f * green) + (0.114f * blue)
    return if (luma < 0.58f) light else dark
}
