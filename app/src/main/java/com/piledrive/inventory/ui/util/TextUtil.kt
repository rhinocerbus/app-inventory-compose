package com.piledrive.inventory.ui.util

import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MeasureTextWidth(sample: String, textStyle: TextStyle, paint: TextPaint): Float {
	val resolver: FontFamily.Resolver = LocalFontFamilyResolver.current
	val typeface: Typeface = remember(resolver, textStyle) {
		resolver.resolve(
			fontFamily = textStyle.fontFamily,
			fontWeight = textStyle.fontWeight ?: FontWeight.Normal,
			fontStyle = textStyle.fontStyle ?: FontStyle.Normal,
			fontSynthesis = textStyle.fontSynthesis ?: FontSynthesis.All,
		)
	}.value as Typeface
	paint.typeface = typeface
	paint.textSize = with(LocalDensity.current) { textStyle.fontSize.toPx() }
	// Paint letter spacing is in ems which are multiples of the textSize/fontSize but letter spacings in TextStyle are in sp.
	paint.letterSpacing = (textStyle.letterSpacing.value ?: 0f) / textStyle.fontSize.value

	return paint.measureText(sample)
}