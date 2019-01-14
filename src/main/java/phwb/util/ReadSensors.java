package phwb.util;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.*;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import java.util.ArrayList;
import java.util.List;

public class ReadSensors {


    private static List<SensorProperty> list;

    public synchronized static List<SensorProperty> getSensorProperties() {
        list = new ArrayList<>();
        readGroupComponent();
        return list;
    }

    private static void readGroupComponent() {
        final Components components = JSensors.get.components();
        final List<Cpu> cpus = components.cpus;
        if (cpus != null) {
            for (final Cpu cpu : cpus) {
                readComponent(cpu, cpu.name.trim());
            }
        }
        final List<Gpu> gpus = components.gpus;
        if (gpus != null) {
            for (final Gpu gpu : gpus) {
                readComponent(gpu, gpu.name.trim());
            }
        }
        final List<Disk> disks = components.disks;
        if (disks != null) {
            for (final Disk disk : disks) {
                readComponent(disk, disk.name.trim());
            }
        }
    }

    private static void readComponent(final Component component, String groupName) {
        groupName = groupName.replace(" ", "_");
        if (component.sensors != null) {
            final List<Temperature> temps = component.sensors.temperatures;
            for (final Temperature temp : temps) {
                list.add(new SensorProperty(groupName + "___" + temp.name.trim().replace(" ","_"), temp.value.floatValue()));
            }
            final List<Fan> fans = component.sensors.fans;
            for (final Fan fan : fans) {
                list.add(new SensorProperty(groupName + "___" + fan.name.trim().replace(" ","_"), fan.value.floatValue()));
            }
            final List<Load> loads = component.sensors.loads;
            for (final Load load : loads) {
                list.add(new SensorProperty(groupName + "___" + load.name.trim().replace(" ","_"), load.value.floatValue()));
            }
        }
    }

}
