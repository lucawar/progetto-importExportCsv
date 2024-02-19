package lucaguerra.progettoimporExportCsv.config;

// QUESTO è IL FILE PRINCIPALE, RAPPRESENTA TUTTA LA CONFIGURAZIONE DEGLI STEP E DEI JOB

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lucaguerra.progettoimporExportCsv.job.mapper.PlayerMapper;
import lucaguerra.progettoimporExportCsv.job.processor.book.BookAuthorProcessor;
import lucaguerra.progettoimporExportCsv.job.processor.book.BookTitleProcessor;
import lucaguerra.progettoimporExportCsv.job.processor.player.PlayerItemProcessor;
import lucaguerra.progettoimporExportCsv.job.writer.BookWriter;
import lucaguerra.progettoimporExportCsv.job.writer.PlayerWriter;
import lucaguerra.progettoimporExportCsv.models.Book;
import lucaguerra.progettoimporExportCsv.models.Player;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class SpringBatchConfig {


    private final PlayerMapper playerMapper;


    // IMPOSTAZIONE IL JOB
    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("AVVIO JOB---------------");
        return new JobBuilder("importPlayers", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepPlayer(jobRepository, transactionManager))
                .next(stepBook(jobRepository, transactionManager))
                .build();
    }

    // STEP--------------------------------------------------------------------------------

    // IMPOSTAZIONE STEP DI LAVORO Player
    @Bean
    public Step stepPlayer(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("AVVIO STEP_PLAYER---------------");
        return new StepBuilder("stepPlayer", jobRepository).<Player, Player>
                        chunk(10, transactionManager)
                .reader(readerPlayer())
                .processor(processorPlayer())
                .writer(writerPlayer())
                .build();
    }

    // IMPOSTAZIONE STEP DI LAVORO Book
    @Bean
    public Step stepBook(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("AVVIO STEP_BOOK--------------");
        return new StepBuilder("stepBook", jobRepository).<Book, Book>
                        chunk(10, transactionManager)
                .reader(readerBook())
                .processor(processorBook())
                .writer(writerBook())
                .build();
    }

    // PLAYER--------------------------------------------------------------------------------

    // CONFIGURAZIONE READER PLAYER
    @Bean
    @StepScope
    public FlatFileItemReader<Player> readerPlayer() {
        FlatFileItemReader<Player> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/csv/player.csv"));
        itemReader.setName("csvReaderPlayer");
        itemReader.setLinesToSkip(1);
        // SERVE A INDICARE COME DEVE MAPPARE I FILE.CSV CON LA CLASSE MAPPER, SI CREA UN MAPPER DEDICATO IN QUESTO CASO PLayerLineMapper()
        itemReader.setLineMapper(playerMapper.playerlineMapper());
        return itemReader;
    }


    // CREIAMO UN BEAN PER ANDARE AD ISTANZIARE LA CLASSE PROCESSOR PER Player
    @StepScope
    @Bean
    public ItemProcessor<Player, Player> processorPlayer() {
        // Usiamo CompositeItemProcessor per unire più processi (Aggiungere i Processor desiderati in List.of)
        CompositeItemProcessor<Player, Player> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(new PlayerItemProcessor()));
        return processor;
    }


    // CREIAMO UN BEAN PER ANDARE AD ISTANZIARE LA CLASSE WRITER PER Player
    @Bean
    @StepScope
    public ItemWriter<Player> writerPlayer() {
        return new PlayerWriter();
    }


    // BOOK-----------------------------------------------------------------------------------------

    // CONFIGURAZIONE READER BOOK
    @Bean
    @StepScope
    public FlatFileItemReader<Book> readerBook(){
        // A differenza di ReaderPlayer usiamo il ReaderBuilder per configurare la mappatura
       return new FlatFileItemReaderBuilder<Book>()
               .name("csvReaderBook")
               .resource(new ClassPathResource("csv/book.csv"))
               // Di default separa i field (valori) quando trova ","
               .delimited()
               // Nel file book.csv non cè una riga di intestazione con gli attributi, quindi li andiamo a specificare
               .names(new String[]{"title", "author", "year_of_publishing"})
               // Imposto il tipo di destinazione della classe da mappare (Book)
               .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                   setTargetType(Book.class);
               }})
               .build();
    }

    // CONFIGURAZIONE PROCESSOR BOOK
    @Bean
    @StepScope
    public ItemProcessor<Book, Book> processorBook() {
        // Usiamo CompositeItemProcessor per unire più processi (Aggiungere i Processor desiderati in List.of)
        CompositeItemProcessor<Book, Book> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(new BookTitleProcessor(), new BookAuthorProcessor()));
        return processor;
    }

    // CREIAMO UN BEAN PER ANDARE AD ISTANZIARE LA CLASSE WRITER PER Book
    @Bean
    @StepScope
    public ItemWriter<Book> writerBook() {
        return new BookWriter();
    }
}
