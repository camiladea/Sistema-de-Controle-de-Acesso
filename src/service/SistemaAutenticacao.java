package service;

import dao.RegistroAcessoDAO;
import dao.UsuarioDAO;
import model.Administrador;
import model.RegistroAcesso;
import model.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.libfprint.*;
import java.sql.SQLException;

/**
 * Gerencia a autenticação biométrica.
 */
public class SistemaAutenticacao {

    private final UsuarioDAO usuarioDAO;
    private final LeitorBiometrico leitor;

    public SistemaAutenticacao() {
        usuarioDAO = new UsuarioDAO();
        leitor = new LeitorBiometrico();
    }

    public boolean registrarDigital(int usuarioId) {
        Optional<byte[]> opt = leitor.capturarDigitalParaCadastro();
        if (opt.isEmpty()) {
            System.out.println("Falha ao capturar digital.");
            return false;
        }
        try {
            usuarioDAO.salvarDigital(usuarioId, opt.get());
            System.out.println("Template biométrico armazenado.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarDigital(int usuarioId) {
        try {
            byte[] template = usuarioDAO.obterDigitalPorId(usuarioId);
            if (template == null) {
                System.out.println("Usuário sem digital cadastrada.");
                return false;
            }
            return leitor.verificarDigital(template);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}