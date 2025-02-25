package com.piledrive.inventory.ui.forms.state

import com.piledrive.inventory.ui.forms.validators.Validators

class TextFormFieldState(
	mainValidator: Validators<String>,
	externalValidators: List<Validators.Custom> = listOf()
) : BaseFormFieldState<String>(mainValidator, externalValidators) {

}