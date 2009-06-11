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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * AdvancedReaderTest.
 *
 * Created: 27 oct. 06 01:03:26
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
public class AdvancedReaderTest {

    protected String text = "\t\t\n" + "1 toto tata tutu\n" + "2 toto tata tutu\n"
            + "3 toto tata tutu\n" + "4 toto tata tutu\n"
            + "5 toto tata tutu\n" + "6 toto tata tutu\n"
            + "7 toto tata tutu\n" + "8 toto tata tutu\n"
            + "9 toto tata tutu\n";

    protected File file;
    
    /**
     * setUp - create test file.
     * @throws IOException 
     */
    @Before
    public void setUp() throws IOException {
        file = File.createTempFile("test-AdvancedReader", ".txt");
        file.deleteOnExit();

        Writer out = new BufferedWriter(new FileWriter(file));
        out.write(text);
        out.close();
    }

    /**
     * Test method for {@link org.nuiton.jrst.AdvancedReader#eof()}.
     * @throws IOException 
     */
    @Test
    public void testEof() throws IOException {
        AdvancedReader in = new AdvancedReader(new FileReader(file));
        Assert.assertFalse(in.eof());
        in.readAll();
        Assert.assertTrue(in.eof());

        in = new AdvancedReader(new StringReader(text));
        Assert.assertFalse(in.eof());
        in.readAll();
        Assert.assertTrue(in.eof());
    }

