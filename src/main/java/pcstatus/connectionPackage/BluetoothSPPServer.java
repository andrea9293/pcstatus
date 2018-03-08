/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.connectionPackage;

import pcstatus.dataPackage.SingletonStaticGeneralStats;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 *
 * @author Andrea Bravaccino
 */

class BluetoothSPPServer {

    /**
     * output stream
     */
    private OutputStream outStream = null;
    /**
     * input stream
     */
    private InputStream inStream;
    /**
     * varable to send a bluetooth message
     */
    private PrintWriter pWriter;
    /**
     * variable to open server URL
     */
    private StreamConnectionNotifier streamConnNotifier;
    /**
     * String to recived message
     */
    private final String[] lineRead = {null};

    /**
     * bluetooth connection status
     */
    private boolean connectionIsAvaible = false;

    /**
     * Thread that runs <code>bluetoothServer()</code>
     */
    private Thread startBluetoothServer = new Thread(() -> {
        Thread.currentThread().setName("startServerBluetooth");
        bluetoothServer();
    }, "startServerBluetooth");

    /**
     * Executor service that run <code>startBluetoothServer</code>
     */
    private ExecutorService bluetoothServerExecutor = Executors.newSingleThreadExecutor();

    /**
     * a thread that will send json string and waits until bluetooth
     * connected device does not response
     */
    private Thread sendReciveMessageThread = new Thread(() -> {
        Thread.currentThread().setName("sendReciveMessageThread");
        sendReciveMessage();
    }, "sendReciveMessageThread");

    /**
     * Executor service that run <code>sendReciveMessageThread</code>
     */
    private ExecutorService messageThreadExecutor = Executors.newSingleThreadExecutor();

    /**
     * start server creating an UUID and a connection String,
     * then waiting for a device to connect
     */
    private void bluetoothServer() {
        if (sendReciveMessageThread.isAlive()) {
            sendReciveMessageThread.interrupt();
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

            StreamConnection connection = streamConnNotifier.acceptAndOpen();

            System.out.println("Remote device address: " + RemoteDevice.getRemoteDevice(connection).getBluetoothAddress());
            System.out.println("Remote device name: " + RemoteDevice.getRemoteDevice(connection).getFriendlyName(true));

            //the stream is opened both in and out
            outStream = connection.openOutputStream();
            inStream = connection.openInputStream();
            connectionIsAvaible = true;
            SingletonStaticGeneralStats.getInstance().setBluetoothServerCreated(true);
            sendBluetoothMessage();
        } catch (Exception e) {
            //e.printStackTrace();
            //in case of problems, the connection is stopped
            closeConnection();
        }
    }

    /**
     * close all stream and interrupt all thread, then set false and modify the bluetooth
     * label to alert the entire program that the bluetooth is no longer available
     */
    void closeConnection() {
        try {
            if (sendReciveMessageThread.isAlive())
                sendReciveMessageThread.interrupt();
            if (startBluetoothServer.isAlive())
                startBluetoothServer.interrupt();
            if (pWriter != null)
                pWriter.close();
            if (outStream != null)
                outStream.close();
            if (inStream != null)
                inStream.close();
            streamConnNotifier.close();
            connectionIsAvaible = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set false and change bluetooth label to alert the entire program
        SingletonStaticGeneralStats.getInstance().setBluetoothServerCreated(false);
        SingletonStaticGeneralStats.getInstance().setBluetoothName("Bluetooth not available");
        connectionIsAvaible = false;
    }

    /**
     * if bluetooth connection is available, <code>sendBluetoothMessage()</code> run <code>sendReciveMessageThread()</code>.
     * Else, checks if Bluetooth is activated and chooses if close connection or wait again
     */
    void sendBluetoothMessage() {
        if (connectionIsAvaible) {
            if (outStream != null) {
                if (sendReciveMessageThread.isInterrupted()) {
                    messageThreadExecutor.execute(sendReciveMessageThread);
                    System.out.println("thread creato e avviato");
                    sendBluetoothMessage();
                } else {
                    sendReciveMessageThread.interrupt();
                    messageThreadExecutor.execute(sendReciveMessageThread);
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

    private void sendReciveMessage() {
        //send json to remote bluetooth device
        pWriter = new PrintWriter(new OutputStreamWriter(outStream));
        pWriter.write(SingletonStaticGeneralStats.getInstance().getJsonStr() + "\n");
        pWriter.flush();

        try {
            //recive message from bluetooth device to check the connection
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            lineRead[0] = bReader.readLine();
        } catch (IOException e) {
            //if recive message failed, stop the thread and restart bluetooth server
            if (!sendReciveMessageThread.isInterrupted()) {
                System.out.println("sono stato interrotto");
                sendReciveMessageThread.interrupt();
                connectionIsAvaible = false;
                if (Thread.currentThread().isInterrupted())
                    System.out.println("sono stato interrotto da message thread");
                else
                    Thread.currentThread().interrupt();
                closeConnection();
                startServerBluetooth();
            } else
                System.out.println("non sono stato interrotto ma sono nel catch");
        }
    }

    /**
     * run bluetooth server thread
     */
    void startServerBluetooth() {
        if (!startBluetoothServer.isAlive()) {
            bluetoothServerExecutor.execute(startBluetoothServer);
        }
    }

    /**
     * this method terminates all executors
     */
    void terminateExecutors() {
        messageThreadExecutor.shutdownNow();
        bluetoothServerExecutor.shutdownNow();
    }
}
