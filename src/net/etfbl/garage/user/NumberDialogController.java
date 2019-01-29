package net.etfbl.garage.user;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static net.etfbl.garage.user.UserPartController.initGarage;

public class NumberDialogController implements Initializable {
    @FXML
    public Button submitButton;
    @FXML
    public TextField numField;
    @FXML
    public AnchorPane anchor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            numField.textProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (!newValue.matches("[0-9]*") || (!newValue.isEmpty() && Integer.valueOf(newValue) > 36)) {
                            ((StringProperty)observable).setValue(oldValue);
                        }
                    }
            );

        BooleanBinding bind = numField.textProperty().isEmpty();
        submitButton.disableProperty().bind(bind);

    }

    public void handleSubmitButtonAction(ActionEvent actionEvent) {
        int min;
        if(!numField.getText().isEmpty()) {
            min = Integer.valueOf(numField.getText());
            initGarage(min);
            ((Stage)(numField.getScene().getWindow())).close();
        }

    }
}
