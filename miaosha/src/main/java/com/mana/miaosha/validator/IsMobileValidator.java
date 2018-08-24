package com.mana.miaosha.validator;


import org.apache.commons.lang3.StringUtils;

import com.mana.miaosha.util.ValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//真正的验证器    两参数：注解，注解类型
    public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

        private boolean required = false;

        public void initialize(IsMobile constraintAnnotation) {
            required = constraintAnnotation.required();
        }

        public boolean isValid(String value, ConstraintValidatorContext context) {
            if(required) {
                return ValidatorUtil.isMobile(value); //如果必须，则合法
            }else {
                if(StringUtils.isEmpty(value)) {
                    return true;
                }else {
                    return ValidatorUtil.isMobile(value);
                }
            }
        }

    }
