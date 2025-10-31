package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Administrador;
import model.Funcionario;
import model.Usuario;
import util.ConexaoBancoDados;

public class UsuarioDAO {

    public void salvarDigital(int usuarioId, byte[] digitalTemplate) throws SQLException {
        String sql = "UPDATE Usuario SET digitalTemplate = ? WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, digitalTemplate);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        }
    }

    public byte[] obterDigitalPorId(int usuarioId) throws SQLException {
        String sql = "SELECT digitalTemplate FROM Usuario WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getBytes("digitalTemplate");
        }
        return null;
    }

    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE login = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setSenhaHash(rs.getString("senhaHash"));
                u.setDigitalTemplate(rs.getBytes("digitalTemplate"));
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";
        try (Connection conn = ConexaoBancoDados.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                lista.add(u);
            }
        }
        return lista;
    }
}