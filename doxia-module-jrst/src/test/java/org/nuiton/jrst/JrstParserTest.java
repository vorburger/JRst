/* *##% Doxia module jrst
 * Copyright (C) 2009 CodeLutin
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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##% */

package org.nuiton.jrst;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.maven.doxia.AbstractModuleTest;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author chatellier
 * @version $Revision : 1$
 */
public class JrstParserTest extends AbstractModuleTest {

    protected JrstParser parser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        parser = (JrstParser) lookup(Parser.ROLE, "jrst");
    }

    public void testParse() throws Exception {
        StringWriter output = null;
        Reader reader = null;

        try {
            output = new StringWriter();
            reader = getTestReader("test", "rst");

            Sink sink = new XdocSink(output){};
            parser.parse(reader, sink);

            assertTrue(output.toString().indexOf("emphasis") != -1);
            assertTrue(output.toString().indexOf("This is the first item") != -1);
            assertTrue(output.toString().indexOf("Title") != -1);
            assertTrue(output.toString().indexOf("blocks.") != -1);
        } finally {
            if (output != null) {
                output.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Override
    protected String outputExtension() {
        return "rst";
    }

    @Override
    protected String getOutputDir(){
        return "parser/";
    }

}
