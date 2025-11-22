package controller;

import model.Administrador;
import model.RegistroAcesso;
import model.Usuario;
import service.GerenciadorUsuarios;
import service.SistemaAutenticacao;
import dao.UsuarioDAO;
import dao.RegistroAcessoDAO;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TerminalController {

    private final SistemaAutenticacao sistemaAutenticacao;
    private final GerenciadorUsuarios gerenciadorUsuarios;
    private final UsuarioDAO usuarioDAO;
    private final RegistroAcessoDAO registroAcessoDAO;

    public TerminalController() {
        this.sistemaAutenticacao = new SistemaAutenticacao();
        this.gerenciadorUsuarios = new GerenciadorUsuarios();
        this.usuarioDAO = new UsuarioDAO();
        this.registroAcessoDAO = new RegistroAcessoDAO();
    }

    public Optional<Usuario> solicitarAutenticacaoBiometrica(java.util.function.Consumer<String> statusUpdater) {
        return sistemaAutenticacao.autenticarPorBiometria(statusUpdater);
    }

    public Optional<Administrador> solicitarAutenticacaoAdmin(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }
        return sistemaAutenticacao.autenticarAdminPorCredenciais(login, senha);
    }

    public boolean solicitarCadastroNovoFuncionario(String nome, String cpf, String email, String cargo) {
        // Chama o método mais detalhado com valores padrão para não-admin e sem statusUpdater.
        return solicitarCadastroNovoFuncionario(nome, cpf, email, cargo, false, null, null, null);
    }

    public boolean solicitarCadastroNovoFuncionario(String nome, String cpf, String email, String cargo, boolean isAdmin, String login, String senha, java.util.function.Consumer<String> statusUpdater) {
        return gerenciadorUsuarios.cadastrarNovoUsuario(nome, cpf, email, cargo, isAdmin, login, senha, statusUpdater);
    }

    public List<Usuario> solicitarListaDeUsuarios() {
        return usuarioDAO.listarTodos();
    }

    public boolean removerUsuario(int id) {
        return usuarioDAO.remover(id);
    }

    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    public List<RegistroAcesso> solicitarRelatorioAcesso(LocalDateTime inicio, LocalDateTime fim) {
        return solicitarRelatorioAcesso(inicio, fim, null, null);
    }

    public List<RegistroAcesso> solicitarRelatorioAcesso(LocalDateTime inicio, LocalDateTime fim, String nomeUsuario, Integer idUsuario) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            return Collections.emptyList();
        }
        return registroAcessoDAO.listarPorPeriodo(inicio, fim, nomeUsuario, idUsuario);
    }

    public void editarUsuario(Usuario usuario) {
        GerenciadorUsuarios gerenciador = new GerenciadorUsuarios();
        gerenciador.editarUsuario(usuario);
    }

    public boolean exportarRelatorioCSV(List<String[]> dados, File arquivoSaida) {
        return registroAcessoDAO.exportarParaCSV(dados, arquivoSaida);
    }
}