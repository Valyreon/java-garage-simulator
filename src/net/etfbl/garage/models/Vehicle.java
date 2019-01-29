package net.etfbl.garage.models;

import javafx.beans.property.SimpleStringProperty;
import net.etfbl.garage.models.departments.firefighters.Firefighter;
import net.etfbl.garage.models.departments.medical.Medical;
import net.etfbl.garage.models.departments.police.Police;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.Random;

import static net.etfbl.garage.models.Direction.*;

public abstract class Vehicle extends Thread implements Serializable {
    static final long serialVersionUID = 42L;
    protected transient static boolean stop = false;
    protected final int speed = (new Random().nextInt(500) + 750) / 2;
    protected transient SimpleStringProperty vehicleName;
    protected transient SimpleStringProperty chassisNumber;
    protected transient SimpleStringProperty engineNumber;
    protected transient SimpleStringProperty registration;
    protected transient File photo;
    protected transient int row, column, platformNumber;
    protected transient Platform platform;
    protected transient long enteredTime = System.currentTimeMillis();
    protected transient boolean policePause = false, medicalPause = false, firePause = false;
    protected transient boolean parked, leaving, entering, notFinished = true;
    protected transient int crashed = 0;

    public Vehicle(String name, String chassisNumber, String engineNumber, String registration) {
        this.vehicleName = new SimpleStringProperty(name);
        this.chassisNumber = new SimpleStringProperty(chassisNumber);
        this.engineNumber = new SimpleStringProperty(engineNumber);
        this.registration = new SimpleStringProperty(registration);
        notFinished = true;
    }

    static private boolean isItRegular(Vehicle veh) {
        return !(veh instanceof Police) && !(veh instanceof Firefighter) && !(veh instanceof Medical);
    }

    public static void stopAll() {
        stop = true;
    }

    static public char getDepartmentNumber(String dept) {
        switch (dept) {
            case "Police":
                return 1;
            case "Medical":
                return 3;
            case "Firefighters":
                return 2;
            default:
                return 0;
        }
    }

    public long getEnteredTime() {
        return enteredTime;
    }

    public void setPolicePause(boolean policePause) {
        this.policePause = policePause;
    }

    public void setMedicalPause(boolean medicalPause) {
        this.medicalPause = medicalPause;
    }

    public int getPlatformNumber() {
        return platformNumber;
    }

