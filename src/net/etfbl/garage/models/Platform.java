package net.etfbl.garage.models;

import net.etfbl.garage.application.UserGarageSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Platform {
    private final Garage garage;
    private volatile Vehicle[][] matrix = new Vehicle[10][8];
    private final int platformIndex;
    private final List<Vehicle> vehiclesInPlatform = new ArrayList<>();
    private volatile boolean emergency = false;
    private Platform next;
    private Platform previous;
    private int numberOfParked = 0;

    public Platform(Garage gar, int index, boolean emergencyPlat) {
        this.garage = gar;
        platformIndex = index;
        if (index != 0) {
            Platform beforeLast = emergencyPlat ? gar.getEmergencyPlatform(index - 1) : gar.getPlatform(index - 1);
            beforeLast.next = this;
            this.previous = beforeLast;
        }
    }

    public static boolean isItParkingAt(int x, int y) {
        if (y == 0 && x >= 2 && x <= 9) {
            return true;
        } else if ((y == 3 || y == 4) && x >= 2 && x <= 7) {
            return true;
        } else {
            return y == 7 && x >= 2 && x <= 9;
        }
    }

    public static int whereIsParking(int row, int column) {
        if (column == 0 && row >= 2 && row <= 9) {
            return 1;
        } else if (column == 3 && row >= 2 && row <= 7) {
            return 2;
        } else if (column == 4 && row >= 2 && row <= 7) {
            return 3;
        } else if (column == 7 && row >= 2 && row <= 9) {
            return 4;
        } else {
            return 0;
        }
    }

    public Garage getGarage() {
        return garage;
    }

    public Vehicle getAt(int x, int y) {
        return matrix[x][y];
    }

    public static boolean isItLeavingLane(int row, int column) {
        switch (column) {
            case 0:
            case 1:
            case 6:
            case 7:
                return row == 0;
            case 2:
            case 5:
                return row >= 0 && row <= 8;
            case 3:
            case 4:
                return row == 8 || row == 0;
            default:
                return false;

        }
    }

    public static boolean isItEnteringLane(int row, int column) {
        switch (column) {
            case 0:
            case 7:
                return row == 1;
            case 1:
            case 6:
                return row >= 1 && row <= 9;
            case 2:
            case 3:
            case 4:
            case 5:
                return row == 9;
            default:
                return false;
        }
    }

    public Platform getNext() {
        return next;
    }

    public Platform getPrevious() {
        return previous;
    }

    public void setAt(int x, int y, Vehicle veh) {
        matrix[x][y] = veh;
    }

    public int count() {
        return vehiclesInPlatform.size();
    }

    public List<Vehicle> getAllVehicles() {
        return vehiclesInPlatform;
    }

    public void setParkedAt(int x, int y, Vehicle veh, int platformNumber) {
        if (isItParkingAt(x, y)) {
            matrix[x][y] = veh;
            matrix[x][y].setPlatform(this);
            matrix[x][y].setParkedTrue();
            matrix[x][y].setPosition(x, y, platformNumber);
            vehiclesInPlatform.add(veh);
            garage.getAllVehicles().add(veh);
        }
    }

    public boolean isEmptyAt(int x, int y) {
        return matrix[x][y] == null;
    }

    public void update() {
        if (garage.getSelectedPlatform() == platformIndex) {
            garage.getOutput().setLength(0);
            for (int i = 0; i < 10; i++, garage.getOutput().append("\n")) {
                for (int j = 0; j < 8; j++) {
                    Vehicle em = garage.getEmergencyPlatform(platformIndex).getAt(i, j);
                    if (em != null) {
                        garage.getOutput().append(em.getSymbol()).append(" ");
                    } else if (matrix[i][j] == null && !isItParkingAt(i, j)) {
                        garage.getOutput().append("   ");
                    } else if (matrix[i][j] == null && isItParkingAt(i, j)) {
                        garage.getOutput().append("*  ");
                    } else {
                        try {
                            garage.getOutput().append(matrix[i][j].getSymbol()).append(" ");
                        } catch (NullPointerException e) {
                            UserGarageSimulator.errorLogger.log(Level.INFO, "NullPointerException in update() method " +
                                    "in Platform.java. Vehicle moved before print.");
                        }
                    }
                }
            }
        }
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean x) {
        emergency = x;
    }

    public void incNumOfParked() {
        numberOfParked++;
    }

    public void decNumOfParked() {
        numberOfParked--;
    }

    public int getNumberOfParked() {
        return numberOfParked;
    }
}
