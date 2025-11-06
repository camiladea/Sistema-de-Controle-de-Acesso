package service;

import java.io.*;
import java.util.*;

/**
 * Wrapper around native C helper (`fp_test`) that interacts with libfprint 2.0.
 * Handles capturing and verifying fingerprints via process calls.
 */
public class NativeLibfprintReader {

    // Path to compiled binary
    private static final String HELPER_PATH = "/usr/local/bin/fp_test";

    /**
     * Captures a fingerprint and returns its Base64-encoded template.
     *
     * @return Optional containing the captured fingerprint as bytes.
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
                System.out.println("[fp_test] " + line); // debug log
                if (line.startsWith("BASE64:")) { // new format
                    base64Template = line.substring("BASE64:".length()).trim();
                    break;
                } else if (line.startsWith("OK ")) {
                    base64Template = line.substring(3).trim();
                    break;
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("[NativeLibfprintReader] Processo terminou com código: " + exitCode);
            }

            if (base64Template != null && !base64Template.isEmpty()) {
                byte[] templateBytes = Base64.getDecoder().decode(base64Template);
                System.out.println("[NativeLibfprintReader] Captura concluída: " + templateBytes.length + " bytes");
                return Optional.of(templateBytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("[NativeLibfprintReader] Falha ao capturar digital.");
        return Optional.empty();
    }

    /**
     * Verifies a fingerprint against a stored Base64 template.
     *
     * @param storedTemplate Template from DB
     * @return true if matched, false otherwise
     */
    public boolean verificarDigital(byte[] storedTemplate) {
        if (storedTemplate == null) {
            System.err.println("[NativeLibfprintReader] ERRO: Template armazenado é nulo.");
            return false;
        }

        String base64 = Base64.getEncoder().encodeToString(storedTemplate);
        ProcessBuilder pb = new ProcessBuilder(HELPER_PATH, "verify", base64);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean matched = false;

            while ((line = reader.readLine()) != null) {
                System.out.println("[fp_test] " + line);
                if (line.contains("MATCH") || line.contains("OK MATCH")) {
                    matched = true;
                }
            }

            process.waitFor();
            return matched;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
