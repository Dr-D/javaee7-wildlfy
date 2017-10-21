package com.drd.ticketagency.control;

/**
 * @author darrenp - 2017-10-06.
 */
public class Seat {

    private int id;
    private String name;
    private int price;
    private boolean booked;

    public Seat(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Seat(int id, String name, int price, boolean booked) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.booked = booked;
    }

    public Seat getBookedSeat() {
        return new Seat(id, name, price, true);
    }

    public Integer getID() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public boolean isBooked() {
        return booked;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", booked=" + booked +
                '}';
    }
}
