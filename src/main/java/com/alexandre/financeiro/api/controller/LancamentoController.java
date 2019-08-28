package com.alexandre.financeiro.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alexandre.financeiro.api.event.RecursoCriadoEvent;
import com.alexandre.financeiro.api.exceptionhandler.FinanceiroApiExceptionHandler.Erro;
import com.alexandre.financeiro.api.model.Lancamento;
import com.alexandre.financeiro.api.repository.LancamentoRepository;
import com.alexandre.financeiro.api.repository.filter.LancamentoFilter;
import com.alexandre.financeiro.api.service.LancamentoService;
import com.alexandre.financeiro.api.service.exception.PessoaInexistenteOuInativaException;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	@GetMapping
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Lancamento> buscar(@PathVariable Long id){
		Optional<Lancamento> buscarLancamento = lancamentoRepository.findById(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(buscarLancamento.get());
	}
	
	@PostMapping
	public ResponseEntity<Lancamento> cadastrar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response){
		Lancamento cadastrarLancamento = lancamentoService.salvar(lancamento);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, cadastrarLancamento.getId()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarLancamento);
	}
	
	@ExceptionHandler( {PessoaInexistenteOuInativaException.class})
	public ResponseEntity<List<Erro>> handlePessoaInexistenteouInativa(PessoaInexistenteOuInativaException ex){
		String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void apagar(@PathVariable Long id){
		lancamentoRepository.deleteById(id);
		
	}

}
