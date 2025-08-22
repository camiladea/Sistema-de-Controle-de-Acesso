package dao;

import model.Administrador;
import model.Funcionario;
import model.Usuario;
import util.ConexaoBancoDados;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void salvar(Usuario usuario) {
        // Use o mesmo nome de tabela que você alterou no SQL:
        final String sql = "INSERT INTO Usuario " +
                "(nome, cpf, email, digitalHash, ativo, tipoUsuario, cargo, matricula, login, senhaHash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Resolve o 'tipo' de forma centralizada, com fallback "User"
        final String tipo = (usuario instanceof Funcionario) ? "Funcionario"
                : (usuario instanceof Administrador) ? "Administrador" : "User";

        try (Connection conexao = ConexaoBancoDados.getConexao();
                PreparedStatement pstm = conexao.prepareStatement(sql)) {

            pstm.setString(1, usuario.getNome());
            pstm.setString(2, usuario.getCpf());
            pstm.setString(3, usuario.getEmail());
            pstm.setString(4, usuario.getDigitalHash());
            pstm.setBoolean(5, usuario.isAtivo());
            pstm.setString(6, tipo); // <- SEMPRE envia 'tipo'

            if (usuario instanceof Funcionario f) {
                pstm.setString(7, f.getCargo());
                pstm.setString(8, f.getMatricula());
                pstm.setNull(9, Types.VARCHAR); // login
                pstm.setNull(10, Types.VARCHAR); // senhaHash

            } else if (usuario instanceof Administrador a) {
                pstm.setNull(7, Types.VARCHAR); // cargo
                pstm.setNull(8, Types.VARCHAR); // matricula
                pstm.setString(9, a.getLogin());
                pstm.setString(10, a.getSenhaHash());

            } else {
                // Usuário "comum"
                pstm.setNull(7, Types.VARCHAR);
                pstm.setNull(8, Types.VARCHAR);
                pstm.setNull(9, Types.VARCHAR);
                pstm.setNull(10, Types.VARCHAR);
            }

            pstm.executeUpdate();
            System.out.println("Usuário salvo com sucesso!");

        } catch (SQLException e) {
            System.err.println(
                    "Erro ao salvar usuário: " + e.getMessage() +
                            " | SQLState=" + e.getSQLState() +
                            " | Code=" + e.getErrorCode());
        }
    }

    public List<String> listarTodosOsHashes() {
        List<String> hashes = new ArrayList<>();
        String sql = "SELECT digitalHash FROM Usuario WHERE ativo = TRUE AND digitalHash IS NOT NULL";
        try (Connection conn = ConexaoBancoDados.getConexao();
                PreparedStatement pstm = conn.prepareStatement(sql);
                ResultSet rset = pstm.executeQuery()) {
            while (rset.next()) {
                hashes.add(rset.getString("digitalHash"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar hashes: " + e.getMessage());
        }
        return hashes;
    }

    public Usuario buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM Usuario WHERE cpf = ?";
        return buscarUsuarioPorString(sql, cpf);
    }

    public Usuario buscarPorHash(String digitalHash) {
        String sql = "SELECT * FROM Usuario WHERE digitalHash = ?";
        return buscarUsuarioPorString(sql, digitalHash);
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
        try (Connection conn = ConexaoBancoDados.getConexao(); PreparedStatement pstm = conn.prepareStatement(sql)) {
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
            usuario = new Funcionario(rset.getString("nome"), rset.getString("cpf"), rset.getString("email"),
                    rset.getString("digitalHash"), rset.getString("cargo"), rset.getString("matricula"));
        } else {
            usuario = new Administrador(rset.getString("nome"), rset.getString("cpf"), rset.getString("email"),
                    rset.getString("digitalHash"), rset.getString("login"), rset.getString("senhaHash"));
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
        String sql = "UPDATE usuarios SET nome = ?, email = ?, digitalHash = ? WHERE cpf = ?";
        try (Connection conn = ConexaoBancoDados.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getDigitalHash());
            stmt.setString(4, usuario.getCpf());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}