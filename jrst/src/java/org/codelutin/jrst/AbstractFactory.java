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
 * AbstractFactory.java
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
public abstract class AbstractFactory implements ElementFactory {// AbstractFactory

    Object SEARCH_CHILD = new Object();
    Object PARSE = new Object();

    /** La liste potentiel de tous les types elements acceptable */
    protected ArrayList childs = new ArrayList();

    protected int currentChildIndex = -1;
    protected ElementFactory currentChild = null;
    protected StringBuffer buffer = null;

    /** Util a tous les enfants pour leur automate */
    Object STATE = null;
    Object CHILD_STATE = null;
    int consumedCharCount = 0;

    Element element = null;

    abstract protected AbstractFactory factoryNew();
    abstract protected Element elementNew();
    abstract public ParseResult accept(int c);
    abstract public ParseResult parse(int c);

    public StringBuffer getBuffer(){
        return buffer;
    }

    public AbstractFactory(){
        init();
    }

    /**
    * Construit une nouvelle factory du meme type avec les memes enfants
    */
    public ElementFactory create(){
        AbstractFactory result = factoryNew();
        result.childs = childs;
        result.init();
        return result;
    }

    public Element getElement(){
        return element;
    }

    /**
    * Appele par la methode create, contient des initialisations, les enfants
    * peuvent surcharger la méthode, mais doivent appeler super.init();
    */
    protected void init(){
        consumedCharCount = 0;
        STATE = null;
        CHILD_STATE = null;
        currentChildIndex = 0;
        currentChild = null;
        element = elementNew();
        buffer = new StringBuffer();
        childIndentRead = new StringBuffer();
        childIndent = -1;
    }


    /** l'indentation requise pour les enfants */
    int childIndent = -1;
    int lastChildIndentRead = 0;
    StringBuffer childIndentRead = null;
    /**
    * se positionne juste au debut du texte pour le fils.
    * c-a-d que l'on mange les caracteres blanc.
    */
    protected ParseResult searchChildText(int c){
        ParseResult result = ParseResult.IN_PROGRESS;
        if((char)c == ' ' || (char)c == '\n'){
            childIndentRead.append((char)c);
            if((char)c == '\n'){ // pour fermer les enfants si besoin
                delegate(c);
            }
        }else{
            lastChildIndentRead = 0;
            for(int i=0; i<childIndentRead.length(); i++){
                if(childIndentRead.charAt(i) == '\n'){
                    lastChildIndentRead = 0;
                }else{
                    lastChildIndentRead++;
                }
            }
            result = ParseResult.FINISHED.setConsumedCharCount(childIndentRead.length());
            if(childIndent == -1 && lastChildIndentRead != 0){
                childIndent = lastChildIndentRead;
            }else if(childIndent != lastChildIndentRead){
                result = ParseResult.FAILED.setError("Bad indentation found: " + lastChildIndentRead+" wait: " + childIndent);
            }
            childIndentRead.delete(0, childIndentRead.length());
        }
        return result;
    }

    /**
    * Recherche un enfant qui conviendrait pour les caracteres que l'on
    * souhaite parser.
    */
    protected ParseResult searchChild(){
        ParseResult result = ParseResult.IN_PROGRESS;
        currentChild = null;
        for(int c=currentChildIndex; c<childs.size() && currentChild == null; c++){
            ElementFactory factory = (ElementFactory)childs.get(c);
            factory = factory.create();
            for(int i=0; i<buffer.length(); i++){
                result = factory.accept((int)buffer.charAt(i));
                if(result == ParseResult.ACCEPT){
                    currentChild = factory.create();
                    currentChildIndex = 0;
                    getElement().addChild(currentChild.getElement());
                    CHILD_STATE = PARSE;
                    break;
                }else if(result == ParseResult.FAILED){
                    currentChildIndex++;
                    result.setError("No more child available");
                    break;
                }
            }
            if(result == ParseResult.IN_PROGRESS){
                break;
            }
        }
        return result;
    }

    /**
    * Delegue le parsage a ses fils. Les caractères recu sont ceux pour le
    * fils, c-a-d que les caracteres blanc du debut de ligne ne sont pas
    * envoyé.
    */
    protected ParseResult delegate(int c){
        if (childs.size() == 0){
            return ParseResult.FAILED.setError("No child available");
        }

        ParseResult result = ParseResult.IN_PROGRESS;

        if(CHILD_STATE == null && (char)c != '\n'){
            CHILD_STATE = SEARCH_CHILD;
        }

        if(CHILD_STATE == SEARCH_CHILD){
            buffer.append((char)c);
            result = searchChild();
            if(currentChild != null){
                result = ParseResult.IN_PROGRESS;
                for(int i=0; i<buffer.length() && result == ParseResult.IN_PROGRESS; i++){
                    result = currentChild.parse((int)buffer.charAt(i));
                }
            }
        }else if(CHILD_STATE == PARSE){
            buffer.append((char)c);
            result = currentChild.parse(c);
        }

        if(result == ParseResult.FINISHED){
            CHILD_STATE = null;
            buffer.delete(0, result.getConsumedCharCount());
            while(buffer.length() > 0 && buffer.charAt(0) == '\n'){
                buffer.deleteCharAt(0);
            }
            result = ParseResult.IN_PROGRESS;
        }
        return result;
    }

    /**
     * TODO Adds a feature to the Child attribute of the AbstractFactory object
     *
     *@param child  l'element enfant
     *@param count le nombre de fois que cette element peut-etre trouvé
     */
    public ElementFactory addChild(ElementFactory child) {
        childs.add(child);
        return this;
    }

}// AbstractFactory

