package pcstatus.connectionPackage;

import pcstatus.ServerBatteryMain;
import pcstatus.SingletonBatteryStatus;

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
public class BluetoothSPPServer {
    private StreamConnection connection;
    private OutputStream outStream = null;
    private PrintWriter pWriter;
    private StreamConnectionNotifier streamConnNotifier;
    private RemoteDevice device;
    private InputStream inStream;
    private Thread messageThread;
    private ServerBatteryMain serverBatteryMain;

    public BluetoothSPPServer(ServerBatteryMain serverBatteryMain) {
        this.serverBatteryMain = serverBatteryMain;
    }

    //start server
    public void startBluetoothServer() throws IOException {

        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";

        //open server url
        streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);


        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        try {
            connection = streamConnNotifier.acceptAndOpen();
        } catch (IOException e) {
            System.out.println("streamConnNotifier.acceptAndOpen()");
            e.printStackTrace();
        }

        try {
            device = RemoteDevice.getRemoteDevice(connection);
        } catch (IOException e) {
            System.out.println("RemoteDevice.getRemoteDevice(connection)");

        }
        System.out.println("Remote device address: " + device.getBluetoothAddress());
        System.out.println("Remote device name: " + device.getFriendlyName(true));

        outStream = connection.openOutputStream();
        inStream = connection.openInputStream();
        sendMessage();
    }

    public void closeConnection() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        if (outStream != null) {
            if (messageThread == null) {
                messageThread = newMessageThread();
                System.out.println("thread creato e avviato");
                sendMessage();
            } else {
                messageThread.interrupt();
                messageThread = newMessageThread();
                messageThread.start();
                System.out.println("thread avviato");
            }
        }
    }

    private Thread newMessageThread() {
        return messageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                pWriter = new PrintWriter(new OutputStreamWriter(outStream));
                pWriter.write(SingletonBatteryStatus.getInstance().getJsonStr() + "\n");
                System.out.println("stampo " + SingletonBatteryStatus.getInstance().getJsonStr());
                pWriter.flush();
                System.out.println("inviato");

                final String[] lineRead = {null};
            /*new Thread(new Runnable() {
                @Override
                public void run() {*/
                try {
                    //Thread.sleep(1500);
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                    lineRead[0] = bReader.readLine();
                    System.out.println(lineRead[0]);
                } catch (IOException e) {
                    //closeConnection();
                    if (!messageThread.isInterrupted()) {
                        System.out.println("sono stato interrotto");
                        messageThread.interrupt();
                        //serverBatteryMain.startServerBluetooth();
                        //e.printStackTrace();
                    } else
                        System.out.println("non sono stato interrotto ma sono nel catch");//e.printStackTrace();
                }
               /* }
            }).start();*/
            }
        }, "messageThread");
    }
}
