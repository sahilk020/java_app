package com.pay10;


import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("Inside AES256Test...Start");
        AES256 aes=new AES256();
        String encKey="AXBHRBANKING2010"; //AXBHRBANKING2010
        String salt="98767645342653891765293640896514";
        String iv="98767645342653891765293640896514";
        String painString="PRN~91313114111$PID~000006756195$MD~P$ITC~1188111111$CRN~INR$AMT~12.00$RESPONSE~AUTO$CG~Y$checksum~0e5f3a26ba560cd285e1f7cd5c5d7b3b50cc12cb4d879785f5c10cc2fc223989";


        System.out.println("encKey>>>>>>>>>>>>>>>>"+encKey);
        System.out.println("BILLPAY_SALT>>>>>>>>>>>>>>>>"+salt);
        System.out.println("BILLPAY_IV>>>>>>>>>>>>>>>>"+iv);

        String encValue=aes.encrypt(salt, iv, encKey, painString);
        String decValue=aes.decrypt(salt, iv, encKey,"wG6dbu+RCON00Soi32+QF+4RQbhSQEkVXxaWd8iqcKYVFLXgQM9dOeFHLVNBCSmZSXu/aWX4fUHlWb2C9rpfgDpyWXlGWS1wmotY91215dGm/l90ElMYTs9HjJltekImU+80i68YmSxAyzh4Zze388OgBJWKKd2ul5s/hVxon0wkjhJAdGfOB/eh+eQKq2Ai12neEe5OK/KvgFpRra3yeeXA5Fv/kIMzcsrd1L+hbbo=");

     //   String decValue = aes.decrypt()
        System.out.println("Enc Values>>>>>>>"+encValue);
        System.out.println("Dec Values>>>>>>>>"+decValue);



        System.out.println("Inside AES256Test...End");

    }


}

class AES256
{
    public String encrypt(String salt, String iv, String encKey, String painString)
    {
        int keySize;
        int iterationCount;
        Cipher cipher=null;
        byte[] encrypted=null;
        String encVal="";

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey key = generateKey1(salt, encKey);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(hex(iv)));
            encrypted=cipher.doFinal(painString.getBytes("UTF-8"));
            encVal=new String(Base64.encodeBase64(encrypted));
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }

        return encVal;

    }
    private SecretKey generateKey1(String salt, String passphrase)
    {
        SecretKey key=null;
        try
        {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), 10, 256);
            key= new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return key;

    }
    public static byte[] hex(String str)
    {
        return DatatypeConverter.parseHexBinary(str);
    }
    public String decrypt(String salt, String iv, String encKey,String cipherValue)
    {
        int keySize;
        int iterationCount;
        Cipher cipher=null;
        byte[] devalue=null;
        String descryptedString="";
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey key = generateKey1(salt, encKey);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(hex(iv)));
            devalue=cipher.doFinal(DatatypeConverter.parseBase64Binary(cipherValue));
            descryptedString=new String(devalue);
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return descryptedString;
    }
}
