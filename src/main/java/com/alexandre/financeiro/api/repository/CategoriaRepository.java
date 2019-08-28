package com.alexandre.financeiro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexandre.financeiro.api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	

}
