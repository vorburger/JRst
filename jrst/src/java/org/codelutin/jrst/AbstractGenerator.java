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
 * AbstractGenerator.java
 *
 * Created: Oct 8, 2003
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
import java.lang.reflect.Method;

public abstract class AbstractGenerator implements Generator { // AbstractGenerator

    abstract public String inlineMarkup(String text);

    public void visit(Element e){
        if(e != null){
            callGenerate(e);
        }
    }

    protected void callGenerate(Element e){
        try{
            Class eclass = e.getClass();
            while(eclass != null && !eclass.equals(Element.class)){
                try{
                    Method m = getClass().getMethod("generate", new Class[]{eclass});
                    m.invoke(this, new Object[]{e});
                    return;
                }catch(NoSuchMethodException eee){
                    eclass = eclass.getSuperclass();
                }
            }
            generate(e);
        }catch(Exception eee){
            eee.printStackTrace();
        }
    }

    // Gestion de l'indentation
    protected int indentation = 0;

    protected String getIndent() {
        String resultat = "";
        for(int i = 0; i != indentation; i++)
            resultat += "  ";

        return resultat;
    }


} // AbstractGenerator

