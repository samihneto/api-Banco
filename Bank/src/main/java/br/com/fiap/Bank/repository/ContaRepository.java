package br.com.fiap.Bank.repository;

import br.com.fiap.Bank.model.Conta;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ContaRepository {
    private List<Conta> contas = new ArrayList<>();

    public void salvar(Conta conta) {
        contas.add(conta);
    }

    public List<Conta> listarTodas() {
        return contas;
    }
}