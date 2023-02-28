package hung.poc.kafka.pojo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CustomerValidator implements Validator {

    final LocalValidatorFactoryBean beanValidator;

    private List<String> blacklist = List.of("john.doe@gmail.com");

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.info("Check customer instance with bean validation first...");
        ValidationUtils.invokeValidator(beanValidator,target,errors);
        if (!errors.hasErrors()) {
            log.info("Check other high-level error");
            Customer customer = (Customer)target;
            if (blacklist.contains(customer.getEmail())) {
                errors.rejectValue("email","in-blacklist","Email in blacklist");
            }
        }
    }
}