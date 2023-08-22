package com.architecture.springboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


@Log4j2
@Service
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class Security {
    private final Environment environment;
    private String access_key;

    @PostConstruct
    public void initSecurity() {
        access_key = environment.getProperty("server.settings.accesskey");
    }

    public String encryptionJWT() {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        return JWT.create()
                .withExpiresAt(Time.LongTimeStamp(0, 0, 1, 0, 0, 0))
                .withClaim("signature", encryptionSHA256(access_key))
                .withIssuer("auth0")
                .sign(algorithm);
    }

    /**
     * SHA-256으로 해싱하는 메소드
     *
     * @param msg
     * @return bytes
     * @throws NoSuchAlgorithmException
     */
    public String encryptionSHA256(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(msg.getBytes());
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 바이트를 헥스값으로 변환한다.
     *
     * @param bytes
     * @return
     */
    public String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public String createToken(String access_key) {
        if (Objects.equals(access_key, this.access_key)) {
            return encryptionJWT();
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            String signature = jwt.getClaim("signature").asString();

            /** Signature & name Valid Checker*/
            if (!signature.equals(encryptionSHA256(access_key))) {
                return false;
            }

            /** Timer Limit Valid Checker*/
            return jwt.getExpiresAt().getTime() <= Time.LongTimeStamp(0, 0, 1, 0, 0, 0).getTime();
            // TODO Catch문 Exception 별로 예외처리
        } catch (NullPointerException e) {
            log.error(e);
            return false;
        } catch (JWTDecodeException e) {
            log.error(e);
            return false;
        } catch (JWTVerificationException e) {
            log.error(e);
            return false;
        } catch (JWTCreationException e) {
            log.error(e);
            return false;
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }
}
