/*
 * This is the source code of PC-status.
 * It is licensed under GNU AGPL v3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Andrea Bravaccino.
 */
package pcstatus.dataPackage;

import oshi.SystemInfo;
import oshi.software.os.OSFileStore;
import oshi.util.FormatUtil;

/**
 * Using Oshi lib, This class retrieve all information about memories (Filesystems and RAM) and set them in model
 *
 * @author Andrea Bravaccino
 */
public class MemoryStats {
    /**
     * Intstance of singleton
     */
    private static MemoryStats ourInstance = new MemoryStats();

    /**
     * variable to initialize <code>SystemInfo()</code> of Oshi lib.
     * This variable is useful for obtaining information about the operating system and computer hardware.
     * @see oshi.SystemInfo
     */
    private SystemInfo systemInfo = new SystemInfo();

    /**
     * getter for current instance of <code>MemoryStats</code>
     * @return current instance of <code>MemoryStats</code>
     */
    public static MemoryStats getInstance() {
        return ourInstance;
    }

    /**
     * empty constructor
     */
    private MemoryStats() {
    }

    /**
     * Getter for RAM informations. This function also invoke <code>setRamMemoryInModel(Float )</code> to set free Ram in model
     *
     * @see MemoryStats#setRamMemoryInModel(Float)
     * @return a String with informations about RAM with this format: Memory: (value for free RAM) free of (value for total RAM)
     */
    public String getRamMemory() {
        setRamMemoryInModel(
                GeneralStats.getInstance().round(
                        ((float) systemInfo.getHardware().getMemory().getAvailable() / (float) systemInfo.getHardware().getMemory().getTotal()) * 100,
                        2)
        );
        return "Memory: " + FormatUtil.formatBytes(systemInfo.getHardware().getMemory().getAvailable()) + " free of "
                + FormatUtil.formatBytes(systemInfo.getHardware().getMemory().getTotal());
    }

    /**
     * This method set free RAM value in model
     * @param ramMemoryInModel free RAM size
     */
    private void setRamMemoryInModel(Float ramMemoryInModel){
        SingletonDynamicGeneralStats.getInstance().setFreeRam(ramMemoryInModel);
    }

    /**
     * this function retrieve informations about filesystems and calculate total and free space for each of them.
     * After processing the data, send them to the model invoking <code>setAvailabeFileSystemInModel(String[])</code>
     *
     * @see MemoryStats#setAvailabeFileSystemInModel(String[])
     * @return a string containing informations about all filesystems founded with total and free size, label, and type of device
     */
    public String getFileSystems() {
        OSFileStore[] fsArray = systemInfo.getOperatingSystem().getFileSystem().getFileStores();
        String[] numericSpace = new String[fsArray.length];
        String[] volume = new String[fsArray.length];
        String[] stringBuilder = new String[fsArray.length];
        for (int i = 0; i < fsArray.length; i++) {
            OSFileStore fs = fsArray[i];
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            stringBuilder[i] = (String.format(" %s (%s) [%s] %s free of %s (%.1f%%) " +
                            (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s") +
                            "%n", fs.getName(),
                    fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total, fs.getLogicalVolume()));
            try {
                if (total == 0) {
                    volume[i] = fs.getMount();
                    numericSpace[i] = "0";
                } else {
                    volume[i] = fs.getMount();
                    numericSpace[i] = String.format("%.1f", (100d * usable / total)).replace(",", ".");
                }
            } catch (NumberFormatException e) {
                numericSpace[i] = "error";
            }
        }

        for (int i = 0; i < numericSpace.length; i++) {
            if (volume[i].equals("C:\\")) {
                String genericString = numericSpace[0];
                numericSpace[0] = numericSpace[i];
                numericSpace[i] = genericString;

                genericString = stringBuilder[0];
                stringBuilder[0] = stringBuilder[i];
                stringBuilder[i] = genericString;
            }
        }
        setAvailabeFileSystemInModel(numericSpace);
        return String.join("", stringBuilder);
    }

    /**
     * This method set available filesystems value in model
     * @param availabeFileSystemInModel value to set in model
     */
    private void setAvailabeFileSystemInModel(String[] availabeFileSystemInModel){
        Float[] floats = new Float[availabeFileSystemInModel.length];
        for (int i = 0; i < availabeFileSystemInModel.length; i++) {
            floats[i] = Float.valueOf(availabeFileSystemInModel[i]);
        }
        SingletonDynamicGeneralStats.getInstance().setAvaibleFileSystem(floats);
    }
}
