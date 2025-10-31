package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central SQLite connection helper.
 * Keeps your DB filename ("sistema_acesso.db") as before but returns a fresh
 * Connection each call (safer than a long-lived static Connection).
 */
public class ConexaoBancoDados {

    private static final String URL = "jdbc:sqlite:sistema_acesso.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC do SQLite não encontrado.");
            e.printStackTrace();
        }
    }

    public static Connection getConexao() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL);
        // optional debug:
        // System.out.println("Conexão com o SQLite estabelecida com sucesso.");
        return conexao;
    }

    public static void fecharConexao(Connection c) {
        try {
            if (c != null && !c.isClosed()) {
                c.close();
                // System.out.println("Conexão com o SQLite fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o SQLite.");
            e.printStackTrace();
        }
    }
}
