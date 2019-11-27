package il.co.quana;


import java.util.Arrays;

public class TmpTmp {

    public static int MsgID = 0;
    public static byte[] notifyConnectedDevices(String value) {
        byte[] values = value.getBytes();

        byte[] header = new byte[] {0x51, 0x75, 0x00, 0x01, 0x03, 0x08};
        //Message ID
        header[2] = (byte)(MsgID & 0xFF);
        header[3] = (byte)(MsgID >> 8);
        MsgID++;
        //Opcode
        if (values[0] >= '0' && values[0] <= '9')
        {
            header[4] = (byte)(values[0] - '0');
        }
        else if (values[0] >= 'A' && values[0] <= 'Z')
        {
            header[4] = (byte)((values[0] - 'A') + 0x0A);
        }
        else if (values[0] >= 'a' && values[0] <= 'z')
        {
            header[4] = (byte)((values[0] - 'a') + 0x0A);
        }
        //Data length - opcode
        header[5] = (byte)(values.length - 1);
        if ((values.length > 1) && ((values[0] == '1') || (values[0] == '2')))
        {
            values[1] = (byte)(values[1] - '0');
            if ((values[0] == '1') && (values.length > 2))
            {
                values[2] = (byte)(values[2] - '0');
            }
        }
        else
        {
            if ((values.length > 1) && ((values[0] == '6') || (values[0] == '9')))
            {
                short SampleID = 0;
                for (int i = 1; i < values.length; i++)
                {

                    values[i] = (byte)(values[i] - '0');
                    SampleID = (short)((10*(SampleID)) + values[i]);
                }
                values[1] = (byte)SampleID;
                values = Arrays.copyOf(values, 2);
            }
        }
        byte[]AppendedArray = new byte[values.length + header.length + 1];
        System.arraycopy(header, 0, AppendedArray, 0, header.length);
        System.arraycopy(values, 1, AppendedArray, header.length, values.length - 1);
        AppendedArray[AppendedArray.length-1] = (byte)0xFF;
        return AppendedArray;
    }
}
