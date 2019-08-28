package com.alexandre.financeiro.api.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alexandre.financeiro.api.event.RecursoCriadoEvent;
import com.alexandre.financeiro.api.model.Categoria;
import com.alexandre.financeiro.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping
	public ResponseEntity<?> listar() {
		List<Categoria> procurarCategoria = categoriaRepository.findAll();

		return ResponseEntity.status(HttpStatus.OK).body(procurarCategoria);

	}

	@GetMapping("/{id}")
	public ResponseEntity<Categoria> buscar(@PathVariable Long id) {
		Optional<Categoria> categoria = categoriaRepository.findById(id);

		return ResponseEntity.status(HttpStatus.OK).body(categoria.get());

	}

	@PostMapping
	public ResponseEntity<Categoria> adicionar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {

		Categoria categoriaSalva = categoriaRepository.save(categoria);

		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getid()));

		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);

	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {

		categoriaRepository.deleteById(id);

	}

	@PutMapping("/{id}")
	public ResponseEntity<Categoria> alterar(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
		Optional<Categoria> categoriaSalva = categoriaRepository.findById(id);
		BeanUtils.copyProperties(categoria, categoriaSalva.get(), "id");

		categoriaRepository.save(categoriaSalva.get());

		return ResponseEntity.status(HttpStatus.OK).body(categoriaSalva.get());
	}

}
