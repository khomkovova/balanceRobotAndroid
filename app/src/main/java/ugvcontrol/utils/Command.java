package ugvcontrol.utils;

public class Command {

    private byte command;

    public Command(int type, int value) {
        command |= ((byte) (type & 0xF)) << 4;
        command |= ((byte) (value & 0xF));
    }

    public byte getByte() {
        return command;
    }

    public String toBinaryString() {
        char[] bits = new char[8];
        byte j = 1;
        for (int i = 0; i < 8; i++) {
            if((command & j) != 0) {
                bits[7 - i] = '1';
            } else {
                bits[7 - i] = '0';
            }
            j <<= 1;
        }
        return new String(bits);
    }
}
