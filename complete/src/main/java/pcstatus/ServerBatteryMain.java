package pcstatus;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pcstatus.connectionPackage.BluetoothSPPServer;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class ServerBatteryMain extends Application implements Observer {


    private Controller controller;
    private static ConfigurableApplicationContext applicationContext;
    private BluetoothSPPServer bluetooth;
    private Thread startBluetoothServer;
    private TimerTask task;
    private Timer timer;
    private Stage primaryStage;
    private boolean firstShow = true;
    private static String[] args;

    @Override
    public void start(Stage primaryStage) throws Exception {
        applicationContext = SpringApplication.run(ServerBatteryMain.class, args);

        this.primaryStage = primaryStage;
        SingletonBatteryStatus.getInstance().addingObserver(ServerBatteryMain.this);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();

        ProgressIndicator progressIndicator = new ProgressIndicator();
        this.primaryStage.setTitle("PCstatus");
        this.primaryStage.setScene(new Scene(root/*, 500, 250*/));
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
        this.primaryStage.centerOnScreen();
        this.primaryStage.setOnCloseRequest(event -> {
            try {
                shutDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.exit();
        });
        controller = loader.getController();

        /*Button button = controller.getButtonRefresh();

        button.setOnAction(event -> {
            refresh();
        });*/

        bluetoothThread();
        scheduleTask();
    }

    public static void main(String[] args) throws IOException {
        ServerBatteryMain.args = args;
        launch(args);
    }

    private void shutDown() throws IOException {
        SpringApplication.exit(applicationContext, () -> 0);
        if (bluetooth != null) {
            bluetooth.closeConnection();
        }
        taskCancel();
        if (startBluetoothServer!=null)
            startBluetoothServer.interrupt();
    }

    @Override
    public void update(Observable o, Object arg) {
        SingletonBatteryStatus singletonBatteryStatus = SingletonBatteryStatus.getInstance();
        String ip = "";
        try {
            ip = "This is your IP: " + getMyIp() + "\n\n";
        } catch (UnknownHostException | SocketException e) {
            ip = "Unable to find your IP";
            e.printStackTrace();
        }

       // controller.setIpText(ip);
        controller.setBatteryText(String.join("\n", singletonBatteryStatus.getBattery()));
        controller.setCpuText(String.join("\n", singletonBatteryStatus.getCpu()));
        controller.setDisksText(String.join("\n", singletonBatteryStatus.getDisks()));
        controller.setSystemText(String.join("\n", singletonBatteryStatus.getComputerInfo()));
        controller.setMiscellaneous(String.join("\n", singletonBatteryStatus.getMiscellaneous()));
       // controller.setCpuImage("Image/" + singletonBatteryStatus.getCpu()[0]);
        primaryStage.sizeToScene();
        if (firstShow){
            primaryStage.centerOnScreen();
            firstShow=false;
        }
        sendBluetoothMessage();

    }

    private void refresh() {
        URL prova = null;
        try {
            prova = new URL("http://localhost:8080/greeting/");
            BufferedReader in = new BufferedReader(new InputStreamReader(prova.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                SingletonBatteryStatus.getInstance().setJsonStr(inputLine);
            }
            in.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendBluetoothMessage() {
        if (bluetooth != null) bluetooth.sendMessage();
    }

    private void bluetoothThread() throws IOException {
        if (startBluetoothServer == null || !startBluetoothServer.isAlive()) {

            //display local device address and name
            LocalDevice localDevice;
            try {
                localDevice = LocalDevice.getLocalDevice();
                System.out.println("Address: " + localDevice.getBluetoothAddress());
                System.out.println("Name: " + localDevice.getFriendlyName());
                bluetooth = new BluetoothSPPServer(ServerBatteryMain.this);
                startServerBluetooth();

            } catch (BluetoothStateException e) {
                System.out.println("Bluetooth non supportato");
            }
        } else {
            System.out.println("sono in esecuzione");
        }
    }

    private void startServerBluetooth() {
        if (startBluetoothServer == null || !startBluetoothServer.isAlive()) {
            startBluetoothServer = new Thread(() -> {
                try {
                    bluetooth.startBluetoothServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            startBluetoothServer.start();
        }
    }

    private void scheduleTask() {
        timer = new Timer();
        System.out.println("task programmato");
        if (task == null /*|| !taskHasStarted()*/) {
            task = new TimerTask() {
                @Override
                public void run() {
                    //System.out.println("Inside Timer Task" + System.currentTimeMillis());
                    refresh();
                }
            };
            timer.schedule(task, 0, 3000); //it executes this every 1 minute
        }
    }

    private void taskCancel() {
        if (task != null) {
            System.out.println("interrotto il timer");
            task.cancel();
            timer.cancel();
        }
    }
    private String getMyIp() throws UnknownHostException, SocketException {
        InetAddress addr = InetAddress.getLocalHost();
        return addr.getHostAddress();
    }

    public static class ProgressForm {
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

        public void activateProgressBar(final Task<?> task)  {
            pin.progressProperty().bind(task.progressProperty());
            dialogStage.show();
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
    }
}
