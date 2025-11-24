package service;

import dao.RegistroAcessoDAO;
import dao.UsuarioDAO;
import model.Administrador;
import model.RegistroAcesso;
import model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SistemaAutenticacao 
 */
import util.HashUtils;

public class SistemaAutenticacao {
    private final LeitorBiometrico leitorBiometrico;
    private final UsuarioDAO usuarioDAO;
    private final RegistroAcessoDAO registroAcessoDAO;
    private static final String ORIGEM_TERMINAL = "Terminal Principal";

    public SistemaAutenticacao() {
        this.leitorBiometrico = new LeitorBiometrico();
        this.usuarioDAO = new UsuarioDAO();
        this.registroAcessoDAO = new RegistroAcessoDAO();
    }

    public Optional<Usuario> autenticarPorBiometria(java.util.function.Consumer<String> statusUpdater) {
        leitorBiometrico.conectar();
        Optional<String> cpfAutenticadoOpt = leitorBiometrico.verificarDigital(statusUpdater);
        leitorBiometrico.desconectar();

        if (cpfAutenticadoOpt.isEmpty()) {
            registrarAcesso(null, "Falha na Verificação Biometrica");
            statusUpdater.accept("Acesso Negado. Tente novamente.");
            return Optional.empty();
        }

        String cpfAutenticado = cpfAutenticadoOpt.get();
        Usuario usuario = usuarioDAO.buscarPorCpf(cpfAutenticado);

        if (usuario != null) {
            if (usuario.isAtivo()) {
                registrarAcesso(usuario, "Acesso Permitido");
                statusUpdater.accept("Acesso Permitido: " + usuario.getNome());
                return Optional.of(usuario);
            } else {
                registrarAcesso(usuario, "Acesso Negado (Usuário Inativo)");
                statusUpdater.accept("Acesso Negado: Usuário Inativo.");
                return Optional.empty();
            }
        } else {
            registrarAcesso(null, "Acesso Negado (Usuário Não Encontrado)");
            statusUpdater.accept("Acesso Negado: Usuário não encontrado.");
            return Optional.empty();
        }
    }

    public Optional<Administrador> autenticarAdminPorCredenciais(String login, String senha) {
        Usuario usuario = usuarioDAO.buscarPorLogin(login);
        if (usuario instanceof Administrador) {
            Administrador admin = (Administrador) usuario;
            String senhaHasheada = HashUtils.hashSenha(senha);
            if (senhaHasheada.equals(admin.getSenhaHash())) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }

    private void registrarAcesso(Usuario usuario, String status) {
        int usuarioId = (usuario != null) ? usuario.getId() : 0;
        registroAcessoDAO.salvar(new RegistroAcesso(LocalDateTime.now(), usuarioId, status, ORIGEM_TERMINAL));
    }
}