import controller.TerminalController;
import view.TelaAutenticacao;

/**
 * Classe principal que inicia a aplicação do Sistema de Controle de Acesso.
 * Versão Final Validada.
 */
public class Main {

    public static void main(String[] args) { 

          
            TerminalController controller = new TerminalController();

         
            TelaAutenticacao telaPrincipal = new TelaAutenticacao(controller);

            
            telaPrincipal.setVisible(true);
        
    }
}