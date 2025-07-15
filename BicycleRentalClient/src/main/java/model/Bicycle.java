package model;

public class Bicycle {
    private long id;
    private String brand;
    private int gearCount;
    private double weight;
    private Color color;
    private boolean isRented;
    private int batteryCapacity;
    private int range;
    private boolean hasPedalAssist;

    public Bicycle() {}

    public Bicycle(long id, String brand, int gearCount, double weight, Color color) {
        this.id = id;
        this.brand = brand;
        this.gearCount = gearCount;
        this.weight = weight;
        this.color = color;
        this.isRented = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getGearCount() {
        return gearCount;
    }
    public void setGearCount(int gearCount) {
        this.gearCount = gearCount;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(int batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean isHasPedalAssist() {
        return hasPedalAssist;
    }

    public void setHasPedalAssist(boolean hasPedalAssist) {
        this.hasPedalAssist = hasPedalAssist;
    }

    @Override
    public String toString() {
        return "ID: " + id + " - " + brand + " (" + color + ")";
    }
}