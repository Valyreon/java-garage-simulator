package net.etfbl.garage.models;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Van extends Vehicle {
    protected int carryWeight;

    public Van(String name, String chassisNumber, String engineNumber, String registration, File photo, int carryWeight) {
        super(name, chassisNumber, engineNumber, registration);
        this.photo = photo;
        this.carryWeight = carryWeight;
    }

    public Van(String name, String chassisNumber, String engineNumber, String registration) {
        super(name, chassisNumber, engineNumber, registration);
    }

    public int getCarryWeight() {
        return carryWeight;
    }

    public void setCarryWeight(int carryWeight) {
        this.carryWeight = carryWeight;
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
        obj.add(enteredTime);
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
        enteredTime = (long)obj.get(6);
    }

}
