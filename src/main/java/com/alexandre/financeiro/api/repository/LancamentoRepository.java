package com.alexandre.financeiro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexandre.financeiro.api.model.Lancamento;
import com.alexandre.financeiro.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {

}
