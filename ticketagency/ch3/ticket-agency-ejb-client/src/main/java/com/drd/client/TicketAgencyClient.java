package com.drd.client;

import com.drd.ticketagency.boundary.NotEnoughMoneyException;
import com.drd.ticketagency.boundary.TheatreBookerRemote;
import com.drd.ticketagency.boundary.TheatreInfoRemote;
import com.drd.ticketagency.control.NoSuchSeatException;
import com.drd.ticketagency.control.SeatBookedException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author darrenp - 2017-10-06.
 */
public class TicketAgencyClient {

    private static final Logger logger = Logger.getLogger(TicketAgencyClient.class.getName());
    private final String usage = "Commands: book, list, money, quit";

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
        BOOK, LIST, MONEY, QUIT, INVALID;

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
        System.out.println("Commands: book, list, money, quit");
    }
}
