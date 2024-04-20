package com.anderson.cnab;

import java.math.BigDecimal;

public record TransacaoCNAB(
        Integer tipo,
        String data,
        BigDecimal valor,
        Long cpf,
        String cartao,
        String hora,
        String donoDaLoja,
        String nomeDaLoja
) {
}
