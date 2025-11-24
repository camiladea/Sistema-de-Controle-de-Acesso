package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexaoBancoDados {

    private static final Logger LOGGER = Logger.getLogger(ConexaoBancoDados.class.getName());
    private static final String DB_PROPERTIES_FILE = "config.properties";
    private static String DB_PATH;

    static {
        try (InputStream input = ConexaoBancoDados.class.getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
            Properties prop = new Properties();
            if (input == null) {
                LOGGER.log(Level.SEVERE, "Arquivo de propriedades do banco de dados não encontrado: " + DB_PROPERTIES_FILE);
                // Fallback para um caminho padrão se o arquivo não for encontrado
                DB_PATH = "sistema_acesso.db"; 
            } else {
                prop.load(input);
                DB_PATH = prop.getProperty("db.path", "sistema_acesso.db");
                LOGGER.log(Level.INFO, "Caminho do banco de dados carregado de {0}: {1}", new Object[]{DB_PROPERTIES_FILE, DB_PATH});
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar o arquivo de propriedades do banco de dados", ex);
            DB_PATH = "sistema_acesso.db"; // Fallback em caso de erro de leitura
        }

        java.io.File dbFile = new java.io.File(DB_PATH);
        if (!dbFile.exists() || dbFile.length() == 0) {
            LOGGER.log(Level.INFO, "Banco de dados não encontrado ou vazio. Inicializando...");
            DatabaseInitializer.initializeDatabase();
        }
    }

    public static Connection getConexao() throws SQLException {
        String url = "jdbc:sqlite:" + DB_PATH;
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao conectar ao banco de dados SQLite em " + DB_PATH, e);
            throw e;
        }
    }
}