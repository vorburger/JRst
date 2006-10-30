/* *##%
 * Copyright (C) 2006
 *     Code Lutin, Cédric Pineau, Benjamin Poussin
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
 * JRSTReader.java
 *
 * Created: 27 oct. 06 00:15:34
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import static org.codelutin.jrst.ReStructuredText.*;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.codelutin.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;


/*
 *
<pre>
+--------------------------------------------------------------------+
| document  [may begin with a title, subtitle, decoration, docinfo]  |
|                             +--------------------------------------+
|                             | sections  [each begins with a title] |
+-----------------------------+-------------------------+------------+
| [body elements:]                                      | (sections) |
|         | - literal | - lists  |       | - hyperlink  +------------+
|         |   blocks  | - tables |       |   targets    |
| para-   | - doctest | - block  | foot- | - sub. defs  |
| graphs  |   blocks  |   quotes | notes | - comments   |
+---------+-----------+----------+-------+--------------+
| [text]+ | [text]    | (body elements)  | [text]       |
| (inline +-----------+------------------+--------------+
| markup) |
+---------+
</pre>
 * 
 * 
 * Inline support: http://docutils.sourceforge.net/docs/user/rst/quickref.html
 * 
 * <li> STRUCTURAL ELEMENTS: document, section, topic, sidebar
 * <li> STRUCTURAL SUBELEMENTS: title, subtitle, decoration, docinfo, transition
 * <li> docinfo: address, author, authors, contact, copyright, date, field,
 *        organization, revision, status, version
 * <li> decoration: footer, header
 * <li> BODY ELEMENTS: admonition, attention, block_quote, bullet_list, caution,
 *        citation, comment, compound, container, danger, definition_list, 
 *        doctest_block, enumerated_list, error, field_list, figure, footnote, 
 *        hint, image, important, line_block, literal_block, note, option_list, 
 *        paragraph, pending, raw, rubric, substitution_definition, 
 *        system_message, table, target, tip, warning
 * <li> SIMPLE BODY ELEMENTS: comment, doctest_block, image, literal_block, 
 *        paragraph, pending, raw, rubric, substitution_definition, target
 * <li> COMPOUND BODY ELEMENTS: admonition, attention, block_quote, bullet_list,
 *        caution, citation, compound, container, danger, definition_list, 
 *        enumerated_list, error, field_list, figure, footnote, hint, important, 
 *        line_block, note, option_list, system_message, table, tip, warning
 * <li> BODY SUBELEMENTS: attribution, caption, classifier, colspec, field_name,
 *        label, line, option_argument, option_string, term
 *        definition, definition_list_item, description, entry, field, 
 *        field_body, legend, list_item, option, option_group, option_list_item,
 *        row, tbody, tgroup, thead
 * <li> INLINE ELEMENTS: abbreviation, acronym, citation_reference, emphasis, 
 *        footnote_reference, generated, image, inline, literal, problematic, 
 *        reference, strong, subscript, substitution_reference, superscript, 
 *        target, title_reference, raw
 *
 <pre>
 DOCUMENT :: ( (title, subtitle?)?, decoration?, (docinfo, transition?)?,
   STRUCTURE.MODEL; )
 decoration :: (header?, footer?)
 header, footer, definition, description, attention, caution,  danger,  error,
 hint,  important, note, tip, warning :: (BODY.ELEMENTS;)+
 transition :: EMPTY
 docinfo :: (BIBLIOGRAPHIC.ELEMENTS;)+
 BIBLIOGRAPHIC.ELEMENTS :: author | authors | organization | contact | address
   | version | revision | status | date | copyright | field
 authors :: ( (author)+ )
 field :: (field_name, field_body)
 field_body, list_item :: (BODY.ELEMENTS;)*
 STRUCTURE.MODEL :: ( ( (BODY.ELEMENTS; | topic | sidebar)+, transition? )*, 
   ( (section), (transition?, (section) )* )? )
 BODY.ELEMENTS :: paragraph | compound | container | literal_block | doctest_block
   | line_block | block_quote | table | figure | image | footnote | citation
   | rubric | bullet_list | enumerated_list | definition_list | field_list 
   | option_list | attention | caution | danger | error | hint | important 
   | note | tip | warning | admonition | reference | target 
   | substitution_definition | comment | pending | system_message | raw
 topic :: (title?, (BODY.ELEMENTS;)+)
 sidebar :: (title, subtitle?, (BODY.ELEMENTS; | topic)+)
 section :: (title, STRUCTURE.MODEL;)
 line_block :: (line | line_block)+
 block_quote:: ((BODY.ELEMENTS;)+, attribution?)
 bullet_list, enumerated_list :: (list_item +)
 definition_list :: (definition_list_item +)
 definition_list_item :: (term, classifier?, definition)
 field_list :: (field +)
 option_list :: (option_list_item +)
 option_list_item :: (option_string, option_argument *, description)
 option_string, option_argument :: (#PCDATA)
 admonition :: (title, (BODY.ELEMENTS;)+)
 
 title, subtitle, author, organization, contact, address, version, revision,
 status, date, copyright, field_name, paragraph, compound, container,
 literal_block, doctest_block, attribution, line, term, classifier :: TEXT.MODEL;
 
 TEXT.MODEL :: (#PCDATA | INLINE.ELEMENTS;)*
 INLINE.ELEMENTS :: emphasis | strong | literal | reference | footnote_reference
   | citation_reference | substitution_reference | title_reference | abbreviation
   | acronym | subscript | superscript | inline | problematic | generated
   | target | image | raw
 emphasis :: '*' #PCDATA '*'
 strong :: '**' #PCDATA '**'
 literal :: '``' #PCDATA '``'
 footnote_reference :: '[' ([0-9]+|#) ']'
 citation_reference :: '[' [a-zA-Z]+ ']'
 
 </pre>
*/

