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
import jregex.Pattern;
import jregex.Matcher;


/**
 * TODO Description of the Class
 *
 *@author    poussin
 *@created   7 octobre 2003
 *
 *  mise à jour le 18 juin 2004
 */

public abstract class AbstractFactory implements ElementFactory {// AbstractFactory

    /** --- Constantes --- */
    // type OR ou AND sur les fils
    final static protected Object FUNCTION_OR = new Object();
    final static protected Object FUNCTION_AND = new Object();

    // la cardinalite de la factory : type "1", "?", "+", "*"
    final static protected Object CARDINALITE_SIMPLE = new Object(); // une fois
    final static protected Object CARDINALITE_ZERO_UN = new Object(); // zéro ou un
    final static protected Object CARDINALITE_ETOILE = new Object(); // zéro ou plus
    final static protected Object CARDINALITE_PLUS = new Object(); // un ou plus

    // Etat du fils vu du père
    final static Object SEARCH_CHILD = new Object();
    final static Object PARSE = new Object();

    /** --- Attibuts --- **/
    /* Etat courant de la Factory pour leur automate */
    protected Object STATE = null;

    /* Gestion des enfants de la Factory */
    // La liste de tous les types elements acceptable
    protected ArrayList childs = new ArrayList();
    // l'enfant courant
    protected ElementFactory currentChild = null;
    // indexe de départ pour le prochain enfant
    protected int currentChildIndex = -1;
    // Etat de l'enfant
    protected Object CHILD_STATE = null;

    /* le buffer des caractères à parser */
    protected StringBuffer buffer = null;
    // nombre de caractères consommés dans le buffer
    int consumedCharCount = 0;

    /* L'élément de la Factory */
    Element element = null;

    /* comportement de la Factory */
    // les enfants sont à suivre ou tous possibles en même temps
    protected Object factoryAND_OR = FUNCTION_AND; // AND par défaut
    // nombre de fois qu'il faut répèter la Factory
    protected Object factoryCardinalite = CARDINALITE_SIMPLE;



    /** --- Méthodes abstraites --- **/

    abstract protected AbstractFactory factoryNew();
    abstract protected Element elementNew();
    abstract public ParseResult accept(int c);
    abstract public ParseResult parseEnd(int c);
    abstract public ParseResult parse(int c);

    /**
     **   Déclaration des méthodes
     **/

    // Constructeur
    public AbstractFactory(){ init(); }

    /**
    * Appele par la methode create, contient des initialisations, les enfants
    * peuvent surcharger la méthode, mais doivent appeler super.init();
    */
    protected void init(){
        consumedCharCount = 0;
        STATE = null;
        CHILD_STATE = null;
        currentChild = null;
        currentChildIndex = 0;
        element = elementNew();
        buffer = new StringBuffer();
//        childIndentRead = new StringBuffer();
//        childIndent = -1;
    }

    /**
    * Construit une nouvelle factory du meme type avec les memes enfants
    * la meme cardinalité et la même propriété AND_OR
    */
    public ElementFactory create(Object cardinal){
        AbstractFactory result = factoryNew();
        result.childs = childs;
        result.factoryAND_OR = factoryAND_OR;
        result.factoryCardinalite = cardinal;
        result.init();
        return result;
    }

    public ElementFactory create() { return create(factoryCardinalite); }

    /**  Accesseurs **/
    public Element      getElement() { return element; }
    public StringBuffer getBuffer()  { return buffer;  }

    // renvoie une nouvelle factory avec une cardinalité simple (1)
    public ElementFactory getSimple() { return create( CARDINALITE_SIMPLE );  }
    // renvoie une nouvelle factory avec une cardinalité zéro ou un (?)
    public ElementFactory getZero_Un(){ return create( CARDINALITE_ZERO_UN ); }
    // renvoie une nouvelle factory avec une cardinalité étoile (*)
    public ElementFactory getEtoile() { return create( CARDINALITE_ETOILE );  }
    // renvoie une nouvelle factory avec une cardinalité plus (+)
    public ElementFactory getPlus()   { return create( CARDINALITE_PLUS );    }

    // pour savoir si un Element est nécessaire par rapport à sa
    // cardinalité
    public boolean isNecessaire() {
        return (factoryCardinalite == CARDINALITE_PLUS ||
                factoryCardinalite == CARDINALITE_SIMPLE);
    }
    // pour savoir si un Element est multiple par rapport à sa
    // cardinalité
    public boolean isMultiple() {
        return (factoryCardinalite == CARDINALITE_PLUS ||
                factoryCardinalite == CARDINALITE_ETOILE);
    }

    /** Modififieurs OR et AND **/
    // passe le fonctionnement de la factory en OR
    public void setFunctionOR()  { factoryAND_OR = FUNCTION_OR;  }
    // passe le fonctionnement de la factory en AND
    // (ne devrait pas bcp servir normallement car c le mode par défaut)
    public void setFunctionAND() { factoryAND_OR = FUNCTION_AND; }




    /**
     **    Gestion des enfants
     **/


    /**
    * Recherche un enfant qui conviendrait pour les caracteres que l'on
    * souhaite parser.
    * il s'agit d'un parcours de liste donc l'ordre d'insertion est important
    */
    protected ParseResult searchChild(){

        ParseResult result = ParseResult.IN_PROGRESS;

        // nombre de fils qui vont être parcourus pendant la recherche
        int limit = 0;

        if (factoryAND_OR == FUNCTION_OR) { // FUNCTION_OR
            limit = childs.size();      // on parcours tous les fils dans le cas du OR
        }else{ // FUNCTION AND
            limit = currentChildIndex;  // parcours du prochain fils dans le cas du AND
            boolean fini = false;       // ainsi que tous les fils zéro ki suivent
            while (limit < childs.size() && ! fini) {
                AbstractFactory factory = (AbstractFactory)childs.get(limit);
                if ( factory.isNecessaire() ) {
                    fini = true;
                }else{
                    limit++; // dans le cas de '*' ou de '?'
                }
            }
            if (limit < childs.size()) {
                limit++; // pour le "c<limit"
            }
        }

        // recherche du fils
        currentChild = null;
        for(int c=currentChildIndex; c<limit && currentChild == null; c++){
            ElementFactory factory = (ElementFactory)childs.get(c);
            factory = factory.create();
            for(int i=0; i<buffer.length(); i++) {
                result = factory.accept((int)buffer.charAt(i));
                if (result == ParseResult.ACCEPT) {
                    currentChild = factory.create();
                    if (factoryAND_OR == FUNCTION_OR) { // FUNCTION_OR
                        // on recommence à zéro pour la prochaine recherche
                        currentChildIndex = 0;
                    }else{
                        if ( !((AbstractFactory)currentChild).isNecessaire() ){
                            // on passe au fils suivant
                            currentChildIndex++;
                        }
                    }
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

    /**
     *  le petit nom véritable de la factory
     */

    // pour récuperer le nom des factories
    Pattern factoryName = new Pattern("org\\.codelutin\\.jrst\\.(.*)Factory");

    public String Name() {

        Matcher myM = factoryName.matcher(getClass().getName());
        myM.matches();
        return myM.group(1);
    }

    // identification
    public String identify() {
        return Integer.toHexString(hashCode());
    }


}// AbstractFactory

