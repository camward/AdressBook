package controller;

import interfaces.impl.CollectionAdressBook;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import objects.Person;

import java.io.IOException;

public class MainController {

    private CollectionAdressBook addressBookImpl = new CollectionAdressBook();

    private Stage mainStage;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSrch;

    @FXML
    private TextField txtSrch;

    @FXML
    private Label lblCount;

    @FXML
    private TableView tabBook;

    @FXML
    private TableColumn<Person, String> columnFIO;

    @FXML
    private TableColumn<Person, String> columnPhone;

    private Parent fxmEdit;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private EditController editController;
    private Stage editDialogStage;

    public void setMainStage(Stage mainStage){
        this.mainStage = mainStage;
    }

    @FXML
    private void initialize(){
        // для множественного выбора записей
        // tabBook.getSelectionModel().getSelectionMode(SelectionMode.MULTIPLE);

        columnFIO.setCellValueFactory(new PropertyValueFactory<Person, String>("fio"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<Person, String>("phone"));

        initListeners();

        addressBookImpl.fillTestData();
        tabBook.setItems(addressBookImpl.getPersonList());

        initLoader();
    }

    private void initListeners(){
        addressBookImpl.getPersonList().addListener(new ListChangeListener<Person>() {
            @Override
            public void onChanged(Change<? extends Person> c) {
                updateCountLabel();
            }
        });

        tabBook.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2){
                    editController.setPerson((Person) tabBook.getSelectionModel().getSelectedItem());
                    dataEdit();
                }
            }
        });
    }

    private void initLoader(){
        try {
            fxmlLoader.setLocation(getClass().getResource("../fxml/edit.fxml"));
            fxmEdit = fxmlLoader.load();
            editController = fxmlLoader.getController();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void updateCountLabel(){
        lblCount.setText("Количество записей: " + addressBookImpl.getPersonList().size());
    }

    public void showDialog(ActionEvent actionEvent){

        Object source = actionEvent.getSource();

        // если нажата не кнопка, то выходим из метода
        if(!(source instanceof Button)){
            return;
        }

        Button clickedButton = (Button)source;

        switch (clickedButton.getId()){
            case "btnAdd":
                editController.setPerson(new Person());
                dataEdit();
                addressBookImpl.add(editController.getPerson());
                break;

            case "btnEdit":
                editController.setPerson((Person) tabBook.getSelectionModel().getSelectedItem());
                dataEdit();
                break;

            case "btnDelete":
                addressBookImpl.delete((Person) tabBook.getSelectionModel().getSelectedItem());
                break;
        }
    }

    private void dataEdit(){
        if(editDialogStage == null){
            editDialogStage = new Stage();
            editDialogStage.setTitle("Редактирование записи");
            editDialogStage.setMinWidth(300);
            editDialogStage.setMinHeight(150);
            editDialogStage.setResizable(false);
            editDialogStage.setScene(new Scene(fxmEdit));
            editDialogStage.initModality(Modality.WINDOW_MODAL);
            editDialogStage.initOwner(mainStage);
        }

        editDialogStage.showAndWait();
    }

}