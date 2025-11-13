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

    public boolean cadastrarNovoFuncionario(String nome, String cpf, String email, String cargo,
            boolean isAdmin, String login, String senha) { // <--- NOVOS PARÂMETROS
        System.out.println("[GerenciadorUsuarios] Capturando digital para novo funcionário...");

        // ✅ Validação de CPF (ADICIONAR CASO NÃO EXISTA): Evita cadastrar CPF duplicado
        // antes da digital
        if (usuarioDAO.buscarPorCPF(cpf) != null) {
            System.err.println("[GerenciadorUsuarios] FALHA DE NEGOCIO: CPF já existe: " + cpf);
            return false;
        }

        // ✅ leitorBiometrico returns Optional<String> (Base64)
        Optional<String> digitalOpt = leitorBiometrico.capturarDigital();

        if (digitalOpt.isEmpty()) {
            System.err.println("[GerenciadorUsuarios] Falha na captura da digital.");
            return false;
        }

        // ✅ Decode Base64 into raw bytes before saving in DB
        byte[] digitalBytes = Base64.getDecoder().decode(digitalOpt.get());

        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setCpf(cpf);
        novo.setEmail(email);
        novo.setCargo(cargo);
        novo.setAtivo(true);
        novo.setDigitalTemplate(digitalBytes);

        if (isAdmin) {
            // --- LÓGICA PARA ADMINISTRADOR ---
            novo.setTipoUsuario("ADMINISTRADOR");
            novo.setLogin(login);
            novo.setSenhaHash(senha); // Usa a senha em texto puro (não criptografada)
        } else {
            // --- LÓGICA ORIGINAL PARA FUNCIONÁRIO ---
            novo.setTipoUsuario("FUNCIONARIO");
            novo.setLogin(cpf);
            novo.setSenhaHash("123"); // password placeholder for now
        }

        boolean ok = usuarioDAO.inserir(novo);

        if (ok)
            System.out.println("[GerenciadorUsuarios] Usuário cadastrado com sucesso: " + nome);
        else
            System.err.println("[GerenciadorUsuarios] Falha ao inserir usuário no banco.");

        return ok;
    }

    public Optional<Usuario> identificarUsuarioPorBiometria() {
        System.out.println("[GerenciadorUsuarios] Iniciando identificação biométrica...");

        List<Usuario> usuarios = usuarioDAO.listarTodos();
        if (usuarios.isEmpty()) {
            System.err.println("[GerenciadorUsuarios] Nenhum usuário encontrado no banco.");
            return Optional.empty();
        }

        // ✅ Convert all fingerprint templates to Base64 strings
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

        // ✅ identify expects List<String>
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

    public boolean editarUsuario(Usuario usuario, boolean isAdmin, String login, String senha) {

        // Assumo que usuario.getNome(), usuario.getEmail() e usuario.isAtivo()
        // já foram atualizados pela camada View (TelaEdicaoUsuario).

        if (usuario == null) {
            System.err.println("[GerenciadorUsuarios] Usuário a ser editado é nulo.");
            return false;
        }

        // Lógica de ADM/Permissão
        if (isAdmin) {
            usuario.setTipoUsuario("ADMINISTRADOR");

            // O Login é obrigatório (validado na View).
            usuario.setLogin(login);

            // Se a senha foi preenchida (não é null), usa o novo valor.
            // Se for null, o Service deve preservar a senha hash existente no objeto.
            if (senha != null && !senha.isEmpty()) {
                usuario.setSenhaHash(senha); // Usa a senha em texto puro (NÃO SEGURO!)
            }
        } else {
            // Demote (remover privilégio de ADM): Define como Funcionário.
            usuario.setTipoUsuario("FUNCIONARIO");
            // Reverte para os valores padrão de funcionário:
            usuario.setLogin(usuario.getCpf());
            usuario.setSenhaHash("123");
        }

        // Salvar no Banco (assume que usuarioDAO.atualizar() existe)
        return usuarioDAO.atualizar(usuario);
    }
}
