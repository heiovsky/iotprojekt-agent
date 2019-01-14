package phwb.agent;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.metadata.collections.FieldDefinitionCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.AspectCollection;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.constants.Aspects;
import com.thingworx.types.constants.CommonPropertyNames;
import com.thingworx.types.constants.DataChangeType;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;
import phwb.util.ReadSensors;
import phwb.util.SensorProperty;

// Refer to the "Steam Sensor Example" section of the documentation
// for a detailed explanation of this example's operation

// Property Definitions
@SuppressWarnings("serial")

// Steam Thing virtual thing class that simulates a Steam Sensor
public class PcThing extends VirtualThing implements Runnable {

    private Thread _shutdownThread = null;


    public PcThing(String name, String description, String identifier,
                   ConnectedThingClient client) throws Exception {
        super(name, description, identifier, client);

        // Data Shape definition that is used by the steam sensor fault event
        // The event only has one field, the message
        super.initializeFromAnnotations();
        this.init();
    }

    // From the VirtualThing class
    // This method will get called when a connect or reconnect happens
    // Need to send the values when this happens
    // This is more important for a solution that does not send its properties on a regular basis
    public void synchronizeState() {
        // Be sure to call the base class
        super.synchronizeState();
        // Send the property values to Thingworx when a synchronization is required
        super.syncProperties();
    }

    private void init() {
        AspectCollection aspects = new AspectCollection();
        aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive(DataChangeType.NEVER.name()));
        aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
        aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(0));
        aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(false));
        aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
        aspects.put("pushType", new StringPrimitive(DataChangeType.VALUE.name()));
        aspects.put(Aspects.ASPECT_DEFAULTVALUE, new IntegerPrimitive(0));
        for (SensorProperty s : ReadSensors.getSensorProperties()) {
            PropertyDefinition property1 = new PropertyDefinition(s.name, "", BaseTypes.NUMBER);
            property1.setAspects(aspects);
            this.defineProperty(property1);
        }
        FieldDefinitionCollection fields = new FieldDefinitionCollection();
        fields.addFieldDefinition(new FieldDefinition("name", BaseTypes.STRING));
        defineDataShapeDefinition("PcSensorsNames", fields);
    }

    @ThingworxServiceDefinition(name = "GetPcSensorsNames",
            description = "Get Pc Sensors Names")
    @ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result",
            baseType = "INFOTABLE", aspects = {"dataShape:PcSensorsNames"})
    public InfoTable GetPcSensorsNames() {
        InfoTable table = new InfoTable(getDataShapeDefinition("PcSensorsNames"));
        ValueCollection entry = new ValueCollection();
        try {
            for (SensorProperty s : ReadSensors.getSensorProperties()) {
                entry.clear();
                entry.SetStringValue("name", s.name);
                table.addRow(entry.clone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }


    // The processScanRequest is called by the SteamSensorClient every scan cycle
    @Override
    public void processScanRequest() throws Exception {
        // Be sure to call the base classes scan request
        super.processScanRequest();
        // Execute the code for this simulation every scan
        this.scanDevice();
    }

    // Performs the logic for the steam sensor, occurs every scan cycle
    public void scanDevice() throws Exception {
        for (SensorProperty s : ReadSensors.getSensorProperties()) {
            super.setProperty(s.name, s.value);
        }
        super.updateSubscribedProperties(15000);
        super.updateSubscribedEvents(60000);
    }


    @ThingworxServiceDefinition(name = "Shutdown", description = "Shutdown the client")
    @ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "",
            baseType = "NOTHING")
    public synchronized void Shutdown() throws Exception {
        if (this._shutdownThread == null) {
            this._shutdownThread = new Thread(this);
            this._shutdownThread.start();
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            this.getClient().shutdown();
        } catch (Exception x) {
        }
    }
}
