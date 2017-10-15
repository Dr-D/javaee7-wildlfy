package com.drd.ticketagency.boundary;

/**
 * @author darrenp - 2017-10-06.
 */
public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
