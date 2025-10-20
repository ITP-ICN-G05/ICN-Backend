package com.gof.ICNBack.Utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password utility class for secure password hashing and verification
 * Uses BCrypt algorithm with automatic salting
 */
@Component
public class PasswordUtil {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Hash a plaintext password using BCrypt
     * 
     * @param plainPassword The plaintext password to hash
     * @return The hashed password with salt
     */
    public String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    /**
     * Verify a plaintext password against a hashed password
     * 
     * @param plainPassword  The plaintext password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
