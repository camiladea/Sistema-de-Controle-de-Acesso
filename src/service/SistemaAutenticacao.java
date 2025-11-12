package service;

import dao.UsuarioDAO;
import java.util.Optional;
import model.Administrador;
import model.Usuario;

public class SistemaAutenticacao {

    private final UsuarioDAO usuarioDAO;

    public SistemaAutenticacao() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Optional<Usuario> autenticarPorBiometria() {
        // Placeholder for biometric auth logic
        return Optional.empty();
    }

    public Optional<Administrador> autenticarAdminPorCredenciais(String login, String senha) {
        if (login == null || senha == null || login.isEmpty() || senha.isEmpty()) {
            System.out.println("[SistemaAutenticacao] Login ou senha vazios.");
            return Optional.empty();
        }

        Usuario usuario = usuarioDAO.buscarPorLogin(login);

        if (usuario == null) {
            System.out.println("[SistemaAutenticacao] Nenhum usuário encontrado para login: " + login);
            return Optional.empty();
        }

        if (!"ADMIN".equalsIgnoreCase(usuario.getTipoUsuario())) {
            System.out.println("[SistemaAutenticacao] Usuário não é administrador.");
            return Optional.empty();
        }

        String senhaSalva = usuario.getSenhaHash();
        boolean senhaCorreta = senhaSalva != null && senhaSalva.equals(senha);

        if (!senhaCorreta) {
            System.out.println("[SistemaAutenticacao] Senha incorreta para login: " + login);
            return Optional.empty();
        }

        System.out.println("[SistemaAutenticacao] Login de administrador bem-sucedido: " + login);

        return Optional.of(new Administrador(
            usuario.getNome(),
            usuario.getCpf(),
            usuario.getEmail(),
            usuario.getCargo(),
            usuario.getLogin(),
            usuario.getSenhaHash(),
            usuario.getDigitalTemplate()
        ));
    }
}