/**
 * Le principe est d'utiliser les methodes peek* {@link JRSTLexer} pour prendre
 * l'element que l'on attend, si la methode retourne null ou un autre element
 * et bien c que ce n'est pas le bon choix, cela veut dire que l'element courant
 * est fini d'etre lu (plus de paragraphe dans la section par exemple) ou
 * qu'il y a une erreur dans le fichier en entre.
 * <p>
 * On construit un arbre XML representant le RST au fur et a mesure, on peut
 * ensuite appliquer une fueille de style ou autre chose avec
 * {@link JRSTGenerator}
 * 
 * <p>
 * Tous les elements ont un attribut level qui permet de savoir on il est dans
 * la hierarchie. Le Document a le level -1001, et les sections/titres
 * on pour level les valeurs 1000, -999, -998, ...
 * <p>
 * de cette facon les methods isUpperLevel et isSameLevel fonctionne pour
 * tous les elements de la meme facon
 * 
 * @author poussin
 */
public class JRSTReader {
    
    boolean ERROR_MISSING_ITEM = false;
    static int MAX_SECTION_DEPTH = -1000;
    
    /**
     * 
     */
    public JRSTReader() {
    }

    
    /**
     * On commence par decouper tout le document en Element, puis on construit
     * l'article a partir de ces elements.
     * @param reader
     * @return
     * @throws IOException
     * @throws DocumentException 
     */
    public Document read(Reader reader) throws IOException, DocumentException {
        JRSTLexer lexer = new JRSTLexer(reader);
               
        Element root = composeDocument(lexer);
        
        // remove all level attribute
        root.accept(new VisitorSupport() {
            public void visit(Element e) {
                e.addAttribute("level", null);
            }
        });
        
        Document result = DocumentHelper.createDocument();
        result.setRootElement(root);
        
        return result;
    }

    /**
     * @param in
     * @param root
     * @throws IOException 
     * @throws DocumentException 
     */
    private Element composeDocument(JRSTLexer lexer) throws IOException, DocumentException {
        Element result = DocumentHelper.createElement(DOCUMENT);
        result.addAttribute("level", String.valueOf(MAX_SECTION_DEPTH - 1));

        Element item = null;
        
        // le titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element title = result.addElement(TITLE);
            copyLevel(item, title);
            title.appendContent(inline(item.getText()));
        }
        
