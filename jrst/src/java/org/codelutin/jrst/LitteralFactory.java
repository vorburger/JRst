/* *##%
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
 * LitteralFactory.java
 *
 * bloc litteral dans le texte rST
 *
 * Created: 23 juillet 2004
 *
 */

package org.codelutin.jrst;

public class LitteralFactory extends AbstractFactory { // LitteralFactory

    /** Constantes **/

    /* pour l'indentation */
    final static Object READING_HEAD  = new Object(); // lecture de la tete
    final static Object INDENT_COUNT  = new Object(); // compte le nombre d'indentation
    final static Object READING_BODY  = new Object(); // lecture du corps
    final static Object VERIFY_INDENT = new Object(); // on essayes de retrouver la même indentation
    final static Object FINISHED      = new Object(); // état final


    /** Attributs **/

    Object INDENT_STATE = null;  // état dans le mécanisme de gestion de l'indentation
    int indentRead      =  0;    // indentation en cours de lecture
    int indentLength    = -1;    // indentation de base trouvée avec INDENT_SEARCH

    int lastc           = -1;    // caractère précédent
    int lastlastc       = -1;    // caractère précédent le caractère précédent

    StringBuffer sb     = null;
    int count           = 0;


    /** Méthodes **/

    // Accesseurs et recopieurs
    protected AbstractFactory factoryNew(){ return new LitteralFactory(); }
    protected Element elementNew(){ return new Litteral(); }
    protected Litteral getLitteral(){ return (Litteral)getElement(); }

    // Constructeur
    public LitteralFactory(){ init(); }

    // Initialisation
    public void init(){

        lastc        = -1;
        lastlastc    = -1;
        indentRead   =  0;
        indentLength = -1;
        INDENT_STATE = READING_HEAD;
        count        =  0;
        sb           = new StringBuffer();

        super.init();

    }

    // Accept ce qui arrive ?
    public ParseResult accept(int c) {
        ParseResult result = parseHead(c);

        if (result == ParseResult.FINISHED) {
            if (Parser.DEBUG != null)
                System.out.print("\033[01;32m[Litteral] \033[00m ");

            result = ParseResult.ACCEPT;
        }else{
            if (Parser.DEBUG == Parser.DEBUG_LEVEL2)
                System.out.print("\033[01;31m[Litteral] \033[00m");
        }

        return result;
    }

     // Demande à se terminer
    public ParseResult parseEnd(int c){
        getLitteral().setText(sb.toString());
        return ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
    }


    // Parse Head
    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        //System.out.print("\033[00;33m"+(char)c+"\033[00m");

        if((char)c == '\n' && (char)lastc == ':' && (char)lastlastc == ':') {
            result = ParseResult.FINISHED.setConsumedCharCount(3);
        }

        if ((char)c != ':' && (char) c != '\n')
            result = ParseResult.FAILED.setError("not a Litteral header  != '::\\n' ");

        lastlastc = lastc;
        lastc = c;

        return result;
    }

    // Parse Body
    public ParseResult parseBody(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
//        System.out.print("\033[00;37m"+(char)c+"\033[00m");
        sb.append((char)c);
        return result;
    }


    // Parse caractère après caractère
    public ParseResult parse(int c) {
        if(INDENT_STATE == FINISHED)
            throw new IllegalStateException("Parsing is finished");

        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        //System.out.print("\033[00;36m"+(char) c+"\033[00m"); // CYAN


        //** Gestion de l'indentation si il y en a une
        if (INDENT_STATE == INDENT_COUNT || INDENT_STATE == VERIFY_INDENT) {
            if ((char)c == ' ') {
                indentRead++;
                if (indentLength > 0 && indentRead > indentLength){
                    INDENT_STATE = READING_BODY;
                }
            }else if ((char)c == '\n') {
                indentRead = 0;
                sb.append((char)c);
            }else{ // on trouve un caractère qui n'est pas un espace
                if (INDENT_STATE == INDENT_COUNT){
                    indentLength = indentRead;
                    INDENT_STATE = READING_BODY;
                    if (indentRead == 0){ // Pas d'indentation
                        indentLength = -2;
                    }
                }
                if (INDENT_STATE == VERIFY_INDENT) {
                    INDENT_STATE = READING_BODY;
                    if ((indentRead < indentLength) || (indentRead == 0 && indentLength != -2)) {
                        // indentation terminée, le bloc est à fermer
                        INDENT_STATE = FINISHED;
                    }
                }
            }
        }

        //** Gestion du contenu
        if (INDENT_STATE == READING_BODY || INDENT_STATE == READING_HEAD) { // le corps
            if ((char)c == '\n') {

                if (INDENT_STATE == READING_BODY)
                    result = parseBody(c);
                indentRead = 0;
                if (indentLength == -1)
                    INDENT_STATE = INDENT_COUNT;
                else
                    INDENT_STATE = VERIFY_INDENT;


            }else{
                if (INDENT_STATE == READING_BODY)
                    result = parseBody(c);
                else
                    result = parseHead(c);
            }
        }

        if (INDENT_STATE == FINISHED)
            result = parseEnd(c);

        lastlastc = lastc;
        lastc = c;

        return result;
    }

} // LitteralFactory

