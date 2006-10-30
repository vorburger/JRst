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

import static org.codelutin.jrst.ReStructuredText.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * Le principe est de positionner la mark du {@link AdvancedReader} lors du
 * debut d'une methode peek*, puis a la fin de la methode de regarder le nombre
 * de caractere utilisé pour la methode et de faire un reset.
 * <p>
 * Le nombre de caractere utilisé servira pour le remove lorsque l'utilisateur
 * indiquera qu'il utilise l'element retourné, si l'utilisateur n'appelle pas
 * remove alors il peut relire autant de fois qu'il veut le meme element, ou
 * essayer d'en lire un autre.
 * <p>
 * Pour mettre en place ce mecanisme le plus simple est d'utiliser les methodes 
 * {@link JRSTLexer#beginPeek()} et {@link JRSTLexer#endPeek()} 
 * 
 * @author poussin
 */
public class JRSTLexer {

    /**
     * retient le niveau du titre, pour un titre de type double, on met 
     * deux fois le caratere dans la chaine, sinon on le met une seul fois.
     * 
     * <pre>
     * =====
     * Super
     * =====
     * 
     * titre
     * -----
     * </pre>
     * 
     * donnera dans la liste ["==", "-"]
     */
    private List<String> titleLevels = null;
    private AdvancedReader in = null;
    /** length of the last element returned (number of char need to this element) */
    private int elementLength = 0;
    
    public JRSTLexer(Reader reader) {
        titleLevels = new ArrayList<String>();
        this.in = new AdvancedReader(reader);
    }

    /**
     * true if no more element to read
     * @return
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
     * @return
     * @throws IOException 
     */
    public void remove() throws IOException {
        in.skip(elementLength);
    }
        
    private void beginPeek() throws IOException {
        elementLength = 0;
        in.mark();
    }
    
    private void endPeek() throws IOException {
        elementLength = in.readSinceMark();
        in.reset();
    }
    
    /**
     * Read block text, block text have same indentation and is continu (no
     * blank line)
     * 
     * @param minLeftMargin min left blank needed to accept to read block
     * @return
     * @throws IOException 
     */
    private String [] readBlock(int minLeftMargin) throws IOException {
        String [] result = new String[0]; 
        String firstLine = in.readLine();        
        if (firstLine != null) {
            in.unread(firstLine, true);
            int level = level(firstLine);
            if (level >= minLeftMargin) {
                result = in.readWhile("^ {"+level+"}\\S+.*");
            }
        }
        
        return result;
    }
    
    /**
     * All lines are joined and left and right spaces are removed during join 
     * 
     * @param lines
     * @return
     */
    private String joinBlock(String [] lines) {
        String result = joinBlock(lines, " ", true);
        return result;
    }
    
    private String joinBlock(String [] lines, String joinSep, boolean trim) {
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
     * Return title or para
     * @return
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
     * <pre>
     * :author: Benjamin Poussin
     * :address: 
     *   Quelque part
     *   Dans le monde
     * </pre>
     * 
     * @return
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
     * @return
     * @throws IOException
     */
    public Element peekBodyElement() throws IOException {
        Element result = null;
        if (result == null) {
            result = peekTransition();
        }
        if (result == null) {
            result = peekTable();
        }
        if (result == null) {
            result = peekBulletList();
        }
        if (result == null) {
            result = peekEnumeratedList();
        }
        if (result == null) {
            result = peekDefinitionList();
        }
        if (result == null) {
            result = peekFieldList();
        }
        if (result == null) {
            result = peekLiteralBlock();
        }        
        if (result == null) {
            result = peekPara();
        }        
        
        return result;
    }
    
    public Element peekTransition() throws IOException {
        beginPeek();
        
        Element result = null;

        // must have one blank line before
        String line = in.readLine();
        if (line != null && line.matches("\\s*")) {
            in.skipBlankLines();
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
     * read paragraph with attribut level that represente the space numbers
     * at left side 
     * 
     * @return &lt;paragraph level="[int]"&gt[text]&lt;/paragraph&gt;
     * @throws IOException 
     */
    public Element peekPara() throws IOException {
        beginPeek();
        
        Element result = null;
        in.skipBlankLines();
        
        String [] lines = readBlock(0);
        if (lines.length > 0) {            
            int level = level(lines[0]);
            String para = joinBlock(lines);
            
            if (para.endsWith(": ::")) {
                para = para.substring(0, para.length() - " ::".length());
                in.unread("::", true);
            } else if (para.endsWith("::")) {
                para = para.substring(0, para.length() - ":".length()); // keep one :
                in.unread("::", true);                
            }
            
            result = DocumentHelper.createElement(PARAGRAPH)
            .addAttribute("level", String.valueOf(level))
            .addText(para);
        }

        endPeek();        
        return result;
    }

    public Element peekLiteralBlock() throws IOException {
        beginPeek();
        
        Element result = null;
        in.skipBlankLines();
        
        String [] prefix = in.readLines(2);
        if (prefix.length == 2 &&
                prefix[0].matches("::\\s*") && prefix[1].matches("\\s*")) {

            String para = in.readLine();
            if (para != null) {
                int level = level(para);
                para = para.substring(level) + "\n";
                
                // it's literal block until level is down 
                String [] lines = in.readWhile("^ {"+level+"}.*");
                while (lines.length > 0) {
                    for (String line : lines) {
                        para += line.substring(level) + "\n";
                    }                    
                    in.skipBlankLines();
                    lines = in.readWhile("^ {"+level+"}.*");
                }
                
                result = DocumentHelper.createElement(LITERAL_BLOCK)
                .addAttribute("level", String.valueOf(level))
                .addText(para);
            }
        }

        endPeek();        
        return result;
    }

    /**
     * read doc info author, date, version, ...
     * <pre>
     * :author: Benjamin Poussin
     * :address: 
     *   Quelque part
     *   Dans le monde
     * </pre>
     * 
     * @return
     * @throws IOException 
     */
    public Element peekDocInfoItem() throws IOException {
        beginPeek();

        Element result = null;
        in.skipBlankLines();
        String line = in.readLine();
        // (?i) case inensitive on docinfo item
        if (line.matches("^:((?i)"+DOCINFO_ITEM+"):.*$")) {
            result = DocumentHelper.createElement(DOCINFO);
            result.addAttribute("level", "0");
            String infotype = line.substring(1, line.indexOf(":", 1));
            
            if (!in.eof()) {
                String [] content = readBlock(1);
                line += joinBlock(content);
            }            
            String text = line.substring(line.indexOf(":", 1) + 1).trim();
            
            // CVS, RCS support
            text = text.replaceAll("\\$\\w+: (.+?)\\$", "$1");
            
            result.addAttribute("type", infotype).addText(text);
        }

        endPeek();

        return result;
    }

    public Element peekTable() throws IOException {
        // TODO now we take table as LITERAL_BLOCK, but in near
        // futur we must parse correctly TABLE (show JRSTGenerator and JRSTReader too)
        beginPeek();

        Element result = null;
        in.skipBlankLines();
        String line = in.readLine();

        if (line != null && line.matches("^\\s*(\\+-+)+\\+\\s*$")) { // complexe table 
            result = DocumentHelper.createElement(TABLE);
            result.addAttribute("level", String.valueOf(level(line)));

            String [] table = in.readUntilBlank();
            line += "\n" + joinBlock(table, "\n", false);
            
            result.addText(line);
        } else if (line != null && line.matches("^\\s*(=+ +)+=+\\s*$")) { // simple table
            // dans les tables simples il peut y avoir des lignes blanches au
            // milieu. Mais la premiere et la derniere lignes sont identiques
            String first = line;
            
            result = DocumentHelper.createElement(TABLE);
            result.addAttribute("level", String.valueOf(level(line)));

            String [] table = in.readUntil(first);
            line += "\n" + joinBlock(table, "\n", false);
            String next = in.readLine();
            line += "\n" + next;
            
            next = in.readLine();
            if (line != null) {
                if (next.matches("\\s*")) {
                    // no header
                    in.unread(next, true);
                } else {
                    // read body table
                    table = in.readUntil(first);
                    line += "\n" + joinBlock(table, "\n", false);                
                    next = in.readLine();
                    line += "\n" + next;
                }
            }
            
            result.addText(line);
        }

        endPeek();

        return result;
    }

    /**
     * read list
     * <pre>
     * - first line
     * - next line
     * </pre>
     * 
     * @return &lt;bullet_list level="[int]" bullet="char"&gt;&lt;[text];&lt;/bullet_list&gt;
     * @throws IOException 
     */
    public Element peekBulletList() throws IOException {
        beginPeek();

        Element result = null;
        in.skipBlankLines();
        String line = in.readLine();
        if (line != null && line.matches("^\\s*["+escapeRegex(BULLET_CHAR)+"] \\S.*")) {
            int level = level(line);
            String bullet = line.substring(level, level + 1);

            result = DocumentHelper.createElement(BULLET_LIST)
            .addAttribute("level", String.valueOf(level))
            .addAttribute("bullet", bullet);
            
            if (!in.eof()) {
                String [] content = readBlock(level + 1);
                line += " " + joinBlock(content);
            }            
            String text = line.substring(level + 1).trim();
            
            result.addText(text);
        }

        endPeek();
        return result;
    }

    /**
     * read field list
     * <pre>
     * :first: text
     * :second: text
     *   and other text
     * :last empty:
     * </pre>
     * 
     * @return &lt;field_list level="[int]" name="[text]"&gt;[text]&lt;/field_list&gt;
     * @throws IOException 
     */
    public Element peekFieldList() throws IOException {
        beginPeek();

        Element result = null;
        in.skipBlankLines();
        String line = in.readLine();
        if (line != null) {
            Pattern pattern = Pattern.compile("^\\s*:([^:]+): [^\\s].*");
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int level = level(line);
                String name = matcher.group(1);
                int begin = matcher.end(1) + 1;

                result = DocumentHelper.createElement(FIELD_LIST)
                .addAttribute("level", String.valueOf(level))
                .addAttribute("name", name);

                if (!in.eof()) {
                    String [] content = readBlock(level + 1);
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
     * <pre>
     * un autre mot
     *   une autre definition
     *
     * le mot : la classe
     *   la definition
     *
     * le mot : la classe 1 : la classe 2
     *   la definition
     * </pre>
     * 
     * @return &lt;definition_list level="[int]" term="[text]" classifiers="[text]"&gt;[text]&lt;/definition_list&gt;
     * @throws IOException 
     */
    public Element peekDefinitionList() throws IOException {
        beginPeek();

        Element result = null;
        in.skipBlankLines();
        String [] lines = in.readLines(2);
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
                    .addAttribute("term", term)
                    .addAttribute("classifiers", classifiers);

                    if (!in.eof()) {
                        String [] content = readBlock(level + 1);
                        String text = joinBlock(content);
                        result.addText(text);
                    }
                }
            }
        }
        
        endPeek();
        return result;
    }
    
    /**
     * read enumarted list
     * 
     * can be:
     * <li> 1, 2, 3, ...
     * <li> a, b, c, ...
     * <li> A, B, C, ...
     * <li> i, ii, iii, iv, ...
     * <li> I, II, III, IV, ...
     *
     * or # for auto-numbered
     * 
     * <pre>
     * 1. next line
     * 1) next line
     * (1) first line
     * </pre>
     * 
     * @return &lt;enumerated_list level="[int]" start="[number]" prefix="[char]" suffix="[char]" enumtype="[(arabic|loweralpha|upperalpha|lowerroman|upperroman]"&gt;[text]&lt;/enumerated_list&gt;
     * @throws IOException 
     */
    public Element peekEnumeratedList() throws IOException {
        beginPeek();

        Element result = null;
        in.skipBlankLines();
        String line = in.readLine();
        if (line != null) {
            Pattern pattern = Pattern.compile("^\\s*(\\(?)(#|\\d+|[a-z]|[A-Z]|[ivxlcdm]+|[IVXLCDM]+)([\\.)]) [^\\s].*");
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
                    start = String.valueOf((int)start.charAt(0) -(int)'a');
                } else if (start.matches("[A-Z]+")) {
                    enumtype = "upperalpha";
                    start = String.valueOf((int)start.charAt(0) -(int)'A');
                }
                
                result = DocumentHelper.createElement(ENUMERATED_LIST)
                .addAttribute("level", String.valueOf(level))
                .addAttribute("start", start)
                .addAttribute("prefix", prefix)
                .addAttribute("suffix", suffix)
                .addAttribute("enumtype", enumtype);

                if (!in.eof()) {
                    String [] content = readBlock(level + 1);
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
     * Parse un titre simple ou double
     * 
     * simple:
     * <pre>
     * Le titre
     * ========
     * </pre>
     * 
     * double:
     * <pre>
     * ============
     *   le titre
     * ============
     * </pre>
     * 
     * @return &lt;title level="[int]" type="[simple|double]" char="[underline char]"&gt;
     * @throws IOException 
     */
    public Element peekTitle() throws IOException {
        beginPeek();

        Element result = null;
        in.skipBlankLines();
        String line = in.readLine();
        if (startsWithTitleChar(line)) {
            String [] titles = in.readLines(2);
            if (titles.length == 2
                    && line.length() >= titles[0].length()
                    && line.length() == titles[1].length()
                    && line.equals(titles[1])) {
                result = DocumentHelper.createElement(TITLE)
                .addAttribute("type", "double")
                .addAttribute("char", titles[1].substring(0, 1))
                .addText(titles[0]);
            }
        } else {
            String title = in.readLine();
            if (title != null &&
                    startsWithTitleChar(title) &&
                    line.length() == title.length()) {
                result = DocumentHelper.createElement(TITLE)
                .addAttribute("type", "simple")
                .addAttribute("char", title.substring(0, 1))
                .addText(line);
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
            result.addAttribute("level", String.valueOf(JRSTReader.MAX_SECTION_DEPTH + level));
        }
        
        endPeek();
        return result;
    }

    /**
     * return true if line can be underline or overline for title
     * @param line
     * @return
     */
    private boolean startsWithTitleChar(String line) {
        if (line == null || line.length() < 2) {
            return false;
        }
        // est-ce que la ligne est constituer entierement du meme caractere et
        // qu'il y en a au moins 2
        boolean result = line.matches("(["+escapeRegex(TITLE_CHAR)+"])\\1+");
        return result;
    }
    
    /**
     * @param title_char
     * @return
     */
    private String escapeRegex(String text) {
        String result = text.replaceAll("([()[\\\\]*+?.])", "\\\\$1");
        return result;
    }

    private int level(String line) {
        int result = 0;        
        while (line.length() > result && line.charAt(result) == ' ') {
            result++;
        }
        return result;
    }
    
}


