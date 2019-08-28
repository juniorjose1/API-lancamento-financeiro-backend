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
import com.alexandre.financeiro.api.model.Pessoa;
import com.alexandre.financeiro.api.repository.PessoaRepository;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping
	public ResponseEntity<?> listar() {
		List<Pessoa> listarPessoas = pessoaRepository.findAll();

		return ResponseEntity.status(HttpStatus.OK).body(listarPessoas);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Pessoa> buscar(@PathVariable Long id) {
		Optional<Pessoa> buscarPessoa = pessoaRepository.findById(id);

		return ResponseEntity.status(HttpStatus.OK).body(buscarPessoa.get());

	}

	@PostMapping
	public ResponseEntity<Pessoa> adicionar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {

		Pessoa salvarPessoa = pessoaRepository.save(pessoa);

		publisher.publishEvent(new RecursoCriadoEvent(this, response, salvarPessoa.getId()));

		return ResponseEntity.status(HttpStatus.CREATED).body(salvarPessoa);
	}
	

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		pessoaRepository.deleteById(id);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Pessoa> alterar(@PathVariable Long id, @Valid @RequestBody Pessoa pessoa){
		Optional<Pessoa> pessoaSalva = pessoaRepository.findById(id);
		BeanUtils.copyProperties(pessoa, pessoaSalva.get(), "id");
		pessoaRepository.save(pessoaSalva.get());
		return ResponseEntity.status(HttpStatus.OK).body(pessoaSalva.get());
	}
	
	@PutMapping("/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void alterarAtivo(@PathVariable Long id, @RequestBody Boolean ativo) {
		Pessoa pessoaSalva = pessoaRepository.getOne(id);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}
	
	
	

}
