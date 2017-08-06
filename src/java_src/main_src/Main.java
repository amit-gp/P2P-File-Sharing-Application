package java_src.main_src;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Amit Kumar on 01-08-2017.
 */
public class Main extends Application
{

    private static Stage primaryStage;

    public static void main(String[] args)
    {
        P2P_Client p2p_client = new P2P_Client();
        launch(args);
    }

    public static void requestApplicationFocus()
    {
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                primaryStage.toFront();
                primaryStage.requestFocus();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
        primaryStage.setTitle("P2P_FileShare - Login");
        primaryStage.setScene(new Scene(root, 550, 500));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(200);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new javafx.event.EventHandler<WindowEvent>()     //Exits program when window closes
        {
            @Override
            public void handle(WindowEvent event)
            {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
