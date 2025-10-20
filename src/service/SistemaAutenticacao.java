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

    public Optional<Usuario> autenticarPorBiometria() {
        leitorBiometrico.conectar();
        Optional<String> hashOpt = leitorBiometrico.lerDigital("Autenticação");
        leitorBiometrico.desconectar();

        if (hashOpt.isEmpty()) {
            registrarAcesso(null, "Falha na Captura");
            return Optional.empty();
        }

        // Para simular um login bem-sucedido, vamos pegar o primeiro usuário com digital
        // que encontrarmos no banco e assumir que a digital dele "correspondeu".
        List<Usuario> todosOsUsuarios = usuarioDAO.listarTodos();
        
        Optional<Usuario> usuarioCorrespondente = todosOsUsuarios.stream()
        .filter(u -> u.getDigitalTemplate() != null && u.getDigitalTemplate().length > 0)
            .findFirst();

        if (usuarioCorrespondente.isPresent() && usuarioCorrespondente.get().isAtivo()) {
            registrarAcesso(usuarioCorrespondente.get(), "Acesso Permitido (Simulado)");
            return usuarioCorrespondente;
        }
        // Se não houver usuários com digital ou o encontrado estiver inativo...
        
        registrarAcesso(null, "Acesso Negado");
        return Optional.empty();
    }

    public Optional<Administrador> autenticarAdminPorCredenciais(String login, String senha) {
        Usuario usuario = usuarioDAO.buscarPorLogin(login);
        if (usuario instanceof Administrador) {
            Administrador admin = (Administrador) usuario;
            if (senha.equals(admin.getSenhaHash())) { // Simulação, usar BCrypt em produção
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