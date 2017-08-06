package java_src.controllers;

import java_src.main_src.PeerConnectionDetails;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * Created by Amit Kumar on 04-08-2017.
 */
public class PeerConnectionDetailsListViewCell extends ListCell<PeerConnectionDetails>
{
    @FXML private Label PeerHeaderDetailsLabel;
    @FXML private Label PeerFileDetailsLabel;
    @FXML private GridPane gridPane;
    @FXML private Button acceptButton;
    @FXML private Label PeerIPDetailsLabel;
    private FXMLLoader mLLoader;


    @Override
    protected void updateItem(PeerConnectionDetails peerConnectionDetails, boolean empty)
    {
        super.updateItem(peerConnectionDetails, empty);

        if(empty || peerConnectionDetails == null)  //This is requires according to documentation to avoid graphical artifacts.
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            //Here the FXML controller is specified by code and not in the fxml file. This is because, the resources like the label are null when another instance of this class is created.
            if (mLLoader == null)
            {
                mLLoader = new FXMLLoader(getClass().getResource("/PeerConnectionDetials.fxml"));
                mLLoader.setController(this);

                try
                {
                    mLLoader.load();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            PeerHeaderDetailsLabel.setText(peerConnectionDetails.getPeername());
            PeerFileDetailsLabel.setText(peerConnectionDetails.getFile());
            PeerIPDetailsLabel.setText(peerConnectionDetails.getSocket().getInetAddress().toString());

            acceptButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    System.out.println("Accepted file !!!");
                    peerConnectionDetails.sendAcceptSignal();
                }
            });

            setText(null);

            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    setGraphic(gridPane);
                }
            });
        }

    }
}
