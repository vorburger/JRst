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
 * ##%*/

/* *
 * FactoryParser.java
 *
 * Created: 19 juillet 2004
 *
 */

package org.codelutin.jrst;

import java.util.HashMap;
import org.codelutin.xml.XMLObjectParser;
import org.codelutin.util.ResourceNotFoundException;

 public class FactoryParser {

     /** Constantes **/

     /** Attributs **/

     // la table des références
     static HashMap references = new HashMap();

     // l'instance
     ElementFactory instanceFactory = null;

     // la classe
     Class classe = null;

     // cardinalité
     Object cardinality = AbstractFactory.CARDINALITE_SIMPLE;

     // nom
     String nom = null;

     /** Méthodes **/

     public FactoryParser() {     }

     /**
     * @param filename fichier XML a lire
     */
     public FactoryParser( String filename ) throws ResourceNotFoundException {
         XMLObjectParser xOP = new XMLObjectParser(filename);
         xOP.setPrefix("par");
         xOP.parseRoot(this);
     }

     // -- -- - - Elements XML

     public void parsetFactory(FactoryParser child) {
         getInstance().addChild(child.getInstance());
     }


     // -- -- - - Attributs XML

     public void parsetClass(Class myclass) throws Exception {
         classe = myclass;
         try{
             instanceFactory =(ElementFactory)myclass.newInstance();
         }catch(ClassCastException eee){
             System.err.println("Error " + myclass.getName() + " don't implement ElementFactory");
             throw eee;
         }
     }

     public void parsetRef(String ref) {
         FactoryParser fPref = (FactoryParser)references.get(ref);
         instanceFactory = fPref.getInstance();
     }

     public void parsetName(String name) {
         nom = name;
         references.put(name, this);
         if (instanceFactory instanceof AndElementFactory) {
             ((AndElementFactory)instanceFactory).setName(nom);
         }
         if (instanceFactory instanceof OrElementFactory) {
             ((OrElementFactory)instanceFactory).setName(nom);
         }
     }

     public void parsetCardinality(String cardinality) {
         if ("1".equals(cardinality)) {
             this.cardinality = AbstractFactory.CARDINALITE_SIMPLE;
         }else if ("0..1".equals(cardinality)) {
             this.cardinality = AbstractFactory.CARDINALITE_ZERO_UN;
         }else if ("1..*".equals(cardinality) || "1..n".equals(cardinality)) {
             this.cardinality = AbstractFactory.CARDINALITE_PLUS;
         }else if ("*".equals(cardinality) || "n".equals(cardinality)) {
             this.cardinality = AbstractFactory.CARDINALITE_ETOILE;
         }
     }

     // -- -- - - Récupération

     public ElementFactory getInstance() {
         if (cardinality == AbstractFactory.CARDINALITE_ZERO_UN)
             return instanceFactory.getZero_Un();

         if (cardinality == AbstractFactory.CARDINALITE_PLUS)
             return instanceFactory.getPlus();

         if (cardinality == AbstractFactory.CARDINALITE_ETOILE)
             return instanceFactory.getEtoile();

         return instanceFactory.getSimple();
     }

 }
