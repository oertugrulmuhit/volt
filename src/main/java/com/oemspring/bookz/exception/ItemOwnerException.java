package com.oemspring.bookz.exception;

public class ItemOwnerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ItemOwnerException(String msg) {
        super(msg);
    }
}
