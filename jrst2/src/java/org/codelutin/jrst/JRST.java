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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

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
 * @author poussin
 */
@CommandLineInterface(application="JRST")
public class JRST {

    static final private String docbook = "/xsl/dn2dbk.xsl";
    static final private String html = "/xsl/rst2xhtml.xsl";

    static final private String walshDir = "/docbook-xsl-nwalsh";
    
    static final private String xhtml = walshDir + "/xhtml/docbook.xsl";
    static final private String javahelp = walshDir + "/javahelp/javahelp.xsl";
//    static final private String dbkx2html = walshDir + "/html/onechunk.xsl";
    static final private String htmlhelp = walshDir + "/htmlhelp/htmlhelp.xsl";

    static final private String rst2xdoc = "/xsl/rst2xdoc.xsl";
    
    static private Map<String, String> stylesheets = null;
    
    static {
        stylesheets = new HashMap<String, String>();
        stylesheets.put("docbook", docbook);
        stylesheets.put("html", html);
        stylesheets.put("xhtml", docbook+","+xhtml);
        stylesheets.put("javahelp", docbook+","+javahelp);
        stylesheets.put("htmlhelp", docbook+","+htmlhelp);
        stylesheets.put("xdoc", rst2xdoc);
    }
    
    static public void main(String [] args) throws ArgumentValidationException, IOException, TransformerException, DocumentException {
        JRSTOption option = CliFactory.parseArguments(JRSTOption.class, args);
        
        if (option.isHelp()) {
            Cli<JRSTOption> cli = CliFactory.createCli(JRSTOption.class);
            System.out.println(cli.getHelpMessage());
            return;
        }
        
        // prepare the output flux
        XMLWriter out = null;
        if (option.isOutFile()) {
            out = new XMLWriter(new FileWriter(option.getFile()), new OutputFormat("  ", true));            
        } else {
            out = new XMLWriter(System.out, new OutputFormat("  ", true));
        }
        
        // search xsl file list to apply
        String xslList = null;
        if (option.isXslFile()) {
            xslList = option.getXslFile();
        } else {            
            xslList = stylesheets.get(option.getOutType());            
        }

        // parse rst file
        URL url = option.getFile().toURL();
        Reader in = new InputStreamReader(url.openStream());        
        JRSTReader jrst = new JRSTReader();
        Document doc = jrst.read(in);       
        
        // apply xsl on rst xml document
        String [] xsls = StringUtil.split(xslList, ",");
        for (String xsl : xsls) {
            URL stylesheet = null;
            File file = new File(xsl);            
            if (file.exists()) {
                stylesheet = file.toURL();
            } else {
                stylesheet = JRSTReaderTest.class.getResource(xsl);
            }
            JRSTGenerator gen = new JRSTGenerator();
            doc = gen.transform(doc, stylesheet);
        }
        
        // write generated document
        out.write(doc);
    }
    
    public static interface JRSTOption {
        
        @Option(description="display this help and exit")
        boolean isHelp();
        
        @Option(shortName="x", description = "XSL file list to apply, comma separated")
        public String getXslFile();        
        public boolean isXslFile();
        
        @Option(shortName="t", pattern = "xhtml|docbook|xml|html|xdoc", // TODO |pdf|rst|odt|rtf",                
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


