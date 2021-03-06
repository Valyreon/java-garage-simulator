package net.etfbl.garage.models.departments.police;

import javafx.beans.property.SimpleStringProperty;
import net.etfbl.garage.application.UserGarageSimulator;
import net.etfbl.garage.models.Car;
import net.etfbl.garage.models.Direction;
import net.etfbl.garage.models.Platform;
import net.etfbl.garage.models.Vehicle;
import net.etfbl.garage.models.reports.AccidentReport;
import net.etfbl.garage.models.reports.WantedReport;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class PoliceCar extends Car implements Police {
    private final List<String> wantedRegistrations = new ArrayList<>();
    private File wantedList = null;
    private boolean rotation = false;
    private Vehicle target, otherVehicleInvolved;

    public PoliceCar(String name, String chassisNumber, String engineNumber, String registration, File photo, int numberOfDoors, File wantedList) {
        super(name, chassisNumber, engineNumber, registration, photo, numberOfDoors);
        this.wantedList = wantedList;
        readWantedFile();
    }

    public PoliceCar(String name, String chassisNumber, String engineNumber, String registration) {
        super(name, chassisNumber, engineNumber, registration);
    }

    public void readWantedFile() {
        if (wantedList != null && wantedList.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(wantedList));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.replace(" ", "");
                    wantedRegistrations.add(line);
                }
            } catch (FileNotFoundException e) {
                UserGarageSimulator.logError(Level.INFO, "FileNotFoundException in method readWantedFile() in class " +
                        "PoliceCar.java");
            } catch (IOException e) {
                UserGarageSimulator.logError(Level.INFO, "IOException in method readWantedFile() in class PoliceCar.java");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setTarget(Vehicle veh, Vehicle other) {
        target = veh;
        otherVehicleInvolved = other;
        rotation = true;
    }

    public File getWantedList() {
        return wantedList;
    }

    public void setWantedList(File wantedList) {
        this.wantedList = wantedList;
    }

    public boolean isRotation() {
        return rotation;
    }

    @Override
    public void run() {
        while (notFinished && !stop) {
            if (rotation) {
                vehicleMoveRot();
            } else {
                detectWanted(); //check for wanted vehicles
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
            try {
                sleep(new Random().nextInt(7000) + 3000);
            } catch (InterruptedException e) {
                UserGarageSimulator.logError(Level.INFO, "Vehicle has been interrupted at ("+row+", "+column+", "+platformNumber+").");
            }
            /************/
            AccidentReport report = new AccidentReport(target, otherVehicleInvolved);
            report.saveReport();
            /************/
            rotation = false;
            target.setPolicePause(false);
            fromEnteringToLeavingLane();
            setLeavingTrue();
            Vehicle veh;
            target.getPlatform().setEmergency(false);
            otherVehicleInvolved.getPlatform().setEmergency(false); // in case of race condition
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
        return (target.getRow() == row - 1 || target.getRow() == row + 1 || target.getRow() == row) && (target.getColumn() == column || target.getColumn() == column - 1 || target.getColumn() == column + 1);
    }

    @Override
    public String getSymbol() {
        if (rotation) {
            return "PR";
        }
        return "P ";
    }

    @Override
    public String getDepartmentString() {
        return "Police";
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        //out.defaultWriteObject();
        List<Object> obj = new ArrayList<>();
        obj.add(vehicleName.get());
        obj.add(chassisNumber.get());
        obj.add(engineNumber.get());
        obj.add(registration.get());
        obj.add(numberOfDoors);
        obj.add(photo);
        obj.add(rotation);
        obj.add(wantedList);
        out.writeObject(obj);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        //in.defaultReadObject();
        List<Object> obj = (List<Object>) in.readObject();
        vehicleName = new SimpleStringProperty((String) obj.get(0));
        chassisNumber = new SimpleStringProperty((String) obj.get(1));
        engineNumber = new SimpleStringProperty((String) obj.get(2));
        registration = new SimpleStringProperty((String) obj.get(3));
        numberOfDoors = (int) obj.get(4);
        photo = (File) obj.get(5);
        rotation = (boolean) obj.get(6);
        wantedList = (File) obj.get(7);
        enteredTime = System.currentTimeMillis();
        readWantedFile();
    }

    public void detectWanted() {
        if (wantedList != null) {
            int[] checkColumns;
            if (column == 1 || column == 5) {
                checkColumns = new int[]{column - 1, column + 2};
            } else if (column == 6 || column == 2) {
                checkColumns = new int[]{column + 1, column - 2};
            } else {
                checkColumns = new int[]{column};
            }
            for (int i : checkColumns) {
                Vehicle veh = platform.getGarage().getPlatform(platformNumber).getAt(row, i);
                if (veh != null && wantedRegistrations.contains(veh.getRegistration())) {
                    try {
                        sleep(new Random().nextInt(2000) + 3000);
                    } catch (InterruptedException e) {
                        UserGarageSimulator.logError(Level.INFO, "Vehicle has been interrupted at ("+row+", "+column+", "+platformNumber+").");
                    }
                    WantedReport report = new WantedReport(veh);
                    report.saveReport();
                    veh.setLeavingTrue();
                    if(!veh.isAlive())
                        veh.start();
                    this.setLeavingTrue();
                }
            }
        }
    }

}
