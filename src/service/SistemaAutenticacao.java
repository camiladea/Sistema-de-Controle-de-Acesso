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
 * SistemaAutenticacao (LÓGICA DE SIMULAÇÃO CORRIGIDA)
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
        Optional<String> firOpt = leitorBiometrico.lerDigital("Autenticação");
        leitorBiometrico.desconectar();

        if (firOpt.isEmpty()) {
            registrarAcesso(null, "Falha na Captura");
            return Optional.empty();
        }

        String firCapturada = firOpt.get();

        // Pega todos os usuários ativos com FIR
        List<Usuario> usuarios = usuarioDAO.listarTodos().stream()
                .filter(u -> u.getDigitalFIR() != null && !u.getDigitalFIR().isEmpty() && u.isAtivo())
                .toList();

        // Tenta validar cada um
        for (Usuario u : usuarios) {
            boolean match = leitorBiometrico.verificarDigital(firCapturada, u.getDigitalFIR());
            if (match) {
                registrarAcesso(u, "Acesso Permitido");
                System.out.println("Usuário autenticado: " + u.getNome());
                return Optional.of(u);
            }
        }

        // Se nenhum bateu
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