        // le sous titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element subtitle = result.addElement(SUBTITLE);
            copyLevel(item, subtitle);
            subtitle.appendContent(inline(item.getText()));
        }
        
        // les infos du doc
        item = lexer.peekDocInfo();
        Element documentinfo = null;
        while (itemEquals(DOCINFO, item) || itemEquals(FIELD_LIST, item)) {
            if (documentinfo == null) {
                documentinfo = result.addElement(DOCINFO);
            }
            
            if (itemEquals(FIELD_LIST, item)) {
                Element field = composeFieldItemList(lexer);
                documentinfo.add(field);
            } else {
                if ("author".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(AUTHOR).appendContent(inline(item.getText()));
                } else if ("date".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(DATE).appendContent(inline(item.getText()));
                } else if ("organization".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(ORGANIZATION).appendContent(inline(item.getText()));
                } else if ("contact".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(CONTACT).appendContent(inline(item.getText()));
                } else if ("address".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(ADDRESS).appendContent(inline(item.getText()));
                } else if ("version".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(VERSION).appendContent(inline(item.getText()));
                } else if ("revision".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(REVISION).appendContent(inline(item.getText()));
                } else if ("status".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(STATUS).appendContent(inline(item.getText()));
                } else if ("copyright".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(COPYRIGHT).appendContent(inline(item.getText()));
                } // TODO authors

                lexer.remove();
            }
            item = lexer.peekDocInfo();
        }
        
        // l'abstract du doc
        item = lexer.peekTitle();
        while (itemNotEquals(TITLE, item) && !lexer.eof()) {
            composeBody(lexer, result);            
            item = lexer.peekTitle();
        }
        
        // les sections
        item = lexer.peekTitle();
        while (itemEquals(TITLE, item, true, lexer.eof())) {
            Element section = composeSection(lexer);
            result.add(section);
            item = lexer.peekTitle();
        }
        
        return result;
    }

    /**
     * *@param root
     * @return
     * @throws DocumentException 
     * @throws IOException 
     */
    private Element composeBody(JRSTLexer lexer, Element parent) throws DocumentException, IOException {
        Element item = lexer.peekTitleOrBodyElement();
        while (!lexer.eof() && itemNotEquals(TITLE, item) && isUpperLevel(item, parent)) {
            if (itemEquals(PARAGRAPH, item)) {
                lexer.remove();
                Element para = parent.addElement(PARAGRAPH);
                copyLevel(item,para);
                para.appendContent(inline(item.getText()));
            } else if (itemEquals(TRANSITION, item)) {
                lexer.remove();
                Element para = parent.addElement(TRANSITION);
                copyLevel(item, para);
            } else if (itemEquals(LITERAL_BLOCK, item)) {
                lexer.remove();
                Element para = parent.addElement(LITERAL_BLOCK);
                copyLevel(item, para);
                para.setText(item.getText());
            } else if (itemEquals(TABLE, item)) {
                // TODO now we take table as LITERAL_BLOCK, but in near
                // futur we must parse correctly TABLE (show JRSTGenerator and JRSTLexer too)
                lexer.remove();
                Element para = parent.addElement(TABLE);
                copyLevel(item, para);
                para.setText(item.getText());
             } else if (itemEquals(BULLET_LIST, item)) {
                Element list = composeBulletList(lexer);
                parent.add(list);
            } else if (itemEquals(ENUMERATED_LIST, item)) {
                Element list = composeEnumeratedList(lexer);
                parent.add(list);
            } else if (itemEquals(DEFINITION_LIST, item)) {
                Element list = composeDefinitionList(lexer);
                parent.add(list);
            } else if (itemEquals(FIELD_LIST, item)) {
                Element list = composeFieldList(lexer);
                parent.add(list);
            } else {
                if (ERROR_MISSING_ITEM) {
                    throw new DocumentException("Unknow item type: " + item.getName()); 
                } else {
                    lexer.remove();
                }
            }
            item = lexer.peekTitleOrBodyElement();
        }
        return parent;
    }

    private Element composeBulletList(JRSTLexer lexer) throws IOException, DocumentException {
        Element item = lexer.peekBulletList();
        Element result = DocumentHelper.createElement(BULLET_LIST);
        copyLevel(item, result);        
        result.addAttribute("bullet", item.attributeValue("bullet"));
        while (itemEquals(BULLET_LIST, item) && isSameLevel(item, result) &&
                hasSameAttribute(item, result, "bullet")) {
            lexer.remove();
            Element bullet = result.addElement(LIST_ITEM);
            copyLevel(item, bullet);
            bullet.addElement(PARAGRAPH).appendContent(inline(item.getText()));
            composeBody(lexer, bullet);
            
            item = lexer.peekBulletList();
        }
        return result;
    }

    private Element composeEnumeratedList(JRSTLexer lexer) throws IOException, DocumentException {
        Element item = lexer.peekEnumeratedList();
        Element result = DocumentHelper.createElement(ENUMERATED_LIST);
        copyLevel(item, result);        
        result.addAttribute("start", item.attributeValue("start"));
        result.addAttribute("prefix", item.attributeValue("prefix"));
        result.addAttribute("suffix", item.attributeValue("suffix"));
        result.addAttribute("enumtype", item.attributeValue("enumtype"));
        while (itemEquals(ENUMERATED_LIST, item) && isSameLevel(item, result) &&
                hasSameAttribute(item, result, "prefix", "suffix") &&
                ("auto".equals(item.attributeValue("enumtype")) ||
                        hasSameAttribute(item, result, "enumtype"))) {
            lexer.remove();
            Element e = result.addElement(LIST_ITEM);
            copyLevel(item, e);
            e.addElement(PARAGRAPH).appendContent(inline(item.getText()));
            composeBody(lexer, e);
            
            item = lexer.peekEnumeratedList();
        }
        return result;
    }

    private Element composeDefinitionList(JRSTLexer lexer) throws IOException, DocumentException {
        Element item = lexer.peekBodyElement();
        Element result = DocumentHelper.createElement(DEFINITION_LIST);
        copyLevel(item, result);        
        while (itemEquals(DEFINITION_LIST, item) && isSameLevel(item, result)) {
            lexer.remove();
            Element def = result.addElement(DEFINITION_LIST_ITEM);
            copyLevel(item, def);        
            
            Element term = def.addElement(TERM);
            copyLevel(item, term);        
            term.appendContent(inline(item.attributeValue("term")));
            
            String [] classifiers = StringUtil.split(item.attributeValue("classifiers"), " : ");
            for (String classifierText : classifiers) {
                Element classifier = def.addElement("classifier");
                copyLevel(item, classifier);
                classifier.appendContent(inline(classifierText));
            }
            
            Element defintion = def.addElement(DEFINITION);
            defintion.addElement(PARAGRAPH).appendContent(inline(item.getText()));
            copyLevel(item, defintion);        
            
            composeBody(lexer, defintion);
            
            item = lexer.peekBodyElement();
        }
        return result;
    }

    private Element composeFieldList(JRSTLexer lexer) throws IOException, DocumentException {
        Element item = lexer.peekBodyElement();
        Element result = DocumentHelper.createElement(FIELD_LIST);
        copyLevel(item, result);        
        while (itemEquals(FIELD_LIST, item) && isSameLevel(item, result)) {
            Element field = composeFieldItemList(lexer);
            result.add(field);            
            item = lexer.peekBodyElement();
        }
        return result;
    }

    private Element composeFieldItemList(JRSTLexer lexer) throws IOException, DocumentException {
        Element item = lexer.peekFieldList();
        if (itemEquals(FIELD_LIST, item)) {
            lexer.remove();
            Element field = DocumentHelper.createElement(FIELD);
            copyLevel(item, field);        
            Element fieldName = field.addElement(FIELD_NAME);
            copyLevel(item, fieldName);        
            fieldName.appendContent(inline(item.attributeValue("name")));
            Element fieldBody = field.addElement(FIELD_BODY);
            fieldBody.addElement(PARAGRAPH).appendContent(inline(item.getText()));
            copyLevel(item, fieldBody);        
            composeBody(lexer, fieldBody);
            
            return field;
        } else {
            throw new DocumentException("Waiting for " + FIELD_LIST + " and found " + item.getName());
        }
    }

    
    
    /**
     * @param items
     * @return
     * @throws DocumentException 
     * @throws IOException 
     */
    private Element composeSection(JRSTLexer lexer) throws DocumentException, IOException {
        Element result = DocumentHelper.createElement(SECTION);
        Element firstTitle = null;
        
        Element item = null;
        
        // le titre de la section
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item, true, lexer.eof())) {
            lexer.remove();
            firstTitle = item; 
            Element title = result.addElement(TITLE);
            copyLevel(item, result);
            copyLevel(item, title);
            
            title.appendContent(inline(item.getText()));
        }
        
        // le contenu de la section
        item = lexer.peekTitle();
        while (itemNotEquals(TITLE, item) && !lexer.eof()) {
            composeBody(lexer, result);            
            item = lexer.peekTitle();
        }
        
        // les sous sections
        item = lexer.peekTitle();
        while (itemEquals(TITLE, item) 
                && isUpperLevel(item, firstTitle)) {
            Element section = composeSection(lexer);
            result.add(section);
            item = lexer.peekTitle();
        }
        
        return result;
    }

    /**
     * Indique si la sous section est bien une sous section, c-a-d dire
     * que son level est superieur a celui de la section
     * 
     * @param item
     * @param firstTitle
     * @return
     * @throws DocumentException 
     */
    private boolean isUpperLevel(Element subSection, Element section) throws DocumentException {
//        if (!(itemEquals(SECTION, subSection) &&  itemEquals(SECTION, section))
//                || itemEquals(DOCUMENT, section) || itemEquals(SECTION, section)) {
//            // all element is upper than Document or section
//            return true;
//        }
        int subSectionLevel = Integer.parseInt(subSection.attributeValue("level"));
        int sectionLevel = Integer.parseInt(section.attributeValue("level"));
        boolean result = subSectionLevel > sectionLevel;
        return result;
    }
    
    /**
     * Indique si les deux elements sont au meme niveau
     * 
     * @param item
     * @param firstTitle
     * @return
     * @throws DocumentException 
     */
    private boolean isSameLevel(Element subSection, Element section) throws DocumentException {
//        if (itemEquals(DOCUMENT, section) || itemEquals(SECTION, section)) {
//            // all element is upper than Document or section
//            return false;
//        }
        int subSectionLevel = Integer.parseInt(subSection.attributeValue("level"));
        int sectionLevel = Integer.parseInt(section.attributeValue("level"));
        boolean result = subSectionLevel == sectionLevel;
        return result;
    }
    
    private boolean hasSameAttribute(Element e1, Element e2, String ... attnames) {
        boolean result = true;
        for (String attname : attnames) {
            String a1 = e1.attributeValue(attname);
            String a2 = e2.attributeValue(attname);
            if (!ObjectUtils.equals(a1, a2)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void copyLevel(Element from, Element to) throws DocumentException {
        String level = from.attributeValue("level");
        if (level == null) {
            throw new DocumentException("Element without level: " + from);
        }
        to.addAttribute("level", level);
    }

    private boolean itemEquals(String name, Element e) throws DocumentException {
        boolean result = itemEquals(name, e, false, false);
        return result;
    }
    
    private boolean itemEquals(String name, Element e, boolean throwError, boolean eof) throws DocumentException {
        boolean result = e != null && name.equals(e.getName());
        if (ERROR_MISSING_ITEM && !result && throwError && !eof) {
            throw new DocumentException("Malformed document waiting " + name + " and found " + (e!=null?e.getName():"null"));
        }
        return result;
    }    
    
    private boolean itemNotEquals(String name, Element e) {
        boolean result = e == null || !name.equals(e.getName());
        return result;
    }    
    
    private Element inline(String text) throws DocumentException {
        text = StringEscapeUtils.escapeXml(text);
        
        // do all substitution inline
        text = REGEX_EMAIL.matcher(text).replaceAll("$1<"+REFERENCE+" refuri='mailto:$2'>$2</"+REFERENCE+">$3");        
        text = REGEX_STRONG.matcher(text).replaceAll("<"+STRONG+">$1</"+STRONG+">");        
        text = REGEX_EMPHASIS.matcher(text).replaceAll("<"+EMPHASIS+">$1</"+EMPHASIS+">");
        text = REGEX_REFERENCE.matcher(text).replaceAll("<"+REFERENCE+" refuri='$1'>$1</"+REFERENCE+">$2");
        
        // undo substitution in LITERAL
        Matcher matcher = REGEX_LITERAL.matcher(text);
        while(matcher.find()) {
            String start = text.substring(0, matcher.start());
            String end = text.substring(matcher.end());

            String literal = matcher.group(1);
            literal = literal.replaceAll("</?"+STRONG+"[^>]*>", "**");
            literal = literal.replaceAll("</?"+EMPHASIS+"[^>]*>", "**");
            text = start + "<"+LITERAL+">" + literal + "</"+LITERAL+">" + end;
        }
        
        Element result = DocumentHelper.parseText("<TMP>"+text+"</TMP>").getRootElement();
        
        return result;
    }
}


