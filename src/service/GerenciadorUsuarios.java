package service;

import dao.UsuarioDAO;
import model.Funcionario;
import model.Usuario;
import java.util.Optional;

/**
 * GerenciadorUsuarios (VERSÃO FINAL CORRIGIDA E SINCRONIZADA)
 * Centraliza a lógica de negócio para o gerenciamento de usuários.
 */
public class GerenciadorUsuarios {
    private final UsuarioDAO usuarioDAO;
    private final LeitorBiometrico leitorBiometrico;

    public GerenciadorUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new LeitorBiometrico();
    }

    /**
     * Orquestra o processo de cadastro de um novo funcionário.
     * 
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean cadastrarNovoFuncionario(String nome, String cpf, String email, String cargo, String matricula) {
        // 1. Validação da Regra de Negócio: CPF não pode ser duplicado.
        if (usuarioDAO.buscarPorCpf(cpf) != null) {
            System.err.println("FALHA DE NEGÓCIO: Tentativa de cadastrar um CPF que já existe: " + cpf);
            return false;
        }

        // 2. Interação com o Hardware
        leitorBiometrico.conectar();

        // --- LINHA CORRIGIDA ---
        // Agora passamos o propósito da leitura para o método, como ele espera.
        Optional<String> digitalHashOpt = leitorBiometrico.lerDigital("Cadastro de Novo Usuário");

        leitorBiometrico.desconectar();

        if (digitalHashOpt.isEmpty()) {
            System.err.println("FALHA DE HARDWARE: Não foi possível capturar a impressão digital.");
            return false;
        }

        // 3. Criação do Objeto do Modelo e Persistência
        Usuario novoFuncionario = new Funcionario(nome, cpf, email, digitalHashOpt.get(), cargo, matricula);
        usuarioDAO.salvar(novoFuncionario);

        return true;

    }

    // No GerenciadorUsuarios
    public boolean editarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        UsuarioDAO dao = new UsuarioDAO();
        return dao.atualizar(usuario);
    }

}