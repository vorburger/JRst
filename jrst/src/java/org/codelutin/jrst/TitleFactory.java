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

import jregex.Pattern;
import jregex.Matcher;

public class TitleFactory extends AbstractFactory { // TitleFactory

    static final String ACCEPTED_CHAR_ALL = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    static final String ACCEPTED_CHAR = "\\*\\+\\-\\:\\=\\_\\~\\`";

    static final Object BEFORE = new Object();
    static final Object IN = new Object();
    static final Object AFTER = new Object();
    static final Object FINISHED = new Object();


    Pattern titlePattern = new Pattern("(["+ACCEPTED_CHAR+"]+\\n)?(.+)\\n(["+ACCEPTED_CHAR+"]+)\\n");
    Matcher titleMatcher = titlePattern.matcher();

    StringBuffer titleText = null;

    int titleMark = -1;
    int countMarkBefore = 0;
    int countMarkAfter = 0;

    protected AbstractFactory factoryNew(){ return new TitleFactory(); }
    protected Element elementNew(){ return new Title(); }
    protected Title getTitle(){ return (Title)getElement(); }

    public void init(){
        super.init();
        titleText = new StringBuffer();
        titleMark = -1;

        countMarkBefore = 0;
        countMarkAfter = 0;

        STATE = BEFORE;
    }

    public ParseResult accept(int c) {

        ParseResult result = ParseResult.IN_PROGRESS; //parse(c);

        titleText.append((char) c);

        if (titleMatcher.matches(titleText.toString())){
/*            for(int i = 1; i != 4; i++)
                System.out.print("\033[00;35m"+titleMatcher.group(i)+"\033[00m");
*/
            if ( ((titleMatcher.group(1) != null) && (titleMatcher.group(1).length() < titleMatcher.group(2).length())) ||
            (titleMatcher.group(3).length() < titleMatcher.group(2).length())) {
                result = ParseResult.FAILED;
            }else{
                result = ParseResult.ACCEPT;
            }

        }else if (! titleMatcher.matchesPrefix()){
            result = ParseResult.FAILED;
        }

        if (Parser.DEBUG != null){
            if(result == ParseResult.ACCEPT){
                System.out.print("\033[01;32m[Title] \033[00m ");
                result = ParseResult.ACCEPT;
            }else if (result == ParseResult.FAILED) {
                if (Parser.DEBUG == Parser.DEBUG_LEVEL2)
                    System.out.print("\033[01;31m[Title] \033[00m");
            }
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
        if (STATE == FINISHED) {
                System.out.print("\033[01;31m TITLE DEJA FINISHED \033[00m ");
                return ParseResult.FINISHED;
        }

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
                    if ((char)c != ' ')
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
                if((countMarkBefore >= titleText.length() || countMarkBefore == 0) && titleText.length() > 0){
                    STATE = AFTER;
                }else{
                    result = ParseResult.FAILED.setError("Title and upperline don't have same length");
                }
            }else if ((char)c == ' ') {
                if (titleText.toString().trim().length() != 0)
                    titleText.append((char)c);
            }else{
                titleText.append((char)c);
            }
        }else if (STATE == AFTER){
            if (titleMark == -1 || titleMark == c){
                titleMark = c;
                countMarkAfter++;
            }
            if (countMarkAfter < titleText.length() &&  (char)c == '\n'){
                result = ParseResult.FAILED.setError("Title and underline don't have same length: "+titleText.length()+ "underline length: "+countMarkAfter);
            }else if((char)c == '\n' && countMarkAfter >= titleText.length()){
                getTitle().setText(titleText.toString());
                // if (getTitle().getText().length() > 0) {
                    getTitle().setTitleMark(titleMark);
                    getTitle().setMarkLength(countMarkAfter);
                    getTitle().setUpperline(countMarkBefore!=0);
                    if (Parser.DEBUG == Parser.DEBUG_LEVEL2)
                        System.out.print("\033[01;35m["+(char)titleMark+" "+countMarkAfter+" "+countMarkBefore+"] \033[00m ");
                    countMarkAfter = 0;
                    countMarkBefore = 0;
                    titleText.delete(0,titleText.length());
                    titleMark = -1;
                    STATE = FINISHED;
                    result = ParseResult.FINISHED.setConsumedCharCount(consumedCharCount);
                //}else
                //    result = ParseResult.FAILED.setError("Bad Title from the swamp");
            }
        }

        return result;
    }

} // TitleFactory

