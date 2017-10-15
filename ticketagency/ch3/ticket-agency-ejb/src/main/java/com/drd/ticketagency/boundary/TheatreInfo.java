package com.drd.ticketagency.boundary;

import com.drd.ticketagency.control.Seat;
import com.drd.ticketagency.control.TheatreBox;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.Collection;

/**
 * @author darrenp - 2017-10-06.
 */
@Stateless
@Remote(TheatreInfoRemote.class)
public class TheatreInfo implements TheatreInfoRemote {

    @EJB
    private TheatreBox box;

    @Override
    public String printSeatList() {
        final Collection<Seat> seats = box.getSeats();
        final StringBuilder sb = new StringBuilder();
        for(Seat seat : seats) {
            sb.append(seat.toString());
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }
}
