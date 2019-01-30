package net.etfbl.garage.admin;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.etfbl.garage.application.AdminGarageSimulator;
import net.etfbl.garage.application.UserGarageSimulator;
import net.etfbl.garage.models.Vehicle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class AdminPartController implements Initializable {

    @FXML
    private TableView<Vehicle> table;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button startButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ComboBox<String> platformChoose;
    
    public TableView<Vehicle> getTable() {
        return table;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table.getColumns().forEach((column) -> column.prefWidthProperty().bind(table.widthProperty().multiply(0.248888)));
        table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("vehicleName"));
        table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("chassisNumber"));
        table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("engineNumber"));
        table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("registration"));
        
        typeCombo.setItems(FXCollections.observableArrayList(
                "Car","Motorbike","Van"
        ));
        ArrayList<String> helplist = new ArrayList<>();
        for (int i = 0; i < AdminGarageSimulator.getNumberOfPlatforms(); i++) {
            helplist.add(Integer.toString(i));
        }
        platformChoose.setItems(FXCollections.observableArrayList(helplist));
        platformChoose.getSelectionModel().selectFirst();
        
        editButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));

        platformChoose.valueProperty().addListener((ov, old, newValue) -> table.setItems(AdminGarageSimulator.getPlatformList().get(Integer.valueOf(newValue))));
    }

    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        try {
            if(typeCombo.getValue() == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddForm.fxml"));
            Stage stage = setUpDialogWindow(loader, "Add "+typeCombo.getValue(), ((Node)event.getSource()).getScene().getWindow());
            loader.<AddFormController>getController().setType(typeCombo.getValue(), stage, Integer.valueOf(platformChoose.getValue()));
            stage.show();
        } catch(IOException e) {
            AdminGarageSimulator.logError(Level.INFO, "User opened file dialog but did not choose file.");
        }
    }

    @FXML
    private void handleEditButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditForm.fxml"));
            Stage stage = setUpDialogWindow(loader, "Edit item", ((Node) event.getSource()).getScene().getWindow());
            loader.<EditFormController>getController().setFields(table.getSelectionModel().getSelectedItem(), stage);
            stage.show();
        } catch (IOException e) {
            AdminGarageSimulator.logError(Level.INFO, "IOException while handling action on Edit Button in " +
                    "AdminPartController.class");
        }
    }

    @FXML
    private void handleDeleteButtonAction() {
        Vehicle item = table.getSelectionModel().getSelectedItem();
        if(item != null) {
            AdminGarageSimulator.getPlatformList().get(Integer.valueOf(platformChoose.getValue())).remove(item);
        }
    }

    @FXML
    private void handleStartButtonAction() {
        AdminGarageSimulator.finish();
        UserGarageSimulator userPart = new UserGarageSimulator();
        userPart.start(new Stage());
        ((Stage) (table.getScene().getWindow())).close();
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
}
