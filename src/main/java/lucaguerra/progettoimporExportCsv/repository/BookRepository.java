package lucaguerra.progettoimporExportCsv.repository;

import lucaguerra.progettoimporExportCsv.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
