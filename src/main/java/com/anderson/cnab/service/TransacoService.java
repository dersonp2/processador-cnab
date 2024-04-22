package com.anderson.cnab.service;

import com.anderson.cnab.entity.TipoTransacao;
import com.anderson.cnab.entity.Transacao;
import com.anderson.cnab.entity.TransacaoReport;
import com.anderson.cnab.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
public class TransacoService {
    private final TransacaoRepository repository;

    public TransacoService(TransacaoRepository repository) {
        this.repository = repository;
    }

    public Iterable<Transacao> listTotaisTransacoes() {
        var transacoes = repository.findAll();
        return transacoes;
    }

    public List<TransacaoReport> listTotaisTransacoesPorNomeLoja() {
        var transacoes = repository.findAllByOrderByNomeDaLojaAscIdDesc();

        var reportMap = new LinkedHashMap<String, TransacaoReport>();

        transacoes.forEach(transacao -> {
            String nomeDaLoja = transacao.nomeDaLoja();
            var tipoTransacao = TipoTransacao.findByTipo(transacao.tipo());
            BigDecimal valor = transacao.valor().multiply(
                tipoTransacao.getSinal()
            );

            reportMap.compute(nomeDaLoja, (key, existingReport) -> {
                var report = Objects.requireNonNullElseGet(existingReport,
                        () -> new TransacaoReport(BigDecimal.ZERO, key, new ArrayList<>()));

                return report.addTotal(valor).addTransacao(transacao.withValor(valor));
            });
        });
        return new ArrayList<>(reportMap.values());
    }
}
