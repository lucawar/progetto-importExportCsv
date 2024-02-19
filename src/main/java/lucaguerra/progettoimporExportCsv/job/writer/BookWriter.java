package lucaguerra.progettoimporExportCsv.job.writer;

import lombok.extern.slf4j.Slf4j;
import lucaguerra.progettoimporExportCsv.models.Book;
import lucaguerra.progettoimporExportCsv.repository.BookRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BookWriter implements ItemWriter<Book> {

    @Autowired
    BookRepository bookRepository;

    @Override
    public void write(Chunk<? extends Book> chunk) throws Exception {
        log.info("Writing: {}",chunk.getItems().size());
        bookRepository.saveAll(chunk.getItems());
    }
}
