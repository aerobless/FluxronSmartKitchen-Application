package ch.fluxron.fluxronapp;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import ch.fluxron.fluxronapp.data.DeviceParameter;
import ch.fluxron.fluxronapp.data.ParamManager;

/**
 * Unit-test for ParamManager class.
 */
public class ParamManagerTest extends AndroidTestCase {

    public void testSomething(){
        ParamManager paramManager = new ParamManager(getContext());
        //paramManager.loadOD();

        List<DeviceParameter> paramList = paramManager.loadParameters();
        for(DeviceParameter p:paramList){
            Log.d("Fluxron", p.toString());
        }
    }
}
