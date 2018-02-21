package ugvcontrol.arduino;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.ArrayList;

import ugvcontrol.utils.Command;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;


public class DefaultCommandWriterTest extends TestCase {

    @Test
    public void testWriteCommand() throws Exception {
        UsbManager usbManager = mock(UsbManager.class);
        UsbDevice usbDevice = mock(UsbDevice.class);
        DefaultCommandWriter commandWriter = mock(DefaultCommandWriter.class, withSettings()
                .useConstructor(usbManager, usbDevice));

        ArrayList<Command> commands = new ArrayList<>();
        when(commandWriter.getCommands()).thenReturn(commands);

        UsbDeviceConnection connection = mock(UsbDeviceConnection.class);
        when(usbManager.openDevice(usbDevice)).thenReturn(connection);

        UsbSerialPort port = mock(UsbSerialPort.class);
        when(commandWriter.getPort(usbDevice)).thenReturn(port);

        doNothing().when(port).open(connection);
        doReturn(1).when(port).write(new byte[10], 100);
        doNothing().when(port).close();

        doNothing().when(connection).close();

        commandWriter.writeCommand();
    }
}