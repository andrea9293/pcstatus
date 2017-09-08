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
        String[] cpuInfo = null;
        String disks = null;
        String computerInfo = null;
        String miscellaneous = null;
        String numericCpuLoad = null;
        String[] numericAvaibleFileSystem = null;
        String numericFreeRam = null;
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

        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
            latch.countDown();
        }).start();

        disks = GeneralStats.getFileSystem();

        computerInfo = GeneralStats.getComputerSystemInfo();

        StringBuilder sb = new StringBuilder();
        String ramMemory = GeneralStats.getRamMemory() + "\n";

        try {
            //System.out.println(latch.getCount());
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cpuInfo = GeneralStats.getPcInfo();
        sb.append(cpuInfo[4] + "\n");
        sb.append(ramMemory);
        sb.append(batteryParts[1] + "\n");

        sb.append(networkSpeed[0]);

        miscellaneous = sb.toString();

        numericAvaibleFileSystem = SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem();
        numericCpuLoad = SingletonNumericGeneralStats.getInstance().getCpuLoad();
        numericFreeRam = SingletonNumericGeneralStats.getInstance().getFreeRam();

        cpuInfo = GeneralStats.getPcInfo();

        return new Greeting(counter.incrementAndGet(), template, batteryParts, cpuInfo, disks, computerInfo, miscellaneous, numericAvaibleFileSystem, numericCpuLoad, numericFreeRam/* SingletonNumericGeneralStats.getInstance().getRamPerProcess()*/);
    }

    public static void getAllData() {
        String[] cpuInfo = null;
        String[] disks = null;
        String[] computerInfo = null;
        String[] miscellaneous = null;
        String numericCpuLoad = null;
        String[] numericAvaibleFileSystem = null;
        String numericFreeRam = null;
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

        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
            latch.countDown();
        }).start();

        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        String template = batteryStatus.toString();
        String batteryParts[] = template.split("\n");
        SingletonBatteryStatus.getInstance().setBattery(batteryParts);

        disks = GeneralStats.getFileSystem().split("\n");;
        SingletonBatteryStatus.getInstance().setDisks(disks);

        computerInfo = GeneralStats.getComputerSystemInfo().split("\n");;
        SingletonBatteryStatus.getInstance().setComputerInfo(computerInfo);

        StringBuilder sb = new StringBuilder();

        String ramMemory = GeneralStats.getRamMemory() + "\n";

        try {
            //System.out.println(latch.getCount());
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cpuInfo = GeneralStats.getPcInfo();
        sb.append(cpuInfo[4] + "\n");
        sb.append(ramMemory);
        sb.append(batteryParts[1] + "\n");

        sb.append(networkSpeed[0]);

        miscellaneous = sb.toString().split("\n");;
        SingletonBatteryStatus.getInstance().setMiscellaneous(miscellaneous);

        SingletonBatteryStatus.getInstance().setCpu(cpuInfo);

        SingletonBatteryStatus.getInstance().notifyMyObservers();

        /*numericAvaibleFileSystem = SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem();
        numericCpuLoad = SingletonNumericGeneralStats.getInstance().getCpuLoad();
        numericFreeRam = SingletonNumericGeneralStats.getInstance().getFreeRam();*/
    }
}
