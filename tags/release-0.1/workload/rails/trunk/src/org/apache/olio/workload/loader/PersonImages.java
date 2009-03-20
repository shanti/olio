package org.apache.olio.workload.loader;
import org.apache.olio.workload.util.ScaleFactors;
import org.apache.olio.workload.loader.framework.*;

public class PersonImages extends Images {

    public void prepare() {
     int i = getSequence();
     ++i;
     imageId = ScaleFactors.events + i;
     prefix = "p";
    }
}
