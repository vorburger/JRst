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
 * EnumListFactory.java
 *
 * Created: 25 juin 2004
 *
 * @author Bucas <bucas@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class EnumListFactory extends IndentedAbstractFactory { // EnumListFactory


    /** Constantes **/

    static final Object ENUM_SEQ_BEGIN = new Object();
    static final Object ENUM_SEQ_SEPARATOR = new Object();
    static final Object AFTER_ENUM = new Object();
    static final Object ENUM_SEQ_NEXT = new Object();

    /** Attributs **/

    boolean firstChar = true;
    StringBuffer text = null;

    /** Méthodes **/

    protected AbstractFactory factoryNew(){ return  new EnumListFactory(); }
    protected Element elementNew(){ return new EnumList(); }
    protected EnumList getEnumList(){ return (EnumList)getElement(); }

    // Constructeur
    public EnumListFactory() { init(); }

    // Initialisation
    protected void init(){
        firstChar   = true;
        oneLiner    = true;
        text        = new StringBuffer();
        SUB_STATE   = ENUM_SEQ_BEGIN;
        headRegExpr = "\\(?([0-9]{,5}|[a-z]{,5}|[A-Z]{,5}|[ivxdcm]{,5}|[IVXDCM]{,5})(\\.|\\)) ";
        super.init();
    }

    //
    // TODO !
    //
    // fonction pour parser la valeur de l'énumérateur
    /*

    si 0-9 : atoi (texte)
    si a-z : vérifier i)
    si A-Z : vérifier I) et plus si affinités MMMMCMXCIX (4999)

    */
    // Vérifier que les valeurs se suivent bien


    private ParseResult parseValue(int c, EnumList myList) {
        ParseResult result = ParseResult.IN_PROGRESS;
        boolean took = true;

        if (c >= (int)'0' && c <= (int)'9') { // numeric
            if (myList.getType() == null) {
                myList.setType(EnumList.KIND_NUM);
            }else if (myList.getType() != EnumList.KIND_NUM) {
                //result = ParseResult.FAILED.setError("Mixing numeric and alpha are not permit in EnumList");
                result = ParseResult.FINISHED;
            }
        }else if (c >= (int)'a' && c <= (int)'z') { // alpha lower_case
            if (myList.getType() == null) {
                myList.setType(EnumList.KIND_ALPHA);
            }else if (myList.getType() != EnumList.KIND_ALPHA) {
                //result = ParseResult.FAILED.setError("Mixing numeric and alpha or alpha and alpha uppercase are not permit in EnumList");
                result = ParseResult.FINISHED;
            }
        }else if (c >= (int)'A' && c <= (int)'Z') {     // alpha upper_case
            if (myList.getType() == null) {
                myList.setType(EnumList.KIND_ALPHA_MAJ);
            }else if (myList.getType() != EnumList.KIND_ALPHA_MAJ) {
                //result = ParseResult.FAILED.setError("Mixing numeric and alpha or alpha and alpha uppercase are not permit in EnumList");
                result = ParseResult.FINISHED;
            }
        }else
            took = false;

        if ( result == ParseResult.FAILED )
            took = false;

        if ( took )
            result = ParseResult.ACCEPT;

        return result;
    }

    public ParseResult parseHead(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;

        EnumList myList = getEnumList();

        //System.out.print((char)c);

        if(SUB_STATE == ENUM_SEQ_BEGIN || SUB_STATE == ENUM_SEQ_NEXT){
            if (firstChar && (char)c == '\\') {
                result = ParseResult.FAILED.setError("First character is escape :: not an EnumList");
            }else if(firstChar && (char)c == '(') {
                if (myList.getFormateur() != null) {
                    result = ParseResult.FAILED.setError("Only one '(' allowed for EnumList");
                }else{
                    myList.setFormateur(EnumList.KIND_PARA_PARA);
                }
            }else{
                result = parseValue(c, myList);
                if ( result == ParseResult.ACCEPT ) {
                    result = ParseResult.IN_PROGRESS;
                    // System.out.print("Val["+(char)c+']');
                    text.append((char) c);
                }else if (result == ParseResult.IN_PROGRESS) {
                    if (text.length() == 0) {
                        result = ParseResult.FAILED.setError("no enum value");
                    }else{
                        SUB_STATE = ENUM_SEQ_SEPARATOR;
                        firstChar = true;

                        // TODO: ici faire un appel de fonction pour parser la valeur du texte

                        if (SUB_STATE == ENUM_SEQ_NEXT) {
                            // TODO: ici il faut vérifier la valeur
                            // pour savoir si elle suit bien
                            // les valeurs précédentes
                        }

                        Term t = new Term(text);
                        getElement().addChild(t);
                        text.delete(0, text.length());
                    }
                }
            }
            if (SUB_STATE != ENUM_SEQ_SEPARATOR)
                firstChar = false;
        }

        if (SUB_STATE == ENUM_SEQ_SEPARATOR) {
            boolean ok = false;
            if ((char)c == '.') {       // DOT Formating
                if (myList.getFormateur() != null && myList.getFormateur() != EnumList.KIND_DOT){
                    result = ParseResult.FAILED.setError("Dot is illogic here because of the '(' before.");
                }else{
                    myList.setFormateur(EnumList.KIND_DOT);
                    //System.out.println("DOT");
                    ok = true;
                }
            }else if ((char)c == ')' ) { // parenthesis formating
                if (text.length() == 0) {
                    result = ParseResult.FAILED.setError("Dot needs text before");
                }else{
                    if (myList.getFormateur() == null) {
                        myList.setFormateur(EnumList.KIND_PARA);
                        //System.out.println("PARA");
                    }
                    ok = true;
                }
            }else {
                result = ParseResult.FAILED.setError("Invalid Character");
            }
            if ( ok ) {
                SUB_STATE = AFTER_ENUM;
                firstChar = true;
            }else{
                firstChar = false;
            }
        }else if (SUB_STATE == AFTER_ENUM) {
            if (firstChar && (char)c != ' ') {
                result = ParseResult.FAILED.setError("Character ' ' missing");
            }else if ((firstChar && (char)c == ' ')) {
                firstChar = false;
                INDENT_STATE = READING_BODY;
                SUB_STATE = ENUM_SEQ_NEXT;
            }else{
                INDENT_STATE = READING_BODY;
                SUB_STATE = ENUM_SEQ_NEXT;
                result = delegate(c);
            }
        }


        if (result == ParseResult.FAILED) {
            System.out.println(result.getError());
            myList.setType(null);
            myList.setFormateur(null);
        }


        return result;
    }

} // FieldListFactory

