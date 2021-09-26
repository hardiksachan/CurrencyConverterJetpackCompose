package com.hardiksachan.currencyconverterjetpackcompose.presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = BlackDark,
    primaryVariant = Black,
    secondary = White,
    secondaryVariant = WhiteDark,
    onPrimary = WhiteText,
    onSecondary = BlackText
)

private val LightColorPalette = lightColors(
    primary = White,
    primaryVariant = WhiteLight,
    secondary = Black,
    secondaryVariant = BlackLight,
    onPrimary = BlackText,
    onSecondary = WhiteText
)

@Composable
fun AppTheme(
    content: @Composable() () -> Unit
) {
    val colors = LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}