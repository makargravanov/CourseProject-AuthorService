package com.example.authorservice.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DefaultTokenService{
    private final AuthorServiceDB authorServiceDB;

    @Value("${auth.jwt.secret}")
    private String secretKey;

    public DefaultTokenService(AuthorServiceDB authorServiceDB) {
        this.authorServiceDB = authorServiceDB;
    }

    /*@Async
    public CompletableFuture<Boolean> checkTokenToAuthorPermission(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            if (!decodedJWT.getIssuer().equals("userService")) {
                System.out.println("Issuer is incorrect");
                return CompletableFuture.completedFuture(false);
            }
            if (authorServiceDB.findAuthorByUserIdDB(Long.valueOf(decodedJWT.getSubject())).get().isEmpty()) {
                System.out.println("Id is incorrect, user is not author");
                return CompletableFuture.completedFuture(false);
            }
        } catch (JWTVerificationException | ExecutionException | InterruptedException e) {
            System.out.println("Token is invalid: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.completedFuture(true);
    }*/

    public Boolean checkToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        System.out.println(secretKey);
        System.out.println(token);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            if (!decodedJWT.getIssuer().equals("userService")) {
                System.out.println("Issuer is incorrect");
                return false;
            }
        } catch (JWTVerificationException e) {
            System.out.println("Token is invalid: " + e.getMessage());
            return false;
        }

        return true;
    }

    public Long getIdFromToken(String checkedToken) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(checkedToken);
        return Long.valueOf(decodedJWT.getSubject());
    }

}