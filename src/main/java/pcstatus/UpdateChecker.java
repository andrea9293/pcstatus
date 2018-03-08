package pcstatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * this class manage updatesfrom github
 */
public class UpdateChecker {

    public static String urlToLatestVersion = "https://github.com/andrea9293/pcstatus/releases/latest";
    private static String actualVersion = "1.2beta";

    public static Boolean checkUpdate(){
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try {
            url = new URL(urlToLatestVersion);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            boolean b = false;
            while ((line = br.readLine()) != null) {
                if (line.contains("releases/download/" + actualVersion)) {
                    System.out.println(line.trim());
                    return true;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return false;
    }
}
