package service;

import dao.UsuarioDAO;
import model.Usuario;
import model.Administrador;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.security.MessageDigest;

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
     * Perform biometric authentication — ask the helper to IDENTIFY against all stored templates.
     */
    public Optional<Usuario> autenticarPorBiometria() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            List<byte[]> templates = new ArrayList<>();
            List<Usuario> usersWithTemplates = new ArrayList<>();

            for (Usuario u : usuarios) {
                if (u.getDigitalTemplate() != null && u.getDigitalTemplate().length > 0) {
                    templates.add(u.getDigitalTemplate());
                    usersWithTemplates.add(u);
                }
            }

            if (templates.isEmpty()) {
                System.err.println("[SistemaAutenticacao] Nenhuma digital cadastrada no banco.");
                return Optional.empty();
            }

            Optional<Integer> matchIndex = leitorBiometrico.identificar(templates);
            if (matchIndex.isPresent()) {
                int idx = matchIndex.get();
                if (idx >= 0 && idx < usersWithTemplates.size()) {
                    Usuario matchedUser = usersWithTemplates.get(idx);
                    System.out.println("[SistemaAutenticacao] Usuário autenticado: " + matchedUser.getNome());
                    return Optional.of(matchedUser);
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
     * Verify admin credentials via login and plain password. Accepts:
     * - legacy plain stored password (exact match),
     * - stored SHA-256(hex) produced from the plain password.
     */
    public Optional<Administrador> autenticarAdminPorCredenciais(String login, String senhaPlain) {
        try {
            Usuario user = usuarioDAO.buscarPorLogin(login);
            if (user == null) {
                System.err.println("[SistemaAutenticacao] Usuário não encontrado: " + login);
                return Optional.empty();
            }

            String stored = user.getSenhaHash();
            if (stored == null) stored = "";

            // 1) exact match (legacy plain text)
            if (!stored.isEmpty() && stored.equals(senhaPlain)) {
                if (user instanceof Administrador) return Optional.of((Administrador) user);
                return Optional.empty();
            }

            // 2) SHA-256 hex match
            String sha256hex = sha256Hex(senhaPlain);
            if (!stored.isEmpty() && stored.equalsIgnoreCase(sha256hex)) {
                if (user instanceof Administrador) return Optional.of((Administrador) user);
                return Optional.empty();
            }

            // not matched
            System.err.println("[SistemaAutenticacao] Credenciais inválidas para " + login);
            return Optional.empty();

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String sha256Hex(String input) {
        if (input == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) {
                sb.append(String.format("%02x", x));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
