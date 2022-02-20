package de.disk0.ldap.api.totp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class TotpUtil {

	private static Log log = LogFactory.getLog(TotpUtil.class);
	
	public static OTP createTOTP(String subject, String issuer) throws IOException {
		try {
			OTP totp = new OTP();
			totp.setIssuer(
					String.format(
							"TOTP%1$s%2$s",
							new SimpleDateFormat("yyMMdd").format(new Date()), 
							TokenGenerator.generateToken(5).toUpperCase()
							)
					);
			totp.setSecret(TotpUtil.generateSecret());
			totp.setName(subject+"@"+issuer);
			totp.setIssuer(issuer);
			return totp;		
		} catch (Exception e) {
			throw new IOException("unable to create OTP token",e);
		}
	}
	
	
	public static String toString(byte[] bytes) {
		Base32 base32 = new Base32();
		String secretKey = base32.encodeToString(bytes);
		return secretKey;//.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
	}

	public static byte[] toBytes(String secret) {
		secret = secret.replaceAll(" ","");
		Base32 base32 = new Base32();
		byte[] bytes = base32.decode(secret);
		return bytes;
	}
	
	public static String generateSecret() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		Base32 base32 = new Base32();
		String secretKey = base32.encodeToString(bytes);
		//return secretKey.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
		return secretKey;
	}

	public static void createQRCode(OutputStream os, String barCodeData, int height, int width) throws IOException, WriterException {
		Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 0);
		BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height, hints);
		MatrixToImageWriter.writeToStream(matrix, "png", os);
	}

	public static byte[] createQRCode(String barCodeData, int height, int width) throws IOException, WriterException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		createQRCode(baos, barCodeData, height, width);
		baos.flush();
		return baos.toByteArray();
	}
	
	public static String createQRCodeBase64(String barCodeData, int height, int width) throws IOException, WriterException {
		return Base64.getEncoder().encodeToString(createQRCode(barCodeData, height, width));
	}
	
	public static String generateSecretUrl(String secretKey, String hash, int digits, int period, String name, String issuer) {
		String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
		try {
			return String.format(
					"otpauth://totp/%s:%s?secret=%s&digits=%s&algorithm=%s&period=%s",
					URLEncoder.encode(issuer.replaceAll(" ","_"), "UTF-8"),
					URLEncoder.encode(name, "UTF-8"),
					normalizedBase32Key,
					digits,
					hash,
					period);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
      * This method uses the JCE to provide the crypto algorithm.
      * HMAC computes a Hashed Message Authentication Code with the
      * crypto hash algorithm as a parameter.
      *
      * @param crypto: the crypto algorithm (HmacSHA1, HmacSHA256,
      *                             HmacSHA512)
      * @param keyBytes: the bytes to use for the HMAC key
      * @param text: the message or text to be authenticated
      */

     private static byte[] hmac_sha(String crypto, byte[] keyBytes,
             byte[] text){
         try {
             Mac hmac;
             hmac = Mac.getInstance(crypto);
             SecretKeySpec macKey =
                 new SecretKeySpec(keyBytes, "RAW");
             hmac.init(macKey);
             return hmac.doFinal(text);
         } catch (GeneralSecurityException gse) {
             throw new UndeclaredThrowableException(gse);
         }
     }


     /**
      * This method converts a HEX string to Byte[]
      *
      * @param hex: the HEX string
      *
      * @return: a byte array
      */

     private static byte[] hexStr2Bytes(String hex){
         // Adding one byte to get the right conversion
         // Values starting with "0" can be converted
         byte[] bArray = new BigInteger("10" + hex,16).toByteArray();

         // Copy all the REAL bytes, not the "first"
         byte[] ret = new byte[bArray.length - 1];
         for (int i = 0; i < ret.length; i++)
             ret[i] = bArray[i+1];
         return ret;
     }

     private static final int[] DIGITS_POWER
     // 0 1  2   3    4     5      6       7        8
     = {1,10,100,1000,10000,100000,1000000,10000000,100000000 };

     /**
      * This method generates a TOTP value for the given
      * set of parameters.
      *
      * @param key: the shared secret, HEX encoded
      * @param time: a value that reflects a time
      * @param returnDigits: number of digits to return
      *
      * @return: a numeric String in base 10 that includes
      *              {@link truncationDigits} digits
      */

     public static String generateTOTP(String key,
             String time,
             String returnDigits){
         return generateTOTP(key, time, returnDigits, "HmacSHA1");
     }


     /**
      * This method generates a TOTP value for the given
      * set of parameters.
      *
      * @param key: the shared secret, HEX encoded
      * @param time: a value that reflects a time
      * @param returnDigits: number of digits to return
      *
      * @return: a numeric String in base 10 that includes
      *              {@link truncationDigits} digits
      */

     public static String generateTOTP256(String key,
             String time,
             String returnDigits){
         return generateTOTP(key, time, returnDigits, "HmacSHA256");
     }

     /**
      * This method generates a TOTP value for the given
      * set of parameters.
      *
      * @param key: the shared secret, HEX encoded
      * @param time: a value that reflects a time
      * @param returnDigits: number of digits to return
      *
      * @return: a numeric String in base 10 that includes
      *              {@link truncationDigits} digits
      */

     public static String generateTOTP512(String key,
             String time,
             String returnDigits){
         return generateTOTP(key, time, returnDigits, "HmacSHA512");
     }


     /**
      * This method generates a TOTP value for the given
      * set of parameters.
      *
      * @param key: the shared secret, HEX encoded
      * @param time: a value that reflects a time
      * @param returnDigits: number of digits to return
      * @param crypto: the crypto function to use
      *
      * @return: a numeric String in base 10 that includes
      *              {@link truncationDigits} digits
      */

     public static String generateTOTP(String key,
             String time,
             String returnDigits,
             String crypto){
    	 
         int codeDigits = Integer.decode(returnDigits).intValue();
         String result = null;

         // Using the counter
         // First 8 bytes are for the movingFactor
         // Compliant with base RFC 4226 (HOTP)
         while (time.length() < 16 )
             time = "0" + time;

         // Get the HEX in a Byte[]
         byte[] msg = hexStr2Bytes(time);
         byte[] k = hexStr2Bytes(key);
         byte[] hash = hmac_sha(crypto, k, msg);

         // put selected bytes into result int
         int offset = hash[hash.length - 1] & 0xf;

         int binary =
             ((hash[offset] & 0x7f) << 24) |
             ((hash[offset + 1] & 0xff) << 16) |
             ((hash[offset + 2] & 0xff) << 8) |
             (hash[offset + 3] & 0xff);

         int otp = binary % DIGITS_POWER[codeDigits];

         result = Integer.toString(otp);
         while (result.length() < codeDigits) {
             result = "0" + result;
         }
         return result;
     }	

    public static boolean checkOld(String secretBase32, String code) throws NoSuchAlgorithmException, InvalidKeyException {
		return check(secretBase32,"SHA1",code,6,30,5);
    }

    public static boolean check(String secretBase32, String hash, String code, int length, int interval, int window) throws NoSuchAlgorithmException, InvalidKeyException {
    	try {
    	    Base32 codec = new Base32();
    		byte[] decodedKey = codec.decode(secretBase32.replaceAll(" ", "").toUpperCase());
    		return check(decodedKey, hash,code, length, interval, window);
		} catch (Exception e) {
			log.warn("error checking OTP: ",e);
			return false;
		}
	}

	public static boolean check(byte[] secret, String code) throws NoSuchAlgorithmException, InvalidKeyException {
		return check(secret,"SHA1",code,6,30,5);
	}
	
	public static boolean check(byte[] secret, String hash, String code, int length, int interval, int window) throws NoSuchAlgorithmException, InvalidKeyException {
		
		try {
			getOffset(secret, hash, code, length, interval, window);
			return true;
		} catch (Exception e) {
			log.warn("TOTP CHECK --- invalid, returning false");
		}
		return false;
	}
	
	public static int getOffset(String secretBase32, String code) throws InvalidKeyException, NoSuchAlgorithmException {
		return getOffset(secretBase32,"SHA1",code,6,30,5);
	}
	
	public static int getOffset(String secretBase32, String hash, String code, int length, int interval, int window) throws NoSuchAlgorithmException, InvalidKeyException {
		Base32 codec = new Base32();
		
		byte[] decodedKey = codec.decode(secretBase32.replaceAll(" ", "").toUpperCase());
		return getOffset(decodedKey,hash,code,length,interval,window);
	}
	
	public static int getOffset(byte[] secret, String hash, String code, int length, int interval, int window) throws NoSuchAlgorithmException, InvalidKeyException {

		if(code == null || code.length() != length) {
			throw new InvalidKeyException("expected "+length+" but found "+(code==null?"[NULL]":code.length()+""));
		}
		
		long time = (System.currentTimeMillis() / 1000) / interval;

		Integer os = null;
		
		for (int i = -window; i <= window; ++i) {
			
			String hexKey = Hex.encodeHexString(secret);
		    String hexTime = Long.toHexString(time + i);

		    String actual = "";

		    if(hash.equals("SHA1")) {
		    		actual = generateTOTP(hexKey, hexTime, length+"");
		    } else if(hash.equals("SHA256")) {
		    		actual = generateTOTP256(hexKey, hexTime, length+"");
		    } else if(hash.equals("SHA512")) {
		    		actual = generateTOTP512(hexKey, hexTime, length+"");
		    } else {
		    		throw new RuntimeException("unsupported: "+hash);
		    }
		    
		    
			if (code.equals(actual)) {
				log.info("comparing OTP: "+code+" / "+actual+" offset: "+i+" <<< ");
				os = i;
			} else {
				log.info("comparing OTP: "+code+" / "+actual+" offset: "+i+" x ");
			}

		}
		if(os==null) { 
			throw new InvalidKeyException();
		}
		return os.intValue();
	}

	public static String generateTotp(String secret, String hash, int interval, int digits, Date time) {
		time = time == null?new Date():time;
		long t = (time.getTime() / 1000) / interval;
		String hexTime = Long.toHexString(t);
		
		Base32 codec = new Base32();
		byte[] sb = codec.decode(secret.replaceAll(" ", "").toUpperCase());
		String hexKey = Hex.encodeHexString(sb);
		

	    if(hash.equals("SHA1")) {
    		return generateTOTP(hexKey, hexTime, digits+"");
	    } else if(hash.equals("SHA256")) {
	    	return generateTOTP256(hexKey, hexTime, digits+"");
	    } else if(hash.equals("SHA512")) {
	    	return generateTOTP512(hexKey, hexTime, digits+"");
	    } else {
	    		throw new RuntimeException("unsupported: "+hash);
	    }

	}
	
	
}