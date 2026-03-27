package com.wiseplanner.exception;

public class FileCorruptionException extends RuntimeException {
    public FileCorruptionException(String message) {
        super(message);
    }
}
