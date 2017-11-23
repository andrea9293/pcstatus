package pcstatus.connectionPackage;

import pcstatus.dataPackage.SingletonBatteryStatus;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;

/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */
class BluetoothSPPServer {
    private StreamConnection connection;
    private OutputStream outStream = null;
    private PrintWriter pWriter;
    private StreamConnectionNotifier streamConnNotifier;
    private RemoteDevice device;
    private InputStream inStream;
    private Thread messageThread;
    private boolean connectionIsAvaible = false;
    private Thread startBluetoothServer;

    BluetoothSPPServer() {
    }

    //start server
    private void bluetoothServer() {

        if (messageThread != null) {
            System.out.println("in bluetoothServer messageThread non è null");
            messageThread.interrupt();
        }
        try {
            //Create a UUID for SPP
            UUID uuid = new UUID("1101", true);
            //Create the servicve url
            String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";

            //open server url
            streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

            //Wait for client connection
            System.out.println("\nServer Started. Waiting for clients to connect...");

            connection = streamConnNotifier.acceptAndOpen();


            device = RemoteDevice.getRemoteDevice(connection);
            System.out.println("Remote device address: " + device.getBluetoothAddress());
            System.out.println("Remote device name: " + device.getFriendlyName(true));
            outStream = connection.openOutputStream();
            inStream = connection.openInputStream();
            connectionIsAvaible = true;
            sendMessage();
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    void closeConnection() {
        try {
            if (messageThread != null && messageThread.isAlive())
                messageThread.interrupt();
            if (pWriter != null)
                pWriter.close();
            if (outStream != null)
                outStream.close();
            if (inStream != null)
                inStream.close();
            streamConnNotifier.close();
            connectionIsAvaible = false;
            SingletonBatteryStatus.getInstance().setBluetoothServerCreated(false);
        } catch (IOException e) {
            //ErrorManager.exeptionDialog(e);
            e.printStackTrace();
        }
        SingletonBatteryStatus.getInstance().setBluetoothName("Bluetooth not available");
        connectionIsAvaible = false;
    }

    void sendMessage() {
        if (connectionIsAvaible) {
            if (outStream != null) {
                if (messageThread == null) {
                    messageThread = newMessageThread();
                    System.out.println("thread creato e avviato");
                    sendMessage();
                } else {
                    messageThread.interrupt();
                    messageThread = newMessageThread();
                    messageThread.start();
                }
            }
        } else {
            try {
                if (LocalDevice.getLocalDevice() != null && !LocalDevice.getLocalDevice().getFriendlyName().equals("null"))
                    System.out.println("connessione non stabilita " + LocalDevice.getLocalDevice().getFriendlyName());
                else {
                    closeConnection();
                }
            } catch (BluetoothStateException | NullPointerException e) {
                e.printStackTrace();
                closeConnection();
            }
        }
    }

    private Thread newMessageThread() {
        return messageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                pWriter = new PrintWriter(new OutputStreamWriter(outStream));
                pWriter.write(SingletonBatteryStatus.getInstance().getJsonStr() + "\n");
                pWriter.flush();

                final String[] lineRead = {null};

                try {
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                    lineRead[0] = bReader.readLine();
                    System.out.println(lineRead[0]);
                } catch (IOException e) {
                    if (!messageThread.isInterrupted()) {
                        System.out.println("sono stato interrotto");
                        messageThread.interrupt();
                        connectionIsAvaible = false;
                        if (Thread.currentThread().isInterrupted())
                            System.out.println("sono stato interrotto da message thread");
                        else
                            Thread.currentThread().interrupt();
                        closeConnection();
                        startServerBluetooth();
                    } else
                        System.out.println("non sono stato interrotto ma sono nel catch");//e.printStackTrace();
                }
            }
        }, "messageThread");
    }

    void startServerBluetooth() {
        if (startBluetoothServer == null || !startBluetoothServer.isAlive()) {
            startBluetoothServer = new Thread(this::bluetoothServer, "startServerBluetooth");

            startBluetoothServer.start();
        }
    }
}
