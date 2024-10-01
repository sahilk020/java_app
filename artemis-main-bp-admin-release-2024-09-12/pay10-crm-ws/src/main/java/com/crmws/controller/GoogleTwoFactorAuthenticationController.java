package com.crmws.controller;

import com.crmws.dto.QRCode2FADTO;
import com.crmws.entity.CredentialRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Slf4j

@RestController

@RequiredArgsConstructor

@RequestMapping("/google2FA")

public class GoogleTwoFactorAuthenticationController {

	private final GoogleAuthenticator gAuth;

	@SneakyThrows
	@GetMapping("/generateServlet/{username}")

	public void generate(@PathVariable String username, HttpServletResponse response) {
		
		try {
			CredentialRepository credentialRepository = new CredentialRepository();
			final GoogleAuthenticatorKey key = gAuth.createCredentials(username);
			System.out.println("KEY " + key.getKey());
			//credentialRepository.saveUserCredentials(username, key.getKey(), 0, null);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("BP-GATE-LOGIN", username, key);
			BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
	//Simple writing to outputstream
			ServletOutputStream outputStream = response.getOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
			outputStream.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	@SneakyThrows
	@GetMapping("/generate/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public QRCode2FADTO generateBase64QR(@PathVariable String username, HttpServletResponse response) {
		try {
			CredentialRepository credentialRepository = new CredentialRepository();
			final GoogleAuthenticatorKey key = gAuth.createCredentials(username);
			System.out.println("KEY " + key.getKey());
			//credentialRepository.saveUserCredentials(username, key.getKey(), 0, null);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("BP-GATE-LOGIN", username, key);
			BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 300, 300);
			//Simple writing to outputstream
//			ServletOutputStream outputStream = response.getOutputStream();
//			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
//			

			String base64String =convertBitMatrixToBase64(bitMatrix);
			System.out.println(base64String);
//			outputStream.close();
			
			
			return	new QRCode2FADTO(true, "Base64 image generated successfully", base64String);
//			returnreturn new Gson().toJson(jo).toString();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}
	
	private static String convertBitMatrixToBase64(BitMatrix bitMatrix) throws IOException {
        // Convert BitMatrix to a binary byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Encode the byte array as Base64
        return Base64.getEncoder().encodeToString(byteArray);
    }
	@PostMapping("/validate/key/{username}/{code}")

	public String validateKey(@PathVariable String username, @PathVariable int code) {
		try {
			String response = gAuth.getCredentialRepository().getSecretKey(username);
			if (response != null
					&& (!response.equalsIgnoreCase("User not registered on google authenticator please register first")
							&& !response.equalsIgnoreCase("User Not Found."))) {
				if (gAuth.authorizeUser(username, code)) {
					return "Authenticated";
				} else
					return "Invalid/Expired Google Authentication Code!";
			} else
				return response;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "Something went wrong!";
		}

	}

}