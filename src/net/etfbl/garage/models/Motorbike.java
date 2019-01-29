package net.etfbl.garage.models;

import javafx.beans.property.SimpleStringProperty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Motorbike extends Vehicle {


    public Motorbike(String name, String chassisNumber, String engineNumber, String registration, File photo) {
        super(name,chassisNumber,engineNumber,registration);
        this.photo = photo;
        notFinished = true;
    }

    public Motorbike(String name, String chassisNumber, String engineNumber, String registration) {
        super(name,chassisNumber,engineNumber,registration);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        //out.defaultWriteObject();
        List<Object> obj = new ArrayList<>();
        obj.add(vehicleName.get());
        obj.add(chassisNumber.get());
        obj.add(engineNumber.get());
        obj.add(registration.get());
        obj.add(photo);
        obj.add(enteredTime);
        out.writeObject(obj);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        //in.defaultReadObject();
        List<Object> obj = (List<Object>)in.readObject();
        vehicleName = new SimpleStringProperty((String)obj.get(0));
        chassisNumber = new SimpleStringProperty((String)obj.get(1));
        engineNumber = new SimpleStringProperty((String)obj.get(2));
        registration = new SimpleStringProperty((String)obj.get(3));
        photo = (File) obj.get(4);
        enteredTime = (long)obj.get(5);
    }
}


