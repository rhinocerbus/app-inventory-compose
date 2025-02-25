package com.piledrive.inventory.ui.forms.validators

sealed class Validators<T>(val errMsg: String) {
	abstract fun doCheck(value: T?): Boolean

	class Required<U>(errMsg: String) : Validators<U>(errMsg) {
		override fun doCheck(value: U?): Boolean {
			value ?: return false
			return when (value) {
				is String -> value.isNotBlank()
				else -> true
			}
		}
	}

	class Custom(val runCheck: (Any?) -> Boolean, errMsg: String) : Validators<Any?>(errMsg) {
		override fun doCheck(value: Any?): Boolean {
			return runCheck.invoke(value)
		}
	}
}