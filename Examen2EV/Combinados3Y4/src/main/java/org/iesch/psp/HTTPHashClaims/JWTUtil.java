package org.iesch.psp.HTTPHashClaims;// --- JWTUtil.java ---
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JWTUtil {

    // Clave secreta para firmar los tokens
    private static final byte[] SECRET_KEY = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
            0x17, 0x18, 0x19, 0x20, 0x21, 0x22, 0x23, 0x24,
            0x25, 0x26, 0x27, 0x28, 0x29, 0x30, 0x31, 0x32
    };

    // Generar token con claims
    public static String generateToken(List<Claim> claims) {
        try {
            // Header (simulado en Base64)
            String header = Base64.getEncoder().encodeToString(
                    "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

            // Payload con claims
            StringBuilder payloadJson = new StringBuilder("{");
            for (int i = 0; i < claims.size(); i++) {
                Claim c = claims.get(i);
                payloadJson.append("\"").append(c.getType()).append("\":\"").append(c.getValue()).append("\"");
                if (i < claims.size() - 1) {
                    payloadJson.append(",");
                }
            }
            payloadJson.append("}");

            String payload = Base64.getEncoder().encodeToString(
                    payloadJson.toString().getBytes(StandardCharsets.UTF_8));

            // Signature: HMAC-SHA256 de header.payload
            String data = header + "." + payload;
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(signatureBytes);

            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Verificar token y devolver claims
    public static List<Claim> verifyToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            // Recalculamos la firma
            String data = header + "." + payload;
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] expectedSignature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String expectedSignatureStr = Base64.getEncoder().encodeToString(expectedSignature);

            // Comparamos firmas
            if (!signature.equals(expectedSignatureStr)) {
                return null; // Firma no válida
            }

            // Decodificamos payload y extraemos claims
            String payloadJson = new String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8);
            // Parseamos JSON simple: {"key":"value","key2":"value2"}
            payloadJson = payloadJson.replace("{", "").replace("}", "").replace("\"", "");
            String[] pairs = payloadJson.split(",");

            List<Claim> claims = new ArrayList<>();
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    claims.add(new Claim(kv[0].trim(), kv[1].trim()));
                }
            }
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    // Obtener valor de un claim del token
    public static String getClaimValue(List<Claim> claims, String type) {
        for (Claim c : claims) {
            if (c.getType().equals(type)) {
                return c.getValue();
            }
        }
        return null;
    }

    // Comprobar si tiene un claim concreto
    public static boolean hasClaim(List<Claim> claims, String type, String value) {
        for (Claim c : claims) {
            if (c.getType().equals(type) && c.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
}