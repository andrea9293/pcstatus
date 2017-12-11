/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.dataPackage;

import oshi.SystemInfo;
import oshi.util.FormatUtil;

import java.math.BigDecimal;

/**
 * this class contain getters of all static information of computer like name of pc, types of cpu hardware,
 * ram memory and baseboard information.
 *
 * @author Andrea Bravaccino
 */
public class GeneralStats {
    /**
     * Intstance of singleton
     */
    private static GeneralStats ourInstance = new GeneralStats();

    /**
     * empty constructor
     */
    private GeneralStats() {
    }

    /**
     * getter for current instance of <code>GeneralStats</code>
     * @return current instance of <code>GeneralStats</code>
     */
    public static GeneralStats getInstance() {
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
     * this function, using Oshi lib, retrieve information about CPU.
     * Before returns, this function invoke <code>setCpuInfoInModel(String[] cpuInfo)</code>
     *
     * @see GeneralStats#setCpuInfoInModel(String[])
     * @return array string contains information about CPU with this format
     * <p>
     * cpuInfo[0] = Vendor
     * <p>
     * cpuInfo[1] = model of CPU
     * <p>
     * cpuInfo[2] = frequency of CPU
     * <p>
     * cpuInfo[3] = number of physical processors
     * <p>
     * cpuInfo[4] = number of logical processors
     * <p>
     * cpuInfo[5] = percentage of CPU load
     * <p>
     * In case of problems return an array with only one string: "cpu not supported"
     */
    public String[] getCpuInfo() {
        String[] cpuInfo = new String[6];
        cpuInfo[0] = "Vendor: " + systemInfo.getHardware().getProcessor().getVendor();
        cpuInfo[1] = systemInfo.getHardware().getProcessor().getName();
        cpuInfo[2] = "Clock: " + FormatUtil.formatHertz(systemInfo.getHardware().getProcessor().getVendorFreq());
        cpuInfo[3] = "Physical CPU(s): " + systemInfo.getHardware().getProcessor().getPhysicalProcessorCount();
        cpuInfo[4] = "Logical CPU(s): " + systemInfo.getHardware().getProcessor().getLogicalProcessorCount();
        //cpuInfo[5] = round((float) (systemInfo.getHardware().getProcessor().getSystemCpuLoad() * 100), 2);

        setCpuInfoInModel(cpuInfo);
        return cpuInfo;
    }

    /**
     * this function call a setter from model (SingletonStaticGeneralStats) that sets
     * the percentage of cpu load
     * @see GeneralStats#getCpuInfo()
     * @see SingletonStaticGeneralStats#setCpuInfo(String[])
     * @param cpuInfo information about CPU
     */
    private void setCpuInfoInModel(String[] cpuInfo){
        SingletonStaticGeneralStats.getInstance().setCpuInfo(cpuInfo);
    }

    /**
     * this function round up a number
     * @param value the number to be rounded
     * @param decimalPlace how many decimal places are needed
     * @return rounded number
     */
    Float round(float value, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * using oshi lib, this function retrieve information like SO, name of PC, total RAM and informations about baseboard
     * @return a string with all informations retrieved separated by a "\n"
     */
    public String getComputerSystemInfo() {
        genericStringBuilder.setLength(0);
        genericStringBuilder.append(systemInfo.getOperatingSystem()).append("\n");
        genericStringBuilder.append(systemInfo.getHardware().getComputerSystem().getManufacturer()).append(" ").append(systemInfo.getHardware().getComputerSystem().getModel()).append("\n");
        genericStringBuilder.append("RAM: ").append(FormatUtil.formatBytes(systemInfo.getHardware().getMemory().getTotal())).append("\n\n");

        genericStringBuilder.append("Baseboard:").append("\n");
        genericStringBuilder.append("manufacturer: ").append(systemInfo.getHardware().getComputerSystem().getBaseboard().getManufacturer()).append("\n");
        genericStringBuilder.append("     ").append("model: ").append(systemInfo.getHardware().getComputerSystem().getBaseboard().getModel()).append("\n");
        genericStringBuilder.append("     ").append("version: ").append(systemInfo.getHardware().getComputerSystem().getBaseboard().getVersion()).append("\n");

        SingletonStaticGeneralStats.getInstance().setSystemInformation(genericStringBuilder.toString());

        return genericStringBuilder.toString();
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
  /*  public static String getFileSystems()  {
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
        SingletonDynamicGeneralStats.getInstance().setAvaibleFileSystem(numericSpace);
        // System.out.println(ris);
        return ris;
    }*/
}
