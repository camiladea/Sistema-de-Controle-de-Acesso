package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * LeitorBiometrico usando fprintd via terminal (ProcessBuilder)
 * Compatível com Digital Persona U.are.U 4000B e libfprint-2.
 * 
 * Este método é o mais confiável para Armbian ARM64 e não requer acesso root direto.
 */
public class LeitorBiometrico {

    public void conectar() {
        System.out.println("LOG [LeitorBiometrico]: Inicializando via fprintd (daemon do sistema).");
    }

    public void desconectar() {
        System.out.println("LOG [LeitorBiometrico]: Encerrando conexão (nenhuma ação necessária para fprintd).");
    }

    /**
     * Captura uma nova digital e retorna o hash (string simbólica) do usuário.
     * Este método invoca o daemon fprintd, portanto funciona igual ao terminal.
     */
    public Optional<String> lerDigital(String proposito) {
        try {
            System.out.println("LOG [LeitorBiometrico]: Capturando digital (" + proposito + ")");
            ProcessBuilder pb = new ProcessBuilder("fprintd-enroll", "usuario_padrao");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[fprintd] " + line);
                output.append(line).append("\n");
            }
            process.waitFor();

            if (output.toString().contains("enroll-completed")) {
                String hash = "FP_" + System.currentTimeMillis();
                System.out.println("LOG [LeitorBiometrico]: Digital cadastrada com sucesso.");
                return Optional.of(hash);
            } else {
                System.err.println("ERRO: Falha ao capturar digital (mensagem: " + output + ")");
                return Optional.empty();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Realiza a verificação de digital via fprintd-verify.
     * O parâmetro hash é simbólico e serve apenas para consistência no sistema.
     */
    public boolean verificarDigital(String hashSalvo) {
        try {
            System.out.println("LOG [LeitorBiometrico]: Iniciando verificação biométrica...");
            ProcessBuilder pb = new ProcessBuilder("fprintd-verify", "usuario_padrao");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[fprintd] " + line);
                output.append(line).append("\n");
            }
            process.waitFor();

            boolean sucesso = output.toString().contains("verify-match");
            if (sucesso) {
                System.out.println("LOG [LeitorBiometrico]: Digital verificada com sucesso!");
            } else {
                System.err.println("LOG [LeitorBiometrico]: Digital não corresponde.");
            }
            return sucesso;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
