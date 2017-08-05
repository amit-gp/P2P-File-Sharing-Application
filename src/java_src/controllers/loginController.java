package java_src.controllers;

import java_src.main_src.P2P_Client;
import java_src.main_src.PeerConnectionDetails;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Amit Kumar on 01-08-2017.
 */
public class loginController implements Initializable
{
    private File filePath;
    @FXML private Button chooseFileButton;
    @FXML private Text fileChosenText;
    @FXML private TextField chooseIPTextField;
    @FXML private TextField enterNameTextField;
    @FXML private Text IPChosenText;
    @FXML private Button sendButton;
    @FXML private ListView<PeerConnectionDetails> sentFilesListView;
    private static ObservableList<PeerConnectionDetails> sentFilesObservableList;

    public loginController()
    {
        sentFilesObservableList = FXCollections.observableArrayList();

        /*
        //Pseudo Peer connections to test the observable list.:

        sentFilesObservableList.addAll(
                new PeerConnectionDetails("Arvind", "SomeHentai.exe"),
                new PeerConnectionDetails("Ataa", "SomeMuslimThang.pdf")
        );
        */
    }

    public static void updateObservalbeList(PeerConnectionDetails peerConnectionDetails)
    {
        sentFilesObservableList.addAll(peerConnectionDetails);
    }

    @FXML private void onChooseFileButtonAction()
    {
        FileChooser fileChooser = new FileChooser();
        filePath = fileChooser.showOpenDialog(new Stage());
        System.out.println("The file was chosen: " + filePath);
        fileChosenText.setText(filePath.toString().substring(0, 30) + "...");
    }

    public void initialize(URL location, ResourceBundle resources)
    {
        sentFilesListView.setItems(sentFilesObservableList);
        sentFilesListView.setCellFactory(peerConnectionListView -> new PeerConnectionDetailsListViewCell());
        setChooseIPTextField(IPTextFeildStates.INCORRECT);
        P2P_Client.startListening();
    }

    @FXML private void onChooseIPTextFieldKeyPressed()
    {
        if(chooseIPTextField.getText().length() >= 8) {
            setChooseIPTextField(IPTextFeildStates.CHECKING);
        }
        else {
            setChooseIPTextField(IPTextFeildStates.INCORRECT);
        }
        chooseIPTextField.textProperty().addListener(new ChangeListener<String>()
        {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!newValue.matches("^\\d+(\\.\\d+)?")) {
                    chooseIPTextField.setText(newValue.replaceAll("[a-z]", ""));
                }
            }
        });

    }

    @FXML private void onSendButtonAction()
    {
        if(chooseIPTextField.getText().equals(""))
            return;
        //sendButton.setDisable(true);
        P2P_Client.sendFile(filePath, chooseIPTextField.getText(), enterNameTextField.getText());
    }

    private enum IPTextFeildStates
    {
        INCORRECT, CHECKING, ONLINE
    }

    private void setChooseIPTextField(IPTextFeildStates ipState)
    {
        switch (ipState)
        {
            case ONLINE:
                IPChosenText.setText("USer is online !!!");
                IPChosenText.setFill(Color.DARKGREEN);
                break;

            case CHECKING:
                IPChosenText.setText("Checking if user is online..");
                IPChosenText.setFill(Color.DARKBLUE);
                break;

            case INCORRECT:
                IPChosenText.setText("Please enter a valid IP");
                IPChosenText.setFill(Color.RED);
                break;
        }
    }
}
