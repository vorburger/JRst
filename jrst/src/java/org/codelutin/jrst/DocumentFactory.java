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
 * DocumentFactory.java
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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class DocumentFactory extends AbstractFactory { // DocumentFactory

    protected AbstractFactory factoryNew(){  return new DocumentFactory();  }
    protected Element elementNew(){  return new Document();  }
    public ParseResult accept(int c){  return ParseResult.ACCEPT;  }

    int consumed = 0;

    public ParseResult parse(int c){
        ParseResult result = ParseResult.IN_PROGRESS;
        consumed++;

        if (Parser.DEBUG != null)
            System.out.print("\033[00;37m"+(char)c+"\033[00m");

        result = delegate(c);
        if (result == ParseResult.FAILED)
            result.setConsumedCharCount(consumed);
        return result;
    }

    /**
     *   Récursif à travers le modèle pour chercher le titre du document
     *   qui est en réalité le premier titre trouvé !
     *   @param e l'élément dans lequel chercher le titre (lui ou ses fils)
     */

    public Title ParcoursTitle(Element e) {
        if (e instanceof Title) {
            return (Title)e;
        }else{
            for(Iterator i=e.getChilds().iterator(); i.hasNext();){
                Title result = ParcoursTitle((Element)i.next());
                if ( result != null ) {
                    return result;
                }
            }
        }
        return null;
    }

    public FieldList ChercheBibliographic(Element e) {
        if (e instanceof FieldList) {
            for(int i=0; i<e.getChilds().size(); i++){
                Object child = e.getChilds().get(i);
                if(child instanceof Term){
                    if ("author".equals(((Term)child).getText().toLowerCase())) {
                        return (FieldList)e;
                    }
                }
            }
        }else{
            for(Iterator i=e.getChilds().iterator(); i.hasNext();){
                FieldList result = ChercheBibliographic((Element)i.next());
                if ( result != null ) {
                    return result;
                }
            }
        }
        return null;
    }

    public List getContents(Element e) {
        if (e instanceof Title) {
            ArrayList result = new ArrayList();
            result.add(e);
            return result;
        }else{
            ArrayList resultat = null;
            for(Iterator i=e.getChilds().iterator(); i.hasNext();){
                List result = getContents((Element)i.next());
                if ( result != null ) {
                    if (resultat == null) {
                        resultat = (ArrayList)result;
                    }else{
                        resultat.addAll(result);
                    }
                }
            }
            return resultat;
        }
    }

    // Récolte les cibles potientielles des liens intégrés au texte
    public List getHyperlinks(Element e) {
        if (e instanceof Title) { // les titres sont par défaut des liens potentiels
            ArrayList result = new ArrayList();
            result.add(e);
            return result;
        }else if (e instanceof Hyperlink) {
            ArrayList result = new ArrayList();
            result.add(e);
            return result;
        }else{
            ArrayList resultat = null;
            for(Iterator i=e.getChilds().iterator(); i.hasNext();){
                List result = getHyperlinks((Element)i.next());
                if ( result != null ) {
                    if (resultat == null) {
                        resultat = (ArrayList)result;
                    }else{
                        resultat.addAll(result);
                    }
                }
            }
            return resultat;
        }
    }



    public ParseResult parseEnd(int c){
        Document myDoc = (Document)getElement();

        Title t = ParcoursTitle(myDoc);
        if (t != null) {
            myDoc.setTitle(t);
        }else{
            //System.out.println("No Title Found!");
        }

        FieldList f = ChercheBibliographic(myDoc);
        if (f != null) {
            myDoc.setBibliographic(f);
        }else{
            //System.out.println("No bibliographic data");
        }

        List e = getContents(myDoc);
        if (e != null) {
            myDoc.setContents(e);
        }else{
            System.err.println("Problem with contents");
        }

        List h = getHyperlinks(myDoc);
        if (h != null) {
            myDoc.setHyperlinks(h);
        }else{
            System.err.println("Problem with hyperlinks");
        }

        return ParseResult.ACCEPT;
    }
} // DocumentFactory

