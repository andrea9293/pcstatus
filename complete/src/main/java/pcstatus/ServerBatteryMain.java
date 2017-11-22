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
import pcstatus.dataPackage.SingletonBatteryStatus;
import pcstatus.dataPackage.SingletonNumericGeneralStats;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class ServerBatteryMain extends Application implements Observer {

    private Controller controller;
    private static ConfigurableApplicationContext applicationContext;
    private Stage primaryStage;
    private boolean firstShow = true;
    private static String[] args;
    private CountDownLatch latch = new CountDownLatch(2);
    private int port = 8080;
    private ConnectionManager connectionManager;
    private Boolean isServerCreated;


    @Override
    public void start(Stage primaryStage) {
        long startAllTime = System.currentTimeMillis();
        SingletonBatteryStatus.getInstance().addingObserver(ServerBatteryMain.this);
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
        this.primaryStage.setScene(new Scene(root));
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
        this.primaryStage.centerOnScreen();
        this.primaryStage.setOnCloseRequest(event -> {
            shutDown();
            Platform.exit();
        });
        controller = loader.getController();

        try {
            System.out.println("aspetto");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finito di aspettare");
        try {
            this.primaryStage.setTitle("PCstatus - " + getMyIp());
            controller.setIpAddressInformation(getMyIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            this.primaryStage.setTitle("PCstatus - problem with IP identifier");
            e.printStackTrace();
        }
        long stopAllTime = System.currentTimeMillis();
        System.out.println("\n\n                                                " +
                " fatto tutto e ci ho messo " + (stopAllTime - startAllTime) + "\n");

        connectionManager.scheduleTask(isServerCreated);
    }

    public static void main(String[] args) throws IOException {
        ServerBatteryMain.args = args;
        launch(args);
    }

    private void shutDown() {
        try {
            SpringApplication.exit(applicationContext, () -> 0);

        } catch (IllegalArgumentException e) {
            System.out.println("il constesto è null, non si è avviato il server");
        }

        connectionManager.shutDown();
    }

    @Override
    public void update(Observable o, Object arg) {
        SingletonBatteryStatus singletonBatteryStatus = SingletonBatteryStatus.getInstance();

        controller.setBatteryText(String.join("\n", singletonBatteryStatus.getBattery()));
        controller.setCpuText(String.join("\n", singletonBatteryStatus.getCpu()));
        controller.setDisksText(String.join("\n", singletonBatteryStatus.getDisks()));
        controller.setSystemText(String.join("\n", singletonBatteryStatus.getComputerInfo()));
        controller.setMiscellaneous(String.join("\n", singletonBatteryStatus.getMiscellaneous()));
        controller.getLineChartClass().addEntryLineChart(Float.parseFloat(SingletonNumericGeneralStats.getInstance().getCpuLoad()));

        if (singletonBatteryStatus.getBatteryPerc() != null)
            controller.getStackedAreaChartClass().addEntryStackedAreaChart(singletonBatteryStatus.getBatteryPerc());
        controller.getPieChartClass().addEntryPieChart(singletonBatteryStatus.getAvaibleFileSystem());

        if (firstShow) {
            controller.getMultipleLineChartClass().createSeries();
            controller.getMultipleLineChartClass().addEntryLineChart(singletonBatteryStatus.getPercPerThread());
            resizeWindow();
        } else {
            controller.getMultipleLineChartClass().addEntryLineChart(singletonBatteryStatus.getPercPerThread());
        }
        connectionManager.sendBluetoothMessage();
    }

    private void resizeWindow() {
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        firstShow = false;
    }


    private String getMyIp() throws UnknownHostException, SocketException {
        InetAddress addr = InetAddress.getLocalHost();
        return addr.getHostAddress();
    }

    private Thread serverThread = new Thread(this::runSpringApplication,"serverThread");

    private void runSpringApplication() {
        try {
            applicationContext = SpringApplication.run(ServerBatteryMain.class, args);
            connectionManager.setPort(port);
            controller.setServerPortInformation(String.valueOf(port));
            try {
                controller.setBluetoothInformation(LocalDevice.getLocalDevice().getFriendlyName());
            }
            catch (BluetoothStateException e) {
                System.out.println("bluetooth non supportato");
                //e.printStackTrace();
            }
            isServerCreated = true;
            latch.countDown();
        } catch (ConnectorStartFailedException e) {
            System.out.println("c'è qualcosa che non va con la porta");
            port = port + 1;
            if (port < 8091) {
                System.getProperties().put("server.port", port);
                runSpringApplication();
            } else {
                ErrorManager.exeptionDialog(e);
                controller.setIpAddressInformation("Server is not created");
                controller.setServerPortInformation("Server is not created");
                controller.setBluetoothInformation("Server is not created");
                isServerCreated = false;
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



