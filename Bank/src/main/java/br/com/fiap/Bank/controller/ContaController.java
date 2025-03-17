package br.com.fiap.Bank.controller;

import br.com.fiap.Bank.model.Conta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contas")
public class ContaController {
    private static final List<Conta> contas = new ArrayList<>();

    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarConta(@RequestBody Conta conta) {
        // Criação de um mapa de erros
        Map<String, String> erros = new HashMap<>();

        // 1. Verificar se o nome do titular é obrigatório
        if (conta.getNomeTitular() == null || conta.getNomeTitular().isEmpty()) {
            erros.put("nomeTitular", "Nome do titular é obrigatório");
        }

        // 2. Verificar se o CPF do titular é obrigatório
        if (conta.getCpfTitular() == null || conta.getCpfTitular().isEmpty()) {
            erros.put("cpfTitular", "CPF do titular é obrigatório");
        }

        // 3. Verificar se a data de abertura não é no futuro
        if (conta.getDataAbertura() != null && conta.getDataAbertura().isAfter(LocalDate.now())) {
            erros.put("dataAbertura", "Data de abertura não pode ser no futuro");
        }

        // 4. Verificar se o saldo inicial não é negativo
        if (conta.getSaldo() < 0) {
            erros.put("saldo", "Saldo inicial não pode ser negativo");
        }

        // 5. Verificar se o tipo é válido
        if (conta.getTipo() == null || (!conta.getTipo().equals("corrente") && 
                                         !conta.getTipo().equals("poupança") && 
                                         !conta.getTipo().equals("salário"))) {
            erros.put("tipo", "Tipo deve ser 'corrente', 'poupança' ou 'salário'");
        }

        // Se houver erros, retorna 400 Bad Request com as mensagens de erro
        if (!erros.isEmpty()) {
            return new ResponseEntity<>(erros, HttpStatus.BAD_REQUEST);
        }

        // Se não houver erros, adiciona a conta e retorna 201 Created
        contas.add(conta);
        return new ResponseEntity<>(conta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Conta>> listarContas() {
        return ResponseEntity.ok(contas);
    }
}
