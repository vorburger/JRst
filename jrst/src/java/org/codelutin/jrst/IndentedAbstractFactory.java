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
/*
 * IndentedAbstractFactory.java
 *
 * Created: 28 Juin 2004
 *
 * @author Bucas <bucas@codelutin.com>
 * Copyright Code Lutin
 *
 */
package org.codelutin.jrst;

import jregex.Pattern;
import jregex.Matcher;

public abstract class IndentedAbstractFactory extends AbstractFactory {// IndentedAbstractFactory

    /** Constantes **/

    /* pour l'indentation */
    final static Object READING_HEAD  = new Object(); // lecture de la tete
    final static Object INDENT_COUNT  = new Object(); // compte le nombre d'indentation
    final static Object READING_BODY  = new Object(); // lecture du corps
    final static Object VERIFY_INDENT = new Object(); // on essayes de retrouver la même indentation
    final static Object FINISHED      = new Object(); // état final

    /* acceptation par rapport à une expression régulière */
    final static Object FIRST_LINE    = new Object();
    final static Object INDENT_READ   = new Object();
    final static Object END_ACCEPT    = new Object();

    /** Attributs **/

    // paramètre de la classe
    String headRegExpr    = null;  // expression régulière pour l'acceptance de la factory
    Pattern headRE        = null;  // jregex Pattern for headRegExpr
    Matcher headREMatcher = null;  // jregex Matcher for headRegExpr

    boolean unique      = false; // est ce que l'élément est unique ou bien forme une liste
    boolean oneLiner    = false; // est ce que l'élément peut être sur une seule ligne
    boolean noEndHead   = false; // est ce qu'il faut envoyer \n à parseHead pour identifier la tete


    Object INDENT_STATE = null;  // état dans le mécanisme de gestion de l'indentation
    Object ACCEPT_STATE = null;  // état dans le mécanisme de gestion de l'acceptance par expression régulière
    Object SUB_STATE    = null;  // l'état dans la tête ou le corps

    int indentRead      =  0;    // indentation en cours de lecture
    int indentLength    = -1;    // indentation de base trouvée avec INDENT_SEARCH

    int lastc           = -1;    // caractère précédent
    int lastlastc       = -1;    // caractère précédent le caractère précédent

    protected StringBuffer text   = null;

    /** Méthodes **/

    // abstraites  !
    abstract protected ParseResult parseHead(int c);

    // Initialisation
    protected void init(){
        super.init();
        lastc        = -1;
        lastlastc    = -1;
        indentRead   =  0;
        indentLength = -1;
        text = new StringBuffer();
        INDENT_STATE = READING_HEAD;
        ACCEPT_STATE = FIRST_LINE;

        if (headRegExpr != null){
            if (oneLiner) {
                headRE = new Pattern(headRegExpr);
            }else{
                headRE = new Pattern(headRegExpr+".*(\\n+[ ]+)");
            }
            headREMatcher = headRE.matcher();
        }
    }

    // Accept ce qui arrive ?
    public ParseResult accept(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        if (headRegExpr != null){
            result = headRegExprAccept(c);
        }else{
            result = parse(c);
            if (result == ParseResult.FINISHED){
                result = ParseResult.ACCEPT;
            }
        }

        if (result == ParseResult.FAILED) {
        //    System.out.print(result.getError());
        }

        return result;
    }

