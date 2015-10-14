package ch.fluxron.fluxronapp;
import android.test.AndroidTestCase;

import ch.fluxron.fluxronapp.data.ParamImporter;

/**
 * Unit-test for ParamImporter class.
 */
public class ParamImporterTest extends AndroidTestCase {

    public void testSomething(){
        ParamImporter paramImporter = new ParamImporter(getContext());
        //paramImporter.loadOD();

        paramImporter.loadParameters();
    }
}
