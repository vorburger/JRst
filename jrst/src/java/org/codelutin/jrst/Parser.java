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

public abstract class Parser { // Parser

    static public void help() {
            System.out.println("--[ JRsT 0.0a - www.codelutin.com - 2003,2004 ]--");
            System.out.println("");
            System.out.println("usage :  jrst [--html|--xdoc|--xml|--rst] document.rst");
            System.out.println("");
            System.out.println("-h, --help   this help");
            System.out.println("--html       (default)");
            System.out.println("--xdoc       ");
            System.out.println("--xml        ");
            System.out.println("--rst        generate with the selected format");
    }


    static public void main(String [] args) throws Exception {

        Generator gen = null;
        Reader in = null;
        if(args.length > 0){
            for(int i = 0; i < args.length; i ++) {
                if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                    help();
                    return;
                }else if ( "--html".equals(args[i]) ) {
                    gen = new HtmlGenerator();
                }else if ( "--xdoc".equals(args[i]) ) {
                    gen = new XdocGenerator();
                }else if ( "--xml".equals(args[i]) ) {
                    gen = new XmlGenerator();
                }else if ( "--rst".equals(args[i]) ) {
                    gen = new RstGenerator();
                }else if (args[i].matches("\\-+.*")) {
                    System.out.println("Unknown argument : " +args[i]+ "\n");
                    help();
                    return;
                }else{
                    //System.out.println("Lecture du fichier " + filename);
                    in = new LineNumberReader(new FileReader(args[i]));
                }
            }
            if (gen == null) {
                gen = new HtmlGenerator();
            }
        }else{
            help();
            return;
        }

        // Lecture de la hiérarchie des éléments
        DocumentFactory document = new DocumentFactory();
        FactoryParser fp = new FactoryParser("jrst.xml");
        document = (DocumentFactory)fp.getInstance();


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
            result = document.parseEnd((int)'D'); // 'D' like DAT green
//            document.getBuffer().delete(0,);
        }

        Element e = document.getElement();
        gen.visit(e);
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

