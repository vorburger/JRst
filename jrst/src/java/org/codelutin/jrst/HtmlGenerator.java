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
 * ##%*/

/* *
 * HtmlGenerator.java
 *
 * Created: 24 janv. 2004
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

public class HtmlGenerator extends AbstractGenerator { // HtmlGenerator

    protected boolean showBalise = false;
    protected boolean paraP = true;

    protected Document doc = null;

    public HtmlGenerator() {
        super();
    }

    public void generate(Element e){
        System.out.println("<!-- default generate: " + e.getClass().getName() + "-->");
    }

    // Element racine - root
    public void generate(Document e){
        doc = e;

        System.out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        System.out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        System.out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
        System.out.println("<head>");
        System.out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        System.out.println("<meta name=\"generator\" content=\"JRST http://www.codelutin.org/\" />");
        if (e.getTitle() != null)
            System.out.println("<title>"+e.getTitle().getText()+"</title>");
        System.out.println("<link rel=\"stylesheet\" href=\"default.css\" type=\"text/css\" />");
        System.out.println("</head>\n<body>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
        System.out.println("</body></html>");
    }

    public void generate(AndElement e){
        if ( showBalise) System.out.println("<!-- AndElement:"+e.name+" -->");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
    }

    public void generate(OrElement e){
        if ( showBalise) System.out.println("<!-- OrElement:"+e.name+" -->");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
    }

    public void generate(Title e){
        if (e == doc.getTitle()) {
            System.out.println("<h1 class=\"title\">"+e.getText().trim()+"</h1>");
        }else if ( e.getUpperline()) {
            System.out.print("<h1><a class=\"toc-backref\" href=\"#id"+e.getId()+"\" name=\""+getHtmlName(e.getText())+"\"> ");
            System.out.println(e.getText()+"</h1>");
        }else{
            System.out.print("<h2><a class=\"toc-backref\" href=\"#id"+e.getId()+"\" name=\""+getHtmlName(e.getText())+"\"> ");
            System.out.println(e.getText()+"</h2>");
        }
    }

    public void generate(Para e){
        if (paraP) {
            System.out.println("<p>"+inlineMarkup(e.getText())+"</p>");
        }else{
            System.out.print(inlineMarkup(e.getText()));
        }
    }

    public void generate(Litteral e){
        System.out.println(getIndent()+"<pre>"+e.getText()+"</pre>");
    }

    public void generate(Term e){
        System.out.print(e.getText());
    }

    public void generate(BulletList e){
        System.out.println(getIndent()+"<ul>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            System.out.print("<li>");
            indentation ++;
            visit((Element)i.next());
            indentation --;
            System.out.println("</li>");
        }
        System.out.println(getIndent()+"</ul>");
   }

   public void generate(FieldList e){

       String classTerm = null;
       if (e == doc.getBibliographic()) {
           System.out.println(getIndent()+"<table class=\"docinfo\" frame=\"void\" rules=\"none\">");
           System.out.println(getIndent()+"<col class=\"docinfo-name\" />");
           System.out.println(getIndent()+"<col class=\"docinfo-content\" />");
           classTerm = " class=\"docinfo-name\" ";
       }else{
           System.out.println(getIndent()+"<table>");
       }
       System.out.println(getIndent()+"<tbody valign=\"top\">");

       boolean oldParaP = paraP;
       paraP = false;
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               System.out.print(getIndent()+"<tr><th"+classTerm+">");
               System.out.print(((Term)child).getText()+":");
               System.out.println("</th>");
           }else{
               System.out.print(getIndent()+"<td>");
               indentation ++;
               visit((Element)child);
               indentation --;
               System.out.println("</td></tr>");
           }
       }
       paraP = oldParaP;
       System.out.println(getIndent()+"</tbody></table>");
   }

   public void generate(EnumList e){
       System.out.println("<ol>");
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               System.out.print("<li>");
               //visit((Element)child);
           }else{
               visit((Element)child);
               System.out.print("</li>");
           }
       }
       System.out.println("</ol>");

       /*
       <ol type="1">
       <li value="3"> .. valeur de départ de la numérotation
       */
   }

   public void generate(DefList e){

       System.out.println(getIndent()+"<dl>");
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               System.out.print("<dt><strong>");
               visit((Element)child);
               System.out.print("</strong>");
           }else{
               System.out.print("<dd>");
               indentation ++;
               visit((Element)child);
               indentation --;
               System.out.print("</dd>");
           }
       }
       System.out.println(getIndent()+"</dl>");
   }


   public void generate(OptionList e){
       if ( showBalise)  System.out.println(getIndent()+"<!-- OptionList -->");
       boolean first = true;

       System.out.println(getIndent()+"<table><tbody>");
       for(int i=0; i<e.getChilds().size(); i++) {
           Object child = e.getChilds().get(i);
           if ( child instanceof Term ) {
               if (first) {
                   System.out.print(getIndent()+"<tr><td>");
                   first = false;
               }
               visit((Element)child);
           }else{
               System.out.println("</td>");
               System.out.print(getIndent()+"<td>");
               indentation ++;
               visit((Element)child);
               indentation --;
               System.out.println("</td></tr>");
               first = true;
           }
       }
       System.out.println(getIndent()+"</tbody></table>");
   }

   public void generate(Directive e){
       if ( showBalise ) System.out.println("<!-- Directive -->");
       boolean afterTitle = false;

       if (e.getType() == Directive.KIND_NOTE) {
           System.out.println(getIndent()+ "<div class=\"note\">"+
           "<p class=\"admonition-title first\">Note</p>");
       }else if (e.getType() == Directive.KIND_CONTENTS) {
           System.out.println(getIndent()+ "<table border='1'><tbody><td>");
       }else{
           System.out.println(getIndent()+"<strong>"+e.getText()+"</strong>");
       }

       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           indentation ++;
           visit((Element)child);
           indentation --;
       }

       if (e.getType() == Directive.KIND_NOTE) {
           System.out.println(getIndent()+ "</div>");
       }else if (e.getType() == Directive.KIND_CONTENTS) {
           System.out.println(getIndent()+"</td></tbody></table>");
       }
   }

   public void generate(Hyperlink e){
       /*
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
       */
   }

   public void generate(Comment e){
      System.out.println("<!-- Comment ");
      boolean oldShow = showBalise;
      showBalise = false;

      indentation ++;
      for(int i=0; i<e.getChilds().size(); i++){
          Object child = e.getChilds().get(i);
          visit((Element)child);
      }
      indentation --;
      System.out.println("-->");
      showBalise = oldShow;
   }

   public void generate(GridTable e) {
       boolean oldParaP = paraP;
       boolean inHead = e.getHead();
       paraP = false;

       System.out.println(getIndent()+"<table border=\"1\" cellspacing=\"0\">");
       if (inHead)
           System.out.println(getIndent()+"<thead valign=\"bottom\">");
       else
           System.out.println(getIndent()+"<tbody valign=\"top\">");

       for(int j=0; j<e.getLongueur(); j++){
           System.out.println(getIndent()+"<tr>");
           indentation++;
           for(int i=0; i<e.getLargeur(); i++){
               if (e.getTable(i,j) != null) {
                   Element compartment = e.getTable(i,j);

                   int countRowSpan = 0;
                   int countColumnSpan = 0;

                   while (j+countColumnSpan < e.getLongueur() &&
                   e.getTable(i,j+countColumnSpan) == compartment) {
                       countRowSpan=0;
                       while (i+countRowSpan < e.getLargeur() &&
                       e.getTable(i+countRowSpan,j+countColumnSpan) == compartment) {
                           if (!(countRowSpan == 0 && countColumnSpan == 0))
                               // pour ne pas faire la case par la suite
                               e.setTable(i+countRowSpan,j+countColumnSpan, null);

                           countRowSpan++;
                       }
                       countColumnSpan++;
                   }

                   String rowSpan = countRowSpan != 0 ? " colspan=\""+(countRowSpan)+"\"" : "";
                   rowSpan += countColumnSpan != 0 ? " rowspan=\""+(countColumnSpan)+"\"" : "";

                   if (inHead)
                       System.out.print(getIndent()+"<th"+rowSpan+">");
                   else
                       System.out.print(getIndent()+"<td"+rowSpan+">");

                   indentation++;
                   //System.out.println("["+i+","+j+"]");
                   visit(compartment);
                   indentation--;

                   if (inHead)
                       System.out.println(getIndent()+"</th>");
                   else
                       System.out.println(getIndent()+"</td>");
               }
           }
           indentation--;
           System.out.println(getIndent()+"</tr>");
           if (inHead) {
               System.out.println(getIndent()+"</thead>\n"+getIndent()+"<tbody valign=\"top\">");
               inHead = false;
           }
       }
       System.out.println(getIndent()+"</table>");

   }

   /** Modificateur de String **/

   public String getHtmlName(String text) {
       return text.toLowerCase().trim().replace(' ','_');
   }

   public String inlineMarkup(String text) {
       // TODO
       String before = "([ '\"(\\[<])";
       String after = "([ '\".,:\\;!?)\\]}/\\>])";
       String t = text;

       t = t.replaceAll("([^ ]+@[^ ]+\\.[^ ]+)","<a href=\"mailto:$1\">$1</a>"); // courriel
       t = t.replaceAll("(((http[s]?)|ftp|mailto|telnet|news|skype|e2k|ssh)://[^ \\)]+\\.[^ \\)]+)","<a href=\"$1\">$1</a>"); // URL

       t = t.replaceAll(before+"[\\`][\\`]([^ ]*.*[^ ]*)[\\`][\\`]"+after,"$1<code>$2</code>$3"); // inline literal
       t = t.replaceAll(before+"[\\`]([^ ]*.*[^ ]*)[\\`]"+after,"$1<i>$2</i>$3"); // interpreted text

       t = t.replaceAll(before+"[\\*][\\*]([^ ]*.*[^ ]*)[\\*][\\*]"+after,"$1<strong>$2</strong>$3"); // strong emphasis
       t = t.replaceAll(before+"[\\*]([^ ]*.*[^ ]*)[\\*]"+after,"$1<em>$2</em>$3"); // emphasis

       return t;
   }

} // HtmlGenerator

