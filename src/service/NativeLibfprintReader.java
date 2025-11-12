package service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class NativeLibfprintReader {

    private static final String EXECUTABLE = "/usr/local/bin/fp_test"; // adjust if different

    /**
     * Enroll a new fingerprint and return Base64 string of template.
     */
    public Optional<String> capturarDigital() {
        try {
            ProcessBuilder pb = new ProcessBuilder(EXECUTABLE, "enroll");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("OK ")) {
                        String b64 = line.substring(3).trim();
                        System.out.println("[NativeLibfprintReader] Captura concluída: "
                                + b64.length() + " bytes base64");
                        process.waitFor(1, TimeUnit.SECONDS);
                        return Optional.of(b64);
                    }
                }
            }

            int exit = process.waitFor();
            System.err.println("[NativeLibfprintReader] Processo de captura finalizado com código: " + exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Identifica qual digital corresponde entre uma lista de templates base64.
     * Returns Optional<Integer> com o índice correspondente ou vazio se não encontrou.
     */
    public Optional<Integer> identificar(List<String> templatesBase64) {
        if (templatesBase64 == null || templatesBase64.isEmpty()) {
            System.err.println("[NativeLibfprintReader] Nenhum template fornecido para identificação.");
            return Optional.empty();
        }

        try {
            List<String> cmd = new ArrayList<>();
            cmd.add(EXECUTABLE);
            cmd.add("identify");
            cmd.addAll(templatesBase64); // pass base64 strings directly to fp_test identify

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("OK ")) {
                        int index = Integer.parseInt(line.substring(3).trim());
                        System.out.println("[NativeLibfprintReader] Digital reconhecida! Índice: " + index);
                        process.waitFor(1, TimeUnit.SECONDS);
                        return Optional.of(index);
                    } else if (line.contains("NO_MATCH")) {
                        System.out.println("[NativeLibfprintReader] Nenhuma correspondência encontrada.");
                        process.waitFor(1, TimeUnit.SECONDS);
                        return Optional.empty();
                    }
                }
            }

            int exit = process.waitFor();
            System.err.println("[NativeLibfprintReader] Processo identify finalizado com código: " + exit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
