package com.alexandre.financeiro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexandre.financeiro.api.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
	
	

}
