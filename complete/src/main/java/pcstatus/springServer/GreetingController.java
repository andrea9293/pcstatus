package pcstatus.springServer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pcstatus.dataPackage.SingletonBatteryStatus;
import pcstatus.dataPackage.GeneralStats;
import pcstatus.dataPackage.Kernel32;
import pcstatus.dataPackage.SingletonNumericGeneralStats;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();
    public static ExecutorService getCpuInfo = Executors.newSingleThreadExecutor();
    public static ExecutorService getNetworkSpeed = Executors.newSingleThreadExecutor();
    private static Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();

    private static final String[][] cpuInfo = {null};
    private static String diskString = null;
    private static String computerInfoString = null;
    private static String miscellaneousString = null;
    private static String numericCpuLoad = null;
    private static String[] numericAvaibleFileSystem = null;
    private static String numericFreeRam = null;
    private static final String[] networkSpeed = {null};
    private static String numericPercPerThread = null;
    private static String numericBatteryPerc = null ;

    private static String[] disks = null;
    private static String[] computerInfo = null;
    private static String[] miscellaneous = null;
    private static StringBuilder sb = new StringBuilder();
    private static String ramMemory;


    @RequestMapping("/greeting")
    public Greeting greeting() {

        //Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        String template = batteryStatus.toString();
        String batteryParts[] = template.split("\n");
       /* final String[][] cpuInfo = {null};
        String disks = null;
        String computerInfo = null;
        String miscellaneous = null;
        String numericCpuLoad = null;
        String[] numericAvaibleFileSystem = null;
        String numericFreeRam = null;
        final String[] networkSpeed = {null};
        String numericPercPerThread = null;
        String numericBatteryPerc = null ;
        //String numericRamPerProcess = null;*/

        //ramPerProcessThread
      /*  new Thread(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("prelevo i ramperprocess...");
            SingletonNumericGeneralStats.getInstance().setRamPerProcess(GeneralStats.getRamPerProcess());
            System.out.println("finito prelevo i ramperprocess...\n");
            long stopTime = System.currentTimeMillis();
            System.out.println("\n\nfinito ramperprocess e ci ho messo " + (stopTime - startTime) + "\n" );
        }).start();*/

        final CountDownLatch latch = new CountDownLatch(2);


        getCpuInfo.execute(new Thread(() -> {
            Thread.currentThread().setName("getCpuInfo");
            cpuInfo[0] = SingletonNumericGeneralStats.getInstance().getCpuInfo();
            cpuInfo[0][5] = GeneralStats.getCpuLoad();
            latch.countDown();
        },"getCpuInfo"));


/*
        new Thread(() -> {
            cpuInfo[0] = SingletonNumericGeneralStats.getInstance().getCpuInfo();
            cpuInfo[0][5] = GeneralStats.getCpuLoad();
            latch.countDown();
        },"getCpuInfo").start();*/

        getNetworkSpeed.execute(new Thread(() -> {
            Thread.currentThread().setName("getNetworkSpeed");
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
            latch.countDown();
        },"getNetworkSpeed"));

        /*new Thread(() -> {
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
            latch.countDown();
        },"getNetworkSpeed").start();*/

        diskString = GeneralStats.getFileSystem();

        computerInfoString = SingletonNumericGeneralStats.getInstance().getSystemInformation();

        StringBuilder sb = new StringBuilder();
        String ramMemory = GeneralStats.getRamMemory() + "\n";

        try {
            //System.out.println(latch.getCount());
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //cpuInfo[0] = GeneralStats.getPcInfo();
        sb.append(cpuInfo[0][5] + "\n");
        sb.append(ramMemory);
        sb.append(batteryParts[1] + "\n");
        sb.append(networkSpeed[0]);

        miscellaneousString = sb.toString();

        numericBatteryPerc = batteryParts[1].replaceAll("[^0-9]", "");
        SingletonBatteryStatus.getInstance().setBatteryPerc(numericBatteryPerc);
        numericAvaibleFileSystem = SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem();
        numericCpuLoad = SingletonNumericGeneralStats.getInstance().getCpuLoad();
        numericFreeRam = SingletonNumericGeneralStats.getInstance().getFreeRam();
        numericPercPerThread = SingletonNumericGeneralStats.getInstance().getPercPerThread();

        //System.gc();
        return new Greeting(counter.incrementAndGet(), template, batteryParts, cpuInfo[0], diskString, computerInfoString, miscellaneousString, numericAvaibleFileSystem, numericCpuLoad, numericFreeRam, numericPercPerThread, numericBatteryPerc/* SingletonNumericGeneralStats.getInstance().getRamPerProcess()*/);
    }

    //todo provare a spostare getAllData nel metodo greeting così da creare il json anche con il bluetooth senza connessione ad internet
    public static void getAllData() {
       /* final String[][] cpuInfo = {null};
        String[] disks = null;
        String[] computerInfo = null;
        String[] miscellaneous = null;
        String numericCpuLoad = null;
        String[] numericAvaibleFileSystem = null;
        String numericFreeRam = null;
        String numericPercPerThread = null;
        final String[] networkSpeed = {null};
        String numericBatteryPerc = null ;*/


        //String numericRamPerProcess = null;

        //ramPerProcessThread
      /*  new Thread(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("prelevo i ramperprocess...");
            SingletonNumericGeneralStats.getInstance().setRamPerProcess(GeneralStats.getRamPerProcess());
            System.out.println("finito prelevo i ramperprocess...\n");
            long stopTime = System.currentTimeMillis();
            System.out.println("\n\nfinito ramperprocess e ci ho messo " + (stopTime - startTime) + "\n" );
        }).start();*/

        final CountDownLatch latch = new CountDownLatch(2);

        new Thread(() -> {
            cpuInfo[0] = GeneralStats.getPcInfo();
            latch.countDown();
        }, "getPCinfoAllData").start();

        new Thread(() -> {
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
            latch.countDown();
        }, "getNetworkSpeedAllData").start();

        //Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        String template = batteryStatus.toString();
        String batteryParts[] = template.split("\n");
        SingletonBatteryStatus.getInstance().setBattery(batteryParts);

        disks = GeneralStats.getFileSystem().split("\n");

        SingletonBatteryStatus.getInstance().setDisks(disks);

        computerInfo = GeneralStats.getComputerSystemInfo().split("\n");

        SingletonBatteryStatus.getInstance().setComputerInfo(computerInfo);

        //StringBuilder sb = new StringBuilder();

        ramMemory = GeneralStats.getRamMemory() + "\n";

        try {
            //System.out.println(latch.getCount());
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //cpuInfo[0] = GeneralStats.getPcInfo();
        //SingletonNumericGeneralStats.getInstance().setCpuLoad(cpuInfo[0][5]);
        sb.append(cpuInfo[0][5] + "\n");
        sb.append(ramMemory);
        sb.append(batteryParts[1] + "\n");
        sb.append(networkSpeed[0]);

        miscellaneous = sb.toString().split("\n");

        SingletonBatteryStatus.getInstance().setMiscellaneous(miscellaneous);

        SingletonBatteryStatus.getInstance().setCpu(cpuInfo[0]);

        SingletonBatteryStatus.getInstance().setAvaibleFileSystem(SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem());

        numericBatteryPerc = batteryParts[1].replaceAll("[^0-9]", "");
        SingletonBatteryStatus.getInstance().setBatteryPerc(numericBatteryPerc);
        SingletonBatteryStatus.getInstance().setPercPerThread(SingletonNumericGeneralStats.getInstance().getPercPerThread());

        //System.gc();
        SingletonBatteryStatus.getInstance().notifyMyObservers();
    }
}
