package com.petvission.auth.service;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class TotpService {

    private final DefaultSecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(
            new DefaultCodeGenerator(), new SystemTimeProvider()
    );

    public String generarSecret() {
        return secretGenerator.generate();
    }

    // Devuelve la imagen QR como data URL base64 para mostrar en el frontend
    public String generarQrDataUrl(String correo, String secret) {
        QrData data = new QrData.Builder()
                .label(correo)
                .secret(secret)
                .issuer("PetVission")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            byte[] qrBytes = new ZxingPngQrGenerator().generate(data);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrBytes);
        } catch (QrGenerationException e) {
            log.error("Error generando QR TOTP", e);
            throw new IllegalStateException("No se pudo generar el código QR");
        }
    }

    public boolean verificarCodigo(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}
