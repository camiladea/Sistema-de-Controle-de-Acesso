package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    private static final String SCRIPT_FILE = "projeto_pi_db.sql";

    public static void initializeDatabase() {
        try (InputStream inputStream = ConexaoBancoDados.class.getClassLoader().getResourceAsStream(SCRIPT_FILE)) {
            if (inputStream == null) {
                LOGGER.log(Level.SEVERE, "Arquivo de script do banco de dados não encontrado: " + SCRIPT_FILE);
                return;
            }

            String script = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

            try (Connection conn = ConexaoBancoDados.getConexao(); Statement stmt = conn.createStatement()) {
                // O script pode conter múltiplos comandos separados por ';'
                for (String sql : script.split(";")) {
                    if (!sql.trim().isEmpty()) {
                        stmt.execute(sql);
                    }
                }
                LOGGER.log(Level.INFO, "Banco de dados inicializado com sucesso a partir de " + SCRIPT_FILE);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Falha ao inicializar o banco de dados", e);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha ao inicializar o banco de dados", e);
        }
    }
}
