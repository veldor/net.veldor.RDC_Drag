package net.veldor.rdc_drag.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.io.*;

public class Properties {
    public static void saveToken(String token) throws IOException {
        /*
         * First, create (or ask some other component for) the adequate encryptor for
         * decrypting the values in our .properties file.
         */
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("test"); // could be got from web, env variable...

        /*
         * Create our EncryptableProperties object and load it the usual way.
         */
        EncryptableProperties props = new EncryptableProperties(encryptor);
        props.setProperty("token", token);
        props.store(new FileWriter("myProperties"), null);
    }

    public static String getToken(){
        File propertiesFile = new File("myProperties");
        if(propertiesFile.isFile()){
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword("test");
            EncryptableProperties props = new EncryptableProperties(encryptor);
            try {
                props.load(new FileInputStream("myProperties"));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return props.getProperty("token", null);
        }
        return null;
    }
}