    /**
     * Test method for
     * {@link org.nuiton.jrst.AdvancedReader#skipBlankLines()}.
     * @throws IOException 
     */
    public void testSkipBlankLines() throws IOException {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            in.skipBlankLines();
            String line = in.readLine();
            Assert.assertEquals("1 toto tata tutu", line);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            in.skipBlankLines();
            String line = in.readLine();
            Assert.assertEquals("1 toto tata tutu", line);
        }
    }

    /**
     * Test method for {@link org.nuiton.jrst.AdvancedReader#readAll()}.
     * @throws IOException 
     */
    public void testReadAll() throws IOException {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String[] lines = in.readAll();
            Assert.assertEquals(10, lines.length);
            Assert.assertEquals("\t\t", lines[0]);
            Assert.assertEquals("1 toto tata tutu", lines[1]);
            Assert.assertEquals("9 toto tata tutu", lines[9]);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String[] lines = in.readAll();
            Assert.assertEquals(10, lines.length);
            Assert.assertEquals("\t\t", lines[0]);
            Assert.assertEquals("1 toto tata tutu", lines[1]);
            Assert.assertEquals("9 toto tata tutu", lines[9]);
        }
    }

    /**
     * Test method for {@link org.nuiton.jrst.AdvancedReader#readLines(int)}.
     * @throws IOException 
     */
    public void testReadLines() throws IOException {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String[] lines = in.readLines(3);
            Assert.assertEquals(3, lines.length);
            Assert.assertEquals("\t\t", lines[0]);
            Assert.assertEquals("1 toto tata tutu", lines[1]);
            Assert.assertEquals("2 toto tata tutu", lines[2]);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String[] lines = in.readLines(3);
            Assert.assertEquals(3, lines.length);
            Assert.assertEquals("\t\t", lines[0]);
            Assert.assertEquals("1 toto tata tutu", lines[1]);
            Assert.assertEquals("2 toto tata tutu", lines[2]);
        }
    }

    /**
     * Test method for
     * {@link org.nuiton.jrst.AdvancedReader#readUntil(java.lang.String)}.
     * @throws IOException 
     */
    public void testReadUntil() throws IOException {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String[] lines = in.readUntil("^3.*$");
            Assert.assertEquals(3, lines.length);
            Assert.assertEquals("\t\t", lines[0]);
            Assert.assertEquals("1 toto tata tutu", lines[1]);
            Assert.assertEquals("2 toto tata tutu", lines[2]);

            String line = in.readLine();
            Assert.assertEquals("3 toto tata tutu", line);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String[] lines = in.readUntil("^3.*$");
            Assert.assertEquals(3, lines.length);
            Assert.assertEquals("\t\t", lines[0]);
            Assert.assertEquals("1 toto tata tutu", lines[1]);
            Assert.assertEquals("2 toto tata tutu", lines[2]);

            String line = in.readLine();
            Assert.assertEquals("3 toto tata tutu", line);
        }
    }

    /**
     * Test method for
     * {@link org.nuiton.jrst.AdvancedReader#readUntil(java.lang.String)}.
     * @throws IOException 
     */
    public void testReadWhile() throws IOException {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            in.skipBlankLines();
            String[] lines = in.readWhile("^[123].*$");
            Assert.assertEquals(3, lines.length);
            Assert.assertEquals("1 toto tata tutu", lines[0]);
            Assert.assertEquals("2 toto tata tutu", lines[1]);
            Assert.assertEquals("3 toto tata tutu", lines[2]);

            String line = in.readLine();
            Assert.assertEquals("4 toto tata tutu", line);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            in.skipBlankLines();
            String[] lines = in.readWhile("^[123].*$");
            Assert.assertEquals(3, lines.length);
            Assert.assertEquals("1 toto tata tutu", lines[0]);
            Assert.assertEquals("2 toto tata tutu", lines[1]);
            Assert.assertEquals("3 toto tata tutu", lines[2]);

            String line = in.readLine();
            Assert.assertEquals("4 toto tata tutu", line);
        }
    }

    /**
     * Test method for {@link org.nuiton.jrst.AdvancedReader#readLine()}.
     * @throws IOException 
     */
    public void testReadLine() throws IOException {
        {
            AdvancedReader in = new AdvancedReader(new FileReader(file));
            String line = in.readLine();
            Assert.assertEquals("\t\t", line);
            line = in.readLine();
            Assert.assertEquals("1 toto tata tutu", line);
            line = in.readLine();
            Assert.assertEquals("2 toto tata tutu", line);
        }

        {
            AdvancedReader in = new AdvancedReader(new StringReader(text));
            String line = in.readLine();
            Assert.assertEquals("\t\t", line);
            line = in.readLine();
            Assert.assertEquals("1 toto tata tutu", line);
            line = in.readLine();
            Assert.assertEquals("2 toto tata tutu", line);
        }
    }

    public void testMark() throws IOException {
        AdvancedReader in = new AdvancedReader(new StringReader(text));
        String line = in.readLine();
        in.mark();
        line = in.readLine();
        Assert.assertEquals("1 toto tata tutu", line);
        in.reset();
        line = in.readLine();
        Assert.assertEquals("1 toto tata tutu", line);
    }

    public void testUnread() throws IOException {
        AdvancedReader in = new AdvancedReader(new StringReader(text));
        Assert.assertEquals(0, in.getCharNumber());
        Assert.assertEquals(0, in.getLineNumber());
        String line = in.readLine();
        Assert.assertEquals(3, in.getCharNumber());
        Assert.assertEquals(1, in.getLineNumber());
        line = in.readLine();
        Assert.assertEquals(4 + "1 toto tata tutu".length(), in.getCharNumber());
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals("1 toto tata tutu", line);
        in.unread(line.length() + 1);
        Assert.assertEquals(3, in.getCharNumber());
        Assert.assertEquals(1, in.getLineNumber());
        line = in.readLine();
        Assert.assertEquals(4 + "1 toto tata tutu".length(), in.getCharNumber());
        Assert.assertEquals(2, in.getLineNumber());
        Assert.assertEquals("1 toto tata tutu", line);
    }

    public void testSkip() throws IOException {
        AdvancedReader in = new AdvancedReader(new StringReader(text));
        in.skip(3);
        String line = in.readLine();
        Assert.assertEquals("1 toto tata tutu", line);
        in.skip(text.length());
        line = in.readLine();
        Assert.assertNull(line);
    }

}
