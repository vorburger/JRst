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

import static org.codelutin.i18n.I18n._;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelutin.jrst.directive.DateDirective;
import org.codelutin.jrst.directive.ImageDirective;
import org.codelutin.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
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
 * {@link JRSTGeneratorTest}
 * 
 * <p>
 * Tous les elements ont un attribut level qui permet de savoir on il est dans
 * la hierarchie. Le Document a le level -1001, et les sections/titres
 * on pour level les valeurs 1000, -999, -998, ...
 * <p>
 * de cette facon les methods isUpperLevel et isSameLevel fonctionne pour
 * tous les elements de la meme facon
 * 
 * </pre>
 * abbreviation
 * acronym
 * address (done)
 * admonition (done)
 * attention (done)
 * attribution
 * author (done)
 * authors (done)
 * block_quote
 * bullet_list (done)
 * caption
 * caution (done)
 * citation
 * citation_reference
 * classifier (done)
 * colspec (done)
 * comment
 * compound
 * contact (done)
 * container
 * copyright (done)
 * danger (done)
 * date (done)
 * decoration
 * definition (done)
 * definition_list (done)
 * definition_list_item (done)
 * description (done)
 * docinfo (done)
 * doctest_block
 * document (done)
 * emphasis (done)
 * entry (done)
 * enumerated_list (done)
 * error (done)
 * field (done)
 * field_body (done)
 * field_list (done)
 * field_name (done)
 * figure
 * footer
 * footnote
 * footnote_reference
 * generated
 * header
 * hint (done)
 * image (done)
 * important (done)
 * inline
 * label
 * legend
 * line
 * line_block
 * list_item (done)
 * literal (done)
 * literal_block (done)
 * note (done)
 * option
 * option_argument
 * option_group
 * option_list
 * option_list_item
 * option_string
 * organization (done)
 * paragraph (done)
 * pending
 * problematic
 * raw
 * reference (partialy done)
 * revision (done)
 * row (done)
 * rubric
 * section (done)
 * sidebar
 * status (done)
 * strong (done)
 * subscript
 * substitution_definition
 * substitution_reference
 * subtitle (done)
 * superscript
 * system_message
 * table (done)
 * target
 * tbody (done)
 * term (done)
 * tgroup (done)
 * thead (done)
 * tip (done)
 * title (done)
 * title_reference
 * topic
 * transition (done)
 * version (done)
 * warning (done)
 * </pre>
 * 
 * @author poussin
 */
public class JRSTReader {
    
    /** to use log facility, just put in your code: log.info(\"...\"); */
    static private Log log = LogFactory.getLog(JRSTReader.class);

    boolean ERROR_MISSING_ITEM = false;
    static int MAX_SECTION_DEPTH = -1000;
    
    static protected Map<String, JRSTDirective> defaultDirectives = null;
    protected Map<String, JRSTDirective> directives = new HashMap<String, JRSTDirective>();
    
    static {
        defaultDirectives = new HashMap<String, JRSTDirective>();
        defaultDirectives.put(IMAGE, new ImageDirective());
        defaultDirectives.put(DATE, new DateDirective());
        defaultDirectives.put("time", new DateDirective());
        // TODO put here all other directive
    }
    
    /**
     * 
     */
    public JRSTReader() {
    }

    
    /**
     * @return the defaultDirectives
     */
    public static JRSTDirective getDefaultDirective(String name) {
        return defaultDirectives.get(name);
    }
    
    /**
     * @param defaultDirectives the defaultDirectives to set
     */
    public static void addDefaultDirectives(String name, JRSTDirective directive) {
        JRSTReader.defaultDirectives.put(name, directive);
    }
    
    /**
     * @return the defaultDirectives
     */
    public JRSTDirective getDirective(String name) {
        return directives.get(name);
    }
    
    /**
     * @param defaultDirectives the defaultDirectives to set
     */
    public void addDirectives(String name, JRSTDirective directive) {
        directives.put(name, directive);
    }
    
    /**
     * On commence par decouper tout le document en Element, puis on construit
     * l'article a partir de ces elements.
     * @param reader
     * @return
     * @throws IOException
     * @throws DocumentException 
     */
    public Document read(Reader reader) throws Exception {
        JRSTLexer lexer = new JRSTLexer(reader);
        try {            
            Element root = composeDocument(lexer);
            
            Document result = DocumentHelper.createDocument();
            result.setRootElement(root);
            
            // remove all level attribute
            root.accept(new VisitorSupport() {
                public void visit(Element e) {
                    e.addAttribute("level", null);
                    if ("true".equalsIgnoreCase(e.attributeValue("inline"))) {
                        e.addAttribute("inline", null);
                        try {
                            inline(e);
                        } catch (DocumentException eee) {
                            if (log.isWarnEnabled()) {
                                log.warn("Can't inline text for " + e, eee);
                            }
                        }
                    }
                }
            });
            
        
            return result;
        } catch (Exception eee) {
            log.error(_("JRST parsing error line {0} char {1}:\n{2}", lexer.getLineNumber(), lexer.getCharNumber(), lexer.readNotBlanckLine()));
            throw eee;
        }
    }

