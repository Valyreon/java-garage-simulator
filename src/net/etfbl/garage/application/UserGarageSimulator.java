package net.etfbl.garage.application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import net.etfbl.garage.models.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class UserGarageSimulator extends Application {

    public static final List<List<Vehicle>> platformList = new ArrayList<>();
    public final static Logger errorLogger = Logger.getLogger(UserGarageSimulator.class.getName());
    public static int numberOfPlatforms = 5;

    public static void read() {
        boolean safeToRead = false;
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
        FileInputStream fis = null;
        if (new File("garaza.ser").exists() && safeToRead) {
            try {
                fis = new FileInputStream("garaza.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                for (int i = 0; i < numberOfPlatforms; i++) {
                    Object obj = ois.readObject();
                    if (obj instanceof List) {
                        List<Vehicle> list = (List<Vehicle>) obj;
                        platformList.add(new ArrayList<>());
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
        } else {
            for (int i = 0; i < numberOfPlatforms; i++) {
                platformList.add(new ArrayList<>());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void write() {
        try {
            // write object to file
            FileOutputStream fos = new FileOutputStream("garaza.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (int i = 0; i < numberOfPlatforms; i++) {
                oos.writeObject(platformList.get(i));
            }
            fos.close();
        } catch (FileNotFoundException e) {
            errorLogger.log(Level.INFO, "FileNotFoundException in method write() in class AdminGarageSimulator.java");
        } catch (IOException e) {
            errorLogger.log(Level.INFO, "IOException in method write() in class AdminGarageSimulator.java");
        }
    }

    @Override
    public void start(Stage stage) {
        FileHandler fh;

        try {
            fh = new FileHandler("error.log", true);
            errorLogger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException | SecurityException e) {
            System.out.println(e.getMessage());
        }

        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent window) {
                try {
                    FXMLLoader dloader = new FXMLLoader(getClass().getResource("/net/etfbl/garage/user/NumberDialog.fxml"));
                    Stage stageDial = setUpDialogWindow(dloader, "Enter minimal ", stage.getScene().getWindow());
                    stageDial.show();
                } catch (IOException e) {
                    UserGarageSimulator.errorLogger.log(Level.INFO, "IOException while opening window for setting minimal " +
                            "number.");
                }
            }
        });
        try {
            read();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/etfbl/garage/user/UserPart.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("User Module");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            errorLogger.log(Level.INFO, "IOException in method start() while loading AdminPart.fxml");
        }
    }

    public static void finish() {
        write();
        for (Handler i : errorLogger.getHandlers()) {
            i.close();
        }
    }

    private Stage setUpDialogWindow(FXMLLoader loader, String title, Window owner) throws IOException {
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle(title);
        if (owner != null) {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
        }
        stage.setResizable(false);
        return stage;
    }
}
