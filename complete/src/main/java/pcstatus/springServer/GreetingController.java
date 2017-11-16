package pcstatus.springServer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pcstatus.SingletonBatteryStatus;
import pcstatus.dataPackage.GeneralStats;
import pcstatus.dataPackage.Kernel32;
import pcstatus.dataPackage.SingletonNumericGeneralStats;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting() {

        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        String template = batteryStatus.toString();
        String batteryParts[] = template.split("\n");
        final String[][] cpuInfo = {null};
        String disks = null;
        String computerInfo = null;
        String miscellaneous = null;
        String numericCpuLoad = null;
        String[] numericAvaibleFileSystem = null;
        String numericFreeRam = null;
        final String[] networkSpeed = {null};
        String numericPercPerThread = null;
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
            cpuInfo[0] = SingletonNumericGeneralStats.getInstance().getCpuInfo();
            cpuInfo[0][5] = GeneralStats.getCpuLoad();//todo a volte crasha, capisci perché
            latch.countDown();
        }).start();

        new Thread(() -> {
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
            latch.countDown();
        }).start();

        disks = GeneralStats.getFileSystem();

        computerInfo = SingletonNumericGeneralStats.getInstance().getSystemInformation();

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

        miscellaneous = sb.toString();

        numericAvaibleFileSystem = SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem();
        numericCpuLoad = SingletonNumericGeneralStats.getInstance().getCpuLoad();
        numericFreeRam = SingletonNumericGeneralStats.getInstance().getFreeRam();
        numericPercPerThread = SingletonNumericGeneralStats.getInstance().getPercPerThread();

        return new Greeting(counter.incrementAndGet(), template, batteryParts, cpuInfo[0], disks, computerInfo, miscellaneous, numericAvaibleFileSystem, numericCpuLoad, numericFreeRam, numericPercPerThread/* SingletonNumericGeneralStats.getInstance().getRamPerProcess()*/);
    }

    public static void getAllData() {
        final String[][] cpuInfo = {null};
        String[] disks = null;
        String[] computerInfo = null;
        String[] miscellaneous = null;
        String numericCpuLoad = null;
        String[] numericAvaibleFileSystem = null;
        String numericFreeRam = null;
        String numericPercPerThread = null;
        final String[] networkSpeed = {null};

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
        }).start();

        new Thread(() -> {
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
            latch.countDown();
        }).start();

        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        String template = batteryStatus.toString();
        String batteryParts[] = template.split("\n");
        SingletonBatteryStatus.getInstance().setBattery(batteryParts);

        disks = GeneralStats.getFileSystem().split("\n");

        SingletonBatteryStatus.getInstance().setDisks(disks);

        computerInfo = GeneralStats.getComputerSystemInfo().split("\n");

        SingletonBatteryStatus.getInstance().setComputerInfo(computerInfo);

        StringBuilder sb = new StringBuilder();

        String ramMemory = GeneralStats.getRamMemory() + "\n";

        try {
            //System.out.println(latch.getCount());
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //cpuInfo[0] = GeneralStats.getPcInfo();
        SingletonNumericGeneralStats.getInstance().setCpuLoad(cpuInfo[0][5]);
        sb.append(cpuInfo[0][5] + "\n");
        sb.append(ramMemory);
        sb.append(batteryParts[1] + "\n");

        sb.append(networkSpeed[0]);

        miscellaneous = sb.toString().split("\n");
        ;
        SingletonBatteryStatus.getInstance().setMiscellaneous(miscellaneous);

        SingletonBatteryStatus.getInstance().setCpu(cpuInfo[0]);

        SingletonBatteryStatus.getInstance().setAvaibleFileSystem(SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem());

        // numericAvaibleFileSystem = ;
//        SingletonBatteryStatus.getInstance().setNumericCpuLoad(SingletonNumericGeneralStats.getInstance().getCpuLoad());
       // SingletonBatteryStatus.getInstance().set = SingletonNumericGeneralStats.getInstance().getFreeRam();
        SingletonBatteryStatus.getInstance().setPercPerThread(SingletonNumericGeneralStats.getInstance().getPercPerThread());

        SingletonBatteryStatus.getInstance().notifyMyObservers();
    }
}
