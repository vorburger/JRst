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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*/

package org.nuiton.jrst;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.apache.maven.doxia.module.xdoc.XdocParser;
import org.apache.maven.doxia.parser.ParseException;
import org.apache.maven.doxia.sink.Sink;
import org.dom4j.Document;

/**
 * Parse an RST model, transform it into xdoc model and emit events into the
 * specified doxia Sink.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 0.9.0
 * @plexus.component role="org.apache.maven.doxia.parser.Parser"
 *                   role-hint="jrst"
 */
public class JrstParser extends XdocParser {

    protected final static String RST2XDOC = "/xsl/rst2xdoc.xsl";

    @Override
    public void parse(Reader source, Sink sink) throws ParseException {

        try {

            // Load source as RST Document
            JRSTReader jrst = new JRSTReader();
            Document doc = jrst.read(source);

            // Apply xsl on rst RST Document
            JRSTGenerator gen = new JRSTGenerator();
            URL stylesheet = JrstParser.class.getResource(RST2XDOC);
            if (stylesheet == null) {
                throw new FileNotFoundException("Can't find stylesheet: "
                        + RST2XDOC);
            }
            doc = gen.transform(doc, stylesheet);

            // Give xsl result to XDoc parser
            Reader reader = new StringReader(doc.asXML());
            super.parse(reader, sink);
        } catch (Exception e) {
            throw new ParseException("Can't parse rst file", e);
        }

    }
}
