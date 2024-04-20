package com.anderson.cnab;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
public class BatchConfig {
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;

    public BatchConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    Job job(Step step, JobRepository jobRepository) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step step(
            ItemReader<TransacaoCNAB> reader,
            ItemProcessor<TransacaoCNAB, Transacao> processor,
            ItemWriter<Transacao> writer) {
        return new StepBuilder("step", jobRepository)
                .<TransacaoCNAB, Transacao>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    FlatFileItemReader<TransacaoCNAB> reader() {
        return new FlatFileItemReaderBuilder<TransacaoCNAB>()
                .name("reader")
                .resource(new FileSystemResource("Cfiles/CNAB.txt"))
                .fixedLength()
                .columns(
                        new Range(1, 1),   // Tipo
                        new Range(2, 9),   // Data
                        new Range(10, 19), // Valor
                        new Range(20, 30), // CPF
                        new Range(31, 42), // Cartão
                        new Range(43, 48), // Hora
                        new Range(49, 62), // Dono da loja
                        new Range(63, 81)  // Nome loja
                )
                .names("tipo",
                        "data",
                        "valor",
                        "cpf",
                        "cartao",
                        "hora",
                        "donoDaLoja",
                        "nomeLoja")
                .targetType(TransacaoCNAB.class)
                .build();

    }

    @Bean
    ItemProcessor<TransacaoCNAB, Transacao> processor() {
        return item -> {
            var transacao = new Transacao(
                    null, item.tipo(), null, null, item.cpf(),
                    item.cartao(), null, item.donoDaLoja().trim(),
                    item.nomeDaLoja().trim()).withValor(item.valor().divide(BigDecimal.valueOf(100)))
                    .withData(item.data())
                    .withHora(item.hora());

            return transacao;
        };
    }

    @Bean
    JdbcBatchItemWriter<Transacao> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transacao>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO
                            transacao (id, tipo, data, valor, cpf, cartao, hora, dono_da_loja, nome_da_loja)
                        VALUES
                            (:id, :tipo, :data, :valor, :cpf, :cartao, :hora, :donoDaLoja, :nomeLoja)
                     """
                ).beanMapped().build();
    }

}
