/* *##%
 * Copyright (C) 2006
 *     Code Lutin, Cï¿½dric Pineau, Benjamin Poussin
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
 * JRSTLexer.java
 *
 * Created: 28 oct. 06 00:44:20
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Le principe est de positionner la mark du {@link AdvancedReader} lors du
 * debut d'une methode peek*, puis a la fin de la methode de regarder le nombre
 * de caractere utilisï¿½ pour la methode et de faire un reset.
 * <p>
 * Le nombre de caractere utilisï¿½ servira pour le remove lorsque l'utilisateur
 * indiquera qu'il utilise l'element retournï¿½, si l'utilisateur n'appelle pas
 * remove alors il peut relire autant de fois qu'il veut le meme element, ou
 * essayer d'en lire un autre.
 * <p>
 * Pour mettre en place ce mecanisme le plus simple est d'utiliser les methodes
 * {@link JRSTLexer#beginPeek()} et {@link JRSTLexer#endPeek()}
 * 
 * 
 * 
 * @author poussin, letellier
 */
public class JRSTLexer {

    /** to use log facility, just put in your code: log.info(\"...\"); */
    static private Log log = LogFactory.getLog(JRSTLexer.class);

    static final public String BULLET_CHAR = "*" + "+" + "-"/*
                                                             * + "\u2022" +
                                                             * "\u2023" +
                                                             * "\u2043"
                                                             */;

    static final public String TITLE_CHAR = "-=-~'`^+:!\"#$%&*,./;|?@\\_[\\]{}<>()";

    static final public String DOCINFO_ITEM = "author|authors|organization|address|contact|version|revision|status|date|copyright";

    public static final String ADMONITION_PATTERN = "admonition|attention|caution|danger|error|hint|important|note|tip|warning";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Title Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    static final public String TITLE = "title";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Bibliographic Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    static final public String DOCINFO = "docinfo";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Decoration Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static final String DECORATION = "decoration";

    public static final String HEADER = "header";

    public static final String FOOTER = "footer";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Structural Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    static final public String TRANSITION = "transition";

    public static final String SIDEBAR = "sidebar";

    public static final String TOPIC = "topic";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Body Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    static final public String LITERAL_BLOCK = "literal_block";

    static final public String PARAGRAPH = "paragraph";

    static final public String BLANK_LINE = "blankLine";

    static final public String SUBSTITUTION_DEFINITION = "substitution_definition";

    static final public String BULLET_LIST = "bullet_list";

    static final public String FIELD_LIST = "field_list";

    static final public String DEFINITION_LIST = "definition_list";

    static final public String ENUMERATED_LIST = "enumerated_list";

    static final public String OPTION_LIST = "option_list";

    public static final String LINE_BLOCK = "line_block";

    public static final String LINE = "line";

    public static final String BLOCK_QUOTE = "block_quote";

    public static final String ATTRIBUTION = "attribution";

    public static final String DOCTEST_BLOCK = "doctest_block";

    public static final String ADMONITION = "admonition";

    public static final String TARGET = "target";

    public static final String FOOTNOTE = "footnote";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Table Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    static final public String TABLE = "table";

    static final public String ROW = "row";

    static final public String CELL = "cell";

    static final public String TABLE_HEADER = "header";

    static final public String TABLE_WIDTH = "width";

    static final public String ROW_END_HEADER = "endHeader";

    static final public String CELL_INDEX_START = "indexStart";

    static final public String CELL_INDEX_END = "indexEnd";

    static final public String CELL_BEGIN = "begin";

    static final public String CELL_END = "end";

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Directive Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    static final public String DIRECTIVE = "directive";

    static final public String DIRECTIVE_TYPE = "type";

    static final public String DIRECTIVE_VALUE = "value";

    /**
     * retient le niveau du titre, pour un titre de type double, on met deux
     * fois le caratere dans la chaine, sinon on le met une seul fois.
     * 
     * <pre>
     * =====
     * Super
     * =====
     * titre
     * -----
     * </pre>
     * 
     * donnera dans la liste ["==", "-"]
     */
    private List<String> titleLevels = null;

    private AdvancedReader in = null;

    /**
     * length of the last element returned (number of char need to this element)
     */
    private int elementLength = 0;

    public JRSTLexer(Reader reader) {
        titleLevels = new ArrayList<String>();
        this.in = new AdvancedReader(reader);
    }

    /**
     * true if no more element to read
     * 
     * @return boolean
     * @throws IOException
     */
    public boolean eof() throws IOException {
        in.mark();
        in.skipBlankLines();
        boolean result = in.eof();
        in.reset();
        return result;
    }

    /**
     * remove one element from list of element already read
     * 
     * @return
     * @throws IOException
     */
    public void remove() throws IOException {
        in.skip(elementLength);
    }

    /**
     * start peek
     * 
     * @throws IOException
     */
    private void beginPeek() throws IOException {
        elementLength = 0;
        in.mark();
    }

    /**
     * end peek
     * 
     * @throws IOException
     */
    private void endPeek() throws IOException {
        elementLength = in.readSinceMark();
        in.reset();
    }

    /**
     * Read block text, block text have same indentation and is continu (no
     * blank line)
     * 
     * @param minLeftMargin min left blank needed to accept to read block
     * @return String[]
     * @throws IOException
     */
    private String[] readBlock(int minLeftMargin) throws IOException {
        String[] result = new String[0];
        String firstLine = in.readLine();
        if (firstLine != null) {
            in.unread(firstLine, true);
            int level = level(firstLine);
            if (level >= minLeftMargin) {
                result = in.readWhile("^ {" + level + "}\\S+.*");
            }
        }

        return result;
    }

    /**
     * All lines are joined and left and right spaces are removed during join
     * 
     * @param String[] lines
     * @return String
     */
    private String joinBlock(String[] lines) {
        String result = joinBlock(lines, " ", true);
        return result;
    }

    /**
     * All lines are joined whith the String joinSep and left and right spaces
     * are removed if trim
     * 
     * @param String[] lines
     * @param String joinSep
     * @param Boolean trim
     * @return String
     */
    private String joinBlock(String[] lines, String joinSep, boolean trim) {
        String result = "";
        String sep = "";
        for (String line : lines) {
            if (trim) {
                line = line.trim();
            }
            result += sep + line;
            sep = joinSep;
        }
        return result;
    }

