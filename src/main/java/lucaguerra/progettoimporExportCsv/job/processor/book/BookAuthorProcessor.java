package lucaguerra.progettoimporExportCsv.job.processor.book;

import lombok.extern.slf4j.Slf4j;
import lucaguerra.progettoimporExportCsv.models.Book;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class BookAuthorProcessor implements ItemProcessor<Book, Book> {
    @Override
    public Book process(Book item) throws Exception {
        log.info("PROCESSO PER AUTHOR {}", item);
        item.setAuthor("By" + item.getAuthor());
        return item;
    }
}
