package pcstatus.dataPackage;

import org.hyperic.sigar.*;
import org.gridkit.lab.sigar.SigarFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.util.FormatUtil;
import pcstatus.ErrorManager;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;

public class GeneralStats {

    private static String spacing = "     ";

    public static String[] getPcInfo() throws SigarException, InterruptedException {
        //this functions use Sigar library because is more faster then Oshi

        CpuPerc cpuperc = SigarFactory.newSigar().getCpuPerc();
        CentralProcessor processor = new SystemInfo().getHardware().getProcessor();

        String[] pc = new String[6];
        pc[0] = spacing + "Vendor: " + processor.getVendor();
        pc[1] = spacing + processor.getName();
        pc[2] = spacing + "Clock: " + FormatUtil.formatHertz(processor.getVendorFreq());
        pc[3] = spacing + "Physical CPU(s): " + processor.getPhysicalProcessorCount();
        pc[4] = spacing + "Logical CPU(s): " + processor.getLogicalProcessorCount();
        pc[5] = spacing + "CPU load: " + round((float) (cpuperc.getCombined() * 100), 2) + "%";

        SingletonNumericGeneralStats.getInstance().setCpuLoad(round((float) (cpuperc.getCombined() * 100), 2));

        return pc;
    }

    public static String round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    private static String formatSize(long size) {
        String hrSize = "";
        long k = size;
        double m = size / 1024.0;
        double g = size / 1048576.0;
        double t = size / 1073741824.0;

        DecimalFormat dec = new DecimalFormat("0.000");
        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = size + " KB";
        }
        return hrSize;
    }

    /*public static void writeTofile(String str) {

        try {
            Sigar sigar = new Sigar();
            // Full path to /p/stat/drupal most likely needed for the .html file
            FileWriter fstream = new FileWriter("/p/stat/drupal/systems_reports/" + machineName(sigar) + ".html");
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(str);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }*/

    public static String getComputerSystemInfo() {

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        oshi.software.os.OperatingSystem os = si.getOperatingSystem();
        ComputerSystem computerSystem = hal.getComputerSystem();
        StringBuilder computerSystemString = new StringBuilder();

        computerSystemString.append(spacing + os + "\n");
        computerSystemString.append(spacing + computerSystem.getManufacturer() + " " + computerSystem.getModel() + "\n");
        /*computerSystemString.append(spacing + "Model: " + computerSystem.getModel() + "\n");
        computerSystemString.append(spacing + "Serialnumber: " + computerSystem.getSerialNumber() + "\n\n");*/

        final Baseboard baseboard = computerSystem.getBaseboard();
        computerSystemString.append(spacing + "Baseboard:" + "\n");
        computerSystemString.append(spacing + spacing + "manufacturer: " + baseboard.getManufacturer() + "\n");
        computerSystemString.append(spacing + spacing + "model: " + baseboard.getModel() + "\n");
        computerSystemString.append(spacing + spacing + "version: " + baseboard.getVersion() + "\n");
        //computerSystemString.append(spacing + "  serialnumber: " + baseboard.getSerialNumber() + "\n");

        return computerSystemString.toString();

    }

    public static String getRamMemory() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        GlobalMemory memory = hal.getMemory();

        SingletonNumericGeneralStats.getInstance().setFreeRam(memory.getAvailable(), memory.getTotal());
        return spacing + "Memory: " + FormatUtil.formatBytes(memory.getAvailable()) + " free of "
                + FormatUtil.formatBytes(memory.getTotal());
    }

    public static String getFileSystem() {
        SystemInfo si = new SystemInfo();

        oshi.software.os.OperatingSystem os = si.getOperatingSystem();
        oshi.software.os.FileSystem fileSystem = os.getFileSystem();

        OSFileStore[] fsArray = fileSystem.getFileStores();
        String[] numericSpace = new String[fsArray.length];
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fsArray.length; i++) {
            OSFileStore fs = fsArray[i];
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            stringBuilder.append(spacing + String.format(" %s (%s) [%s] %s of %s free (%.1f%%) " +
                            (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s") +
                            "%n", fs.getName(),
                    fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total, fs.getLogicalVolume()));
            numericSpace[i] = round((float) (100d * usable / total),1);
            /*System.out.format(" %s (%s) [%s] %s of %s free (%.1f%%) is %s " +
                            (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s") +
                            " and is mounted at %s%n", fs.getName(),
                    fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    fs.getVolume(), fs.getLogicalVolume(), fs.getMount());*/
        }
        SingletonNumericGeneralStats.getInstance().setAvaibleFileSystem(numericSpace);
        return stringBuilder.toString();
    }


    private static String getDefaultNetworkInteface() throws SocketException, UnknownHostException {
        final Enumeration<NetworkInterface> netifs = NetworkInterface.getNetworkInterfaces();

        // hostname is passed to your method
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

    public static String getNetworkSpeed() throws SocketException, UnknownHostException {

        String networkName = getDefaultNetworkInteface();
        System.out.println(networkName);

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        NetworkIF[] networkIFs = hal.getNetworkIFs();
        int i = 0;
        NetworkIF net = networkIFs[0];
        while (!networkIFs[i].getName().equals(networkName)) {
            net = networkIFs[i];
            i++;
            System.out.println(networkIFs[i].getName() + " " + networkName);
        }

        //  System.out.format("   MAC Address: %s %n", net.getMacaddr());
        //System.out.format("   MTU: %s, Speed: %s %n", net.getMTU(), FormatUtil.formatValue(net.getSpeed(), "bps"));
        long download1 = net.getBytesRecv();
        long upload1 = net.getBytesSent();
        long timestamp1 = net.getTimeStamp();
        try {
            Thread.sleep(1000); //Sleep for a bit longer, 2s should cover almost every possible problem
        } catch (InterruptedException e) {
            ErrorManager.exeptionDialog(e);
            e.printStackTrace();
        }
        net.updateNetworkStats(); //Updating network stats
        long download2 = net.getBytesRecv();
        long upload2 = net.getBytesSent();
        long timestamp2 = net.getTimeStamp();

        StringBuilder sb = new StringBuilder();
        sb.append(spacing + "download speed: " + formatSize((download2 - download1) / (timestamp2 - timestamp1)) + "/s\n");
        sb.append(spacing + "upload speed: " + formatSize((upload2 - upload1) / (timestamp2 - timestamp1)) + "/s\n");

        return sb.toString();
    }
}