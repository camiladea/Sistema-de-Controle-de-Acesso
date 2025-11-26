package service;

import dao.UsuarioDAO;
import dao.RegistroAcessoDAO; // Import Adicionado
import model.RegistroAcesso; // Import Adicionado
import model.Administrador;
import model.Usuario;

import java.time.LocalDateTime; // Import Adicionado
import java.util.*;

public class SistemaAutenticacao {

    private final UsuarioDAO usuarioDAO;
    private final RegistroAcessoDAO registroAcessoDAO; // Campo Novo
    private final NativeLibfprintReader leitorBiometrico;

    public SistemaAutenticacao() {
        this.usuarioDAO = new UsuarioDAO();
        this.registroAcessoDAO = new RegistroAcessoDAO(); // Inicialização Nova
        this.leitorBiometrico = new NativeLibfprintReader();
    }

    /**
     * Autenticação biométrica geral (identifica qualquer usuário cadastrado com
     * digital).
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
            // Logging opcional removido para limpar a visualização, ou mantenha se preferir
            if (template != null && template.length > 3) {
                String b64 = Base64.getEncoder().encodeToString(template);
                templatesBase64.add(b64);
                usuariosComTemplate.add(u); // IMPORTANT: index linked here
            }
        }

        if (templatesBase64.isEmpty()) {
            System.err.println("[SistemaAutenticacao] Nenhuma digital cadastrada no banco.");
            return Optional.empty();
        }

        // Identify (NÃO ALTERADO)
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

        // =========================================================================
        // [CORREÇÃO APLICADA] VERIFICAÇÃO DE INATIVIDADE
        // =========================================================================
        if (!usuarioCorrespondente.isAtivo()) {
            System.out.println("[SistemaAutenticacao] Acesso NEGADO: Usuário identificado mas INATIVO - "
                    + usuarioCorrespondente.getNome());

            // Registra a tentativa negada no banco
            RegistroAcesso log = new RegistroAcesso(
                    LocalDateTime.now(),
                    usuarioCorrespondente.getId(),
                    "NEGADO_INATIVO",
                    "BIOMETRIA");
            log.setNomeUsuario(usuarioCorrespondente.getNome());
            registroAcessoDAO.salvar(log);

            return Optional.empty(); // Retorna vazio para bloquear a porta
        }
        // =========================================================================

        System.out.println("[SistemaAutenticacao] Usuário autenticado com sucesso: "
                + usuarioCorrespondente.getNome());

        // Registra o sucesso no banco (Para manter consistência com o log de erro)
        RegistroAcesso logSucesso = new RegistroAcesso(
                LocalDateTime.now(),
                usuarioCorrespondente.getId(),
                "AUTORIZADO",
                "BIOMETRIA");
        logSucesso.setNomeUsuario(usuarioCorrespondente.getNome());
        registroAcessoDAO.salvar(logSucesso);

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
                usuario.getDigitalTemplate()));
    }
}