package pcstatus.connectionPackage;

import org.json.JSONException;
import pcstatus.dataPackage.SingletonBatteryStatus;
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

public class ConnectionManager {
    private int port;
    private BluetoothSPPServer bluetooth;
    //private Thread startBluetoothServer;
    private TimerTask task;
    private Timer timer;
    private URL prova;
    private BufferedReader in;
    private String inputLine;

    public ConnectionManager() {
    }

    public void setPort(int port) {
        this.port = port;
        SingletonBatteryStatus.getInstance().setPort(port);
    }

    public void sendBluetoothMessage() {

        if (bluetooth != null) {
            if (SingletonBatteryStatus.getInstance().isBluetoothServerCreated())
                bluetooth.sendMessage();
            else
                bluetooth = null;
        }
        System.out.println(SingletonBatteryStatus.getInstance().isBluetoothServerCreated());

    }

    private void bluetoothThread() {
        try {
            SingletonBatteryStatus.getInstance().setBluetoothName(LocalDevice.getLocalDevice().getFriendlyName());
            bluetooth = new BluetoothSPPServer();
            bluetooth.startServerBluetooth();
        } catch (BluetoothStateException e) {
            SingletonBatteryStatus.getInstance().setBluetoothServerCreated(false);
            SingletonBatteryStatus.getInstance().setBluetoothName("Bluetooth not available");
            System.out.println("server bluetooth non avviato");
            //e.printStackTrace();
        }

    }

   /* public void bluetoothThread() throws IOException {
        if (startBluetoothServer == null || !startBluetoothServer.isAlive()) {

            //display local device address and name
            LocalDevice localDevice;
            try {
                localDevice = LocalDevice.getLocalDevice();
                System.out.println("Address: " + localDevice.getBluetoothAddress());
                System.out.println("Name: " + localDevice.getFriendlyName());
                bluetooth = new BluetoothSPPServer(this);
                bluetooth.startServerBluetooth();

            } catch (BluetoothStateException e) {
                System.out.println("Bluetooth non supportato");
            }
        } else {
            System.out.println("sono in esecuzione");
        }
    }*/

    /*void startServerBluetooth() {
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
    }*/

    public void scheduleTask(Boolean isServerCreated) {
        timer = new Timer();
        if (isServerCreated) {
            System.out.println("task programmato");

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
            System.out.println("task programmato senza server");
            if (task == null) {
                task = new TimerTask() {
                    @Override
                    public void run() {
                        //System.out.println("Inside Timer Task" + System.currentTimeMillis());
                        GreetingController.getAllData();
                    }
                };
                timer.schedule(task, 0, 3000); //it executes this every 1 minute
            }
        }
    }

    public void firstGetter(CountDownLatch latch) {
        new Thread(() -> {
            GreetingController.getAllData();
            latch.countDown();
        }, "firstGetter").start();
    }

    private void refresh() {

        try {
            if (prova == null)
                prova = new URL("http://localhost:" + port + "/greeting/");

            in = new BufferedReader(new InputStreamReader(prova.openStream()));

            while ((inputLine = in.readLine()) != null) {
                SingletonBatteryStatus.getInstance().setJsonStr(inputLine);
            }
            in.close();
        } catch (IOException | JSONException e) {
            System.out.println("server non pronto");
            e.printStackTrace();
        }
    }

    public void shutDown() {
        if (bluetooth != null) {
            bluetooth.closeConnection();
        }
        taskCancel();
      /*  if (startBluetoothServer != null)
            startBluetoothServer.interrupt();*/
    }

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
