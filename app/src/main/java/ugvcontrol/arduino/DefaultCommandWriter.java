package ugvcontrol.arduino;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

import ugvcontrol.utils.Command;
import ugvcontrol.utils.Commands;

public class DefaultCommandWriter implements CommandWriter {
    private UsbManager usbManager;
    private UsbDevice device;

    public DefaultCommandWriter(UsbManager usbManager, UsbDevice device) {
        this.usbManager = usbManager;
        this.device = device;
    }

    protected List<Command> getCommands() {
        return  Commands.getInstance().getActiveCommands();
    }

    protected UsbSerialPort getPort(UsbDevice device) {
        return UsbSerialProber
                .getDefaultProber()
                .probeDevice(device)
                .getPorts()
                .get(0);
    }

    @Override
    public void writeCommand() {
        List<Command> activeCommands = getCommands();

        byte[] data = new byte[activeCommands.size()];
        for (int i = 0; i < activeCommands.size(); i++) {
            data[i] = activeCommands.get(i).getByte();
        }
        try {
            UsbDeviceConnection connection = usbManager.openDevice(device);
            UsbSerialPort port = getPort(device);
            port.open(connection);
            port.write(data, 100);
            port.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
