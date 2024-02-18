package lucaguerra.progettoimporExportCsv.config;

// QUESTO è IL FILE PRINCIPALE, RAPPRESENTA TUTTA LA CONFIGURAZIONE DEGLI STEP E DEI JOB

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lucaguerra.progettoimporExportCsv.job.PlayerItemProcessor;
import lucaguerra.progettoimporExportCsv.models.Player;
import lucaguerra.progettoimporExportCsv.repository.PlayerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
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


    private PlayerRepository playerRepository;



    // IMPOSTAZIONE READER, (LETTURA FILE CSV)
    // ANDIAMO A MAPPARE LA CLASSE (Player in questo caso) PER LEGGERE I DATI DAL FILE.CSV
    // CONFIGURIAMO IL READER
    @Bean
    @StepScope
    public FlatFileItemReader<Player> reader() {
        FlatFileItemReader<Player> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/csv/player.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        // SERVE A INDICARE COME DEVE MAPPARE I FILE.CSV CON LA CLASSE MAPPER
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }


    // QUESTO METODO CONFIGURA LA MAPPATURA DELLA CLASSE
    // POSSIAMO USARE QUESTA CONFIGURAZIONE SE CONOSCIAMO GLI ATTRIBUTI DEL FILE.CSV E DOBBIAMO LEGGERLI TUTTI
    // SE VOGLIAMO LEGGERE SOLO ALCUNI ATTRIBUTI DEL FILE.CSV ABBIAMO BISOGNO DI CREARE UNA CONFIGURAZIONE PESONALIZZATA
    // CREANDO UNA CLASSE CHE IMPLEMENTENTA FieldSetMapper
    private LineMapper<Player> lineMapper() {
        // SPECIFICA IL TIPO DELLA CLASSE
        DefaultLineMapper<Player> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        // SPEZZETTA IN FIELD (valori) QUANDO TROVA "," (CONFIGURIAMO IN BASE AI CARATTERI SPECIALI PRESENTI NEL FILE.CSV)
        lineTokenizer.setDelimiter(",");
        // DI BASE SE Player NON VIENE TROVATA VIENE LANCIATA UN ECCEZIONE
        // SE LO IMPOSTIAMO A false QUANDO Player NON VIENE TROVATA NON VIENE SOLLEVATA UN ECCEZIONE
        lineTokenizer.setStrict(false);
        // IMPOSTAZIONE NAME OVVERO GLI ATTRIBUTI DELLA CLASSE, DEVONO ESSERE UGUALI AGLI ATTRIBUTI DELLA CLASSE (Player in questo caso)
        lineTokenizer.setNames("id", "firstName", "lastName", "position", "team");

        BeanWrapperFieldSetMapper<Player> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        // IMPOSTA IL TARGET DELLA CLASSE DA MAPPARE
        fieldSetMapper.setTargetType(Player.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        // I FIELD (valori) DEVONO ESSERE MAPPATI SUI FIELD DI Player, E LA CORRISPONDENZA DEI FIELD SEGUE L'ORDINE DI setNames;
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    // CREIAMO UN BEAN PER ANDARE AD ISTANZIARE LA CLASSE PlayerItemProcessor

    @StepScope
    @Bean
    public ItemProcessor<Player, Player> processor() {
        CompositeItemProcessor<Player, Player> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(new PlayerItemProcessor(), new PlayerItemProcessor()));
        return processor;
    }

    // PARTE DEDICATA AL WRITER

    // QUESTA CLASSE PERMETTE DI ANDARE A RECUPERARE IL REPOSITORY DELLA CLASSE (Player in questo caso)
    @Bean
    @StepScope
    public RepositoryItemWriter<Player> writer() {
        RepositoryItemWriter<Player> writer = new RepositoryItemWriter<>();
        writer.setRepository(playerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("avvio del job");
        return new JobBuilder("importPlayers", jobRepository)

                .start(step1(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository).<Player, Player>
                        chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


}
