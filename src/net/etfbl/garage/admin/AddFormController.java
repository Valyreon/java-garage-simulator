package net.etfbl.garage.admin;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.garage.models.Car;
import net.etfbl.garage.models.Motorbike;
import net.etfbl.garage.models.Van;
import net.etfbl.garage.models.Vehicle;
import net.etfbl.garage.models.departments.firefighters.FirefighterVan;
import net.etfbl.garage.models.departments.medical.MedicalCar;
import net.etfbl.garage.models.departments.medical.MedicalVan;
import net.etfbl.garage.models.departments.police.PoliceCar;
import net.etfbl.garage.models.departments.police.PoliceMotorbike;
import net.etfbl.garage.models.departments.police.PoliceVan;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static net.etfbl.garage.application.AdminGarageSimulator.platformList;

public class AddFormController implements Initializable {

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
    private Button addButton;
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
    private CheckBox rotationCheck;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        departmentCombo.valueProperty().addListener(new ChangeListener<String>() {
        @Override public void changed(ObservableValue<? extends String> ov, String old, String newValue) {
            if(old!=null) {
                if("Police".equals(old)) {
                    AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox) - 46);
                    wantedHbox.setVisible(false);
                    stage.setHeight(stage.getHeight() - 46);
                    wantedList = null;
                } else if("Firefighters".equals(old) || "Medical".equals(old)) {
                }
            }
            
            if("Police".equals(newValue)) {
                AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox) + 46);
                wantedHbox.setVisible(true);
                stage.setHeight(stage.getHeight() + 46);
            } else if ("Firefighters".equals(newValue) || "Medical".equals(newValue)) {
            } 
        }    
        });
        
        for(TextField i: new TextField[] {nameField, registrationField, chassisField, engineField}) {
            i.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(!newValue.matches("[a-zA-Z0-9 /-]*") || newValue.length()>15) {
                        ((StringProperty)observable).setValue(oldValue);
                    }
                }
            );
        }
        
        for(TextField i: new TextField[] { doorsField, carryField}) {
            i.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(!newValue.matches("[0-9]*") || newValue.length()>10) {
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
        addButton.disableProperty().bind(motorBind);
    }

    @FXML
    private void handleChooseButtonAction() {
        final FileChooser fileChooser = new FileChooser();
        chosenPhoto = fileChooser.showOpenDialog(stage.getScene().getWindow());
        if(null!=chosenPhoto) {
            chooseButton.setText(chosenPhoto.getName());
        }
    }

    @FXML
    private void handleAddButtonAction() {
        String dept = departmentCombo.getValue();
        Vehicle toAdd = null;
        if ("Police".equals(dept)) {
            switch (typeOfVehicle) {
                case "Car":
                    toAdd = new PoliceCar(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto,
                            Integer.valueOf(doorsField.getText().equals("") ? "0" : doorsField.getText()),
                            wantedList);
                    //((PoliceCar) toAdd).setRotation(rotationCheck.isSelected());
                    break;
                case "Motorbike":
                    toAdd = new PoliceMotorbike(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto,
                            wantedList);
                    //((PoliceMotorbike) toAdd).setRotation(rotationCheck.isSelected());
                    break;
                case "Van":
                    toAdd = new PoliceVan(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto,
                            Integer.valueOf(carryField.getText().equals("") ? "0" : carryField.getText()),
                            wantedList);
                    //((PoliceVan) toAdd).setRotation(rotationCheck.isSelected());
                    break;
                default:
                    break;
            }
        } else if ("Medical".equals(dept)) {
            switch (typeOfVehicle) {
                case "Car":
                    toAdd = new MedicalCar(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto,
                            Integer.valueOf(doorsField.getText().equals("") ? "0" : doorsField.getText()));
                    //((MedicalCar) toAdd).setRotation(rotationCheck.isSelected());
                    break;
                case "Van":
                    toAdd = new MedicalVan(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto,
                            Integer.valueOf(carryField.getText().equals("") ? "0" : carryField.getText()));
                    //((MedicalVan) toAdd).setRotation(rotationCheck.isSelected());
                    break;
                default:
                    break;
            }
        } else if ("Firefighters".equals(dept)) {
            if ("Van".equals(typeOfVehicle)) {
                toAdd = new FirefighterVan(
                        nameField.getText(),
                        chassisField.getText(),
                        engineField.getText(),
                        registrationField.getText(),
                        chosenPhoto,
                        Integer.valueOf(carryField.getText().equals("") ? "0" : carryField.getText()));
                //((FirefighterVan) toAdd).setRotation(rotationCheck.isSelected());
            }
        } else {
            switch (typeOfVehicle) {
                case "Car":
                    toAdd = new Car(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto,
                            Integer.valueOf(doorsField.getText().equals("") ? "0" : doorsField.getText()));
                    break;
                case "Van":
                    toAdd = new Van(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto,
                            Integer.valueOf(carryField.getText().equals("") ? "0" : carryField.getText()));
                    break;
                case "Motorbike":
                    toAdd = new Motorbike(
                            nameField.getText(),
                            chassisField.getText(),
                            engineField.getText(),
                            registrationField.getText(),
                            chosenPhoto);
                    break;
                default:
                    break;
            }
        }
        if (toAdd != null) {
            platformList.get(platformNumber).add(toAdd);
        }
        stage.close();
    }

    @FXML
    private void handleCancelButtonAction() {
        ((Stage)cancelButton.getScene().getWindow()).close();
    }
    
    public void setType(String type, Stage stage, int platformNumber) {
        typeOfVehicle = type;
        this.stage = stage;
        this.platformNumber = platformNumber;
        switch(type) {
            case "Motorbike":
                departmentCombo.setItems(FXCollections.observableArrayList("None", "Police"));
                AnchorPane.setTopAnchor(wantedHbox, AnchorPane.getTopAnchor(wantedHbox)-36);
                break;
            case "Van":
            case "Car":
                departmentCombo.setItems("Van".equals(type) ? FXCollections.observableArrayList("None", "Police", "Firefighters", "Medical") : FXCollections.observableArrayList("None", "Police", "Medical"));
                AnchorPane.setTopAnchor(buttonsHBox, AnchorPane.getTopAnchor(buttonsHBox)+42);
                ("Van".equals(type) ? carryGrid : doorsGrid).setVisible(true);
                anchorPane.setPrefHeight(anchorPane.getPrefHeight()+42);
                break;
            default:
                break;
        }
        departmentCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleWantedButtonAction() {
        final FileChooser fileChooser = new FileChooser();
        wantedList = fileChooser.showOpenDialog(stage.getScene().getWindow());
        wantedButton.setText(wantedList.getName());
    }
}
