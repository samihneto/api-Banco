package br.com.fiap.Bank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class TipoContaValidador implements ConstraintValidator<TipoContaValida, String> {

    private static final List<String> TIPOS_VALIDOS = Arrays.asList("corrente", "poupança", "salário");

    @Override
    public boolean isValid(String tipo, ConstraintValidatorContext context) {
        return tipo != null && TIPOS_VALIDOS.contains(tipo.toLowerCase());
    }
}