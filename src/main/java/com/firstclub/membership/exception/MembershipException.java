package com.firstclub.membership.exception;

/**
 * Custom exception for membership service operations
 */
public class MembershipException extends RuntimeException {

    public MembershipException(String message) {
        super(message);
    }

    public MembershipException(String message, Throwable cause) {
        super(message, cause);
    }
} 