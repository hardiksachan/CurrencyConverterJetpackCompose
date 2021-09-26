package com.hardiksachan.currencyconverterjetpackcompose.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Black,
    primaryVariant = BlackDark,
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}