package lucaguerra.progettoimporExportCsv.job;

import lucaguerra.progettoimporExportCsv.models.Player;
import org.springframework.batch.item.ItemProcessor;

// QUESTA CLASSE RAPPRESENTA UNA DELLE 3 OPERAZIONI FONDAMENTALI (ItemReader, ItemProcessor, ItemWriter)
// IMPLEMENTA L'INTERFACCIA ItemProcessor
// LO SCOPO DEL PROCESSOR è QUELLO DI PRENDERE UN OGGETTO (Player in questo caso) E RESTITUIRE UN OGGETTO (Player in questo caso)
// IN ALTRI CONTESTI LA SORGENTE PUO ESSERE DIVERSA DALLA DESTINAZIONE (Player != Player)
public class PlayerItemProcessor implements ItemProcessor<Player, Player> {

    // GESTIAMO L'ELABORAZIONE (process) DEI DATI, IN QUESTA CASO ANDIAMO A PRENDERE I PLAYER CON POSIZIONE FORWARD
    @Override
    public Player process(Player player) throws Exception {
       return player;
    }
}
