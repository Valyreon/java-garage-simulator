package net.etfbl.garage.admin;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.garage.models.Car;
import net.etfbl.garage.models.Motorbike;
import net.etfbl.garage.models.Van;
import net.etfbl.garage.models.Vehicle;
import net.etfbl.garage.models.departments.firefighters.Firefighter;
import net.etfbl.garage.models.departments.medical.Medical;
import net.etfbl.garage.models.departments.police.Police;
import net.etfbl.garage.models.departments.police.PoliceCar;
import net.etfbl.garage.models.departments.police.PoliceMotorbike;
import net.etfbl.garage.models.departments.police.PoliceVan;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EditFormController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField chassisField;
    @FXML
    private TextField engineField;
    @FXML
    private TextField registrationField;
    @FXML
    private TextField doorsField;
    @FXML
    private Button chooseButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label doorsLabel;
    @FXML
    private Label carryLabel;
    @FXML
    private TextField carryField;
    @FXML
    private HBox buttonsHBox;
    @FXML
    private Button wantedButton;
    @FXML
    private HBox wantedHbox;
    @FXML
    private ComboBox<String> departmentCombo;
    @FXML
    private GridPane doorsGrid;
    @FXML
    private GridPane carryGrid;
    @FXML
    private AnchorPane anchorPane;
    
    String typeOfVehicle;
    File chosenPhoto = null;
    File wantedList = null;
    Stage stage;
    int platformNumber;
    Vehicle toEdit;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        departmentCombo.valueProperty().addListener(new ChangeListener<String>() {
        @Override public void changed(ObservableValue<? extends String> ov, String old, String newValue) {
            if(old!=null) {
                if("Police".equals(old)) {
                    AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)-76);
                    wantedHbox.setVisible(false);
                    stage.setHeight(stage.getHeight()-76);
                    wantedList=null;
                } else if("Firefighters".equals(old) || "Medical".equals(old)) {
                    AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)-30);
                    stage.setHeight(stage.getHeight()-30);
                }
            }
            
            if("Police".equals(newValue)) {
                AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)+76);
                wantedHbox.setVisible(true);
                stage.setHeight(stage.getHeight()+76);
            } else if ("Firefighters".equals(newValue) || "Medical".equals(newValue)) {
                AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)+30);
                stage.setHeight(stage.getHeight()+30);
            } 
        }
        });
        
        for(TextField i: new TextField[] {nameField, registrationField, chassisField, engineField,}) {
            i.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.matches("[a-zA-Z0-9 -]*") || newValue.length() > 23) {
                        ((StringProperty)observable).setValue(oldValue);
                    }
                }
            );
        }
        
        for(TextField i: new TextField[] { doorsField, carryField}) {
            i.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.matches("[0-9]*") || newValue.length() > 2) {
                        ((StringProperty)observable).setValue(oldValue);
                    }
                }
            );
        }
        
        BooleanBinding motorBind = 
                        nameField.textProperty().isEmpty()
                        .or(engineField.textProperty().isEmpty())
                        .or(chassisField.textProperty().isEmpty())
                        .or(registrationField.textProperty().isEmpty());
        saveButton.disableProperty().bind(motorBind);
    }
    
    public void setFields(Vehicle veh, Stage stage) {
        this.stage = stage;
        this.toEdit = veh;
        nameField.setText(veh.getVehicleName());
        chassisField.setText(veh.getChassisNumber());
        engineField.setText(veh.getEngineNumber());
        registrationField.setText(veh.getRegistration());
        
        if(veh.getPhoto()!=null) {
            chooseButton.setText(veh.getPhoto().getName());
        }
        
        if(veh instanceof Motorbike) {
            Motorbike mot = (Motorbike)veh;
            departmentCombo.setItems(FXCollections.observableArrayList("None", "Police"));
            departmentCombo.getSelectionModel().select(mot.getDepartmentString());
            AnchorPane.setTopAnchor(wantedHbox, AnchorPane.getTopAnchor(wantedHbox)-36);
            if (mot instanceof Police) {
                PoliceMotorbike polMot = (PoliceMotorbike) mot;
                wantedHbox.setVisible(true);
                anchorPane.setPrefHeight(anchorPane.getPrefHeight() + wantedHbox.getPrefHeight());
                AnchorPane.setTopAnchor(wantedHbox, AnchorPane.getTopAnchor(wantedHbox)-30); // ADDED
                AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)-30); //ADDED
                if (polMot.getWantedList() != null) {
                    wantedButton.setText(polMot.getWantedList().getName());
                }
            }
        } else if(veh instanceof Van) {
            Van van = (Van)veh;
            departmentCombo.setItems(FXCollections.observableArrayList("None", "Police", "Firefighters", "Medical"));
            departmentCombo.getSelectionModel().select(van.getDepartmentString());
            AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)+42);
            carryGrid.setVisible(true);
            carryField.setText(Integer.toString(van.getCarryWeight()));
            anchorPane.setPrefHeight(anchorPane.getPrefHeight() + carryGrid.getPrefHeight());
            if (van instanceof Police) {
                PoliceVan polVan = (PoliceVan) van;
                wantedHbox.setVisible(true);
                anchorPane.setPrefHeight(anchorPane.getPrefHeight() + wantedHbox.getPrefHeight());
                AnchorPane.setTopAnchor(wantedHbox, AnchorPane.getTopAnchor(wantedHbox)-30); // ADDED
                AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)-30); //ADDED
                if (polVan.getWantedList() != null) {
                    wantedButton.setText(polVan.getWantedList().getName());
                }
            } else if (van instanceof Medical || van instanceof Firefighter) {
                anchorPane.setPrefHeight(anchorPane.getPrefHeight() + carryGrid.getPrefHeight());
            }
        } else if(veh instanceof Car) {
            Car car = (Car)veh;
            departmentCombo.setItems(FXCollections.observableArrayList("None", "Police", "Medical"));
            departmentCombo.getSelectionModel().select(car.getDepartmentString());
            AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)+42);
            doorsGrid.setVisible(true);
            doorsField.setText(Integer.toString(car.getNumberOfDoors()));
            anchorPane.setPrefHeight(anchorPane.getPrefHeight() + doorsGrid.getPrefHeight());
            if (car instanceof Police) {
                PoliceCar polCar = (PoliceCar) car;
                wantedHbox.setVisible(true);
                anchorPane.setPrefHeight(anchorPane.getPrefHeight() + wantedHbox.getPrefHeight());
                if (polCar.getWantedList() != null) {
                    wantedButton.setText(polCar.getWantedList().getName());
                }
                AnchorPane.setTopAnchor(wantedHbox, AnchorPane.getTopAnchor(wantedHbox)-30); // ADDED
                AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)-30); //ADDED
            } else if (car instanceof Medical || car instanceof Firefighter) {
                anchorPane.setPrefHeight(anchorPane.getPrefHeight() + doorsGrid.getPrefHeight());
            }
        }
        departmentCombo.setDisable(true);
    }

    @FXML
    private void handleChooseButtonAction() {
        final FileChooser fileChooser = new FileChooser();
        chosenPhoto = fileChooser.showOpenDialog(stage.getScene().getWindow());
        if(chosenPhoto!=null) {
            chooseButton.setText(chosenPhoto.getName());
        } else {
            chooseButton.setText("Choose...");
        }
    }
    
    @FXML
    private void handleWantedButtonAction() {
        final FileChooser fileChooser = new FileChooser();
        wantedList = fileChooser.showOpenDialog(stage.getScene().getWindow());
        if(wantedList!=null) {
            wantedButton.setText(wantedList.getName());
        } else {
            wantedButton.setText("Wanted List...");
        }
    }
    
    @FXML
    private void handleCancelButtonAction() {
        ((Stage)cancelButton.getScene().getWindow()).close();
    }
    
    @FXML
    private void handleSaveButtonAction() {
        toEdit.setVehicleName(nameField.getText());
        toEdit.setEngineNumber(engineField.getText());
        toEdit.setChassisNumber(chassisField.getText());
        toEdit.setRegistration(registrationField.getText());

        if (toEdit instanceof Van) {
            Van vanEdit = (Van)toEdit;
            vanEdit.setCarryWeight(Integer.valueOf(carryField.getText().equals("") ? "0" : carryField.getText()));
        } else if(toEdit instanceof Car) {
            Car carEdit = (Car)toEdit;
            carEdit.setNumberOfDoors(Integer.valueOf( doorsField.getText().equals("") ? "0" : doorsField.getText()));
        }
        stage.close();
    }
    
}
