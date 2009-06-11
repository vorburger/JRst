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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JRSTGeneratorTest.
 *
 * Created: 31 oct. 06 11:14:19
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
public class JRSTGeneratorTest {

    /** to use log facility, just put in your code: log.info("..."); */
    protected static Log log = LogFactory.getLog(JRSTGeneratorTest.class);
    
    @Test
    public void testRstToRst() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to RST (test.rst)...");
        }
        
        File test1 = File.createTempFile("jrst-RstToRst", ".rst");
        JRST.main(new String[] { "-t", "rst", "--force", "-o", test1.getAbsolutePath(), "src/test/resources/test.rst", });
        test1.delete();
    }
    
    @Test
    public void testRstToHtml() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to HTML (test.rst)...");
        }
        
        File test1 = File.createTempFile("jrst-RstToHtml2", ".html");
        JRST.main(new String[] { "-t", "html", "--force", "-o", test1.getAbsolutePath(), "src/test/resources/text.rst" });
        test1.delete();
    }
    
    @Test
    public void testRstToDocbook() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to Docbook (test.rst)...");
        }
        
        File test1 = File.createTempFile("jrst-RstToDocbook", ".dbk");
        JRST.main(new String[]{"-t", "docbook", "--force","-o", test1.getAbsolutePath(),
                 "src/test/resources/test.rst"});
        test1.delete();
    }
    
    @Test
    public void testRstToXdoc() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to Xdoc (test.rst)...");
        }
        
        File test1 = File.createTempFile("jrst-RstToXdoc", ".xdoc");
        JRST.main(new String[] { "-t", "xdoc", "--force","-o", test1.getAbsolutePath(), 
        "src/test/resources/test.rst" });
        test1.delete();
    }

    /**
     * This test is not working.
     * 
     * @throws Exception
     */
    @Ignore
    public void testRstToXdoc2() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to Xdoc (test2.rst)...");
        }
        
        File test1 = File.createTempFile("jrst-RstToXdoc2", ".xdoc");
        JRST.main(new String[] { "-t", "xdoc", "--force","-o", test1.getAbsolutePath(), 
                "src/test/resources/test2.rst" });
        test1.delete();
    }
    
    @Test
    public void testRstToXdoc3() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to Xdoc (test3.rst) ...");
        }
        
        File test1 = File.createTempFile("jrst-RstToXdoc3", ".xdoc");
        JRST.main(new String[] { "-t", "xdoc", "--force","-o", test1.getAbsolutePath(), 
                "src/test/resources/test3.rst" });
        test1.delete();
    }
    
    @Test
    public void testRstToXdoc4() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to Xdoc (test4.rst) ...");
        }
        
        File test1 = File.createTempFile("jrst-RstToXdoc4", ".xdoc");
        JRST.main(new String[] { "-t", "xdoc", "--force","-o", test1.getAbsolutePath(), 
                "src/test/resources/test4.rst" });
        test1.delete();
    }
    
    @Test
    public void testRstToXdocJrstSite() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to Xdoc (frEntier.rst) ...");
        }
        
        File test1 = File.createTempFile("jrst-RstToXdocJrstSite", ".xdoc");
        JRST.main(new String[] { "-t", "xdoc", "--force","-o", test1.getAbsolutePath(), 
        "src/test/resources/frEntier.rst" });
        test1.delete();
    }
    
    @Ignore
    public void testRstToPDF() throws Exception {
        
        if (log.isInfoEnabled()) {
            log.info("Testing RST to PDF (frEntier.rst) ...");
        }
        
        File test1 = File.createTempFile("jrst-RstToPDF", ".pdf");
        JRST.main(new String[] { "-t", "pdf", "--force","-o", test1.getAbsolutePath(), 
        "src/test/resources/frEntier.rst" });
        test1.delete();
    }
}
