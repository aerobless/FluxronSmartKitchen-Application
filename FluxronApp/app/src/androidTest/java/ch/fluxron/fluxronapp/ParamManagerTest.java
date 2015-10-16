package ch.fluxron.fluxronapp;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;

import ch.fluxron.fluxronapp.data.DeviceParameter;
import ch.fluxron.fluxronapp.data.ParamManager;

/**
 * Unit-test for ParamManager class.
 */
public class ParamManagerTest extends AndroidTestCase {

    public void testSomething(){
        ParamManager paramManager = new ParamManager(getContext());
        //paramManager.loadOD();

        Map<String, DeviceParameter> paramList = paramManager.loadParameters();
        for(DeviceParameter p:paramList.values()){
            Log.d("Fluxron", p.toString());
        }
    }
}
