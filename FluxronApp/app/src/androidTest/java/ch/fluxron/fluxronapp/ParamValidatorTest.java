package ch.fluxron.fluxronapp;

import junit.framework.TestCase;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.model.ParamValidator;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;

/**
 * Unit Tests for the ParamValidator class.
 */
public class ParamValidatorTest extends TestCase {

    public void testConvertToObjectBoolean(){
        ParamValidator validator = new ParamValidator();
        ParameterValue value = new ParameterValue(ParamManager.F_TEST_2001SUB8_BOOLEAN_TEST, "true");
        Object result = validator.convertToObject(value);
        assertTrue(result instanceof Boolean);
        boolean val = (Boolean)result;
        assertEquals(val, true);
        ParameterValue value2 = new ParameterValue(ParamManager.F_TEST_2001SUB8_BOOLEAN_TEST, "false");
        Object result2 = validator.convertToObject(value2);
        assertTrue(result2 instanceof Boolean);
        boolean val2 = (Boolean)result2;
        assertEquals(val2, false);
    }

    public void testConvertToObjectUnsignedIntNormal(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_CCLASS_3005SUB1_ERROR_1, "3000");
        Object result = validator.convertToObject(value);
        assertTrue(result instanceof Integer);
        int val = (Integer)result;
        assertEquals(val, 3000);
    }

    public void testConvertToObjectUnsignedIntBadValue(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_CCLASS_3005SUB1_ERROR_1, "-3000");
        Object result = validator.convertToObject(value);
        assertTrue(result == null);
    }

    public void testConvertToObjectUnsignedIntBadValue2(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_CCLASS_3005SUB1_ERROR_1, "NotANumber");
        Object result = validator.convertToObject(value);
        assertTrue(result == null);
    }

    public void testConvertToObjectSignedIntNormal(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_CCLASS_3021SUB1_ANALOGINPUT0, "3000");
        Object result = validator.convertToObject(value);
        assertTrue(result instanceof Integer);
        int val = (Integer)result;
        assertEquals(val, 3000);
    }

    public void testConvertToObjectSignedIntBadValue(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_CCLASS_3021SUB1_ANALOGINPUT0, "NotANumber");
        Object result = validator.convertToObject(value);
        assertTrue(result == null);
    }

    public void testConvertToObjectFloatNormal(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_TEST_3021SUB1_FLOAT_TEST, "13.37");
        Object result = validator.convertToObject(value);
        assertTrue(result instanceof Float);
        Float val = (Float)result;
        assertEquals(val, 13.37f);
    }

    public void testConvertToObjectFloatBadValue(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_TEST_3021SUB1_FLOAT_TEST, "NotANumber");
        Object result = validator.convertToObject(value);
        assertTrue(result == null);
    }

    public void testConvertToObjectString(){
        ParamValidator validator = new ParamValidator();

        ParameterValue value = new ParameterValue(ParamManager.F_CCLASS_1008_MANUFACTURER_DEVICE_NAME, "HAL9000");
        Object result = validator.convertToObject(value);
        assertEquals((String)result, "HAL9000");
    }

    public void testIsValid(){
        ParamValidator validator = new ParamValidator();
        ParameterValue value = new ParameterValue(ParamManager.F_CCLASS_2000SUB4_COIL_SETUP_, "HELLO WORLD");
        ParameterValue value2 = new ParameterValue(ParamManager.F_CCLASS_2000SUB4_COIL_SETUP_, "42");
        assertTrue(!validator.isValid(value));
        assertTrue(validator.isValid(value2));
    }
}
