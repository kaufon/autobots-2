package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EnderecoControler;
import com.autobots.automanager.entidades.Endereco;

@Component
public class AdicionarLinkEnderecoServico implements AdicionaLinkServico<Endereco> {
  @Override
  public void adicionarLink(List<Endereco> documentos) {
    for (Endereco documento : documentos) {
      long id = documento.getId();
      Link linkProprio = WebMvcLinkBuilder
          .linkTo(WebMvcLinkBuilder.methodOn(EnderecoControler.class).obterEndereco(id))
          .withSelfRel();
      documento.add(linkProprio);
    }
  }

  @Override
  public void adicionarLink(Endereco documento) {
    Link linkProprio = WebMvcLinkBuilder
        .linkTo(WebMvcLinkBuilder
            .methodOn(EnderecoControler.class)
            .obterEnderecos())
        .withRel("documentos");
    documento.add(linkProprio);
  }
}
