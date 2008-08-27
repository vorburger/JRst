/* *##% JRst
 * Copyright (C) 2004 - 2008 CodeLutin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*/

/* *
 * JRSTGenerator.java
 *
 * Created: 31 oct. 06 11:14:19
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import junit.framework.TestCase;

/**
 * @author poussin
 * 
 */

public class JRSTGeneratorTest extends TestCase {

    public void testRstToHtml() throws Exception {
        long time0 = System.currentTimeMillis();
        
        // test 1 :
        JRST.main(new String[] { "-t", "rst", "-o", "html", "src/test/resources/test.rst", });
        System.err
                .println("===================================================================");
        long time1 = System.currentTimeMillis();
        
        // test 2 :
        JRST.main(new String[] { "-t", "html",
                "src/test/resources/text.rst" });
        System.err
                .println("===================================================================");
        long time2 = System.currentTimeMillis();
        
        // test 3 :
        // JRST.main(new String[]{"-t", "docbook",
        // "src/test/resources/text.rst"});
        System.err
                .println("===================================================================");
        long time3 = System.currentTimeMillis();
        
        // test 4 :
        // JRST.main(new String[]{"-t", "xhtml",
        // "src/test/resources/text.rst"});
        System.err
                .println("===================================================================");
        long time4 = System.currentTimeMillis();
        
        // test 5 :
        JRST.main(new String[] { "-t", "xdoc",
                "src/test/resources/text.rst" });

        long time5 = System.currentTimeMillis();

        System.out.println("time generation: " + (time1 - time0) + ":"
                + (time2 - time1) + ":" + (time3 - time2) + ":"
                + (time4 - time3) + ":" + (time5 - time4));
    }

}
