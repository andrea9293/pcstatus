package pcstatus.dataPackage;

import org.gridkit.lab.sigar.SigarFactory;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;


/**
 * This Singleton class deals with the calculation of cpu load statistics and its threads
 * Also sets percentage for each thread and cpu load in the model
 *
 * @author Andrea Bravaccino
 */
public class CPUStats {
    /**
     * Intstance of singleton
     */
    private static CPUStats ourInstance = new CPUStats();

    /**
     * empty constructor
     */
    private CPUStats(){}

    /**
     * function to return the instance
     * @return current instance of <code>CPUStats</code>
     */
    public static CPUStats getInstance(){
        return ourInstance;
    }

    /**
     * StringBuilder to collect percentages for each thread
     */
    private StringBuilder genericStringBuilder = new StringBuilder();
    /**
     * contain all informations about CPU
     * @see oshi.hardware.CentralProcessor
     */
    private CentralProcessor processor = new SystemInfo().getHardware().getProcessor();


    /**
     * this function return a string with information about cpu load in percentage.
     * <p>
     * before return, this method call <code>setCpuLoadInModel(String cpuLoad)</code>
     *
     * @see CPUStats#setCpuLoadInModel(Float)
     * @return returns the percentage of cpu load in string format
     */
    public String getCpuLoad() {
        setCpuLoadInModel(GeneralStats.getInstance().round((float) (processor.getSystemCpuLoad() * 100), 2));
        return GeneralStats.getInstance().round((float) (processor.getSystemCpuLoad() * 100), 2).toString();
    }

    /**
     * this function calculates for each thread its load percentage, then return, in a single string, load percentage for each thread of CPU.
     * <p>
     * before return, this method call <code>setPercPerThreadInModel(String cpuLoad)</code> to set values in model
     * @see CPUStats#setPercPerThreadInModel(String)
     * @return this functions returns, in a single string, load percentage for each thread of CPU separated by a "\n".
     * In case of problem return the string "0"
     */
    public String getPercPerThreadStats(){
        try {
            CpuPerc[] cpuperclist = SigarFactory.newSigar().getCpuPercList();
            genericStringBuilder.setLength(0);
            for (CpuPerc aCpuperclist : cpuperclist) {
                genericStringBuilder.append(GeneralStats.getInstance().round((float) (aCpuperclist.getCombined() * 100), 2)).append("\n");
            }
            setPercPerThreadInModel(genericStringBuilder.toString());
            return genericStringBuilder.toString();
        } catch (SigarException e) {
            setPercPerThreadInModel("0");
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * this function call a setter from model (SingletonDynamicGeneralStats) that sets
     * the percentage of cpu load
     * @see SingletonDynamicGeneralStats#setCpuLoad(Float)
     * @param cpuLoad is percentage of cpu load
     */
    private void setCpuLoadInModel(Float cpuLoad){
        SingletonDynamicGeneralStats.getInstance().setCpuLoad(cpuLoad);
    }

    /**
     * this function call a setter from model (SingletonDynamicGeneralStats) that sets
     * the percentage of cpu load
     * @param percPerThread an array of Float containing load percentage for each thread of CPU.
     */
    private void setPercPerThreadInModel(String percPerThread){
        String[] tmpStr = percPerThread.split("\n");
        Float[] tmpFlo = new Float[tmpStr.length];
        for (int i = 0; i < tmpStr.length; i++) {
            tmpFlo[i] = Float.valueOf(tmpStr[i]);
        }
        SingletonDynamicGeneralStats.getInstance().setPercPerThread(tmpFlo);
    }
}
