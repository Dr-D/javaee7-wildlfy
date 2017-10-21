package com.drd.ticketagency.boundary;

import com.drd.ticketagency.control.NoSuchSeatException;
import com.drd.ticketagency.control.SeatBookedException;

import javax.ejb.Asynchronous;
import java.util.concurrent.Future;

/**
 * @author darrenp - 2017-10-06.
 */
public interface TheatreBookerRemote {
    int getAccountBalance();

    String bookSeat(int seatId)
            throws SeatBookedException, NotEnoughMoneyException, NoSuchSeatException;

    @Asynchronous
    Future<String> bookSeatAsync(int seatId);
}
