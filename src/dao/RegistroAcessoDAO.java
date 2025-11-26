package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.RegistroAcesso;
import util.ConexaoBancoDados;

public class RegistroAcessoDAO {

    public void salvar(RegistroAcesso registro) {

        String sql = "INSERT INTO RegistroAcesso (dataHora, usuarioId, status, origem) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoBancoDados.getConexao();
                PreparedStatement pstm = conexao.prepareStatement(sql)) {

            pstm.setString(1, registro.getDataHora().toString()); // Converte LocalDateTime para String no formato ISO

            // Lógica para lidar com ID de usuário nulo (ex: acesso negado)
            if (registro.getUsuarioId() <= 0) {
                pstm.setNull(2, Types.INTEGER); // Define como NULL no banco
            } else {
                pstm.setInt(2, registro.getUsuarioId()); // Define o ID do usuário
            }

            pstm.setString(3, registro.getStatus());
            pstm.setString(4, registro.getOrigem());

            pstm.executeUpdate(); // Executa a inserção
        } catch (SQLException e) {
            System.err.println("Erro ao salvar registro de acesso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<RegistroAcesso> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            return Collections.emptyList(); // Retorna lista vazia se o período for inválido
        }

        List<RegistroAcesso> registros = new ArrayList<>();
        String sql = "SELECT ra.id, ra.dataHora, ra.usuarioId, u.nome AS nomeUsuario, " +
                "ra.status, ra.origem " +
                "FROM RegistroAcesso ra " +
                "LEFT JOIN Usuario u ON ra.usuarioId = u.id " +
                "WHERE ra.dataHora >= ? AND ra.dataHora < ? " +
                "ORDER BY ra.dataHora DESC";

        try (Connection conexao = ConexaoBancoDados.getConexao();
                PreparedStatement pstm = conexao.prepareStatement(sql)) {

            pstm.setString(1, inicio.toString()); // Data de início
            pstm.setString(2, fim.toString()); // Data de fim

            try (ResultSet rset = pstm.executeQuery()) {
                while (rset.next()) {
                    RegistroAcesso registro = new RegistroAcesso(
                            LocalDateTime.parse(rset.getString("dataHora")), // Cria LocalDateTime a partir da String
                            rset.getInt("usuarioId"),
                            rset.getString("status"),
                            rset.getString("origem"));
                    registro.setId(rset.getInt("id"));
                    registro.setNomeUsuario(rset.getString("nomeUsuario"));
                    registros.add(registro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar registros por período: " + e.getMessage());
            e.printStackTrace();
        }

        return registros;
    }
}