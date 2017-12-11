/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.context.ConfigurableApplicationContext;
import pcstatus.connectionPackage.ConnectionManager;
import pcstatus.dataPackage.SingletonStaticGeneralStats;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;


/**
 * Main Class
 */
@SpringBootApplication
public class ServerBatteryMain extends Application implements Observer {

    private static ConfigurableApplicationContext applicationContext;
    private Stage primaryStage;
    private static String[] args;
    private CountDownLatch latch = new CountDownLatch(2);
    /**
     * default port for server
     */
    private int port = 8080;
    private ConnectionManager connectionManager;
    private Thread serverThread = new Thread(this::runSpringApplication,"serverThread");


    /**
     * initialize the program
     * @param primaryStage main frame
     */
    @Override
    public void start(Stage primaryStage) {
        long startAllTime = System.currentTimeMillis();
        SingletonStaticGeneralStats.getInstance().addingObserver(ServerBatteryMain.this);
        SingletonStaticGeneralStats.getInstance().setFirstShow(true);
        connectionManager = new ConnectionManager();
        connectionManager.firstGetter(latch);
        serverThread.start();

        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ProgressIndicator progressIndicator = new ProgressIndicator();
        if (root != null) {
            this.primaryStage.setScene(new Scene(root));
        }
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
        this.primaryStage.centerOnScreen();
        this.primaryStage.setOnCloseRequest(event -> {
            shutDown();
            Platform.exit();
        });

        try {
            System.out.println("aspetto");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finito di aspettare");
        try {
            this.primaryStage.setTitle("PCstatus 1.1.1beta - " + getMyIp());
            SingletonStaticGeneralStats.getInstance().setIpAddress(getMyIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            this.primaryStage.setTitle("PCstatus - problem with IP identifier");
            e.printStackTrace();
        }
        long stopAllTime = System.currentTimeMillis();
        System.out.println("\n\n                                                " +
                " fatto tutto e ci ho messo " + (stopAllTime - startAllTime) + "\n");

        connectionManager.scheduleTask(SingletonStaticGeneralStats.getInstance().isServerCreated());
    }

    public static void main(String[] args) throws IOException {
        ServerBatteryMain.args = args;
        launch(args);
    }

    /**
     * shutdown all threads and close the program
     */
    private void shutDown() {
        try {
            SpringApplication.exit(applicationContext, () -> 0);

        } catch (IllegalArgumentException e) {
            System.out.println("il constesto è null, non si è avviato il server");
        }
        connectionManager.shutDown();
    }

    /**
     * method updating view with new data
     * @see Observer#update(Observable, Object)
     * @param o not used
     * @param arg not used
     */
    @Override
    public void update(Observable o, Object arg) {
        if (SingletonStaticGeneralStats.getInstance().isFirtShow()) {
            resizeWindow();
        }
        connectionManager.sendBluetoothMessage();
    }

    /**
     * adapt size of main frame
     */
    private void resizeWindow() {
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        SingletonStaticGeneralStats.getInstance().setFirstShow(false);
    }

    private String getMyIp() throws UnknownHostException, SocketException {
        InetAddress addr = InetAddress.getLocalHost();
        return addr.getHostAddress();
    }

    /**
     * Recursive function that creates Spring Server by testing from port 8080 to 8090
     */
    private void runSpringApplication() {
        try {
            applicationContext = SpringApplication.run(ServerBatteryMain.class, args);
            connectionManager.setPort(port);
            try {
                SingletonStaticGeneralStats.getInstance().setBluetoothName(LocalDevice.getLocalDevice().getFriendlyName());
            }
            catch (BluetoothStateException e) {
                System.out.println("bluetooth non supportato");
            }
            SingletonStaticGeneralStats.getInstance().setServerCreated(true);
            latch.countDown();
        } catch (ConnectorStartFailedException e) {
            System.out.println("c'è qualcosa che non va con la porta");
            port = port + 1;
            if (port < 8091) {
                System.getProperties().put("server.port", port);
                runSpringApplication();
            } else {
                new ErrorManager().exceptionDialog(e);
                SingletonStaticGeneralStats.getInstance().setServerCreated(false);
                latch.countDown();
            }
        }
    }

    /*public static class ProgressForm {
        private final Stage dialogStage;
        private final ProgressIndicator pin = new ProgressIndicator();

        public ProgressForm() {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // PROGRESS BAR
            final Label label = new Label();
            label.setText("alerto");

            pin.setProgress(-1F);

            final HBox hb = new HBox();
            hb.setSpacing(5);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(pin);

            Scene scene = new Scene(hb);
            dialogStage.setScene(scene);
        }

        public void activateProgressBar(final Task<?> task) {
            pin.progressProperty().bind(task.progressProperty());
            dialogStage.show();
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
    }*/
}



