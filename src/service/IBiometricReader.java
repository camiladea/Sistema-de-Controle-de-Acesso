package service;

import java.util.Optional;

/**
 * Interface for biometric readers.
 * Implementations: NativeLibfprintReader (libfprint helper) and FprintdReader (legacy).
 */
public interface IBiometricReader {
    /**
     * Enroll and return serialized template bytes (Optional.empty() on failure).
     */
    Optional<byte[]> enrollTemplate();

    /**
     * Verify a stored serialized template by capturing a live print and comparing.
     * Returns true on match, false otherwise.
     */
    boolean verifyTemplate(byte[] storedTemplate);
}
