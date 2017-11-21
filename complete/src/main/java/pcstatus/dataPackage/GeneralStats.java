package pcstatus.dataPackage;

import org.gridkit.lab.sigar.SigarFactory;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.util.FormatUtil;
import pcstatus.ErrorManager;
import pcstatus.SingletonBatteryStatus;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Enumeration;

public class GeneralStats {


    private static SystemInfo systemInfo = new SystemInfo();
    private static OSFileStore[] fsArray;
    private static oshi.software.os.FileSystem fileSystem;

    public static String[] getPcInfo() {



        try {
            CpuPerc[] cpuperclist = SigarFactory.newSigar().getCpuPercList();
            StringBuilder percPerThread = new StringBuilder();
            for (CpuPerc aCpuperclist : cpuperclist) {
                percPerThread.append(round((float) (aCpuperclist.getCombined() * 100), 2) + "\n");
            }
            SingletonNumericGeneralStats.getInstance().setPercPerThread(percPerThread.toString());
        } catch (SigarException e) {
            String[] error = new String[1];
            error[0] = "cpu not supported";
            e.printStackTrace();
            return error;
        }

        CentralProcessor processor = systemInfo.getHardware().getProcessor();

        String[] cpuInfo = new String[6];
        cpuInfo[0] = "Vendor: " + processor.getVendor();
        cpuInfo[1] =  processor.getName();
        cpuInfo[2] =  "Clock: " + FormatUtil.formatHertz(processor.getVendorFreq());
        cpuInfo[3] =  "Physical CPU(s): " + processor.getPhysicalProcessorCount();
        cpuInfo[4] =  "Logical CPU(s): " + processor.getLogicalProcessorCount();
        cpuInfo[5] =  "CPU load: " + round((float) (processor.getSystemCpuLoad() * 100), 2) + "%";

        SingletonNumericGeneralStats.getInstance().setCpuInfo(cpuInfo);
        SingletonBatteryStatus.getInstance().setNumericCpuLoad(round((float) (processor.getSystemCpuLoad() * 100), 2));

        return cpuInfo;
    }

    public static String getCpuLoad() {
        try {
            CpuPerc[] cpuperclist = SigarFactory.newSigar().getCpuPercList();
            StringBuilder percPerThread = new StringBuilder();
            for (CpuPerc aCpuperclist : cpuperclist) {
                percPerThread.append(round((float) (aCpuperclist.getCombined() * 100), 2) + "\n");
            }
            SingletonNumericGeneralStats.getInstance().setPercPerThread(percPerThread.toString());
            SingletonBatteryStatus.getInstance().setPercPerThread(percPerThread.toString());
        } catch (SigarException e) {
            SingletonNumericGeneralStats.getInstance().setPercPerThread("0");
            SingletonBatteryStatus.getInstance().setPercPerThread("0");
            e.printStackTrace();
        }

        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        SingletonNumericGeneralStats.getInstance().setCpuLoad(round((float) (processor.getSystemCpuLoad() * 100), 2));
        SingletonBatteryStatus.getInstance().setNumericCpuLoad(round((float) (processor.getSystemCpuLoad() * 100), 2));
        return  "CPU load: " + round((float) (processor.getSystemCpuLoad() * 100), 2) + "%";
    }

