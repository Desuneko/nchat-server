package heaven.nchat.server;

import java.io.*;
import java.math.BigInteger;
import java.security.*;

import javax.crypto.SecretKey;

public class Util {
	public static String getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey)
    {
        try
        {
            return (new BigInteger(digestOperation("SHA-1", new byte[][] {serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded()}))).toString(16);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Compute a message digest on arbitrary byte[] data
     */
    private static byte[] digestOperation(String algorithm, byte[] ... arguments)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[][] args = arguments;
            int count = arguments.length;

            for (int i = 0; i < count; ++i)
            {
                byte[] pArgs = args[i];
                digest.update(pArgs);
            }

            return digest.digest();
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
