import controller.TerminalController;
import view.TelaAutenticacao;

/**
 * Classe principal que inicia a aplicação do Sistema de Controle de Acesso.
 * Versão Final Validada.
 */
public class Main {

    public static void main(String[] args) { 

            // 1. Cria a instância única do nosso controller.
            TerminalController controller = new TerminalController();

            // 2. Cria a tela principal da aplicação, passando o controller.
            TelaAutenticacao telaPrincipal = new TelaAutenticacao(controller);

            // 3. Torna a tela visível.
            telaPrincipal.setVisible(true);
        
    }
}