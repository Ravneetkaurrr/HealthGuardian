package com.user.management.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
