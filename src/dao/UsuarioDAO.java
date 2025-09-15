package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Administrador;
import model.Funcionario;
import model.Usuario;
import util.ConexaoBancoDados;

public class UsuarioDAO {

    public void salvar(Usuario usuario) {
        final String sql = "INSERT INTO Usuario " +
                "(nome, cpf, email, digitalFIR, ativo, tipoUsuario, cargo, login, senhaHash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final String tipo = (usuario instanceof Funcionario) ? "Funcionario"
                : (usuario instanceof Administrador) ? "Administrador"
                : "User";

        try (Connection conexao = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conexao.prepareStatement(sql)) {

            pstm.setString(1, usuario.getNome());
            pstm.setString(2, usuario.getCpf());
            pstm.setString(3, usuario.getEmail());
            pstm.setString(4, usuario.getDigitalFIR());
            pstm.setBoolean(5, usuario.isAtivo());
            pstm.setString(6, tipo);

            if (usuario instanceof Funcionario f) {
                pstm.setString(7, f.getCargo());
                pstm.setNull(8, Types.VARCHAR);
                pstm.setNull(9, Types.VARCHAR);
            } else if (usuario instanceof Administrador a) {
                pstm.setNull(7, Types.VARCHAR);
                pstm.setString(8, a.getLogin());
                pstm.setString(9, a.getSenhaHash());
            } else {
                pstm.setNull(7, Types.VARCHAR);
                pstm.setNull(8, Types.VARCHAR);
                pstm.setNull(9, Types.VARCHAR);
            }

            pstm.executeUpdate();
            System.out.println("Usuário salvo com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    public List<String> listarTodosOsTemplates() {
        List<String> templates = new ArrayList<>();
        String sql = "SELECT digitalFIR FROM Usuario WHERE ativo = TRUE AND digitalFIR IS NOT NULL";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rset = pstm.executeQuery()) {
            while (rset.next()) {
                templates.add(rset.getString("digitalFIR"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar FIRs: " + e.getMessage());
        }
        return templates;
    }

    public Usuario buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM Usuario WHERE cpf = ?";
        return buscarUsuarioPorString(sql, cpf);
    }

    public Usuario buscarPorFIR(String digitalFIR) {
        String sql = "SELECT * FROM Usuario WHERE digitalFIR = ?";
        return buscarUsuarioPorString(sql, digitalFIR);
    }

    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT * FROM Usuario WHERE login = ?";
        return buscarUsuarioPorString(sql, login);
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuario ORDER BY nome ASC";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rset = pstm.executeQuery()) {
            while (rset.next()) {
                usuarios.add(extrairUsuarioDoResultSet(rset));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return usuarios;
    }

    private Usuario buscarUsuarioPorString(String sql, String parametro) {
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, parametro);
            try (ResultSet rset = pstm.executeQuery()) {
                if (rset.next()) {
                    return extrairUsuarioDoResultSet(rset);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    private Usuario extrairUsuarioDoResultSet(ResultSet rset) throws SQLException {
        String tipoUsuario = rset.getString("tipoUsuario");
        Usuario usuario;

        if ("Funcionario".equals(tipoUsuario)) {
            usuario = new Funcionario(
                    rset.getString("nome"),
                    rset.getString("cpf"),
                    rset.getString("email"),
                    rset.getString("digitalFIR"),
                    rset.getString("cargo"));
        } else {
            usuario = new Administrador(
                    rset.getString("nome"),
                    rset.getString("cpf"),
                    rset.getString("email"),
                    rset.getString("digitalFIR"),
                    rset.getString("login"),
                    rset.getString("senhaHash"));
        }
        usuario.setId(rset.getInt("id"));
        usuario.setAtivo(rset.getBoolean("ativo"));
        return usuario;
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id);
            int linhas = pstm.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao remover usuário: " + e.getMessage());
        }
        return false;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM Usuario WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id);
            try (ResultSet rset = pstm.executeQuery()) {
                if (rset.next()) {
                    return extrairUsuarioDoResultSet(rset);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE Usuario SET nome = ?, cpf = ?, email = ?, digitalFIR = ?, " +
                "ativo = ?, tipoUsuario = ?, cargo = ?, login = ?, senhaHash = ? " +
                "WHERE cpf = ?";
        final String tipo = (usuario instanceof Funcionario) ? "Funcionario"
                : (usuario instanceof Administrador) ? "Administrador"
                : "User";

        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getDigitalFIR());
            stmt.setBoolean(5, usuario.isAtivo());
            stmt.setString(6, tipo);

            if (usuario instanceof Funcionario f) {
                stmt.setString(7, f.getCargo());
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR);
            } else if (usuario instanceof Administrador a) {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setString(8, a.getLogin());
                stmt.setString(9, a.getSenhaHash());
            } else {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
                stmt.setNull(9, Types.VARCHAR);
            }
            stmt.setString(10, usuario.getCpf());
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }
}