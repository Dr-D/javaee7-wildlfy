package com.drd.client;

import com.drd.ticketagency.boundary.NotEnoughMoneyException;
import com.drd.ticketagency.boundary.TheatreBookerRemote;
import com.drd.ticketagency.boundary.TheatreInfoRemote;
import com.drd.ticketagency.control.NoSuchSeatException;
import com.drd.ticketagency.control.SeatBookedException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author darrenp - 2017-10-06.
 */
public class TicketAgencyClient {

    private static final Logger logger = Logger.getLogger(TicketAgencyClient.class.getName());
    private final String usage = "Commands: book, list, money, quit, bookasync, mail";

    private final List<Future<String>> lastBookings = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        Logger.getLogger("org.jboss").setLevel(Level.SEVERE);
        Logger.getLogger("org.xnio").setLevel(Level.SEVERE);

        new TicketAgencyClient().run();
    }

    private final Context context;
    private TheatreInfoRemote theatreInfo;
    private TheatreBookerRemote theatreBooker;

    public TicketAgencyClient() throws NamingException {
        final Properties jndiProperties = new Properties();
        jndiProperties.setProperty(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        this.context = new InitialContext(jndiProperties);
    }

    private enum Command {
        BOOK, LIST, MONEY, QUIT, INVALID, BOOKASYNC, MAIL;

        public static Command parseCommand(String stringCommand) {
            try {
                return valueOf(stringCommand.trim().toUpperCase());
            } catch (IllegalArgumentException iae) {
                return INVALID;
            }
        }
    }

    private void run() throws NamingException {
        this.theatreInfo = lookupTheatreInfoEJB();
        this.theatreBooker = lookupTheatreBookerEJB();

        showWelcomeMessage();

        boolean exit = false;
        while (!exit) {
            final String stringCommand =  IOUtils.readLine("> ");
            final Command command = Command.parseCommand(stringCommand);

            switch (command) {
                case MAIL: handleMail(); break;
                case BOOKASYNC: handleBookAsync(); break;
                case BOOK: handleBook(); break;
                case LIST: handleList(); break;
                case MONEY: handleMoney(); break;
                case QUIT: exit = true; break;
                default:
                    logger.warning("Unknown command: '" + stringCommand + "'");
                    System.out.println(usage);
            }
        }
        handleQuit();
    }

    private void handleBookAsync() {
        int seatId;

        try {
            seatId = IOUtils.readInt("Enter SeatID: ");
        } catch (NumberFormatException nfe) {
            logger.warning("Wrong seatID format!\n" + usage);
            return;
        }

        lastBookings.add(theatreBooker.bookSeatAsync(seatId));
        logger.info("Booking issued. Verify you mail!");
    }

    private void handleMail() {
        boolean displayed = false;
        final List<Future<String>> notFinished = new ArrayList<>();
        for(Future<String> booking : lastBookings) {
            if (booking.isDone()) {
                try {
                    final String result = booking.get();
                    logger.info("Mail received: " + result);
                    displayed = true;
                } catch (InterruptedException | ExecutionException e) {
                    logger.warning(e.getMessage());
                }
            } else {
                notFinished.add(booking);
            }
        }

        lastBookings.retainAll(notFinished);
        if(!displayed) {
            logger.info("No mail received!");
        }
    }

    private void handleBook() {
        int seatId;

        try {
            seatId = IOUtils.readInt("Enter SeatID: ");
        } catch (NumberFormatException nfe) {
            logger.warning(usage);
            return;
        }

        try {
            final String retVal = theatreBooker.bookSeat(seatId);
            System.out.println(retVal);
        } catch (SeatBookedException | NotEnoughMoneyException | NoSuchSeatException e) {
            logger.warning(e.getMessage());
        }
    }

    private void handleList() {
        logger.info(theatreInfo.printSeatList());
    }

    private void handleMoney() {
        final int accountBalance = theatreBooker.getAccountBalance();
        logger.info("You have: '" + accountBalance + "' money left");
    }

    void handleQuit() {
        logger.info("Bye");
    }

    private TheatreBookerRemote lookupTheatreBookerEJB() throws NamingException {
        return (TheatreBookerRemote) context.lookup(
                "ejb:/ticket-agency-ejb-1.0-SNAPSHOT/TheatreBooker!com.drd.ticketagency.boundary.TheatreBookerRemote?stateful");
    }

    private TheatreInfoRemote lookupTheatreInfoEJB() throws NamingException {
        return (TheatreInfoRemote) context.lookup(
                "ejb:/ticket-agency-ejb-1.0-SNAPSHOT/TheatreInfo!com.drd.ticketagency.boundary.TheatreInfoRemote");
    }

    private void showWelcomeMessage() {
        System.out.println("Theatre booking system");
        System.out.println("=====================================");
        System.out.println(usage);
    }
}
