package service;

import dao.UsuarioDAO;
import model.Funcionario;
import model.Usuario;
import java.util.Optional;

public class GerenciadorUsuarios {

    private final UsuarioDAO usuarioDAO;
    private final LeitorBiometrico leitorBiometrico;

    public GerenciadorUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new LeitorBiometrico();
    }

    public boolean cadastrarNovoFuncionario(String nome, String cpf, String email, String cargo) {
        if (usuarioDAO.buscarPorCpf(cpf) != null) {
            System.err.println("FALHA DE NEGOCIO: Tentativa de cadastrar um CPF que já existe: " + cpf);
            return false;
        }

        leitorBiometrico.conectar();
        Optional<String> digitalHashOpt = leitorBiometrico.lerDigital("Cadastro de Novo Usuário");
        leitorBiometrico.desconectar();

        if (digitalHashOpt.isEmpty()) {
            System.err.println("FALHA DE HARDWARE: Não foi possível capturar a impressão digital.");
            return false;
        }

        Usuario novoFuncionario = new Funcionario(nome, cpf, email, digitalHashOpt.get(), cargo);
        usuarioDAO.salvar(novoFuncionario);
        return true;
    }

    public boolean editarUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        UsuarioDAO dao = new UsuarioDAO();
        return dao.atualizar(usuario);
    }
}