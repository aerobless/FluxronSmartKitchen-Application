package ch.fluxron.fluxronapp;

import android.util.Log;

import junit.framework.TestCase;

import ch.fluxron.fluxronapp.ui.util.ErrorCodeConverter;

/**
 * Unit Tests for the ErrorCodeConverter class.
 */
public class ErrorCodeConverterTest extends TestCase {

    public void testConvertToCodeIDBelow10(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(83886080);
        assertEquals(error_code1, "e05");
    }

    public void testConvertToCodeIDBelow10withTime(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(83906668);
        assertEquals(error_code1, "e05");
    }

    public void testConvertToCodeIDOver10(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(251678828);
        assertEquals(error_code1, "e15");
    }

    public void testConvertToCodeIDOverF(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(318787692);
        assertEquals(error_code1, "e19");
    }

    public void testConvertToCodeIDNull(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(00000000);
        assertEquals(error_code1, "e00");
    }

    public void testConvertToTimeIDShort(){
        String error_code1 = ErrorCodeConverter.convertToTime(83906668);
        assertEquals(error_code1, "2058.8");
    }

    public void testConvertToTimeIDLong(){
        String error_code1 = ErrorCodeConverter.convertToTime(318787692);
        assertEquals(error_code1, "2058.8");
    }

    public void testConvertToTimeIDNoTimeForTime(){
        String error_code1 = ErrorCodeConverter.convertToTime(83886080);
        assertEquals("00.0", error_code1);
    }

    public void testConvertToTimeIDNull(){
        String error_code1 = ErrorCodeConverter.convertToTime(00000000);
        assertEquals("00.0", error_code1);
    }
}
