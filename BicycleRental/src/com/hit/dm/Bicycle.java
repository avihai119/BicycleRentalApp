package com.hit.dm;

public class Bicycle {
    protected String brand;

    protected int gearCount;
    protected double weight;
    protected Color color;
    protected final long id;
    protected boolean isRented;
    protected long renterId;
    public Bicycle(long id,String brand, int gearCount, double weight, Color color) {
        this.id = id;
        this.brand = brand;
        this.gearCount = gearCount;
        this.weight = weight;
        this.color = color;
        this.isRented = false;
    }

    public boolean isRented(){
        return isRented;
    }

    public long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public void displayInfo() {
        System.out.println("Id: " + id);
        System.out.println("Brand: " + brand);
        System.out.println("Gears: " + gearCount);
        System.out.println("Weight: " + weight + " kg");
        System.out.println("Color: " + color);
        System.out.println("Is rented: " + isRented);
    }
}
