package com.anderson.cnab.repository;

import com.anderson.cnab.entity.Transacao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransacaoRepository extends CrudRepository<Transacao, Long> {

    List<Transacao> findAllByOrderByNomeDaLojaAscIdDesc();
}
