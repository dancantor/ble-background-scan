package com.ubb.bachelor.blebackgroundscan.domain.exception;

public class ScanRecordRepositoryNotInitialized extends Exception {
    public ScanRecordRepositoryNotInitialized(String errorMessage) {
        super(errorMessage);
    }
}