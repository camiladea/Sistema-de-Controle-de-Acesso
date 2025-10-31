package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Base64;

/**
 * Interface Java → binário nativo libfprint.
 * O binário finger_helper deve estar em /usr/local/bin.
 */
public class LeitorBiometrico {

    private static final String HELPER_PATH = "/usr/local/bin/finger_helper";

    public Optional<byte[]> capturarDigitalParaCadastro() {
        try {
            ProcessBuilder pb = new ProcessBuilder(HELPER_PATH, "enroll");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[finger_helper] " + line);
                if (line.startsWith("OK ")) {
                    String base64 = line.substring(3).trim();
                    return Optional.of(Base64.getDecoder().decode(base64));
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean verificarDigital(byte[] template) {
        try {
            String b64 = Base64.getEncoder().encodeToString(template);
            ProcessBuilder pb = new ProcessBuilder(HELPER_PATH, "verify", b64);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[finger_helper] " + line);
                if (line.trim().equalsIgnoreCase("OK")) return true;
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}