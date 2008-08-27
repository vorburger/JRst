/**
 * *##% JRst
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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*
 */
/**
 * Compare les 2 xml avec XMLUnit
 * 
 */
package org.codelutin.jrst;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLTestCase;

public class XMLCaseTest extends XMLTestCase {
    public XMLCaseTest(String name) {
        super(name);
    }

    public DetailedDiff testAllDifferences(String myControlXML, String myTestXML)
            throws Exception {
        DetailedDiff myDiff = new DetailedDiff(compareXML(myControlXML,
                myTestXML));
        return myDiff;

    }

}
