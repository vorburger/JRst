/* *##%
 * Copyright (C) 2006
 *     Code Lutin, CÃ©dric Pineau, Benjamin Poussin
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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.codelutin.i18n.I18n._;
import static org.codelutin.jrst.ReStructuredText.*;
import org.codelutin.jrst.directive.ContentDirective;
import org.codelutin.jrst.directive.DateDirective;
import org.codelutin.jrst.directive.ImageDirective;
import org.codelutin.jrst.directive.SectnumDirective;
import org.codelutin.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 
 * <pre> +--------------------------------------------------------------------+ |
 * document [may begin with a title, subtitle, decoration, docinfo] | |
 * +--------------------------------------+ | | sections [each begins with a
 * title] |
 * +-----------------------------+-------------------------+------------+ |
 * [body elements:] | (sections) | | | - literal | - lists | | - hyperlink
 * +------------+ | | blocks | - tables | | targets | | para- | - doctest | -
 * block | foot- | - sub. defs | | graphs | blocks | quotes | notes | - comments |
 * +---------+-----------+----------+-------+--------------+ | [text]+ | [text] |
 * (body elements) | [text] | | (inline
 * +-----------+------------------+--------------+ | markup) | +---------+
 * </pre>
 * 
 * 
 * Inline support: http://docutils.sourceforge.net/docs/user/rst/quickref.html
 * 
 * <li> STRUCTURAL ELEMENTS: document, section, topic, sidebar <li> STRUCTURAL
 * SUBELEMENTS: title, subtitle, decoration, docinfo, transition <li> docinfo:
 * address, author, authors, contact, copyright, date, field, organization,
 * revision, status, version <li> decoration: footer, header <li> BODY ELEMENTS:
 * admonition, attention, block_quote, bullet_list, caution, citation, comment,
 * compound, container, danger, definition_list, doctest_block, enumerated_list,
 * error, field_list, figure, footnote, hint, image, important, line_block,
 * literal_block, note, option_list, paragraph, pending, raw, rubric,
 * substitution_definition, system_message, table, target, tip, warning <li>
 * SIMPLE BODY ELEMENTS: comment, doctest_block, image, literal_block,
 * paragraph, pending, raw, rubric, substitution_definition, target <li>
 * COMPOUND BODY ELEMENTS: admonition, attention, block_quote, bullet_list,
 * caution, citation, compound, container, danger, definition_list,
 * enumerated_list, error, field_list, figure, footnote, hint, important,
 * line_block, note, option_list, system_message, table, tip, warning <li> BODY
 * SUBELEMENTS: attribution, caption, classifier, colspec, field_name, label,
 * line, option_argument, option_string, term definition, definition_list_item,
 * description, entry, field, field_body, legend, list_item, option,
 * option_group, option_list_item, row, tbody, tgroup, thead <li> INLINE
 * ELEMENTS: abbreviation, acronym, citation_reference, emphasis,
 * footnote_reference, generated, image, inline, literal, problematic,
 * reference, strong, subscript, substitution_reference, superscript, target,
 * title_reference, raw
 * 
 * <pre> DOCUMENT :: ( (title, subtitle?)?, decoration?, (docinfo,
 * transition?)?, STRUCTURE.MODEL; ) decoration :: (header?, footer?) header,
 * footer, definition, description, attention, caution, danger, error, hint,
 * important, note, tip, warning :: (BODY.ELEMENTS;)+ transition :: EMPTY
 * docinfo :: (BIBLIOGRAPHIC.ELEMENTS;)+ BIBLIOGRAPHIC.ELEMENTS :: author |
 * authors | organization | contact | address | version | revision | status |
 * date | copyright | field authors :: ( (author)+ ) field :: (field_name,
 * field_body) field_body, list_item :: (BODY.ELEMENTS;)* STRUCTURE.MODEL :: ( (
 * (BODY.ELEMENTS; | topic | sidebar)+, transition? )*, ( (section),
 * (transition?, (section) )* )? ) BODY.ELEMENTS :: paragraph | compound |
 * container | literal_block | doctest_block | line_block | block_quote | table |
 * figure | image | footnote | citation | rubric | bullet_list | enumerated_list |
 * definition_list | field_list | option_list | attention | caution | danger |
 * error | hint | important | note | tip | warning | admonition | reference |
 * target | substitution_definition | comment | pending | system_message | raw
 * topic :: (title?, (BODY.ELEMENTS;)+) sidebar :: (title, subtitle?,
 * (BODY.ELEMENTS; | topic)+) section :: (title, STRUCTURE.MODEL;) line_block ::
 * (line | line_block)+ block_quote:: ((BODY.ELEMENTS;)+, attribution?)
 * bullet_list, enumerated_list :: (list_item +) definition_list ::
 * (definition_list_item +) definition_list_item :: (term, classifier?,
 * definition) field_list :: (field +) option_list :: (option_list_item +)
 * option_list_item :: (option_string, option_argument *, description)
 * option_string, option_argument :: (#PCDATA) admonition :: (title,
 * (BODY.ELEMENTS;)+)
 * 
 * title, subtitle, author, organization, contact, address, version, revision,
 * status, date, copyright, field_name, paragraph, compound, container,
 * literal_block, doctest_block, attribution, line, term, classifier ::
 * TEXT.MODEL;
 * 
 * TEXT.MODEL :: (#PCDATA | INLINE.ELEMENTS;)* INLINE.ELEMENTS :: emphasis |
 * strong | literal | reference | footnote_reference | citation_reference |
 * substitution_reference | title_reference | abbreviation | acronym | subscript |
 * superscript | inline | problematic | generated | target | image | raw
 * emphasis :: '*' #PCDATA '*' strong :: '**' #PCDATA '**' literal :: '``'
 * #PCDATA '``' footnote_reference :: '[' ([0-9]+|#) ']' citation_reference ::
 * '[' [a-zA-Z]+ ']'
 * 
 * </pre>
 */

/**
 * * Le principe est d'utiliser les methodes peek* {@link JRSTLexer} pour
 * prendre l'element que l'on attend, si la methode retourne null ou un autre
 * element et bien c que ce n'est pas le bon choix, cela veut dire que l'element
 * courant est fini d'etre lu (plus de paragraphe dans la section par exemple)
 * ou qu'il y a une erreur dans le fichier en entre.
 * <p>
 * On construit un arbre XML representant le RST au fur et a mesure, on peut
 * ensuite appliquer une fueille de style ou autre chose avec
 * {@link JRSTGeneratorTest}
 * 
 * <p>
 * Tous les elements ont un attribut level qui permet de savoir on il est dans
 * la hierarchie. Le Document a le level -1001, et les sections/titres on pour
 * level les valeurs 1000, -999, -998, ...
 * <p>
 * de cette facon les methods isUpperLevel et isSameLevel fonctionne pour tous
 * les elements de la meme facon
 * 
 * <pre>
 *   abbreviation
 *   acronym
 *   address (done)
 *   admonition (done)
 *   attention (done)
 *   attribution
 *   author (done)
 *   authors (done)
 *   block_quote (done)
 *   bullet_list (done)
 *   caption
 *   caution (done)
 *   citation
 *   citation_reference
 *   classifier (done)
 *   colspec (done)
 *   comment
 *   compound
 *   contact (done)
 *   container
 *   copyright (done)
 *   danger (done)
 *   date (done)
 *   decoration (done)
 *   definition (done)
 *   definition_list (done)
 *   definition_list_item (done)
 *   description (done)
 *   docinfo (done)
 *   doctest_block (done)
 *   document (done)
 *   emphasis (done)
 *   entry (done)
 *   enumerated_list (done)
 *   error (done)
 *   field (done)
 *   field_body (done)
 *   field_list (done)
 *   field_name (done)
 *   figure
 *   footer (done)
 *   footnote	(done)
 *   footnote_reference (done)
 *   generated
 *   header (done)
 *   hint (done)
 *   image (done)
 *   important (done)
 *   inline
 *   label
 *   legend
 *   line (done)
 *   line_block (done)
 *   list_item (done)
 *   literal (done)
 *   literal_block (done)
 *   note (done)
 *   option (done)
 *   option_argument (done)
 *   option_group (done)
 *   option_list (done)
 *   option_list_item (done)
 *   option_string (done)
 *   organization (done)
 *   paragraph (done)
 *   pending
 *   problematic
 *   raw
 *   reference (done)
 *   revision (done)
 *   row (done)
 *   rubric
 *   section (done)
 *   sidebar (done)
 *   status (done)
 *   strong (done)
 *   subscript
 *   substitution_definition
 *   substitution_reference
 *   subtitle (done)
 *   superscript
 *   system_message
 *   table (done)
 *   target (done)
 *   tbody (done)
 *   term (done)
 *   tgroup (done)
 *   thead (done)
 *   tip (done)
 *   title (done)
 *   title_reference
 *   topic (done)
 *   transition (done)
 *   version (done)
 *   warning (done)
 * </pre>
 * 
 * @author poussin, letellier
 */
