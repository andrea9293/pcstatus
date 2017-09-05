package pcstatus.springServer;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

import org.hyperic.sigar.SigarException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pcstatus.ErrorManager;
import pcstatus.dataPackage.GeneralStats;
import pcstatus.dataPackage.Kernel32;
import pcstatus.dataPackage.SingletonNumericGeneralStats;


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

        try {
            cpuInfo = GeneralStats.getPcInfo();
            disks = GeneralStats.getFileSystem();
            computerInfo = GeneralStats.getComputerSystemInfo();

            StringBuilder sb = new StringBuilder();
            sb.append(cpuInfo[5] + "\n");
            sb.append(GeneralStats.getRamMemory() + "\n");
            sb.append(batteryParts[1] + "\n");
            try {
                sb.append("\n" + GeneralStats.getNetworkSpeed());
            } catch (ArrayIndexOutOfBoundsException | SocketException | UnknownHostException e) {
                System.out.println("there are some problem with detecting network speed");
                //ErrorManager.exeptionDialog(e);
                e.printStackTrace();
            }
            miscellaneous = sb.toString();
            numericAvaibleFileSystem = SingletonNumericGeneralStats.getInstance().getAvaibleFileSystem();
            numericCpuLoad = SingletonNumericGeneralStats.getInstance().getCpuLoad();
            numericFreeRam = SingletonNumericGeneralStats.getInstance().getFreeRam();
        } catch (SigarException | InterruptedException e) {
            ErrorManager.exeptionDialog(e);
            e.printStackTrace();
        }

        return new Greeting(counter.incrementAndGet(), template, batteryParts, cpuInfo, disks, computerInfo, miscellaneous, numericAvaibleFileSystem, numericCpuLoad, numericFreeRam);
    }
}
