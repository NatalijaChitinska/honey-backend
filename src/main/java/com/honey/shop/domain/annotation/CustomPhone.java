package com.honey.shop.domain.annotation;

import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class CustomPhone implements ConstraintValidator<ValidPhone, String> {

  @Override
  public void initialize(ValidPhone validPhone) {}

  private static final Pattern patternMobile =
      Pattern.compile(
          "^((00)?\\+?(389))?[\\/\\-\\s*\\.]?(((\\(0\\))|0)?\\s*7\\d{1})[\\/\\-\\s*\\.\\,]?([\\d]{3})[\\/\\-\\s*\\.\\,]?([\\d]{3})$");

  private static final Pattern patternMobile2 = Pattern.compile("^02\\d{7}$");

  @Override
  public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
    return StringUtils.isNotBlank(phone)
        && (patternMobile.matcher(phone).matches() || patternMobile2.matcher(phone).matches());
  }
}