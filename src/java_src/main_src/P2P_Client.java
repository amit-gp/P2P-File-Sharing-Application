package java_src.main_src;

import java_src.controllers.PeerConnectionDetailsListViewCell;
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
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Amit Kumar on 01-08-2017.
 */
public class P2P_Client implements AcceptSignalListener.CallBack, Initializable, FileSender.CallBack
{
    private static final int PORT = 8080;
    private static Socket sendingSocket; //Used while sending a file to another peer and then closed.
    private static ServerSocket serverSocket; // Always listening for an incoming connection.
    private static ConnectionListener connectionListener;
    private static OutputStream outputStream;
    private static DataOutputStream dataOutputStream;
    private static ObservableList<PeerConnectionDetails> sentFilesObservableList;
    private File filePath;
    @FXML private Button chooseFileButton;
    @FXML private Text fileChosenText;
    @FXML private TextField chooseIPTextField;
    @FXML private TextField enterNameTextField;
    @FXML private Text IPChosenText;
    @FXML private Button sendButton;
    @FXML private ListView<PeerConnectionDetails> sentFilesListView;
    public P2P_Client()
    {
        sentFilesObservableList = FXCollections.observableArrayList();
    }

    public static void updateObservalbeList(PeerConnectionDetails peerConnectionDetails)
    {
        sentFilesObservableList.addAll(peerConnectionDetails);
    }

    @Override
    public void fileSent()
    {
        System.out.println("The file was sent !!");
        enableSendElements();
        setChooseIPTextField(IPTextFeildStates.DONE);

    }

    @FXML
    private void onChooseIPTextFieldMouseClicked()
    {
        setChooseIPTextField(IPTextFeildStates.INCORRECT);
    }

    @Override
    public void requestAccepted()
    {
        //System.out.println("Callback !!");

        setChooseIPTextField(IPTextFeildStates.ACCEPTED_SENDING);
    }

    public void startListening()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
            connectionListener = new ConnectionListener(serverSocket, PORT);
            startThreads();
        }catch (Exception e){e.printStackTrace(); connectionListener = new ConnectionListener(serverSocket, PORT);
            startThreads();}
    }

    public  void sendFile(File file, String IPaddress, String peerName)
    {
        try
        {
            sendingSocket = new Socket(IPaddress, PORT);
            outputStream = sendingSocket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            JSONObject peerDetailsJsonObject = new JSONObject();
            peerDetailsJsonObject.put("peerDetails", "peerDetails").put("peerName", peerName).put("fileName", file.getName()).put("fileSize", (file.length()/ 1024));
            //First sending login info to server
            dataOutputStream.writeUTF(peerDetailsJsonObject.toString());

            AcceptSignalListener signalListener = new AcceptSignalListener(sendingSocket,this );
            new Thread(signalListener).start();
            waitForUserAccept();

            FileSender fileSender = new FileSender(sendingSocket, file, this);
            new Thread(fileSender).start();

        }catch (Exception e){e.printStackTrace();}
    }

    private void startThreads()
    {
        new Thread(connectionListener).start();
    }

    @FXML private void onChooseFileButtonAction()
    {
        FileChooser fileChooser = new FileChooser();
        filePath = fileChooser.showOpenDialog(new Stage());
        System.out.println("The file was chosen: " + filePath);
        fileChosenText.setText(filePath.getName());
    }

    public void initialize(URL location, ResourceBundle resources)
    {
        sentFilesListView.setItems(sentFilesObservableList);
        sentFilesListView.setCellFactory(peerConnectionListView -> new PeerConnectionDetailsListViewCell());
        //setChooseIPTextField(IPTextFeildStates.INCORRECT);
        startListening();
    }

    @FXML private void onChooseIPTextFieldKeyPressed()
    {
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
        sendFile(filePath, chooseIPTextField.getText(), enterNameTextField.getText());
    }

    private void setChooseIPTextField(IPTextFeildStates ipState)
    {
        switch (ipState)
        {
            case INCORRECT:
                IPChosenText.setText("Enter a valid IP here.");
                IPChosenText.setFill(Color.RED);
                break;

            case AWAITING:
                IPChosenText.setText("Awaitng user conformation ......");
                IPChosenText.setFill(Color.RED);
                break;

            case ACCEPTED_SENDING:
                IPChosenText.setText("User accepted !! Sending file\nPlease wait...");
                IPChosenText.setFill(Color.AQUA);
                break;

            case DONE:
                IPChosenText.setText("File Sent");
                IPChosenText.setFill(Color.AQUA);
        }
    }

    public void waitForUserAccept()
    {
        disableSendElements();
        setChooseIPTextField(IPTextFeildStates.AWAITING);
    }

    public void disableSendElements()
    {
        sendButton.setDisable(true);
        chooseIPTextField.setDisable(true);
        chooseFileButton.setDisable(true);
        enterNameTextField.setDisable(true);
    }

    public void enableSendElements()
    {
        sendButton.setDisable(false);
        chooseIPTextField.setDisable(false);
        chooseFileButton.setDisable(false);
        enterNameTextField.setDisable(false);
    }

    private enum IPTextFeildStates
    {
        INCORRECT, DONE, AWAITING, ACCEPTED_SENDING
    }
}
