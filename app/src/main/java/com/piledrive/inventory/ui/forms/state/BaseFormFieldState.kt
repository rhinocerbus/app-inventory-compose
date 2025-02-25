package com.piledrive.inventory.ui.forms.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.piledrive.inventory.ui.forms.validators.Validators

abstract class BaseFormFieldState<T>(
	private val mainValidator: Validators<T>,
	private val externalValidators: List<Validators.Custom> = listOf(),
	private val initialValue: T
) {

	private var hasInteracted = false

	// mutablestate to trigger recomposition
	var currentValue: T by mutableStateOf(initialValue)
		protected set

	var isValid: Boolean = false
		protected set

	var errorMsg: String? = null
		protected set
	val hasError: Boolean
		get() = !errorMsg.isNullOrBlank()

	fun check(value: T) {
		hasInteracted = value != initialValue
		currentValue = value

		var isPassing = true
		isPassing = mainValidator.doCheck(currentValue)
		if (!isPassing) {
			this.isValid = false
			this.errorMsg = if (hasInteracted) {
				mainValidator.errMsg
			} else null
			return
		}

		externalValidators.forEach {
			isPassing = it.doCheck(currentValue)
			if (!isPassing) {
				this.isValid = false
				this.errorMsg = if (hasInteracted) {
					it.errMsg
				} else null
				return
			}
		}

		this.isValid = true
		this.errorMsg = null
	}
}