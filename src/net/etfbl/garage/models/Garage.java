package net.etfbl.garage.models;

import javafx.scene.control.TextArea;
import net.etfbl.garage.application.UserGarageSimulator;
import net.etfbl.garage.models.departments.firefighters.FirefighterVan;
import net.etfbl.garage.models.departments.medical.MedicalCar;
import net.etfbl.garage.models.departments.police.PoliceCar;
import net.etfbl.garage.models.reports.ParkingBill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static net.etfbl.garage.application.UserGarageSimulator.platformList;

public class Garage {
    private final List<Vehicle> allVehicles = new ArrayList<>();
    private final List<Platform> platforms = new ArrayList<>();
    private final List<Platform> emergencyPlatforms = new ArrayList<>();
    private final List<ParkingBill> bills = new ArrayList<>();

    private final StringBuilder output = new StringBuilder();
    private int constructedCars = 0;
    private final int numberOfPlatforms;
    private final int minimumNumberPerPlatform;
    private int selectedPlatform = 0;

    VehicleFactory factory = new VehicleFactory();
    private int numberOfParked = 0;

    private final ExecutorService exec = Executors.newCachedThreadPool();

    public ExecutorService getExecutor() {
        return exec;
    }

    public Garage(List<List<Vehicle>> list, int numberOfPlatforms, int minimum) {
        minimumNumberPerPlatform = minimum;
        this.numberOfPlatforms = numberOfPlatforms;
        for (int i = 0; i < numberOfPlatforms; i++) {
            platforms.add(new Platform(this, i, false));
            emergencyPlatforms.add(new Platform(this, i, true));
            randomDistribution(i, list.get(i));
        }
    }

    public void billParking(Vehicle veh) {
        bills.add(new ParkingBill(veh));
    }

    public List<Vehicle> getAllVehicles() {
        return allVehicles;
    }

    public Platform getPlatform(int index) {
        return platforms.get(index);
    }

    public Platform getEmergencyPlatform(int index) {
        return emergencyPlatforms.get(index);
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public int getSelectedPlatform() {
        return selectedPlatform;
    }

    public List<Platform> getEmergencyPlatforms() {
        return emergencyPlatforms;
    }

    public void setSelectedPlatform(int selectedPlatform) {
        this.selectedPlatform = selectedPlatform;
        platforms.get(selectedPlatform).update();
    }

    public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output.setLength(0);
        this.output.append(output);
    }

    public void startLeaving() {
        for(Platform p: platforms) {
            int leaveCount = (p.count()*15)/100;
            if(leaveCount==0)
                leaveCount++;
            for(int i=0; i<leaveCount; i++) {
                Vehicle x = p.getAllVehicles().get(i);
                x.setLeavingTrue();
                x.start();
            }
        }
    }

    public void stopAll() {
        Vehicle.stopAll();
    }

    void randomDistribution(int platNumber, List<Vehicle> list) {
        Random randomNum = new Random();
        int[] columns = {0,3,4,7};
        for (int i = 0; i < minimumNumberPerPlatform || i < list.size(); i++) {
            int column = 1 + randomNum.nextInt(4);
            int row;
            int count = 0;
            while (!platforms.get(platNumber).isEmptyAt(row = 2 + randomNum.nextInt(column == 2 || column == 3 ? 6 : 8)
                    , columns[column-1])) {
                if (count>=20) {
                    column = 1 + randomNum.nextInt(4);
                    count = 0;
                }
                count++;
            }
            platforms.get(platNumber).setParkedAt(row, columns[column - 1], i < list.size() ? list.get(i) : factory.constructRandomVehicle(), platNumber);
        }
    }

    void read() {
        for (int i = 0; i < platformList.size(); i++) {
            randomDistribution(i, platformList.get(i));
        }
    }

    public int getNumberOfPlatforms() {
        return numberOfPlatforms;
    }

    public void enterNewVehicle() {
        Platform plat = platforms.get(0);
        Vehicle veh = factory.constructRandomVehicle();
        veh.setPosition(1, 0, 0);
        veh.setPlatform(plat);
        veh.setEnteringTrue();
        plat.getAllVehicles().add(veh);
        allVehicles.add(veh);

        exec.execute(() -> {
            while (!plat.isEmptyAt(1, 0)) {
                try {
                    synchronized (plat.getAt(1, 0)) {
                        plat.getAt(1, 0).wait();
                    }
                } catch (InterruptedException e) {
                    //write to error log
                }
            }
            plat.setAt(1, 0, veh);
            veh.start();
        });
    }

    public boolean isFinished() {
        for (Vehicle veh : allVehicles) {
            if (Platform.whereIsParking(veh.row, veh.column) == 0) {
                return false;
            }
        }
        return true;
    }

    public void refreshTextArea(TextArea x) {
        platforms.get(selectedPlatform).update();
        x.setText(output.toString());
    }

    public void callEmergency(Vehicle veh, Vehicle veh2) {
        Platform plat = emergencyPlatforms.get(0);
        Vehicle[] array = new Vehicle[]{
                new MedicalCar("Emergency Medical Car" + constructedCars++, "1ff0", "1880", "911"),
                new FirefighterVan("Emergency Firefighter Truck" + constructedCars++, "1ff0", "1880", "911"),
                new PoliceCar("Emergency Police Car" + constructedCars++, "1ff0", "1880", "911", null, 4, new File("C:\\Users" +
                        "\\Lamora\\Desktop\\wanted.txt"))
        };
        array[0].setPlatform(plat);
        array[1].setPlatform(plat);
        array[2].setPlatform(plat);
        ((MedicalCar) array[0]).setTarget(veh);
        ((FirefighterVan) array[1]).setTarget(veh);
        ((PoliceCar) array[2]).setTarget(veh, veh2);
        for (Vehicle iVeh : array) {
            iVeh.setPosition(1, 0, 0);
            iVeh.setPlatform(plat);
            plat.getAllVehicles().add(iVeh);
            allVehicles.add(iVeh);
            exec.execute(() -> {
                while (!plat.isEmptyAt(1, 0)) {
                    try {
                        synchronized (plat.getAt(1, 0)) {
                            plat.getAt(1, 0).wait();
                        }
                    } catch (InterruptedException e) {
                        UserGarageSimulator.errorLogger.log(Level.INFO, "Uuups. InterruptedException in callEmergency() method " +
                                "in Garage.java");
                    }
                }
                plat.setAt(1, 0, iVeh);
                iVeh.start();
            });
        }
    }

    public void writeBills() {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File("bills.txt"), true));
            for (ParkingBill pay : bills) {
                writer.println(pay.toString());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            UserGarageSimulator.errorLogger.log(Level.INFO, "FileNotFoundException in writeBills() method " +
                    "in Garage.java");
        }
    }

    public List<ParkingBill> getBills() {
        return bills;
    }

    public void notifyEveryone() {
        for(Platform p: platforms) {
            for(Vehicle veh: p.getAllVehicles())
                veh.notifyAll();
        }
    }

    public int getNumberOfParkedVehicles() {
        return numberOfParked;
    }

    public void incNumberOfParked() {
        numberOfParked++;
    }

    public void decNumberOfParked() {
        numberOfParked--;
    }
}
