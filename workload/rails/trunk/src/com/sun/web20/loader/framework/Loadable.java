package com.sun.web20.loader.framework;

public abstract class Loadable {

    protected Loader loader = Loader.getInstance(getClass().getName());

    public abstract String getClearStatement();

    public abstract void prepare();

    public abstract void load();

    public void postLoad() {
        // Empty. We do not make it abstract.
        //  A majority of LoadObjects do not need this.
    }
}
