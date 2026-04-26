/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
*/


package ke.kalc.pos.util;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;

/**
 *
 *   
 */
public class Base64Encoder {
    
    /**
     *
     * @param base64
     * @return
     */
    public static byte[] decode(String base64) {

        try {
            return Base64.decodeBase64(base64.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     *
     * @param raw
     * @return
     */
    public static String encode(byte[] raw) {
        try {
            return new String(Base64.encodeBase64(raw), "ASCII");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    /**
     *
     * @param raw
     * @return
     */
    public static String encodeChunked(byte[] raw) {
        try {
            return new String(Base64.encodeBase64Chunked(raw), "ASCII");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}