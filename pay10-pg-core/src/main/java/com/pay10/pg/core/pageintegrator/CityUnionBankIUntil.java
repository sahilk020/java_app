package com.pay10.pg.core.pageintegrator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import com.pay10.commons.util.SystemConstants;

@Component
public class CityUnionBankIUntil {

	
	public static final String ALGO = "AES";

	public  String getEncryptedString(String iv, String key, String data ) {
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGO);
			IvParameterSpec ivKey = new IvParameterSpec(iv.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));
			Cipher cipher = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivKey);

			byte[] encValue = cipher.doFinal(data.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));

			
			return Hex.encodeHexString(encValue).toUpperCase();
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {

			noSuchAlgorithmException.printStackTrace();
		} catch (NoSuchPaddingException noSuchPaddingException) {

			noSuchPaddingException.printStackTrace();
		} catch (InvalidKeyException invalidKeyException) {

			invalidKeyException.printStackTrace();
		} catch (IllegalBlockSizeException illegalBlockSizeException) {

			illegalBlockSizeException.printStackTrace();
		} catch (BadPaddingException badPaddingException) {

			badPaddingException.printStackTrace();
		} catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {

			invalidAlgorithmParameterException.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			
		}

		return null;
	} 

	public  String getDeryptedPDEK( String IV,String KEK,String DEK) {

		try
		{

			IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(KEK.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(2, skeySpec, iv);

			byte[] original = cipher.doFinal(org.bouncycastle.util.encoders.Hex.decode(DEK));
			return new String(original);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;

	}
	
	
	public  String calculateChecksum( String data){
		int chksum = 0;
		for(int j=0;j<data.length();j++) {
		chksum= chksum+((int)data.charAt(j)) * (j+1); 
		}
		return ""+chksum; // Type casting as String as per return type
		}
	
	public static void main(String[] args) {
		CityUnionBankIUntil data=new CityUnionBankIUntil();
		
		String dataa="FA0B5B9D923BFF32344181A3352FFDEC";
	System.out.println(data.getDeryptedPDEK("B3F544AAAD0CD418","45C0C3C948BA2599BCEFF6888025DF2M",dataa));
	}
}
