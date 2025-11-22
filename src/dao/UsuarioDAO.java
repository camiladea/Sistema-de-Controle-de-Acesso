package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Administrador;
import model.Funcionario;
import model.Usuario;
import util.ConexaoBancoDados;

public class UsuarioDAO {

    /**
     * Salva um novo usuário no banco de dados, tratando o template da digital como BLOB.
     * @return true se o usuário foi salvo com sucesso, false caso contrário.
     */
    public boolean salvar(Usuario usuario) {
        // SQL ajustado para 'digitalTemplate'
        final String sql = "INSERT INTO Usuario (nome, cpf, email, digitalTemplate, ativo, tipoUsuario, cargo, login, senhaHash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        final String tipo = (usuario instanceof Funcionario) ? "Funcionario" : "Administrador";

        try (Connection conexao = ConexaoBancoDados.getConexao();
             PreparedStatement pstm = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstm.setString(1, usuario.getNome());
            pstm.setString(2, usuario.getCpf());
            pstm.setString(3, usuario.getEmail());
            
            // Lida com o template da digital que agora pode ser nulo
            if (usuario.getDigitalTemplate() != null) {
                pstm.setBytes(4, usuario.getDigitalTemplate());
            } else {
                pstm.setNull(4, Types.BLOB);
            }
            
            pstm.setBoolean(5, usuario.isAtivo());
            pstm.setString(6, tipo);

            if (usuario instanceof Funcionario) {
                Funcionario f = (Funcionario) usuario;
                pstm.setString(7, f.getCargo());
                pstm.setNull(8, Types.VARCHAR);
                pstm.setNull(9, Types.VARCHAR);
            } else if (usuario instanceof Administrador) {
                Administrador a = (Administrador) usuario;
                pstm.setNull(7, Types.VARCHAR);
                pstm.setString(8, a.getLogin());
                pstm.setString(9, a.getSenhaHash());
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
            return false;
        }
    }

    /**
     * Extrai um objeto Usuario do ResultSet, tratando a digital como byte[].
     */
    private Usuario extrairUsuarioDoResultSet(ResultSet rset) throws SQLException {
        String tipoUsuario = rset.getString("tipoUsuario");
        Usuario usuario;

        if ("Funcionario".equals(tipoUsuario)) {
            // CORREÇÃO: Chama o construtor correto com byte[]
            usuario = new Funcionario(
                    rset.getString("nome"),
                    rset.getString("cpf"),
                    rset.getString("email"),
                    rset.getBytes("digitalTemplate"), // Lê a digital como array de bytes
                    rset.getString("cargo"));
        } else { // Assume Administrador
            // CORREÇÃO: Chama o construtor correto com byte[]
            usuario = new Administrador(
                    rset.getString("nome"),
                    rset.getString("cpf"),
                    rset.getString("email"),
                    rset.getBytes("digitalTemplate"), // Lê a digital como array de bytes
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
    
    // --- MÉTODOS QUE ESTAVAM FALTANDO ---

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
        final String sql = "UPDATE Usuario SET nome = ?, cpf = ?, email = ?, digitalTemplate = ?, ativo = ?, cargo = ?, login = ?, senhaHash = ? WHERE id = ?";
        try (Connection conn = ConexaoBancoDados.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            
            if (usuario.getDigitalTemplate() != null) {
                stmt.setBytes(4, usuario.getDigitalTemplate());
            } else {
                stmt.setNull(4, Types.BLOB);
            }
            
            stmt.setBoolean(5, usuario.isAtivo());

            // --- CORREÇÃO PARA JAVA 11 ---
            if (usuario instanceof Funcionario) {
                Funcionario f = (Funcionario) usuario; // Cast manual
                stmt.setString(6, f.getCargo());
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
            } else if (usuario instanceof Administrador) {
                Administrador a = (Administrador) usuario; // Cast manual
                stmt.setNull(6, Types.VARCHAR);
                stmt.setString(7, a.getLogin());
                stmt.setString(8, a.getSenhaHash());
            }
            // --- FIM DA CORREÇÃO ---
            
            stmt.setInt(9, usuario.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
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