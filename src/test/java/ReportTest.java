import dao.RegistroAcessoDAO;
import dao.UsuarioDAO;
import model.RegistroAcesso;
import model.Usuario;
import util.DatabaseInitializer;

import java.time.LocalDateTime;
import java.util.List;

public class ReportTest {

    public static void main(String[] args) {
        System.out.println("Running ReportTest with real data...");

        // 1. Initialize the database
        DatabaseInitializer.initializeDatabase();
        System.out.println("Database initialized.");

        // 2. Check if admin user exists
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario admin = usuarioDAO.buscarPorLogin("admin");
        if (admin == null) {
            System.out.println("Test FAILED: Admin user not found.");
            return;
        }
        System.out.println("Admin user found.");

        // 3. Insert some test records
        RegistroAcessoDAO registroDAO = new RegistroAcessoDAO();
        LocalDateTime now = LocalDateTime.now();
        registroDAO.salvar(new RegistroAcesso(now.minusDays(1), admin.getId(), "permitido", "entrada"));
        registroDAO.salvar(new RegistroAcesso(now, admin.getId(), "negado", "saida"));
        registroDAO.salvar(new RegistroAcesso(now.plusDays(1), admin.getId(), "permitido", "entrada"));
        System.out.println("Inserted 3 test records.");

        // 4. Run tests
        boolean allTestsPassed = true;
        allTestsPassed &= testListarPorPeriodo_AllRecords();
        allTestsPassed &= testListarPorPeriodo_NoRecords();
        allTestsPassed &= testListarPorPeriodo_WithNameFilter();

        if (allTestsPassed) {
            System.out.println("All tests PASSED!");
        } else {
            System.out.println("Some tests FAILED.");
        }
    }

    private static boolean testListarPorPeriodo_AllRecords() {
        System.out.println("Running testListarPorPeriodo_AllRecords...");
        RegistroAcessoDAO registroDAO = new RegistroAcessoDAO();
        LocalDateTime now = LocalDateTime.now();
        List<RegistroAcesso> records = registroDAO.listarPorPeriodo(now.minusDays(2), now.plusDays(2), null, null);
        if (records.size() == 3) {
            System.out.println("testListarPorPeriodo_AllRecords PASSED!");
            return true;
        } else {
            System.out.println("testListarPorPeriodo_AllRecords FAILED: Expected 3 records, but found " + records.size());
            return false;
        }
    }

    private static boolean testListarPorPeriodo_NoRecords() {
        System.out.println("Running testListarPorPeriodo_NoRecords...");
        RegistroAcessoDAO registroDAO = new RegistroAcessoDAO();
        LocalDateTime now = LocalDateTime.now();
        List<RegistroAcesso> records = registroDAO.listarPorPeriodo(now.plusDays(3), now.plusDays(4), null, null);
        if (records.isEmpty()) {
            System.out.println("testListarPorPeriodo_NoRecords PASSED!");
            return true;
        } else {
            System.out.println("testListarPorPeriodo_NoRecords FAILED: Expected 0 records, but found " + records.size());
            return false;
        }
    }

    private static boolean testListarPorPeriodo_WithNameFilter() {
        System.out.println("Running testListarPorPeriodo_WithNameFilter...");
        RegistroAcessoDAO registroDAO = new RegistroAcessoDAO();
        LocalDateTime now = LocalDateTime.now();
        List<RegistroAcesso> records = registroDAO.listarPorPeriodo(now.minusDays(2), now.plusDays(2), "Administrador Principal", null);
        if (records.size() == 3) {
            System.out.println("testListarPorPeriodo_WithNameFilter PASSED!");
            return true;
        } else {
            System.out.println("testListarPorPeriodo_WithNameFilter FAILED: Expected 3 records, but found " + records.size());
            return false;
        }
    }
}