package lucaguerra.progettoimporExportCsv.config;

// QUESTO è IL FILE PRINCIPALE, RAPPRESENTA TUTTA LA CONFIGURAZIONE DEGLI STEP E DEI JOB

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lucaguerra.progettoimporExportCsv.job.mapper.PlayerMapper;
import lucaguerra.progettoimporExportCsv.job.processor.PlayerItemProcessor;
import lucaguerra.progettoimporExportCsv.job.writer.PlayerWriter;
import lucaguerra.progettoimporExportCsv.models.Player;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class SpringBatchConfig {


    private final PlayerMapper playerMapper;


    // IMPOSTIAMO IL JOB
    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("AVVIO JOB---------------");
        return new JobBuilder("importPlayers", jobRepository)
                .start(stepPlayer(jobRepository, transactionManager))
                .build();
    }

    // IMPOSTIAMO STEP DI LAVORO Player
    @Bean
    public Step stepPlayer(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("AVVIO STEP_PLAYER---------------");
        return new StepBuilder("stepPlayer", jobRepository).<Player, Player>
                        chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    // CONFIGURIAMO IL READER PER Player
    @Bean
    @StepScope
    public FlatFileItemReader<Player> reader() {
        FlatFileItemReader<Player> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/csv/player.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        // SERVE A INDICARE COME DEVE MAPPARE I FILE.CSV CON LA CLASSE MAPPER, SI CREA UN MAPPER DEDICATO IN QUESTO CASO PLayerLineMapper()
        itemReader.setLineMapper(playerMapper.playerlineMapper());
        return itemReader;
    }


    // CREIAMO UN BEAN PER ANDARE AD ISTANZIARE LA CLASSE PROCESSOR PER Player
    // USIAMO CompositeItemProcessor PER UNIRE PIù PROCESSI (AGGIUNGERE I PROCESSOR DESIDERATI IN LIST.OF)
    @StepScope
    @Bean
    public ItemProcessor<Player, Player> processor() {
        CompositeItemProcessor<Player, Player> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(new PlayerItemProcessor()));
        return processor;
    }


    // CREIAMO UN BEAN PER ANDARE AD ISTANZIARE LA CLASSE WRITER PER Player
    @Bean
    @StepScope
    public ItemWriter<Player> writer() {
        return new PlayerWriter();
    }




}
