package com.ubb.bachelor.blebackgroundscan.domain.exception;

public class InvalidHexadecimalCharacter extends Exception {
    public InvalidHexadecimalCharacter(String errorMessage) {
        super(errorMessage);
    }
}
