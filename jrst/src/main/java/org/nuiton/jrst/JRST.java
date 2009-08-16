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

import static org.nuiton.i18n.I18n._;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.nuiton.i18n.I18n;
import org.nuiton.util.FileCompletion;
import org.nuiton.util.FileUtil;
import org.nuiton.util.Resource;
import org.nuiton.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import org.nuiton.jrst.convertisor.DocUtils2RST;
import org.nuiton.jrst.convertisor.DocUtilsVisitor;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.CommandLineInterface;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

/**
 * FIXME: 'JRST --help' doesn't work, but 'JRST --help toto' work :( FIXME:
 * 'JRST -c' doesn't work, but 'JRST -c toto'
 * 
 * Created: 3 nov. 06 20:56:00
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
@CommandLineInterface(application = "JRST")
public class JRST {

    public static enum Overwrite {
        NEVER, IFNEWER, ALLTIME
    }

    /** to use log facility, just put in your code: log.info("..."); */
    protected static Log log = LogFactory.getLog(JRST.class);

    /** XSL Stylesheet to transform RST into HTML. */
    protected static final String rst2xhtml = "/xsl/rst2xhtml.xsl";
    /** XSL Stylesheet to transform RST into Xdoc. */
    protected static final String rst2xdoc = "/xsl/rst2xdoc.xsl";
    /** XSL Stylesheet to transform RST into Docbook. */
    protected static final String rst2docbook = "/xsl/dn2dbk.xsl";

    /** XSL Stylesheet to transform Docbook into xHTML. */
    protected static final String docbook2xhtml = "/xhtml/docbook.xsl";
    /** XSL Stylesheet to transform Docbook into javahelp. */
    protected static final String docbook2javahelp = "/javahelp/javahelp.xsl";
    /** XSL Stylesheet to transform Docbook into htmlhelp. */
    protected static final String docbook2htmlhelp = "/htmlhelp/htmlhelp.xsl";
    
