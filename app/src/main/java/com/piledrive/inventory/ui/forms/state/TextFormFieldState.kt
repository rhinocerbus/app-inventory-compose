package com.piledrive.inventory.ui.forms.state

import com.piledrive.inventory.ui.forms.validators.Validators

class TextFormFieldState(
	mainValidator: Validators<String>,
	externalValidators: List<Validators.Custom> = listOf(),
	initialValue: String = ""
) : BaseFormFieldState<String>(mainValidator, externalValidators, initialValue) {

}