    /**
     * @param in
     * @param root
     * @throws Exception
     */
    private Element composeDocument(JRSTLexer lexer) throws Exception {
        Element result = DocumentHelper.createElement(DOCUMENT);
        result.addAttribute("level", String.valueOf(MAX_SECTION_DEPTH - 1));

        Element item = null;
        
        // skip blank line
        skipBlankLine(lexer);

        // le titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element title = result.addElement(TITLE);
            copyLevel(item, title);
            title.addAttribute("inline", "true").setText(item.getText());
        }
        
        // skip blank line
        skipBlankLine(lexer);
        
        // le sous titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element subtitle = result.addElement(SUBTITLE);
            copyLevel(item, subtitle);
            subtitle.addAttribute("inline", "true").setText(item.getText());
        }
        
        // skip blank line
        skipBlankLine(lexer);        
        
        // les infos du doc
        item = lexer.peekDocInfo();
        
        Element documentinfo = null;
        while (itemEquals(DOCINFO, item) || itemEquals(FIELD_LIST, item)) {
        	if (documentinfo == null) {
                documentinfo = result.addElement(DOCINFO);
            }
        	skipBlankLine(lexer);  
            if (itemEquals(FIELD_LIST, item)) {
                Element field = composeFieldItemList(lexer);
                documentinfo.add(field);
            } else {
                if ("author".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(AUTHOR).addAttribute("inline", "true").setText(item.getText());
                } else if ("date".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(DATE).addAttribute("inline", "true").setText(item.getText());
                } else if ("organization".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(ORGANIZATION).addAttribute("inline", "true").setText(item.getText());
                } else if ("contact".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(CONTACT).addAttribute("inline", "true").setText(item.getText());
                } else if ("address".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(ADDRESS).addAttribute("inline", "true").setText(item.getText());
                } else if ("version".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(VERSION).addAttribute("inline", "true").setText(item.getText());
                } else if ("revision".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(REVISION).addAttribute("inline", "true").setText(item.getText());
                } else if ("status".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(STATUS).addAttribute("inline", "true").setText(item.getText());
                } else if ("copyright".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(COPYRIGHT).addAttribute("inline", "true").setText(item.getText());
                } else if ("authors".equalsIgnoreCase(item.attributeValue("type"))){
                	Element authors = documentinfo.addElement(AUTHORS);
            		int t=0;
            		String line = item.getText();
            		for (int i=0;i<line.length();i++){
            			if (line.charAt(i)==';' || line.charAt(i)==','){
            				authors.addElement(AUTHOR).addAttribute("inline", "true").setText(line.substring(t,i).trim());
            				t=i+1;
            			}
            			
            		}
            		authors.addElement(AUTHOR).addAttribute("inline", "true").setText(line.substring(t,line.length()).trim());
    			}
                lexer.remove();
            }
            // skip blank line
            skipBlankLine(lexer);
            
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
	
	private void skipBlankLine(JRSTLexer lexer) throws IOException, DocumentException {
        Element item = lexer.peekBlankLine(); 
        // skip blank line
        while (itemEquals(JRSTLexer.BLANK_LINE, item)) {
        	// go to the next element
        	lexer.remove();
            item = lexer.peekBlankLine(); 
        }
	}


	/**
     * *@param root
     * @return
     * @throws DocumentException 
     * @throws IOException 
     */
    private Element composeBody(JRSTLexer lexer, Element parent) throws Exception {
    	
        Element item = lexer.peekTitleOrBodyElement();
        if (item == null && !lexer.eof()) {
            item = lexer.peekTitleOrBodyElement();
        }
        
        while (!lexer.eof() && itemNotEquals(TITLE, item) && isUpperLevel(item, parent)) {
            if (itemEquals(JRSTLexer.BLANK_LINE, item)) {
                // go to the next element
                lexer.remove();
            } else if (itemEquals(ADMONITION, item)) {
            	lexer.remove();
            	Element list = composeAdmonition(lexer, item);
            	parent.add(list);
            	
                
            } else if (itemEquals(PARAGRAPH, item)) {
                lexer.remove();
                Element para = parent.addElement(PARAGRAPH);
                copyLevel(item,para);
                para.addAttribute("inline", "true").setText(item.getText());
            } else if (itemEquals(JRSTLexer.DIRECTIVE, item)) {
                lexer.remove();
                Node directive = composeDirective(item);
                parent.add(directive);
            } else if (itemEquals(SUBSTITUTION_DEFINITION, item)) {
                lexer.remove();
                Element subst = composeSubstitutionDefinition(item);
                parent.add(subst);
            } else if (itemEquals(TRANSITION, item)) {
                lexer.remove();
                Element para = parent.addElement(TRANSITION);
                copyLevel(item, para);
            } else if (itemEquals(LITERAL_BLOCK, item)) {
                lexer.remove();
                Element para = parent.addElement(LITERAL_BLOCK);
                copyLevel(item, para);
                para.setText(item.getText());
            } else if (itemEquals(JRSTLexer.TABLE, item)) {
                lexer.remove();
                Element table = composeTable(lexer, item);
                parent.add(table);
                //                Element para = parent.addElement(TABLE);
//                copyLevel(item, para);
//                para.setText(item.getText());
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

            // Pour afficher le "PseudoXML"
            /*if (item!=null)
            	System.out.println(item.asXML());*/
            
            item = lexer.peekTitleOrBodyElement();
            
           
            
        }
        return parent;
    }

 
	private Element composeAdmonition(JRSTLexer lexer, Element item) throws Exception {
		Element result = null;
		if (item.attributeValue("type").equalsIgnoreCase("admonition")){
			result=DocumentHelper.createElement(ADMONITION);
			String title = item.attributeValue("title");
			String admonitionClass="admonition_"+title;
			admonitionClass=admonitionClass.toLowerCase().replaceAll("\\p{Punct}","");
			admonitionClass=admonitionClass.replace(' ', '-');
			admonitionClass=admonitionClass.replace('\n', '-');
    		result.addAttribute("class", admonitionClass);
    		Element eTitle= result.addElement("title");
    		title=title.trim();
    		JRSTReader reader = new JRSTReader();	// Le titre doit etre parsé
            Document doc = reader.read(new StringReader(title));
            eTitle.appendContent(doc.getRootElement());
			
		}
		else{
			result=DocumentHelper.createElement(item.attributeValue("type").toLowerCase());
		}
        JRSTReader reader = new JRSTReader();
        String text = item.getText();
        Document doc = reader.read(new StringReader(text));  // Ainsi que le txt
	    result.appendContent(doc.getRootElement());
	    return result;
	}


	/**
     * @param item
     * @return
     */
    private Node composeDirective(Element item) {
        Node result = item;
        String type = item.attributeValue(JRSTLexer.DIRECTIVE_TYPE);
        JRSTDirective directive = getDirective(type);
        if (directive == null) {
            directive = getDefaultDirective(type);
        }
        if (directive != null) {
            result = directive.parse(item);
        } else {
            log.warn("Unknow directive type '" + type + "' in: " + item);
        }
        return result;
    }


    /**
     * @param lexer
     * @param item
     * @return
     */
    private Element composeSubstitutionDefinition(Element item) {
        Element result = item;
        Element child = (Element)item.selectSingleNode("*");
        Node newChild = composeDirective(child);
        result.remove(child); // remove old after composeDirective, because directive can be used this parent
        result.add(newChild);
        return result;
    }


    /**
     * @param lexer
     * @param item
     * @return
     */
    private Element composeTable(JRSTLexer lexer, Element item) throws Exception {
    	
        Element result = DocumentHelper.createElement(TABLE);
        
        int tableWidth = Integer.parseInt(item.attributeValue(JRSTLexer.TABLE_WIDTH));
        
        TreeSet<Integer> beginCellList = new TreeSet<Integer>();
        
        for (Element cell : (List<Element>)item.selectNodes(JRSTLexer.ROW+"/"+JRSTLexer.CELL)) {
            Integer begin = Integer.valueOf(cell.attributeValue(JRSTLexer.CELL_INDEX_START)); 
            beginCellList.add(begin);
        }
        
        int [] beginCell = new int[beginCellList.size() + 1]; // + 1 to put table width to simulate new cell
        int [] lengthCell = new int[beginCellList.size()];

        int cellNumber = 0;
        for (int b : beginCellList) {
            beginCell[cellNumber] = b;
            if (cellNumber > 0) {
                lengthCell[cellNumber - 1] = beginCell[cellNumber] - beginCell[cellNumber - 1];
            }
            cellNumber++;
        }
        beginCell[cellNumber] = tableWidth;
        lengthCell[cellNumber - 1] = beginCell[cellNumber] - beginCell[cellNumber - 1];
        
        
        Element tgroup = result.addElement(TGROUP).addAttribute("cols", String.valueOf(cellNumber));
        for (int width : lengthCell) {
            tgroup.addElement(COLSPEC).addAttribute("colwidth", String.valueOf(width));
        }
        
        Element rowList = null;
        if ("true".equals(item.attributeValue(JRSTLexer.TABLE_HEADER))) {
            rowList = tgroup.addElement(THEAD);
        } else {
            rowList = tgroup.addElement(TBODY);
        }
        List<Element> rows = (List<Element>)item.selectNodes(JRSTLexer.ROW); 
        for (int r=0; r<rows.size(); r++) {
            Element row = rowList.addElement(ROW);
            List<Element> cells = (List<Element>)rows.get(r).selectNodes(JRSTLexer.CELL); 
            for (int c=0; c<cells.size(); c++) {
                Element cell = cells.get(c);
                // si la cellule a ete utilisé pour un regroupement vertical on la passe
                if (!"true".equals(cell.attributeValue("used"))) {
                    Element entry = row.addElement(ENTRY);
                    String text = "";
                    
                    // on regroupe les cellules verticalement
                    int morerows = -1;
                    Element tmpCell = null;
                    String cellStart = cell.attributeValue(JRSTLexer.CELL_INDEX_START);
                    do {
                        morerows++;
                        tmpCell = (Element)rows.get(r + morerows).selectSingleNode(
                                JRSTLexer.CELL+"[@"+JRSTLexer.CELL_INDEX_START+"="+cellStart+"]");
                        text += tmpCell.getText();
                        // on marque la cellule comme utilisé
                        tmpCell.addAttribute("used", "true");
                    } while (!"true".equals(tmpCell.attributeValue(JRSTLexer.CELL_END)));
                                 
                    if (morerows > 0) {
                        entry.addAttribute("morerows", String.valueOf(morerows));
                    }

                    // on compte le nombre de cellules regroupees horizontalement
                    int morecols = 0;
                    tmpCell = cells.get(c + morecols);
                    int cellEnd = Integer.parseInt(tmpCell.attributeValue(JRSTLexer.CELL_INDEX_END));
                    while (cellEnd + 1 != beginCell[c + morecols + 1]) {
                        morecols++;
//                        tmpCell = cells.get(c + morecols);
//                        cellEnd = Integer.parseInt(tmpCell.attributeValue(JRSTLexer.CELL_INDEX_END));                       
                    }
                    if (morecols > 0) {
                        entry.addAttribute("morecols", String.valueOf(morecols));
                    }
                    // parse entry text in table
                    JRSTReader reader = new JRSTReader();
                    Document doc = reader.read(new StringReader(text));
                    entry.appendContent(doc.getRootElement());
                }
            }
            if ("true".equals(rows.get(r).attributeValue(JRSTLexer.ROW_END_HEADER))) {
                rowList = tgroup.addElement(TBODY);
            }
        }
                
        return result;
    }

    private Element composeBulletList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekBulletList();
        Element result = DocumentHelper.createElement(BULLET_LIST);
        copyLevel(item, result);        
        result.addAttribute("bullet", item.attributeValue("bullet"));
        while (itemEquals(BULLET_LIST, item) && isSameLevel(item, result) &&
                hasSameAttribute(item, result, "bullet")) {
            lexer.remove();
            Element bullet = result.addElement(LIST_ITEM);
            copyLevel(item, bullet);
            bullet.addElement(PARAGRAPH).addAttribute("inline", "true").setText(item.getText());
            composeBody(lexer, bullet);
            
            item = lexer.peekBulletList();
        }
        return result;
    }

    private Element composeEnumeratedList(JRSTLexer lexer) throws Exception {
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
            e.addElement(PARAGRAPH).addAttribute("inline", "true").setText(item.getText());
            composeBody(lexer, e);
            
            item = lexer.peekEnumeratedList();
        }
        return result;
    }

    private Element composeDefinitionList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekBodyElement();
        Element result = DocumentHelper.createElement(DEFINITION_LIST);
        copyLevel(item, result);        
        while (itemEquals(DEFINITION_LIST, item) && isSameLevel(item, result)) {
            lexer.remove();
            Element def = result.addElement(DEFINITION_LIST_ITEM);
            copyLevel(item, def);        
            
            Element term = def.addElement(TERM);
            copyLevel(item, term);        
            term.addAttribute("inline", "true").setText(item.attributeValue("term"));
            
            String [] classifiers = StringUtil.split(item.attributeValue("classifiers"), " : ");
            for (String classifierText : classifiers) {
                Element classifier = def.addElement("classifier");
                copyLevel(item, classifier);
                classifier.addAttribute("inline", "true").setText(classifierText);
            }
            
            Element definition = def.addElement(DEFINITION);
            definition.addElement(PARAGRAPH).addAttribute("inline", "true").setText(item.getText());
            copyLevel(item, definition);        
            
            composeBody(lexer, definition);
            
            item = lexer.peekBodyElement();
        }
        return result;
    }

    private Element composeFieldList(JRSTLexer lexer) throws Exception {
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

    private Element composeFieldItemList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekFieldList();
        if (itemEquals(FIELD_LIST, item)) {
            lexer.remove();
            Element field = DocumentHelper.createElement(FIELD);
            copyLevel(item, field);        
            Element fieldName = field.addElement(FIELD_NAME);
            copyLevel(item, fieldName);        
            fieldName.addAttribute("inline", "true").setText(item.attributeValue("name"));
            Element fieldBody = field.addElement(FIELD_BODY);
            fieldBody.addElement(PARAGRAPH).addAttribute("inline", "true").setText(item.getText());
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
    private Element composeSection(JRSTLexer lexer) throws Exception {
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
            
            title.addAttribute("inline", "true").setText(item.getText());
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
    
    /**
     * Parse text in element and replace text with parse result 
     * @param text
     * @return
     * @throws DocumentException
     */
    private void inline(Element e) throws DocumentException {
        String text = e.getText();
        text = StringEscapeUtils.escapeXml(text);
        
        // search all LITERAL and replace it with special mark
        // this prevent substitution in literal, example **something** must not
        // change in literal
        Map<String, String> temporaries = new HashMap<String, String>();
        Matcher matcher = REGEX_LITERAL.matcher(text);
        int index = 0;
        while(matcher.find()) {
            int start =  matcher.start();
            int end = matcher.end();
            String literal = "<"+LITERAL+">" + matcher.group(1) + "</"+LITERAL+">";
            String key = "literal" + index++;            
            temporaries.put(key, literal);
            
            text = text.substring(0, start) + "``" + key + "``" + text.substring(end);
        }

        // search all REGEX_INLINE_REFERENCE and replace it with special mark
        // this prevent substitution of URL with REGEX_REFERENCE. Use same
        // mechanisme as literal for that
        matcher = REGEX_INLINE_REFERENCE.matcher(text);
        index = 0;
        while(matcher.find()) {
            int start =  matcher.start();
            int end = matcher.end();
            String reference = "<"+REFERENCE+" refuri='"+matcher.group(2)+"'>"+matcher.group(1)+"</"+REFERENCE+">";
            String key = "inlineReference" + index++;            
            temporaries.put(key, reference);
            
            text = text.substring(0, start) + "``" + key + "``" + text.substring(end);
        }

        
        // do all substitution inline
        text = REGEX_EMAIL.matcher(text).replaceAll("$1<"+REFERENCE+" refuri='mailto:$2'>$2</"+REFERENCE+">$3");        
        text = REGEX_STRONG.matcher(text).replaceAll("<"+STRONG+">$1</"+STRONG+">");        
        text = REGEX_EMPHASIS.matcher(text).replaceAll("<"+EMPHASIS+">$1</"+EMPHASIS+">");
        text = REGEX_REFERENCE.matcher(text).replaceAll("<"+REFERENCE+" refuri='$1'>$1</"+REFERENCE+">$2");
        
        // substitution reference
        matcher = REGEX_SUBSTITUTION_REFERENCE.matcher(text);
        int begin = 0;
        while(matcher.find(begin)) {
            String start = text.substring(0, matcher.start());
            String end = text.substring(matcher.end());
            String ref = matcher.group(1);

            Node subst = e.selectSingleNode(
                    "//"+SUBSTITUTION_DEFINITION+"[@name='"+ref+"']/child::node()");
            
            if (subst == null) {
                text = start + "|" + ref + "|";
            } else {
                text = start + subst.asXML();
            }
            
            begin = text.length();
            text += end;
            matcher = REGEX_SUBSTITUTION_REFERENCE.matcher(text);
        }
        
        // undo substitution in LITERAL
        matcher = REGEX_LITERAL.matcher(text);
        while(matcher.find()) {
            String start = text.substring(0, matcher.start());
            String end = text.substring(matcher.end());

            String tempKey = matcher.group(1);
            text = start + temporaries.get(tempKey) + end;
        }
        
        Element result = DocumentHelper.parseText("<TMP>"+text+"</TMP>").getRootElement();
        
        e.setText("");
        e.appendContent(result);
    }
}


