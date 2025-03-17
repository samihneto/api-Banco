package br.com.fiap.Bank.controller;

import br.com.fiap.Bank.model.Conta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contas")
public class ContaController {
    private static final List<Conta> contas = new ArrayList<>();

    // Endpoint para cadastrar uma conta
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarConta(@RequestBody Conta conta) {
        Map<String, String> erros = new HashMap<>();

        // Validações
        if (conta.getNomeTitular() == null || conta.getNomeTitular().isEmpty()) {
            erros.put("nomeTitular", "Nome do titular é obrigatório");
        }
        if (conta.getCpfTitular() == null || conta.getCpfTitular().isEmpty()) {
            erros.put("cpfTitular", "CPF do titular é obrigatório");
        }
        if (conta.getDataAbertura() != null && conta.getDataAbertura().isAfter(LocalDate.now())) {
            erros.put("dataAbertura", "Data de abertura não pode ser no futuro");
        }
        if (conta.getSaldo() < 0) {
            erros.put("saldo", "Saldo inicial não pode ser negativo");
        }
        if (conta.getTipo() == null || (!conta.getTipo().equals("corrente") && 
                                         !conta.getTipo().equals("poupança") && 
                                         !conta.getTipo().equals("salário"))) {
            erros.put("tipo", "Tipo deve ser 'corrente', 'poupança' ou 'salário'");
        }

        if (!erros.isEmpty()) {
            return ResponseEntity.badRequest().body(erros);
        }

        contas.add(conta);
        return ResponseEntity.status(201).body(conta);
    }

    // Endpoint para listar todas as contas cadastradas
    @GetMapping
    public ResponseEntity<List<Conta>> listarContas() {
        return ResponseEntity.ok(contas);
    }

    // Endpoint para buscar uma conta por ID (número)
    @GetMapping("/{numero}")
    public ResponseEntity<Object> buscarContaPorId(@PathVariable int numero) {
        Optional<Conta> contaOptional = contas.stream()
                .filter(conta -> conta.getNumero() == numero)
                .findFirst();
        
        if (contaOptional.isPresent()) {
            return ResponseEntity.ok(contaOptional.get());
        } else {
            // Retorna o status 404 com uma mensagem de erro
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "404");
            errorResponse.put("message", "Conta não encontrada");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    // Endpoint para buscar uma conta pelo CPF do titular
    @GetMapping("/cpf/{cpfTitular}")
    public ResponseEntity<Object> buscarContaPorCpf(@PathVariable String cpfTitular) {
        List<Conta> contasEncontradas = contas.stream()
                .filter(conta -> conta.getCpfTitular().equals(cpfTitular))
                .collect(Collectors.toList());

        if (!contasEncontradas.isEmpty()) {
            return ResponseEntity.ok(contasEncontradas);
        } else {
            // Retorna o status 404 com uma mensagem de erro
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "404");
            errorResponse.put("message", "Conta não encontrada com esse CPF");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }
}
