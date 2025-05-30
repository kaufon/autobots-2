package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.TelefoneControler;
import com.autobots.automanager.entidades.Telefone;

@Component
public class AdicionarLinkTelefoneServico implements AdicionaLinkServico<Telefone> {
  @Override
  public void adicionarLink(List<Telefone> documentos) {
    for (Telefone documento : documentos) {
      long id = documento.getId();
      Link linkProprio = WebMvcLinkBuilder
          .linkTo(WebMvcLinkBuilder.methodOn(TelefoneControler.class).obterTelefone(id))
          .withSelfRel();
      documento.add(linkProprio);
    }
  }

  @Override
  public void adicionarLink(Telefone documento) {
    Link linkProprio = WebMvcLinkBuilder
        .linkTo(WebMvcLinkBuilder
            .methodOn(TelefoneControler.class)
            .obterTelefones())
        .withRel("documentos");
    documento.add(linkProprio);
  }
}
