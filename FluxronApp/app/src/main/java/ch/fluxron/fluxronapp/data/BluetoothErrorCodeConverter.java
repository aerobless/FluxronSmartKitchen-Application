package ch.fluxron.fluxronapp.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to convert CANopen error payloads to legible error messages.
 */
public class BluetoothErrorCodeConverter {
    Map<String, String> errorCodes;
    public static final String OBJECT_DOES_NOT_EXIST = "0x06020000";

    public BluetoothErrorCodeConverter() {
        errorCodes = new HashMap<>();
        errorCodes.put("0x00000000", "no abort, no SDO error (yet) - TERMINAL SPECIFIC !");
        errorCodes.put("0x05030000", "SDO toggle bit error (protocol violation)");
        errorCodes.put("0x05040000", "SDO communication timeout (*)");
        errorCodes.put("0x05040001", "unknown SDO command specified (protocol incompatibility)");
        errorCodes.put("0x05040002", "invalid SDO block size");
        errorCodes.put("0x05040003", "invalid sequence number");
        errorCodes.put("0x05040004", "CRC error (cyclic redundancy code, during block transfer)");
        errorCodes.put("0x05040005", "out of memory");
        errorCodes.put("0x06010000", "unsupported access");
        errorCodes.put("0x06010001", "tried to read a WRITE-ONLY object");
        errorCodes.put("0x06010002", "tried to write a READ-ONLY object");
        errorCodes.put("0x06020000", "object does not exist (in the CANopen object dictionary)");
        errorCodes.put("0x06040041", "object cannot be mapped (into a PDO)");
        errorCodes.put("0x06040042", "PDO length exceeded (when trying to map an object)");
        errorCodes.put("0x06040043", "general parameter incompatibililty");
        errorCodes.put("0x06040047", "general internal incompatibility");
        errorCodes.put("0x06060000", "access failed due to hardware error");
        errorCodes.put("0x06070010", "data type and length code do not match");
        errorCodes.put("0x06070012", "data type problem, length code is too high");
        errorCodes.put("0x06070013", "data type problem, length code is too low");
        errorCodes.put("0x06090011", "subindex does not exist");
        errorCodes.put("0x06090030", "value range exceeded");
        errorCodes.put("0x06090031", "value range exceeded, too high");
        errorCodes.put("0x06090032", "value range exceeded, too low");
        errorCodes.put("0x06090036", "maximum value is less than minimum value");
        errorCodes.put("0x08000000", "general error");
        errorCodes.put("0x08000020", "data could not be transferred or stored");
        errorCodes.put("0x08000021", "data could not be transferred due to \"local control\"");
        errorCodes.put("0x08000022", "data could not be transferred due to \"device state\"");
        errorCodes.put("0x08000023", "object dictionary does not exist");
    }

    /**
     * Returns a string containing a description of the requested error code.
     * @param errorCode
     * @return
     */
    public String getErrorDescription(String errorCode){
        String result = errorCodes.get(errorCode);
        if(result == null){
            result = "Unable to find description for error code "+errorCode;
        }
        return  result;
    }
}
