package lucaguerra.progettoimporExportCsv.repository;

import lucaguerra.progettoimporExportCsv.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

}
