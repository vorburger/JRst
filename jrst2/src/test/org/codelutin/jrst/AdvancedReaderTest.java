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
 * AdvancedReaderTest.java
 *
 * Created: 27 oct. 06 01:03:26
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;

import junit.framework.TestCase;


/**
 * @author poussin
 *
 */

public class AdvancedReaderTest extends TestCase {

    String text = "\t\t\n" +
        "1 toto tata tutu\n" +
        "2 toto tata tutu\n" +
        "3 toto tata tutu\n" +
        "4 toto tata tutu\n" +
        "5 toto tata tutu\n" +
        "6 toto tata tutu\n" +
        "7 toto tata tutu\n" +
        "8 toto tata tutu\n" +
        "9 toto tata tutu\n";
            
    File file = null;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public AdvancedReaderTest() throws Exception {
        file = File.createTempFile("test-AdvancedReader", ".txt");
        file.deleteOnExit();
        
        Writer out = new BufferedWriter(new FileWriter(file));
        out.write(text);
        out.close();
    }

    /**
     * Test method for {@link org.codelutin.jrst.AdvancedReader#eof()}.
     */
    public void testEof() throws Exception {
        AdvancedReader in = new AdvancedReader(new FileReader(file));
        assertFalse(in.eof());
        in.readAll();
        assertTrue(in.eof());
        
        in = new AdvancedReader(new StringReader(text));
        assertFalse(in.eof());
        in.readAll();
        assertTrue(in.eof());        
    }

    /**
     * Test method for {@link org.codelutin.jrst.AdvancedReader#skip_blank_lines()}.
     */
    public void testSkipBlankLines() throws Exception {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            in.skipBlankLines();
            String line = in.readLine();
            assertEquals("1 toto tata tutu", line);
        }
        
        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            in.skipBlankLines();
            String line = in.readLine();
            assertEquals("1 toto tata tutu", line);
        }
    }

    /**
     * Test method for {@link org.codelutin.jrst.AdvancedReader#readAll()}.
     */
    public void testReadAll() throws Exception {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String [] lines = in.readAll();
            assertEquals(10, lines.length);
            assertEquals("\t\t", lines[0]);
            assertEquals("1 toto tata tutu", lines[1]);
            assertEquals("9 toto tata tutu", lines[9]);
        }
      
        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String [] lines = in.readAll();
            assertEquals(10, lines.length);
            assertEquals("\t\t", lines[0]);
            assertEquals("1 toto tata tutu", lines[1]);
            assertEquals("9 toto tata tutu", lines[9]);
        }      
    }

    /**
     * Test method for {@link org.codelutin.jrst.AdvancedReader#readLines(int)}.
     */
    public void testReadLines() throws Exception {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String [] lines = in.readLines(3);
            assertEquals(3, lines.length);
            assertEquals("\t\t", lines[0]);
            assertEquals("1 toto tata tutu", lines[1]);
            assertEquals("2 toto tata tutu", lines[2]);
        }
      
        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String [] lines = in.readLines(3);
            assertEquals(3, lines.length);
            assertEquals("\t\t", lines[0]);
            assertEquals("1 toto tata tutu", lines[1]);
            assertEquals("2 toto tata tutu", lines[2]);
        }      
    }

    /**
     * Test method for {@link org.codelutin.jrst.AdvancedReader#readUntil(java.lang.String)}.
     */
    public void testReadUntil() throws Exception {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String [] lines = in.readUntil("^3.*$");
            assertEquals(3, lines.length);
            assertEquals("\t\t", lines[0]);
            assertEquals("1 toto tata tutu", lines[1]);
            assertEquals("2 toto tata tutu", lines[2]);
            
            String line = in.readLine();
            assertEquals("3 toto tata tutu", line);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String [] lines = in.readUntil("^3.*$");
            assertEquals(3, lines.length);
            assertEquals("\t\t", lines[0]);
            assertEquals("1 toto tata tutu", lines[1]);
            assertEquals("2 toto tata tutu", lines[2]);

            String line = in.readLine();
            assertEquals("3 toto tata tutu", line);
        }
    }

    /**
     * Test method for {@link org.codelutin.jrst.AdvancedReader#readUntil(java.lang.String)}.
     */
    public void testReadWhile() throws Exception {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            in.skipBlankLines();
            String [] lines = in.readWhile("^[123].*$");
            assertEquals(3, lines.length);
            assertEquals("1 toto tata tutu", lines[0]);
            assertEquals("2 toto tata tutu", lines[1]);
            assertEquals("3 toto tata tutu", lines[2]);
            
            String line = in.readLine();
            assertEquals("4 toto tata tutu", line);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            in.skipBlankLines();
            String [] lines = in.readWhile("^[123].*$");
            assertEquals(3, lines.length);
            assertEquals("1 toto tata tutu", lines[0]);
            assertEquals("2 toto tata tutu", lines[1]);
            assertEquals("3 toto tata tutu", lines[2]);
            
            String line = in.readLine();
            assertEquals("4 toto tata tutu", line);
        }
    }

    /**
     * Test method for {@link org.codelutin.jrst.AdvancedReader#readLine()}.
     */
    public void testReadLine() throws Exception {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String line = in.readLine();
            assertEquals("\t\t", line);
            line = in.readLine();
            assertEquals("1 toto tata tutu", line);
            line = in.readLine();
            assertEquals("2 toto tata tutu", line);
        }
        
        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String line = in.readLine();
            assertEquals("\t\t", line);
            line = in.readLine();
            assertEquals("1 toto tata tutu", line);
            line = in.readLine();
            assertEquals("2 toto tata tutu", line);
        }        
    }

    public void testMark() throws Exception {
        AdvancedReader in = new AdvancedReader(new StringReader(text));
        String line = in.readLine();
        in.mark();
        line = in.readLine();
        assertEquals("1 toto tata tutu", line);
        in.reset();
        line = in.readLine();
        assertEquals("1 toto tata tutu", line);
    }
     
    public void testUnread() throws Exception {
        AdvancedReader in = new AdvancedReader(new StringReader(text));
        assertEquals(0, in.getCharNumber());
        assertEquals(0, in.getLineNumber());
        String line = in.readLine();
        assertEquals(3, in.getCharNumber());
        assertEquals(1, in.getLineNumber());
        line = in.readLine();
        assertEquals(4 + "1 toto tata tutu".length(), in.getCharNumber());
        assertEquals(2, in.getLineNumber());
        assertEquals("1 toto tata tutu", line);
        in.unread(line.length() + 1);
        assertEquals(3, in.getCharNumber());
        assertEquals(1, in.getLineNumber());
        line = in.readLine();
        assertEquals(4 + "1 toto tata tutu".length(), in.getCharNumber());
        assertEquals(2, in.getLineNumber());
        assertEquals("1 toto tata tutu", line);
    }

    public void testSkip() throws Exception {
        AdvancedReader in = new AdvancedReader(new StringReader(text));
        in.skip(3);
        String line = in.readLine();
        assertEquals("1 toto tata tutu", line);
        in.skip(text.length());
        line = in.readLine();
        assertNull(line);
    }

}


