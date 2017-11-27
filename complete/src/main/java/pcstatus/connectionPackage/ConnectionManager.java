package pcstatus.connectionPackage;

import org.json.JSONException;
import pcstatus.dataPackage.SingletonStaticGeneralStats;
import pcstatus.springServer.GreetingController;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * this class implements a manager for the local server and the bluetooth server
 *
 * @author Andrea Bravaccino
 */
public class ConnectionManager {
    private int port;
    private BluetoothSPPServer bluetooth;
    private TimerTask task;
    private Timer timer;
    private URL prova;


    /**
     * this method call server port setter in the model (SingletonStaticGeneralStats) and
     * set a local private variable port
     *
     * @param port this number is the number of the local server port
     */
    public void setPort(int port) {
        this.port = port;
        SingletonStaticGeneralStats.getInstance().setPort(port);
    }

    /**
     * communicates to the Bluetooth APPS server that he should send a Bluetooth message after
     * verifying if a Bluetooth server exists
     */
    public void sendBluetoothMessage() {
        if (bluetooth != null) {
            if (SingletonStaticGeneralStats.getInstance().isBluetoothServerCreated())
                bluetooth.sendBluetoothMessage();
        }
    }

    /**
     * this method instantiates the class and starts the server, otherwise
     * tells the model that the bluetooth is unavailable
     */
    private void bluetoothThread() {
        try {
            SingletonStaticGeneralStats.getInstance().setBluetoothName(LocalDevice.getLocalDevice().getFriendlyName());
            bluetooth = new BluetoothSPPServer();
            bluetooth.startServerBluetooth();
        } catch (BluetoothStateException e) {
            SingletonStaticGeneralStats.getInstance().setBluetoothServerCreated(false);
            SingletonStaticGeneralStats.getInstance().setBluetoothName("Bluetooth not available");
            System.out.println("bluetooth server no started");
        }
    }

    /**
     * This method starts a periodic thread (timer set to 3 seconds) that invokes
     * the refresh() method if the server is created, or else
     * schedules a thread without connecting to the server
     *
     * @param isServerCreated boolean variable that communicates whether the server was created or not
     */
    public void scheduleTask(Boolean isServerCreated) {
        timer = new Timer();
        if (isServerCreated) {
            System.out.println("task scheduled");

            bluetoothThread();
            if (task == null) {
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Thread.currentThread().setName("refresh");
                        refresh();
                    }
                };
                timer.schedule(task, 0, 3000); //it executes this every 3 seconds
            }
        } else {
            System.out.println("task scheduled without server");
            if (task == null) {
                task = new TimerTask() {
                    @Override
                    public void run() {
                        GreetingController.getInstance().getAllData();
                    }
                };
                timer.schedule(task, 0, 3000); //it executes this every 1 minute
            }
        }
    }

    /**
     *  this method is called upon to start the program to start
     *  a thread responsible for recovering data in parallel
     *  to the server creation tasks
     *
     *  @see GreetingController#getAllData()
     *  @param latch Latch variable to synchronize the threads
     */
    public void firstGetter(CountDownLatch latch) {
        new Thread(() -> {
            GreetingController.getInstance().getAllData();
            latch.countDown();
        }, "firstGetter").start();
    }

    /**
     * this method connects to the server address, downloads the json
     * content and sends it to the model
     */
    private void refresh() {

        try {
            if (prova == null)
                prova = new URL("http://localhost:" + port + "/greeting/");

            BufferedReader in = new BufferedReader(new InputStreamReader(prova.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                SingletonStaticGeneralStats.getInstance().setJsonStr(inputLine);
            }
            in.close();
        } catch (IOException | JSONException e) {
            System.out.println("server is not ready");
            e.printStackTrace();
        }
    }

    /**
     * close bluetooth connection (if exists) and invoke taskCancel()
     */
    public void shutDown() {
        if (bluetooth != null) {
            bluetooth.closeConnection();
            bluetooth.terminateExecutors();
        }
        taskCancel();
    }

    /**
     * taskCancel() is resonsabile closing timer and all the threads
     */
    private void taskCancel() {
        if (task != null) {
            System.out.println("interrotto il timer");
            task.cancel();
            timer.cancel();
            GreetingController.getNetworkSpeed.shutdownNow();
            GreetingController.getCpuInfo.shutdownNow();
        }
    }
}
