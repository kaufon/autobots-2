package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.modelo.ClienteSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.servicos.AdicionarLinkClienteServico;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {
  @Autowired
  private ClienteRepositorio repositorio;

  @Autowired
  private DocumentoRepositorio documentoRepositorio;

  @Autowired
  private TelefoneRepositorio telefoneRepositorio;

  @Autowired
  private ClienteSelecionador selecionador;

  @Autowired
  private AdicionarLinkClienteServico adicionadorLink;

  @GetMapping("/cliente/{id}")
  public ResponseEntity<Cliente> obterCliente(@PathVariable long id) {
    List<Cliente> clientes = repositorio.findAll();
    Cliente cliente = selecionador.selecionar(clientes, id);
    if (cliente == null) {
      return ResponseEntity.notFound().build();
    } else {
      adicionadorLink.adicionarLink(cliente);
      return ResponseEntity.ok(cliente);
    }

  }

  @GetMapping("/clientes")
  public ResponseEntity<List<Cliente>> obterClientes() {
    List<Cliente> clientes = repositorio.findAll();
    if (clientes.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      adicionadorLink.adicionarLink(clientes);
      return ResponseEntity.ok(clientes);
    }
  }

  @PostMapping("/cadastro")
  public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
    HttpStatus status = HttpStatus.CREATED;
    if (cliente.getId() == null) {
      status = HttpStatus.CREATED;
      repositorio.save(cliente);
      cliente.getDocumentos().forEach(documento -> {
        documento.setCliente(cliente);
        documentoRepositorio.save(documento);
      });
      cliente.getTelefones().forEach(telefone -> {
        telefone.setCliente(cliente);
        telefoneRepositorio.save(telefone);
      });
    }
    return new ResponseEntity<>(status);
  }

  @PutMapping("/atualizar")
  public ResponseEntity<?> atualizarCliente(@RequestBody Cliente atualizacao) {
    HttpStatus status = HttpStatus.CONFLICT;
    Optional<Cliente> cliente = repositorio.findById(atualizacao.getId());
    if (cliente.isPresent()) {
      ClienteAtualizador atualizador = new ClienteAtualizador();
      atualizador.atualizar(cliente.get(), atualizacao);
      repositorio.save(cliente.get());
      status = HttpStatus.OK;
    } else {
      status = HttpStatus.BAD_REQUEST;
    }
    return new ResponseEntity<>(status);
  }

  @DeleteMapping("/excluir")
  public ResponseEntity<?> excluirCliente(@RequestBody Cliente exclusao) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    Optional<Cliente> cliente = repositorio.findById(exclusao.getId());
    if (cliente.isPresent()) {
      repositorio.delete(cliente.get());
      status = HttpStatus.OK;
    }
    return new ResponseEntity<>(status);
  }
}
