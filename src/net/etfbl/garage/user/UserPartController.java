package net.etfbl.garage.user;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.etfbl.garage.application.UserGarageSimulator;
import net.etfbl.garage.models.Garage;
import net.etfbl.garage.models.Vehicle;
import net.etfbl.garage.models.reports.ParkingBill;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static net.etfbl.garage.application.UserGarageSimulator.*;
import static net.etfbl.garage.models.Vehicle.stopAll;

public class UserPartController implements Initializable {

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static Garage garage = null;
    private static boolean notFinished = true;

    @FXML
    public TextArea matrixOutput;
    @FXML
    public Button startButton;
    @FXML
    public ComboBox platformCombo;
    @FXML
    public Button addButton;

    public static void finish() {
        notFinished = false;
    }

    public static void initGarage(int minimum) {
        garage = new Garage(platformList, numberOfPlatforms, minimum);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        ArrayList<String> helplist = new ArrayList<>();
        for (int i = 0; i < numberOfPlatforms; i++) {
            helplist.add(Integer.toString(i));
        }
        platformCombo.setItems(FXCollections.observableArrayList(helplist));
        platformCombo.getSelectionModel().selectFirst();

        platformCombo.valueProperty().addListener((ov, old, newValue) -> {
            garage.setSelectedPlatform(Integer.valueOf((String) newValue));
            garage.refreshTextArea(matrixOutput);
        });
    }

    private Stage setUpDialogWindow(FXMLLoader loader, String title, Window owner) throws IOException {
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle(title);
        if(owner!=null) {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
        }
        stage.setResizable(false);
        return stage;
    }

    public void handleStartButtonAction(ActionEvent actionEvent) {
        if (notFinished) {
            if (garage == null) {
                initGarage(10);
            }
            garage.startLeaving();
            addButton.setDisable(false);
            executor.execute(() -> {
                while (notFinished) {
                    garage.refreshTextArea(matrixOutput);
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        System.out.println("Someone interrupted me. It's okay.");
                    }
                }
                garage.refreshTextArea(matrixOutput);
            });
            executor.execute(() -> {
                while (notFinished) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (garage.isFinished()) {
                        notFinished = false;
                    }
                }
                stopAll();
                //garage.notifyEveryone();
                addButton.setDisable(true);
                Platform.runLater(() -> startButton.setText("Export to CSV"));
                startButton.setDisable(false);
                garage.writeBills();
                garage.getExecutor().shutdownNow();
                executor.shutdownNow();
                garage.refreshTextArea(matrixOutput);
                for(int i=0; i<platformList.size() ; i++) {
                    platformList.get(i).clear();
                    for (Vehicle veh : garage.getPlatform(i).getAllVehicles()) {
                        if (net.etfbl.garage.models.Platform.isItParkingAt(veh.getRow(), veh.getColumn()))
                            platformList.get(i).add(veh);
                    }
                }
                UserGarageSimulator.finish();

            });
            startButton.setDisable(true);
        } else {
            final FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            File saveFile = fileChooser.showSaveDialog(startButton.getScene().getWindow());
            if (saveFile != null)
                writeExcel(saveFile.getAbsolutePath());
        }
    }

    private void writeExcel(String path) {
        try {
            Writer writer = null;
            try {
                File file = new File(path);
                writer = new BufferedWriter(new FileWriter(file));
                DateFormat simpleTimeFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
                simpleTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                writer.write("Name,Registration,Enter time,Exit time,Price(KM)\n");
                for (ParkingBill bill : garage.getBills()) {
                    Date enter = new Date(bill.getEnterTime());
                    Date exit = new Date(bill.getExitTime());
                    String text =
                            bill.getVehicleName() + "," + bill.getRegistration() + "," + simpleTimeFormat.format(enter)
                                    + "," + simpleTimeFormat.format(exit) + "," + bill.getPrice() + "\n";
                    writer.write(text);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            errorLogger.log(Level.INFO, "Exception while generating CSV file.");
        }
    }

    public void handleAddButtonAction(ActionEvent actionEvent) {
        if (garage.getAllVehicles().size() < garage.getNumberOfPlatforms() * 28)
            garage.enterNewVehicle();
    }


}
