package com.mana.miaosha.validator;  //校验器包

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)

@Documented
@Constraint(
        validatedBy = {IsMobileValidator.class}
)

public @interface IsMobile {

	//默认必须有
    boolean required() default true;

    //必加，校验不通过时的返回信息
    String message() default "手机号码格式错误";

    
    //该两项必加
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

