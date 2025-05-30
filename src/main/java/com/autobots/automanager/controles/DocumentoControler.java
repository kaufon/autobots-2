package com.autobots.automanager.controles;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.servicos.AdicionarLinkDocumentoServico;

import lombok.Data;

@RestController
@RequestMapping("/documento")
public class DocumentoControler {

  @Autowired
  private DocumentoRepositorio documentoRepositorio;

  @Autowired
  private ClienteRepositorio clienteRepositorio;

  @Autowired
  private DocumentoAtualizador documentoAtualizador;

  @Autowired
  private AdicionarLinkDocumentoServico adicionadorLink;

  @Data
  private static class CriarDocumentoRequest {
    private Documento documento;
    private long clienteId;
  }

  @GetMapping("/documentos")
  public ResponseEntity<List<Documento>> obterDocumentos() {
    List<Documento> documentos = documentoRepositorio.findAll();
    if (documentos.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      adicionadorLink.adicionarLink(documentos);
      return ResponseEntity.ok(documentos);
    }
  }

  @GetMapping("/documento/{id}")
  public ResponseEntity<Documento> obterDocumento(@PathVariable long id) {
    Optional<Documento> documento = documentoRepositorio.findById(id);
    if (documento.isPresent()) {
      adicionadorLink.adicionarLink(documento.get());
      return ResponseEntity.ok(documento.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/cadastro")
  public ResponseEntity<?> cadastrarDocumento(@RequestBody CriarDocumentoRequest request) {
    Optional<Cliente> varCliente = clienteRepositorio.findById(request.getClienteId());
    if (varCliente.isEmpty()) {
      return ResponseEntity.badRequest().body("Cliente não encontrado");
    }

    var cliente = varCliente.get();
    Documento documento = request.getDocumento();
    documento.setCliente(cliente);
    documento = documentoRepositorio.save(documento);
    cliente.getDocumentos().add(documento);
    clienteRepositorio.save(cliente);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PutMapping("/atualizar")
  public ResponseEntity<?> atualizarDocumento(@RequestBody Documento documento) {
    Optional<Documento> docExistente = documentoRepositorio.findById(documento.getId());
    if (docExistente.isPresent()) {
      documentoAtualizador.atualizar(docExistente.get(), documento);
      documentoRepositorio.save(docExistente.get());
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("Documento não encontrado");
    }
  }

  @DeleteMapping("/deletar")
  public ResponseEntity<?> deletarDocumento(@RequestBody Documento documento) {
    Optional<Documento> doc = documentoRepositorio.findById(documento.getId());
    if (doc.isPresent()) {
      documentoRepositorio.delete(doc.get());
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("Documento não encontrado");
    }
  }
}
