package pcstatus.dataPackage;

import org.hyperic.sigar.*;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.util.FormatUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

public class GeneralStats {

    public static String[] getPcInfo() throws SigarException, InterruptedException {

     /*   // New instance of SIGAR to do our work and get stuff

        Sigar sigar = new Sigar();

        CpuPerc cpuperc = sigar.getCpuPerc();


        CpuInfo[] cpuInfo = sigar.getCpuInfoList();
        //CpuPerc cpuperc = sigar.getCpuPerc();

        String[] pc = new String[6];
        pc[0] = cpuInfo[0].getVendor();
        pc[1] = cpuInfo[0].getModel();
        pc[2] = cpuInfo[0].getMhz() + " MHz";
        pc[3] = cpuInfo[0].getTotalCores() + " cores";
        pc[4] = round((float) cleanMem(sigar), 2) + " GB";
        pc[5] = round((float) (cpuperc.getCombined() * 100), 2) + "%";*/

        CpuPerc cpuperc = new Sigar().getCpuPerc();
        CentralProcessor processor = new SystemInfo().getHardware().getProcessor();

        String[] pc = new String[6];
        pc[0] = "Vendor: " + processor.getVendor();
        pc[1] = processor.getName();
        pc[2] = "Clock: " + FormatUtil.formatHertz(processor.getVendorFreq());
        pc[3] = "Physical CPU(s): " + processor.getPhysicalProcessorCount();
        pc[4] = "Logical CPU(s): " + processor.getLogicalProcessorCount();
        //pc[5] = round((float) cleanMem(new Sigar()), 2) + " GB";
        pc[5] = "CPU load: " + round((float) (cpuperc.getCombined() * 100), 2) + "%";
        //pc[5] = String.format("CPU load: %.1f%% ",processor.getSystemCpuLoad()*100);


        return pc;
        //writeTofile(html);

    }

    public static String round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    public static String getDiskStats() throws InterruptedException, SigarException {
        Sigar sigar = new Sigar();
        FileSystem[] fslist = sigar.getFileSystemList();
        FileSystemUsage filesystemusage;


        String disks = "";
        String type = "";
        String size = "";
        String usage = "";
        String ris = "";

        for (int i = 0; i < fslist.length; i++) {
            disks = "Label: " + fslist[i].getDevName();
            type = "Type: " + fslist[i].getTypeName();
            filesystemusage = sigar.getFileSystemUsage(fslist[i].getDevName());
            size = "Size: " + formatSize(filesystemusage.getTotal());
            usage = "Used " + (filesystemusage.getUsePercent() * 100) + "%\n";
            String disksType = String.format("%-20s %s", disks, type);
            String sizeUsage = String.format("%-20s %s", size, usage);
            ris += disksType + "\t" + sizeUsage;
        }
        // System.out.println(ris);
        return ris;
    }

    public static String formatSize(long size) {
        String hrSize = "";
        long k = size;
        double m = size / 1024.0;
        double g = size / 1048576.0;
        double t = size / 1073741824.0;

        DecimalFormat dec = new DecimalFormat("0.00");
        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
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

    public static String machineName(Sigar sigar) throws SigarException {
        return sigar.getFQDN();
    }

    public static String cleanCpuPerc(Sigar sigar) throws SigarException {

        CpuPerc cpu = sigar.getCpuPerc();
        String cpu1 = cpu.toString();

        // Remove unwanted information from the output of SIGAR, stripping it down to just the numbers
        //cpu1 = cpu1.replaceAll("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz:,]","");

        // Remove the first 11 characters and then
        // everything after the letter "t" in "system"from the                                // we want to style these headings ourselves via HTML/CSS. Theres probably a better way.
        int length = cpu1.length();

        cpu1 = cpu1.substring(11, length);

        int spaceIndex = cpu1.indexOf("t");

        cpu1 = cpu1.substring(0, spaceIndex);

        return cpu1;

    }

    public static double cleanMem(Sigar sigar) throws SigarException {

        Mem mem = sigar.getMem();
        String mem1 = mem.toString();

        int length = mem1.indexOf("K");

        mem1 = mem1.substring(5, length);

        // Convert mem1 result from K to GB
        double kilobytes = Double.parseDouble(mem1);
        double megabytes = (kilobytes / 1024);
        double gigabytes = (megabytes / 1024);


        // For quick math testing to Eclipse console
        // System.out.println("gigabytes : " + gigabytes);

        return gigabytes;

    }

    public static String loadAvg(Sigar sigar) throws SigarException {

        double[] loadAvg = sigar.getLoadAverage();

        // Convert loadAvg from an array to a string so that we can print it
        return Arrays.toString(loadAvg);

    }

    public static StringBuilder who(Sigar sigar) throws SigarException {

        Who[] who = sigar.getWhoList();
        String str = new String();
        StringBuilder nameList = new StringBuilder();

        for (int i = 0; i < who.length; i++) {
            /*int a = who[i].toString().indexOf("=");
            int b = who[i].toString().indexOf(",");

            str = who[i].toString().substring(a + 1, b);
            System.out.println(str + "\n");*/

            System.out.println(who[i].getDevice());
            System.out.println(who[i].getHost());
            System.out.println(who[i].getUser() + "\n\n");
        }

        return (nameList);

    }

}