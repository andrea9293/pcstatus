package pcstatus.springServer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pcstatus.dataPackage.*;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;


/**
 * In Spring’s approach to building RESTful web services, HTTP requests are handled by a controller.
 * These components are easily identified by the <code>@RestController</code> annotation, and the
 * <code>GreetingController</code> below handles <code>GET</code>
 * requests for <code>/greeting</code> by returning a new instance of the <code>Greeting</code> class
 *
 * @see <a href="https://spring.io/guides/gs/rest-service/" target="_blank">Building a RESTful Web Service - Spring</a>
 * @author Andrea Bravaccino
 */
@RestController
public class GreetingController {
    /**
     * Intstance of singleton
     */
    private static GreetingController instance = new GreetingController();

    /**
     * empty constructor
     */
    private GreetingController(){}

    /**
     * getter for current instance of <code>GreetingController</code>
     * @return current instance of <code>GreetingController</code>
     */
    public static GreetingController getInstance() {
        return instance;
    }

    private final AtomicLong counter = new AtomicLong();
    /**
     * executor for thread retrieving all CPU information
     */
    public static ExecutorService getCpuInfo = Executors.newSingleThreadExecutor();

    /**
     * executor for thread retrieving all network information
     */
    public static ExecutorService getNetworkSpeed = Executors.newSingleThreadExecutor();

    /**
     * instance of SingletonDynamicGeneralStats
     */
    private SingletonDynamicGeneralStats sDgs = SingletonDynamicGeneralStats.getInstance();

    /**
     * instance of SingletonStaticGeneralStats
     */
    private SingletonStaticGeneralStats sSgs = SingletonStaticGeneralStats.getInstance();

    /**
     * recovers all data needed creating a new instance of <code>Greeting</code>
     * @return new instance of <code>Greeting</code>
     */
    @RequestMapping("/greeting")
    public Greeting greeting() {
        //String numericRamPerProcess = null;

        //ramPerProcessThread
      /*  new Thread(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("prelevo i ramperprocess...");
            sDgs.setRamPerProcess(GeneralStats.getRamPerProcess());
            System.out.println("finito prelevo i ramperprocess...\n");
            long stopTime = System.currentTimeMillis();
            System.out.println("\n\nfinito ramperprocess e ci ho messo " + (stopTime - startTime) + "\n" );
        }).start();*/


        String[] batteryParts = BatteryStats.getInstance().getBatteryStats();
        final CountDownLatch latch = new CountDownLatch(2);

        final String[] numericPercPerThread = new String[1];
        String[][] cpuInfo = {null};
        getCpuInfo.execute(new Thread(() -> {
            Thread.currentThread().setName("getCpuInfo");
            cpuInfo[0] = sSgs.getCpuInfo();
            cpuInfo[0][5] = CPUStats.getInstance().getCpuLoad();
            numericPercPerThread[0] = CPUStats.getInstance().getPercPerThreadStats();
            latch.countDown();
        },"getCpuInfo"));

        String[] networkSpeed = {null};
        getNetworkSpeed.execute(new Thread(() -> {
            Thread.currentThread().setName("getNetworkSpeed");
            networkSpeed[0] = "\n" + NetworkStats.getInstance().getNetworkSpeed();
            latch.countDown();
        },"getNetworkSpeed"));

        StringBuilder miscellaneousString = new StringBuilder();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        miscellaneousString.append(cpuInfo[0][5]).append("\n");
        miscellaneousString.append(MemoryStats.getInstance().getRamMemory()).append("\n");
        miscellaneousString.append(batteryParts[1]).append("\n");
        miscellaneousString.append(networkSpeed[0]);

        return new Greeting(counter.incrementAndGet(), Arrays.toString(batteryParts), batteryParts, cpuInfo[0], MemoryStats.getInstance().getFileSystems(), sSgs.getSystemInformation(), miscellaneousString.toString(), sDgs.getAvaibleFileSystem(), sDgs.getCpuLoad().toString(), sDgs.getFreeRam(), numericPercPerThread[0], sDgs.getBatteryPerc().toString()/* sDgs.getRamPerProcess()*/);
    }

    /**
     * recovers all data needed without creating a new instance of <code>Greeting</code>
     */
    //todo provare a spostare getAllData nel metodo greeting così da creare il json anche con il bluetooth senza connessione ad internet
    public void getAllData() {
        //String numericRamPerProcess = null;

        //ramPerProcessThread
      /*  new Thread(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("prelevo i ramperprocess...");
            sDgs.setRamPerProcess(GeneralStats.getRamPerProcess());
            System.out.println("finito prelevo i ramperprocess...\n");
            long stopTime = System.currentTimeMillis();
            System.out.println("\n\nfinito ramperprocess e ci ho messo " + (stopTime - startTime) + "\n" );
        }).start();*/

        final CountDownLatch latch = new CountDownLatch(2);

        String[][] cpuInfo = {null};
        new Thread(() -> {
            cpuInfo[0] = GeneralStats.getInstance().getCpuInfo();
            cpuInfo[0][5] = CPUStats.getInstance().getCpuLoad();
            CPUStats.getInstance().getPercPerThreadStats();
            latch.countDown();
        }, "getPCinfoAllData").start();

        String[] networkSpeed = {null};
        new Thread(() -> {
            networkSpeed[0] = "\n" + NetworkStats.getInstance().getNetworkSpeed();
            latch.countDown();
        }, "getNetworkSpeedAllData").start();

        String[] batteryParts = BatteryStats.getInstance().getBatteryStats();

        sDgs.setDisks(MemoryStats.getInstance().getFileSystems().split("\n"));

        sSgs.setComputerInfo(GeneralStats.getInstance().getComputerSystemInfo().split("\n"));

        String ramMemory = MemoryStats.getInstance().getRamMemory() + "\n";

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String sb = cpuInfo[0][5] + "\n" +
                ramMemory +
                batteryParts[1] + "\n" +
                networkSpeed[0];
        String[] miscellaneous = sb.split("\n");
        sSgs.setMiscellaneous(miscellaneous);

        sSgs.notifyMyObservers();
    }
}
