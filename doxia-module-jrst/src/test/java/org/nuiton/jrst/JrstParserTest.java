/*
 * #%L
 * JRst :: Doxia module
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2009 - 2010 CodeLutin
 * %%
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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.nuiton.jrst;

import org.apache.maven.doxia.AbstractModuleTest;
import org.apache.maven.doxia.module.xdoc.XdocSink;
import org.apache.maven.doxia.parser.Parser;
import org.apache.maven.doxia.sink.Sink;

import java.io.Reader;
import java.io.StringWriter;

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

            assertTrue(output.toString().contains("emphasis"));
            assertTrue(output.toString().contains("This is the first item"));
            assertTrue(output.toString().contains("Title"));
            assertTrue(output.toString().contains("blocks."));
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
