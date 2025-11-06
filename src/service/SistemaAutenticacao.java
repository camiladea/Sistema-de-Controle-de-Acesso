package service;

import dao.UsuarioDAO;
import model.Usuario;
import model.Administrador;
import java.util.Optional;
import java.util.List;

/**
 * SistemaAutenticacao
 * 
 * Handles biometric authentication (via fp_test) and admin login verification.
 */
public class SistemaAutenticacao {

    private final UsuarioDAO usuarioDAO;
    private final NativeLibfprintReader leitorBiometrico;

    public SistemaAutenticacao() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new NativeLibfprintReader();
    }

    /**
     * Perform biometric authentication — captures a fingerprint and compares it
     * against all stored user templates in the database.
     *
     * @return Optional<Usuario> if a match is found, otherwise empty.
     */
    public Optional<Usuario> autenticarPorBiometria() {
        try {
            System.out.println("[SistemaAutenticacao] Iniciando captura biométrica...");
            Optional<byte[]> capturaOpt = leitorBiometrico.capturarDigital();

            if (capturaOpt.isEmpty()) {
                System.err.println("[SistemaAutenticacao] ERRO: Não foi possível capturar a digital.");
                return Optional.empty();
            }

            byte[] novaCaptura = capturaOpt.get();
            List<Usuario> usuarios = usuarioDAO.listarTodos();

            for (Usuario u : usuarios) {
                byte[] templateSalvo = u.getDigitalTemplate();
                if (templateSalvo != null && templateSalvo.length > 0) {
                    System.out.println("[SistemaAutenticacao] Verificando usuário: " + u.getNome());
                    if (leitorBiometrico.verificarDigital(templateSalvo)) {
                        System.out.println("[SistemaAutenticacao] Usuário autenticado com sucesso: " + u.getNome());
                        return Optional.of(u);
                    }
                }
            }

            System.err.println("[SistemaAutenticacao] Nenhuma correspondência biométrica encontrada.");
            return Optional.empty();

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Authenticate an administrator via login and password hash.
     *
     * @param login - Admin username
     * @param senhaHash - Password hash
     * @return Optional<Administrador> if credentials match.
     */
    public Optional<Administrador> autenticarAdminPorCredenciais(String login, String senhaHash) {
        try {
            Usuario user = usuarioDAO.buscarPorLoginESenha(login, senhaHash);
            if (user instanceof Administrador) {
                System.out.println("[SistemaAutenticacao] Admin autenticado: " + user.getNome());
                return Optional.of((Administrador) user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("[SistemaAutenticacao] Credenciais inválidas.");
        return Optional.empty();
    }
}
