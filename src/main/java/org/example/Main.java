/* Script per la creazione della tabella animali

CREATE TABLE ANIMALI
(
    id INT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    specie VARCHAR(255) NOT NULL,
    habitat VARCHAR(255) NOT NULL,
    dieta VARCHAR(255) NOT NULL
);
 */

package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.example.modelli.Animale;
import org.example.modelli.Zoo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance();

        String pathJson = "src/main/resources/animali.json";
        String pathSchema = "src/main/resources/animali.schema.json";

        try(
            InputStream jsonStream = new FileInputStream(pathJson);
            InputStream schemaStream = new FileInputStream(pathSchema);
            ) {

            //#region validazioneJson
            JsonNode jsonDaValidare = mapper.readTree(jsonStream);
            JsonSchema schema = factory.getSchema(schemaStream);
            Set<ValidationMessage> errori = schema.validate(jsonDaValidare);

            if(!errori.isEmpty()) {
                //Errore durante la validazione --> Esci
                for (ValidationMessage error : errori)
                    System.err.println(error.getMessage());

                return;
            }

            //Validazione corretta
            System.out.println("Successo: Il JSON rispetta lo schema");
            //#endregion

            //#region Deserializzazione
            Gson gson = new Gson();
            String json = readStringFromFile(pathJson);
            Zoo zoo = gson.fromJson(json, Zoo.class);
            List<Animale> animali = zoo.getAnimali();

            System.out.println("\n--------------------------------------\nNumero di animali letti: " + animali.size() + "\nJSON deserializzato: " + zoo.toString());
            //#endregion

            //#region PopolamentoDB
            String pathDB = "jdbc:mysql://localhost:3306";
            String db = "/ZOO";
            String user = "root";
            String password = "";

            Connection conn =  DriverManager.getConnection(pathDB + db, user, password);
            System.out.println("\n--------------------------------------\nConnesso al DB");


            String queryInsert = "INSERT INTO ANIMALI(id, nome, specie, habitat, dieta) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(queryInsert);
            int righeInserite = 0;

            for(Animale a : animali){
                stmt.setInt(1, a.getId());
                stmt.setString(2, a.getNome());
                stmt.setString(3, a.getSpecie());
                stmt.setString(4, a.getHabitat());
                stmt.setString(5, a.getDieta());

                int riga = stmt.executeUpdate();
                if(riga!=0)
                    righeInserite++;
            }

            System.out.println("Inserite " + righeInserite + " righe su " + animali.size());
            conn.close();
            //#endregion
        } catch (FileNotFoundException e) {
            System.err.println("File non trovato: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String readStringFromFile(String path) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(path));
        return new String(content);
    }
}