package pcstatus.springServer;

import java.util.concurrent.atomic.AtomicLong;

import pcstatus.SingletonBatteryStatus;

import org.hyperic.sigar.SigarException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pcstatus.dataPackage.Allstats;
import pcstatus.dataPackage.GeneralStats;
import pcstatus.dataPackage.Kernel32;
import pcstatus.dataPackage.NetworkSpeed;


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
        String[] networkSpeed = null;
        String disks = null;
        String computerInfo = null;
        String miscellaneous = null;

        try {
            cpuInfo = GeneralStats.getPcInfo();
            //disks = GeneralStats.getDiskStats();
            disks = Allstats.getFileSystem();
            computerInfo = Allstats.getComputerSystemInfo();
            StringBuilder sb = new StringBuilder();
            sb.append(cpuInfo[5] + "\n");
            sb.append(Allstats.getRamMemory() + "\n");
            sb.append(batteryParts[1] + "\n");
            miscellaneous = sb.toString();
        } catch (SigarException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            networkSpeed = NetworkSpeed.getSpeed();
        } catch (SigarException | InterruptedException e) {
            System.out.println("rete disconnessa o non supportata");
            //e.printStackTrace();
        }

        return new Greeting(counter.incrementAndGet(), template, batteryParts, cpuInfo, networkSpeed, disks, computerInfo, miscellaneous);
    }
}
