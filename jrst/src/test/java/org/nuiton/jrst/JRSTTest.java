/*
 * #%L
 * JRst :: Api
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2004 - 2010 CodeLutin
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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author chemit
 */
public class JRSTTest {

    /** to use log facility, just put in your code: log.info("..."); */
    protected static Log log = LogFactory.getLog(JRSTTest.class);
    static File basedir;
    static File testBasedir;

    @BeforeClass
    public static void beforeClass() {
        String tmp = System.getProperty("basedir");
        if (tmp == null || tmp.isEmpty()) {
            tmp = new File("").getAbsolutePath();
        }
        basedir = new File(tmp);
        testBasedir = new File(basedir,
                "target" + File.separator +
                "tests" + File.separator +
                //                "tests-" + System.nanoTime() + File.separator +
                JRSTTest.class.getName());
        testBasedir.mkdirs();
        log.info("test basedir : " + testBasedir);
    }

    @Test
    public void generateRst() throws Exception {
        File in = new File(testBasedir, "toRst1-in.rst");

        List<String> IN_LINES = Arrays.asList(new String[]{
                    "=====",
                    "Title",
                    "=====",
                    //            "",
                    "SubTitle",
                    "--------",
                    //            "",
                    ".. topic::  TopicTitle",
                    //            "",
                    "   TopicBody.",
                    //            "",
                    //            "",
                    "Another body outside of the topic :)"
                });
        List<String> OUT_LINES = Arrays.asList(new String[]{
                    "Title",
                    "-----",
                    //            "",
                    "SubTitle",
                    "--------",
                    //            "",
                    ".. topic::  TopicTitle",
                    //            "",
                    " TopicBody.",
                    //            "",
                    //            "",
                    "Another body outside of the topic :)"
                });

        FileUtils.writeLines(in, IN_LINES);

        File out = new File(testBasedir, "toRst1-out.rst");

        JRST.generate(JRST.TYPE_RST, in, out, JRST.Overwrite.ALLTIME);

        List<?> readLines = FileUtils.readLines(out);
        Assert.assertEquals(OUT_LINES.size(), readLines.size());
        for (int i = 0, j = OUT_LINES.size(); i < j; i++) {
            String inLine = OUT_LINES.get(i);
            String outLine = (String) readLines.get(i);
            Assert.assertEquals(inLine, outLine);

        }
    }

    @Test
    public void generateHtml() throws Exception {

        File in = new File(basedir, "src/test/resources/text.rst");

        File out = new File(testBasedir, "jrst-RstToHtml2.html");

        JRST.generate(JRST.TYPE_HTML, in, out, JRST.Overwrite.ALLTIME);

    }
}
