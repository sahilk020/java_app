package com.pay10.pg.security;

import com.pay10.commons.util.Validator;
import com.pay10.pg.core.pageintegrator.GeneralValidator;

public class ValidatorFactory {

	public ValidatorFactory() {
	}

	public static Validator	getValidator(){
		return new GeneralValidator();
	}
}
