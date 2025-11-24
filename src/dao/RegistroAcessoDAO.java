package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.RegistroAcesso;

public class RegistroAcessoDAO {

    
    public void salvar(RegistroAcesso registro) {
        String sql = "INSERT INTO RegistroAcesso (dataHora, usuarioId, status, origem) VALUES (?, ?, ?, ?)";
        
        try (Connection conexao = DriverManager.getConnection("jdbc:sqlite:seu_banco_de_dados.db");
             PreparedStatement pstm = conexao.prepareStatement(sql)) {

            
            pstm.setString(1, registro.getDataHora().toString());

            if (registro.getUsuarioId() <= 0) {
                pstm.setNull(2, Types.INTEGER);
            } else {
                pstm.setInt(2, registro.getUsuarioId());
            }

            pstm.setString(3, registro.getStatus());
            pstm.setString(4, registro.getOrigem());

            pstm.executeUpdate();  // Usar executeUpdate para inserções
        } catch (SQLException e) {
            System.err.println("Erro ao salvar registro de acesso: " + e.getMessage());
        }
    }

    
    public List<RegistroAcesso> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<RegistroAcesso> registros = new ArrayList<>();
        String sql = "SELECT ra.id, ra.dataHora, ra.usuarioId, u.nome AS nomeUsuario, " +
                     "ra.status, ra.origem " +
                     "FROM RegistroAcesso ra " +
                     "LEFT JOIN Usuario u ON ra.usuarioId = u.id " +
                     "WHERE ra.dataHora >= ? AND ra.dataHora < ? " +
                     "ORDER BY ra.dataHora DESC";

        try (Connection conexao = DriverManager.getConnection("jdbc:sqlite:seu_banco_de_dados.db");
             PreparedStatement pstm = conexao.prepareStatement(sql)) {

            
            pstm.setString(1, inicio.toString());
            pstm.setString(2, fim.toString());

            try (ResultSet rset = pstm.executeQuery()) {
                while (rset.next()) {
                    RegistroAcesso registro = new RegistroAcesso(
                            LocalDateTime.parse(rset.getString("dataHora")),
                            rset.getInt("usuarioId"),
                            rset.getString("status"),
                            rset.getString("origem")
                    );

                    registro.setId(rset.getInt("id"));
                    registro.setNomeUsuario(rset.getString("nomeUsuario"));

                    registros.add(registro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar registros por período: " + e.getMessage());
        }

        return registros;
    }
}
