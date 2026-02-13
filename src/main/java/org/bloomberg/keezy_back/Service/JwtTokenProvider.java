package org.bloomberg.keezy_back.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secret:KeezySecretKeyForJWTTokenGenerationAndValidation12345678901234567890}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration; // milliseconds

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] base64UrlDecode(String str) {
        return Base64.getUrlDecoder().decode(str);
    }

    private byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            long now = System.currentTimeMillis();
            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", userDetails.getUsername());
            payload.put("iat", now / 1000L);
            payload.put("exp", (now + jwtExpiration) / 1000L);
            
            // Add userId if available from AppUser
            if (userDetails instanceof org.bloomberg.keezy_back.Entity.AppUser) {
                org.bloomberg.keezy_back.Entity.AppUser appUser = (org.bloomberg.keezy_back.Entity.AppUser) userDetails;
                payload.put("userId", appUser.getId());
            }

            String headerJson = objectMapper.writeValueAsString(header);
            String payloadJson = objectMapper.writeValueAsString(payload);

            String headerB64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadB64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

            String signingInput = headerB64 + "." + payloadB64;
            byte[] signature = hmacSha256(jwtSecret.getBytes(StandardCharsets.UTF_8), signingInput);
            String signatureB64 = base64UrlEncode(signature);

            return signingInput + "." + signatureB64;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<>() {});
            Object sub = payload.get("sub");
            return sub != null ? sub.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract userId from JWT token
     * @param token JWT token
     * @return userId or null if not found
     */
    public String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<>() {});
            Object userId = payload.get("userId");
            return userId != null ? userId.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String signingInput = parts[0] + "." + parts[1];
            byte[] expectedSig = hmacSha256(jwtSecret.getBytes(StandardCharsets.UTF_8), signingInput);
            byte[] actualSig = base64UrlDecode(parts[2]);
            // constant-time compare
            if (expectedSig.length != actualSig.length) return false;
            int result = 0;
            for (int i = 0; i < expectedSig.length; i++) {
                result |= expectedSig[i] ^ actualSig[i];
            }
            if (result != 0) return false;

            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<>() {});
            Object expObj = payload.get("exp");
            if (expObj == null) return false;
            long exp = Long.parseLong(expObj.toString());
            long nowSec = System.currentTimeMillis() / 1000L;
            return nowSec <= exp;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract all claims from JWT token
     * @param token JWT token
     * @return Map of all claims or null if invalid
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            return objectMapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }
}
