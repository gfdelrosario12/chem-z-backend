package com.chemz.lms.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class VerificationCodeService {

    private final Map<String, CodeEntry> codeStore = new HashMap<>();

    private static class CodeEntry {
        String code;
        LocalDateTime expiresAt;

        CodeEntry(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    public String generateCode(String email) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit

        codeStore.put(email, new CodeEntry(
                code,
                LocalDateTime.now().plusMinutes(10)
        ));

        return code;
    }

    public boolean verifyCode(String email, String code) {
        if (!codeStore.containsKey(email)) return false;

        CodeEntry entry = codeStore.get(email);

        if (entry.expiresAt.isBefore(LocalDateTime.now())) return false;

        return entry.code.equals(code);
    }
}
