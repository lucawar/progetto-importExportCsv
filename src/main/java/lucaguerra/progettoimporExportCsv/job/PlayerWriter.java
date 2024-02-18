package lucaguerra.progettoimporExportCsv.job;

import lombok.extern.slf4j.Slf4j;
import lucaguerra.progettoimporExportCsv.models.Player;
import lucaguerra.progettoimporExportCsv.repository.PlayerRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class PlayerWriter implements ItemWriter<Player> {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void write(Chunk<? extends Player> chunk) throws Exception {
        log.info("Writing: {}",chunk.getItems().size());
        playerRepository.saveAll(chunk.getItems());
    }


}
