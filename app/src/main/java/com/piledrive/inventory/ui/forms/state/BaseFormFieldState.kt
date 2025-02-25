package com.piledrive.inventory.ui.forms.state

import com.piledrive.inventory.ui.forms.validators.Validators

abstract class BaseFormFieldState<T>(
	val mainValidator: Validators<T>,
	val externalValidators: List<Validators.Custom> = listOf()
) {

	protected var hasInteracted = false

	protected var _currentValue: T? = null
	val currentValue: T?
		get() = _currentValue

	protected var _isValid: Boolean = false
	val isValid: Boolean
		get() = _isValid

	protected var _errorMsg: String? = null
	val errorMsg: String?
		get() = _errorMsg
	val hasError: Boolean
		get() = !_errorMsg.isNullOrBlank()

	fun check(value: T) {
		val interacted = hasInteracted
		hasInteracted = true
		_currentValue = value

		var isPassing = true
		isPassing = mainValidator.doCheck(_currentValue)
		if (!isPassing) {
			_isValid = false
			_errorMsg = if (interacted) {
				mainValidator.errMsg
			} else null
			return
		}

		externalValidators.forEach {
			isPassing = it.doCheck(_currentValue)
			if (!isPassing) {
				_isValid = false
				_errorMsg = if (interacted) {
					it.errMsg
				} else null
				return
			}
		}

		_isValid = true
		_errorMsg = null
	}
}