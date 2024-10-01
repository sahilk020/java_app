package com.pay10.pg.core.sbicard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncryptedResponseData {
    private String signedEncResponsePayload;
    private String responseSymmetricEncKey;
    private String iv;
    private String statusCode;
    private String errorMessage;
    private String clientReferenceId;
    private String var1;
    private String var2;
    private String var3;
}