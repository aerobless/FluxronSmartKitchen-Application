package ch.fluxron.fluxronapp;

import junit.framework.TestCase;

import ch.fluxron.fluxronapp.ui.util.ErrorCodeConverter;

/**
 * Unit Tests for the ErrorCodeConverter class.
 */
public class ErrorCodeConverterTest extends TestCase {

    public void testConvertToCodeIDBelow10(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(83886080);
        assertEquals(error_code1, "error_description_e05");
    }

    public void testConvertToCodeIDBelow10withTime(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(83906668);
        assertEquals(error_code1, "error_description_e05");
    }

    public void testConvertToCodeIDOver10(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(251678828);
        assertEquals(error_code1, "error_description_e15");
    }

    public void testConvertToCodeIDOverF(){
        String error_code1 = ErrorCodeConverter.convertToErrorCode(318787692);
        assertEquals(error_code1, "error_description_e19");
    }

    public void testConvertToTimeIDShort(){
        String error_code1 = ErrorCodeConverter.convertToTime(83906668);
        assertEquals(error_code1, "2058.8");
    }

    public void testConvertToTimeIDLong(){
        String error_code1 = ErrorCodeConverter.convertToTime(318787692);
        assertEquals(error_code1, "2058.8");
    }
}
