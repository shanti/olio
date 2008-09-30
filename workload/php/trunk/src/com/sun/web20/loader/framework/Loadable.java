/* Copyright © 2008 Sun Microsystems, Inc. All rights reserved
 *
 * Use is subject to license terms.
 *
 * $Id: Loadable.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
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
