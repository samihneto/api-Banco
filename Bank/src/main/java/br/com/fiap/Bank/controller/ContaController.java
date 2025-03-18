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

    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarConta(@RequestBody Conta conta) {
        Map<String, String> erros = new HashMap<>();

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

    @GetMapping
    public ResponseEntity<List<Conta>> listarContas() {
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Object> buscarContaPorId(@PathVariable int numero) {
        Optional<Conta> contaOptional = contas.stream()
                .filter(conta -> conta.getNumero() == numero)
                .findFirst();
        
        if (contaOptional.isPresent()) {
            return ResponseEntity.ok(contaOptional.get());
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "404");
            errorResponse.put("message", "Conta não encontrada");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @GetMapping("/cpf/{cpfTitular}")
    public ResponseEntity<Object> buscarContaPorCpf(@PathVariable String cpfTitular) {
        List<Conta> contasEncontradas = contas.stream()
                .filter(conta -> conta.getCpfTitular().equals(cpfTitular))
                .collect(Collectors.toList());

        if (!contasEncontradas.isEmpty()) {
            return ResponseEntity.ok(contasEncontradas);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "404");
            errorResponse.put("message", "Conta não encontrada com esse CPF");
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/encerrar/{numero}")
    public ResponseEntity<Object> encerrarConta(@PathVariable int numero) {
        for (Conta conta : contas) {
            if (conta.getNumero() == numero) {
                if (!conta.isAtiva()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "status", "400",
                        "message", "Conta já está inativa"
                    ));
                }
                conta.setAtiva(false);
                return ResponseEntity.ok(Map.of(
                    "status", "200",
                    "message", "Conta encerrada com sucesso"
                ));
            }
        }
        
        return ResponseEntity.status(404).body(Map.of(
            "status", "404",
            "message", "Conta não encontrada"
        ));
    }

    @PostMapping("/depositar")
    public ResponseEntity<Object> depositar(@RequestBody Map<String, Object> deposito) {
        int numeroConta = (int) deposito.get("numero");
        double valor = (double) deposito.get("valor");

        if (valor <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "400",
                "message", "O valor do depósito deve ser positivo"
            ));
        }

        for (Conta conta : contas) {
            if (conta.getNumero() == numeroConta) {
                if (!conta.isAtiva()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "status", "400",
                        "message", "Conta inativa não pode receber depósitos"
                    ));
                }
                conta.setSaldo(conta.getSaldo() + valor);
                return ResponseEntity.ok(Map.of(
                    "status", "200",
                    "message", "Depósito realizado com sucesso",
                    "saldoAtualizado", conta.getSaldo()
                ));
            }
        }

        return ResponseEntity.status(404).body(Map.of(
            "status", "404",
            "message", "Conta não encontrada"
        ));
    }

    @PostMapping("/sacar")
public ResponseEntity<Object> sacar(@RequestBody Map<String, Object> request) {
    int numeroConta = (int) request.get("numero");
    double valorSaque = (double) request.get("valor");

    Optional<Conta> contaOptional = contas.stream()
            .filter(conta -> conta.getNumero() == numeroConta)
            .findFirst();

    if (contaOptional.isEmpty()) {
        return ResponseEntity.status(404).body(Map.of("status", "404", "message", "Conta não encontrada"));
    }
    Conta conta = contaOptional.get();
    if (!conta.isAtiva()) {
        return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Conta inativa não pode realizar saques"));
    }
    if (valorSaque <= 0) {
        return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "O valor do saque deve ser positivo"));
    }
    if (valorSaque > conta.getSaldo()) {
        return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Saldo insuficiente"));
    }
    conta.setSaldo(conta.getSaldo() - valorSaque);

    return ResponseEntity.ok(Map.of(
        "status", "200",
        "message", "O saque foi realizado com sucesso",
        "saldoAtualizado", conta.getSaldo()
    ));
}
@PostMapping("/pix")
public ResponseEntity<Object> realizarPix(@RequestBody Map<String, Object> request) {
    int numeroOrigem = (int) request.get("numeroOrigem");
    int numeroDestino = (int) request.get("numeroDestino");
    double valorPix = (double) request.get("valor");

    Optional<Conta> contaOrigemOptional = contas.stream()
            .filter(conta -> conta.getNumero() == numeroOrigem)
            .findFirst();

    Optional<Conta> contaDestinoOptional = contas.stream()
            .filter(conta -> conta.getNumero() == numeroDestino)
            .findFirst();

    if (contaOrigemOptional.isEmpty()) {
        return ResponseEntity.status(404).body(Map.of("status", "404", "message", "Conta de origem não encontrada"));
    }
    if (contaDestinoOptional.isEmpty()) {
        return ResponseEntity.status(404).body(Map.of("status", "404", "message", "Conta de destino não encontrada"));
    }

    Conta contaOrigem = contaOrigemOptional.get();
    Conta contaDestino = contaDestinoOptional.get();

    if (!contaOrigem.isAtiva()) {
        return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Conta de origem está inativa"));
    }
    if (!contaDestino.isAtiva()) {
        return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Conta de destino está inativa"));
    }

    if (valorPix <= 0) {
        return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "O valor do Pix deve ser positivo"));
    }

    if (valorPix > contaOrigem.getSaldo()) {
        return ResponseEntity.badRequest().body(Map.of("status", "400", "message", "Saldo insuficiente para realizar o Pix"));
    }

    contaOrigem.setSaldo(contaOrigem.getSaldo() - valorPix);
    contaDestino.setSaldo(contaDestino.getSaldo() + valorPix);

    return ResponseEntity.ok(Map.of(
        "status", "200",
        "message", "Pix realizado com sucesso",
        "saldoAtualizadoOrigem", contaOrigem.getSaldo()
    ));
}

}
