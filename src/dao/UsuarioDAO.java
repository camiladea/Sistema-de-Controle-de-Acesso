package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Administrador;
import model.Usuario;
import util.ConexaoBancoDados;

public class UsuarioDAO {

    // Matches your actual table name (capital U)
    private static final String TABLE = "Usuario";

    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO " + TABLE + 
            " (nome, cpf, email, tipoUsuario, cargo, login, senhaHash, ativo, digitalTemplate) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getTipoUsuario());
            stmt.setString(5, usuario.getCargo());
            stmt.setString(6, usuario.getLogin());
            stmt.setString(7, usuario.getSenhaHash());
            stmt.setBoolean(8, usuario.isAtivo());
            stmt.setBytes(9, usuario.getDigitalTemplate());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE " + TABLE +
            " SET nome=?, cpf=?, email=?, tipoUsuario=?, cargo=?, login=?, senhaHash=?, ativo=?, digitalTemplate=? WHERE id=?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getTipoUsuario());
            stmt.setString(5, usuario.getCargo());
            stmt.setString(6, usuario.getLogin());
            stmt.setString(7, usuario.getSenhaHash());
            stmt.setBoolean(8, usuario.isAtivo());
            stmt.setBytes(9, usuario.getDigitalTemplate());
            stmt.setInt(10, usuario.getId());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario buscarPorCPF(String cpf) {
        String sql = "SELECT * FROM " + TABLE + " WHERE cpf = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario buscarPorLoginESenha(String login, String senhaHash) {
        String sql = "SELECT * FROM " + TABLE + " WHERE LOWER(login) = LOWER(?) AND senhaHash = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login.toLowerCase());
            stmt.setString(2, senhaHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT * FROM " + TABLE + " WHERE LOWER(login) = LOWER(?)";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE;
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** 
     * Converts a ResultSet row into either a Usuario or Administrador instance.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipoUsuario");
        Usuario u;

        // ✅ FIXED: your DB stores 'ADMIN', not 'Administrador'
        if (tipo != null && tipo.equalsIgnoreCase("ADMIN")) {
            u = new Administrador();
        } else {
            u = new Usuario();
        }

        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setCpf(rs.getString("cpf"));
        u.setEmail(rs.getString("email"));
        u.setTipoUsuario(tipo);
        u.setCargo(rs.getString("cargo"));
        u.setLogin(rs.getString("login"));
        u.setSenhaHash(rs.getString("senhaHash"));
        u.setAtivo(rs.getBoolean("ativo"));
        u.setDigitalTemplate(rs.getBytes("digitalTemplate"));
        return u;
    }
}
