package org.little.util;

import java.io.IOException;

public class LogHexDump {

    private static final Logger LOG = LoggerFactory.getLogger(LogHexDump.class);

    public static void toString(byte[] data)throws IOException, ArrayIndexOutOfBoundsException,IllegalArgumentException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
               byte value=data[i];
               buffer.append("(byte)0x");
               buffer.append(_hexcodes[value >> data[i] & 15]);
               buffer.append(_hexcodes[value  & 15]);
               if(i < (data.length-1))buffer.append(',');
        }
        String str=buffer.toString();
        LOG.info(str);
        buffer.setLength(0);
           
    }
    public static void dump(byte[] data)throws IOException, ArrayIndexOutOfBoundsException,IllegalArgumentException {
        long offset=0; 
        int index=0;
        //if (index < 0 || index >= data.length) {
        //    throw new ArrayIndexOutOfBoundsException( "illegal index: " + index + " into array of length "+ data.length);
        //}
        long display_offset = offset + index;
        StringBuilder buffer = new StringBuilder(74);

        for (int j = index; j < data.length; j += 16) {
            int chars_read = data.length - j;

            if (chars_read > 16) {
                chars_read = 16;
            }
            dump(buffer, display_offset).append(' ');
            for (int k = 0; k < 16; k++) {
                if (k < chars_read) {
                    dump(buffer, data[k + j]);
                } else {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            for (int k = 0; k < chars_read; k++) {
                if (data[k + j] >= ' ' && data[k + j] < 127) {
                    buffer.append((char) data[k + j]);
                } else {
                    buffer.append('.');
                }
            }
            //buffer.append(EOL);

            LOG.info(buffer.toString());

            buffer.setLength(0);
            display_offset += chars_read;
        }
    }

    public static final String EOL = System.getProperty("line.separator");
    private static final char[] _hexcodes =
            {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'
            };
    private static final int[] _shifts =
            {
                28, 24, 20, 16, 12, 8, 4, 0
            };

    private static StringBuilder dump(StringBuilder _lbuffer, long value) {
        for (int j = 0; j < 8; j++) {
            _lbuffer.append(_hexcodes[(int) (value >> _shifts[j]) & 15]);
        }
        return _lbuffer;
    }

    private static StringBuilder dump(StringBuilder _cbuffer, byte value) {
        for (int j = 0; j < 2; j++) {
            _cbuffer.append(_hexcodes[value >> _shifts[j + 6] & 15]);
        }
        return _cbuffer;
    }

}
