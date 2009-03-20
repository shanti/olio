package org.apache.olio.workload.loader;
public class EventImages extends Images {

    public void prepare() {
     imageId = getSequence();
     ++imageId;
     prefix = "e";
    }
}
