package service;

import dao.UsuarioDAO;
import model.Usuario;
import model.Administrador;
import util.ConexaoBancoDados;

import java.util.Optional;
import java.util.List;

/**
 * Handles both biometric and admin credential authentication.
 */
public class SistemaAutenticacao {

    private final UsuarioDAO usuarioDAO;
    private final NativeLibfprintReader leitorBiometrico;

    public SistemaAutenticacao() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new NativeLibfprintReader();
    }

    /**
     * Perform biometric authentication — capture fingerprint, compare with stored templates.
     */
    public Optional<Usuario> autenticarPorBiometria() {
        try {
            Optional<byte[]> capturaOpt = leitorBiometrico.capturarDigital();
            if (capturaOpt.isEmpty()) {
                System.err.println("[SistemaAutenticacao] ERRO: Não foi possível capturar a digital.");
                return Optional.empty();
            }

            byte[] captura = capturaOpt.get();
            List<Usuario> usuarios = usuarioDAO.listarTodos();

            for (Usuario u : usuarios) {
                if (u.getDigitalTemplate() != null &&
                        leitorBiometrico.compararDigitais(u.getDigitalTemplate(), captura)) {
                    System.out.println("[SistemaAutenticacao] Usuário autenticado: " + u.getNome());
                    return Optional.of(u);
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
     * Verify admin credentials via login and password hash (still uses stored senhaHash).
     */
    public Optional<Administrador> autenticarAdminPorCredenciais(String login, String senhaHash) {
        Usuario user = usuarioDAO.buscarPorLoginESenha(login, senhaHash);
        if (user instanceof Administrador) {
            return Optional.of((Administrador) user);
        }
        return Optional.empty();
    }
}
