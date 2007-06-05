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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelutin.util.FileUtil;
import org.codelutin.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;
import org.codelutin.i18n.I18n;


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
    static final private String docbook2odfDir = "/docbook2odf-0.211";
    static final private String docbook2xhtml = walshDir + "/xhtml/docbook.xsl";
    static final private String docbook2javahelp = walshDir + "/javahelp/javahelp.xsl";
//    static final private String dbkx2html = walshDir + "/html/onechunk.xsl";
    static final private String docbook2htmlhelp = walshDir + "/htmlhelp/htmlhelp.xsl";
    static final private String rst2rst = "/xsl/xml2rst.xsl";
    static final private String docbook2odf = docbook2odfDir + "/xsl/docbook.xsl";
    static final private String docbook2fo = walshDir + "/fo/docbook.xsl";
    
    // Out put type available
    static final public String TYPE_HTML = "html";
    static final public String TYPE_XDOC = "xdoc";
    static final public String TYPE_DOCBOOK = "docbook";
    static final public String TYPE_XHTML = "xhtml";
    static final public String TYPE_JAVAHELP = "javahelp";
    static final public String TYPE_HTMLHELP = "htmlhelp";
    static final public String TYPE_RST = "rst";
    static final public String TYPE_ODT = "odt";
    static final public String TYPE_FO = "fo";
    
    
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
        stylesheets.put(TYPE_ODT, rst2docbook+","+docbook2odf);
        stylesheets.put(TYPE_FO, rst2docbook + "," + docbook2fo);
    }
    
    static public void main(String [] args) throws Exception {
        if (args.length == 0)
            args = askOption();
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
    
    private static String[] askOption() {
        if (Locale.getDefault().getLanguage()=="fr")
            I18n.init("fr","FR");
        else
            I18n.init("en","US");
        ResourceBundle bundle = ResourceBundle.getBundle("org.codelutin.i18n.I18nBundleBridge");
        System.out.println(bundle.getString("help?"));
        Boolean done=false;
        String cheminRST="";
        while (!done){
            System.out.println(bundle.getString("rstFile?"));
            cheminRST = lire();
            if (cheminRST.length()==0)
                System.exit(0);
            File fileRST = new File(cheminRST);
            if (!fileRST.exists()){
                System.out.println(bundle.getString("dontExist"));
                cheminRST="";
            }
            else
                done=true;
        }
        done=false;
        String type ="";
        while (!done){
            type ="";
            System.out.println(bundle.getString("outputFormat?"));
            type = lire();
            if (type.matches("xhtml|docbook|xml|html|xdoc|rst|pdf|odt|rtf")||type.length()==0)
                done=true;
        }
        String cheminXSL="";
        if (type.length()==0){
            done=false;
            while (!done){
                System.out.println(bundle.getString("xslFile?"));
                cheminXSL = lire();
               
                File fileRST = new File(cheminXSL);
                if (!fileRST.exists()){
                    System.out.println(bundle.getString("dontExist"));
                    cheminXSL = "";
                }
                else
                    done=true;
            }
            if (cheminXSL.length()==0 || !type.matches("xhtml|docbook|xml|html|xdoc|rst|pdf|odt|rtf"))
                type="xml";
        }
        boolean ecraser=false;
        done=false;
        String cheminSortie="";
        while (!done){
            System.out.println(bundle.getString("outputFile?"));
            cheminSortie = lire();
           
            File fileRST = new File(cheminSortie);
            if (fileRST.exists()){
                String strEcraser="";
                do {
                    System.out.println(bundle.getString("overwrite?"));
                    strEcraser = lire();
                }while(!strEcraser.matches("y|n|o"));
                if (strEcraser.equals("y")||strEcraser.equals("o")){
                    done=true;
                    ecraser=true;
                }
            }
            else
                done=true;
        }
        String cmd = "";
        if (ecraser)
            cmd += "--force ";
        cmd += "-t " + type;
        if (cheminXSL.length()>0)
            cmd += " -x " + cheminXSL;
        if (cheminSortie.length()>0)
            cmd += " -o " + cheminSortie;
        cmd += " " + cheminRST + " "; 
        return cmd.split(" ");
    }
    public static String lire(){
        String ligne_lue=null;
        try {InputStreamReader isr = new InputStreamReader(System.in);
              BufferedReader br = new BufferedReader(isr);
              ligne_lue=br.readLine();
         }
        catch(IOException e) {System.err.println(e);}     
     return ligne_lue;
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
        
        @Option(shortName="t", pattern = "xhtml|docbook|xml|html|xdoc|rst|odt|fo", // TODO pdf|rst|odt|rtf",                
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


