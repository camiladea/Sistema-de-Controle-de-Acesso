package main;
import controller.TerminalController;
import view.TelaAutenticacao;


public class Main {

    public static void main(String[] args) { 

          
            TerminalController controller = new TerminalController();

         
            TelaAutenticacao telaPrincipal = new TelaAutenticacao(controller);

            
            telaPrincipal.setVisible(true);
        
    }
}