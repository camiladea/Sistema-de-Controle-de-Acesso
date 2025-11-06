package service;

import dao.UsuarioDAO;
import model.Funcionario;
import model.Usuario;
import java.util.Optional;

/**
 * GerenciadorUsuarios
 * 
 * Handles registration, fingerprint capture, and user data updates.
 */
public class GerenciadorUsuarios {

    private final UsuarioDAO usuarioDAO;
    private final NativeLibfprintReader leitorBiometrico;

    public GerenciadorUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new NativeLibfprintReader();
    }

    /**
     * Register a new employee and capture their fingerprint.
     *
     * @param nome  Nome completo
     * @param cpf   CPF (unique identifier)
     * @param email E-mail
     * @param cargo Cargo ou função
     * @return true se o cadastro foi bem-sucedido
     */
    public boolean cadastrarNovoFuncionario(String nome, String cpf, String email, String cargo) {
        try {
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
            } else {
                System.err.println("[GerenciadorUsuarios] Falha ao inserir no banco de dados.");
            }

            return sucesso;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing user’s data.
     */
    public boolean editarUsuario(Usuario usuario) {
        if (usuario == null) return false;
        return usuarioDAO.atualizar(usuario);
    }
}
