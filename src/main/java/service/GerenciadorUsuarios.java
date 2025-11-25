package service;

import dao.UsuarioDAO;
import model.Administrador;
import model.Funcionario;
import model.Usuario;
import util.HashUtils;
import java.util.logging.Logger;

public class GerenciadorUsuarios {

    private static final Logger LOGGER = Logger.getLogger(GerenciadorUsuarios.class.getName());
    private final UsuarioDAO usuarioDAO;
    private final LeitorBiometrico leitorBiometrico;

    public GerenciadorUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new LeitorBiometrico();
    }

    public boolean cadastrarNovoUsuario(String nome, String cpf, String email, String cargo, boolean isAdmin, String login, String senha, java.util.function.Consumer<String> statusUpdater) {
        if (usuarioDAO.buscarPorCpf(cpf) != null) {
            LOGGER.warning("Tentativa de cadastrar um CPF que já existe: " + cpf);
            return false;
        }

        if (isAdmin) {
            if (login == null || login.trim().isEmpty() || usuarioDAO.buscarPorLogin(login) != null) {
                LOGGER.warning("Tentativa de cadastrar um admin com login inválido ou duplicado: " + login);
                return false;
            }
        }

        LOGGER.info("Iniciando captura de digital para o usuário: " + nome);
        // A lógica agora é registrar a digital no fprintd usando o CPF como chave.
        boolean digitalCadastrada = leitorBiometrico.enroll(cpf, statusUpdater);

        if (!digitalCadastrada) {
            LOGGER.severe("Não foi possível capturar a impressão digital para o usuário: " + nome);
            return false;
        }
        LOGGER.info("Captura de digital concluída com sucesso.");

        Usuario novoUsuario;
        if (isAdmin) {
            String senhaHasheada = HashUtils.hashSenha(senha);
            // Passamos 'null' para os templates de digital, pois eles não são mais armazenados aqui.
            novoUsuario = new Administrador(nome, cpf, email, null, null, null, login, senhaHasheada);
        } else {
            // Passamos 'null' para os templates de digital.
            novoUsuario = new Funcionario(nome, cpf, email, null, null, null, cargo);
        }

        boolean sucesso = usuarioDAO.salvar(novoUsuario);
        if (sucesso) {
            LOGGER.info("Usuário salvo com sucesso. ID: " + novoUsuario.getId());
        } else {
            LOGGER.severe("Falha ao salvar o usuário no banco de dados.");
        }
        return sucesso;
    }

    public boolean editarUsuario(Usuario usuario) {
        if (usuario == null) {
            LOGGER.warning("Tentativa de editar um usuário nulo.");
            return false;
        }
        return usuarioDAO.atualizar(usuario);
    }
}