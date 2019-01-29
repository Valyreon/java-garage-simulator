package net.etfbl.garage.application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.etfbl.garage.admin.AdminPartController;
import net.etfbl.garage.models.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class AdminGarageSimulator extends Application {
    public static final List<ObservableList<Vehicle>> platformList = new ArrayList<>();
    public final static Logger errorLogger = Logger.getLogger(AdminGarageSimulator.class.getName());
    public static int numberOfPlatforms = 5;
    private static boolean safeToRead;
    private static boolean finished;

    static void read() {
        FileInputStream fis = null;
        if (new File("garaza.ser").exists() && safeToRead) {
            try {
                fis = new FileInputStream("garaza.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                for (int i = 0; i < numberOfPlatforms; i++) {
                    Object obj = ois.readObject();
                    if (obj instanceof List) {
                        List<Vehicle> list = (List<Vehicle>) obj;
                        platformList.get(i).addAll(list);
                    } else {
                        throw new Exception("Wrong input object.");
                    }
                }
                fis.close();
            } catch (FileNotFoundException e) {
                errorLogger.log(Level.INFO, "FileNotFoundException in method read() in class AdminGarageSimulator.java");
            } catch (IOException e) {
                errorLogger.log(Level.INFO, "IOException in method read() in class AdminGarageSimulator.java");
            } catch (ClassNotFoundException x) {
                errorLogger.log(Level.INFO, "ClassNotFoundException in method read() in class AdminGarageSimulator.java");
            } catch (Exception e) {
                errorLogger.log(Level.INFO, e.getMessage());
            } finally {

            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void write() {
        try {
            PrintWriter writer = new PrintWriter("properties");
            writer.println(numberOfPlatforms);
            writer.close();
            // write object to file
            FileOutputStream fos = new FileOutputStream("garaza.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (int i = 0; i < numberOfPlatforms; i++) {
                List<Vehicle> list = new ArrayList<>(platformList.get(i));
                oos.writeObject(list);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            errorLogger.log(Level.INFO, "FileNotFoundException in method write() in class AdminGarageSimulator.java");
        } catch (IOException e) {
            errorLogger.log(Level.INFO, "IOException in method write() in class AdminGarageSimulator.java");
        }
    }

    public static void finish() {
        if (!finished) {
            write();
            for (Handler i : errorLogger.getHandlers()) {
                i.close();
            }
            finished = true;
        }
    }

    @Override
    public void start(Stage stage) {
        File pref = new File("properties");
        if (pref.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(pref))) { //try with resources
                String line = reader.readLine();
                line = line.replace(" ", "");
                if (line.matches("[0-9]+") || line.length() < 3) {
                    numberOfPlatforms = Integer.valueOf(line);
                    safeToRead = true;
                }
            } catch (FileNotFoundException e) {
                errorLogger.log(Level.INFO, "FileNotFoundException in method start() in class AdminGarageSimulator.java");
            } catch (IOException e) {
                errorLogger.log(Level.INFO, "IOException in method start() in class AdminGarageSimulator.java");
            }
        }
        for (int i = 0; i < numberOfPlatforms; i++) {
            platformList.add(FXCollections.observableArrayList());
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/etfbl/garage/admin/AdminPart.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("Garage Admin");
            loader.<AdminPartController>getController().getTable().setItems(platformList.get(0));

            FileHandler fh;

            try {
                fh = new FileHandler("error.log", true);
                errorLogger.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
            } catch (IOException | SecurityException e) {
                System.out.println(e.getMessage());
            }
            read();
            stage.show();
        } catch (IOException e) {
            errorLogger.log(Level.INFO, "IOException in method start() while loading AdminPart.fxml");
        }
    }

    @Override
    public void stop() {
        finish();
    }

}