public class JRSTReader {

    /** to use log facility, just put in your code: log.info(\"...\"); */
    static private Log log = LogFactory.getLog(JRSTReader.class);

    boolean ERROR_MISSING_ITEM = false;

    static int MAX_SECTION_DEPTH = -1000;

    static protected Map<String, JRSTDirective> defaultDirectives = null;

    protected Map<String, JRSTDirective> directives = new HashMap<String, JRSTDirective>();

    private boolean sectnum = false;

    private Element footer;

    private int idMax = 0;

    private int symbolMax = 0;

    private int symbolMaxRef = 0;

    private LinkedList<Integer> lblFootnotes = new LinkedList<Integer>();

    private LinkedList<Integer> lblFootnotesRef = new LinkedList<Integer>();

    private LinkedList<Element> eFootnotes = new LinkedList<Element>();

    private LinkedList<Element> eTarget = new LinkedList<Element>();

    private LinkedList<Element> eTargetAnonymous = new LinkedList<Element>();

    private LinkedList<Element> eTargetAnonymousCopy = new LinkedList<Element>();

    private LinkedList<Element> eTitle = new LinkedList<Element>();

    static {
        defaultDirectives = new HashMap<String, JRSTDirective>();
        defaultDirectives.put(IMAGE, new ImageDirective());
        defaultDirectives.put(DATE, new DateDirective());
        defaultDirectives.put("time", new DateDirective());
        defaultDirectives.put("contents", new ContentDirective());
        // defaultDirectives.put("calc", new CalcDirective());
        defaultDirectives.put("sectnum", new SectnumDirective());
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
     * @param JRSTDirective the defaultDirectives to set
     */
    public void addDirectives(String name, JRSTDirective directive) {
        directives.put(name, directive);
    }

    /**
     * On commence par decouper tout le document en Element, puis on construit
     * l'article a partir de ces elements.
     * 
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

            root.accept(new VisitorSupport() {
                public void visit(Element e) {
                    // remove all level attribute
                    e.addAttribute("level", null);
                    // Constrution du sommaire
                    String type = e.attributeValue("type");
                    if (type != null) {
                        if (type.equals("contents")) {
                            composeContents(e);
                            e.addAttribute("type", null);
                        }
                    }

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
            log.error(_("JRST parsing error line {0} char {1}:\n{2}", lexer
                    .getLineNumber(), lexer.getCharNumber(), lexer
                    .readNotBlanckLine()));
            throw eee;
        }
    }

    /**
     * <p>
     * exemple :
     * </p>
     * 
     * <pre>
     * ..contents : Sommaire
     *   depth: 3
     * </pre>
     * 
     * <p>
     * depth sert a limiter la profondeur du sommaire
     * </p>
     * 
     * @param Element
     * 
     */
    private void composeContents(Element e) {
        Element result = DocumentHelper.createElement(TOPIC);
        String option = e.getText();
        int depth = -1;
        // depth: 3
        Pattern pattern = Pattern.compile("\\s*\\:depth\\:\\s*\\p{Digit}+");
        Matcher matcher = pattern.matcher(option);
        if (matcher.matches()) {
            pattern = Pattern.compile("\\p{Digit}+");
            matcher = pattern.matcher(matcher.group());
            if (matcher.find())
                depth = Integer.parseInt(matcher.group());
        }
        int levelInit = 0;
        try {
            levelInit = Integer.parseInt(eTitle.getFirst().attributeValue(
                    "level"));
        } catch (NumberFormatException eee) {
            log
                    .error(
                            "Can't parse level in: "
                                    + eTitle.getFirst().asXML(), eee);
            return;
        }

        LinkedList<Element> title = new LinkedList<Element>();
        // on rajoute les refid
        for (int i = 0; i < eTitle.size(); i++) {
            idMax++;
            eTitle.get(i).addAttribute("refid", "id" + idMax);
        }
        // on enleve les titres limites par depth
        for (Element el : eTitle) {
            int level = Integer.parseInt(el.attributeValue("level"));
            level = level - levelInit;
            el.addAttribute("level", "" + level);
            if (depth == -1)
                title.add(el);
            else {
                if (depth > level)
                    title.add(el);
            }
        }
        e.addAttribute("class", "contents");
        String titleValue = e.attributeValue("value");
        e.addAttribute("value", null);
        String value = titleValue.trim().toLowerCase();
        // sans titre c "contents" par default
        if (value.matches("\\s*")) {
            value = "contents";
            titleValue = "Contents";
        }
        e.addAttribute("id", value);
        e.addAttribute("name", value);
        result.addElement("title").setText(titleValue);
        // on compose les lignes
        result.add(composeLineContent(title, ""));
        e.setText("");
        e.appendContent(result);
    }

    /**
     * @param LinkedList <Element> title, String num
     * @return Element
     */
    private Element composeLineContent(LinkedList<Element> title, String num) {
        Element result = DocumentHelper.createElement(BULLET_LIST);
        if (sectnum)
            result.addAttribute("class", "auto-toc");
        Element item = null;
        int cnt = 0;
        while (!title.isEmpty()) {

            Element e = title.getFirst();
            int level = Integer.parseInt(e.attributeValue("level"));
            LinkedList<Element> child = new LinkedList<Element>();

            if (level <= 0) {
                cnt++;
                title.removeFirst();
                item = result.addElement(LIST_ITEM);
                Element para = item.addElement(PARAGRAPH);
                Element reference = para.addElement(REFERENCE);
                String text = e.getText();
                String id = e.attributeValue("refid");
                reference.addAttribute("id", id);
                reference.addAttribute("refid", text.replaceAll("\\W+", " ")
                        .trim().toLowerCase().replaceAll("\\W+", "-"));
                // si l'on doit les numeroter
                if (sectnum) {
                    Element generated = reference.addElement("generated")
                            .addAttribute("class", "sectnum");
                    generated.setText(num + cnt + "   ");
                    for (int i = 0; i < eTitle.size(); i++) {
                        if (eTitle.get(i).attributeValue("refid").equals(id)) {
                            Element generatedTitle = eTitle.get(i).addElement(
                                    "generated");
                            generatedTitle.addAttribute("class", "sectnum");
                            generatedTitle.setText(num + cnt + "   ");
                        }

                    }
                }
                reference.setText(text.replaceAll("_", "").trim());

            } else {
                do {
                    e.addAttribute("level", "" + (level - 1));
                    child.add(e);
                    title.removeFirst();
                    if (!title.isEmpty()) {
                        e = title.getFirst();
                        level = Integer.parseInt(e.attributeValue("level"));
                    }
                } while (!title.isEmpty() && level > 0);
                String numTmp = "";
                // numerotation
                if (sectnum) {
                    numTmp = num + cnt + ".";
                }
                if (item != null) {
                    item.add(composeLineContent(child, numTmp)); // Appel
                    // recursif
                } else {
                    result.add(composeLineContent(child, numTmp)); // Appel
                    // recursif
                }
            }
        }
        return result;
    }

    /**
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeDocument(JRSTLexer lexer) throws Exception {
        Element result = DocumentHelper.createElement(DOCUMENT);
        result.addAttribute("level", String.valueOf(MAX_SECTION_DEPTH - 1));

        Element item = null;

        // skip blank line
        skipBlankLine(lexer);

        // les liens anonymes
        LinkedList<Element> items = lexer.refTarget();
        for (Element e : items) {
            eTarget.add(e);

        }

        // le header
        item = lexer.peekHeader();
        if (itemEquals(HEADER, item)) {
            Element decoration = result.addElement(DECORATION);
            Element header = decoration.addElement(HEADER);
            header.addAttribute("inline", "true").setText(item.getText());
        }

        // le footer
        item = lexer.peekFooter();
        if (itemEquals(FOOTER, item)) {
            footer = DocumentHelper.createElement(DECORATION);
            Element header = footer.addElement(FOOTER);
            header.addAttribute("inline", "true").setText(item.getText());
        }

        // les hyperlinks
        LinkedList<Element> listItem = lexer.peekTargetAnonymous();
        if (listItem != null) {
            for (Element e : listItem) {
                Element anonym = DocumentHelper.createElement(TARGET);
                anonym.addAttribute("anonymous", "1");
                idMax++;
                anonym.addAttribute("id", "id" + idMax);
                anonym
                        .addAttribute("refuri", e.attributeValue("refuri")
                                .trim());
                eTargetAnonymous.add(anonym);
                eTargetAnonymousCopy.add(anonym);
            }
        }

        // les eléments a enlever (deja parser : header, footer...)
        item = lexer.peekRemove();
        if (itemEquals("remove", item))
            lexer.remove();

        // le titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element title = result.addElement(TITLE);
            String txt = item.getText();
            result.addAttribute("id", txt.replaceAll("[(\\W+)_]", " ")
                    .toLowerCase().trim().replaceAll("\\s+", "-"));
            result.addAttribute("name", txt.toLowerCase().replaceAll(
                    "[(\\W+)_&&[^\\:]]+", " ").trim());
            copyLevel(item, title);
            title.addAttribute("inline", "true").setText(txt.trim());
        }

        // skip blank line
        skipBlankLine(lexer);

        // le sous titre du doc
        item = lexer.peekTitle();
        if (itemEquals(TITLE, item)) {
            lexer.remove();
            Element subtitle = result.addElement(SUBTITLE);
            String txt = item.getText();
            subtitle.addAttribute("id", txt.replaceAll("[(\\W+)_]", " ")
                    .toLowerCase().trim().replaceAll("\\s+", "-"));
            subtitle.addAttribute("name", txt.toLowerCase().replaceAll(
                    "[(\\W+)_]", " ").trim());
            copyLevel(item, subtitle);
            DocumentHelper.createElement("footnotes");
            subtitle.addAttribute("inline", "true").setText(txt.trim());
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
                    documentinfo.addElement(AUTHOR).addAttribute("inline",
                            "true").setText(item.getText());
                } else if ("date".equalsIgnoreCase(item.attributeValue("type"))) {
                    documentinfo.addElement(DATE)
                            .addAttribute("inline", "true").setText(
                                    item.getText().trim());
                } else if ("organization".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    documentinfo.addElement(ORGANIZATION).addAttribute(
                            "inline", "true").setText(item.getText().trim());
                } else if ("contact".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    documentinfo.addElement(CONTACT).addAttribute("inline",
                            "true").setText(item.getText().trim());
                } else if ("address".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    documentinfo.addElement(ADDRESS).addAttribute("inline",
                            "true").setText(item.getText().trim());
                } else if ("version".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    documentinfo.addElement(VERSION).addAttribute("inline",
                            "true").setText(item.getText().trim());
                } else if ("revision".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    documentinfo.addElement(REVISION).addAttribute("inline",
                            "true").setText(item.getText().trim());
                } else if ("status".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    documentinfo.addElement(STATUS).addAttribute("inline",
                            "true").setText(item.getText().trim());
                } else if ("copyright".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    documentinfo.addElement(COPYRIGHT).addAttribute("inline",
                            "true").setText(item.getText().trim());
                } else if ("authors".equalsIgnoreCase(item
                        .attributeValue("type"))) {
                    Element authors = documentinfo.addElement(AUTHORS);
                    int t = 0;
                    String line = item.getText();
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ';' || line.charAt(i) == ',') {
                            authors.addElement(AUTHOR).addAttribute("inline",
                                    "true")
                                    .setText(line.substring(t, i).trim());
                            t = i + 1;
                        }

                    }
                    authors.addElement(AUTHOR).addAttribute("inline", "true")
                            .setText(line.substring(t, line.length()).trim());
                }
                lexer.remove();
            }
            // skip blank line
            // skipBlankLine(lexer);
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

        // on ajoute le footer a la fin
        if (footer != null)
            result.add(footer);

        return result;
    }

    /**
     * <p>
     * skip blank line
     * </p>
     * 
     * @param lexer
     * @throws DocumentException
     * @throws IOException
     */
    private void skipBlankLine(JRSTLexer lexer) throws IOException,
            DocumentException {
        Element item = lexer.peekBlankLine();
        // skip blank line
        while (itemEquals(JRSTLexer.BLANK_LINE, item)) {
            // go to the next element
            lexer.remove();
            item = lexer.peekBlankLine();
        }
    }

    /**
     * *
     * <p>
     * Corps du document
     * </p>
     * 
     * @param lexer
     * @param root
     * @return Element
     * @throws DocumentException
     * @throws IOException
     */
    private Element composeBody(JRSTLexer lexer, Element parent)
            throws Exception {

        Element item = lexer.peekTitleOrBodyElement();
        if (item == null && !lexer.eof()) {
            item = lexer.peekTitleOrBodyElement();
        }

        while (!lexer.eof() && itemNotEquals(TITLE, item)
                && isUpperLevel(item, parent)) {
            if (itemEquals(JRSTLexer.BLANK_LINE, item)) {
                // go t o the next element
                lexer.remove();
            } else if (itemEquals("remove", item)) {
                lexer.remove();
            } else if (itemEquals("include", item)) {
                lexer.remove();
                Element list = composeInclude(item);
                parent.add(list);
            } else if (itemEquals(DOCTEST_BLOCK, item)) {
                lexer.remove();
                Element list = composeDoctestBlock(item);
                parent.add(list);
            } else if (itemEquals(ADMONITION, item)) {
                lexer.remove();
                Element list = composeAdmonition(item);
                parent.add(list);
            } else if (itemEquals(SIDEBAR, item)) {
                lexer.remove();
                Element list = composeSidebar(item);
                parent.add(list);
            } else if (itemEquals(TOPIC, item)) {
                lexer.remove();
                Element list = composeTopic(item);
                parent.add(list);
            } else if (itemEquals(TRANSITION, item)) {
                lexer.remove();
                Element para = parent.addElement(TRANSITION);
                copyLevel(item, para);
            } else if (itemEquals(PARAGRAPH, item)) {
                lexer.remove();
                Element para = parent.addElement(PARAGRAPH);
                copyLevel(item, para);
                para.addAttribute("inline", "true").setText(item.getText());
            } else if (itemEquals(JRSTLexer.DIRECTIVE, item)) {
                lexer.remove();
                Node directive = composeDirective(item);
                parent.add(directive);
            } else if (itemEquals(SUBSTITUTION_DEFINITION, item)) {
                lexer.remove();
                Element subst = composeSubstitutionDefinition(item);
                parent.add(subst);
            } else if (itemEquals(LITERAL_BLOCK, item)) {
                lexer.remove();
                Element para = parent.addElement(LITERAL_BLOCK);
                copyLevel(item, para);
                para.setText(item.getText());
            } else if (itemEquals(JRSTLexer.TABLE, item)) {
                lexer.remove();
                Element table = composeTable(item);
                parent.add(table);
                // Element para = parent.addElement(TABLE);
                // copyLevel(item, para);
                // para.setText(item.getText());
            } else if (itemEquals(LINE_BLOCK, item)) {
                lexer.remove();
                Element list = composeLineBlock(lexer, item);
                parent.add(list);
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
            } else if (itemEquals(BLOCK_QUOTE, item)) {
                lexer.remove();
                Element list = composeBlockQuote(item);
                parent.add(list);
            } else if (itemEquals(OPTION_LIST, item)) {
                Element list = composeOptionList(lexer);
                parent.add(list);
            } else if (itemEquals(TARGET, item)) {
                lexer.remove();
                Element list = composeTarget(item);
                parent.add(list);
            } else if (itemEquals("targetAnonymous", item)) {
                lexer.remove();
                Element list = composeTargetAnonymous(item);
                parent.add(list);
            } else if (itemEquals("footnotes", item)) {
                lexer.remove();
                Element[] list = composeFootnote(item);
                for (Element l : list)
                    parent.add(l);
            } else if (itemEquals(COMMENT, item)) {
                lexer.remove();
                Element list = composeComment(item);
                parent.add(list);
            }

            else {
                if (ERROR_MISSING_ITEM) {
                    throw new DocumentException("Unknow item type: "
                            + item.getName());
                } else {
                    lexer.remove();
                }
            }

            // Pour afficher le "PseudoXML"
            // if (item!=null) System.out.println(item.asXML());

            item = lexer.peekTitleOrBodyElement();
        }

        return parent;
    }

    /**
     * <p>
     * include un document rst
     * </p>
     * 
     * <pre>
     * .. include:: doc.rst
     * </pre>
     * 
     * <p>
     * include un document literal (code...)
     * </p>
     * 
     * <pre>
     * .. include:: literal
     *       doc.rst
     * </pre>
     * 
     * @param item
     * @return Element
     * @throws Exception
     */
    private Element composeInclude(Element item) throws Exception {
        String option = item.attributeValue("option");
        String path = item.getText();
        Element result = null;
        if (option.equals("literal")) {
            result = DocumentHelper.createElement(LITERAL_BLOCK);
            FileReader reader = new FileReader(path);
            BufferedReader bf = new BufferedReader(reader);
            String line = "";
            String lineTmp = bf.readLine();
            while (lineTmp != null) {
                line += '\n' + lineTmp;
                lineTmp = bf.readLine();
            }
            result.setText(line);
        } else {
            File fileIn = new File(path);
            URL url = fileIn.toURL();
            Reader in = new InputStreamReader(url.openStream());

            Document doc = newJRSTReader(in);

            result = doc.getRootElement();
        }
        return result;
    }

    /**
     * <pre>
     * ..
     *   So this block is not &quot;lost&quot;,
     *   despite its indentation.
     * </pre>
     * 
     * @param item
     * @return Element
     */
    private Element composeComment(Element item) {

        return item;
    }

    /**
     * <pre>
     * __ http://truc.html
     * </pre>
     * 
     * @param item
     * @return Element
     */
    private Element composeTargetAnonymous(Element item) {
        Element result = null;
        result = eTargetAnonymousCopy.getFirst();
        eTargetAnonymousCopy.removeFirst();
        return result;
    }

    /**
     * <pre _ target: target.html </pre>
     * 
     * @param item
     * @return Element
     */
    private Element composeTarget(Element item) {
        Element result = null;
        for (Element e : eTarget) {
            if (e.attributeValue("id").equals(item.attributeValue("id"))) {
                result = e;
            }
        }
        return result;
    }

    /**
     * <pre>
     * .. [#] This is a footnote
     * </pre>
     * 
     * @param item
     * @return Element
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Element[] composeFootnote(Element item) throws Exception {
        Element[] result = null;
        if (itemEquals("footnotes", item)) {
            List<Element> footnotes = (List<Element>) item
                    .selectNodes(FOOTNOTE);
            result = new Element[footnotes.size()];
            int cnt = 0;
            for (Element footnote : footnotes) {
                result[cnt] = DocumentHelper.createElement(FOOTNOTE);
                Element efootnote = DocumentHelper.createElement(FOOTNOTE);
                int labelMax = 0;

                for (int i = 0; i < lblFootnotes.size(); i++) {
                    int lbl = (Integer) lblFootnotes.get(i);
                    labelMax = Math.max(lbl, labelMax);
                }

                boolean[] labels = new boolean[labelMax];
                for (int i = 0; i < labels.length; i++)
                    labels[i] = false;
                for (int i = 0; i < lblFootnotes.size(); i++) {
                    labels[(Integer) lblFootnotes.get(i) - 1] = true;
                }
                idMax++;
                String name = null;
                String id = "";
                String label = null;
                String type = footnote.attributeValue("type");
                if (type.equals("autoNum") || type.equals("autoNumLabel")) {
                    result[cnt].addAttribute("auto", "1");
                }
                if (type.equals("autoSymbol")) {
                    result[cnt].addAttribute("auto", "*");
                }
                result[cnt].addAttribute("backrefs", "id" + idMax);
                efootnote.addAttribute("backrefs", "id" + idMax);
                if (type.equals("num") || type.equals("autoNumLabel")) {
                    name = footnote.attributeValue("name");
                    if (type.equals("autoNumLabel"))
                        id = name;
                    else
                        label = name;
                }
                if (type.equals("autoNum") || type.equals("autoNumLabel")) {
                    boolean done = false;

                    for (int i = 0; i < labels.length && !done; i++) {
                        if (!labels[i]) {
                            done = true;
                            label = "" + (i + 1);
                        }
                    }
                    if (!done)
                        label = "" + (labels.length + 1);
                    if (type.equals("autoNum"))
                        name = label;
                }
                if (type.equals("autoSymbol")) {

                    int nb = Math.abs(symbolMax / 10) + 1;
                    char symbol = FOOTNOTE_SYMBOL.charAt(symbolMax % 10);
                    label = "";
                    for (int j = 0; j < nb; j++) {
                        label += symbol;
                    }
                    symbolMax++;

                }
                result[cnt].addAttribute("id", "" + id);
                efootnote.addAttribute("id", "" + id);
                if (!type.equals("autoSymbol")) {
                    result[cnt].addAttribute("name", "" + name);
                    efootnote.addAttribute("name", "" + name);
                }
                result[cnt].addElement("label").setText("" + label);
                efootnote.addAttribute("label", "" + label);
                if (!type.equals("autoSymbol"))
                    lblFootnotes.add(Integer.parseInt(label));
                efootnote.addAttribute("type", type);
                eFootnotes.add(efootnote);
                String text = footnote.getText();
                Document doc = newJRSTReader(new StringReader(text));
                result[cnt].appendContent(doc.getRootElement());

                cnt++;
            }
        }
        for (int i = 0; i < result.length; i++) {
            if (result[i].attributeValue("id").equals("")) {
                idMax++;
                result[i].addAttribute("id", "id" + idMax);
                ((Element) eFootnotes.get(i)).addAttribute("id", "id" + idMax);
            }

        }

        return result;
    }

    /**
     * <pre>
     * -a command-line option &quot;a&quot; -1 file, --one=file, --two file Multiple
     * options with arguments.
     * </pre>
     * 
     * @param lexer
     * @return Element
     * @throws Exception
     * @throws DocumentException
     */
    @SuppressWarnings("unchecked")
    private Element composeOptionList(JRSTLexer lexer)
            throws DocumentException, Exception {
        Element item = lexer.peekOption();
        Element result = DocumentHelper.createElement(OPTION_LIST);
        while (itemEquals(OPTION_LIST, item)) {
            lexer.remove();
            Element optionListItem = result.addElement(OPTION_LIST_ITEM);
            Element optionGroup = optionListItem.addElement(OPTION_GROUP);
            List<Element> option = (List<Element>) item.selectNodes("option");
            for (Element e : option) {
                Element eOption = optionGroup.addElement(OPTION);
                eOption.addElement(OPTION_STRING).setText(
                        e.attributeValue(OPTION_STRING));
                if (e.attributeValue("delimiterExiste").equals("true")) {
                    eOption.addElement(OPTION_ARGUMENT).addAttribute(
                            "delimiter", e.attributeValue("delimiter"))
                            .setText(e.attributeValue(OPTION_ARGUMENT));
                }
            }
            Element description = optionListItem.addElement(DESCRIPTION);

            String text = item.getText();
            Document doc = newJRSTReader(new StringReader(text));
            description.appendContent(doc.getRootElement());

            item = lexer.peekOption();
        }
        return result;
    }

    /**
     * <pre>
     * .. topic:: Title
     * 
     *    Body.
     * </pre>
     * 
     * @param Element item
     * @return Element
     * @throws Exception
     */

    private Element composeTopic(Element item) throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(TOPIC);
        result.addElement(TITLE).addAttribute("inline", "true").setText(
                item.attributeValue(TITLE));
        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());

