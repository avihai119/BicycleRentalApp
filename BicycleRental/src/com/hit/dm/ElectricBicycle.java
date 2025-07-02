package com.hit.dm;

public class ElectricBicycle extends Bicycle {
    private int batteryCapacity;
    private int range;
    private boolean hasPedalAssist;

    public ElectricBicycle(long id ,String brand, int gearCount, double weight, Color color,
                           int batteryCapacity, int range, boolean hasPedalAssist) {
        super(id,brand, gearCount, weight, color);
        this.batteryCapacity = batteryCapacity;
        this.range = range;
        this.hasPedalAssist = hasPedalAssist;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Battery Capacity: " + batteryCapacity + " Wh");
        System.out.println("Range: " + range + " km");
        System.out.println("Pedal Assist: " + (hasPedalAssist ? "Yes" : "No"));
    }
}

