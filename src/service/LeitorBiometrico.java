package service;

import javax.swing.JOptionPane;
import java.util.Optional;

/**
 * LeitorBiometrico (SIMULAÇÃO CORRIGIDA)
 * Utiliza JOptionPane para uma simulação gráfica e robusta, sem erros de loop.
 */
public class LeitorBiometrico {

    public void conectar() {
        System.out.println("LOG [LeitorBiometrico]: SIMULAÇÃO - Conectado.");
    }

    public void desconectar() {
        System.out.println("LOG [LeitorBiometrico]: SIMULAÇÃO - Desconectado.");
    }

    /**
     * SIMULAÇÃO CORRIGIDA: Usa uma caixa de diálogo para pausar e simular a leitura.
     * @return um Optional contendo um hash de digital simulado.
     */
    public Optional<String> lerDigital(String proposito) {
        // Mostra uma janela gráfica para o usuário
        JOptionPane.showMessageDialog(
            null, 
            "Simulação de Leitor Biométrico:\nPor favor, posicione o dedo no leitor e clique em OK.",
            "Aguardando Biometria (" + proposito + ")", 
            JOptionPane.INFORMATION_MESSAGE
        );

        // Gera um hash único para a simulação
        String hashSimulado = "HASH_SIMULADO_" + System.currentTimeMillis();
        System.out.println("LOG [LeitorBiometrico]: Hash gerado: " + hashSimulado);
        return Optional.of(hashSimulado);
    }
}