//    /** XSL Stylesheet to transform xml into rst. */
//    protected static final String rst2rst = "JRSTWriter";

    /** XSL Stylesheet to transform Docbook into ODF. */
    protected static final String docbook2odf = "/xsl/docbook2odf-0.244/docbook.xsl";
    /** XSL Stylesheet to transform Docbook into PDF. */
    protected static final String docbook2fo = "/fo/docbook.xsl";

    /** HTML output format type */
    public static final  String TYPE_HTML = "html";

    /** XDOC output format type */
    public static final String TYPE_XDOC = "xdoc";

    /** DOCBOOK output format type */
    public static final String TYPE_DOCBOOK = "docbook";

    /** XHTML output format type */
    public static final String TYPE_XHTML = "xhtml";

    /** JAVA HELP output format type */
    public static final String TYPE_JAVAHELP = "javahelp";

    /** HTML HELP output format type */
    public static final String TYPE_HTMLHELP = "htmlhelp";

    /** RST output format type */
    public static final String TYPE_RST = "rst";

    /** ODT output format type */
    public static final String TYPE_ODT = "odt";
    
    /** FO output format type */
    public static final String TYPE_FO = "fo";

    /** PDF output format type */
    public static final String TYPE_PDF = "pdf";

    /**
     * key, Out type; value: chain of XSL file to provide wanted file for output
     */
    protected static Map<String, String> stylesheets = null;

    static {
        stylesheets = new HashMap<String, String>();
        stylesheets.put(TYPE_HTML, rst2xhtml);
        stylesheets.put(TYPE_XDOC, rst2xdoc);
        stylesheets.put(TYPE_DOCBOOK, rst2docbook);
        stylesheets.put(TYPE_XHTML, rst2docbook + "," + docbook2xhtml);
        stylesheets.put(TYPE_JAVAHELP, rst2docbook + "," + docbook2javahelp);
        stylesheets.put(TYPE_HTMLHELP, rst2docbook + "," + docbook2htmlhelp);
        stylesheets.put(TYPE_RST, "");
        stylesheets.put(TYPE_ODT, rst2docbook + "," + docbook2odf);
        stylesheets.put(TYPE_FO, rst2docbook + "," + docbook2fo);
        stylesheets.put(TYPE_PDF, rst2docbook + "," + docbook2fo);
    }

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        
        I18n.init();

        if (args.length == 0) {
            args = askOption();
        }
        if (args == null) {
            System.exit(0);
        }

        JRSTOption option = CliFactory.parseArguments(JRSTOption.class, args);

        if (option.isHelp()) {
            Cli<JRSTOption> cli = CliFactory.createCli(JRSTOption.class);
            System.out.println(cli.getHelpMessage());
            return;
        }
        if (option.isConsole()) {
            args = askOptionText();
        }
        option = CliFactory.parseArguments(JRSTOption.class, args);
        // search xsl file list to apply
        String xslList = null;
        if (option.isXslFile()) {
            xslList = option.getXslFile();
        } else {
            xslList = option.getOutType();
        }

        generate(xslList, option.getFile(), option.getOutFile(), option
                .isForce() ? Overwrite.ALLTIME : Overwrite.NEVER);
    }

    private static String[] askOption() throws SecurityException,
            NoSuchMethodException, IOException {
        String[] result = null;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment
                    .getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            boolean done = false;
            if (!(gs == null)) {
                if (gs.length > 0) {
                    result = askOptionGraph();
                    done = true;
                }
            }
            if (!done) {
                result = askOptionText();
            }
        } catch (java.awt.HeadlessException e) {
            result = askOptionText();
        }

        return result;

    }

    /**
     * interface graphique
     * 
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    private static String[] askOptionGraph() throws SecurityException,
            NoSuchMethodException {
        
        Method m = JRSTOption.class.getMethod("getOutType");
        Option a = m.getAnnotation(Option.class);
        JRSTInterface graph = new JRSTInterface(a.pattern());

        // String[] result=graph.askOption();
        return graph.getCmd();
    }

    /**
     * Interface textuel
     * 
     * @return String[]
     * @throws IOException
     */
    private static String[] askOptionText() throws IOException {
        
        System.out.println(_("help?"));
        Boolean done = false;
        String cheminRST = "";
        while (!done) {
            System.out.println(_("rstFile?"));
            cheminRST = lireFile(false, false);
            if (cheminRST.length() == 0) {
                System.exit(0);
            }
            File fileRST = new File(cheminRST);
            if (!fileRST.exists()) {
                System.out.println(_("dontExist"));
                cheminRST = "";
            } else {
                done = true;
            }
        }
        done = false;
        String type = "";
        while (!done) {
            type = "";
            System.out.println(_("outputFormat?"));
            type = lire();
            if (type.matches("xhtml|docbook|xml|html|xdoc|rst|pdf|odt|rtf")
                    || type.length() == 0) {
                done = true;
            }
        }
        String cheminXSL = "";
        if (type.length() == 0) {
            done = false;
            while (!done) {
                System.out.println(_("xslFile?"));
                String cheminXSLtmp = lireFile(false, true);

                File fileRST = new File(cheminXSLtmp);
                if (cheminXSLtmp.equals("")) {
                    if (cheminXSL.length() != 0) {
                        cheminXSL = cheminXSL.substring(0,
                                cheminXSL.length() - 1);
                    }
                    done = true;
                } else {
                    if (!fileRST.exists()) {
                        System.out.println(_("dontExist"));
                    } else {
                        cheminXSL += cheminXSLtmp;
                        String other = "";
                        do {
                            System.out.println(_("other?"));
                            other = lire();
                        } while (!other.matches("y|n|o"));
                        if (other.equals("y") || other.equals("o")) {
                            cheminXSL += ",";
                        }
                        else if (other.equals("n")) {
                            done = true;
                        }
                    }
                }

            }
            if (cheminXSL.length() == 0
                    || !type
                            .matches("xhtml|docbook|xml|html|xdoc|rst|pdf|odt|rtf")) {
                type = "xml";
            }
        }
        boolean ecraser = false;
        done = false;
        String cheminSortie = "";
        while (!done) {
            System.out.println(_("outputFile?"));
            cheminSortie = lireFile(true, true);

            File fileRST = new File(cheminSortie);
            if (fileRST.exists()) {
                String strEcraser = "";
                do {
                    System.out.println(_("overwrite?"));
                    strEcraser = lire();
                } while (!strEcraser.matches("y|n|o"));
                if (strEcraser.equals("y") || strEcraser.equals("o")) {
                    done = true;
                    ecraser = true;
                }
            } else {
                done = true;
            }
        }
        String cmd = "";
        if (ecraser) {
            cmd += "--force ";
        }
        cmd += "-t " + type;
        if (cheminXSL.length() > 0) {
            cmd += " -x " + cheminXSL;
        }
        if (cheminSortie.length() > 0) {
            cmd += " -o " + cheminSortie;
        }
        cmd += " " + cheminRST + " ";
        return cmd.split(" ");
    }

    /**
     * lit une ligne
     * 
     * @return String
     */
    public static String lire() {

        String ligne_lue = null;
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            ligne_lue = br.readLine();
        } catch (IOException e) {
            System.err.println(e);
        }
        return ligne_lue;

    }

    public static String lireFile(boolean enreg, boolean exit)
            throws IOException {
        String line = "";
        FileCompletion fc = new FileCompletion(enreg, exit);
        if (fc.consoleAvailable()) {
            line = fc.read();
        }
        if (line == null) {
            line = "";
        }
        return line;
    }

    public static void generate(String xslListOrOutType, File fileIn,
            File fileOut, Overwrite overwrite) throws Exception {
        generate(xslListOrOutType, fileIn, "UTF-8", fileOut, "UTF-8", overwrite);
    }

    /**
     * 
     * @param xslListOrOutType
     * @param fileIn
     * @param inputEncoding
     * @param fileOut
     * @param outputEncoding
     * @param overwrite
     * @throws Exception
     */

    public static void generate(String xslListOrOutType, File fileIn,
            String inputEncoding, File fileOut, String outputEncoding,
            Overwrite overwrite) throws Exception {
        if (fileOut != null
                && fileOut.exists()
                && (overwrite == Overwrite.NEVER || (overwrite == Overwrite.IFNEWER && FileUtil
                        .isNewer(fileIn, fileOut)))) {
            // System.err.println("Don't generate file " + fileOut +
            // ", because already exists");
            log.info("Don't generate file " + fileOut
                    + ", because already exists");
        } else {
            // search xsl file list to apply
            String xslList = stylesheets.get(xslListOrOutType);
            if (xslListOrOutType == null) {
                xslList = xslListOrOutType;
            }

            // parse rst file
            URL url = fileIn.toURI().toURL();
            Reader in = new InputStreamReader(url.openStream(), inputEncoding);
            JRSTReader jrst = new JRSTReader();
            Document doc = jrst.read(in);

            // Sortie vers rst
            if (xslListOrOutType.equals(TYPE_RST)){
                // Creation d'un visitor qui convertie de l'xml vers le rst
                DocUtilsVisitor visitor = new DocUtils2RST();

                // Atacher le visitor au document
                // il va parcourir tout les elements et reconstruire du rst
                doc.accept(visitor);

                // Recuperation du resultat
                String result = visitor.getResult();
                // nettoyage du visiteur
                visitor.clear();
                // Ecriture du resultat dans un fichier
                // prepare the output flux
                FileWriter out = null;
                if (fileOut != null) {
                    try {
                        fileOut.getAbsoluteFile().getParentFile().mkdirs();
                        out = new FileWriter(fileOut);
                        // write generated document
                        out.write(result);
                    } finally {
                        out.close();
                    }
                } else {
                    // Si aucun fichier de sortie nest definie, on utilise la sortie standard
                    System.out.println(result);
                }
            }
            else {
                // apply xsl on rst xml document
                JRSTGenerator gen = new JRSTGenerator();
                String[] xsls = StringUtil.split(xslList, ",");
                for (String xsl : xsls) {
                    URL stylesheet = null;
                    File file = new File(xsl);
                    if (file.exists()) {
                        stylesheet = file.toURI().toURL();
                    } else {
                        //stylesheet = JRST.class.getResource(xsl);
                        stylesheet = Resource.getURL(xsl);
                    }
                    if (stylesheet == null) {
                        throw new FileNotFoundException("Can't find stylesheet: "
                                + xsl);
                    }

                    // add uri resolver
                    /*gen.setUriResolver(new URIResolver() {
                        public Source resolve(String href, String base) {
                            System.out.println("RESOLVING: href: "+href+" base: "+base);

                            return null;
                            try {
                                System.out.println("RESOLVING: href: "+href+" base: "+base);
                                StreamSource ss = new StreamSource(new FileInputStream(new File(href)));
                                return ss;
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    });*/

                    doc = gen.transform(doc, stylesheet);
                }

                boolean pdf = false;
                // generation PDF
                if (xslListOrOutType != null) {
                    if (xslListOrOutType.equals("pdf")) {
                        pdf = true;
                        FopFactory fopFactory = FopFactory.newInstance();
                        // OutputStream outPDF = new BufferedOutputStream(new
                        // FileOutputStream(new File("C:/Temp/myfile.pdf")));

                        OutputStream outPDF = null;
                        if (fileOut != null) {
                            fileOut.getAbsoluteFile().getParentFile().mkdirs();
                            outPDF = new BufferedOutputStream(new FileOutputStream(
                                    fileOut));
                        } else {
                            outPDF = new BufferedOutputStream(System.out);
                        }

                        FOUserAgent userAgent = fopFactory.newFOUserAgent();

                        // Step 3: Construct fop with desired output format
                        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF,
                                userAgent, outPDF);

                        // Step 4: Setup JAXP using identity transformer
                        TransformerFactory factory = TransformerFactory
                                .newInstance();
                        Transformer transformer = factory.newTransformer(); // identity
                        // transformer

                        // Step 5: Setup input and output for XSLT transformation
                        // Setup input stream
                        Source src = new StreamSource(new StringReader(doc.asXML()));

                        // Resulting SAX events (the generated FO) must be piped
                        // through to FOP
                        Result res = new SAXResult(fop.getDefaultHandler());

                        // Step 6: Start XSLT transformation and FOP processing
                        transformer.transform(src, res);

                        if (fileOut != null) {
                            outPDF.close();
                        }
                    }
                }
                if (!pdf) {
                    // prepare the output flux
                    XMLWriter out = null;
                    if (fileOut != null) {
                        fileOut.getAbsoluteFile().getParentFile().mkdirs();

                        out = new XMLWriter(FileUtil.getWriter(fileOut,
                                outputEncoding), new OutputFormat("  ", true,
                                outputEncoding));
                    } else {
                        out = new XMLWriter(System.out, new OutputFormat("  ",
                                true, outputEncoding));
                    }
                    // write generated document
                    out.write(doc);

                    if (fileOut != null) {
                        out.close();
                    }
                }
            }
        }
    }

    /**
     * Les options
     */
    public static interface JRSTOption {

        @Option(description = "display this help and exit")
        boolean isHelp();

        @Option(description = "overwrite existing out file")
        boolean isForce();

        @Option(shortName = "c", description = "Console mode")
        public boolean isConsole();

        @Option(shortName = "x", description = "XSL file list to apply, comma separated")
        public String getXslFile();

        public boolean isXslFile();

        @Option(shortName = "t", pattern = "xml|xhtml|docbook|html|xdoc|rst|fo|pdf", // TODO
        // odt|rtf",
        description = "Output type")
        public String getOutType();

        public boolean isOutType();

        @Option(shortName = "o", description = "Output file")
        public File getOutFile();

        public boolean isOutFile();

        @Unparsed(name = "FILE")
        public File getFile();
    }

}
