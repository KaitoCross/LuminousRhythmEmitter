package com.github.kaitocross.luminousrhythmcontroller.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val DarkBlue = Color(0xFF5C78F4)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val LightBlue = Color(0xFF1D53B8)

@Immutable
data class CustomColorsPalette(
    val blueIconColor: Color = Color.Unspecified,
)
val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }
