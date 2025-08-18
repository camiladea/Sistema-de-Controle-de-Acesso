import com.formdev.flatlaf.FlatDarkLaf;
import controller.TerminalController;
import util.ConexaoBancoDados;
import view.TelaAutenticacao;

import java.sql.Connection;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException; // Import específico

/**
 * Classe principal que inicia a aplicação do Sistema de Controle de Acesso.
 * Versão Final Validada.
 */
public class Main {

    public static void main(String[] args) {
        // SwingUtilities.invokeLater garante que a GUI seja criada e manipulada
        // na thread correta (Event Dispatch Thread - EDT).
        SwingUtilities.invokeLater(() -> {
            try {
                // Configura o Look and Feel ANTES de qualquer componente Swing ser criado.
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException e) {
                // Se o tema falhar, a aplicação ainda rodará com o tema padrão do Java.
                System.err.println("Falha ao inicializar o tema FlatLaf. Usando tema padrão.");
                e.printStackTrace();
            }

            Connection conexao = ConexaoBancoDados.getConexao();
            // 1. Cria a instância única do nosso controller.
            TerminalController controller = new TerminalController();

            // 2. Cria a tela principal da aplicação, passando o controller.
            TelaAutenticacao telaPrincipal = new TelaAutenticacao(controller);

            // 3. Torna a tela visível.
            telaPrincipal.setVisible(true);
        });
    }
}