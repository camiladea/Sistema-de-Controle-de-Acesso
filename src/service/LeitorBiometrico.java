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
     * Captura uma nova digital e a registra no serviço fprintd, associando-a ao CPF do usuário.
     * @param cpf O CPF do usuário, que será usado como 'username' no fprintd.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean enroll(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            System.err.println("ERRO: CPF não pode ser nulo ou vazio para o cadastro da digital.");
            return false;
        }
        try {
            System.out.println("LOG [LeitorBiometrico]: Iniciando captura de digital para o CPF: " + cpf);
            // Usamos o CPF como o nome de usuário para o fprintd
            ProcessBuilder pb = new ProcessBuilder("fprintd-enroll", cpf);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // É crucial consumir a saída do processo para evitar que ele trave
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[fprintd-enroll] " + line);
                    output.append(line);
                }
                process.waitFor();

                if (output.toString().contains("Enroll result: enroll-completed")) {
                    System.out.println("LOG [LeitorBiometrico]: Digital para o CPF " + cpf + " cadastrada com sucesso.");
                    return true;
                } else {
                    System.err.println("ERRO: Falha ao capturar digital (saída do fprintd: " + output + ")");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
