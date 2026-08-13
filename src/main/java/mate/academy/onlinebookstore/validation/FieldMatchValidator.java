package mate.academy.onlinebookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Object propertyValue1 = new BeanWrapperImpl(o).getPropertyValue(firstFieldName);
            Object propertyValue2 = new BeanWrapperImpl(o).getPropertyValue(secondFieldName);

            return Objects.equals(propertyValue1, propertyValue2);
        } catch (Exception e) {
            return false;
        }
    }
}
