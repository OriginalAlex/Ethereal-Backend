package io.github.originalalex.ethereal.tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

public class TokenHandling {

    private static final String SECRET = "SDUFRTGHJ234UI iauyoeryt  dfsg   3u4 ytgderfgeqwtsfdsg";
    private static final long validDuration = 8*60*60*1000; // 8 hours

    public static String generateToken(Map<String, Object> header, Map<String, String> payload) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTCreator.Builder builder = JWT.create()
                    .withHeader(header)
                    .withExpiresAt(new Date(System.currentTimeMillis() + validDuration));
            for (Map.Entry<String, String> entry : payload.entrySet()) {
                builder.withClaim(entry.getKey(), entry.getValue());
            }
            String token = builder.sign(algorithm);
            return token;
        } catch (UnsupportedEncodingException e) {
        } catch (JWTCreationException e) {
        }
        return null;
    }

    public static DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            return verifier.verify(token);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JWTVerificationException e) {e.printStackTrace();/*the token is a lie!*/}
        return null;
    }

    public static String extrapilateName(String token) {
        return verifyToken(token).getClaims().get("name").asString();
    }

}
