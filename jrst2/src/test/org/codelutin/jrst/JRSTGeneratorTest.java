/* *##%
 * Copyright (C) 2006
 *     Code Lutin, Cédric Pineau, Benjamin Poussin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *##%*/

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
        JRST.main(new String[]{"src/test/org/codelutin/jrst/text.rst"});
        System.err.println("===================================================================");
        long time1 = System.currentTimeMillis();
        JRST.main(new String[]{"-t", "html", "src/test/org/codelutin/jrst/text.rst"});        
        System.err.println("===================================================================");
        long time2 = System.currentTimeMillis();
//        JRST.main(new String[]{"-t", "docbook", "src/test/org/codelutin/jrst/text.rst"});        
        System.err.println("===================================================================");
        long time3 = System.currentTimeMillis();
//        JRST.main(new String[]{"-t", "xhtml", "src/test/org/codelutin/jrst/text.rst"});        
        System.err.println("===================================================================");
        long time4 = System.currentTimeMillis();
        JRST.main(new String[]{"-t", "xdoc", "src/test/org/codelutin/jrst/text.rst"});        

        long time5 = System.currentTimeMillis();
        
        System.out.println("time generation: " + (time1-time0) + ":" +(time2-time1) + ":"+ (time3-time2) + ":"+ (time4-time3) + ":"+ (time5-time4));
    }

}


