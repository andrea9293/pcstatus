package pcstatus.springServer;

import org.hyperic.sigar.SigarException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pcstatus.ErrorManager;
import pcstatus.dataPackage.GeneralStats;
import pcstatus.dataPackage.Kernel32;
import pcstatus.dataPackage.SingletonNumericGeneralStats;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
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

        long startAllTime = System.currentTimeMillis();

        new Thread(() -> {
            networkSpeed[0] = "\n" + GeneralStats.getNetworkSpeed();
        }).start();

        cpuInfo = GeneralStats.getPcInfo();

        disks = GeneralStats.getFileSystem();

        computerInfo = GeneralStats.getComputerSystemInfo();

        StringBuilder sb = new StringBuilder();
        sb.append(cpuInfo[5] + "\n");
        sb.append(GeneralStats.getRamMemory() + "\n");
        sb.append(batteryParts[1] + "\n");

           /* try {
                System.out.println("calcolo velocità di rete...");
                startTime = System.currentTimeMillis();
                sb.append("\n" + GeneralStats.getNetworkSpeed());
                stopTime = System.currentTimeMillis();
                System.out.println("time to execute code " + (stopTime - startTime) + "");
            } catch (ArrayIndexOutOfBoundsException | SocketException | UnknownHostException e) {
                System.out.println("there are some problem with detecting network speed");
                //ErrorManager.exeptionDialog(e);
                e.printStackTrace();
            }*/

        while (networkSpeed[0] == null) {
            System.out.println("aspetto");
            //do nothing, waiting for network speed
        }

        sb.append(networkSpeed[0]);

        miscellaneous = sb.toString();

        numericAvaibleFileSystem = SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem();
        numericCpuLoad = SingletonNumericGeneralStats.getInstance().getCpuLoad();
        numericFreeRam = SingletonNumericGeneralStats.getInstance().getFreeRam();

        long stopAllTime = System.currentTimeMillis();
        System.out.println("\n\n                                                 fatto tutto e ci ho messo " + (stopAllTime - startAllTime) + "\n");
        return new Greeting(counter.incrementAndGet(), template, batteryParts, cpuInfo, disks, computerInfo, miscellaneous, numericAvaibleFileSystem, numericCpuLoad, numericFreeRam/* SingletonNumericGeneralStats.getInstance().getRamPerProcess()*/);
    }
}
