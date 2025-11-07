package service;

import java.io.*;
import java.util.*;

/**
 * Wrapper around native C helper (`fp_test`) that interacts with libfprint 2.0.
 */
public class NativeLibfprintReader {

    private static final String HELPER_PATH = "/usr/local/bin/fp_test";

    public Optional<byte[]> capturarDigital() {
        ProcessBuilder pb = new ProcessBuilder(HELPER_PATH, "enroll");
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String base64Template = null;

            while ((line = reader.readLine()) != null) {
                System.out.println("[fp_test] " + line);
                if (line.startsWith("OK ")) {
                    base64Template = line.substring(3).trim();
                    break;
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("[NativeLibfprintReader] enroll exit code: " + exitCode);
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
     * Verify stored template by invoking helper which will prompt for a finger and return OK/FAIL.
     * This method is kept for single-template verification use-cases.
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
                if (line.trim().equals("OK")) {
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

    /**
     * IDENTIFY: send all stored templates to the helper in a single call and let it prompt the user once.
     *
     * Returns Optional<Integer> containing the matched index (0-based) when matched,
     * or Optional.empty() when no match or on error.
     */
    public Optional<Integer> identificar(List<byte[]> templates) {
        if (templates == null || templates.isEmpty()) return Optional.empty();

        // Build args: fp_test identify <base64-1> <base64-2> ...
        List<String> cmd = new ArrayList<>();
        cmd.add(HELPER_PATH);
        cmd.add("identify");
        for (byte[] t : templates) {
            cmd.add(Base64.getEncoder().encodeToString(t));
        }

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Optional<Integer> result = Optional.empty();

            while ((line = reader.readLine()) != null) {
                System.out.println("[fp_test] " + line);
                line = line.trim();
                // Expected outputs: "OK <index>" or "NO_MATCH"
                if (line.startsWith("OK ")) {
                    String num = line.substring(3).trim();
                    try {
                        int idx = Integer.parseInt(num);
                        result = Optional.of(idx);
                        break;
                    } catch (NumberFormatException ex) { /* ignore */ }
                } else if (line.equals("NO_MATCH") || line.equals("FAIL")) {
                    result = Optional.empty();
                }
            }

            process.waitFor();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
