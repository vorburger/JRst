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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 * @author poussin
 *
 */

public class JRSTGeneratorTest extends TestCase {

    String dn2dbkx = "/xslt/dn2dbk.xsl";
    String dbkx2xhtml = "/usr/share/xml/docbook/stylesheet/nwalsh/xhtml/docbook.xsl";
    
    public void testRstToHtml() throws Exception {
        URL url = JRSTReaderTest.class.getResource("/org/codelutin/jrst/text.rst");
        Reader in = new InputStreamReader(url.openStream());
        
        JRSTReader jrst = new JRSTReader();
        Document doc = jrst.read(in);
        
        XMLWriter out = new XMLWriter(System.out, new OutputFormat("  ", true));
        out.write(doc);
        
        URL stylesheet = JRSTReaderTest.class.getResource(dn2dbkx);
        JRSTGenerator gen = new JRSTGenerator();
        Document dbkx = gen.transform(doc, stylesheet);
        
        stylesheet = new File(dbkx2xhtml).toURL();
        gen = new JRSTGenerator();
        Document xhtml = gen.transform(dbkx, stylesheet);
        
        HTMLWriter outSystem = new HTMLWriter(System.out, new OutputFormat("  ", true));
        outSystem.write(xhtml);
    }

}


