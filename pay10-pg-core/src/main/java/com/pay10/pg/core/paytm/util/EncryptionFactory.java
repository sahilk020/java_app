package com.pay10.pg.core.paytm.util;

public class EncryptionFactory {
   private EncryptionFactory() {
   }

   public static Encryption getEncryptionInstance(String algorithmType) {
      return new AesEncryption();
   }

}
