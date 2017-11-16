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
import pcstatus.springServer.GreetingController;
import pcstatus.springServer.ServerManager;

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
    private boolean isServerCreated;
    private ServerManager serverManager;


    @Override
    public void start(Stage primaryStage) {
        long startAllTime = System.currentTimeMillis();
        SingletonBatteryStatus.getInstance().addingObserver(ServerBatteryMain.this);
        serverManager = new ServerManager(ServerBatteryMain.this);
        firstGetter.start();
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
            serverManager.bluetoothThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("aspetto");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finito di aspettare");
        try {
            this.primaryStage.setTitle("PCstatus - " + getMyIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            this.primaryStage.setTitle("PCstatus - problem with IP identifier");
            e.printStackTrace();
        }
        long stopAllTime = System.currentTimeMillis();
        System.out.println("\n\n                                                " +
                " fatto tutto e ci ho messo " + (stopAllTime - startAllTime) + "\n");
        if (isServerCreated)
            serverManager.scheduleTask();
        else
            serverManager.scheduleTaskWithoutServer();
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

        serverManager.shutDown();
    }

    @Override
    public void update(Observable o, Object arg) {
        SingletonBatteryStatus singletonBatteryStatus = SingletonBatteryStatus.getInstance();

        controller.setBatteryText(String.join("\n", singletonBatteryStatus.getBattery()));
        controller.setCpuText(String.join("\n", singletonBatteryStatus.getCpu()));
        controller.setDisksText(String.join("\n", singletonBatteryStatus.getDisks()));
        controller.setSystemText(String.join("\n", singletonBatteryStatus.getComputerInfo()));
        controller.setMiscellaneous(String.join("\n", singletonBatteryStatus.getMiscellaneous()));
        System.out.println("kjsbdfjkbdss         " + singletonBatteryStatus.getNumericCpuLoad());
        controller.getLineChartClass().addEntryLineChart(singletonBatteryStatus.getNumericCpuLoad());
        controller.getPieChartClass().addEntryPieChart(singletonBatteryStatus.getAvaibleFileSystem());

        if (firstShow) {
            controller.getMultipleLineChartClass().createSeries();
            controller.getMultipleLineChartClass().addEntryLineChart(singletonBatteryStatus.getPercPerThread());
            resizeWindow();
        }else {
            controller.getMultipleLineChartClass().addEntryLineChart(singletonBatteryStatus.getPercPerThread());
        }
        serverManager.sendBluetoothMessage();
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

    private Thread serverThread = new Thread(this::runSpringApplication);

    private void runSpringApplication() {
        try {
            applicationContext = SpringApplication.run(ServerBatteryMain.class, args);
            isServerCreated = true;
            latch.countDown();
        } catch (ConnectorStartFailedException e) {
            System.out.println("c'è qualcosa che non va con la porta");
            port = port + 1;
            if (port < 8091) {
                System.getProperties().put("server.port", port);
                runSpringApplication();
            } else {
                isServerCreated = false;
                latch.countDown();
            }
        }
    }

    private Thread firstGetter = new Thread(() -> {
        GreetingController.getAllData();
        latch.countDown();
    });

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
