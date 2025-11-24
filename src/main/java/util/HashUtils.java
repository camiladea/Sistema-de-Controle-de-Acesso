package util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HashUtils {
    
    public static String hashSenha(String senha) {
        if (senha == null || senha.isEmpty()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(HashUtils.class.getName()).log(Level.SEVERE, "Algoritmo de hashing SHA-256 não encontrado", e);
            // Em um caso real, uma exceção mais específica de aplicativo deveria ser lançada.
            throw new RuntimeException("Erro crítico de segurança: algoritmo de hash não disponível.", e);
        }
    }
    
    public static String gerarHashSHA256(InputStream inputStream) throws Exception {
        try {
            // Cria uma instância do MessageDigest com o algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;

            // Lê os dados do InputStream em blocos e atualiza o digest
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            // Obtém o hash final
            byte[] hashedBytes = digest.digest();

            // Converte o array de bytes para uma string hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Lança uma exceção se o algoritmo não for encontrado (improvável, mas boa prática)
            throw new Exception("Algoritmo de hash não encontrado: " + e.getMessage(), e);
        }
    }
}