    public int getRow() {
        return row;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public SimpleStringProperty vehicleNameProperty() {
        return vehicleName;
    }

    public SimpleStringProperty chassisNumberProperty() {
        return chassisNumber;
    }

    public SimpleStringProperty engineNumberProperty() {
        return engineNumber;
    }

    public SimpleStringProperty registrationProperty() {
        return registration;
    }

    public String getVehicleName() {
        return vehicleName.get();
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName.setValue(vehicleName);
    }

    public String getChassisNumber() {
        return chassisNumber.get();
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber.setValue(chassisNumber);
    }

    public String getEngineNumber() {
        return engineNumber.get();
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber.setValue(engineNumber);
    }

    public String getRegistration() {
        return registration.get();
    }

    public void setRegistration(String registration) {
        this.registration.setValue(registration);
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public boolean isLeaving() {
        return leaving;
    }

    public void setLeavingTrue() {
        leaving = true;
        parked = entering = false;
        notFinished = true;
        if (Platform.isItParkingAt(row, column)) {
            platform.decNumOfParked();
            platform.getGarage().decNumberOfParked();
        }
    }

    public void setEnteringTrue() {
        entering = true;
        parked = leaving = false;
        notFinished = true;
    }

    public int getColumn() {
        return column;
    }

    public void setParkedTrue() {
        parked = true;
        leaving = entering = false;
        platform.incNumOfParked();
        platform.getGarage().incNumberOfParked();
    }

    public String getDepartmentString() {
        return "None";
    }

    public String getSymbol() {
        return "V ";
    }

    @Override
    public void run() {
        while (notFinished && !stop) {
            vehicleMove();
        }
    }

    protected void vehicleMove() {
        if (parked) {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (entering) {
            if (!parked && row == 1 && platform.getNumberOfParked() >= 28) {
                if (column != 7) {
                    this.move(RIGHT);
                } else {
                    this.moveToPlatformAbove();
                }
            } else if (Platform.isItEnteringLane(row, column)) {
                if (row >= 2 && (column == 1 || column == 6)) {
                    if (column == 1) { // if we are in column 1 then we should first look left, then right
                        if (platform.isEmptyAt(row, column - 1)) {
                            this.move(LEFT);
                            this.setParkedTrue(); //park left
                        } else if (row <= 7 && platform.isEmptyAt(row, column + 2) && (platform.isEmptyAt(row, column + 1) || platform.getAt(row, column + 1).isLeaving())) {
                            this.move(RIGHT); //now we on the leaving lane
                            this.move(RIGHT);
                            this.setParkedTrue(); //park right
                        }
                    } else { // if we are in column 6 first check right, then left
                        if (platform.isEmptyAt(row, column + 1)) {
                            this.move(RIGHT);
                            this.setParkedTrue(); //park right
                        } else if (row <= 7 && platform.isEmptyAt(row, column - 2) && (platform.isEmptyAt(row, column - 1) || platform.getAt(row, column - 1).isLeaving())) {
                            this.move(LEFT); //now we are on the leaving lane
                            this.move(LEFT);
                            this.setParkedTrue(); //park left
                        }
                    }
                }
                if (!parked)
                    moveDownEnteringLane();
            } else if (Platform.isItLeavingLane(row, column)) {
                fromLeavingToEnteringLane();
            }
        } else if (leaving) {
            if (Platform.isItLeavingLane(row, column)) {
                this.moveDownLeavingLane();
            } else if (Platform.isItEnteringLane(row, column)) { //if I am not i need to get to it first
                fromEnteringToLeavingLane();
            } else {
                getFromParkingToLeavingLane();
            }
        }
    }

    protected void getFromParkingToEnteringLane() {
        switch (Platform.whereIsParking(row, column)) {
            case 1:
                this.move(RIGHT);
                break;
            case 2:
                this.move(LEFT);
                this.move(LEFT);
                break;
            case 3:
                this.move(RIGHT);
                this.move(RIGHT);
                break;
            case 4:
                this.move(LEFT);
                break;
            default:
                break;
        }
    }

    protected void getFromParkingToLeavingLane() {
        if (row == 9 && column == 7) {
            this.move(LEFT);
            this.move(UP);
            this.move(LEFT);
        } else if (row == 9 && column == 0) {
            this.move(RIGHT);
            this.move(RIGHT);
            this.move(UP);
        }
        switch (Platform.whereIsParking(row, column)) {
            case 1:
                this.move(RIGHT);
                this.move(RIGHT);
                break;
            case 2:
                this.move(LEFT);
                break;
            case 3:
                this.move(RIGHT);
                break;
            case 4:
                this.move(LEFT);
                this.move(LEFT);
                break;
            default:
                break;
        }
    }

    protected void moveDownEnteringLane() {
        if ((row == 1 && (column == 0 || column == 6)) || (row == 9 && column >= 1 && column <= 5)) {
            this.move(RIGHT);
        } else if (column == 1 && row >= 1 && row <= 8) {
            this.move(DOWN);
        } else if (column == 6 && row >= 2 && row <= 9) {
            this.move(UP);
        } else if (column == 7) {
            if (platformNumber == platform.getGarage().getPlatforms().size() - 1) {
                this.move(UP);
                this.setLeavingTrue();
            } else {
                moveToPlatformAbove();
            }
        }
    }

    protected void moveDownLeavingLane() {
        if ((row == 0 && column >= 1 && column <= 7) || (row == 8 && column >= 3 && column <= 5)) {
            this.move(LEFT);
        } else if (column == 5 && row >= 0 && row <= 7) {
            this.move(DOWN);
        } else if (column == 2 && row >= 1 && row <= 8) {
            this.move(UP);
        } else if (column == 0 && row == 0) {
            if (platformNumber == 0) { //move to previous platform
                platform.setAt(0, 0, null);
                this.notFinished = false;
                platform.getGarage().getAllVehicles().remove(this);
                platform.getAllVehicles().remove(this);
                if (isItRegular(this)) {
                    platform.getGarage().billParking(this);
                }
            } else {
                moveToPlatformBelow();
            }
        }
    }

    protected void moveToPlatformBelow() {
        Platform platBelow = platform.getPrevious(); //platform.getGarage().getPlatform(platformNumber-1);
        Vehicle veh;
        while ((veh = platBelow.getAt(0, 7)) != null) {
            try {
                synchronized (veh) {
                    veh.wait();
                }
            } catch (InterruptedException e) {
                //write to error log
            }
        }

        platform.setAt(row, column, null);
        platform.getAllVehicles().remove(this);
        platformNumber--;
        row = 0;
        column = 7;
        platform = platBelow;
        platBelow.getAllVehicles().add(this);
        platBelow.setAt(0, 7, this);
        synchronized (this) {
            this.notifyAll();
        }

    }

    protected void sleepLittle() {
        try {
            sleep(speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void moveToPlatformAbove() {
        Platform platAbove = platform.getNext(); //platform.getGarage().getPlatform(platformNumber + 1);
        Vehicle veh;
        while ((veh = platAbove.getAt(1, 0)) != null) {
            try {
                synchronized (veh) {
                    veh.wait();
                }
            } catch (InterruptedException e) {
                //write to error log
            }
        }
        platform.setAt(row, column, null);
        platform.getAllVehicles().remove(this);
        platformNumber++;
        row = 1;
        column = 0;
        platform = platAbove;
        platAbove.getAllVehicles().add(this);
        platAbove.setAt(1, 0, this);
        synchronized (this) {
            this.notifyAll();
        }

    }

    public void setFirePause(boolean firePause) {
        this.firePause = firePause;
    }

    public void setPosition(int x, int y, int platNumber) {
        row = x;
        column = y;
        platformNumber = platNumber;
    }

    protected void fromEnteringToLeavingLane() {
        if (row == 9 && column == 1) {
            move(RIGHT);
            move(UP);
        } else if (row == 9 && column == 6) {
            move(UP);
            move(LEFT);
        } else if (row == 1) {
            move(UP);
        } else if (Platform.isItEnteringLane(row, column)) {
            if (column == 0 || column == 7 || (column >= 2 && column <= 6 && row == 9)) {
                move(UP);
            } else if (column == 1) {
                move(RIGHT);
            } else if (column == 6) {
                move(LEFT);
            }
        }
    }

    protected void fromLeavingToEnteringLane() {
        if (row == 0 && column == 2) {
            move(DOWN);
            move(LEFT);
        } else if (row == 0 && column == 5) {
            move(DOWN);
            move(RIGHT);
        } else if (Platform.isItLeavingLane(row, column)) {
            if (row == 0) {
                move(DOWN);
            } else if (column == 2) {
                move(LEFT);
            } else if (column == 5) {
                move(RIGHT);
            } else {
                move(DOWN);
            }
        }
    }

    protected void move(Direction dir) {
        boolean noDept = isItRegular(this);
        if (noDept) {
            while (platform.isEmergency() || policePause || medicalPause || firePause) {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        int rowMove = row, columnMove = column;
        switch (dir) {
            case UP:
                rowMove--;
                break;
            case RIGHT:
                columnMove++;
                break;
            case DOWN:
                rowMove++;
                break;
            case LEFT:
                columnMove--;
                break;
            default:
                break;
        }
        sleepLittle();
        boolean emerg = noDept && !(column <= 1 && row <= 1) && crashed < 2; /*!(column<=2 && row<=1)*/
        Vehicle veh;
        while ((veh = platform.getAt(rowMove, columnMove)) != null && !stop) {
            if (emerg && isItRegular(veh)) {
                if (Math.random() > 0.9) { //10% crash chance
                    veh.policePause = true;
                    veh.firePause = true;
                    veh.medicalPause = true;
                    platform.setEmergency(true);
                    platform.getGarage().callEmergency(veh, this);
                }
                emerg = false;
                crashed++;
            }
            try {
                synchronized (veh) {
                    veh.wait();
                }
            } catch (InterruptedException e) {
                //write to error log
            }
        }
        platform.setAt(row, column, null);
        platform.setAt(row = rowMove, column = columnMove, this);
        synchronized (this) {
            this.notifyAll();
        }
        sleepLittle();
    }

    public double distanceTo(Vehicle veh) {
        if (veh == null || veh.getPlatformNumber() != platformNumber) {
            return Double.MAX_VALUE;
        }
        return Math.sqrt((row - veh.row) * (row - veh.row) + (column - veh.column) * (column - veh.column));
    }
}
