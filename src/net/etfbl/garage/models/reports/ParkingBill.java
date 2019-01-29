package net.etfbl.garage.models.reports;

import net.etfbl.garage.models.Vehicle;

import java.util.Date;

public class ParkingBill {
    private String vehicleName;
    private String registration;
    private long price;
    private long enterTime;
    private long exitTime = System.currentTimeMillis();

    public ParkingBill(Vehicle veh) {
        vehicleName = veh.getVehicleName();
        registration = veh.getRegistration();
        enterTime = veh.getEnteredTime();
        long hours = (exitTime - enterTime) / 1000;
        if (hours <= 1) {
            price = 1;
        } else if (hours <= 3) {
            price = 2;
        } else if (hours <= 24) {
            price = 8;
        } else {
            price = (hours / 24) * 8;
        }
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getRegistration() {
        return registration;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nNAME: " + vehicleName);
        builder.append("\nREGISTRATION: " + registration);
        builder.append("\nTIME OF ARRIVAL: " + new Date(enterTime).toString());
        builder.append("\nTIME OF DEPARTURE: " + new Date(exitTime).toString());
        builder.append("\n" + "PRICE: " + price + " KM");
        return builder.toString();
    }

    public long getEnterTime() {
        return enterTime;
    }

    public long getExitTime() {
        return exitTime;
    }
}
