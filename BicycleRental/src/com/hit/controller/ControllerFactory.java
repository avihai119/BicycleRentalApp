package com.hit.controller;

import com.hit.controller.BicycleController;
import com.hit.controller.RentalController;
import com.hit.controller.UserController;
import com.hit.service.RentalService;

public class ControllerFactory {
    private static ControllerFactory instance;
    private RentalService rentalService;

    private ControllerFactory(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    public static synchronized ControllerFactory getInstance(RentalService rentalService) {
        if (instance == null) {
            instance = new ControllerFactory(rentalService);
            System.out.println("Controller Factory loaded and initialized");
        }

        return instance;
    }

    public Object createController(String controllerType) {
        if (controllerType == null) {
            throw new IllegalArgumentException("Controller type cannot be null");
        }

        switch (controllerType.toLowerCase()) {
            case "bicycle":
                System.out.println("Factory creating BicycleController");
                return new BicycleController(rentalService);

            case "rental":
                System.out.println("Factory creating RentalController");
                return new RentalController(rentalService);

            case "user":
                System.out.println("Factory creating UserController");
                return new UserController(rentalService);

            default:
                throw new IllegalArgumentException("Unknown controller type: " + controllerType +
                        ". Supported types: bicycle, rental, user");
        }
    }

    public Object createController(ControllerType type) {
        return createController(type.name().toLowerCase());
    }

    public boolean isControllerSupported(String controllerType) {
        try {
            ControllerType.valueOf(controllerType.toUpperCase());
            return true;
        }

        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String[] getSupportedControllerTypes() {
        ControllerType[] types = ControllerType.values();
        String[] typeNames = new String[types.length];

        for (int i = 0; i < types.length; i++) {
            typeNames[i] = types[i].name().toLowerCase();
        }

        return typeNames;
    }

    public enum ControllerType {
        BICYCLE,
        RENTAL,
        USER
    }
}