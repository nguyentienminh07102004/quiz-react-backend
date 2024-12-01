package com.ptitb22dccn539.quiz.Security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.Entity.RoleEntity;
import com.ptitb22dccn539.quiz.Model.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JWTGenerator {
    @Value(value = "${jwt.SIGNER_KEY}")
    private String SIGNER_KEY;

    public Pair<String, String> generatorToken(UserEntity user) throws JOSEException {
        String roles = user.getRoles().stream()
                .map(RoleEntity::getCode)
                .collect(Collectors.joining(" "));
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String jti = UUID.randomUUID().toString();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000))
                .notBeforeTime(new Date())
                .jwtID(jti)
                .claim("scope", roles) // mac dinh phai la scope
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        return Pair.of(jwsObject.serialize(), jti);
    }

    public JWTClaimsSet getClaimSet(String token) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        if(!signedJWT.verify(verifier)) {
            // verify token
            throw new DataInvalidException("Token is invalid");
        }
        return signedJWT.getJWTClaimsSet();
    }
}