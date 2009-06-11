/* *##% JRst
 * Copyright (C) 2004 - 2009 CodeLutin
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

package org.nuiton.jrst;

import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * JRSTReaderTest.
 *
 * Created: 27 oct. 06 12:11:44
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
public class JRSTReaderTest extends TestCase {

    public void testRead() throws Exception {
        URL url = JRSTReaderTest.class
                .getResource("/test.rst");
        Reader in = new InputStreamReader(url.openStream());

        JRSTReader jrst = new JRSTReader();
        Document doc = jrst.read(in);

        {
            XMLWriter out = new XMLWriter(System.out, new OutputFormat("  ",
                    true));
            out.write(doc);
        }

        // {
        // StringWriter out = new StringWriter();
        // JRSTGenerator gen = new JRSTGenerator(out);
        // gen.generate(doc);
        // System.out.println(out.toString());
        // }
    }

}
