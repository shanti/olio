
/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.sun.com/cddl/cddl.html or
 * install_dir/legal/LICENSE
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at install_dir/legal/LICENSE.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: Web20Driver.java,v 1.1 2007/02/21 23:54:59 sp208304 Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.web20.driver;

import com.sun.faban.driver.*;
import com.sun.faban.driver.util.Random;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.logging.Logger;

@BenchmarkDefinition (
    name    = "Web 2.0 workload",
    version = "0.2"
)
@BenchmarkDriver (
    name           = "Web20Driver",
    threadPerScale    = 1
)
/* @MatrixMix (
    operations = {"MyOperation1", "MyOperation2"},
    mix = { @Row({  0, 70}), 
            @Row({ 60,  0}) },
    deviation = 2
)
 */
@FlatMix (
    operations = {"HomePage", "SubmitPage"},
    mix = { 50, 50 },
    deviation = 2    
)
@NegativeExponential (
    cycleType = CycleType.CYCLETIME,
    cycleMean = 5000,
    cycleDeviation = 2
)
public class Web20Driver {

    /** The driver context for this instance */
    private DriverContext ctx;
    private HttpTransport http;
    private String url1, url2;
    Logger logger;
    Random random;

    public Web20Driver() throws XPathExpressionException {
        ctx = DriverContext.getContext();
        http = new HttpTransport();
        logger = ctx.getLogger();
        random = ctx.getRandom();
        String host = ctx.getXPathValue("/webBenchmark/serverConfig/host");
        String port = ctx.getXPathValue("/webBenchmark/serverConfig/port");
        String path1 = ctx.getProperty("path1");
        String path2 = ctx.getProperty("path2");
        url1 = "http://" + host + ':' + port + '/' + path1;
        url2 = "http://" + host + ':' + port + '/' + path2;
  
    }

    @OnceBefore public void testPreRun() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        logger.info("Tested pre-run (sleep 5) done");
    }

    @OnceAfter public void testPostRun() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        logger.info("Tested post-run (sleep 5) done");
    }

    @BenchmarkOperation (
        name    = "HomePage",
        max90th = 2,
        timing  = Timing.AUTO
    )
    public void doMyOperation1() throws IOException {
        logger.finest("Accessing " + url1);
        http.fetchURL(url1);
    }

    @BenchmarkOperation (
        name    = "SubmitPage",
        max90th = 2,
        timing  = Timing.AUTO
    )
    public void doMyOperation2() throws IOException {
        logger.finest("Accessing " + url2);
        http.fetchURL(url2);
    }
}
