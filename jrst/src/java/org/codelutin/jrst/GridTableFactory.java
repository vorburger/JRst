/*##%
* Copyright (C) 2002, 2004 Code Lutin
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
 * GridTableFactory.java
 *
 * Created: 20 juillet 2004
 *
 */

package org.codelutin.jrst;

import jregex.Pattern;
import jregex.Matcher;

public class GridTableFactory extends AbstractFactory { // GridTableFactory

    /** Constantes **/

    /** Attributs **/

    // Acceptation par rapport à une expression régulière
    static Pattern pat = new Pattern("(\\+\\-+)+\\+\\n");
    static Matcher mat = pat.matcher();

    // tableau des lignes du tableau
    // et nombre de lignes dans le tableau
    StringBuffer[] theLines = null;
    int line = 0;

    // les cases du tableau
    StringBuffer[][] table = null;

    StringBuffer text = null;

    int type  = 0;
    int count = 0;

    boolean first = true;

    // position dans le tableau
    int x = 0;
    int y = 0;

    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return new GridTableFactory(); }
    protected Element elementNew(){ return new GridTable(); }
    protected GridTable getGridTable(){ return (GridTable)getElement(); }

    // Constructeur
    public GridTableFactory(){ init(); }

    // Initialisation
    public void init(){
        super.init();

        // initialisation des attributs
        type  = 0;
        count = 0;
        x = 0;
        y = 0;

        first = true;
        text = new StringBuffer();

        line = 0;
        theLines = new StringBuffer[15]; // on commence avec 15 lignes
        theLines[0] = new StringBuffer();

        // état initial
        STATE = null; // no state for GridTable
    }

    // Accepter une séquence de caractères
    public ParseResult accept(int c) {
        ParseResult result = null;

        text.append((char)c);
        if (mat.matches(text.toString())) {
            result = ParseResult.ACCEPT;
        }else if (mat.matchesPrefix()) {
            result = ParseResult.IN_PROGRESS;
        }else{
            result = ParseResult.FAILED;
        }

        return result;
    }

    // Terminer
    public ParseResult parseEnd(int c){
        return ParseResult.ACCEPT;
    }

    // parse les lignes du tableau qui commencent par '+'
    public ParseResult parseInter(int n) {
        ParseResult result = ParseResult.IN_PROGRESS;
        boolean contentInInter = false;
        System.out.print("Inter   +");

        for (int i = 1; i < theLines[n].length() && result == ParseResult.IN_PROGRESS; i++) {
            char c = theLines[n].charAt(i);
            if ( ! contentInInter ) {
                if ( c == '+' ) {
                    x ++;
                }else if ( c == '-' ) {
                }else if ( c == '=' ) {
                }else{
                    contentInInter = true;
                    System.out.print(c);
                }
            }else{

                if ( c == '+' ) {
                    x ++;
                    contentInInter = false;
                }else if ( c == '|' ) {
                    x ++;
                }else{
                    System.out.print(c);
                }
            }
        }
        System.out.println();
        return result;
    }

    // parse les lignes du tableau qui commencent par '|'
    public ParseResult parseContent(int n) {
        ParseResult result = ParseResult.IN_PROGRESS;
        System.out.print("Content |");

        for (int i = 0; i < theLines[n].length() && result == ParseResult.IN_PROGRESS; i++) {
            char c = theLines[n].charAt(i);
            if ( c == '|' ) {
                x ++;
            }else{
                System.out.print(c);
            }
        }
        System.out.println();
        return result;
    }

    // parse les lignes du tableau
    public ParseResult parseLines(){
        ParseResult result = ParseResult.IN_PROGRESS;

        // on a déjà le nombre maximum de lignes du tableau : line
        // ainsi que le nombre maximum de colonnes du tableau : theLines[0].length() / 2;
        table = new StringBuffer[theLines[0].length() / 2][line];

        for (int i = 0; i < line && result == ParseResult.IN_PROGRESS; i++) {

            if (theLines[i].charAt(0) == '+') {
                x = 0;
               // result = parseInter(i);
                y ++;
            }else if (theLines[i].charAt(0) == '|') {
                x = 0;
               // result = parseContent(i);
            }else{
                result = ParseResult.FAILED.setError(" Attendu | ou + , eu '"+theLines[i].charAt(0)+"'");
            }

        }

        if (result == ParseResult.IN_PROGRESS) {
            result = ParseResult.FINISHED;
        }

        return result;
    }


    /**
    * Retourne IN_PROGRESS, FAILED ou ACCEPT
    * {@link getParseResult}.
    */
    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        if ((char)c == '\n' && first) {
            result = parseLines();
        }else if ((char)c == '\n') {
            line++;
            if (line >= theLines.length) {
                StringBuffer[] tmp = new StringBuffer[theLines.length + 10];
                System.arraycopy(theLines,0,tmp,0,theLines.length);
                theLines = tmp;
            }
            theLines[line] = new StringBuffer();
            first = true;
        }else{
            theLines[line].append((char)c);
            first = false;
        }

        return result;
    }

} // GridTableFactory


