package ugvcontrol.arduino;

import junit.framework.TestCase;

import org.junit.Test;

import static org.mockito.Mockito.*;


public class ArduinoConnectorTest extends TestCase {

    @Test
    public void testRun() throws Exception {
        CommandWriter commandWriter = mock(DefaultCommandWriter.class);
        ArduinoConnector connector = spy(new ArduinoConnector(commandWriter));

        when(connector.isStoped()).thenReturn(false).thenReturn(true);

        connector.run();

        verify(commandWriter, times(1)).writeCommand();
        verify(connector, times(2)).isStoped();
        verify(connector, times(1)).sleep();
    }
}