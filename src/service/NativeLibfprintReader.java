package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Base64;

/**
 * Wrapper around the native C helper (`finger_helper`) that interacts with libfprint.
 * 
 * Handles capturing and comparing fingerprint templates via process calls.
 */
public class NativeLibfprintReader {

    private static final String HELPER_PATH = "/usr/local/bin/finger_helper"; // adjust if in another path

    /**
     * Captures a fingerprint template by invoking the helper binary.
     * 
     * @return Optional containing the captured fingerprint bytes (template), or empty if failed.
     */
    public Optional<byte[]> capturarDigital() {
        ProcessBuilder pb = new ProcessBuilder(HELPER_PATH, "enroll");
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            String base64Template = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("OK ")) {
                    base64Template = line.substring(3).trim();
                    break;
                }
            }

            process.waitFor();

            if (base64Template != null && !base64Template.isEmpty()) {
                byte[] templateBytes = Base64.getDecoder().decode(base64Template);
                System.out.println("[NativeLibfprintReader] Captura de digital concluída. Tamanho: " + templateBytes.length + " bytes");
                return Optional.of(templateBytes);
            } else {
                System.err.println("[NativeLibfprintReader] Falha ao capturar digital (sem saída OK).");
                return Optional.empty();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Compares two fingerprint templates by calling the helper binary.
     * 
     * @param storedTemplate The template stored in the database.
     * @param candidateTemplate The newly captured template.
     * @return true if templates match, false otherwise.
     */
    public boolean compararDigitais(byte[] storedTemplate, byte[] candidateTemplate) {
        if (storedTemplate == null || candidateTemplate == null) {
            System.err.println("[NativeLibfprintReader] ERRO: Um dos templates está nulo.");
            return false;
        }

        String storedBase64 = Base64.getEncoder().encodeToString(storedTemplate);
        String candidateBase64 = Base64.getEncoder().encodeToString(candidateTemplate);

        ProcessBuilder pb = new ProcessBuilder(HELPER_PATH, "verify", storedBase64);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            boolean match = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("OK")) {
                    match = true;
                    break;
                }
            }

            process.waitFor();

            System.out.println("[NativeLibfprintReader] Resultado da verificação: " + (match ? "CORRESPONDÊNCIA" : "NÃO CORRESPONDE"));
            return match;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
