package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.servicos.AdicionarLinkEnderecoServico;

import lombok.Data;

@RestController
@RequestMapping("/endereco")
public class EnderecoControler {

  @Autowired
  private EnderecoRepositorio enderecoRepositorio;

  @Autowired
  private ClienteRepositorio clienteRepositorio;

  @Autowired
  private EnderecoAtualizador enderecoAtualizador;

  @Autowired
  private AdicionarLinkEnderecoServico adicionadorLink;

  @Data
  private static class CriarEnderecoRequest {
    private Endereco endereco;
    private long clienteId;
  }

  @GetMapping("/enderecos")
  public ResponseEntity<List<Endereco>> obterEnderecos() {
    List<Endereco> enderecos = enderecoRepositorio.findAll();
    if (enderecos.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      adicionadorLink.adicionarLink(enderecos);
      return ResponseEntity.ok(enderecos);
    }
  }

  @GetMapping("/endereco/{id}")
  public ResponseEntity<Endereco> obterEndereco(@PathVariable long id) {
    Optional<Endereco> endereco = enderecoRepositorio.findById(id);
    if (endereco.isPresent()) {
      adicionadorLink.adicionarLink(endereco.get());
      return ResponseEntity.ok(endereco.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/cadastro")
  public ResponseEntity<?> cadastrarEndereco(@RequestBody CriarEnderecoRequest request) {
    Optional<Cliente> varCliente = clienteRepositorio.findById(request.getClienteId());
    if (varCliente.isEmpty()) {
      return ResponseEntity.badRequest().body("Cliente não encontrado");
    }

    var cliente = varCliente.get();
    Endereco endereco = request.getEndereco();
    endereco.setCliente(cliente);
    endereco = enderecoRepositorio.save(endereco);
    cliente.setEndereco(endereco);
    clienteRepositorio.save(cliente);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PutMapping("/atualizar")
  public ResponseEntity<?> atualizarEndereco(@RequestBody Endereco enderecoAtualizado) {
    Optional<Endereco> enderecoExistente = enderecoRepositorio.findById(enderecoAtualizado.getId());
    if (enderecoExistente.isPresent()) {
      enderecoAtualizador.atualizar(enderecoExistente.get(), enderecoAtualizado);
      enderecoRepositorio.save(enderecoExistente.get());
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("Endereço não encontrado");
    }
  }

  @DeleteMapping("/deletar")
  public ResponseEntity<?> deletarEndereco(@RequestBody Endereco endereco) {
    Optional<Endereco> enderecoASerDeletado = enderecoRepositorio.findById(endereco.getId());
    if (enderecoASerDeletado.isEmpty()) {
      return ResponseEntity.badRequest().body("Endereço não encontrado");
    }

    var cliente = enderecoASerDeletado.get().getCliente();
    cliente.setEndereco(null);
    clienteRepositorio.save(cliente);
    enderecoRepositorio.delete(enderecoASerDeletado.get());

    return ResponseEntity.ok().build();
  }
}
