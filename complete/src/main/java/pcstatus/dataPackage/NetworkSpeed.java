package pcstatus.dataPackage;

import org.hyperic.sigar.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Headgrowe
 */
public class NetworkSpeed {

    static Map<String, Long> rxCurrentMap = new HashMap<String, Long>();
    static Map<String, List<Long>> rxChangeMap = new HashMap<String, List<Long>>();
    static Map<String, Long> txCurrentMap = new HashMap<String, Long>();
    static Map<String, List<Long>> txChangeMap = new HashMap<String, List<Long>>();
    private static Sigar sigar;

    /**
     * @throws InterruptedException
     * @throws SigarException
     */
    public NetworkSpeed() {

    }

    public NetworkSpeed(Sigar s) throws SigarException, InterruptedException {
        sigar = s;
        getMetric();
        System.out.println(networkInfo());
        //Thread.sleep(1000);
    }

    public static String[] getSpeed() throws SigarException, InterruptedException {
        new NetworkSpeed(new Sigar());
        return newMetricThread();
    }

    public static String networkInfo() throws SigarException {
        //NetInfo info = sigar.getNetInfo();
        NetInterfaceConfig networkInterface = sigar.getNetInterfaceConfig();
        return networkInterface.getDescription();
    }

    public static String getDefaultGateway() throws SigarException {
        return sigar.getNetInfo().getDefaultGateway();
    }

    public static String[] newMetricThread() throws SigarException, InterruptedException {
        Long[] m = getMetric();
        long totalrx = m[0];
        long totaltx = m[1];
        System.out.print("totalrx(download): ");
        System.out.println("\t" + Sigar.formatSize(totalrx));
        System.out.print("totaltx(upload): ");
        System.out.println("\t" + Sigar.formatSize(totaltx));
        System.out.println("-----------------------------------");
        String[] speed = new String[2];
        speed[0] = Sigar.formatSize(totalrx);
        speed[1] = Sigar.formatSize(totaltx);
        return speed;
    }


    public static Long[] getMetric() throws SigarException {
        for (String ni : sigar.getNetInterfaceList()) {
            // System.out.println(ni);
            NetInterfaceStat netStat = sigar.getNetInterfaceStat(ni);
            NetInterfaceConfig ifConfig = sigar.getNetInterfaceConfig(ni);
            String hwaddr = null;
            if (!NetFlags.NULL_HWADDR.equals(ifConfig.getHwaddr())) {
                hwaddr = ifConfig.getHwaddr();
            }
            if (hwaddr != null) {
                long rxCurrenttmp = netStat.getRxBytes();
                saveChange(rxCurrentMap, rxChangeMap, hwaddr, rxCurrenttmp, ni);
                long txCurrenttmp = netStat.getTxBytes();
                saveChange(txCurrentMap, txChangeMap, hwaddr, txCurrenttmp, ni);
            }
        }
        long totalrx = getMetricData(rxChangeMap);
        long totaltx = getMetricData(txChangeMap);
        for (List<Long> l : rxChangeMap.values())
            l.clear();
        for (List<Long> l : txChangeMap.values())
            l.clear();
        return new Long[] { totalrx, totaltx };
    }

    private static long getMetricData(Map<String, List<Long>> rxChangeMap) {
        long total = 0;
        for (Entry<String, List<Long>> entry : rxChangeMap.entrySet()) {
            int average = 0;
            for (Long l : entry.getValue()) {
                average += l;
            }
            if(entry.getValue().size() != 0)
                total += average / entry.getValue().size();
        }
        return total;
    }

    private static void saveChange(Map<String, Long> currentMap,
                                   Map<String, List<Long>> changeMap, String hwaddr, long current,
                                   String ni) {
        Long oldCurrent = currentMap.get(ni);
        if (oldCurrent != null) {
            List<Long> list = changeMap.get(hwaddr);
            if (list == null) {
                list = new LinkedList<Long>();
                changeMap.put(hwaddr, list);
            }
            list.add((current - oldCurrent));
        }
        currentMap.put(ni, current);
    }
}