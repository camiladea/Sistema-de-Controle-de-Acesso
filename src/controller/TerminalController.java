package controller;

import model.Administrador;
import model.RegistroAcesso;
import model.Usuario;
import service.GerenciadorUsuarios;
import service.SistemaAutenticacao;
import dao.UsuarioDAO;
import dao.RegistroAcessoDAO;

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

    // --- AUTENTICAÇÃO ---
    public Optional<Usuario> solicitarAutenticacaoBiometrica() {
        return sistemaAutenticacao.autenticarPorBiometria();
    }

    public Optional<Administrador> solicitarAutenticacaoAdmin(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.isEmpty()) {
            return Optional.empty();
        }
        return sistemaAutenticacao.autenticarAdminPorCredenciais(login, senha);
    }

    // --- USUÁRIOS ---
    public boolean solicitarCadastroNovoFuncionario(String nome, String cpf, String email, String cargo,
            String matricula) {
        return gerenciadorUsuarios.cadastrarNovoFuncionario(nome, cpf, email, cargo, matricula);
    }

    public List<Usuario> solicitarListaDeUsuarios() {
        return usuarioDAO.listarTodos();
    }

    // 🚀 Novo: remover usuário
    public boolean removerUsuario(int id) {
        return usuarioDAO.remover(id);
    }

    // 🚀 Novo: buscar usuário por ID
    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    // 🚀 Opcional: editar usuário

    // --- RELATÓRIOS ---
    public List<RegistroAcesso> solicitarRelatorioAcesso(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            return Collections.emptyList();
        }
        return registroAcessoDAO.listarPorPeriodo(inicio, fim);
    }

    public void editarUsuario(Usuario usuario) {
        GerenciadorUsuarios gerenciador = new GerenciadorUsuarios();
        gerenciador.editarUsuario(usuario);
    }

}
