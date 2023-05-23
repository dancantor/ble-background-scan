package com.ubb.bachelor.blebackgroundscan.domain.mapper;

import com.ubb.bachelor.blebackgroundscan.domain.exception.InvalidHexadecimalCharacter;

public class ByteMapper {
    public static String byteArrayToString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder(byteArray.length);
        for (byte b : byteArray) {
            stringBuilder.append(String.format("%02X", b));
        }
        return stringBuilder.toString();
    }

    public static byte[] stringToByteArray(String stringValue) throws InvalidHexadecimalCharacter {
        if (stringValue.equals("")) {
            return new byte[0];
        }
        var hexValues = stringValue.split(" ");
        var bytes = new byte[hexValues.length];
        for (int i = 0; i < hexValues.length; ++i) {
            bytes[i] = hexToByte(hexValues[i]);
        }
        return bytes;
    }

    private static byte hexToByte(String hexString) throws InvalidHexadecimalCharacter {
        Integer firstDigit = toDigit(hexString.charAt(0));
        Integer secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static Integer toDigit(char hexChar) throws InvalidHexadecimalCharacter {
        var digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new InvalidHexadecimalCharacter("Invalid hexadecimal character: " + hexChar);
        }
        return digit;

    }
}