        return result;
    }

    /**
     * <pre>
     * .. sidebar:: Title
     *    :subtitle: If Desired
     *    
     *    Body.
     * </pre>
     * 
     * @param Element
     * @return Element
     * @throws Exception
     */

    private Element composeSidebar(Element item) throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(SIDEBAR);
        result.addElement(TITLE).addAttribute("inline", "true").setText(
                item.attributeValue(TITLE));
        if (item.attributeValue("subExiste").equals("true"))
            result.addElement(SUBTITLE).addAttribute("inline", "true").setText(
                    item.attributeValue(SUBTITLE));

        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());

        return result;
    }

    /**
     * <pre>
     * | line block
     * |
     * |    indent
     * </pre>
     * 
     * @param lexer
     * @param item
     * @return Element
     * @throws Exception
     */

    @SuppressWarnings("unchecked")
    private Element composeLineBlock(JRSTLexer lexer, Element item)
            throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(LINE_BLOCK);
        List<Element> lines = (List<Element>) item.selectNodes(LINE);
        int[] levels = new int[lines.size()];
        int cnt = 0;
        for (Element l : lines) {
            levels[cnt] = Integer.parseInt(l.attributeValue("level"));
            cnt++;
        }
        cnt = 0;
        boolean[] lineDone = new boolean[lines.size()];
        for (int i = 0; i < lineDone.length; i++)
            lineDone[i] = false;
        for (Element l : lines) {
            if (levels[cnt] == 0)
                result.addElement(LINE).addAttribute("inline", "true").setText(
                        l.getText());
            else {
                if (!lineDone[cnt]) {
                    Element newItem = DocumentHelper.createElement(LINE_BLOCK);
                    Boolean done = false;
                    for (int i = cnt; i < lines.size() && !done; i++) {
                        if (levels[i] > 0) {
                            Element eLine = newItem.addElement("line");
                            eLine.addAttribute("level", "" + (levels[i] - 1));
                            eLine.setText(lines.get(i).getText());
                            lineDone[i] = true;
                        } else
                            done = true;

                    }
                    Element eLineBlock = result.addElement(LINE_BLOCK);
                    // Appel recursif
                    eLineBlock.appendContent(composeLineBlock(lexer, newItem));
                }
            }
            cnt++;

        }
        return result;
    }

    /**
     * <pre>
     * &gt;&gt;&gt; print 'this is a Doctest block'
     * this is a Doctest block
     * </pre>
     * 
     * @param Element
     * @return Element
     */
    private Element composeDoctestBlock(Element item) {
        return item;
    }

    /**
     * <pre>
     * As a great paleontologist once said,
     * 
     *      This theory, that is mine, is mine.
     * 
     *      -- Anne Elk (Miss)
     * </pre>
     * 
     * @param Element
     * @return Element
     * @throws Exception
     * 
     */

    private Element composeBlockQuote(Element item) throws Exception {
        Element result = null;
        result = DocumentHelper.createElement(BLOCK_QUOTE);

        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());
        String sAttribution = item.attributeValue(ATTRIBUTION);
        if (sAttribution != null) {
            Element attribution = result.addElement(ATTRIBUTION);
            attribution.setText(sAttribution);
            attribution.addAttribute("inline", "true");
        }
        return result;
    }

    /**
     * <pre>
     * .. admonition:: And, by the way...
     * 
     *      You can make up your own admonition too.
     * </pre>
     * 
     * @param Element
     * @return Element
     * @throws Exception
     * 
     */
    private Element composeAdmonition(Element item) throws Exception {
        Element result = null;
        if (item.attributeValue("type").equalsIgnoreCase(ADMONITION)) {
            result = DocumentHelper.createElement(ADMONITION);
            String title = item.attributeValue(TITLE);
            String admonitionClass = "admonition_" + title;
            admonitionClass = admonitionClass.toLowerCase().replaceAll(
                    "\\p{Punct}", "");
            admonitionClass = admonitionClass.replace(' ', '-');
            admonitionClass = admonitionClass.replace('\n', '-');
            result.addAttribute("class", admonitionClass);
            result.addElement(TITLE).addAttribute("inline", "true").setText(
                    title.trim());
        } else
            result = DocumentHelper.createElement(item.attributeValue("type")
                    .toLowerCase());

        String text = item.getText();
        Document doc = newJRSTReader(new StringReader(text));
        result.appendContent(doc.getRootElement());
        return result;
    }

    /**
     * parse all directives
     * 
     * @param Element
     * @return Node
     */
    private Node composeDirective(Element item) {
        Node result = item;
        String type = item.attributeValue(JRSTLexer.DIRECTIVE_TYPE);
        if (type.equals("sectnum"))
            sectnum = true;
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
     * <pre>
     * .. |biohazard| image:: biohazard.png
     * </pre>
     * 
     * @param Element
     * @return Element
     */
    private Element composeSubstitutionDefinition(Element item) {
        Element result = item;
        Element child = (Element) item.selectSingleNode("*");
        Node newChild = composeDirective(child);
        result.remove(child); // remove old after composeDirective, because
        // directive can be used this parent
        result.add(newChild);
        return result;
    }

    /**
     * <p>
     * Complexe Table
     * </p>
     * 
     * <pre>
     * +------------------------+------------+---------------------+
     * | body row 3             | Cells may  | - Table cells       |
     * +------------------------+ span rows. | - contain           |
     * | body row 4             |            | - body elements.    |
     * +------------------------+------------+---------------------+
     * </pre>
     * 
     * <p>
     * And simple table
     * </p>
     * 
     * <pre>
     * =====  =====  ======
     *    Inputs     Output
     * ============  ======
     *   A      B    A or B
     * ------------  ------
     *   A      B    A or B
     * =====  =====  ======
     * </pre>
     * 
     * @param Element
     * @return Element
     * 
     */
    @SuppressWarnings("unchecked")
    private Element composeTable(Element item) throws Exception {

        Element result = DocumentHelper.createElement(TABLE);

        int tableWidth = Integer.parseInt(item
                .attributeValue(JRSTLexer.TABLE_WIDTH));

        TreeSet<Integer> beginCellList = new TreeSet<Integer>();

        for (Element cell : (List<Element>) item.selectNodes(JRSTLexer.ROW
                + "/" + JRSTLexer.CELL)) {
            Integer begin = Integer.valueOf(cell
                    .attributeValue(JRSTLexer.CELL_INDEX_START));
            beginCellList.add(begin);
        }

        int[] beginCell = new int[beginCellList.size() + 1]; // + 1 to put
        // table width
        // to simulate
        // new cell
        int[] lengthCell = new int[beginCellList.size()];

        int cellNumber = 0;
        for (int b : beginCellList) {
            beginCell[cellNumber] = b;
            if (cellNumber > 0) {
                lengthCell[cellNumber - 1] = beginCell[cellNumber]
                        - beginCell[cellNumber - 1];
            }
            cellNumber++;
        }
        beginCell[cellNumber] = tableWidth;
        lengthCell[cellNumber - 1] = beginCell[cellNumber]
                - beginCell[cellNumber - 1];

        Element tgroup = result.addElement(TGROUP).addAttribute("cols",
                String.valueOf(cellNumber));
        for (int width : lengthCell) {
            tgroup.addElement(COLSPEC).addAttribute("colwidth",
                    String.valueOf(width));
        }

        Element rowList = null;
        if ("true".equals(item.attributeValue(JRSTLexer.TABLE_HEADER))) {
            rowList = tgroup.addElement(THEAD);
        } else {
            rowList = tgroup.addElement(TBODY);
        }
        List<Element> rows = (List<Element>) item.selectNodes(JRSTLexer.ROW);
        for (int r = 0; r < rows.size(); r++) {
            Element row = rowList.addElement(ROW);
            List<Element> cells = (List<Element>) rows.get(r).selectNodes(
                    JRSTLexer.CELL);
            for (int c = 0; c < cells.size(); c++) {
                Element cell = cells.get(c);
                // si la cellule a ete utilise pour un regroupement vertical on
                // la passe
                if (!"true".equals(cell.attributeValue("used"))) {
                    Element entry = row.addElement(ENTRY);
                    String text = "";

                    // on regroupe les cellules verticalement
                    int morerows = -1;
                    Element tmpCell = null;
                    String cellStart = cell
                            .attributeValue(JRSTLexer.CELL_INDEX_START);
                    do {
                        morerows++;
                        tmpCell = (Element) rows.get(r + morerows)
                                .selectSingleNode(
                                        JRSTLexer.CELL + "[@"
                                                + JRSTLexer.CELL_INDEX_START
                                                + "=" + cellStart + "]");
                        text += tmpCell.getText();
                        // on marque la cellule comme utilise
                        tmpCell.addAttribute("used", "true");
                    } while (!"true".equals(tmpCell
                            .attributeValue(JRSTLexer.CELL_END)));

                    if (morerows > 0) {
                        entry
                                .addAttribute("morerows", String
                                        .valueOf(morerows));
                    }

                    // on compte le nombre de cellules regroupees
                    // horizontalement
                    int morecols = 0;
                    tmpCell = cells.get(c + morecols);
                    int cellEnd = Integer.parseInt(tmpCell
                            .attributeValue(JRSTLexer.CELL_INDEX_END));
                    while (cellEnd + 1 != beginCell[c + morecols + 1]) {
                        morecols++;
                        // tmpCell = cells.get(c + morecols);
                        // cellEnd =
                        // Integer.parseInt(tmpCell.attributeValue(JRSTLexer.
                        // CELL_INDEX_END));
                    }
                    if (morecols > 0) {
                        entry
                                .addAttribute("morecols", String
                                        .valueOf(morecols));
                    }
                    // parse entry text in table
                    Document doc = newJRSTReader(new StringReader(text));
                    entry.appendContent(doc.getRootElement());
                }
            }
            if ("true".equals(rows.get(r).attributeValue(
                    JRSTLexer.ROW_END_HEADER))) {
                rowList = tgroup.addElement(TBODY);
            }
        }

        return result;
    }

    /**
     * <p>
     * items begin with "-", "+", or "*"
     * </p>
     * 
     * <pre>
     * * aaa
     *   - bbb
     * * ccc
     *   - ddd
     *      + eee
     * </pre>
     * 
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeBulletList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekBulletList();
        Element result = DocumentHelper.createElement(BULLET_LIST);
        copyLevel(item, result);
        result.addAttribute("bullet", item.attributeValue("bullet"));
        while (itemEquals(BULLET_LIST, item) && isSameLevel(item, result)
                && hasSameAttribute(item, result, "bullet")) {
            lexer.remove();
            Element bullet = result.addElement(LIST_ITEM);
            copyLevel(item, bullet);
            bullet.addElement(PARAGRAPH).addAttribute("inline", "true")
                    .setText(item.getText());
            composeBody(lexer, bullet);

            item = lexer.peekBulletList();
        }
        return result;
    }

    /**
     * <pre>
     * 3. et meme
     * * #. pour voir
     * * I) de tout
     * (a) pour tout
     * (#) vraiment tout
     * </pre>
     * 
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeEnumeratedList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekEnumeratedList();
        Element result = DocumentHelper.createElement(ENUMERATED_LIST);
        copyLevel(item, result);
        String enumType = item.attributeValue("enumtype");
        if (!enumType.equals("arabic"))
            result.addAttribute("start", item.attributeValue("start"));
        result.addAttribute("prefix", item.attributeValue("prefix"));
        result.addAttribute("suffix", item.attributeValue("suffix"));
        result.addAttribute("enumtype", enumType);
        while (itemEquals(ENUMERATED_LIST, item)
                && isSameLevel(item, result)
                && hasSameAttribute(item, result, "prefix", "suffix")
                && ("auto".equals(item.attributeValue("enumtype")) || hasSameAttribute(
                        item, result, "enumtype"))) {
            lexer.remove();
            Element e = result.addElement(LIST_ITEM);
            copyLevel(item, e);
            e.addElement(PARAGRAPH).addAttribute("inline", "true").setText(
                    item.getText());
            composeBody(lexer, e);

            item = lexer.peekEnumeratedList();
        }
        return result;
    }

    /**
     * <pre>
     * le mot : la classe
     *   la definition
     * </pre>
     * 
     * @param lexer
     * @return Element
     * @throws Exception
     */
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
            term.addAttribute("inline", "true").setText(
                    item.attributeValue("term"));

            String[] classifiers = StringUtil.split(item
                    .attributeValue("classifiers"), " : ");
            for (String classifierText : classifiers) {
                Element classifier = def.addElement("classifier");
                copyLevel(item, classifier);
                classifier.addAttribute("inline", "true").setText(
                        classifierText);
            }

            Element definition = def.addElement(DEFINITION);
            definition.addElement(PARAGRAPH).addAttribute("inline", "true")
                    .setText(item.getText());
            copyLevel(item, definition);

            composeBody(lexer, definition);

            item = lexer.peekBodyElement();
        }
        return result;
    }

    /**
     * <pre>
     * :un peu: de field
     *      ca ne fait pas
     *      de mal
     * </pre>
     * 
     * @param lexer
     * @return Element
     * @throws Exception
     */
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

    /**
     * <pre>
     * :field1: avec un
     *    petit texte 
     *    - et meme un 
     *    - debut 
     *    - de list
     * </pre>
     * 
     * @param lexer
     * @return Element
     * @throws Exception
     */
    private Element composeFieldItemList(JRSTLexer lexer) throws Exception {
        Element item = lexer.peekFieldList();
        if (itemEquals(FIELD_LIST, item)) {
            lexer.remove();
            Element field = DocumentHelper.createElement(FIELD);
            copyLevel(item, field);
            Element fieldName = field.addElement(FIELD_NAME);
            copyLevel(item, fieldName);
            fieldName.addAttribute("inline", "true").setText(
                    item.attributeValue("name"));
            Element fieldBody = field.addElement(FIELD_BODY);
            fieldBody.addElement(PARAGRAPH).addAttribute("inline", "true")
                    .setText(item.getText());
            copyLevel(item, fieldBody);
            composeBody(lexer, fieldBody);

            return field;
        } else {
            throw new DocumentException("Waiting for " + FIELD_LIST
                    + " and found " + item.getName());
        }
    }

    /**
     * <pre>
     * DEFINITIONS
     * -----------
     * </pre>
     * 
     * @param lexer
     * @return Element
     * @throws Exception
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
            title.addAttribute("inline", "true").setText(item.getText().trim());
            result.addAttribute("id", item.getText().replaceAll("\\W+", " ")
                    .trim().toLowerCase().replaceAll("\\W+", "-"));
            result.addAttribute("name", item.getText().toLowerCase().trim());
            eTitle.add(title);
        }

        // le contenu de la section
        item = lexer.peekTitle();
        while (itemNotEquals(TITLE, item) && !lexer.eof()) {
            composeBody(lexer, result);
            item = lexer.peekTitle();
        }

        // les sous sections
        item = lexer.peekTitle();
        while (itemEquals(TITLE, item) && isUpperLevel(item, firstTitle)) {
            Element section = composeSection(lexer);
            result.add(section);
            item = lexer.peekTitle();
        }

        return result;
    }

    /**
     * Indique si la sous section est bien une sous section, c-a-d dire que son
     * level est superieur a celui de la section
     * 
     * @param item
     * @param firstTitle
     * @return boolean
     * @throws DocumentException
     */
    private boolean isUpperLevel(Element subSection, Element section)
            throws DocumentException {
        // if (!(itemEquals(SECTION, subSection) && itemEquals(SECTION,
        // section))
        // || itemEquals(DOCUMENT, section) || itemEquals(SECTION, section)) {
        // // all element is upper than Document or section
        // return true;
        // }
        int subSectionLevel = Integer.parseInt(subSection
                .attributeValue("level"));
        int sectionLevel = Integer.parseInt(section.attributeValue("level"));
        boolean result = subSectionLevel > sectionLevel;
        return result;
    }

    /**
     * Indique si les deux elements sont au meme niveau
     * 
     * @param item
     * @param firstTitle
     * @return boolean
     * @throws DocumentException
     */
    private boolean isSameLevel(Element subSection, Element section)
            throws DocumentException {
        // if (itemEquals(DOCUMENT, section) || itemEquals(SECTION, section)) {
        // // all element is upper than Document or section
        // return false;
        // }
        int subSectionLevel = Integer.parseInt(subSection
                .attributeValue("level"));
        int sectionLevel = Integer.parseInt(section.attributeValue("level"));
        boolean result = subSectionLevel == sectionLevel;
        return result;
    }

    /**
     * @param Element e1
     * @param Element e2
     * @param String ... attnames
     * @return boolean
     */
    private boolean hasSameAttribute(Element e1, Element e2, String... attnames) {
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

    /**
     * @param Element from
     * @param Element to
     * @throws DocumentException
     */
    private void copyLevel(Element from, Element to) throws DocumentException {
        String level = from.attributeValue("level");
        if (level == null) {
            throw new DocumentException("Element without level: " + from);
        }
        to.addAttribute("level", level);
    }

    /**
     * @param String name
     * @param Element e
     * @return boolean
     * @throws DocumentException
     */
    private boolean itemEquals(String name, Element e) throws DocumentException {
        boolean result = itemEquals(name, e, false, false);
        return result;
    }

    /**
     * @param String name
     * @param Element e
     * @param boolean throwError
     * @param boolean eof
     * @return boolean
     * @throws DocumentException
     */
    private boolean itemEquals(String name, Element e, boolean throwError,
            boolean eof) throws DocumentException {
        boolean result = e != null && name.equals(e.getName());
        if (ERROR_MISSING_ITEM && !result && throwError && !eof) {
            throw new DocumentException("Malformed document waiting " + name
                    + " and found " + (e != null ? e.getName() : "null"));
        }
        return result;
    }

    /**
     * @param String name
     * @param Element e
     * @return boolean
     */
    private boolean itemNotEquals(String name, Element e) {
        boolean result = e == null || !name.equals(e.getName());
        return result;
    }

    private Document newJRSTReader(Reader r) throws Exception {
        JRSTReader reader = new JRSTReader();
        reader.setVariable(idMax, symbolMax, symbolMaxRef, lblFootnotes,
                lblFootnotesRef, eFootnotes, eTarget, eTargetAnonymous,
                eTargetAnonymousCopy);

        return reader.read(r);

    }

    /**
     * <p>
     * Initialises les variables d'environements par ex, les hyperlinks peuvent
     * etre referencer dans tous le document
     * </p>
     * 
     * @param idMax
     * @param symbolMax
     * @param symbolMaxRef
     * @param lblFootnotes
     * @param lblFootnotesRef
     * @param eFootnotes
     * @param eTarget
     * @param eTargetAnonymous
     * @param eTargetAnonymousCopy
     */
    public void setVariable(int idMax, int symbolMax, int symbolMaxRef,
            LinkedList<Integer> lblFootnotes,
            LinkedList<Integer> lblFootnotesRef,
            LinkedList<Element> eFootnotes, LinkedList<Element> eTarget,
            LinkedList<Element> eTargetAnonymous,
            LinkedList<Element> eTargetAnonymousCopy) {
        this.idMax = idMax;
        this.symbolMax = symbolMax;
        this.symbolMaxRef = symbolMaxRef;
        this.lblFootnotes = lblFootnotes;
        this.lblFootnotesRef = lblFootnotesRef;
        this.eFootnotes = eFootnotes;
        this.eTarget = eTarget;
        this.eTargetAnonymous = eTargetAnonymous;
        this.eTargetAnonymousCopy = eTargetAnonymousCopy;
    }

    /**
     * Parse text in element and replace text with parse result
     * 
     * @param Element e
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
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String literal = "<" + LITERAL + ">" + matcher.group(1) + "</"
                    + LITERAL + ">";
            String key = "literal" + index++;
            temporaries.put(key, literal);
            text = text.substring(0, start) + "<tmp>" + key + "</tmp>"
                    + text.substring(end);
            matcher = REGEX_LITERAL.matcher(text);
        }
        // search all REGEX_INLINE_REFERENCE and replace it with special mark
        // this prevent substitution of URL with REGEX_REFERENCE. Use same
        // mechanisme as literal for that
        matcher = REGEX_INLINE_REFERENCE.matcher(text);
        index = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            Element ref = DocumentHelper.createElement(REFERENCE);
            ref.addAttribute("refuri", matcher.group(2));
            ref.setText(matcher.group(1));
            String key = "inlineReference" + index++;
            temporaries.put(key, ref.asXML());
            text = text.substring(0, start) + "<tmp>" + key + "</tmp>"
                    + text.substring(end);
            matcher = REGEX_INLINE_REFERENCE.matcher(text);

        }
        // do all substitution inline
        text = REGEX_EMAIL.matcher(text).replaceAll(
                "$1<" + REFERENCE + " refuri='mailto:$2'>$2</" + REFERENCE
                        + ">$3");
        text = REGEX_STRONG.matcher(text).replaceAll(
                "<" + STRONG + ">$1</" + STRONG + ">");
        text = REGEX_EMPHASIS.matcher(text).replaceAll(
                "<" + EMPHASIS + ">$1</" + EMPHASIS + ">");
        text = REGEX_REFERENCE.matcher(text).replaceAll(
                "<" + REFERENCE + " refuri='$1'>$1</" + REFERENCE + ">$2");
        // _[#]truc
        matcher = REGEX_FOOTNOTE_REFERENCE.matcher(text);
        while (matcher.find()) {
            String txtDebut = text.substring(0, matcher.start());
            String txtFin = text.substring(matcher.end() + 1, text.length());
            Element footnote = DocumentHelper.createElement(FOOTNOTE_REFERENCE);
            String sFootnote = matcher.group();
            boolean done = false;
            for (int i = 0; i < sFootnote.length() && !done; i++) {
                if (sFootnote.charAt(i) == ']') {
                    String id = sFootnote.substring(1, i);
                    if (id.equals("*")) {
                        int nb = Math.abs(symbolMaxRef / 10) + 1;
                        char symbol = FOOTNOTE_SYMBOL.charAt(symbolMaxRef % 10);
                        String label = "";
                        for (int j = 0; j < nb; j++) {
                            label += symbol;
                        }
                        symbolMaxRef++;
                        footnote.addAttribute("auto", "*");
                        for (int j = 0; j < eFootnotes.size(); j++) {
                            Element eFootnote = (Element) eFootnotes.get(j);
                            if (eFootnote.attributeValue("label").equals(label)) {

                                footnote.addAttribute("id", eFootnote
                                        .attributeValue("backrefs"));
                                footnote.addAttribute("refid", eFootnote
                                        .attributeValue("id"));

                            }
                        }
                        footnote.setText(label);

                    } else if (id.matches("[1-9]+")) {

                        for (int j = 0; j < eFootnotes.size(); j++) {
                            Element eFootnote = (Element) eFootnotes.get(j);
                            if (eFootnote.attributeValue("label").equals(id)) {
                                footnote.addAttribute("id", eFootnote
                                        .attributeValue("backrefs"));
                                footnote.addAttribute("refid", eFootnote
                                        .attributeValue("id"));
                            }
                        }
                        footnote.setText(id);
                        lblFootnotesRef.add(Integer.parseInt(id));

                    } else if (id.equals("#")) {
                        int lblMax = 0;
                        for (int j = 0; j < lblFootnotesRef.size(); j++)
                            lblMax = Math.max(lblMax, (Integer) lblFootnotesRef
                                    .get(j));

                        boolean[] lbls = new boolean[lblMax];
                        for (int j = 0; j < lbls.length; j++)
                            lbls[j] = false;
                        for (int j = 0; j < lblFootnotesRef.size(); j++)
                            lbls[(Integer) lblFootnotesRef.get(j) - 1] = true;
                        boolean valide = false;
                        do {
                            boolean trouve = false;
                            String label = null;
                            for (int j = 0; j < lbls.length && !trouve; j++) {

                                if (!lbls[j]) {
                                    trouve = true;
                                    label = "" + (j + 1);
                                }
                            }
                            if (!trouve)
                                label = "" + (lbls.length + 1);
                            footnote.addAttribute("auto", "1");
                            for (int j = 0; j < eFootnotes.size(); j++) {
                                Element eFootnote = (Element) eFootnotes.get(j);
                                if (eFootnote.attributeValue("label").equals(
                                        label)) {
                                    if (!(eFootnote.attributeValue("type")
                                            .equals("autoNumLabel"))) {
                                        footnote.addAttribute("id", eFootnote
                                                .attributeValue("backrefs"));
                                        footnote.addAttribute("refid",
                                                eFootnote.attributeValue("id"));
                                        footnote.setText(label);
                                        lblFootnotesRef.add(Integer
                                                .parseInt(label));
                                        valide = true;
                                    } else {
                                        valide = false;
                                        lbls[Integer.parseInt(label) - 1] = true;
                                    }
                                }
                            }
                        } while (!valide);

                    }

                    else {
                        footnote.addAttribute("auto", "1");

                        String name = id.substring(1);
                        boolean trouve = false;
                        for (int j = 0; j < eFootnotes.size() && !trouve; j++) {
                            Element eFootnote = (Element) eFootnotes.get(j);
                            if (eFootnote.attributeValue("name").equals(name)) {
                                footnote.addAttribute("id", eFootnote
                                        .attributeValue("backrefs"));
                                footnote.addAttribute("refid", eFootnote
                                        .attributeValue("id"));
                                String label = eFootnote
                                        .attributeValue("label");
                                footnote.setText(label);
                                lblFootnotesRef.add(Integer.parseInt(label));
                                trouve = true;
                            }
                        }

                        footnote.addAttribute("name", name);
                    }
                    done = true;
                }
            }
            text = txtDebut + footnote.asXML() + txtFin;
            matcher = REGEX_FOOTNOTE_REFERENCE.matcher(text);
        }
        // .. __http://truc.html
        matcher = REGEX_ANONYMOUS_HYPERLINK_REFERENCE.matcher(text);
        while (matcher.find()) {
            String txtDebut = text.substring(0, matcher.start());
            String txtFin = text.substring(matcher.end(), text.length());
            String ref = text.substring(matcher.start(), matcher.end() - 2);
            ref = ref.replaceAll("`", "");
            Element anonym = DocumentHelper.createElement("reference");
            anonym.addAttribute("anonymous", "1");
            anonym.addAttribute("name", ref.trim());
            if (!eTargetAnonymous.isEmpty()) {
                Element target = eTargetAnonymous.getFirst();
                eTargetAnonymous.removeFirst();
                anonym.addAttribute("refuri", target.attributeValue("refuri"));
            }
            anonym.setText(ref);
            text = txtDebut + anonym.asXML() + txtFin;
            matcher = REGEX_ANONYMOUS_HYPERLINK_REFERENCE.matcher(text);
        }
        // .. _truc: http://truc.html
        matcher = REGEX_HYPERLINK_REFERENCE.matcher(text);
        while (matcher.find()) {
            String txtDebut = text.substring(0, matcher.start());
            String txtFin = text.substring(matcher.end(), text.length());
            String ref = text.substring(matcher.start(), matcher.end() - 1);

            ref = ref.replaceAll("(&apos;|_)", "");
            ref = ref.replaceAll("[\\W&&[^-]]", " ").trim();
            Element hyper = DocumentHelper.createElement("reference");
            hyper.addAttribute("name", ref);
            boolean trouve = false;
            for (int i = 0; i < eTarget.size() && !trouve; i++) {
                Element el = eTarget.get(i);
                String refTmp = ref.replaceAll("\\s", "-").toLowerCase();
                if (el.attributeValue("id").equalsIgnoreCase(refTmp)) {
                    hyper.addAttribute("refuri", el.attributeValue("refuri"));
                    trouve = true;
                }
            }
            if (!trouve)
                hyper.addAttribute("refid", ref);
            hyper.setText(ref);
            text = txtDebut + hyper.asXML() + " " + txtFin;
            matcher = REGEX_HYPERLINK_REFERENCE.matcher(text);

        }

        // substitution reference
        matcher = REGEX_SUBSTITUTION_REFERENCE.matcher(text);
        int begin = 0;
        while (matcher.find(begin)) {
            String start = text.substring(0, matcher.start());
            String end = text.substring(matcher.end());
            String ref = matcher.group(1);

            Node subst = e.selectSingleNode("//" + SUBSTITUTION_DEFINITION
                    + "[@name='" + ref + "']/child::node()");

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
        Pattern p = Pattern.compile("<tmp>([^<>]+)</tmp>");

        matcher = p.matcher(text);
        while (matcher.find()) {
            String start = text.substring(0, matcher.start());
            String end = text.substring(matcher.end());

            String tempKey = matcher.group(1);
            text = start + temporaries.get(tempKey) + end;
            matcher = p.matcher(text);
        }
        Element result = DocumentHelper.parseText(
                "<TMP>" + text.trim() + "</TMP>").getRootElement();

        e.setText("");
        e.appendContent(result);
    }
}