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
        String sql = "INSERT INTO Usuario (nome, cpf, email, digitalHash, ativo, tipoUsuario, cargo, matricula, login, senhaHash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = ConexaoBancoDados.getConexao(); PreparedStatement pstm = conexao.prepareStatement(sql)) {
            pstm.setString(1, usuario.getNome());
            pstm.setString(2, usuario.getCpf());
            pstm.setString(3, usuario.getEmail());
            pstm.setString(4, usuario.getDigitalHash());
            pstm.setBoolean(5, usuario.isAtivo());
            if (usuario instanceof Funcionario) {
                pstm.setString(6, "Funcionario");
                pstm.setString(7, ((Funcionario) usuario).getCargo());
                pstm.setString(8, ((Funcionario) usuario).getMatricula());
                pstm.setNull(9, Types.VARCHAR);
                pstm.setNull(10, Types.VARCHAR);
            } else if (usuario instanceof Administrador) {
                pstm.setString(6, "Administrador");
                pstm.setNull(7, Types.VARCHAR);
                pstm.setNull(8, Types.VARCHAR);
                pstm.setString(9, ((Administrador) usuario).getLogin());
                pstm.setString(10, ((Administrador) usuario).getSenhaHash());
            }
            pstm.execute();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    public List<String> listarTodosOsHashes() {
        List<String> hashes = new ArrayList<>();
        String sql = "SELECT digitalHash FROM Usuario WHERE ativo = TRUE AND digitalHash IS NOT NULL";
        try (Connection conn = ConexaoBancoDados.getConexao(); PreparedStatement pstm = conn.prepareStatement(sql); ResultSet rset = pstm.executeQuery()) {
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
        try (Connection conn = ConexaoBancoDados.getConexao(); PreparedStatement pstm = conn.prepareStatement(sql); ResultSet rset = pstm.executeQuery()) {
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
            usuario = new Funcionario(rset.getString("nome"), rset.getString("cpf"), rset.getString("email"), rset.getString("digitalHash"), rset.getString("cargo"), rset.getString("matricula"));
        } else {
            usuario = new Administrador(rset.getString("nome"), rset.getString("cpf"), rset.getString("email"), rset.getString("digitalHash"), rset.getString("login"), rset.getString("senhaHash"));
        }
        usuario.setId(rset.getInt("id"));
        usuario.setAtivo(rset.getBoolean("ativo"));
        return usuario;
    }
}