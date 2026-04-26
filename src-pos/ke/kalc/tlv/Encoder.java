/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2022 KALC & previous Openbravo POS related works   
**
**    https://www.KALC.co.uk
**   
**
 */
package ke.kalc.tlv;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author John Lewis
 */
public class Encoder {

    private static String[] tags;
    private static ByteBuffer bbuf;
    private static BitMatrix byteMatrix;
    private static BufferedImage image;

    public Encoder() {

    }

    public static String getTLVString(Object[] array) {
        return toBase64String(encode(array));
    }

    public static String[] getTLVArray(String baseString) {
        return decode(baseString);
    }

    public static BufferedImage getTLVQRCode(Object[] array, Integer bcWidth) {
        if (bcWidth < 100) {
            bcWidth = 100;
        }
        return getQRCode(toBase64String(encode(array)), bcWidth);
    }

    private static byte[] encode(Object[] array) {
        int element = 1;

        byte[] arr = new byte[0];

        for (Object object : array) {
            // if the array element is empty return null
            if (object == null || object.toString().isEmpty()) {
                return null;
            }

            String str = object.toString();
            encode(element, str);

            byte[] tagData = new byte[bbuf.remaining()];
            bbuf.get(tagData);

            int size = arr.length;
            byte[] val = str.getBytes(Charset.forName("UTF-8"));
            arr = resizeArray(arr, (byte) val.length + 2);
            System.arraycopy(tagData, 0, arr, size, tagData.length);
            element++;
        }
        return arr;
    }

    private static String[] decode(String baseString) {
        byte[] decodedString;
        try {
            decodedString = Base64.getDecoder().decode(baseString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
        parseBase64(decodedString);
        return tags;
    }

    private static String parseBase64(byte[] decodedString) {
        int element = 0;
        tags = new String[0];
        while (decodedString.length > 2) {
            int valSize = decodedString[1];
            tags = resizeArray(tags, 1);
            decodedString = ArrayUtils.remove(decodedString, 0);
            decodedString = ArrayUtils.remove(decodedString, 0);
            byte[] slice = Arrays.copyOfRange(decodedString, 0, valSize);
            tags[element] = new String(slice, StandardCharsets.UTF_8);
            decodedString = removeArrayElements(decodedString, valSize);
            element++;
        }
        return null;
    }

    private static byte[] removeArrayElements(byte[] array, Integer index) {
        if (index > array.length) {
            return array;
        }

        for (int j = 0; j < index; j++) {
            array = ArrayUtils.remove(array, 0);
        }

        return array;
    }

    private static byte[] resizeArray(byte[] array, Integer size) {
        byte[] tempArray = new byte[array.length + size];
        System.arraycopy(array, 0, tempArray, 0, array.length);
        return tempArray;
    }

    private static String[] resizeArray(String[] array, Integer size) {
        String[] tempArray = new String[array.length + size];
        System.arraycopy(array, 0, tempArray, 0, array.length);
        return tempArray;
    }

    private static ByteBuffer encode(Integer tagId, String str) {
        byte tag = (byte) tagId.byteValue();
        byte len = (byte) str.length();
        byte[] val = str.getBytes(Charset.forName("UTF-8"));

        bbuf = ByteBuffer.allocate((byte) val.length + 2);

        bbuf.put(0, tag); // position=0
        bbuf.put(1, (byte) val.length); // position=1

        int pos = 2; // position=2 val start
        for (byte b : val) {
            bbuf.put(pos, b);
            pos++;
        }
        return bbuf;
    }

    private static String toBase64String(byte[] buff) {
        byte[] encoded = Base64.getEncoder().encode(buff);
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private static BufferedImage getQRCode(String codeText, Integer size) {
        try {
            HashMap<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            byteMatrix = qrCodeWriter.encode(codeText, BarcodeFormat.QR_CODE, size, size, hintMap);
            int imageWidth = byteMatrix.getWidth();
            image = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imageWidth, imageWidth);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            return image;
        } catch (WriterException e) {
            return null;
        }
    }

}