    static String round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    private static String formatSize(long size) {
        String hrSize;
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

    public static String getComputerSystemInfo() {

       // SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        oshi.software.os.OperatingSystem os = systemInfo.getOperatingSystem();
        ComputerSystem computerSystem = hal.getComputerSystem();
        StringBuilder computerSystemString = new StringBuilder();

        computerSystemString.append( os + "\n");
        computerSystemString.append( computerSystem.getManufacturer() + " " + computerSystem.getModel() + "\n");
        computerSystemString.append( "RAM: " + FormatUtil.formatBytes(hal.getMemory().getTotal()) + "\n\n");

        final Baseboard baseboard = computerSystem.getBaseboard();
        computerSystemString.append("Baseboard:" + "\n");
        computerSystemString.append("manufacturer: " + baseboard.getManufacturer() + "\n");
        computerSystemString.append("     " + "model: " + baseboard.getModel() + "\n");
        computerSystemString.append("     " + "version: " + baseboard.getVersion() + "\n");

        SingletonNumericGeneralStats.getInstance().setSystemInformation(computerSystemString.toString());

        return computerSystemString.toString();
    }

    public static String getRamMemory() {
        //SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        GlobalMemory memory = hal.getMemory();

        SingletonNumericGeneralStats.getInstance().setFreeRam(memory.getAvailable(), memory.getTotal());
        return  "Memory: " + FormatUtil.formatBytes(memory.getAvailable()) + " free of "
                + FormatUtil.formatBytes(memory.getTotal());
    }

    public static String getFileSystem() {
        //SystemInfo si = new SystemInfo();

        //oshi.software.os.OperatingSystem os = systemInfo.getOperatingSystem();
        fileSystem = systemInfo.getOperatingSystem().getFileSystem();

        fsArray = fileSystem.getFileStores();
        String[] numericSpace = new String[fsArray.length];
        String[] volume = new String[fsArray.length];
        String[] stringBuilder = new String[fsArray.length];
        for (int i = 0; i < fsArray.length; i++) {
            OSFileStore fs = fsArray[i];
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            stringBuilder[i] = ( String.format(" %s (%s) [%s] %s of %s free (%.1f%%) " +
                            (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s") +
                            "%n", fs.getName(),
                    fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total, fs.getLogicalVolume()));
            try {
                if (total == 0) {
                    volume[i] = fs.getMount();
                    numericSpace[i] = "0";
                } else {
                    volume[i] = fs.getMount();
                    numericSpace[i] = String.format("%.1f", (100d * usable / total)).replace(",", ".");
                }
            } catch (NumberFormatException e) {
                numericSpace[i] = "error";
            }
        }

        for (int i = 0; i < numericSpace.length; i++) {
            String tmp;
            if (volume[i].equals("C:\\")) {
                tmp = numericSpace[0];
                numericSpace[0] = numericSpace[i];
                numericSpace[i] = tmp;

                tmp = stringBuilder[0];
                stringBuilder[0] = stringBuilder[i];
                stringBuilder[i] = tmp;
            }
        }
        SingletonNumericGeneralStats.getInstance().setAvaibleFileSystem(numericSpace);
        return String.join("", stringBuilder);
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

    public static String getNetworkSpeed() {

        String networkName;
        StringBuilder sb = new StringBuilder();

        try {
            networkName = getDefaultNetworkInteface();
        } catch (SocketException | UnknownHostException e) {
            sb.append( "download speed: not supported\n");
            sb.append( "upload speed: not supported\n");
            e.printStackTrace();
            return sb.toString();
        }
        //System.out.println(networkName);

        //SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        NetworkIF[] networkIFs = hal.getNetworkIFs();
        int i = 0;
        NetworkIF net = networkIFs[0];
        try {
            while (!networkIFs[i].getName().equals(networkName)) {
                net = networkIFs[i];
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sb.append( "download speed: not supported\n");
            sb.append( "upload speed: not supported\n");
            return sb.toString();
        }

        long download1 = net.getBytesRecv();
        long upload1 = net.getBytesSent();
        long timestamp1 = net.getTimeStamp();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            ErrorManager.exeptionDialog(e);
            e.printStackTrace();
        }
        net.updateNetworkStats(); //Updating network stats
        long download2 = net.getBytesRecv();
        long upload2 = net.getBytesSent();
        long timestamp2 = net.getTimeStamp();

        sb.append( "download speed: " + formatSize((download2 - download1) / (timestamp2 - timestamp1)) + "/s\n");
        sb.append( "upload speed: " + formatSize((upload2 - upload1) / (timestamp2 - timestamp1)) + "/s\n");

        return sb.toString();
    }


    //todo funzione in sospeso finché non miglioro ulteriormente le performance (meglio usare Sigar, è più veloce)
  /*  public static String getRamPerProcess() {
        OperatingSystem operatingSystem = new SystemInfo().getOperatingSystem();
        StringBuilder stringBuilder = new StringBuilder();
        for (OSProcess process : operatingSystem.getProcesses(5, OperatingSystem.ProcessSort.MEMORY)) {
            long privateWorkingSet = Long.parseLong(WmiUtil.selectStringFrom(null, "Win32_perfRawData_PerfProc_Process",
                    "WorkingSetPrivate", "WHERE IDprocess = " + process.getProcessID())); //retrieve memory in bytes
            stringBuilder.append(process.getName() + "\t" + privateWorkingSet + "\n");
        }
        System.out.println("\n\n\n" + stringBuilder.toString() + "\n\n");
        return stringBuilder.toString();

        /*long startTime = System.currentTimeMillis();
        OperatingSystem operatingSystem = new SystemInfo().getOperatingSystem();
        StringBuilder stringBuilder = new StringBuilder();
        ProcMem procMem;

        long[] processes = SigarFactory.newSigar().getProcList();
        System.out.println("\n\n\n\n\n ");
        for (int i = 0; i < processes.length; i++) {
            procMem = SigarFactory.newSigar().getProcMem(processes[i]);
            System.out.println("pid " + processes[i] + " " + (procMem.getShare()) + " workingset: " + (procMem.getSize() / 1024) + " " + (procMem.getResident() / 1024));
        }
        System.out.println("\n\n\n\n\n ");
        long stopTime = System.currentTimeMillis();
        System.out.println("time to execute code " + (stopTime - startTime) + "");

    }*/


    // for linux SO
  /*  public static String getFileSystem()  {
        SigarProxy sigar = SigarFactory.newSigar();
        FileSystem[] fslist = new FileSystem[0];
        try {
            fslist = sigar.getFileSystemList();
        } catch (SigarException e) {
            e.printStackTrace();
            return "function not supported in in Sigar declaration";
        }
        FileSystemUsage filesystemusage;

        String[] numericSpace = new String[fslist.length];

        String disks = "";
        String type = "";
        String size = "";
        String usage = "";
        String ris = "";

        for (int i = 0; i < fslist.length; i++) {
            disks =  "Label: " + fslist[i].getDevName();
            type = "Type: " + fslist[i].getTypeName();
            try {
                filesystemusage = sigar.getMountedFileSystemUsage(fslist[i].getDevName());
            } catch (SigarException e) {
                e.printStackTrace();
                return "function not supported in getting filesystem";
            }
            size = "Size: " + formatSize(filesystemusage.getTotal());
            usage = "Used " + (filesystemusage.getUsePercent() * 100) + "%\n";
            String disksType = String.format("%-20s %s", disks, type);
            String sizeUsage = String.format("%-20s %s", size, usage);
            ris += disksType + "\t" + sizeUsage;

            try {
                if (filesystemusage.getTotal() == 0) {
                    numericSpace[i] = "0";
                } else {
                    numericSpace[i] = round((100 * filesystemusage.getFree() / filesystemusage.getTotal()), 1);
                }
            } catch (NumberFormatException e) {
                numericSpace[i] = "error";
            }
        }
        SingletonNumericGeneralStats.getInstance().setAvaibleFileSystem(numericSpace);
        // System.out.println(ris);
        return ris;
    }*/
}
