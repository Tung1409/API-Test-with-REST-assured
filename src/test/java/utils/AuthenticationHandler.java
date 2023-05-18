package utils;

import org.apache.commons.codec.binary.Base64;

public class AuthenticationHandler {

    public static String encodeCredStr(String email, String token){
        if (email == null || token == null){
            throw new IllegalArgumentException("[ERR] email or token can't be null");
        }
        String cred = email.concat(":").concat(token);
        // Base64 tu apache.common.codec
        byte[] encodedCred = Base64.encodeBase64(cred.getBytes());
        return new String(encodedCred);
    }
}
