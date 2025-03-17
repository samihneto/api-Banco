package br.com.fiap.Bank.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

import br.com.fiap.Bank.validation.TipoContaValida;

public class ContaDTO {
    
    @NotBlank(message = "O nome do titular é obrigatório.")
    private String nomeTitular;

    @NotBlank(message = "O CPF do titular é obrigatório.")
    private String cpfTitular;

    @PastOrPresent(message = "A data de abertura não pode ser no futuro.")
    private LocalDate dataAbertura;

    @PositiveOrZero(message = "O saldo inicial não pode ser negativo.")
    private double saldo;

    @NotBlank(message = "O tipo da conta é obrigatório.")
    @TipoContaValida
    private String tipo;

    private String numero;
    private String agencia;
    private boolean ativa;

    // Getters e Setters
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getAgencia() { return agencia; }
    public void setAgencia(String agencia) { this.agencia = agencia; }

    public String getNomeTitular() { return nomeTitular; }
    public void setNomeTitular(String nomeTitular) { this.nomeTitular = nomeTitular; }

    public String getCpfTitular() { return cpfTitular; }
    public void setCpfTitular(String cpfTitular) { this.cpfTitular = cpfTitular; }

    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}