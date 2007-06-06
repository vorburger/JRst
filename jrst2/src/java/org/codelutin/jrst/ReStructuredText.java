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
 * Docbook.java
 *
 * Created: 27 oct. 06 11:10:30
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import java.util.regex.Pattern;


/**
 * @author poussin
 *
 */

public class ReStructuredText {
    
    public static final String DTD = "http://docutils.sourceforge.net/docs/ref/docutils.dtd";
    
    public static final String TITLE_CHAR = "-=-~'`^+:!\"#$%&*,./;|?@\\_[\\]{}<>()";
    public static final String QUOTED_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    public static final String BULLET_CHAR = "*" + "+" + "-" + "\u2022" + "\u2023" + "\u2043";
    public static final String DOCINFO_ITEM =
        "author|authors|organization|address|contact|version|revision|status|date|copyright";
    public static final String FOOTNOTE_SYMBOL = "\u002A"+"\u2020"+"\u2021"+"\u00A7"+"\u00B6"+"\u0023"+"\u2660"+"\u2665"+"\u2666"+"\u2663";
    // public static final String ADMONITION = "admonition|caution|danger|error|hint|important|note|tip|warning";
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Root Element
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String DOCUMENT = "document";
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Title Elements
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Bibliographic Elements
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String DOCINFO = "docinfo";
//    public static final String INFO = "info";
    public static final String AUTHOR = "author";
    public static final String AUTHORS = "authors";
    public static final String ORGANIZATION = "organization";
    public static final String ADDRESS = "address";
    public static final String CONTACT = "contact";
    public static final String VERSION = "version";
    public static final String REVISION = "revision";
    public static final String STATUS = "status";
    public static final String DATE =  "date";
    public static final String COPYRIGHT = "copyright";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Decoration Elements
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String DECORATION = "decoration";
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Structural Elements
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String SECTION = "section";
    public static final String TOPIC = "topic";
    public static final String SIDEBAR = "sidebar";
    public static final String TRANSITION = "transition";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Body Elements
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String PARAGRAPH = "paragraph";
    public static final String COMPOUND = "compound";
    public static final String CONTAINER = "container";
    public static final String BULLET_LIST = "bullet_list";
    public static final String ENUMERATED_LIST = "enumerated_list";
    public static final String LIST_ITEM = "list_item";
    public static final String DEFINITION_LIST = "definition_list";
    public static final String DEFINITION_LIST_ITEM = "definition_list_item";
    public static final String TERM = "term";
    public static final String CLASSIFIER = "classifier";
    public static final String DEFINITION = "definition";
    public static final String FIELD_LIST = "field_list";
    public static final String FIELD = "field";
    public static final String FIELD_NAME = "field_name";
    public static final String FIELD_BODY = "field_body";
    public static final String OPTION_LIST = "option_list";
    public static final String OPTION_LIST_ITEM = "option_list_item";
    public static final String OPTION_GROUP = "option_group";
    public static final String OPTION = "option";
    public static final String OPTION_STRING = "option_string";
    public static final String OPTION_ARGUMENT = "option_argument";
    public static final String DESCRIPTION = "description";
    public static final String LITERAL_BLOCK = "literal_block";
    public static final String LINE_BLOCK = "line_block";
    public static final String LINE = "line";
    public static final String BLOCK_QUOTE = "block_quote";
    public static final String ATTRIBUTION = "attribution";
    public static final String DOCTEST_BLOCK = "doctest_block";
    public static final String ATTENTION = "attention";
    public static final String CAUTION = "caution";
    public static final String DANGER = "danger";
    public static final String ERROR = "error";
    public static final String HINT = "hint";
    public static final String IMPORTANT = "important";
    public static final String NOTE = "note";
    public static final String TIP = "tip";
    public static final String WARNING = "warning";
    public static final String ADMONITION = "admonition";
    public static final String FOOTNOTE = "footnote";
    public static final String CITATION = "citation";
    public static final String LABEL = "label";
    public static final String RUBRIC = "rubric";
    public static final String TARGET = "target";
    public static final String SUBSTITUTION_DEFINITION = "substitution_definition";
    public static final String COMMENT = "comment";
    public static final String PENDING = "pending";
    public static final String FIGURE = "figure";
    public static final String IMAGE = "image";
    public static final String CAPTION = "caption";
    public static final String LEGEND = "legend";
    public static final String SYSTEM_MESSAGE = "system_message";
    public static final String RAW = "raw";
    
