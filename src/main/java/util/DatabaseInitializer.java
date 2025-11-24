package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
                createDefaultAdmin(conn);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha ao inicializar o banco de dados", e);
        }
    }

    private static void createDefaultAdmin(Connection conn) {
        String adminLogin = "admin";
        try (PreparedStatement checkUser = conn.prepareStatement("SELECT 1 FROM Usuario WHERE login = ?")) {
            checkUser.setString(1, adminLogin);
            try (ResultSet rs = checkUser.executeQuery()) {
                if (rs.next()) {
                    LOGGER.log(Level.INFO, "Usuário admin padrão já existe.");
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha ao verificar a existência do usuário admin.", e);
            return;
        }

        try (PreparedStatement insertAdmin = conn.prepareStatement(
                "INSERT INTO Usuario (nome, cpf, email, tipoUsuario, ativo, login, senhaHash) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            insertAdmin.setString(1, "Administrador Principal");
            insertAdmin.setString(2, "000.000.000-00");
            insertAdmin.setString(3, "admin@sistema.com");
            insertAdmin.setString(4, "Administrador");
            insertAdmin.setInt(5, 1);
            insertAdmin.setString(6, adminLogin);
            insertAdmin.setString(7, HashUtils.hashSenha("admin123"));
            insertAdmin.executeUpdate();
            LOGGER.log(Level.INFO, "Usuário admin padrão criado com sucesso.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha ao criar o usuário admin padrão.", e);
        }
    }
}
