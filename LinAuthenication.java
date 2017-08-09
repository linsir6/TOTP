package 双向认证;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by linSir
 * date at 2017/8/8.
 * describe: 封装一层算法
 */

public class LinAuthenication {

	private static long mStartTime = 0;
    private static final long mTimeStep = 30;
	
	public static String getCurrentCode(String secret) throws OtpSourceException {
        long otp_state = getValueAtTime(System.currentTimeMillis()/1000 );
        return computePin(secret, otp_state);
    }
	
	private static String computePin(String secret, long otp_state)
            throws OtpSourceException {
        if (secret == null || secret.length() == 0) {
            throw new OtpSourceException("Null or empty secret");
        }
        try {
            PasscodeGenerator.Signer signer = getSigningOracle(secret);
            PasscodeGenerator pcg = new PasscodeGenerator(signer, 6);

            return pcg.generateResponseCode(otp_state);

        } catch (GeneralSecurityException e) {
            throw new OtpSourceException("Crypto failure", e);
        }
    }


    public static long getValueAtTime(long time) {
        long timeSinceStartTime = time - mStartTime;
        if (timeSinceStartTime >= 0) {
            return timeSinceStartTime / mTimeStep;
        } else {
            return (timeSinceStartTime - (mTimeStep - 1)) / mTimeStep;
        }
    }


    static PasscodeGenerator.Signer getSigningOracle(String secret) {
        try {
            byte[] keyBytes = Base32String.decode(secret);
            final Mac mac = Mac.getInstance("HMACSHA1");
            mac.init(new SecretKeySpec(keyBytes, ""));

            return new PasscodeGenerator.Signer() {
                @Override
                public byte[] sign(byte[] data) {
                    return mac.doFinal(data);
                }
            };
        } catch (Base32String.DecodingException error) {
            //Log.e(LOCAL_TAG, error.getMessage());
        } catch (NoSuchAlgorithmException error) {
            //Log.e(LOCAL_TAG, error.getMessage());
        } catch (InvalidKeyException error) {
            //Log.e(LOCAL_TAG, error.getMessage());
        }

        return null;
    }

    private static final String LOCAL_TAG = "AccountDb";


    public static class OtpSourceException extends Exception{

        public OtpSourceException(String message) {
            super(message);
        }

        public OtpSourceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
	
	
}
