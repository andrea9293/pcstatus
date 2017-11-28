/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.dataPackage;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import pcstatus.ErrorManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Enumeration;

/**
 * this class, using Oshi lib, takes information about the network adapter and calculates download and upload speed
 *
 * @author Andrea Bravaccino
 */
public class NetworkStats {
    /**
     * Intstance of singleton
     */
    private static NetworkStats ourInstance = new NetworkStats();

    /**
     * empty constructor
     */
    private NetworkStats(){}

    /**
     * getter for current instance of <code>NetworkStats</code>
     * @return current instance of <code>NetworkStats</code>
     */
    public static NetworkStats getInstance() {
        return ourInstance;
    }


    /**
     * variable to initialize <code>SystemInfo()</code> of Oshi lib.
     * This variable is useful for obtaining information about the operating system and computer hardware.
     * @see oshi.SystemInfo
     */
    private SystemInfo systemInfo = new SystemInfo();

    /**
     * A StringBuilder for build strings of informations
     */
    private StringBuilder genericStringBuilder = new StringBuilder();


    /**
     * this function, invoked by <code>getNetworkSpeed()</code>, identifies default network interface
     * @return The name of default network interface
     * @throws SocketException exception managed in <code>getNetworkSpeed()</code>
     * @throws UnknownHostException exception managed in <code>getNetworkSpeed()</code>
     */
    private String getDefaultNetworkInteface() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> netifs = NetworkInterface.getNetworkInterfaces();

        InetAddress myAddr = InetAddress.getLocalHost();

        while (netifs.hasMoreElements()) {
            NetworkInterface networkInterface = netifs.nextElement();
            Enumeration<InetAddress> inAddrs = networkInterface.getInetAddresses();
            while (inAddrs.hasMoreElements()) {
                InetAddress inAddr = inAddrs.nextElement();
                if (inAddr.equals(myAddr)) {
                    return networkInterface.getName();
                }
            }
        }
        return "";
    }

    /**
     * this functions retrieves download and upload speed
     * @return a string containing download and upload speed with separated by a "\n". Ex.
     * <p>
     * download speed: 158,487 KB/s \n upload speed: 0 KB/s
     * <p>
     * In case of problems, the function returns "download speed: not supported\n upload speed: not supported\n"
     */
    public String getNetworkSpeed() {

        genericStringBuilder.setLength(0);
        String genericString;
        try {
            genericString = getDefaultNetworkInteface();
        } catch (SocketException | UnknownHostException e) {
            genericStringBuilder.append("download speed: not supported\n");
            genericStringBuilder.append("upload speed: not supported\n");
            e.printStackTrace();
            return genericStringBuilder.toString();
        }

        NetworkIF[] networkIFs = systemInfo.getHardware().getNetworkIFs();
        int i = 0;
        NetworkIF net = networkIFs[0];
        try {
            while (!networkIFs[i].getName().equals(genericString)) {
                net = networkIFs[i];
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            genericStringBuilder.append("download speed: not supported\n");
            genericStringBuilder.append("upload speed: not supported\n");
            return genericStringBuilder.toString();
        }

        long download1 = net.getBytesRecv();
        long upload1 = net.getBytesSent();
        long timestamp1 = net.getTimeStamp();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            new ErrorManager().exceptionDialog(e);
            e.printStackTrace();
        }
        net.updateNetworkStats(); //Updating network stats
        long download2 = net.getBytesRecv();
        long upload2 = net.getBytesSent();
        long timestamp2 = net.getTimeStamp();

        genericStringBuilder.append("download speed: ").append(formatSize((download2 - download1) / (timestamp2 - timestamp1))).append("/s\n");
        genericStringBuilder.append("upload speed: ").append(formatSize((upload2 - upload1) / (timestamp2 - timestamp1))).append("/s\n");

        return genericStringBuilder.toString();
    }


    /**
     * this functions converts byte to KB, MB, GB, TB with 3 decimal places
     * @param size number to convert
     * @return coverted number with unit size  (ex. 1,234 KB)
     */
    private String formatSize(long size) {
        double m = size / 1024.0;
        double g = size / 1048576.0;
        double t = size / 1073741824.0;

        DecimalFormat dec = new DecimalFormat("0.000");
        String genericString;
        if (t > 1) {
            genericString = dec.format(t).concat(" TB");
        } else if (g > 1) {
            genericString = dec.format(g).concat(" GB");
        } else if (m > 1) {
            genericString = dec.format(m).concat(" MB");
        } else {
            genericString = size + " KB";
        }
        return genericString;
    }
}
