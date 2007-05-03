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
 * JRST.java
 *
 * Created: 3 nov. 06 20:56:00
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelutin.util.FileUtil;
import org.codelutin.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;


/**
 * FIXME: 'JRST --help' doesn't work, but 'JRST --help toto' work :(
 * 
 * @author poussin
 */
@CommandLineInterface(application="JRST")
public class JRST {

    public static enum Overwrite {
        NEVER,
        IFNEWER,
        ALLTIME
    }
    
    /** to use log facility, just put in your code: log.info(\"...\"); */
    static private Log log = LogFactory.getLog(JRST.class);

    // XSL Stylesheet to transforme something to other format 
    static final private String rst2xhtml = "/xsl/rst2xhtml.xsl";
    static final private String rst2xdoc = "/xsl/rst2xdoc.xsl";
    static final private String rst2docbook = "/xsl/dn2dbk.xsl";
    static final private String walshDir = "/docbook-xsl-nwalsh";    
    static final private String docbook2xhtml = walshDir + "/xhtml/docbook.xsl";
    static final private String docbook2javahelp = walshDir + "/javahelp/javahelp.xsl";
//    static final private String dbkx2html = walshDir + "/html/onechunk.xsl";
    static final private String docbook2htmlhelp = walshDir + "/htmlhelp/htmlhelp.xsl";
    static final private String rst2rst = "/xsl/xml2rst.xsl";
    
    // Out put type available
    static final public String TYPE_HTML = "html";
    static final public String TYPE_XDOC = "xdoc";
    static final public String TYPE_DOCBOOK = "docbook";
    static final public String TYPE_XHTML = "xhtml";
    static final public String TYPE_JAVAHELP = "javahelp";
    static final public String TYPE_HTMLHELP = "htmlhelp";
    static final public String TYPE_RST = "rst";
    
    /** key, Out type; value: chain of XSL file to provide wanted file for output */
    static private Map<String, String> stylesheets = null;
    
    static {
        stylesheets = new HashMap<String, String>();
        stylesheets.put(TYPE_HTML, rst2xhtml);
        stylesheets.put(TYPE_XDOC, rst2xdoc);
        stylesheets.put(TYPE_DOCBOOK, rst2docbook);
        stylesheets.put(TYPE_XHTML, rst2docbook+","+docbook2xhtml);
        stylesheets.put(TYPE_JAVAHELP, rst2docbook+","+docbook2javahelp);
        stylesheets.put(TYPE_HTMLHELP, rst2docbook+","+docbook2htmlhelp);
        stylesheets.put(TYPE_RST, rst2rst);
    }
    
    static public void main(String [] args) throws Exception {
        JRSTOption option = CliFactory.parseArguments(JRSTOption.class, args);
        
        if (option.isHelp()) {
            Cli<JRSTOption> cli = CliFactory.createCli(JRSTOption.class);
            System.out.println(cli.getHelpMessage());
            return;
        }
                
        // search xsl file list to apply
        String xslList = null;
        if (option.isXslFile()) {
            xslList = option.getXslFile();
        } else {            
            xslList = option.getOutType();            
        }

        generate(xslList, option.getFile(), option.getOutFile(), option.isForce()?Overwrite.ALLTIME:Overwrite.NEVER);
    }
    
    public static void generate(String xslListOrOutType, File fileIn, File fileOut,
            Overwrite overwrite) throws Exception {
        if (fileOut != null && fileOut.exists() && 
                (overwrite == Overwrite.NEVER || 
                        (overwrite == Overwrite.IFNEWER && FileUtil.isNewer(fileIn, fileOut)))) {
            log.info("Don't generate file "+fileOut+", because already exists");
        } else {
            // search xsl file list to apply
            String xslList = stylesheets.get(xslListOrOutType);            
            if (xslListOrOutType == null) {
                xslList = xslListOrOutType;
            }
            
            // prepare the output flux
            XMLWriter out = null;
            if (fileOut != null) {
                fileOut.getParentFile().mkdirs();
                out = new XMLWriter(new FileWriter(fileOut), new OutputFormat("  ", true));            
            } else {
                out = new XMLWriter(System.out, new OutputFormat("  ", true));
            }
    
            // parse rst file
            URL url = fileIn.toURL();
            Reader in = new InputStreamReader(url.openStream());        
            JRSTReader jrst = new JRSTReader();
            Document doc = jrst.read(in);       
            
            // apply xsl on rst xml document
            JRSTGenerator gen = new JRSTGenerator();
            String [] xsls = StringUtil.split(xslList, ",");
            for (String xsl : xsls) {
                URL stylesheet = null;
                File file = new File(xsl);            
                if (file.exists()) {
                    stylesheet = file.toURL();
                } else {
                    stylesheet = JRST.class.getResource(xsl);
                }
                if (stylesheet == null) {
                    throw new FileNotFoundException("Can't find stylesheet: " + xsl);
                }
                doc = gen.transform(doc, stylesheet);
            }
            
            // write generated document
            out.write(doc);
            
            if (fileOut != null) {
                out.close();
            }
        }
    }
    
    public static interface JRSTOption {
        
        @Option(description="display this help and exit")
        boolean isHelp();
        
        @Option(description="overwrite existing out file")
        boolean isForce();
        
        @Option(shortName="x", description = "XSL file list to apply, comma separated")
        public String getXslFile();        
        public boolean isXslFile();
        
        @Option(shortName="t", pattern = "xhtml|docbook|xml|html|xdoc|rst", // TODO |pdf|rst|odt|rtf",                
                description = "Output type")
        public String getOutType();
        public boolean isOutType();

        @Option(shortName="o", description = "Output file")
        public File getOutFile();
        public boolean isOutFile();

        @Unparsed(name = "FILE")
        public File getFile();
    }
    
}


