/* ##%
 * Copyright (C) 2002, 2003 Code Lutin
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
 * ##%*/

/*
 * Parser.java
 *
 * Created: 7 oct. 2003
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

import java.util.ArrayList;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.codelutin.util.Resource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.FileWriter;

public abstract class Parser { // Parser

    // Nivos de débuggage
    public static final Object DEBUG_LEVEL0 = null;
    public static final Object DEBUG_LEVEL1 = new Object();
    public static final Object DEBUG_LEVEL2 = new Object();
    public static final Object DEBUG_LEVEL3 = new Object();

    // pour activer le mode debugage
    public static Object DEBUG = DEBUG_LEVEL0; // null; //

    // formats de sortie
    static final Object TYPE_RST = new Object();
    static final Object TYPE_HTML = new Object();
    static final Object TYPE_XDOC = new Object();
    static final Object TYPE_XML = new Object();

    static protected boolean overwrite = true;

    static public void help() {
            System.out.println("--[ JRsT 0.0a - www.codelutin.com - 2003,2004 ]--");
            System.out.println("");
            System.out.println("usage :  jrst [--html|--xdoc|--xml|--rst] [[--nooverwrite] -o outfile] document.rst");
            System.out.println("");
            System.out.println("-h, --help   this help");
            System.out.println("--html       (default)");
            System.out.println("--xdoc       ");
            System.out.println("--xml        ");
            System.out.println("--rst        generate with the selected format");
            System.out.println("--nooverwrite don't generate file allready uptodate");
            System.out.println("-o file      output file (--output)");
            System.out.println("-v #         verbosity : 0 <= # <= 3");
    }


    static public void main(String [] args) throws Exception {

        String fileIn = null;
        String fileOut = null;
        Object type = null;

        if(args.length > 0){
            for(int i = 0; i < args.length; i ++) {
                if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                    help();
                    return;
                }else if ( "--html".equals(args[i]) ) {
                    type = TYPE_HTML;
                }else if ( "--xdoc".equals(args[i]) ) {
                    type = TYPE_XDOC;
                }else if ( "--xml".equals(args[i]) ) {
                    type = TYPE_XML;
                }else if ( "--rst".equals(args[i]) ) {
                    type = TYPE_RST;
                }else if ( "--nooverwrite".equals(args[i]) ) {
                    overwrite = false;
                }else if ( "-o".equals(args[i]) ) {
                    fileOut = args[++i];
                }else if ( "-v".equals(args[i]) ) {
                    String value = args[++i];
                    if ( "0".equals(value) )
                        DEBUG = DEBUG_LEVEL0;
                    else if ( "1".equals(value) )
                        DEBUG = DEBUG_LEVEL1;
                    else if ( "2".equals(value) )
                        DEBUG = DEBUG_LEVEL2;
                    else if ( "3".equals(value) )
                        DEBUG = DEBUG_LEVEL3;
                }else if (args[i].matches("\\-+.*")) {
                    System.out.println("Unknown argument : " +args[i]+ "\n");
                    help();
                    return;
                }else{
                    fileIn = args[i];
                }
            }
            if (type == null) {
                type = TYPE_HTML;
            }
        }else{
            help();
            return;
        }

        parse(type, fileIn, fileOut);

    }

    static public void parse(Object type, String fileIn, String fileOut)  throws Exception{
        if(!overwrite && new File(fileIn).lastModified() <= new File(fileOut).lastModified()){
            if(DEBUG == DEBUG_LEVEL0){
                System.out.println("File " + fileOut +" is uptodate compared to " + fileIn);
            }
            return;
        }

        Reader in = null;
        try{
            in = new LineNumberReader(new FileReader(fileIn));
        }catch (IOException ioe) {
            System.err.println("Error with outfile ("+fileIn+") : "+ioe.getMessage());
            return;
        }
        Writer out = null;

        // Instanciation du Générateur
        Generator gen = null;
        if (type == TYPE_HTML) {
            gen = new HtmlGenerator();
        }else if (type == TYPE_XDOC) {
            gen = new XdocGenerator();
        }else if (type == TYPE_XML) {
            gen = new XmlGenerator();
        }else if (type == TYPE_RST) {
            gen = new RstGenerator();
        }else{
            System.err.println("unknown outfile kind -HTML by default-");
            gen = new HtmlGenerator();
        }

        // fichier de sortie
        if (fileOut != null){
            File foout = new File(fileOut);

            if ( foout.getParent() != null) {
                File parent = foout.getParentFile();
                if (! parent.mkdirs()){
                    System.err.println("Path "+foout.getParent()+" not created");
                }
            }

            try {
                out = new BufferedWriter(new FileWriter(fileOut));
                gen.setOs(out);
            }catch ( IOException ioe) {
                System.err.println("Error with infile ("+fileOut+") : "+ioe.getMessage());
            }
        }


        // Lecture de la hiérarchie des éléments
        DocumentFactory document = new DocumentFactory();
        FactoryParser fp = new FactoryParser(Resource.getURL("jrst.xml"));
        document = (DocumentFactory)fp.getInstance();

        /** PARSAGE **/
        if (DEBUG != null)
            System.out.println("\033[00;31mParsing...\033[00m");
        ParseResult result = ParseResult.IN_PROGRESS;
        // on considère qu'avant le document il y a une ligne blanche
        int c = (int)'\n';
        while(c != -1 && ((result = document.parse(c)) == ParseResult.IN_PROGRESS)){
            c = in.read();
        }
        // après le document il y a une ligne blanche
        if (c == -1) {
            result = document.parse((int)'\n');
            result = document.parseEnd((int)'D'); // 'D' like DAT green
        }else{
//            result = document.parseEnd((int)'D'); // 'D' like DAT green
            document.getBuffer().delete(0,result.getConsumedCharCount()-50);
        }

        /** GENERATION **/
        if (DEBUG != null)
            System.out.println("\033[00;31mGenerating...\033[00m");
        // lancement de la génération du document de sortie
        Element e = document.getElement();
        gen.visit(e);


        if (out != null) {
            out.close();
            gen.close();
        }
        if(result == ParseResult.FAILED){
            System.out.println("\033[01;31m------[-- ERROR ### --]-------------------------------------\033[00m");
            System.out.println("Nombre de caractères lu :"+ result.getConsumedCharCount());
            System.out.println(result.getError());
            System.out.print("buffer was : [");
            System.out.print(document.getBuffer().toString());
            System.out.println("]");
        }

    }

} // Parser