    /**
     * search if the doc have an header
     * 
     * <pre>
     *    .. header:: This space for rent. aaaa **aaaa**
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekHeader() throws IOException {
        beginPeek();
        Element result = null;
        String[] line = in.readAll();
        if (line != null) {
            int i = 0;
            for (String l : line) {
                i++;
                if (l.matches("^\\s*.. " + HEADER + ":: .*")) {
                    int level = level(l);
                    l = l.replaceAll("^\\s*.. " + HEADER + ":: ", "");
                    result = DocumentHelper.createElement(HEADER).addAttribute(
                            "level", String.valueOf(level));
                    result.addAttribute("line", "" + i);
                    result.setText(l);
                }

            }
        }
        endPeek();
        return result;

    }

    /**
     * search if the doc have an header
     * 
     * <pre>
     *    .. footer:: design by **LETELLIER Sylvain**
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekFooter() throws IOException {
        beginPeek();
        Element result = null;
        String[] line = in.readAll();
        if (line != null) {
            int i = 0;
            for (String l : line) {
                i++;

                if (l.matches("^\\s*.. " + FOOTER + ":: .*")) {
                    int level = level(l);
                    l = l.replaceAll("^\\s*.. " + FOOTER + ":: ", "");
                    result = DocumentHelper.createElement(FOOTER).addAttribute(
                            "level", String.valueOf(level));
                    result.addAttribute("line", "" + i);
                    result.setText(l);
                }
            }
        }
        endPeek();
        return result;

    }

    /**
     * <pre>
     * .. __: http://www.python.org
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    public LinkedList<Element> peekTargetAnonymous() throws IOException {
        beginPeek();
        LinkedList<Element> result = new LinkedList<Element>();
        String[] line = in.readAll();
        if (line != null) {
            int i = 0;
            for (String l : line) {
                i++;

                if (l.matches("^\\s*__ .+$|^\\s*\\.\\. __\\:.+$")) {
                    Element resultTmp = DocumentHelper
                            .createElement("targetAnonymous");
                    resultTmp.addAttribute("level", "" + level(l));
                    Matcher matcher = Pattern.compile("__ ").matcher(l);
                    if (matcher.find()) {
                        resultTmp.addAttribute("refuri", l.substring(matcher
                                .end(), l.length()));
                    }
                    result.add(resultTmp);
                }
            }

        }
        endPeek();
        return result;
    }

    /**
     * Return title or para
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekTitleOrBodyElement() throws IOException {
        Element result = null;
        if (result == null) {
            result = peekTitle();
        }
        if (result == null) {
            result = peekBodyElement();
        }

        return result;
    }

    /**
     * read doc info author, date, version, ... or field list element
     * 
     * <pre>
     * :author: Benjamin Poussin
     * :address:
     *   Quelque part
     *   Dans le monde
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekDocInfo() throws IOException {
        Element result = null;
        if (result == null) {
            result = peekDocInfoItem();

        }
        if (result == null) {
            result = peekFieldList();
        }

        return result;

    }

    /**
     * Return para
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekBodyElement() throws IOException {
        Element result = null;
        if (result == null) {
            result = peekInclude();
        }
        if (result == null) {
            result = peekComment();
        }
        if (result == null) {
            result = peekDoctestBlock();
        }
        if (result == null) {
            result = peekAdmonition();
        }
        if (result == null) {
            result = peekSidebar();
        }
        if (result == null) {
            result = peekTopic();
        }
        if (result == null) {
            result = peekRemove();
        }
        if (result == null) {
            result = peekDirectiveOrReference();
        }
        if (result == null) {
            result = peekTransition();
        }
        if (result == null) {
            result = peekTable();
        }
        if (result == null) {
            result = peekLineBlock();
        }
        if (result == null) {
            result = peekBulletList();
        }
        if (result == null) {
            result = peekOption();
        }
        if (result == null) {
            result = peekEnumeratedList();
        }
        if (result == null) {
            result = peekTarget();
        }
        if (result == null) {
            result = peekFootnote();
        }
        if (result == null) {
            result = peekDefinitionList();
        }
        if (result == null) {
            result = peekFieldList();
        }
        if (result == null) {
            result = peekTargetAnonymousBody();
        }
        if (result == null) {
            result = peekLiteralBlock();
        }
        if (result == null) {
            result = peekBlockQuote();
        }
        if (result == null) {
            result = peekBlankLine();
        }
        if (result == null) {
            result = peekPara();
        }

        return result;
    }

    /**
     * Remove already read elements
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekRemove() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            // Le header est parse des le debut
            if (line.matches("^\\s*.. " + HEADER + ":: .*")) {
                result = DocumentHelper.createElement("remove").addAttribute(
                        "level", "" + level(line));
            }
            // Le footer
            if (line.matches("^\\s*.. " + FOOTER + ":: .*")) {
                result = DocumentHelper.createElement("remove").addAttribute(
                        "level", "" + level(line));
            }

        }
        endPeek();
        return result;
    }

    /**
     * read include
     * 
     * <pre>
     * .. include:: text.txt
     * or
     * .. include:: literal
     *      text.txt
     * 
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekInclude() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^\\s*\\.\\.\\sinclude\\:\\:.+$")) {
                result = DocumentHelper.createElement("include");
                result.addAttribute("level", "" + level(line));
                String option = line.substring(line.indexOf("::") + 2).trim();
                result.addAttribute("option", "");
                if (option.trim().equalsIgnoreCase("literal")) {
                    result.addAttribute("option", "literal");
                    line = in.readLine();
                    result.setText(line.trim());
                } else
                    result.setText(option);

            }
        }
        endPeek();
        return result;
    }

    /**
     * read options
     * 
     * <pre>
     * Ex :     -a            command-line option &quot;a&quot;
     *          -1 file, --one=file, --two file
     *                     Multiple options with arguments.
     * Schéma :  ________________________________
     *           v          |                    |
     *        -{1,2}\w+ -&gt;|','                   |
     *                    |'='-----|-&gt; \w+ ---&gt;|','
     *                    |' '-----|           |' '---+
     *                    |&quot;  &quot; -----&gt; \w+ ---&gt; end   |
     *                      &circ;                         |
     *                      |_________________________|
     * Légende :   
     * 
     *          -{1,2} --&gt; 1 or 2 tirets
     *          \w+ -----&gt; word characters one or more times
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekOption() throws IOException {

        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^(\\s*-{1,2}[^\\s-].+\\s+.*)+\\s+.+$")) {
                result = DocumentHelper.createElement(OPTION_LIST)
                        .addAttribute("level", "" + level(line));
                char delimiter;
                do {
                    Matcher matcher = Pattern.compile("-{1,2}.+").matcher(line);
                    matcher.find();
                    Element option = result.addElement("option");
                    String option_stringTmp = matcher.group();
                    matcher = Pattern.compile("^-{1,2}\\w+").matcher(
                            option_stringTmp);
                    matcher.find();
                    String option_string = matcher.group();
                    option.addAttribute("option_string", option_string);
                    delimiter = option_stringTmp.charAt(matcher.end());
                    option_stringTmp = option_stringTmp.substring(
                            matcher.end(), option_stringTmp.length());
                    option.addAttribute("delimiterExiste", "false");
                    boolean done = false;
                    if (delimiter == ' ') { // S'il ya 2 espace a suivre,
                        // l'option est finit
                        if (option_stringTmp.charAt(1) == ' ') {
                            done = true;
                        }
                    }
                    if ((delimiter == '=' || delimiter == ' ') && !done) {
                        option.addAttribute("delimiterExiste", "true");
                        option.addAttribute("delimiter", "" + delimiter);
                        matcher = Pattern.compile(delimiter + "\\w+").matcher(
                                option_stringTmp);
                        String option_argument;
                        if (matcher.find()) {
                            option_argument = matcher.group().substring(1,
                                    matcher.group().length());
                            option.addAttribute("option_argument",
                                    option_argument);
                            if (option_stringTmp.charAt(option_argument
                                    .length() + 1) == ',') {
                                delimiter = ',';
                                line = line.substring(option_string.length()
                                        + option_argument.length() + 3, line
                                        .length());
                            } else
                                done = true;
                        } else { // Si la description n'est pas sur la meme
                            // ligne
                            option_argument = option_stringTmp;
                            option.addAttribute("option_argument",
                                    option_argument);
                            line = in.readLine();
                            if (line != null) {
                                result.setText(line.trim());
                            }
                        }
                    }
                    if (done)
                        result.setText(option_stringTmp.substring(
                                matcher.end(), option_stringTmp.length())
                                .trim());
                } while (delimiter == ',');
            }
        }
        endPeek();
        return result;
    }

    /**
     * read topic
     * 
     * <pre>
     * -.. topic:: Title
     *     Body.
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekTopic() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^\\.\\.\\s*(" + TOPIC + ")::\\s*(.*)$")) {
                Matcher matcher = Pattern.compile(TOPIC + "::").matcher(line);
                matcher.find();
                result = DocumentHelper.createElement(TOPIC).addAttribute(
                        "level", "" + level(line));
                String title = line.substring(matcher.end(), line.length());
                result.addAttribute(TITLE, title);
                line = in.readLine();
                if (line.matches("\\s*"))
                    line = in.readLine();
                int level = level(line);
                String[] lines = null;
                if (level != 0) {
                    lines = in.readWhile("(^ {" + level + "}.*)|(\\s*)");
                    String txt = line;
                    for (String txtTmp : lines)
                        txt += "\n" + txtTmp.trim();
                    result.setText(txt);
                }

            }
        }

        endPeek();
        return result;
    }

    /**
     * read sidebar
     * 
     * <pre>
     * .. sidebar:: Title
     *    :subtitle: If Desired
     *    Body.
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekSidebar() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^\\.\\.\\s*(" + SIDEBAR + ")::\\s*(.*)$")) {
                Matcher matcher = Pattern.compile(SIDEBAR + "::").matcher(line);
                matcher.find();
                result = DocumentHelper.createElement(SIDEBAR).addAttribute(
                        "level", "" + level(line));
                String title = line.substring(matcher.end(), line.length());
                result.addAttribute(TITLE, title);
                line = in.readLine();
                if (line.matches("^\\s+:subtitle:\\s*(.*)$*")) {
                    matcher = Pattern.compile(":subtitle:\\s*").matcher(line);
                    matcher.find();
                    String subTitle = line.substring(matcher.end(), line
                            .length());
                    result.addAttribute("subExiste", "true");
                    result.addAttribute("subtitle", subTitle);
                    line = in.readLine();
                } else
                    result.addAttribute("subExiste", "false");
                String txt = joinBlock(readBlock(level(line)));
                result.setText(txt);

            }
        }

        endPeek();
        return result;
    }

    /**
     * read line block
     * 
     * <pre>
     * |        A one, two, a one two three four
     * |
     * | Half a bee, philosophically,
     * |     must, *ipso facto*, half not be.
     * | But half the bee has got to be,
     * |               *vis a vis* its entity.  D'you see?
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekLineBlock() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("\\|\\s.*")) {
                String[] linesTmp = readBlock(0);
                String[] lines = new String[linesTmp.length + 1];
                lines[0] = line;
                for (int i = 0; i < linesTmp.length; i++)
                    lines[i + 1] = linesTmp[i];
                int[] levelsTmp = new int[lines.length];
                int levelmin = 999;
                result = DocumentHelper.createElement(LINE_BLOCK).addAttribute(
                        "level", 0 + "");
                for (int i = 0; i < levelsTmp.length; i++)
                    // on enleve |
                    lines[i] = lines[i].replaceAll("\\|\\s?", "");
                for (int i = 0; i < levelsTmp.length; i++)
                    // determination des levels
                    levelsTmp[i] = level(lines[i]);
                for (int i : levelsTmp)
                    // level minimal
                    levelmin = Math.min(levelmin, i);
                int cnt = 0;
                String lineAv = "";
                int[] levels = new int[levelsTmp.length];
                for (String l : lines) {
                    if (!l.matches("\\s*")) { // Si la ligne courante n'est
                        // pas vide
                        int level = levelsTmp[cnt] - levelmin;
                        if (level != 0) {
                            if (cnt != 0) {
                                if (!lineAv.matches("\\s*")) { // Si la ligne
                                    // d'avant n'est
                                    // pas vide
                                    int levelAv = levelsTmp[cnt - 1] - levelmin;
                                    if (levelAv < level) {
                                        levels[cnt] = levels[cnt - 1] + 1;
                                        if (cnt != levels.length) {
                                            int levelAp = levelsTmp[cnt + 1]
                                                    - levelmin;
                                            if (levelAp < level
                                                    && levelAv < levelAp)
                                                levels[cnt]++;
                                        }
                                    } else {
                                        levels[cnt] = levels[cnt - 1] - 1;
                                    }
                                } else
                                    levels[cnt] = 1;
                            } else
                                levels[cnt] = 1;
                        } else
                            levels[cnt] = 0;
                    } else {
                        if (cnt != 0)
                            levels[cnt] = levels[cnt - 1];
                        else
                            levels[cnt] = 0;
                    }
                    cnt++;
                    lineAv = l;
                }
                for (int i = 0; i < levels.length; i++) {
                    Element eLine = result.addElement(LINE);
                    eLine.addAttribute("level", "" + levels[i]);
                    eLine.setText(lines[i].trim());
                }
            }
        }
        endPeek();
        return result;
    }

    /**
     * read doctest block
     * 
     * <pre>
     * &gt;&gt;&gt; print 'this is a Doctest block'
     * this is a Doctest block
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekDoctestBlock() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^\\s*>>>\\s.*")) {
                int level = level(line);
                result = DocumentHelper.createElement(DOCTEST_BLOCK)
                        .addAttribute("level", String.valueOf(level));
                result.addAttribute("xml:space", "preserve");
                line += "\n" + joinBlock(readBlock(level));
                result.setText(line);
            }
        }
        endPeek();
        return result;
    }

    /**
     * read block quote
     * 
     * <pre>
     * As a great paleontologist once said,
     * 
     *     This theory, that is mine, is mine.
     * 
     *     -- Anne Elk (Miss)
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekBlockQuote() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("\\s*")) {
                line = in.readLine();
                if (line != null) {
                    int level = level(line);
                    String blockQuote = null;
                    if (!(level == 0)) {
                        String txt = line;
                        String[] lines = in.readWhile("(^ {" + level
                                + "}.*)|(\\s*)");
                        for (String l : lines) {
                            if (l.matches("^ {" + level + "}--\\s*.*")) {
                                blockQuote = l;
                                blockQuote = blockQuote.replaceAll("--", "")
                                        .trim();
                            } else
                                txt += "\n" + l;
                        }
                        result = DocumentHelper.createElement(BLOCK_QUOTE)
                                .addAttribute("level", String.valueOf(level));
                        if (blockQuote != null)
                            result.addAttribute(ATTRIBUTION, blockQuote);
                        result.setText(txt);
                    }
                }
            }
        }
        endPeek();
        return result;
    }

    /**
     * read admonitions :
     * admonition|attention|caution|danger|error|hint|important|note|tip|warning
     * 
     * <pre>
     * .. Attention:: All your base are belong to us.
     * .. admonition:: And, by the way...
     * 
     *    You can make up your own admonition too.
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekAdmonition() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            String lineTest = line.toLowerCase();
            Pattern pAdmonition = Pattern.compile("^\\.\\.\\s*("
                    + ADMONITION_PATTERN + ")::\\s*(.*)$");
            Matcher matcher = pAdmonition.matcher(lineTest);

            if (matcher.matches()) {

                boolean admonition = false;
                matcher = Pattern.compile(ADMONITION_PATTERN).matcher(lineTest);
                matcher.find();
                int level = level(line);
                result = DocumentHelper.createElement(ADMONITION).addAttribute(
                        "level", "" + level);

                if (matcher.group().equals(ADMONITION)) { // Il y a un titre
                    // pour un
                    // admonition
                    // general
                    admonition = true;
                    result.addAttribute("type", ADMONITION);
                    String title = line.substring(matcher.end() + 2, line
                            .length());

                    result.addAttribute("title", title);
                } else
                    result.addAttribute("type", matcher.group());

                String firstLine = "";
                if (!admonition && matcher.end() + 2 < line.length())
                    firstLine = line
                            .substring(matcher.end() + 2, line.length());
                line = in.readLine();
                if (line != null) {
                    if (line.matches("\\s*"))
                        line = in.readLine();
                    if (line != null) {
                        if (!line.matches("\\s*")) {
                            level = level(line);
                            String txt = firstLine.trim() + "\n" + line + "\n";
                            txt += "\n" + readBlockWithBlankLine(level);
                            result.setText(txt);
                        }

                    } else
                        result.setText(firstLine);
                } else
                    result.setText(firstLine);
            }
        }
        endPeek();
        return result;
    }

    /**
     * read blank line
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekBlankLine() throws IOException {
        beginPeek();
        Element result = null;

        // must have one blank line before
        String line = in.readLine();
        if (line != null && line.matches("\\s*")) {
            int level = level(line);
            result = DocumentHelper.createElement(BLANK_LINE).addAttribute(
                    "level", String.valueOf(level));
        }

        endPeek();
        return result;
    }

    /**
     * read directive or reference
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekDirectiveOrReference() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            Pattern pImage = Pattern
                    .compile("^\\.\\.\\s*(?:\\|([^|]+)\\|)?\\s*(\\w+)::\\s*(.*)$");
            Matcher matcher = pImage.matcher(line);
            if (matcher.matches()) {
                String ref = matcher.group(1);
                String directiveType = matcher.group(2);
                String directiveValue = matcher.group(3);
                Element directive = null;
                if (ref != null && !"".equals(ref)) {
                    result = DocumentHelper
                            .createElement(SUBSTITUTION_DEFINITION);
                    result.addAttribute("name", ref);
                    directive = result.addElement(DIRECTIVE);
                } else {
                    result = DocumentHelper.createElement(DIRECTIVE);
                    directive = result;
                }
                result.addAttribute("level", "0");

                directive.addAttribute(DIRECTIVE_TYPE, directiveType);
                directive.addAttribute(DIRECTIVE_VALUE, directiveValue);

                String[] lines = readBlock(2);
                String text = joinBlock(lines, "\n", false);

                directive.setText(text);
            }
        }
        endPeek();
        return result;
    }

    /**
     * read transition
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekTransition() throws IOException {
        beginPeek();

        Element result = null;
        // no eat blank line, see next comment

        // must have one blank line before
        String line = in.readLine();
        if (line != null && line.matches("\\s*")) {
            // in.skipBlankLines();
            line = in.readLine();
            if (line != null && line.matches("-{3,}\\s*")) {
                line = in.readLine();
                // must have one blank line after
                if (line != null && line.matches("\\s*")) {
                    result = DocumentHelper.createElement(TRANSITION)
                            .addAttribute("level", String.valueOf(0));
                }
            }
        }
        endPeek();
        return result;
    }

    /**
     * read paragraph with attribut level that represente the space numbers at
     * left side
     * 
     * @return &lt;paragraph level="[int]"&gt[text]&lt;/paragraph&gt;
     * @throws IOException
     */
    public Element peekPara() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();

