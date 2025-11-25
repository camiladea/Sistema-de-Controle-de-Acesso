package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBancoDados {

    // path to database file, we use sistema_acesso.db in project root
    private static final String URL = "jdbc:sqlite:sistema_acesso.db";

    private static Connection conexao = null;

    public static Connection getConexao() {
        try {
            // load driver for SQLite
            Class.forName("org.sqlite.JDBC");
            
            // sets up connection
            conexao = DriverManager.getConnection(URL);
            System.out.println("Conexão com o SQLite estabelecida com sucesso.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC do SQLite não encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco de dados SQLite.");
            e.printStackTrace();
        }
        return conexao;
    }

    
    public static void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("Conexão com o SQLite fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o SQLite.");
            e.printStackTrace();
        }
    }
}