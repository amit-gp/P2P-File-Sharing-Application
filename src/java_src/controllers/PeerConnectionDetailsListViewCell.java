package java_src.controllers;

import java_src.main_src.FileReceiver;
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
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Amit Kumar on 04-08-2017.
 */
public class PeerConnectionDetailsListViewCell extends ListCell<PeerConnectionDetails> implements FileReceiver.CallBack
{
    @FXML private Label PeerHeaderDetailsLabel;
    @FXML private Label PeerFileDetailsLabel;
    @FXML private GridPane gridPane;
    @FXML private Button acceptButton;
    @FXML private Label PeerIPDetailsLabel;
    private Label downloadProgressLabel;
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
            System.out.println(peerConnectionDetails.getFileName() + " " + peerConnectionDetails.isDownloadComplete());

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
            PeerFileDetailsLabel.setText(peerConnectionDetails.getFileName());
            PeerIPDetailsLabel.setText(peerConnectionDetails.getSocket().getInetAddress().toString());

            if (!peerConnectionDetails.isDownloadComplete())
            {
                acceptButton = new Button("Accept");
                acceptButton.setOnAction(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        System.out.println("Accepted file !!!");
                        peerConnectionDetails.sendAcceptSignal();
                        acceptButton.setDisable(true);
                        gridPane.getChildren().remove(acceptButton);
                        downloadProgressLabel = new Label("Downloading.....");
                        downloadProgressLabel.setTextFill(Color.web("#0076a3"));
                        gridPane.add(downloadProgressLabel, 2, 1);
                        fileSender(peerConnectionDetails);
                    }
                });

                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        gridPane.add(acceptButton, 1, 1);
                        System.out.println("Creating accept for " + peerConnectionDetails.getFileName());
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
            if (peerConnectionDetails.i != 0)
                peerConnectionDetails.setDownloadComplete(true);
            peerConnectionDetails.i++;
        }

    }

    @Override
    public void downloadComplete()
    {
        System.out.println("Download Complete !!");

        Button showFileButton = new Button("Show File");
        showFileButton.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                File file = new File("C:\\P2P_FileSharing");
                Desktop desktop = null;
                if (Desktop.isDesktopSupported())
                {
                    desktop = Desktop.getDesktop();
                }
                try
                {
                    desktop.open(file);

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                downloadProgressLabel.setText("Download Complete !");
                gridPane.add(showFileButton, 2, 0);
            }
        });
    }


    private void fileSender(PeerConnectionDetails peerConnectionDetails)
    {
        FileReceiver fileReceiver = new FileReceiver(peerConnectionDetails.getSocket(), peerConnectionDetails.getFileSize(),peerConnectionDetails.getFileName(), this);
        new Thread(fileReceiver).start();
    }
}
