package phwb;


import phwb.util.SystemEnv;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AppConstants {

    public final static Integer SCAN_RATE_OF_SENSORS = 1000;
    public final static String APP_KEY = "138e4f5d-3f1e-4f3c-959f-49b82e2d6556";
    public final static String SERVER_URI = "ws://localhost:8080/Thingworx/WS";
    public static String IP_ADDRESS;
    public static String HOSTNAME;

    static {
        try {
            IP_ADDRESS = SystemEnv.getIpAddressMain().trim();
            HOSTNAME = SystemEnv.getHostname().trim();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
