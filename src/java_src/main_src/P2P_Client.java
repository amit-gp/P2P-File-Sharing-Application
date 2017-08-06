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
    private static Socket sendingSocket; //Used while sending a file to another peer and then closed.
    private static ServerSocket serverSocket; // Always listening for an incoming connection.
    private static final int PORT = 8080;
    private static ConnectionListener connectionListener;
    private static OutputStream outputStream;
    private static DataOutputStream dataOutputStream;

    private File filePath;
    @FXML private Button chooseFileButton;

    @Override
    public void fileSent()
    {
        System.out.println("The file was sent !!!!");
    }

    @FXML private Text fileChosenText;
    @FXML private TextField chooseIPTextField;
    @FXML private TextField enterNameTextField;
    @FXML private Text IPChosenText;
    @FXML private Button sendButton;
    @FXML private ListView<PeerConnectionDetails> sentFilesListView;
    private static ObservableList<PeerConnectionDetails> sentFilesObservableList;

    public P2P_Client()
    {
        sentFilesObservableList = FXCollections.observableArrayList();
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

    private enum IPTextFeildStates
    {
        INCORRECT, CHECKING, ONLINE, AWAITING, ACCEPTED_SENDING
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

            case AWAITING:
                IPChosenText.setText("Awaitng user conformation ......");
                IPChosenText.setFill(Color.RED);
                break;

            case ACCEPTED_SENDING:
                IPChosenText.setText("User accepted !! Sending file\nPlease wait...");
                IPChosenText.setFill(Color.RED);
                break;
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
}
