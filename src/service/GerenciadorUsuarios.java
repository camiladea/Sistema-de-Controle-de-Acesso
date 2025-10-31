package service;

import dao.UsuarioDAO;
import model.Funcionario;
import model.Usuario;
import java.util.Optional;

/**
 * Handles user registration, editing, and DAO bridging.
 */
public class GerenciadorUsuarios {

    private final UsuarioDAO usuarioDAO;
    private final NativeLibfprintReader leitorBiometrico;

    public GerenciadorUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new NativeLibfprintReader();
    }

    /**
     * Register a new employee and capture their fingerprint template.
     */
    public boolean cadastrarNovoFuncionario(String nome, String cpf, String email, String cargo) {
        try {
            // Check if CPF already exists
            if (usuarioDAO.buscarPorCPF(cpf) != null) {
                System.err.println("[GerenciadorUsuarios] ERRO: CPF já cadastrado: " + cpf);
                return false;
            }

            System.out.println("[GerenciadorUsuarios] Capturando digital para novo funcionário...");
            Optional<byte[]> digitalOpt = leitorBiometrico.capturarDigital();

            if (digitalOpt.isEmpty()) {
                System.err.println("[GerenciadorUsuarios] Falha: não foi possível capturar a digital.");
                return false;
            }

            byte[] digitalTemplate = digitalOpt.get();

            Usuario novo = new Funcionario(nome, cpf, email, cargo, "", "", digitalTemplate);
            boolean sucesso = usuarioDAO.inserir(novo);

            if (sucesso) {
                System.out.println("[GerenciadorUsuarios] Funcionário cadastrado com sucesso: " + nome);
            }
            return sucesso;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Edit user data.
     */
    public boolean editarUsuario(Usuario usuario) {
        if (usuario == null) return false;
        return usuarioDAO.atualizar(usuario);
    }
}
