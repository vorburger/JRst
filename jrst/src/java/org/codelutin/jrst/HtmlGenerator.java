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
        os.println("<!-- default generate: " + e.getClass().getName() + "-->");
    }

    // Element racine - root
    public void generate(Document e){
        doc = e;

        os.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        os.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        os.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
        os.println("<head>");
        os.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        os.println("<meta name=\"generator\" content=\"JRST http://www.codelutin.org/\" />");
        if (e.getTitle() != null)
            os.println("<title>"+e.getTitle().getText()+"</title>");
        os.println("<link rel=\"stylesheet\" href=\"default.css\" type=\"text/css\" />");
        os.println("</head>\n<body>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
        os.println("</body></html>");
    }

    public void generate(AndElement e){
        if ( showBalise) os.println("<!-- AndElement:"+e.name+" -->");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
    }

    public void generate(OrElement e){
        if ( showBalise) os.println("<!-- OrElement:"+e.name+" -->");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            this.visit((Element)i.next());
        }
    }

    public void generate(Title e){
        if (e == doc.getTitle()) {
            os.println("<h1 class=\"title\">"+e.getText().trim()+"</h1>");
        }else if ( e.getUpperline()) {
            os.print("<h1><a class=\"toc-backref\" href=\"#id"+e.getId()+"\" name=\""+getHtmlName(e.getText())+"\"> ");
            os.println(e.getText()+"</h1>");
        }else{
            os.print("<h2><a class=\"toc-backref\" href=\"#id"+e.getId()+"\" name=\""+getHtmlName(e.getText())+"\"> ");
            os.println(e.getText()+"</h2>");
        }
    }

    public void generate(Para e){
        if (paraP) {
            os.println("<p>"+inlineMarkup(e.getText())+"</p>");
        }else{
            os.print(inlineMarkup(e.getText()));
        }
    }

    public void generate(Litteral e){
        os.println(getIndent()+"<pre class=\"literal-block\">"+e.getText()+"</pre>");
    }

    public void generate(Term e){
        os.print(e.getText());
    }

    public void generate(BulletList e){
        os.println(getIndent()+"<ul>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            os.print("<li>");
            indentation ++;
            visit((Element)i.next());
            indentation --;
            os.println("</li>");
        }
        os.println(getIndent()+"</ul>");
   }

   public void generate(FieldList e){

       String classTerm = null;
       if (e == doc.getBibliographic()) {
           os.println(getIndent()+"<table class=\"docinfo\" frame=\"void\" rules=\"none\">");
           os.println(getIndent()+"<col class=\"docinfo-name\" />");
           os.println(getIndent()+"<col class=\"docinfo-content\" />");
           classTerm = " class=\"docinfo-name\" ";
       }else{
           os.println(getIndent()+"<table>");
       }
       os.println(getIndent()+"<tbody valign=\"top\">");

       boolean oldParaP = paraP;
       paraP = false;
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               os.print(getIndent()+"<tr><th"+classTerm+">");
               os.print(((Term)child).getText()+":");
               os.println("</th>");
           }else{
               os.print(getIndent()+"<td>");
               indentation ++;
               visit((Element)child);
               indentation --;
               os.println("</td></tr>");
           }
       }
       paraP = oldParaP;
       os.println(getIndent()+"</tbody></table>");
   }

   public void generate(EnumList e){
       os.println("<ol>");
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               os.print("<li>");
               //visit((Element)child);
           }else{
               visit((Element)child);
               os.print("</li>");
           }
       }
       os.println("</ol>");

       /*
       <ol type="1">
       <li value="3"> .. valeur de départ de la numérotation
       */
   }

   public void generate(DefList e){

       os.println(getIndent()+"<dl>");
       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           if(child instanceof Term){
               os.print("<dt><strong>");
               visit((Element)child);
               os.print("</strong>");
           }else{
               os.print("<dd>");
               indentation ++;
               visit((Element)child);
               indentation --;
               os.print("</dd>");
           }
       }
       os.println(getIndent()+"</dl>");
   }


   public void generate(OptionList e){
       if ( showBalise)  os.println(getIndent()+"<!-- OptionList -->");
       boolean first = true;

       os.println(getIndent()+"<table><tbody>");
       for(int i=0; i<e.getChilds().size(); i++) {
           Object child = e.getChilds().get(i);
           if ( child instanceof Term ) {
               if (first) {
                   os.print(getIndent()+"<tr><td>");
                   first = false;
               }
               visit((Element)child);
           }else{
               os.println("</td>");
               os.print(getIndent()+"<td>");
               indentation ++;
               visit((Element)child);
               indentation --;
               os.println("</td></tr>");
               first = true;
           }
       }
       os.println(getIndent()+"</tbody></table>");
   }

   public void generate(BlockQuote e){
       if ( showBalise)  os.println(getIndent()+"<!-- blockQuote -->");

       os.println(getIndent()+"<blockquote>");
       for(int i=0; i<e.getChilds().size(); i++) {
           Object child = e.getChilds().get(i);
           //indentation ++;
           visit((Element)child);
           //indentation --;
       }
       os.println(getIndent()+"</blockquote>");
   }


   public void generate(Directive e){
       if ( showBalise ) os.println("<!-- Directive -->");
       boolean afterTitle = false;

       if (e.getType() == Directive.KIND_NOTE) {
           os.println(getIndent()+ "<div class=\"note\">"+
           "<p class=\"admonition-title first\">Note</p>");
       }else if (e.getType() == Directive.KIND_CONTENTS) {
           os.println(getIndent()+ "<table border='1'><tbody><td>");
           Element contents = doc.getContents();
       }else{
           os.println(getIndent()+"<strong>"+e.getText()+"</strong>");
       }

       for(int i=0; i<e.getChilds().size(); i++){
           Object child = e.getChilds().get(i);
           indentation ++;
           visit((Element)child);
           indentation --;
       }

       if (e.getType() == Directive.KIND_NOTE) {
           os.println(getIndent()+ "</div>");
       }else if (e.getType() == Directive.KIND_CONTENTS) {
           os.println(getIndent()+"</td></tbody></table>");
       }
   }

   public void generate(Hyperlink e){
       /*
       if ( showBalise) os.println(getIndent()+"<Hyperlink>");

       boolean oldShow = showBalise;
       int counter = 0;
       showBalise = false;

       for(Iterator i=e.getChilds().iterator(); i.hasNext();){

           if (counter == 0){
               os.print(getIndent()+".. ");
               visit((Term)i.next());
               os.print(": ");
           }else{
               visit((Element)i.next());
           }
           counter++;
       }
       showBalise = oldShow;
       //os.println();
       */
   }

   public void generate(Comment e){
      os.println("<!-- Comment ");
      boolean oldShow = showBalise;
      showBalise = false;

      indentation ++;
      for(int i=0; i<e.getChilds().size(); i++){
          Object child = e.getChilds().get(i);
          visit((Element)child);
      }
      indentation --;
      os.println("-->");
      showBalise = oldShow;
   }

   public void generate(GridTable e) {
       boolean oldParaP = paraP;
       boolean inHead = e.getHead();
       paraP = false;

       os.println(getIndent()+"<table border=\"1\" cellspacing=\"0\">");
       if (inHead)
           os.println(getIndent()+"<thead valign=\"bottom\">");
       else
           os.println(getIndent()+"<tbody valign=\"top\">");

       for(int j=0; j<e.getLongueur(); j++){
           os.println(getIndent()+"<tr>");
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
                       os.print(getIndent()+"<th"+rowSpan+">");
                   else
                       os.print(getIndent()+"<td"+rowSpan+">");

                   indentation++;
                   //os.println("["+i+","+j+"]");
                   visit(compartment);
                   indentation--;

                   if (inHead)
                       os.println(getIndent()+"</th>");
                   else
                       os.println(getIndent()+"</td>");
               }
           }
           indentation--;
           os.println(getIndent()+"</tr>");
           if (inHead) {
               os.println(getIndent()+"</thead>\n"+getIndent()+"<tbody valign=\"top\">");
               inHead = false;
           }
       }
       os.println(getIndent()+"</table>");

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

