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
     * @param statusUpdater Callback para enviar atualizações de status para a UI.
     * @return true se o cadastro foi bem-sucedido, false caso contrário.
     */
    public boolean enroll(String cpf, java.util.function.Consumer<String> statusUpdater) {
        if (cpf == null || cpf.trim().isEmpty()) {
            statusUpdater.accept("Erro: CPF inválido.");
            System.err.println("ERRO: CPF não pode ser nulo ou vazio para o cadastro da digital.");
            return false;
        }
        try {
            System.out.println("LOG [LeitorBiometrico]: Iniciando captura de digital para o CPF: " + cpf);
            ProcessBuilder pb = new ProcessBuilder("fprintd-enroll", cpf);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder fullOutput = new StringBuilder();
                String line;
                int stage = 1;

                while ((line = reader.readLine()) != null) {
                    System.out.println("[fprintd-enroll] " + line);
                    fullOutput.append(line);

                    String userMessage = parseFprintdMessage(line, stage);
                    statusUpdater.accept(userMessage);

                    if (line.contains("enroll-stage-passed")) {
                        stage++;
                    }
                }
                process.waitFor();

                if (fullOutput.toString().contains("Enroll result: enroll-completed")) {
                    statusUpdater.accept("Cadastro concluído!");
                    System.out.println("LOG [LeitorBiometrico]: Digital para o CPF " + cpf + " cadastrada com sucesso.");
                    return true;
                } else {
                    statusUpdater.accept("Falha no cadastro.");
                    System.err.println("ERRO: Falha ao capturar digital (saída do fprintd: " + fullOutput + ")");
                    return false;
                }
            }
        } catch (Exception e) {
            statusUpdater.accept("Erro crítico no leitor.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Traduz a saída do fprintd para mensagens mais amigáveis.
     */
    private String parseFprintdMessage(String rawLine, int currentStage) {
        final int totalStages = 10; // Requisito do usuário: 10 capturas.
        if (rawLine.contains("Place your finger on the reader")) {
            return String.format("Posicione o dedo no leitor... (%d/%d)", currentStage, totalStages);
        }
        if (rawLine.contains("Remove your finger from the reader")) {
            return "Remova o dedo do leitor.";
        }
        if (rawLine.contains("Enrolling")) {
            return "Iniciando cadastro...";
        }
        if (rawLine.contains("enroll-stage-passed")) {
            return String.format("Captura %d de %d bem-sucedida!", currentStage, totalStages);
        }
        if (rawLine.contains("enroll-completed")) {
            return "Cadastro da digital concluído com sucesso!";
        }
        return "Aguarde...";
    }

    /**
     * Realiza a verificação de digital via fprintd-verify.
     * Retorna o CPF do usuário autenticado, se houver.
     */
    public Optional<String> verificarDigital() {
        try {
            System.out.println("LOG [LeitorBiometrico]: Iniciando verificação biométrica...");
            // fprintd-verify sem argumento tenta verificar qualquer digital cadastrada
            ProcessBuilder pb = new ProcessBuilder("fprintd-verify");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                String authenticatedUser = null;

                while ((line = reader.readLine()) != null) {
                    System.out.println("[fprintd-verify] " + line);
                    output.append(line);
                    if (line.contains("verify-match")) {
                        // Ex: "verify-match (username: 12345678900)"
                        int startIndex = line.indexOf("username: ");
                        if (startIndex != -1) {
                            startIndex += "username: ".length();
                            int endIndex = line.indexOf(")", startIndex);
                            if (endIndex != -1) {
                                authenticatedUser = line.substring(startIndex, endIndex).trim();
                            }
                        }
                    }
                }
                process.waitFor();

                if (authenticatedUser != null) {
                    System.out.println("LOG [LeitorBiometrico]: Digital verificada com sucesso para o usuário: " + authenticatedUser);
                    return Optional.of(authenticatedUser);
                } else {
                    System.err.println("LOG [LeitorBiometrico]: Digital não corresponde ou falha na verificação.");
                    return Optional.empty();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
