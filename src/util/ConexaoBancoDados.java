package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/* Se o erro for Erro ao buscar usuário: Driver JDBC do MySQL não encontrado: ir na class path 
 * <classpathentry kind="lib" path="C:\Users\aluno\Downloads\Sistema-de-Controle-de-Acesso\lib\mysql-connector-j-9.3.0.jar"/>
*/
public class ConexaoBancoDados {
    private static final String URL = "jdbc:mysql://localhost:3306/projeto_pi_db";
    private static final String USUARIO = "root";
    private static final String SENHA = "admin";

    // O construtor privado continua para evitar instâncias
    private ConexaoBancoDados() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    // Este método agora sempre cria e retorna uma NOVA conexão
    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("Nova conexão estabelecida");
            return conexao;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado!", e);
        }
    }

    // Método para fechar a conexão, se necessário
    public static void fecharConexao(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexão fechada");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}