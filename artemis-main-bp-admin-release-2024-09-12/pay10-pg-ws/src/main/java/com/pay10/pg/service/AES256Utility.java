package com.pay10.pg.service;


import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;



import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AES256Utility {
	
	private static Logger log = LoggerFactory.getLogger(AES256Utility.class);
	 	private String ips;
	    private Key keySpec;
	    //public static String sKey="jpuT6032jpuT6032";
	  
	    public AES256Utility() {
	        try {
	        	byte[] keyBytes = new byte[32];
	            byte[] b = "bf2ec373865b7f2b164a6af12f35bc1f".getBytes();//"a5e8d2e9c1721ae0e84ad660c472c1f3".getBytes("UTF-8");//Utils.getPropertyValue("irctcKey").getBytes("UTF-8");
	            System.arraycopy(b, 0, keyBytes, 0, keyBytes.length);
	            log.info("keyBytes >>> "+new String(keyBytes));
	            keySpec = new SecretKeySpec(keyBytes, "AES");
	        	this.ips = "bf2ec373865b7f2b164a6af12f35bc1f".substring(0, 16);//Utils.getPropertyValue("irctcKey").substring(0, 16);
	            log.info("ips >>> "+new String(ips));
	            
	        } catch (Exception e) {
	        }
	    }


	    public String encrypt(String str) {
	        Cipher cipher;
	        try {
	            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            cipher.init(Cipher.ENCRYPT_MODE, keySpec,
	                    new IvParameterSpec(ips.getBytes("UTF-8")));

	            byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));
	            String Str = Utils.byteToHex(encrypted);//new String(Base64.encodeBase64(encrypted));
	            return Str;
	        } catch (Exception e) {
	        	
	        	e.printStackTrace();
	        }
	        return null;
	    }

	    public String decrypt(String str) {
	        Cipher cipher;
	        try {
	            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	            cipher.init(Cipher.DECRYPT_MODE, keySpec,
	                    new IvParameterSpec(ips.getBytes("UTF-8")));
	            
	            byte[] byteStr = Utils.hex2ByteArray(str);//Base64.decodeBase64(str.getBytes());
	            String Str = new String(cipher.doFinal(byteStr), "UTF-8");
	            
	            return Str;
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        return null;
	    }
	
	    
	    public static void main(String[] args) {
			
	    	String skey ="BF2EC373865B7F2B164A6AF12F35B".toLowerCase();
	    	System.out.println(skey);
	    	/*try {
				Set<String> algorithms = Security.getAlgorithms("Cipher");
				for(String algorithm: algorithms) {
				    int max;
					max = Cipher.getMaxAllowedKeyLength(algorithm);
				    System.out.printf("%-22s: %dbit%n", algorithm, max);
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}*/
	    
	    	//String str="6F36D87EC8E61F8AF6E5AC1586D78E9857886C794E470D9AC373CA7D3CE130B04B11B44645A3883BC5081FA86E4EB052C09B03960FA0D7555BA253D8490A8A1418779A003E9C56E89D88DB78AB6C8905B681DD4650A28B0F7783919D92706A13A21D69ECF93C3AAA2CA09DC6A55D83B34C548A3E54FE3DE885D1D6890E814475EE892A5AC117F7E44FEE030532D6511B9B64C792E4266D8B032A0539C9B4E85B1E2F281E6287B6F7F215CCB45473204E6B483199485C1247C34BDE8BCFE57EEAF118273F2BC8ACB45816865DCED39E50629FAEFBF35D92023556CED6DC4C1231B5A80353FDCFC35979D97FAC6BB85833E48CD903B9D85128CA222A18E4246DDC2B6745195505A1DB24CFA2BEB933C69D5C91AD4145D2385B1C1C2F18E4C6C072";
	    	
	    	AES256Utility utility = new AES256Utility();
			//String str = utility.encrypt("merchantCode=M0002|reservationId=040620191530200|txnAmount=1.23|currencyType=INR|appCode=ET|pymtMode=Mobile|txnDate=20190604|securityId=CRIS|RU=http://localhost:7001/pgTest/HdfcMobMppOnCrisApi/returnUrl.jsp|checkSum=AD049C73E090F5AA32C8BA3EFEDD2A24F65B8FE6FDE20918C7A121CC438BC165");
	    	
	    	String str="merchantCode=1009120911030610|reservationId=1644920623232750|bankTxnId=IG01439494|txnAmount=2000.00";
	    	String checkSum=Utils.getSHA(str);
	    	log.info("Checksum >>> "+checkSum.toUpperCase());
	    	
	    	str=str+"|checkSum="+checkSum.toUpperCase();
	    	//log.info(str);
	    	str = utility.encrypt(str);
			//str = utility.decrypt(str);
			log.info("enc >>> "+str);
			 String dec= utility.decrypt(str);
			 log.info("dec >>> "+dec);
			
		}
}
