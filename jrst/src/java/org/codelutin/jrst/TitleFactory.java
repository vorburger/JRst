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
* TitleFactory.java
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

public class TitleFactory extends AbstractFactory { // TitleFactory

    static final String ACCEPTED_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    static final Object BEFORE = new Object();
    static final Object IN = new Object();
    static final Object AFTER = new Object();
    static final Object FINISHED = new Object();

    StringBuffer titleText = null;

    int titleMark = -1;
    int countMarkBefore = 0;
    int countMarkAfter = 0;

    protected AbstractFactory factoryNew(){
        return new TitleFactory();
    }
    protected Element elementNew(){
        return new Title();
    }

    protected Title getTitle(){
        return (Title)getElement();
    }

    public void init(){
        super.init();
        titleText = new StringBuffer();
        titleMark = -1;

        countMarkBefore = 0;
        countMarkAfter = 0;
        STATE = BEFORE;
    }

    public ParseResult accept(int c) {
        ParseResult result = parse(c);
        if(result == ParseResult.FINISHED){
            System.out.print("\033[01;32mTitle \033[00m ");
            result = ParseResult.ACCEPT;
        }else if (result == ParseResult.FAILED) {
            System.out.print("\033[01;31mTitle \033[00m");
        }
        return result;
    }

    public ParseResult parseEnd(int c){
        return ParseResult.ACCEPT;
    }

    /**
    * Retourne true tant que l'objet n'a pas fini de parser son élément.
    * Lorsqu'il retourne false, la factory est capable de savoir si l'élement est convenable ou non, pour cela il faut appeler la méthode {@link getParseResult}.
    */
    public ParseResult parse(int c) {
        ParseResult result = ParseResult.IN_PROGRESS;
        consumedCharCount++;

        if(STATE == BEFORE){
            if((char)c == '\n'){
                if(countMarkBefore != 0){
                    STATE = IN;
                }else{
                    result = ParseResult.FAILED.setError("Cariage return not allowed before title");
                }
            }else if(countMarkBefore == 0){
                if(ACCEPTED_CHAR.indexOf((char)c) == -1 ){
                    titleText.append((char)c);
                    STATE = IN;
                }else{
                    titleMark = c;
                    countMarkBefore++;
                }
            }else{
                if (titleMark == c){
                    countMarkBefore++;
                }else{
                    result = ParseResult.FAILED.setError("Bad char in upperline title have: "+(char)c +" wait: "+(char)titleMark);
                }
            }
        }else if(STATE == IN){
            if((char)c == '\n'){
                if(countMarkBefore >= titleText.length() || countMarkBefore == 0){
                    STATE = AFTER;
                }else{
                    result = ParseResult.FAILED.setError("Title and upperline don't have same length");
                }
            }else{
                titleText.append((char)c);
            }
        }else{
            if (titleMark == -1 || titleMark == c){
                titleMark = c;
                countMarkAfter++;
            }
            if (countMarkAfter < titleText.length() && (char)c == '\n'){
                result = ParseResult.FAILED.setError("Title and underline don't have same length: "+titleText.length()+ "underline length: "+countMarkAfter);
            }else if((char)c == '\n' && countMarkAfter >= titleText.length()){
                getTitle().setText(titleText.toString());
                getTitle().setTitleMark(titleMark);
                getTitle().setMarkLength(countMarkAfter);
                getTitle().setUpperline(countMarkBefore!=0);
                STATE = FINISHED;
                result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount);
            }
        }

        return result;
    }

} // TitleFactory

