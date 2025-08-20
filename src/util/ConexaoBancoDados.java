package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBancoDados {
    private static final String URL = "jdbc:mysql://localhost:3306/projeto_pi_db";
    private static final String USUARIO = "root";
    private static final String SENHA = "admin";
    private static volatile Connection conexao = null;

    private ConexaoBancoDados() {}

    @SuppressWarnings("DoubleCheckedLocking")
    public static Connection getConexao() {
        if (conexao == null) {
            synchronized (ConexaoBancoDados.class) {
                if (conexao == null) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                        
                       if (conexao != null) {
						System.out.println("Conexão estabelecida");
					}
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Driver JDBC do MySQL não encontrado!", e);
                    } catch (SQLException e) {
                        throw new RuntimeException("Falha ao conectar ao banco de dados.", e);
                    }
                }
            }
        }
        return conexao;
    }
}