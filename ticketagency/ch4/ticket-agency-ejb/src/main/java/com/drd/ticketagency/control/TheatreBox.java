package com.drd.ticketagency.control;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

/**
 * @author darrenp - 2017-10-06.
 */

@Singleton
@Startup
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
public class TheatreBox {

    private static final Logger logger = Logger.getLogger(TheatreBox.class);

    private Map<Integer, Seat> seats;

    @PostConstruct
    public  void setupTheatre() {
        seats = new HashMap<>();
        int id = 0;
        for(int i = 0; i < 5; i++) {
            addSeat(new Seat(++id, "Stalls", 40));
            addSeat(new Seat(++id, "Circle", 20));
            addSeat(new Seat(++id, "Balcony", 10));
        }
        logger.info("Seat Map constructed.");
    }

    private void addSeat(Seat seat) {
        seats.put(seat.getID(), seat);
    }

    @Lock(READ)
    public Collection<Seat> getSeats() {
        return Collections.unmodifiableCollection(seats.values());
    }

    @Lock(READ)
    public int getSeatPrice(int seatID) throws NoSuchSeatException {
        return getSeat(seatID).getPrice();
    }

    @Lock(WRITE)
    public void buyTicket(int seatId) throws SeatBookedException, NoSuchSeatException {
        final Seat seat = getSeat(seatId);
        if(seat.isBooked()) {
            throw new SeatBookedException(String.format("Seat '%d' already booked!", seatId));
        }
        addSeat(seat.getBookedSeat());
    }

    @Lock(READ)
    private Seat getSeat(int seatID) throws NoSuchSeatException {
        final Seat seat = seats.get(seatID);
        if (seat == null) {
            throw new NoSuchSeatException(String.format("Seat '%d' does not exist!", seatID));
        }
        return seat;
    }
}