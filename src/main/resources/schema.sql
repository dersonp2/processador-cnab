CREATE TABLE IF NOT EXISTS transacao (
                           id SERIAL PRIMARY KEY,
                           tipo INT,
                           data DATE,
                           valor DECIMAL(19,2),
                           cpf BIGINT,
                           cartao VARCHAR(255),
                           hora TIME,
                           dono_da_loja VARCHAR(50),
                           nome_da_loja VARCHAR(50)
);