    // table
    public static final String TABLE = "table";
    public static final String TGROUP = "tgroup";
    public static final String COLSPEC = "colspec";
    public static final String THEAD = "thead";
    public static final String TBODY = "tbody";
    public static final String ROW = "row";
    public static final String ENTRY = "entry";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Inline Elements
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final String EMPHASIS = "emphasis";
    public static final String STRONG = "strong";
    public static final String LITERAL = "literal";
    public static final String REFERENCE = "reference";
    public static final String FOOTNOTE_REFERENCE = "footnote_reference";
    public static final String CITATION_REFERENCE = "citation_reference";
    public static final String SUBSTITUTION_REFERENCE = "substitution_reference";
    public static final String TITLE_REFERENCE = "title_reference";
    public static final String ABBREVIATION = "abbreviation";
    public static final String ACRONYM = "acronym";
    public static final String SUPERSCRIPT = "superscript";
    public static final String SUBSCRIPT = "subscript";
    public static final String INLINE = "inline";
    public static final String PROBLEMATIC = "problematic";
    public static final String GENERATED = "generated";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Inline Elements Regex
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final Pattern REGEX_EMPHASIS = Pattern.compile("\\*([^*(\\]_.+\\[)].+?)\\*");
    public static final Pattern REGEX_STRONG = Pattern.compile("\\*\\*(.+?)\\*\\*");
    public static final Pattern REGEX_LITERAL = Pattern.compile("``([^`]+)``");
    public static final Pattern REGEX_REFERENCE = Pattern.compile("(http://[-/%#[\\&&&[^(&gt;)]]\\._\\w]+\\w+)((\\W|&|$)+)");
    public static final Pattern REGEX_INLINE_REFERENCE = Pattern.compile("`(.+) \\&lt\\;(http://[-/%#&\\._\\w]+)(\\&gt\\;)`");
    public static final Pattern REGEX_EMAIL = Pattern.compile("(^|[^_\\w])([-\\._\\w]+@[-\\._\\w]+)([^-\\._\\w]|$)");
    public static final Pattern REGEX_FOOTNOTE_REFERENCE = Pattern.compile("\\[(#|[0-9]|\\*)\\w*\\]_");
    		//"\\[([0-9]+?|#)\\]");
    public static final Pattern REGEX_CITATION_REFERENCE = Pattern.compile("\\[([^\\]]+?)\\]");
    public static final Pattern REGEX_SUBSTITUTION_REFERENCE = Pattern.compile("\\|([^|]+?)\\|");
    public static final Pattern REGEX_ABBREVIATION = Pattern.compile("(.*?)");
    public static final Pattern REGEX_ACRONYM = Pattern.compile("(.*?)");
    public static final Pattern REGEX_SUPERSCRIPT = Pattern.compile("(.*?)");
    public static final Pattern REGEX_SUBSCRIPT = Pattern.compile("(.*?)");
    public static final Pattern REGEX_INLINE = Pattern.compile("(.*?)");
    public static final Pattern REGEX_PROBLEMATIC = Pattern.compile("(.*?)");
    public static final Pattern REGEX_GENERATED = Pattern.compile("(.*?)");
    public static final Pattern REGEX_HYPERLINK_REFERENCE = Pattern.compile("(\\`[^_<>]+\\`_(\\W|$))|((&apos;|`)[^_<`(&apos;)>]+(&apos;|`)_(\\W|$))|([\\S]+[^\\s<>\\.`]+_(\\W|$))");
    public static final Pattern REGEX_ANONYMOUS_HYPERLINK_REFERENCE = Pattern.compile("(\\`[^<>`\\]\\[]+\\`__)|(\\w+[^()`\\s<>]+__)");
}


