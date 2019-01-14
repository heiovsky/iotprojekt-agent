
package phwb.agent;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.communications.common.SecurityClaims;
import phwb.AppConstants;


public class PcSensorClient extends ConnectedThingClient {

    private PcSensorClient(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void main(String[] args) throws Exception {

        // Set the required configuration information
        ClientConfigurator config = new ClientConfigurator();
        // Set the URI of the server that we are going to connect to
        if (args.length > 0) {
            config.setUri(args[0]);
        } else {
            config.setUri(AppConstants.SERVER_URI);
        }
        // Set the ApplicationKey. This will allow the client to authenticate with the server.
        // It will also dictate what the client is authorized to do once connected.
        config.setAppKey(AppConstants.APP_KEY);

        // Reconnect every 15 seconds if a disconnect occurs or if initial connection cannot be made
        config.setReconnectInterval(15);

        // Set the security using an Application Key
        String appKey = config.getAppKey();
        SecurityClaims claims = SecurityClaims.fromAppKey(appKey);
        config.setSecurityClaims(claims);

        // This will allow us to test against a server using a self-signed certificate.
        // This should be removed for production systems.
        config.ignoreSSLErrors(true); // All self signed certs


        // Create the client passing in the configuration from above
        PcSensorClient client = new PcSensorClient(config);

        PcThing pcThing = new PcThing("PC_" + AppConstants.HOSTNAME, AppConstants.IP_ADDRESS, AppConstants.HOSTNAME, client);
        client.bindThing(pcThing);

        try {
            // Start the client
            client.start();
        } catch (Exception eStart) {
            System.out.println("Initial Start Failed : " + eStart.getMessage());
        }

        // As long as the client has not been shutdown, continue
        while (!client.isShutdown()) {
            // Only process the Virtual Things if the client is connected
            if (client.isConnected()) {
                // Loop over all the Virtual Things and process them
                for (VirtualThing thing : client.getThings().values()) {
                    try {
                        thing.processScanRequest();
                    } catch (Exception eProcessing) {
                        System.out.println("Error Processing Scan Request for [" + thing.getName() + "] : " + eProcessing.getMessage());
                    }
                }
            }
            // Suspend processing at the scan rate interval
            Thread.sleep(AppConstants.SCAN_RATE_OF_SENSORS);
        }
    }
}
