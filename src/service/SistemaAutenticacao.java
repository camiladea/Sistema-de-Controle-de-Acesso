package service;

import dao.UsuarioDAO;
import java.util.*;
import model.Administrador;
import model.Usuario;

public class SistemaAutenticacao {

    private final UsuarioDAO usuarioDAO;
    private final NativeLibfprintReader leitorBiometrico;

    public SistemaAutenticacao() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new NativeLibfprintReader();
    }

    /**
     * Autenticação biométrica geral (identifica qualquer usuário cadastrado com digital).
     */
    public Optional<Usuario> autenticarPorBiometria() {
        System.out.println("[SistemaAutenticacao] Iniciando autenticação biométrica...");

        List<Usuario> usuarios = usuarioDAO.listarTodos();
        System.out.println("[SistemaAutenticacao] Total de usuários carregados: " + usuarios.size());

        // --------------------------
        // FIX: manter lista paralela alinhada
        // --------------------------
        List<String> templatesBase64 = new ArrayList<>();
        List<Usuario> usuariosComTemplate = new ArrayList<>();

        for (Usuario u : usuarios) {
            byte[] template = u.getDigitalTemplate();
            System.out.println("[SistemaAutenticacao] Usuário: " + u.getNome() +
                               ", template bytes = " + (template != null ? template.length : 0));

            if (template != null && template.length > 3) {
                String b64 = Base64.getEncoder().encodeToString(template);
                templatesBase64.add(b64);
                usuariosComTemplate.add(u);   // IMPORTANT: index linked here
            }
        }

        if (templatesBase64.isEmpty()) {
            System.err.println("[SistemaAutenticacao] Nenhuma digital cadastrada no banco.");
            return Optional.empty();
        }

        // Identify
        Optional<Integer> matchIndex = leitorBiometrico.identificar(templatesBase64);

        if (matchIndex.isEmpty()) {
            System.err.println("[SistemaAutenticacao] Nenhuma correspondência biométrica encontrada.");
            return Optional.empty();
        }

        int idx = matchIndex.get();
        if (idx < 0 || idx >= usuariosComTemplate.size()) {
            System.err.println("[SistemaAutenticacao] Índice de correspondência inválido: " + idx);
            return Optional.empty();
        }

        Usuario usuarioCorrespondente = usuariosComTemplate.get(idx);

        System.out.println("[SistemaAutenticacao] Usuário autenticado com sucesso: " 
                           + usuarioCorrespondente.getNome());

        return Optional.of(usuarioCorrespondente);
    }

    /**
     * Autenticação de administrador por login/senha
     */
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
