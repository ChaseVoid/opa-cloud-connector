package com.ajay.opa.cloud.utils;

import com.ajay.opa.cloud.endpoint.ServiceProperties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by asolleti on 10/18/2015.
 */
public class ServicePropertiesEncrypter {

    private String propertiesFilePath;
    private File propertiesFile;
    private static final String saltPrefix = "48bgh29dgjt6jsvksaerjdwg7m1C";
    private static final String ALGORITHM = "AES";
    private static final byte[] keyBytes = {0, 40, 18, 86, 7, 41, 21, 51, 55, 82, 101, 54, 17, 33, 85, 114};

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected path of service configuration file.");
            System.exit(1);
        }

        ServicePropertiesEncrypter encrypter = new ServicePropertiesEncrypter(args[0]);
        try {
            encrypter.replacePropertiesFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Successfully encrypted values and overwrote configuration file '" + args[0] + "'.");
        System.exit(0);
    }

    public ServicePropertiesEncrypter(String path) {
        this.propertiesFilePath = path;
        this.propertiesFile = new File(this.propertiesFilePath);
        if (!this.propertiesFile.exists())
            throw new IllegalArgumentException("Provided file '" + this.propertiesFilePath + "' does not exist.");
    }

    public void replacePropertiesFile()
            throws IOException, IllegalBlockSizeException, IllegalArgumentException {

        Properties properties = ServiceProperties.getProperties(this.propertiesFilePath);

        String serviceUsernameProperty = properties.getProperty(ServiceProperties.SERVICE_USERNAME, "");
        String servicePasswordProperty = properties.getProperty(ServiceProperties.SERVICE_PASSWORD, "");

        String sfdcUsernameProperty = properties.getProperty(ServiceProperties.SFDC_USERNAME, "");
        String sfdcPasswordProperty = properties.getProperty(ServiceProperties.SFDC_PASSWORD, "");

        if ("".equals(serviceUsernameProperty)) {
            throw new IllegalArgumentException("Configuration file '" + this.propertiesFilePath + "' does not contain '" + ServiceProperties.SERVICE_USERNAME + "' property");
        }
        if ("".equals(servicePasswordProperty)) {
            throw new IllegalArgumentException("Configuration file '" + this.propertiesFilePath + "' does not contain '" + ServiceProperties.SERVICE_PASSWORD + "' property");
        }

        if ("".equals(sfdcUsernameProperty)) {
            throw new IllegalArgumentException("Configuration file '" + this.propertiesFilePath + "' does not contain '" + ServiceProperties.SFDC_USERNAME + "' property");
        }
        if ("".equals(sfdcPasswordProperty)) {
            throw new IllegalArgumentException("Configuration file '" + this.propertiesFilePath + "' does not contain '" + ServiceProperties.SFDC_PASSWORD + "' property");
        }

        boolean serviceUsernameEncrypted = isEncrypted(serviceUsernameProperty);
        boolean servicePasswordEncrypted = isEncrypted(servicePasswordProperty);

        boolean sfdcUsernameEncrypted = isEncrypted(sfdcUsernameProperty);
        boolean sfdcPasswordEncrypted = isEncrypted(sfdcPasswordProperty);

        if (((serviceUsernameEncrypted) && (!servicePasswordEncrypted))
                || ((!serviceUsernameEncrypted) && (servicePasswordEncrypted))
                || ((sfdcUsernameEncrypted) && (!sfdcPasswordEncrypted))
                || ((!sfdcUsernameEncrypted) && (sfdcPasswordEncrypted))) {
            throw new RuntimeException("Either username is encrypted and password is not encrypted or vice versa. Erase and replace all authentication credentials in configuration file.");
        }

        if ((!serviceUsernameEncrypted)
                && (!servicePasswordEncrypted)
                && (!sfdcPasswordEncrypted)
                && (!sfdcPasswordEncrypted)) {
            rewritePropertiesFile(properties);
        }
    }

    private static Key generateKey() {
        Key key = new SecretKeySpec(keyBytes, ALGORITHM);
        return key;
    }

    public static String decrypt(String encryptedString)
            throws IllegalBlockSizeException, InvalidBase64DataException {
        return decrypt(encryptedString, true);
    }

    private static String decrypt(String encryptedString, boolean removeSalt)
            throws IllegalBlockSizeException, InvalidBase64DataException {
        if (Base64.decode(encryptedString.toCharArray()) == null) {
            throw new IllegalArgumentException("String '" + encryptedString + "' is not an encrypted string.");
        }

        try {
            Key key = generateKey();
            Cipher checkCipher = Cipher.getInstance(ALGORITHM);

            checkCipher.init(2, key);

            byte[] encrypted = Base64.decode(encryptedString.toCharArray());
            byte[] decrypted = checkCipher.doFinal(encrypted);
            String decryptedString = new String(decrypted, "utf-8");

            if (removeSalt) {
                return decryptedString.substring(saltPrefix.length());
            }
            return decryptedString;
        } catch (NoSuchAlgorithmException
                | InvalidKeyException
                | UnsupportedEncodingException
                | BadPaddingException
                | NoSuchPaddingException e) {
            throw new RuntimeException("Could not initialise cipher to decrypt string.", e);
        }
    }

    public static String encrypt(String toEncrypt)
            throws IllegalBlockSizeException {
        try {
            Key key = generateKey();
            Cipher checkCipher = Cipher.getInstance(ALGORITHM);

            checkCipher.init(1, key);

            String saltedString = saltPrefix + toEncrypt;
            byte[] encrypted = checkCipher.doFinal(saltedString.getBytes("utf-8"));

            char[] encodedChars = Base64.encode(encrypted);
            return new String(encodedChars);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | BadPaddingException
                | UnsupportedEncodingException
                | InvalidKeyException e) {
            throw new RuntimeException("Could not initialise cipher to encrypt string.", e);
        }
    }

    private String getLineBreak() {
        return System.getProperty("line.separator");
    }

    private void rewritePropertiesFile(Properties properties)
            throws IOException, IllegalBlockSizeException {
        String fileString = "";
        Enumeration<Object> keys = properties.keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String keyString = (String) key;
            String propertyString;

            switch (keyString) {
                case ServiceProperties.SERVICE_USERNAME:
                    propertyString = ServiceProperties.SERVICE_USERNAME + "=" + encrypt(properties.getProperty(ServiceProperties.SERVICE_USERNAME, "")) + getLineBreak();
                    break;
                case ServiceProperties.SERVICE_PASSWORD:
                    propertyString = ServiceProperties.SERVICE_PASSWORD + "=" + encrypt(properties.getProperty(ServiceProperties.SERVICE_PASSWORD, "")) + getLineBreak();
                    break;
                case ServiceProperties.SFDC_USERNAME:
                    propertyString = ServiceProperties.SFDC_USERNAME + "=" + encrypt(properties.getProperty(ServiceProperties.SFDC_USERNAME, "")) + getLineBreak();
                    break;
                case ServiceProperties.SFDC_PASSWORD:
                    propertyString = ServiceProperties.SFDC_PASSWORD + "=" + encrypt(properties.getProperty(ServiceProperties.SFDC_PASSWORD, "")) + getLineBreak();
                    break;
                default:
                    propertyString = keyString + "=" + properties.getProperty(keyString, "") + getLineBreak();
                    break;
            }

            fileString = fileString + propertyString;
        }

        if ((!this.propertiesFile.exists()) || (!this.propertiesFile.canWrite())) {
            throw new RuntimeException("Cannot write to file '" + this.propertiesFilePath + "'");
        }

        FileOutputStream clearFile = new FileOutputStream(this.propertiesFile.getAbsolutePath());
        clearFile.write(new byte[0]);
        clearFile.close();

        FileWriter writer = new FileWriter(this.propertiesFile);
        writer.write(fileString);
        writer.flush();
        writer.close();
    }

    public static boolean isEncrypted(String possiblyEncrypted) {
        String possiblyDecrypted;
        try {
            possiblyDecrypted = decrypt(possiblyEncrypted, false);
        } catch (IllegalBlockSizeException
                | IllegalArgumentException
                | InvalidBase64DataException e) {
            return false;
        }

        return possiblyDecrypted.startsWith(saltPrefix);
    }
}
