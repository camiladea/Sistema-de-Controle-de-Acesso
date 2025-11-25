package service;

import dao.UsuarioDAO;
import java.util.*;
import model.Usuario;

public class GerenciadorUsuarios {

    private final UsuarioDAO usuarioDAO;
    private final NativeLibfprintReader leitorBiometrico;

    public GerenciadorUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
        this.leitorBiometrico = new NativeLibfprintReader();
    }

    public boolean cadastrarNovoFuncionario(String nome, String cpf, String email, String cargo) {
        System.out.println("[GerenciadorUsuarios] Capturando digital para novo funcionário...");
        
        // LeitorBiometrico returns Optional<String> (Base64)
        Optional<String> digitalOpt = leitorBiometrico.capturarDigital();

        if (digitalOpt.isEmpty()) {
            System.err.println("[GerenciadorUsuarios] Falha na captura da digital.");
            return false;
        }

        // Decode Base64 into raw bytes before saving in DB
        byte[] digitalBytes = Base64.getDecoder().decode(digitalOpt.get());

        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setCpf(cpf);
        novo.setEmail(email);
        novo.setCargo(cargo);
        novo.setAtivo(true);
        novo.setTipoUsuario("FUNCIONARIO");
        novo.setLogin(cpf);
        novo.setSenhaHash("123"); // password placeholder for "now"
        novo.setDigitalTemplate(digitalBytes);

        boolean ok = usuarioDAO.inserir(novo);

        if (ok)
            System.out.println("[GerenciadorUsuarios] Funcionário cadastrado com sucesso: " + nome);
        else
            System.err.println("[GerenciadorUsuarios] Falha ao inserir funcionário no banco.");

        return ok;
    }

    public Optional<Usuario> identificarUsuarioPorBiometria() {
        System.out.println("[GerenciadorUsuarios] Iniciando identificação biométrica...");

        List<Usuario> usuarios = usuarioDAO.listarTodos();
        if (usuarios.isEmpty()) {
            System.err.println("[GerenciadorUsuarios] Nenhum usuário encontrado no banco.");
            return Optional.empty();
        }

        // Convert all fingerprint templates to Base64 strings, those should be at least < 400 bytes.
        List<String> templatesBase64 = new ArrayList<>();
        for (Usuario u : usuarios) {
            byte[] tpl = u.getDigitalTemplate();
            if (tpl != null && tpl.length > 0)
                templatesBase64.add(Base64.getEncoder().encodeToString(tpl));
        }

        if (templatesBase64.isEmpty()) {
            System.err.println("[GerenciadorUsuarios] Nenhum usuário possui digital cadastrada.");
            return Optional.empty();
        }

        // Identify expects List<String>
        Optional<Integer> matchIndex = leitorBiometrico.identificar(templatesBase64);

        if (matchIndex.isPresent()) {
            int idx = matchIndex.get();
            if (idx >= 0 && idx < usuarios.size()) {
                Usuario u = usuarios.get(idx);
                System.out.println("[GerenciadorUsuarios] Digital corresponde ao usuário: " + u.getNome());
                return Optional.of(u);
            }
        }

        System.out.println("[GerenciadorUsuarios] Digital não reconhecida.");
        return Optional.empty();
    }

    public void editarUsuario(Usuario usuario) {
        if (usuario == null) return;
        boolean ok = usuarioDAO.atualizar(usuario);
        if (ok)
            System.out.println("[GerenciadorUsuarios] Usuário atualizado: " + usuario.getNome());
        else
            System.err.println("[GerenciadorUsuarios] Falha ao atualizar usuário.");
    }
}
