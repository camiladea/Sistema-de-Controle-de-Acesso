package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.RegistroAcesso;
import util.ConexaoBancoDados;

public class RegistroAcessoDAO {

    public void salvar(RegistroAcesso registro) {
        String sql = "INSERT INTO RegistroAcesso (dataHora, usuarioId, status, origem) VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conexao.prepareStatement(sql)) {

            pstm.setTimestamp(1, Timestamp.valueOf(registro.getDataHora()));

            if (registro.getUsuarioId() <= 0) {
                pstm.setNull(2, Types.INTEGER);
            } else {
                pstm.setInt(2, registro.getUsuarioId());
            }

            pstm.setString(3, registro.getStatus());
            pstm.setString(4, registro.getOrigem());

            pstm.execute();
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
                     "WHERE ra.dataHora BETWEEN ? AND ? " +
                     "ORDER BY ra.dataHora DESC";

        try (Connection conexao = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conexao.prepareStatement(sql)) {

            pstm.setTimestamp(1, Timestamp.valueOf(inicio));
            pstm.setTimestamp(2, Timestamp.valueOf(fim));

            try (ResultSet rset = pstm.executeQuery()) {
                while (rset.next()) {

                    RegistroAcesso registro = new RegistroAcesso(
                            rset.getTimestamp("dataHora").toLocalDateTime(),
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