    // Demande à se terminer
    public ParseResult parseEnd(int c){
        ParseResult result = endChild(c);
        if (result == ParseResult.ACCEPT)
            result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount-1);
        return result;
    }

    // Demande au fils de se terminer
    public ParseResult endChild(int c) {
        ParseResult result = ParseResult.ACCEPT;
        if (currentChild != null)
            result = currentChild.parseEnd(c);

        CHILD_STATE = null;
        buffer.delete(0,buffer.length());

        return result;
    }

    // Acceptancion par rapport à une Expression Booléenne
    // une chose est sûre : les entêtes sont sur une seule ligne
    // mais on peut avoir besoin de l'indentation pour identifier
    // certains d'entres eux
    public ParseResult headRegExprAccept(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        //System.out.print("\033[00;33m"+(char)c+"\033[00m");//JAUNE
        //System.out.print("\033[00;36m"+"#"+"\033[00m"); // CYAN


        if(ACCEPT_STATE == FIRST_LINE) {
            text.append((char)c);
            if (headREMatcher.matches(text.toString())){
                result = ParseResult.ACCEPT;
            }else if (! headREMatcher.matchesPrefix()){
                result = ParseResult.FAILED;
            }else if ((char)c == '\n') {
                if ( oneLiner ) {
                    ACCEPT_STATE = END_ACCEPT;
                }else{
                    ACCEPT_STATE = INDENT_READ;
                }
            }

            // pour les non oneLiner il faut confirmer
            //  en utilisant l'indentation
        }else if (ACCEPT_STATE == INDENT_READ) {
            if (((char)c == '\n')||((char)c == ' ')) {
                text.append((char)c);
            }else{
                ACCEPT_STATE = END_ACCEPT;
            }
        }

        if (ACCEPT_STATE == END_ACCEPT) {

            if ( headREMatcher.matches(text.toString())){
                result = ParseResult.ACCEPT;
            }else{
                result = ParseResult.FAILED;
            }

            ACCEPT_STATE = FIRST_LINE;
        }

        if (result == ParseResult.ACCEPT){
            //System.out.print("\033[01;32m["+Name()+"]\033[00m ");
            //+"("+headRegExpr+")");
        }else if (result == ParseResult.FAILED){
            //System.out.print("\033[01;31m["+Name()+"]\033[00m ");
        }else if (result == ParseResult.IN_PROGRESS){
            //System.out.print("\033[00;35m["+Name()+"]\033[00m ");
        }

        return result;
    }

    // Parse Body par défaut
    public ParseResult parseBody(int c) {
        //System.out.print("\033[00;37m"+(char)c+"\033[00m");
        return delegate(c);
    }

    // Parse caractère après caractère
    public ParseResult parse(int c) {
        if(INDENT_STATE == FINISHED)
            throw new IllegalStateException("Parsing is finished");

        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        //** Gestion de l'indentation
        if (INDENT_STATE == INDENT_COUNT || INDENT_STATE == VERIFY_INDENT) {
            if ((char)c == ' ') {
                indentRead++;
                if (indentLength != -1 && indentRead > indentLength){
                    INDENT_STATE = READING_BODY;
                }

            }else if ((char)c == '\n') {
                if ((char)lastc == '\n') {
                    if ((char)lastlastc == '\n') {
                        result = endChild(c);
                    }
                }
                indentRead = 0;
                result = delegate(c);
            }else{ // on trouve un caractère qui n'est pas un espace
                if (INDENT_STATE == INDENT_COUNT){
                    indentLength = indentRead;
                    INDENT_STATE = READING_BODY;
                    if (indentRead == 0){ // ONE_LINER
                        if (unique) {
                            INDENT_STATE = FINISHED;
                        }else{
                            INDENT_STATE = READING_HEAD;
                            result = endChild(c);
                            indentLength = -1;
                        }
                    }
                    /*if (oneLiner) {
                        result = endChild(c);
                    }*/
                }
                if (INDENT_STATE == VERIFY_INDENT) {
                    INDENT_STATE = READING_BODY;
                    if (indentRead < indentLength || indentRead == 0){
                        // indentation terminée, le bloc est à fermer
                        if (unique) {
                            INDENT_STATE = FINISHED;
                        }else{
                            INDENT_STATE = READING_HEAD;
                            result = endChild(c);
                            indentLength = -1;
                        }
                        //result = endChild(c);
                    }
                }
            }
        }

        //** Gestion du contenu
        if (INDENT_STATE == READING_BODY || INDENT_STATE == READING_HEAD) { // le corps
            if ((char)c == '\n') {
                indentRead = 0;

                // lorsque la fin de l'entete n'est pas identifiable
                // il faut envoyer le code '\n'
                if ( noEndHead && (INDENT_STATE == READING_HEAD))
                    result = parseHead(c); // est ce bien ??


                result = delegate(c);
                if (indentLength == -1) {
                    INDENT_STATE = INDENT_COUNT;
                }else{
                    INDENT_STATE = VERIFY_INDENT;
                }
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

}// IndentedAbstractFactory