        String[] lines;
        do {
            lines = readBlock(0);
            if (lines.length > 0) {
                int level = level(lines[0]);
                String para = joinBlock(lines);

                boolean literal = false;
                if (para.endsWith(": ::")) {
                    para = para.substring(0, para.length() - " ::".length());
                    in.unread("::", true);
                    literal = true;
                } else if (para.endsWith("::")) {
                    para = para.substring(0, para.length() - ":".length()); // keep
                    // one
                    // :
                    in.unread("::", true);
                    literal = true;
                }

                if (para.length() == 0 || ":".equals(para)) {
                    if (literal) {
                        in.readLine(); // eat "::"
                    }
                } else {
                    // if para is empty, there are error and possible
                    // infiny loop on para, force read next line
                    result = DocumentHelper.createElement(PARAGRAPH)
                            .addAttribute("level", String.valueOf(level))
                            .addText(para);
                }
            }
        } while (result == null && lines.length > 0);

        endPeek();
        return result;
    }

    /**
     * read literal block
     * 
     * <pre>
     * ::
     * 
     *     LiteralBlock
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekLiteralBlock() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();

        String[] prefix = in.readLines(2);
        if (prefix.length == 2 && prefix[0].matches("\\s*::\\s*")
                && prefix[1].matches("\\s*")) {

            String para = in.readLine();
            if (para != null) {
                int level = level(para);
                para = para.substring(level) + "\n";

                // it's literal block until level is down
                String[] lines = in.readWhile("(^ {" + level + "}.*|\\s*)");
                while (lines.length > 0) {
                    for (String line : lines) {
                        if (!line.matches("\\s*"))
                            para += line.substring(level) + "\n";
                        else
                            para += "\n";
                    }
                    lines = in.readWhile("(^ {" + level + "}.*|\\s*)");
                }

                result = DocumentHelper.createElement(LITERAL_BLOCK)
                        .addAttribute("level", String.valueOf(level)).addText(
                                para);
            }
        }

        endPeek();
        return result;
    }

    /**
     * read doc info author, date, version, ...
     * 
     * <pre>
     * :author: Benjamin Poussin
     * :address:
     *   Quelque part
     *   Dans le monde
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekDocInfoItem() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();
        String line = in.readLine();
        // (?i) case inensitive on docinfo item
        if (line != null && line.matches("^:((?i)" + DOCINFO_ITEM + "):.*$")) {

            result = DocumentHelper.createElement(DOCINFO);
            result.addAttribute("level", "0");
            String infotype = line.substring(1, line.indexOf(":", 1));

            /*
             * if (!in.eof()) { String [] content = readBlock(1); line +=
             * joinBlock(content); }
             */
            String text = line.substring(line.indexOf(":", 1) + 1).trim();
            String[] textTmp = in.readWhile("^\\s+.*");
            if (textTmp.length != 0)
                in.mark();

            for (String txt : textTmp)
                text += "\n" + txt.trim();

            // CVS, RCS support
            text = text.replaceAll("\\$\\w+: (.+?)\\$", "$1");

            result.addAttribute("type", infotype).addText(text);
        }
        endPeek();
        return result;
    }

    /**
     * read table simple and complexe
     * 
     * <pre>
     * +------------------------+------------+----------+----------+
     * | Header row, column 1   | Header 2   | Header 3 | Header 4 |
     * | (header rows optional) |            |          |          |
     * +========================+============+==========+==========+
     * | body row 1, column 1   | column 2   | column 3 | column 4 |
     * +------------------------+------------+----------+----------+
     * | body row 2             | Cells may span columns.          |
     * +------------------------+------------+---------------------+
     * </pre>
     * 
     * @return Element
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public Element peekTable() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();
        String line = in.readLine();

        if (line != null) {
            Pattern pTableBegin = Pattern.compile("^\\s*(\\+-+)+\\+\\s*$");
            Matcher matcher = null;

            matcher = pTableBegin.matcher(line);
            if (matcher.matches()) { // complexe table
                result = DocumentHelper.createElement(TABLE);
                result.addAttribute(TABLE_HEADER, "false");
                int level = level(line);
                result.addAttribute("level", String.valueOf(level));
                line = line.trim();
                int tableWidth = line.length();
                result.addAttribute(TABLE_WIDTH, String.valueOf(tableWidth));

                Pattern pCellEnd = Pattern
                        .compile("^\\s{"
                                + level
                                + "}(\\+-+\\+|\\|(?:[^+]+))([^+]+(?:\\+|\\|\\s*$)|-+\\+)*\\s*"); // fin
                // de
                // ligne
                Pattern pCell = Pattern.compile("^\\s{" + level
                        + "}(\\|[^|]+)+\\|\\s*$"); // une ligne
                Pattern pHeader = Pattern.compile("^\\s{" + level
                        + "}(\\+=+)+\\+\\s*$"); // fin du header
                Pattern pEnd = Pattern.compile("^\\s{" + level
                        + "}(\\+-+)+\\+\\s*$"); // fin de table

                // used to know if | is cell separator or not
                String lastSeparationLine = line;
                String lastLine = line;

                Element row = DocumentHelper.createElement(ROW);
                String[] table = in.readUntilBlank();

                boolean done = false;
                for (String l : table) {
                    done = false;
                    l = l.trim();
                    if (l.length() != tableWidth) {
                        // Erreur dans la table, peut-etre lever une exception ?
                        result = null;
                        break;
                    }
                    matcher = pEnd.matcher(l);
                    if (!done && matcher.matches()) {
                        // fin normale de ligne, on peut directement l'assigner
                        lastSeparationLine = l;
                        for (Element cell : (List<Element>) row.elements()) {
                            cell.addAttribute(CELL_END, "true");
                        }
                        row.addAttribute(ROW_END_HEADER, "false");
                        result.add(row);
                        row = DocumentHelper.createElement(ROW);
                        done = true;
                    }
                    matcher = pHeader.matcher(l);
                    if (!done && matcher.matches()) {
                        // fin de du header, on peut directement l'assigner
                        lastSeparationLine = l;
                        for (Element cell : (List<Element>) row.elements()) {
                            cell.addAttribute(CELL_END, "true");
                        }
                        row.addAttribute(ROW_END_HEADER, "true");
                        result.add(row);
                        result.addAttribute(TABLE_HEADER, "true");
                        row = DocumentHelper.createElement(ROW);
                        done = true;
                    }
                    matcher = pCell.matcher(l);
                    if (!done && matcher.matches()) {
                        // debug
                        row.addAttribute("debug", "pCell");
                        // recuperation des textes des cellules
                        int start = -1;
                        String content = "";
                        matcher = Pattern.compile("([^|]+)\\|").matcher(l);
                        for (int cellNumber = 0; matcher.find(); cellNumber++) {
                            int tmpstart = matcher.start(1);
                            int end = matcher.end(1);
                            String tmpcontent = matcher.group(1);
                            // on a forcement un | ou un + au dessus du +
                            // et forcement un + sur lastSeparationLine
                            // sinon ca veut dire qu'il y avait un | dans la
                            // cell
                            if ((lastLine.charAt(end) == '|' || lastLine
                                    .charAt(end) == '+')
                                    && lastSeparationLine.charAt(end) == '+') {
                                if ("".equals(content)) {
                                    content = tmpcontent;
                                }
                                if (start == -1) {
                                    start = tmpstart;
                                }
                                Element cell = null;
                                if (row.nodeCount() <= cellNumber) {
                                    cell = row.addElement(CELL);
                                    cell.addAttribute(CELL_END, "false");
                                } else {
                                    cell = (Element) row.node(cellNumber);

                                }
                                cell.addAttribute(CELL_INDEX_START, String
                                        .valueOf(start));
                                cell.addAttribute(CELL_INDEX_END, String
                                        .valueOf(end));
                                cell.setText(cell.getText() + content + "\n");
                                start = end + 1; // +1 to pass + or | at end
                                // of cell
                                content = "";
                            } else {
                                // start = tmpstart;
                                content += tmpcontent + "|";
                            }
                        }
                        done = true;
                    }
                    matcher = pCellEnd.matcher(l);
                    if (!done && matcher.matches()) {
                        // debug
                        row.addAttribute("debug", "pCellEnd");
                        // fin d'une ligne, on ne peut pas l'assigner
                        // directement
                        // pour chaque continuation de cellule, il faut copier
                        // l'ancienne valeur

                        // mais on commence tout de meme par fermer tout les
                        // cells
                        for (Element cell : (List<Element>) row.elements()) {
                            cell.addAttribute(CELL_END, "true");
                        }

                        StringBuffer tmp = new StringBuffer(l);
                        int start = -1;
                        String content = "";
                        matcher = Pattern.compile("([^+|]+|-+)([+|])").matcher(
                                l);
                        for (int cellNumber = 0; matcher.find(); cellNumber++) {
                            int tmpstart = matcher.start(1);
                            int end = matcher.end(1);
                            String tmpcontent = matcher.group(1);
                            String ender = matcher.group(2);
                            if (!tmpcontent.matches("-+")) {
                                // on a forcement un | au dessus du + ou du |
                                // sinon ca veut dire qu'il y avait un + dans la
                                // cell
                                if (lastLine.charAt(end) == '|') {
                                    if (start == -1) {
                                        start = tmpstart;
                                    }
                                    // -1 and +1 to take the + or | at begin and
                                    // end
                                    String old = lastSeparationLine.substring(
                                            start - 1, end + 1);
                                    tmp.replace(start - 1, end + 1, old);
                                    if ("".equals(content)) {
                                        content = tmpcontent;
                                    }
                                    Element cell = null;
                                    if (row.nodeCount() <= cellNumber) {
                                        cell = row.addElement(CELL);
                                    } else {
                                        cell = (Element) row.node(cellNumber);

                                    }
                                    cell.setText(cell.getText() + content
                                            + "\n");
                                    // on a ajouter des choses dans la cell,
                                    // donc
                                    // ce n'est pas la fin
                                    cell.addAttribute(CELL_END, "false");
                                    cell.addAttribute(CELL_INDEX_START, String
                                            .valueOf(start));
                                    cell.addAttribute(CELL_INDEX_END, String
                                            .valueOf(end));
                                    start = end + 1; // +1 to pass + or | at
                                    // end of cell
                                    content = "";
                                } else {
                                    // start = tmpstart;
                                    content += tmpcontent + ender;
                                }
                            }
                        }
                        lastSeparationLine = tmp.toString();
                        row.addAttribute(ROW_END_HEADER, "false");
                        result.add(row);
                        row = DocumentHelper.createElement(ROW);
                        done = true;
                    }
                    if (!done) {
                        log.warn("Bad table format line " + in.getLineNumber());
                    }
                    lastLine = l;
                }

                //
                // line += "\n" + joinBlock(table, "\n", false);
                //
                // result.addText(line);
            } else if (line.matches("^\\s*(=+ +)+=+\\s*$")) {
                // Les donnees de la table peuvent depasser de celle-ci
                /*
                 * ===== ===== ====== Inputs Output ------------ ------ A B A or
                 * B ===== ===== ====== False False Second column of row 1. True
                 * False Second column of row 2.
                 * 
                 * True 2 - Second column of row 3.
                 * 
                 * - Second item in bullet list (row 3, column 2). ============
                 * ======
                 */

                result = DocumentHelper.createElement(TABLE);
                line = line.trim();
                Pattern pBordersEquals = Pattern.compile("^\\s*(=+ +)+=+\\s*$"); // Separation
                // =
                Pattern pBordersTiret = Pattern.compile("^\\s*(-+ +)+-+\\s*$"); // Separation
                // -
                Pattern pBorders = Pattern.compile("^\\s*([=-]+ +)+[=-]+\\s*$"); // =
                                                                                 // ou
                // -
                String[] table = in.readUntilBlank(); // Recuperation de la
                // table

                int tableWidth = line.length();
                int nbSeparations = 0;
                for (String l : table) {
                    if (l.length() > tableWidth)
                        tableWidth = l.length(); // Determination de la
                    // longueur max
                    matcher = pBordersEquals.matcher(l);
                    if (matcher.matches())
                        nbSeparations++;

                }
                // Header if the table contains 3 equals separations
                result.addAttribute(TABLE_HEADER, "" + (nbSeparations == 2));
                int level = level(line);
                result.addAttribute("level", String.valueOf(level));
                result
                        .addAttribute(TABLE_WIDTH, String
                                .valueOf(tableWidth + 1));
                Element row = DocumentHelper.createElement(ROW);
                // Determination of the columns positions
                List columns = new LinkedList();
                matcher = Pattern.compile("=+\\s+").matcher(line);
                for (int cellNumber = 0; matcher.find(); cellNumber++) {
                    columns.add(matcher.end());
                }
                columns.add(tableWidth);

                // Traitement du tbl
                /*
                 * ===== ===== ====== Inputs Output ------------ ------ A B A or
                 * B ===== ===== ====== False False Second column of row 1. True
                 * False Second column of row 2.
                 * 
                 * True 2 - Second column of row 3.
                 * 
                 * - Second item in bullet list (row 3, column 2). ============
                 * ====== devient l'equivalent : ===== ===== ====== Inputs
                 * Output ------------ ------ A B A or B ===== ===== ======
                 * False False Second column of row 1. ----- ----- ------ True
                 * False Second column of row 2. ----- ----- ------ True 2 -
                 * Second column of row 3. - Second item in bullet list (row 3,
                 * column 2). ============ ======
                 */
                String lineRef = line.replace('=', '-');
                Matcher matcher2;
                List tableTmp = new LinkedList();

                for (int i = 0; i < table.length - 1; i++) {
                    tableTmp.add(table[i]);
                    if (!table[i].equals("")) {
                        if (!table[i + 1]
                                .substring(0, (Integer) columns.get(0))
                                .matches("\\s*")) {
                            matcher = pBorders.matcher(table[i]);
                            matcher2 = pBorders.matcher(table[i + 1]);
                            if (!matcher.matches() && !matcher2.matches()
                                    && !table[i + 1].equals("")) {
                                tableTmp.add(lineRef);
                            }
                        }
                    }
                }
                tableTmp.add(table[table.length - 1]);
                table = new String[tableTmp.size()];
                for (int i = 0; i < tableTmp.size(); i++)
                    table[i] = (String) tableTmp.get(i);

                boolean done = false;
                LinkedList lastLines = new LinkedList();
                int separation = 1;
                for (String l : table) {
                    if (l != null) {
                        done = false;
                        matcher = pBordersTiret.matcher(l);
                        matcher2 = pBordersEquals.matcher(l);
                        if (matcher.matches() || matcher2.matches()) { // Intermediate
                            // separation
                            while (!lastLines.isEmpty()) {
                                matcher = Pattern.compile("[-=]+\\s*").matcher(
                                        l);
                                String tmpLine = (String) lastLines.getLast();
                                lastLines.removeLast();
                                int cellNumber;
                                for (cellNumber = 0; matcher.find(); cellNumber++) {
                                    Element cell = null;
                                    if (row.nodeCount() <= cellNumber) {
                                        cell = row.addElement(CELL);
                                    } else {
                                        cell = (Element) row.node(cellNumber);
                                    }
                                    if (matcher.start() < tmpLine.length()) {
                                        if (columns.size() - 1 == cellNumber) {
                                            cell.setText(tmpLine.substring(
                                                    matcher.start(), tmpLine
                                                            .length())
                                                    + "\n");
                                        } else {
                                            if (matcher.end() < tmpLine
                                                    .length())
                                                cell.setText(tmpLine.substring(
                                                        matcher.start(),
                                                        matcher.end())
                                                        + "\n");
                                            else
                                                cell.setText(tmpLine.substring(
                                                        matcher.start(),
                                                        tmpLine.length())
                                                        + "\n");
                                        }
                                    }

                                    if (lastLines.size() == 0) {
                                        row.addAttribute("debug", "pCell");
                                        cell.addAttribute(CELL_END, "true");
                                    } else {
                                        row.addAttribute("debug", "pCellEnd");
                                        cell.addAttribute(CELL_END, "false");
                                    }
                                    cell.addAttribute(CELL_INDEX_START, String
                                            .valueOf(matcher.start() + 1));
                                    if (line.length() == matcher.end())
                                        cell
                                                .addAttribute(
                                                        CELL_INDEX_END,
                                                        String
                                                                .valueOf(columns
                                                                        .get(columns
                                                                                .size() - 1)));
                                    else
                                        cell.addAttribute(CELL_INDEX_END,
                                                String.valueOf(matcher.end()));
                                }

                                if (matcher2.matches()) {
                                    separation++;
                                    row.addAttribute(ROW_END_HEADER, ""
                                            + (separation == 2));
                                } else
                                    row.addAttribute(ROW_END_HEADER, "false");

                                result.add(row);
                                row = DocumentHelper.createElement(ROW);
                                done = true;
                            }
                        }
                        if (!done && l.matches("^\\s*(.+ +)+.+\\s*$")) {
                            // Data
                            lastLines.addFirst(l); // Les donnees sont stoquee
                            // dans une file d'attente
                            // lastLines (FIFO)
                            done = true;
                        }
                        if (!done) {
                            log.warn("Bad table format line "
                                    + in.getLineNumber());
                        }
                    }
                }
            }
        }
        endPeek();

        return result;
    }

    /**
     * read list
     * 
     * <pre>
     * - first line
     * - next line
     * </pre>
     * 
     * @return &lt;bullet_list level="[int]"
     *         bullet="char"&gt;&lt;[text];&lt;/bullet_list&gt;
     * @throws IOException
     */
    public Element peekBulletList() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();
        String line = in.readLine();
        if (line != null
                && line.matches("^\\s*[" + escapeRegex(BULLET_CHAR)
                        + "] +\\S.*")) {
            int level = level(line);
            String bullet = line.substring(level, level + 1);

            result = DocumentHelper.createElement(BULLET_LIST).addAttribute(
                    "level", String.valueOf(level)).addAttribute("bullet",
                    bullet);

            if (!in.eof()) {
                String[] content = readBlock(level + 1);
                line += " " + joinBlock(content);
            }
            String text = line.substring(level + 1).trim();

            result.addText(text);
            in.skipBlankLines();
        }

        endPeek();
        return result;
    }

    /**
     * read field list
     * 
     * <pre>
     * :first: text
     * :second: text
     *   and other text
     * :last empty:
     * </pre>
     * 
     * @return &lt;field_list level="[int]"
     *         name="[text]"&gt;[text]&lt;/field_list&gt;
     * @throws IOException
     */
    public Element peekFieldList() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();
        String line = in.readLine();
        if (line != null) {
            Pattern pattern = Pattern.compile("^\\s*:([^:]+): [^\\s].*");
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int level = level(line);
                String name = matcher.group(1);
                int begin = matcher.end(1) + 1;

                result = DocumentHelper.createElement(FIELD_LIST).addAttribute(
                        "level", String.valueOf(level)).addAttribute("name",
                        name);

                if (!in.eof()) {
                    String[] content = readBlock(level + 1);
                    line += " " + joinBlock(content);
                }
                String text = line.substring(begin).trim();

                result.addText(text);
            }
        }

        endPeek();
        return result;
    }

    /**
     * read definition list
     * 
     * <pre>
     * un autre mot
     *   une autre definition
     * le mot : la classe
     *   la definition
     * le mot : la classe 1 : la classe 2
     *   la definition
     * </pre>
     * 
     * @return &lt;definition_list level="[int]" term="[text]"
     *         classifiers="[text]"&gt;[text]&lt;/definition_list&gt;
     * @throws IOException
     */
    public Element peekDefinitionList() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();
        String[] lines = in.readLines(2);
        if (lines.length == 2) {
            int level = level(lines[0]);
            int levelDef = level(lines[1]);
            if (level < levelDef) {
                in.unread(lines[1], true);
                Pattern pattern = Pattern.compile("^\\s*([^:]+)(?: : (.*))?");
                Matcher matcher = pattern.matcher(lines[0]);
                if (matcher.matches()) {
                    String term = matcher.group(1);
                    String classifiers = matcher.group(2);

                    result = DocumentHelper.createElement(DEFINITION_LIST)
                            .addAttribute("level", String.valueOf(level))
                            .addAttribute("term", term).addAttribute(
                                    "classifiers", classifiers);

                    // poussin 20070207 don't read block here because can't
                    // interpret it correctly in JRSTReader
                    // if (!in.eof()) {
                    // String [] content = readBlock(level + 1);
                    // String text = joinBlock(content);
                    // result.addText(text);
                    // }
                }
            }
        }

        endPeek();
        return result;
    }

    /**
     * read enumarted list
     * 
     * can be: <li>1, 2, 3, ... <li>a, b, c, ... <li>A, B, C, ... <li>i, ii,
     * iii, iv, ... <li>I, II, III, IV, ...
     * 
     * or # for auto-numbered
     * 
     * <pre>
     * 
     * 1. next line 1) next line (1) first line
     * 
     * </pre>
     * 
     * @return &lt;enumerated_list level="[int]" start="[number]"
     *         prefix="[char]" suffix="[char]"
     *         enumtype="[(arabic|loweralpha|upperalpha|lowerroman|upperroman]"&gt
     *         ;[text]&lt;/enumerated_list&gt;
     * @throws IOException
     */
    public Element peekEnumeratedList() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();
        String line = in.readLine();
        if (line != null) {
            Pattern pattern = Pattern
                    .compile("^\\s*(\\(?)(#|\\d+|[a-z]|[A-Z]|[ivxlcdm]+|[IVXLCDM]+)([\\.)]) [^\\s].*");
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int level = level(line);
                String prefix = matcher.group(1);
                String start = matcher.group(2);
                String suffix = matcher.group(3);
                int begin = matcher.end(3);

                // arabic|loweralpha|upperalpha|lowerroman|upperroman
                String enumtype = "auto";
                if (start.matches("\\d+")) {
                    enumtype = "arabic";
                } else if (start.matches("(i|[ivxlcdm][ivxlcdm]+)")) {
                    enumtype = "lowerroman";
                    start = "1"; // TODO transform romain to arabic
                } else if (start.matches("(I|[IVXLCDM][IVXLCDM]+)")) {
                    enumtype = "upperroman";
                    start = "1"; // TODO transform romain to arabic
                } else if (start.matches("[a-z]+")) {
                    enumtype = "loweralpha";
                    start = String.valueOf((int) start.charAt(0) - (int) 'a');
                } else if (start.matches("[A-Z]+")) {
                    enumtype = "upperalpha";
                    start = String.valueOf((int) start.charAt(0) - (int) 'A');
                }

                result = DocumentHelper.createElement(ENUMERATED_LIST)
                        .addAttribute("level", String.valueOf(level))
                        .addAttribute("start", start).addAttribute("prefix",
                                prefix).addAttribute("suffix", suffix)
                        .addAttribute("enumtype", enumtype);

                if (!in.eof()) {
                    String[] content = readBlock(level + 1);
                    line += " " + joinBlock(content);
                }
                String text = line.substring(begin).trim();

                result.addText(text);
                in.skipBlankLines();
            }
        }

        endPeek();
        return result;
    }

    /**
     * Parse un titre simple ou double
     * 
     * simple:
     * 
     * <pre>
     * 
     * Le titre ========
     * 
     * </pre>
     * 
     * double:
     * 
     * <pre>
     * 
     * ============ le titre ============
     * 
     * </pre>
     * 
     * @return &lt;title level="[int]" type="[simple|double]" char="[underline
     *         char]"&gt;
     * @throws IOException
     */
    public Element peekTitle() throws IOException {
        beginPeek();

        Element result = null;
        // in.skipBlankLines();
        String line = in.readLine();

        if (line != null) {
            if (startsWithTitleChar(line)) {
                String[] titles = in.readLines(2);
                if (titles.length == 2 && line.length() >= titles[0].length()
                        && line.length() == titles[1].length()
                        && line.equals(titles[1])) {
                    result = DocumentHelper.createElement(TITLE).addAttribute(
                            "type", "double").addAttribute("char",
                            titles[1].substring(0, 1)).addText(titles[0]);
                }
            } else {
                String title = in.readLine();
                if (title != null && startsWithTitleChar(title)
                        && line.length() == title.length()) {

                    result = DocumentHelper.createElement(TITLE).addAttribute(
                            "type", "simple").addAttribute("char",
                            title.substring(0, 1)).addText(line);
                }
            }
        }

        if (result != null) {
            // add level information
            String titleLevel = result.attributeValue("char");

            if ("double".equals(result.attributeValue("type"))) {
                titleLevel += titleLevel;
            }
            int level = titleLevels.indexOf(titleLevel);
            if (level == -1) {
                level = titleLevels.size();
                titleLevels.add(titleLevel);
            }
            result.addAttribute("level", String
                    .valueOf(JRSTReader.MAX_SECTION_DEPTH + level));
        }

        endPeek();
        return result;
    }

    public Element peekTarget() throws IOException {
        beginPeek();

        String line = in.readLine();
        Element result = null;
        if (line != null) {
            if (line.matches("^\\s*\\.\\.\\s_[^_:].+:.*")) {
                result = DocumentHelper.createElement(TARGET);
                Matcher matcher = Pattern.compile("\\.\\.\\s_").matcher(line);
                if (matcher.find()) {
                    int i = line.indexOf(':');
                    result.addAttribute("id", line.substring(matcher.end(), i)
                            .replaceAll("\\W", "-").toLowerCase());
                    result.addAttribute("level", "" + level(line));
                }
            }
        }
        endPeek();
        return result;
    }

    /**
     * .. _frungible doodads: http://www.example.org/
     * 
     * @return Element
     * @throws IOException
     */
    public LinkedList<Element> refTarget() throws IOException {
        beginPeek();

        String[] lines = in.readAll();
        LinkedList<Element> result = new LinkedList<Element>();
        for (String line : lines) {
            if (line.matches("^\\s*\\.\\.\\s_[^_:].+:.*")) {
                result.add(DocumentHelper.createElement(TARGET));
                Matcher matcher = Pattern.compile("\\.\\.\\s_").matcher(line);
                if (matcher.find()) {
                    boolean done = false;
                    for (int i = matcher.end(); i < line.length() && !done; i++) {
                        if (line.charAt(i) == ':') {
                            result.getLast().addAttribute("level",
                                    "" + level(line));
                            result.getLast().addAttribute(
                                    "id",
                                    line.substring(matcher.end(), i)
                                            .replaceAll("\\W", "-")
                                            .toLowerCase());
                            result.getLast().addAttribute(
                                    "name",
                                    line.substring(matcher.end(), i)
                                            .toLowerCase());
                            if (i + 2 > line.length()) {
                                line = in.readLine();
                                // FIXME 20071129 chatellier
                                // line = null if link is non well formed
                                // .. _Unifying types and classes in Python:
                                // miss uri
                                if (line == null) {
                                    line = "";
                                }
                                result.getLast().addAttribute("refuri",
                                        line.trim());
                            } else
                                result.getLast().addAttribute("refuri",
                                        line.substring(i + 2, line.length()));

                            done = true;
                        }
                    }
                }
            }
        }
        endPeek();
        return result;
    }

    /**
     * .. __: http://www.python.org
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekTargetAnonymousBody() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^\\s*__ .+$|^\\s*\\.\\. __\\:.+$")) {
                result = DocumentHelper.createElement("targetAnonymous");
                result.addAttribute("level", "" + level(line));

            }
        }

        endPeek();
        return result;
    }

    /**
     * .. comment
     * 
     * @return Element
     * @throws IOException
     */
    private Element peekComment() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^\\.\\.\\s*$")) {
                result = DocumentHelper.createElement("comment");
                result.addAttribute("level", "0");
                result.addAttribute("xml:space", "preserve");
                line = in.readLine();
                int level = level(line);
                String[] lines = readBlock(level);
                String text = line.substring(level);
                for (String l : lines)
                    text += "\n" + l.substring(level);
                result.setText(text);
            }
        }

        endPeek();
        return result;
    }

    /**
     * .. _frungible doodads: http://www.example.org/
     * 
     * @return Element
     * @throws IOException
     */
    public Element peekFootnote() throws IOException {
        beginPeek();
        Element result = null;
        String line = in.readLine();
        if (line != null) {
            if (line.matches("^\\s*\\.\\.\\s\\[(#|[0-9]|\\*).*\\]\\s.+$")) {
                result = DocumentHelper.createElement("footnotes");
                boolean bLine = false;
                do {

                    bLine = false;
                    Element footnote = result.addElement(FOOTNOTE);
                    Matcher matcher = Pattern.compile("\\.\\.\\s\\[").matcher(
                            line);

                    if (matcher.find()) {

                        boolean done = false;
                        for (int i = matcher.end(); i < line.length() && !done; i++) {
                            if (line.charAt(i) == ']') {

                                result.addAttribute("level", "" + level(line));
                                String id = line.substring(matcher.end(), i);
                                if (id.matches("\\*")) {
                                    footnote.addAttribute("type", "autoSymbol");
                                } else if (id.matches("[0-9]")) {
                                    footnote.addAttribute("type", "num");
                                    footnote.addAttribute("name", id);
                                } else if (id.equals("#")) {
                                    footnote.addAttribute("type", "autoNum");
                                } else {
                                    footnote.addAttribute("type",
                                            "autoNumLabel");
                                    footnote.addAttribute("name", id
                                            .substring(1));
                                }
                                String text = line.substring(i + 2, line
                                        .length());

                                int levelAv = level(line);
                                line = in.readLine();
                                if (line != null) {
                                    if (line
                                            .matches("^\\s*\\.\\.\\s\\[(#|[0-9]|\\*).*\\]\\s.+$")) {
                                        bLine = true;
                                    } else {

                                        int level = level(line);
                                        if (levelAv < level) {
                                            String[] lines = in
                                                    .readWhile("(^ {" + level
                                                            + "}.*)|(\\s*)");
                                            text += "\n" + line.trim();
                                            for (String l : lines)
                                                text += "\n" + l.trim();

                                        } else if (line.matches("\\s*")) {
                                            level = levelAv + 1;
                                            String[] lines = in
                                                    .readWhile("(^ {" + level
                                                            + "}.*)|(\\s*)");
                                            text += "\n" + line.trim();
                                            for (String l : lines)
                                                text += "\n" + l.trim();

                                        }
                                    }
                                    if (!bLine) {
                                        in.skipBlankLines();
                                        String[] linesTmp = in
                                                .readWhile("^\\s*\\.\\.\\s\\[(#|[0-9]|\\*).*\\]\\s.+$");

                                        if (linesTmp.length > 0) {
                                            line = linesTmp[0];
                                            bLine = true;
                                        }
                                    }

                                }
                                if (line == null)
                                    line = "";
                                footnote.setText(text);
                                done = true;
                            }

                        }
                    }

                } while (bLine);
            }
        }
        endPeek();
        return result;
    }

    /**
     * Read block text, block text have same indentation
     * 
     * @param minLeftMargin min left blank needed to accept to read block
     * @return String
     * @throws IOException
     */
    private String readBlockWithBlankLine(int level) throws IOException {
        String txt = "";
        String[] lines = in.readWhile("(^ {" + level + "}.*)|(\\s*)");
        while (lines.length > 0) {
            for (String l : lines) {
                l = l.trim();
                txt += l + "\n";

            }
            lines = in.readWhile("(^ {" + level + "}.*)|(\\s*)");
        }
        return txt;
    }

    /**
     * Lit les premieres ligne non vide et les retourne, rien n'est modifier par
     * rapport aux positions dans le fichier. Util pour afficher a l'utilisateur
     * les lignes qui ont produit une erreur
     * 
     * @return
     * @throws IOException
     */
    public String readNotBlanckLine() throws IOException {
        beginPeek();
        in.skipBlankLines();
        String line = joinBlock(in.readUntilBlank(), "\n", false);
        endPeek();
        return line;
    }

    /**
     * return the number of line read
     * 
     * @return int
     * @throws IOException
     */
    public int getLineNumber() {
        return in.getLineNumber();
    }

    /**
     * return the number of char read
     * 
     * @return int
     * @throws IOException
     */
    public int getCharNumber() {
        return in.getCharNumber();
    }

    /**
     * return true if line can be underline or overline for title
     * 
     * @param line
     * @return boolean
     */
    private boolean startsWithTitleChar(String line) {
        if (line == null || line.length() < 2) {
            return false;
        }
        // est-ce que la ligne est constituer entierement du meme caractere et
        // qu'il y en a au moins 2
        boolean result = line
                .matches("([" + escapeRegex(TITLE_CHAR) + "])\\1+");
        return result;
    }

    /**
     * @param title_charescapeRegex
     * @return String
     */
    private String escapeRegex(String text) {
        String result = text.replaceAll("([()[\\\\]*+?.])", "\\\\$1");
        return result;
    }

    /**
     * @param String line
     * @return int
     * @throws IOException
     */
    private int level(String line) {
        int result = 0;
        while (line.length() > result && line.charAt(result) == ' ') {
            result++;
        }
        return result;
    }
}
