package pcstatus.springServer;

import java.util.concurrent.atomic.AtomicLong;

import pcstatus.SingletonBatteryStatus;

import org.hyperic.sigar.SigarException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

        try {
            cpuInfo = GeneralStats.getPcInfo();
            networkSpeed = NetworkSpeed.getSpeed();
            disks = GeneralStats.getDiskStats();
        } catch (SigarException | InterruptedException e) {
            e.printStackTrace();
        }

        return new Greeting(counter.incrementAndGet(), template, batteryParts, cpuInfo, networkSpeed, disks);
    }
}
