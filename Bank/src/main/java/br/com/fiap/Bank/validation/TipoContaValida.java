package br.com.fiap.Bank.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TipoContaValidador.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TipoContaValida {
    String message() default "O tipo da conta deve ser 'corrente', 'poupança' ou 'salário'.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}