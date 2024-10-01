package com.pay10.pg.core.sbicard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Base64;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncryptedRequestData {
    private String signedEncRequestPayload;
    private String requestSymmetricEncKey;
    private String iv;

    public static EncryptedRequestData buildRequest(String signedEncPayload,
                                                    String symmetricEncKey,
                                                    AES aes){
        return  EncryptedRequestData.builder()
                .signedEncRequestPayload(signedEncPayload)
                .requestSymmetricEncKey(symmetricEncKey)
                .iv(Base64.getEncoder().encodeToString(aes.getIV()))
                .build();
    }
}

