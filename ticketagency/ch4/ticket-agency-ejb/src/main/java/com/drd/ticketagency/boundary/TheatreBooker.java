package com.drd.ticketagency.boundary;

import com.drd.ticketagency.control.NoSuchSeatException;
import com.drd.ticketagency.control.SeatBookedException;
import com.drd.ticketagency.control.TheatreBox;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author darrenp - 2017-10-06.
 */
@Stateful
@Remote(TheatreBookerRemote.class)
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
public class TheatreBooker implements TheatreBookerRemote {

    private static final Logger logger = Logger.getLogger(TheatreBooker.class);

    @EJB
    private TheatreBox theatreBox;
    private int money;

    @PostConstruct
    public void createCustomer() {
        this.money = 100;
    }

    @Override
    public int getAccountBalance() {
        return money;
    }

    @Override
    public String bookSeat(int seatId)
            throws SeatBookedException, NotEnoughMoneyException, NoSuchSeatException {
        final int seatPrice = theatreBox.getSeatPrice(seatId);
        if(seatPrice > money) {
            throw new NotEnoughMoneyException(String.format("You don't have enough money to buy this '%d' seat!", seatId));
        }

        theatreBox.buyTicket(seatId);
        money = money - seatPrice;

        logger.infov("Seat '{0}' booked.", seatId);
        return "Seat booked.";
    }

    @Asynchronous
    @Override
    public Future<String> bookSeatAsync(int seatId) {
        try {
            Thread.sleep(10000);
            bookSeat(seatId);
            return new AsyncResult<>("Booked seat: " + seatId + ". Money left: " + money);
        } catch (NoSuchSeatException | SeatBookedException |
                NotEnoughMoneyException | InterruptedException e) {
            return new AsyncResult<>(e.getMessage());
        }
    }
}
