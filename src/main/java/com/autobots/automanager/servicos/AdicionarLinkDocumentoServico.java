package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.DocumentoControler;
import com.autobots.automanager.entidades.Documento;

@Component
public class AdicionarLinkDocumentoServico implements AdicionaLinkServico<Documento> {
  @Override
  public void adicionarLink(List<Documento> documentos) {
    for (Documento documento : documentos) {
      long id = documento.getId();
      Link linkProprio = WebMvcLinkBuilder
          .linkTo(WebMvcLinkBuilder.methodOn(DocumentoControler.class).obterDocumento(id))
          .withSelfRel();
      documento.add(linkProprio);
    }
  }

  @Override
  public void adicionarLink(Documento documento) {
    Link linkProprio = WebMvcLinkBuilder
        .linkTo(WebMvcLinkBuilder
            .methodOn(DocumentoControler.class)
            .obterDocumentos())
        .withRel("documentos");
    documento.add(linkProprio);
  }
}
