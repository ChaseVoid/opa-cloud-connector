package com.ajay.opa.cloud.utils;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;

/**
 * Created by asolleti on 10/18/2015.
 */
public final class Base64 {
    private static char[] map6BitToChars = new char[64];
    private static byte[] mapCharsTo6Bit = new byte[128];
    private static MessageFormat BAD_CHAR_MESSAGE = new MessageFormat("Invalid Base64 data - encountered \'{1}\' which cannot be translated");

    private Base64() {
    }

    public static char[] encode(byte[] data) {
        return encode(data, 0, data.length);
    }

    public static char[] encode(byte[] data, int start, int length) {
        int outputSize = (length + 2) / 3 * 4;
        char[] encodedData = new char[outputSize];
        int inPos = start;

        int outPos;
        for(outPos = 0; inPos + 3 <= start + length; inPos += 3) {
            encodedData[outPos++] = map6BitToChars[63 & data[inPos] >> 2];
            encodedData[outPos++] = map6BitToChars[48 & data[inPos] << 4 | 15 & data[inPos + 1] >> 4];
            encodedData[outPos++] = map6BitToChars[60 & data[inPos + 1] << 2 | 3 & data[inPos + 2] >> 6];
            encodedData[outPos++] = map6BitToChars[63 & data[inPos + 2]];
        }

        int remainingLength = start + length - inPos;
        if(remainingLength > 0) {
            encodedData[outPos++] = map6BitToChars[63 & data[inPos] >> 2];
            if(remainingLength == 1) {
                encodedData[outPos++] = map6BitToChars[48 & data[inPos] << 4];
                encodedData[outPos++] = 61;
            } else {
                encodedData[outPos++] = map6BitToChars[48 & data[inPos] << 4 | 15 & data[inPos + 1] >> 4];
                encodedData[outPos++] = map6BitToChars[60 & data[inPos + 1] << 2];
            }

            encodedData[outPos++] = 61;
        }

        return encodedData;
    }

    public static byte[] decode(char[] encodedData) throws InvalidBase64DataException {
        return decode(encodedData, 0, encodedData.length);
    }

    public static byte[] decode(char[] encodedData, int start, int length) throws InvalidBase64DataException {
        ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
        int bufferBits = 0;
        int bufferBitLength = 0;
        int pos = start;

        while(true) {
            if(pos < start + length) {
                char charCode = encodedData[pos++];
                if(charCode == 10 || charCode == 13 || charCode == 32 || charCode == 9) {
                    continue;
                }

                if((char)charCode != 61) {
                    if(charCode >= 0 && charCode <= 127) {
                        byte code = mapCharsTo6Bit[charCode];
                        if(code == -1) {
                            throw new InvalidBase64DataException(BAD_CHAR_MESSAGE.format(new Object[]{new Character((char)charCode)}));
                        }

                        bufferBits = bufferBits << 6 | code;
                        bufferBitLength += 6;
                        if(bufferBitLength >= 8) {
                            byte data = (byte)(bufferBits >> bufferBitLength - 8);
                            bufferBitLength -= 8;
                            decodedStream.write(data);
                        }
                        continue;
                    }

                    throw new InvalidBase64DataException(BAD_CHAR_MESSAGE.format(new Object[]{new Character((char)charCode)}));
                }
            }

            return decodedStream.toByteArray();
        }
    }

    static {
        int i = 0;

        char charValue;
        for(charValue = 65; charValue <= 90; map6BitToChars[i++] = charValue++) {
            ;
        }

        for(charValue = 97; charValue <= 122; map6BitToChars[i++] = charValue++) {
            ;
        }

        for(charValue = 48; charValue <= 57; map6BitToChars[i++] = charValue++) {
            ;
        }

        map6BitToChars[i++] = 43;
        map6BitToChars[i++] = 47;

        for(i = 0; i < 128; ++i) {
            mapCharsTo6Bit[i] = -1;
        }

        for(i = 0; i < 64; ++i) {
            charValue = map6BitToChars[i];
            mapCharsTo6Bit[charValue] = (byte)i;
        }

    }
}
