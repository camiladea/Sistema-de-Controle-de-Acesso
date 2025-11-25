package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Administrador;
import model.Funcionario;
import model.Usuario;
import util.ConexaoBancoDados;

public class UsuarioDAO {

    /**
     * Salva um novo usuário no banco de dados, tratando os múltiplos templates de digital como BLOB.
     * @return true se o usuário foi salvo com sucesso, false caso contrário.
     */
    public boolean salvar(Usuario usuario) {
        final String sql = "INSERT INTO Usuario (nome, cpf, email, digitalTemplate, digitalTemplate1, digitalTemplate2, ativo, tipoUsuario, cargo, login, senhaHash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final String tipo = (usuario instanceof Funcionario) ? "Funcionario" : "Administrador";

        try (Connection conexao = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstm.setString(1, usuario.getNome());
            pstm.setString(2, usuario.getCpf());
            pstm.setString(3, usuario.getEmail());

            // Lida com os múltiplos templates de digital
            pstm.setBytes(4, usuario.getDigitalTemplate());
            pstm.setBytes(5, usuario.getDigitalTemplate1());
            pstm.setBytes(6, usuario.getDigitalTemplate2());
            
            pstm.setBoolean(7, usuario.isAtivo());
            pstm.setString(8, tipo);

            if (usuario instanceof Funcionario) {
                Funcionario f = (Funcionario) usuario;
                pstm.setString(9, f.getCargo());
                pstm.setNull(10, Types.VARCHAR);
                pstm.setNull(11, Types.VARCHAR);
            } else if (usuario instanceof Administrador) {
                Administrador a = (Administrador) usuario;
                pstm.setNull(9, Types.VARCHAR);
                pstm.setString(10, a.getLogin());
                pstm.setString(11, a.getSenhaHash());
            }

            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Usuário salvo com sucesso! ID: " + usuario.getId());
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extrai um objeto Usuario do ResultSet, tratando as digitais como byte[].
     */
    private Usuario extrairUsuarioDoResultSet(ResultSet rset) throws SQLException {
        String tipoUsuario = rset.getString("tipoUsuario");
        Usuario usuario;

        byte[] digitalTemplate = rset.getBytes("digitalTemplate");
        byte[] digitalTemplate1 = rset.getBytes("digitalTemplate1");
        byte[] digitalTemplate2 = rset.getBytes("digitalTemplate2");

        if ("Funcionario".equals(tipoUsuario)) {
            usuario = new Funcionario(
                    rset.getString("nome"),
                    rset.getString("cpf"),
                    rset.getString("email"),
                    digitalTemplate,
                    digitalTemplate1,
                    digitalTemplate2,
                    rset.getString("cargo"));
        } else { // Assume Administrador
            usuario = new Administrador(
                    rset.getString("nome"),
                    rset.getString("cpf"),
                    rset.getString("email"),
                    digitalTemplate,
                    digitalTemplate1,
                    digitalTemplate2,
                    rset.getString("login"),
                    rset.getString("senhaHash"));
        }
        usuario.setId(rset.getInt("id"));
        usuario.setAtivo(rset.getBoolean("ativo"));
        return usuario;
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
    
    public Usuario buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM Usuario WHERE cpf = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, cpf);
            try (ResultSet rset = pstm.executeQuery()) {
                if (rset.next()) {
                    return extrairUsuarioDoResultSet(rset);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por CPF: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT * FROM Usuario WHERE login = ?";
        try (Connection conn = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, login);
            try (ResultSet rset = pstm.executeQuery()) {
                if (rset.next()) {
                    return extrairUsuarioDoResultSet(rset);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por Login: " + e.getMessage());
        }
        return null;
    }
    
    public boolean atualizar(Usuario usuario) {
        final String sql = "UPDATE Usuario SET nome = ?, cpf = ?, email = ?, digitalTemplate = ?, digitalTemplate1 = ?, digitalTemplate2 = ?, ativo = ?, cargo = ?, login = ?, senhaHash = ? WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            
            stmt.setBytes(4, usuario.getDigitalTemplate());
            stmt.setBytes(5, usuario.getDigitalTemplate1());
            stmt.setBytes(6, usuario.getDigitalTemplate2());
            
            stmt.setBoolean(7, usuario.isAtivo());

            if (usuario instanceof Funcionario) {
                Funcionario f = (Funcionario) usuario;
                stmt.setString(8, f.getCargo());
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
            } else if (usuario instanceof Administrador) {
                Administrador a = (Administrador) usuario;
                stmt.setNull(8, Types.VARCHAR);
                stmt.setString(9, a.getLogin());
                stmt.setString(10, a.getSenhaHash());
            }
            
            stmt.setInt(11, usuario.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean remover(int id) {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao remover usuário: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM Usuario WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao(); PreparedStatement pstm = conn.prepareStatement(sql)) {
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
}
