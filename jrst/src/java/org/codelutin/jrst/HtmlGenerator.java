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
import java.util.List;
import java.util.ArrayList;

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

        //os.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        os.println("<?xml version=\"1.0\" encoding=\"iso-8859-15\" ?>");
        os.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        os.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
        os.println("<head>");
        os.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-15\" />");
        os.println("<meta name=\"generator\" content=\"JRST http://www.codelutin.org/\" />");
        if (e.getTitle() != null)
            os.println("<title>"+inlineMarkup(e.getTitle().getText())+"</title>");
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
            os.println("<h1 class=\"title\">"+inlineMarkup(e.getText()).trim()+"</h1>");
        }else if ( e.getUpperline()) {
            os.print("<h1><a class=\"toc-backref\"  name=\""+getHtmlName(e.getText())+"\"> ");//href=\"#id"+e.getId()+"\"
            os.println(inlineMarkup(e.getText())+"</a></h1>");
        }else{
            os.print("<h"+(e.getProfondeur()+1)+"><a class=\"toc-backref\"  name=\""+getHtmlName(e.getText())+"\"> "); //href=\"#id"+e.getId()+"\"
            os.println(inlineMarkup(e.getText())+"</a></h"+(e.getProfondeur()+1)+">");
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
        os.println(getIndent()+"<pre class=\"literal-block\">"+encode(e.getText())+"</pre>");
    }

    public void generate(Term e){
        os.print(inlineMarkup(e.getText()));
    }

    public void generate(BulletList e){
        os.println(getIndent()+"<ul>");
        for(Iterator i=e.getChilds().iterator(); i.hasNext();){
            indentation ++;
            os.print(getIndent()+"<li>");
            visit((Element)i.next());
            os.println(getIndent()+"</li>");
            indentation --;
        }
        os.println(getIndent()+"</ul>");
   }

   public void generate(FieldList e){

       String classTerm = "";
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
               os.print(inlineMarkup(((Term)child).getText())+":");
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

       // Une Note
       if (e.getType() == Directive.KIND_NOTE) {
           os.println(getIndent()+ "<div class=\"note\">"+
           "<p class=\"admonition-title first\">Note</p>");
           for(int i=0; i<e.getChilds().size(); i++){
               Object child = e.getChilds().get(i);
               indentation ++;
               visit((Element)child);
               indentation --;
           }
           os.println(getIndent()+ "</div>");

       // la Table des matières
       }else if (e.getType() == Directive.KIND_CONTENTS) {
           os.println(getIndent()+ "<div class=\"contents topic\" id=\"contents\">");
           os.println(getIndent()+ "<p class=\"topic-title first\"><a name=\"contents\">Contents</a></p>");
           List contents = doc.getContents();
           generateContents(contents);
           os.println(getIndent()+ "</div>");

       // une image
       }else if (e.getType() == Directive.KIND_IMAGE) {
           if (e.getChilds().size() > 0) {
               String img  = getIndent()+ "<img src=\""+((Term)e.getChilds().get(0)).getText()+"\" ";
               String comment = null;

               for(int i=1; i<e.getChilds().size(); i++){
                   Term child = (Term)e.getChilds().get(i);
                   if (child.getText().matches(":height: (\\d*)"))
                       img = img + "height=\""+child.getText().replaceAll(":height: (\\d*)","$1")+"\" ";
                   else if (child.getText().matches(":width: (\\d*)"))
                       img = img + "width=\""+child.getText().replaceAll(":width: (\\d*)","$1")+"\" ";
                   else if (child.getText().matches(":comment: (.*)")){
                       comment = child.getText().replaceAll(":comment: (.*)","$1");
                       img = img + "alt=\""+comment+"\" ";
                   }

               }
               os.println(img+"/>");
               if (comment != null) {
                   os.println(getIndent()+"<p><i>"+comment+"</i></p>");
               }
           }
       }else{
           os.println(getIndent()+"<strong>"+inlineMarkup(e.getText())+"</strong>");
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
       os.println(getIndent()+"</tbody></table>");

   }

   /** table des matières **/

   public void generateContents(List e) {
       final int coef = 256;
       ArrayList stack = new ArrayList(); // pîle des symboles de titre
       int ulCounter = 0;
       int counter = 0;

       if (e == null) return;

       Title prec = null;
       for(Iterator i=e.iterator(); i.hasNext();){ // liste des titres
           Title next = (Title)i.next();
           if (next == doc.getTitle())
               continue;

           if (prec != null) {
               if (prec.getTitleMark() != (next.getUpperline()?next.getTitleMark()*coef:next.getTitleMark())) {
                   boolean trouve = false;
                   int trouveAt = -1;
                   for(int j=0; j < stack.size(); j++){ // parcous de la pile
                       if (trouve) {
                           indentation --;
                           os.println(getIndent()+"</ul>");
                       }
                       if (((Integer)stack.get(j)).intValue() == (next.getUpperline()?next.getTitleMark()*coef:next.getTitleMark())) {
                           trouve = true;
                           trouveAt = j;
                       }
                   }
                   if ( !trouve ) {
                       stack.add(new Integer(next.getUpperline()?next.getTitleMark()*coef:next.getTitleMark()));
                       os.println(getIndent()+ "<ul class=\"simple\">");
                       indentation ++;
                   }else{

                       while (stack.size()-1 > trouveAt && stack.size()>0 ){
                           stack.remove(stack.size()-1);
                       }
                   }
               }
           }else{
               // 1er tour de boucle
               stack.add(new Integer(next.getUpperline()?next.getTitleMark()*coef:next.getTitleMark()));
               os.println(getIndent()+ "<ul class=\"simple\">");
               indentation ++;
           }

               next.setProfondeur(stack.size());
               next.setId(counter);
               os.print(getIndent()+"<li class=\"reference\"><a href=\"#"+getHtmlName(next.getText())+
               "\"  id=\"id"+next.getId()+"\" name=\"id"+next.getId()+"\">");
               os.println(inlineMarkup(next.getText())+"</a></li>");
               prec = next;
               counter ++;

       }
       for(Iterator j=stack.iterator(); j.hasNext();){
           j.next();
           indentation --;
           os.println(getIndent()+"</ul>");
       }
   }


   /** Modificateur de String **/

   public String getHtmlName(String text) {
       return text.toLowerCase().trim().replace(' ','-');
   }

   /**
    * Encode pour les expressions régulières
    */
   public String encodeSpecChar(String text) {
       return text.replaceAll("\\(", "\\\\(").replaceAll("\\)","\\\\)").replaceAll("\\[","\\\\[").replaceAll("\\]","\\\\]");
   }

   /**
   * Permet de convertir les caratere speciaux HTML du texte
   */
   protected String encode(String s){
       s = s.replaceAll("&", "&amp;"); // first all the time
       s = s.replaceAll("<", "&lt;");
       s = s.replaceAll(">", "&gt;");
       return s;
   }

   public String inlineMarkup(String text) {
       String before = "(^|[ '\"(\\[<])";
       String after = "([ '\".,:\\;!?)\\]}/\\>]|$)";

       String t = encode(text);

       t = t.replaceAll("([^ ]+@[^ ]+\\.[^ ]+)","<a href=\"mailto:$1\">$1</a>"); // courriel
       t = t.replaceAll("(((http[s]?)|ftp|mailto|telnet|news|e2k|ssh)://[^ \\)]+\\.[^ \\)]+)","<a href=\"$1\">$1</a>"); // URL

       t = t.replaceAll(before+"[\\`][\\`]([^ ]*?.*?[^ ]*?)[\\`][\\`]"+after,"$1<code>$2</code>$3"); // inline literal
       t = t.replaceAll(before+"[\\`]([^ ]*?.*?[^ ]*?)[\\`]"+after,"$1<i>$2</i>$3"); // interpreted text

       t = t.replaceAll(before+"[\\*][\\*]([^ ]*?.*?[^ ]*?)[\\*][\\*]"+after,"$1<strong>$2</strong>$3"); // strong emphasis
       t = t.replaceAll(before+"[\\*]([^ ]*?.*?[^ ]*?)[\\*]"+after,"$1<em>$2</em>$3"); // emphasis

       //  String quotedReference = t.replaceFirst("^.*`(.+?)`_.*$","$1");//getHtmlName(); // reference
       String quotedReference = t.replaceFirst("^.*"+before+"`(.+?)`_"+after+".*$","$2");//getHtmlName(); // reference
       while (! quotedReference.equals(t)) {
           String link = getHtmlName(doc.findLink(quotedReference));
           //  t = t.replaceFirst("(`"+quotedReference+"`_)","<a href=\""+link+"\">"+quotedReference+"</a>");
           t = t.replaceFirst(before+"(`"+encodeSpecChar(quotedReference)+"`_)"+after,"$1<a href=\""+link+"\">"+quotedReference+"</a>$3");
           //  quotedReference = t.replaceFirst("^.*`(.+?)`_.*$","$1");
           quotedReference = t.replaceFirst("^.*"+before+"`(.+?)`_"+after+".*$","$2");
       }

       //  String simpleReference = t.replaceFirst("^.*([^ ]+)_.*$","$1");
       String simpleReference = t.replaceFirst("^.*"+before+"([^ ]+)_"+after+".*$","$2");
       while(! simpleReference.equals(t)) {
           String link = getHtmlName(doc.findLink(simpleReference));
           // t = t.replaceFirst("("+simpleReference+"_)","<a href=\""+link+"\">"+simpleReference+"</a>");
           t = t.replaceFirst(before+"("+encodeSpecChar(simpleReference)+"_)"+after,"$1<a href=\""+link+"\">"+simpleReference+"</a>$3");
           // simpleReference = t.replaceFirst("^.*([^ ]+)_.*$","$1");
           simpleReference = t.replaceFirst("^.*"+before+"([^ ]+)_"+after+".*$","$2");
       }


       return t;
   }



} // HtmlGenerator

