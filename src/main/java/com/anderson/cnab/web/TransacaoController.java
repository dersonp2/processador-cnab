package com.anderson.cnab.web;

import com.anderson.cnab.entity.Transacao;
import com.anderson.cnab.entity.TransacaoReport;
import com.anderson.cnab.service.TransacoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("transacoes")
public class TransacaoController {

    private final TransacoService service;

    public TransacaoController(TransacoService service) {
        this.service = service;
    }

    @GetMapping("report")
    List<TransacaoReport> listAllReport() {
        return null;
    }

    @GetMapping
    Iterable<Transacao> listAll() {
        return service.listTotaisTransacoes();
    }

    @GetMapping("order")
    Iterable<TransacaoReport> listAllOrder() {
        return service.listTotaisTransacoesPorNomeLoja();
    }
}
