/*##%
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
 *##%*/
/*
 * RootFactory.java
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

/**
 * TODO Description of the Class
 *
 *@author    poussin
 *@created   7 octobre 2003
 */
public class RootFactory implements ElementFactory {// RootFactory

    /** TODO Description of the Field */
    protected ArrayList childs = new ArrayList();
    protected ArrayList currentChilds = null;
    /** TODO Description of the Field */
    protected Element element = null;

    ParseResult parseResult = null;

    public void init(){
        currentChilds = null;
        element = createElement();
        parseResult = PARSE_IN_PROGRESS.create();
    }

    /**
     * TODO Description of the Method
     *
     *@param c  TODO Description of the Parameter
     *@return   TODO Description of the Return Value
     */
    public ParseResult accept(int c) {
        // on passe les lignes blanches entre les éléments
        if(currentChilds == null && (char)c == '\n'){
            return parseResult;
        }

        if (currentChilds == null) {
            currentChilds = new ArrayList();
            for (int i=0; i<childs.size(); i++) {
                ElementFactory factory = (ElementFactory)childs.get(i);
                factory.init();
                currentChilds.add(factory);
            }
        }

        String message = "";

        // pour chaque fils enregistre on regarde s'il accept le caractere
        // suivant. Si ce n'est pas le cas, alors on le retire
        for (int i = currentChilds.size()-1; 0 <= i; i--) {
            ElementFactory factory = (ElementFactory)currentChilds.get(i);
            ParseResult result = factory.accept(c);
            if (result.equals(PARSE_FAILED)) {
                currentChilds.remove(i);
                message += result.text + "\n";
            }else if(result.equals(PARSE_FINISHED)){
                element.addChild(factory.getElement());
                currentChilds = null;
                break;
            }
        }

        // si on a plus d'enfant et qu'aucun n'a parse correctement le texte
        if(currentChilds != null && currentChilds.size() == 0){
            if(!"".equals(message))
                System.out.println("Error: \n"+ message);
        }

        return parseResult;
    }

    /**
     * TODO Adds a feature to the Child attribute of the RootFactory object
     *
     *@param child  TODO The feature to be added to the Child attribute
     */
    public ElementFactory addChild(ElementFactory child) {
        childs.add(child);
        return this;
    }

    /**
     * TODO Gets the element attribute of the RootFactory object
     *
     *@return   TODO The element value
     */
    public Element getElement() {
        return element;
    }

    protected Element createElement(){
        return new RootElement();
    }
}// RootFactory

