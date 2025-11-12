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
    if (templates == null || templates.isEmpty()) {
        System.err.println("[NativeLibfprintReader] Nenhum template válido encontrado para identificação.");
        return Optional.empty();
    }

    // Log each template’s length
    for (int i = 0; i < templates.size(); i++) {
        System.out.println("[NativeLibfprintReader] Template " + i + " length = " + templates.get(i).length + " bytes");
    }

    // Build args: fp_test identify <base64-1> <base64-2> ...
    List<String> cmd = new ArrayList<>();
    cmd.add(HELPER_PATH);
    cmd.add("identify");
    for (byte[] t : templates) {
        String b64 = Base64.getEncoder().encodeToString(t);
        cmd.add(b64);
    }

    System.out.println("[NativeLibfprintReader] Executando comando:");
    for (String c : cmd) System.out.print(c + " ");
    System.out.println();

    ProcessBuilder pb = new ProcessBuilder(cmd);
    pb.redirectErrorStream(true);

    try {
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        Optional<Integer> result = Optional.empty();

        while ((line = reader.readLine()) != null) {
            System.out.println("[fp_test output] " + line);
            line = line.trim();
            if (line.startsWith("OK ")) {
                String num = line.substring(3).trim();
                try {
                    int idx = Integer.parseInt(num);
                    result = Optional.of(idx);
                    break;
                } catch (NumberFormatException ex) {
                    System.err.println("[NativeLibfprintReader] Falha ao interpretar índice: " + num);
                }
            } else if (line.equals("NO_MATCH") || line.equals("FAIL")) {
                result = Optional.empty();
            }
        }

        int exit = process.waitFor();
        System.out.println("[NativeLibfprintReader] fp_test terminou com código " + exit);
        return result;

    } catch (Exception e) {
        System.err.println("[NativeLibfprintReader] ERRO ao identificar:");
        e.printStackTrace();
        return Optional.empty();
    }
}

}
