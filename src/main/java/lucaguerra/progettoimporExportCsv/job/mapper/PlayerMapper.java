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

    // QUESTO METODO CONFIGURA LA MAPPATURA DELLA CLASSE Player
    // POSSIAMO USARE QUESTA CONFIGURAZIONE SE CONOSCIAMO GLI ATTRIBUTI DEL FILE.CSV E DOBBIAMO LEGGERLI TUTTI
    // SE VOGLIAMO LEGGERE SOLO ALCUNI ATTRIBUTI DEL FILE.CSV ABBIAMO BISOGNO DI CREARE UNA CONFIGURAZIONE PESONALIZZATA
    // CREANDO UNA CLASSE CHE IMPLEMENTENTA FieldSetMapper
    public LineMapper<Player> playerlineMapper() {
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
}
