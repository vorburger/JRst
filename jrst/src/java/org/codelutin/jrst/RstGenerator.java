/* *##%
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

/* *
 * RstGenerator.java
 *
 * Created: 17 janv. 2004
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

public class RstGenerator extends AbstractGenerator { // RstGenerator

    private boolean showBalise = true;

    public void generate(Element e){
        System.out.println("default generate: "+e.getClass().getName());
    }

    public void generate(Document e){
        if ( showBalise) System.out.println("<Document>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
    }

    public void generate(AndElement e){
        if ( showBalise) System.out.println("<AndElement:"+e.name+">");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
    }

    public void generate(OrElement e){
       // if (e.name.matches("StructureModel.*")) System.out.println();
        if ( showBalise) System.out.println("<OrElement:"+e.name+">");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
    }

    public void generate(Title e){
        if ( showBalise) System.out.println("<Title>");
        String title = e.getText();
        if(e.getUpperline()){
            for(int i=0; i<e.getMarkLength(); i++){
                System.out.print((char)e.getTitleMark());
            }
            System.out.println();
        }

        System.out.println(title);

        for(int i=0; i<e.getMarkLength(); i++){
            System.out.print((char)e.getTitleMark());
        }
        System.out.println();
        System.out.println();
    }

    public void generate(Para e){
        String texte = e.getText();
//        texte = texte.replaceAll("\n", "\n"+ getIndent());
//        texte = texte.replaceAll(" ::$", "");
//        texte = texte.replaceAll("(\\S)::$", "$1:");
        if ( showBalise) System.out.println(getIndent()+"<Para>");
        System.out.println(getIndent()+texte);
    }

    public void generate(Litteral e){
        if ( showBalise) System.out.println(getIndent()+"<Litteral>");
        System.out.println(e.getText());
    }

    public void generate(Term e){
        if ( showBalise) System.out.print(getIndent()+"<Term>");
        System.out.print(e.getText());
    }

    public void generate(BulletList e){
        //if ( showBalise)
            System.out.println(getIndent()+"<BulletList>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            System.out.print(getIndent()+e.getSymbole()+" ");
            indentation ++;
            visit((Element)i.next());
            indentation --;
        }
        //System.out.println();
   }

   public void generate(FieldList e){
       //if ( showBalise)
           System.out.println(getIndent()+"<FieldList>");
       boolean oldShow = showBalise;
       showBalise = false;
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               System.out.print(getIndent()+":");
               visit((Element)child);
               System.out.print(": ");
           }else{
               indentation ++;
               visit((Element)child);
               indentation --;
           }
       }
       System.out.println();
       showBalise = oldShow;
   }

   public void generate(EnumList e){
       //if ( showBalise)
           System.out.println(getIndent()+"<EnumList>");
       boolean oldShow = showBalise;
       showBalise = false;
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               if (e.getFormateur() == EnumList.KIND_PARA_PARA) {
                   System.out.print("(");
               }
               System.out.print(getIndent());
               visit((Element)child);
               if (e.getFormateur() == EnumList.KIND_DOT) {
                   System.out.print(". ");
               }else if (e.getFormateur() == EnumList.KIND_PARA ||
                         e.getFormateur() == EnumList.KIND_PARA_PARA){
                   System.out.print(") ");
               }else{
                   System.out.print("???? ");
               }
           }else{
               indentation ++;
               visit((Element)child);
               indentation --;
           }
       }
       System.out.println();
       showBalise = oldShow;
   }

   public void generate(DefList e){
       //if ( showBalise)
           System.out.println(getIndent()+"<DefList>");
       boolean oldShow = showBalise;
       //showBalise = false;
       //System.out.println(e.getChilds().size());

       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               System.out.print(getIndent());
               visit((Element)child);
           }else{
               indentation ++;
               visit((Element)child);
               indentation --;
           }
       }
       System.out.println();
       showBalise = oldShow;
   }


   public void generate(OptionList e){
       //if ( showBalise)
           System.out.println(getIndent()+"<OptionList>");
       boolean oldShow = showBalise;
       //showBalise = false;
       //System.out.println(e.getChilds().size());

       boolean first = true;
       for(int i=0; i<e.getChilds().size(); i++) {
           Object child = e.getChilds().get(i);
           if ( child instanceof Term ) {
               if ( first ) {
                   System.out.print(getIndent());
                   first = false;
               }
               visit((Element)child);
           }else{
               // TODO : mettre le bon nombre d'espaces
               //System.out.print();
               indentation ++;
               visit((Element)child);
               indentation --;
               first = true;
           }
       }
       showBalise = oldShow;
   }

   public void generate(Directive e){
       if ( showBalise ) System.out.println("<Directive>");
       boolean oldShow = showBalise;
       boolean afterTitle = false;
       showBalise = false;

       System.out.print(".. ");
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               visit((Element)child);
               if (i == 0) {
                   System.out.print(":: ");
               }else if (i == 1) {
                   System.out.println();
                   afterTitle = true;
                   indentation ++;
               }
           }else{
               if ( ! afterTitle ) {
                   System.out.println();
                   afterTitle = true;
                   indentation ++;
               }
               visit((Element)child);
           }
       }
       if (afterTitle) indentation --;
       System.out.println();
       showBalise = oldShow;
  }

  public void generate(Hyperlink e){
       if ( showBalise) System.out.println(getIndent()+"<Hyperlink>");

       boolean oldShow = showBalise;
       int counter = 0;
       showBalise = false;

       for(Iterator i=e.getChilds().iterator(); i.hasNext();){

           if (counter == 0){
               System.out.print(getIndent()+".. ");
               visit((Term)i.next());
               System.out.print(": ");
           }else{
               visit((Element)i.next());
           }
           counter++;
       }
      showBalise = oldShow;
      //System.out.println();
  }

  public void generate(Comment e){
      if ( showBalise) System.out.println("<Comment>");
      boolean oldShow = showBalise;
      showBalise = false;

      System.out.print(".. ");
      indentation ++;
      for(int i=0; i<e.getChilds().size(); i++){
          Object child = e.getChilds().get(i);
          visit((Element)child);
      }
      indentation --;
      System.out.println();
      showBalise = oldShow;
  }

  public String inlineMarkup(String text) {
      return text;
  }

} // RstGenerator

