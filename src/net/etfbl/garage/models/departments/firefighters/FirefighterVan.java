package net.etfbl.garage.models.departments.firefighters;

import javafx.beans.property.SimpleStringProperty;
import net.etfbl.garage.models.Direction;
import net.etfbl.garage.models.Platform;
import net.etfbl.garage.models.Van;
import net.etfbl.garage.models.Vehicle;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FirefighterVan extends Van implements Firefighter {
    boolean rotation = false;
    private Vehicle target = null;

    public FirefighterVan(String name, String chassisNumber, String engineNumber, String registration, File photo, int carryWeight) {
        super(name, chassisNumber, engineNumber, registration, photo, carryWeight);
    }

    public FirefighterVan(String name, String chassisNumber, String engineNumber, String registration) {
        super(name, chassisNumber, engineNumber, registration);
    }

    public void setTarget(Vehicle veh) {
        target = veh;
        rotation = true;
    }

    public boolean isRotation() {
        return rotation;
    }

    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    @Override
    public String getSymbol() {
        if (rotation) {
            return "FR";
        }
        return "F ";
    }

    public String getDepartmentString() {
        return "Firefighters";
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        //out.defaultWriteObject();
        List<Object> obj = new ArrayList<>();
        obj.add(vehicleName.get());
        obj.add(chassisNumber.get());
        obj.add(engineNumber.get());
        obj.add(registration.get());
        obj.add(carryWeight);
        obj.add(photo);
        obj.add(rotation);
        out.writeObject(obj);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        //in.defaultReadObject();
        List<Object> obj = (List<Object>) in.readObject();
        vehicleName = new SimpleStringProperty((String) obj.get(0));
        chassisNumber = new SimpleStringProperty((String) obj.get(1));
        engineNumber = new SimpleStringProperty((String) obj.get(2));
        registration = new SimpleStringProperty((String) obj.get(3));
        carryWeight = (int) obj.get(4);
        photo = (File) obj.get(5);
        rotation = (boolean) obj.get(6);
        enteredTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (notFinished && !stop) {
            if (rotation) {
                vehicleMoveRot();
            } else {
                vehicleMove();
            }
        }
    }

    private void vehicleMoveRot() {
        if (target.getPlatformNumber() > platformNumber) {
            if (column == 7) {
                moveToPlatformAbove();
            } else {
                move(Direction.RIGHT);
            }
        } else if (!isNearTarget()) {
            if (row == 1 && (target.getRow() == 0 || target.getRow() == 1)) {
                move(Direction.RIGHT);
            } else {
                moveDownEnteringLane();
            }
        } else {
            rotation = false;
            target.setFirePause(false);
            fromEnteringToLeavingLane();
            setLeavingTrue();
            Vehicle veh;
            while (platform.getGarage().getPlatform(platformNumber).getAt(row, column) != null && !stop && notFinished) {
                moveDownLeavingLane();
            }
            if (notFinished) {
                Platform platNorm = platform.getGarage().getPlatform(platformNumber);
                platNorm.setAt(row, column, this);
                platform.setAt(row, column, null);
                platform.getAllVehicles().remove(this);
                platNorm.getAllVehicles().add(this);
                synchronized (this) {
                    this.notifyAll();
                }
                platform = platNorm;
                target = null;
            }
        }
    }

    private boolean isNearTarget() {
        return (target.getRow() == row - 1 || target.getRow() == row + 1 || target.getRow() == row) && (target.getColumn() == column - 1 || target.getColumn() == column + 1 || target.getColumn() == column);
    }
}
