package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.TelefoneAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.servicos.AdicionarLinkTelefoneServico;

import lombok.Data;

@RestController
@RequestMapping("/telefone")
public class TelefoneControler {

  @Autowired
  private TelefoneRepositorio telefoneRepositorio;

  @Autowired
  private ClienteRepositorio clienteRepositorio;

  @Autowired
  private TelefoneAtualizador telefoneAtualizador;

  @Autowired
  private AdicionarLinkTelefoneServico adicionadorLink;

  @Data
  private static class CriarTelefoneRequest {
    private Telefone telefone;
    private long clienteId;
  }


  @GetMapping("/telefones")
  public ResponseEntity<List<Telefone>> obterTelefones() {
    List<Telefone> telefones = telefoneRepositorio.findAll();
    if (telefones.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    adicionadorLink.adicionarLink(telefones);
    return ResponseEntity.ok(telefones);
  }

  @GetMapping("/telefone/{id}")
  public ResponseEntity<Telefone> obterTelefone(@PathVariable long id) {
    Optional<Telefone> telefone = telefoneRepositorio.findById(id);
    if (telefone.isPresent()) {
      adicionadorLink.adicionarLink(telefone.get());
      return ResponseEntity.ok(telefone.get());
    }
    return ResponseEntity.notFound().build();
  }


  @PostMapping("/cadastro")
  public ResponseEntity<?> cadastrarTelefone(@RequestBody CriarTelefoneRequest request) {
    Optional<Cliente> clienteOpt = clienteRepositorio.findById(request.getClienteId());
    if (clienteOpt.isEmpty()) {
      return ResponseEntity.badRequest().body("Cliente não encontrado");
    }

    Cliente cliente = clienteOpt.get();
    Telefone telefone = request.getTelefone();
    telefone.setCliente(cliente);

    telefone = telefoneRepositorio.save(telefone);
    cliente.getTelefones().add(telefone);
    clienteRepositorio.save(cliente);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }


  @PutMapping("/atualizar")
  public ResponseEntity<?> atualizarTelefone(@RequestBody Telefone telefoneAtualizado) {
    Optional<Telefone> telefoneOpt = telefoneRepositorio.findById(telefoneAtualizado.getId());
    if (telefoneOpt.isEmpty()) {
      return ResponseEntity.badRequest().body("Telefone não encontrado");
    }

    telefoneAtualizador.atualizar(telefoneOpt.get(), telefoneAtualizado);
    telefoneRepositorio.save(telefoneOpt.get());
    return ResponseEntity.ok().build();
  }


  @DeleteMapping("/deletar")
  public ResponseEntity<?> deletarTelefone(@RequestBody Telefone telefone) {
    Optional<Telefone> telefoneOpt = telefoneRepositorio.findById(telefone.getId());
    if (telefoneOpt.isEmpty()) {
      return ResponseEntity.badRequest().body("Telefone não encontrado");
    }

    telefoneRepositorio.delete(telefoneOpt.get());
    return ResponseEntity.ok().build();
  }
}
