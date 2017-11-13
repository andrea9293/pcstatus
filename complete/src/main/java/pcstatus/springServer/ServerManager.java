package pcstatus.springServer;

import org.json.JSONException;
import pcstatus.ServerBatteryMain;
import pcstatus.SingletonBatteryStatus;
import pcstatus.connectionPackage.BluetoothSPPServer;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class ServerManager {
    ServerBatteryMain serverBatteryMain;
    int port = 8080;

    public ServerManager(ServerBatteryMain serverBatteryMain){
        this.serverBatteryMain = serverBatteryMain;
        //this.port = port;
    }
    public ServerManager(){

    }

    private BluetoothSPPServer bluetooth;
    private Thread startBluetoothServer;
    private TimerTask task;
    private Timer timer;

    public void sendBluetoothMessage() {
        if (bluetooth != null) bluetooth.sendMessage();
    }

    public void bluetoothThread() throws IOException {
        if (startBluetoothServer == null || !startBluetoothServer.isAlive()) {

            //display local device address and name
            LocalDevice localDevice;
            try {
                localDevice = LocalDevice.getLocalDevice();
                System.out.println("Address: " + localDevice.getBluetoothAddress());
                System.out.println("Name: " + localDevice.getFriendlyName());
                bluetooth = new BluetoothSPPServer(this);
                startServerBluetooth();

            } catch (BluetoothStateException e) {
                //ErrorManager.exeptionDialog(e);
                System.out.println("Bluetooth non supportato");
            }
        } else {
            System.out.println("sono in esecuzione");
        }
    }

    public void startServerBluetooth() {
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

    public void scheduleTask() {
        timer = new Timer();
        System.out.println("task programmato");
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    //System.out.println("Inside Timer Task" + System.currentTimeMillis());
                    refresh();
                }
            };
            timer.schedule(task, 0, 3000); //it executes this every 3 seconds
        }
    }

    public void scheduleTaskWithoutServer() {
        timer = new Timer();
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

    private void refresh() {
        //update(null,null);
        URL prova = null;
        try {
            prova = new URL("http://localhost:" + port + "/greeting/");
            BufferedReader in = new BufferedReader(new InputStreamReader(prova.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                SingletonBatteryStatus.getInstance().setJsonStr(inputLine);
            }
            in.close();
        } catch (IOException | JSONException e) {
            System.out.println("server non pronto");
            e.printStackTrace();
        }
    }

    public void shutDown(){
        if (bluetooth != null) {
            bluetooth.closeConnection();
        }
        taskCancel();
        if (startBluetoothServer != null)
            startBluetoothServer.interrupt();
    }

    private void taskCancel() {
        if (task != null) {
            System.out.println("interrotto il timer");
            task.cancel();
            timer.cancel();
        }
    }
}
