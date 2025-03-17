package br.com.fiap.Bank.controller;

import br.com.fiap.Bank.dto.ContaDTO;
import br.com.fiap.Bank.model.Conta;
import br.com.fiap.Bank.repository.ContaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaRepository contaRepository;

    public ContaController(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @PostMapping
    public ResponseEntity<?> cadastrarConta(@RequestBody @Valid ContaDTO contaDTO) {
        Conta conta = new Conta(
                contaDTO.getNumero(),
                contaDTO.getAgencia(),
                contaDTO.getNomeTitular(),
                contaDTO.getCpfTitular(),
                contaDTO.getDataAbertura(),
                contaDTO.getSaldo(),
                contaDTO.isAtiva(),
                contaDTO.getTipo()
        );

        contaRepository.salvar(conta);
        return ResponseEntity.ok("Conta cadastrada com sucesso!");
    }

    @GetMapping
    public List<Conta> listarContas() {
        return contaRepository.listarTodas();
    }
}