package lucaguerra.progettoimporExportCsv.job.mapper;

import lombok.Data;
import lucaguerra.progettoimporExportCsv.models.Player;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.stereotype.Component;


@Component
@Data
public class PlayerMapper {

    // QUESTA CLASSE CONTIENE UN METODO PER CONFIGURARE LA MAPPATURA DELLA CLASSE Player
    // POSSIAMO USARE QUESTA CONFIGURAZIONE SE CONOSCIAMO GLI ATTRIBUTI DEL FILE.CSV E DOBBIAMO LEGGERLI TUTTI
    // SE VOGLIAMO LEGGERE SOLO ALCUNI ATTRIBUTI DEL FILE.CSV ABBIAMO BISOGNO DI CREARE UNA CONFIGURAZIONE PESONALIZZATA
    // CREANDO UNA CLASSE CHE IMPLEMENTENTA FieldSetMapper
    public LineMapper<Player> playerlineMapper() {
        // Specifica il tipo di classe
        DefaultLineMapper<Player> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        // Spezzata in field (valori) quando trova "," (Configuriamo a seconda dei caratteri speciali presenti nel file.csv)
        lineTokenizer.setDelimiter(",");
        // Di default se la classe Player non viene trovata vine lanciate un eccezione, impostiamo a false per evitare ciò
        lineTokenizer.setStrict(false);
        // Impostiamo gli attributi della classe (In questo caso li conosciamo e li dovremo impostare cosi)
        lineTokenizer.setNames("id", "firstName", "lastName", "position", "team");

        BeanWrapperFieldSetMapper<Player> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        // Imposta il target da mappare (Player), da qui prendera tutte le info sulla classe Player
        fieldSetMapper.setTargetType(Player.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        // I field devono essere mappati sui filed di Player, e la corrispondenza dei field segue l'ordine di SetNames
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
