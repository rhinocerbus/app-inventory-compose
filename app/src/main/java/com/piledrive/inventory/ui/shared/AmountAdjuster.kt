package com.piledrive.inventory.ui.shared

import android.text.TextPaint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piledrive.inventory.data.model.QuantityUnit
import com.piledrive.lib_compose_components.ui.theme.custom.AppTheme
import com.piledrive.lib_compose_components.ui.util.MeasureTextWidth


@Composable
fun AmountAdjuster(
	modifier: Modifier = Modifier,
	unit: QuantityUnit? = null,
	qtyValue: Double,
	increment: Double,
	min: Double = 0.0,
	max: Double = -1.0,
	readOnly: Boolean,
	hideButtonsIfDisabled: Boolean,
	onQtyChange: (Double) -> Unit
) {
	Surface {

		Row(verticalAlignment = Alignment.CenterVertically) {
			if (!(readOnly && hideButtonsIfDisabled)) {
				IconButton(
					onClick = {
						onQtyChange(qtyValue - increment)
					},
					enabled = qtyValue > min && !readOnly
				) {
					Icon(Icons.Default.KeyboardArrowDown, "decrement item stash amount")
				}
			}

			val amountW =
				MeasureTextWidth("00.00", MaterialTheme.typography.bodySmall, TextPaint())

			OutlinedTextField(
				modifier = Modifier
					.width(amountW.dp)
					.focusable(!readOnly),
				value = if (qtyValue < 0) "--" else "$qtyValue",
				label = {
					unit?.also {
						Text(it.label)
					}
				},
				onValueChange = {
					if (it.toDouble() < 0) {
						//err
					} else {
						onQtyChange(it.toDouble())
					}
				},
				textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
				singleLine = true,
				readOnly = readOnly
			)

			if (!(readOnly && hideButtonsIfDisabled)) {
				IconButton(
					onClick = {
						onQtyChange(qtyValue + increment)
					},
					enabled = !readOnly && !(max != -1.0 && qtyValue >= max)
				) {
					Icon(Icons.Default.KeyboardArrowUp, "increment item stash amount")
				}
			}
		}
	}
}

@Preview
@Composable
private fun AmountAdjusterPreview() {
	AppTheme {
		AmountAdjuster(
			Modifier,
			unit = QuantityUnit.defaultUnitBags,
			qtyValue = 8.0,
			increment = 1.0,
			min = 0.0,
			max = -1.0,
			readOnly = false,
			hideButtonsIfDisabled = false,
			onQtyChange = {}
		)
	}
}