/*
 * #%L
 * JRst :: Api
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2004 - 2010 CodeLutin
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.nuiton.jrst.convertisor;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.nuiton.jrst.ReStructuredText;

/**
 *
 * @author letellier
 */
public abstract class DocUtilsVisitor extends VisitorSupport {

    public static final String EMPTY_STRING = "";
    public static final String LINE_SEPARATOR = "\n";
    public static final String SPACE = " ";
    /** to use log facility, just put in your code: log.info(\"...\"); */
    private static Log log = LogFactory.getLog(DocUtilsVisitor.class);
    // Enregistre les elements parse pour ne jamais les revisiter
    protected List<Element> cachedElements;
    // le resultat de la visite
    protected StringBuilder rstResult;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Root Element
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public abstract String composeDocument(Element e);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Title Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public abstract String composeTitle(Element e);

    public abstract String composeSubTitle(Element e);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Bibliographic Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public abstract String composeDocInfo(Element e);

    // public abstract String compose
    public abstract String composeAutor(Element e);

    public abstract String composeAutors(Element e);

    public abstract String composeOrganisation(Element e);

    public abstract String composeAddress(Element e);

    public abstract String composeContact(Element e);

    public abstract String composeVersion(Element e);

    public abstract String composeResvision(Element e);

    public abstract String composeStatus(Element e);

    public abstract String composeDate(Element e);

    public abstract String composeCopyright(Element e);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Decoration Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public abstract String composeDecoration(Element e);

    public abstract String composeHeader(Element e);

    public abstract String composeFooter(Element e);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Structural Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public abstract String composeSection(Element e);

    public abstract String composeTopic(Element e);

    public abstract String composeSideBar(Element e);

    public abstract String composeTransition(Element e);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Body Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public abstract String composeParagraph(Element e);

    public abstract String composeCompound(Element e);

    public abstract String composeContainer(Element e);

    public abstract String composeBulletList(Element e);

    public abstract String composeEnumeratedList(Element e);

    public abstract String composeListItem(Element e);

    public abstract String composeDefinitionList(Element e);

    public abstract String composeDefinitionListItem(Element e);

    public abstract String composeTerm(Element e);

    public abstract String composeClassifier(Element e);

    public abstract String composeDefinition(Element e);

    public abstract String composeFieldList(Element e);

    public abstract String composeField(Element e);

    public abstract String composeFieldName(Element e);

    public abstract String composeFieldBody(Element e);

    public abstract String composeOptionList(Element e);

    public abstract String composeOptionListItem(Element e);

    public abstract String composeOptionGroup(Element e);

    public abstract String composeOption(Element e);

    public abstract String composeOptionString(Element e);

    public abstract String composeOptionArgument(Element e);

    public abstract String composeDescription(Element e);

    public abstract String composeLiteralBlock(Element e);

    public abstract String composeLineBlock(Element e);

    public abstract String composeLine(Element e);

    public abstract String composeBlockQuote(Element e);

    public abstract String composeAttribution(Element e);

    public abstract String composeDocTestBlock(Element e);

    public abstract String composeAttention(Element e);

    public abstract String composeCaution(Element e);

    public abstract String composeDanger(Element e);

    public abstract String composeError(Element e);

    public abstract String composeHint(Element e);

    public abstract String composeImportant(Element e);

    public abstract String composeNote(Element e);

    public abstract String composeTip(Element e);

    public abstract String composeWarning(Element e);

    public abstract String composeAdmonition(Element e);

    public abstract String composeFootnote(Element e);

    public abstract String composeCitation(Element e);

    public abstract String composeLabel(Element e);

    public abstract String composeRubric(Element e);

    public abstract String composeTarget(Element e);

    public abstract String composeSubstitutionDefinition(Element e);

    public abstract String composeComment(Element e);

    public abstract String composePending(Element e);

    public abstract String composeFigure(Element e);

    public abstract String composeImage(Element e);

    public abstract String composeCaption(Element e);

    public abstract String composeLegend(Element e);

    public abstract String composeSystemMessage(Element e);

    public abstract String composeRaw(Element e);

    // table
    public abstract String composeTable(Element e);

    public abstract String composeTgroup(Element e);

    public abstract String composeColspec(Element e);

    public abstract String composeThead(Element e);

    public abstract String composeTbody(Element e);

    public abstract String composeRow(Element e);

    public abstract String composeEntry(Element e);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Inline Elements
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public abstract String composeEmphasis(Element e);

    public abstract String composeStrong(Element e);

    public abstract String composeLiteral(Element e);

    public abstract String composeReference(Element e);

    public abstract String composeFootnoteReference(Element e);

    public abstract String composeCitationReference(Element e);

    public abstract String composeSubstitutionReference(Element e);

    public abstract String composeTitleReference(Element e);

    public abstract String composeAbbreviation(Element e);

    public abstract String composeAcronyme(Element e);

    public abstract String composeSuperscipt(Element e);

    public abstract String composeSubscript(Element e);

    public abstract String composeInline(Element e);

    public abstract String composeProblematic(Element e);

    public abstract String composeGenerated(Element e);

    public DocUtilsVisitor() {
        cachedElements = new ArrayList<Element>();
        rstResult = new StringBuilder();
    }

    @Override
    public void visit(Element e) {

        if (!cachedElements.contains(e)) {
            log.debug("entre element " + e.getName());
            addCachedElement(e);

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Root Element
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            if (elementEquals(ReStructuredText.DOCUMENT, e)) {
                appendResult(composeDocument(e));
            } // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Title Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            else if (elementEquals(ReStructuredText.TITLE, e)) {
                appendResult(composeTitle(e));
            } else if (elementEquals(ReStructuredText.SUBTITLE, e)) {
                appendResult(composeSubTitle(e));
            } // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Bibliographic Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            else if (elementEquals(ReStructuredText.DOCINFO, e)) {
                appendResult(composeDocInfo(e));

                // } else if (elementEquals(ReStructuredText.INFO,e)) { appendResult(composeInfo(e));
            } else if (elementEquals(ReStructuredText.AUTHOR, e)) {
                appendResult(composeAutor(e));
            } else if (elementEquals(ReStructuredText.AUTHORS, e)) {
                appendResult(composeAutors(e));
            } else if (elementEquals(ReStructuredText.ORGANIZATION, e)) {
                appendResult(composeOrganisation(e));
            } else if (elementEquals(ReStructuredText.ADDRESS, e)) {
                appendResult(composeAddress(e));
            } else if (elementEquals(ReStructuredText.CONTACT, e)) {
                appendResult(composeContact(e));
            } else if (elementEquals(ReStructuredText.VERSION, e)) {
                appendResult(composeVersion(e));
            } else if (elementEquals(ReStructuredText.REVISION, e)) {
                appendResult(composeResvision(e));
            } else if (elementEquals(ReStructuredText.STATUS, e)) {
                appendResult(composeStatus(e));
            } else if (elementEquals(ReStructuredText.DATE, e)) {
                appendResult(composeDate(e));
            } else if (elementEquals(ReStructuredText.COPYRIGHT, e)) {
                appendResult(composeCopyright(e));
            } // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Decoration Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            else if (elementEquals(ReStructuredText.DECORATION, e)) {
                appendResult(composeDecoration(e));
            } else if (elementEquals(ReStructuredText.HEADER, e)) {
                appendResult(composeHeader(e));
            } else if (elementEquals(ReStructuredText.FOOTER, e)) {
                appendResult(composeFooter(e));
            } // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Structural Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            else if (elementEquals(ReStructuredText.SECTION, e)) {
                appendResult(composeSection(e));
            } else if (elementEquals(ReStructuredText.TOPIC, e)) {
                appendResult(composeTopic(e));
            } else if (elementEquals(ReStructuredText.SIDEBAR, e)) {
                appendResult(composeSideBar(e));
            } else if (elementEquals(ReStructuredText.TRANSITION, e)) {
                appendResult(composeTransition(e));
            } // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Body Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            else if (elementEquals(ReStructuredText.PARAGRAPH, e)) {
                appendResult(composeParagraph(e));
            } else if (elementEquals(ReStructuredText.COMPOUND, e)) {
                appendResult(composeCompound(e));
            } else if (elementEquals(ReStructuredText.CONTAINER, e)) {
                appendResult(composeContainer(e));
            } else if (elementEquals(ReStructuredText.BULLET_LIST, e)) {
                appendResult(composeBulletList(e));
            } else if (elementEquals(ReStructuredText.ENUMERATED_LIST, e)) {
                appendResult(composeEnumeratedList(e));
            } else if (elementEquals(ReStructuredText.LIST_ITEM, e)) {
                appendResult(composeListItem(e));
            } else if (elementEquals(ReStructuredText.DEFINITION_LIST, e)) {
                appendResult(composeDefinitionList(e));
            } else if (elementEquals(ReStructuredText.DEFINITION_LIST_ITEM, e)) {
                appendResult(composeDefinitionListItem(e));
            } else if (elementEquals(ReStructuredText.TERM, e)) {
                appendResult(composeTerm(e));
            } else if (elementEquals(ReStructuredText.CLASSIFIER, e)) {
                appendResult(composeClassifier(e));
            } else if (elementEquals(ReStructuredText.DEFINITION, e)) {
                appendResult(composeDefinition(e));
            } else if (elementEquals(ReStructuredText.FIELD_LIST, e)) {
                appendResult(composeFieldList(e));
            } else if (elementEquals(ReStructuredText.FIELD, e)) {
                appendResult(composeField(e));
            } else if (elementEquals(ReStructuredText.FIELD_NAME, e)) {
                appendResult(composeFieldName(e));
            } else if (elementEquals(ReStructuredText.FIELD_BODY, e)) {
                appendResult(composeFieldBody(e));
            } else if (elementEquals(ReStructuredText.OPTION_LIST, e)) {
                appendResult(composeOptionList(e));
            } else if (elementEquals(ReStructuredText.OPTION_LIST_ITEM, e)) {
                appendResult(composeOptionListItem(e));
            } else if (elementEquals(ReStructuredText.OPTION_GROUP, e)) {
                appendResult(composeOptionGroup(e));
            } else if (elementEquals(ReStructuredText.OPTION, e)) {
                appendResult(composeOption(e));
            } else if (elementEquals(ReStructuredText.OPTION_STRING, e)) {
                appendResult(composeOptionString(e));
            } else if (elementEquals(ReStructuredText.OPTION_ARGUMENT, e)) {
                appendResult(composeOptionArgument(e));
            } else if (elementEquals(ReStructuredText.DESCRIPTION, e)) {
                appendResult(composeDescription(e));
            } else if (elementEquals(ReStructuredText.LITERAL_BLOCK, e)) {
                appendResult(composeLiteralBlock(e));
            } else if (elementEquals(ReStructuredText.LINE_BLOCK, e)) {
                appendResult(composeLineBlock(e));
            } else if (elementEquals(ReStructuredText.LINE, e)) {
                appendResult(composeLine(e));
            } else if (elementEquals(ReStructuredText.BLOCK_QUOTE, e)) {
                appendResult(composeBlockQuote(e));
            } else if (elementEquals(ReStructuredText.ATTRIBUTION, e)) {
                appendResult(composeAttribution(e));
            } else if (elementEquals(ReStructuredText.DOCTEST_BLOCK, e)) {
                appendResult(composeDocTestBlock(e));
            } else if (elementEquals(ReStructuredText.ATTENTION, e)) {
                appendResult(composeAttention(e));
            } else if (elementEquals(ReStructuredText.CAUTION, e)) {
                appendResult(composeCaution(e));
            } else if (elementEquals(ReStructuredText.DANGER, e)) {
                appendResult(composeDanger(e));
            } else if (elementEquals(ReStructuredText.ERROR, e)) {
                appendResult(composeError(e));
            } else if (elementEquals(ReStructuredText.HINT, e)) {
                appendResult(composeHint(e));
            } else if (elementEquals(ReStructuredText.IMPORTANT, e)) {
                appendResult(composeImportant(e));
            } else if (elementEquals(ReStructuredText.NOTE, e)) {
                appendResult(composeNote(e));
            } else if (elementEquals(ReStructuredText.TIP, e)) {
                appendResult(composeTip(e));
            } else if (elementEquals(ReStructuredText.WARNING, e)) {
                appendResult(composeWarning(e));
            } else if (elementEquals(ReStructuredText.ADMONITION, e)) {
                appendResult(composeAdmonition(e));
            } else if (elementEquals(ReStructuredText.FOOTNOTE, e)) {
                appendResult(composeFootnote(e));
            } else if (elementEquals(ReStructuredText.CITATION, e)) {
                appendResult(composeCitation(e));
            } else if (elementEquals(ReStructuredText.LABEL, e)) {
                appendResult(composeLabel(e));
            } else if (elementEquals(ReStructuredText.RUBRIC, e)) {
                appendResult(composeRubric(e));
            } else if (elementEquals(ReStructuredText.TARGET, e)) {
                appendResult(composeTarget(e));
            } else if (elementEquals(ReStructuredText.SUBSTITUTION_DEFINITION, e)) {
                appendResult(composeSubstitutionDefinition(e));
            } else if (elementEquals(ReStructuredText.COMMENT, e)) {
                appendResult(composeComment(e));
            } else if (elementEquals(ReStructuredText.PENDING, e)) {
                appendResult(composePending(e));
            } else if (elementEquals(ReStructuredText.FIGURE, e)) {
                appendResult(composeFigure(e));
            } else if (elementEquals(ReStructuredText.IMAGE, e)) {
                appendResult(composeImage(e));
            } else if (elementEquals(ReStructuredText.CAPTION, e)) {
                appendResult(composeCaption(e));
            } else if (elementEquals(ReStructuredText.LEGEND, e)) {
                appendResult(composeLegend(e));
            } else if (elementEquals(ReStructuredText.SYSTEM_MESSAGE, e)) {
                appendResult(composeSystemMessage(e));
            } else if (elementEquals(ReStructuredText.RAW, e)) {
                appendResult(composeRaw(e));

                // table
            } else if (elementEquals(ReStructuredText.TABLE, e)) {
                appendResult(composeTable(e));
            } else if (elementEquals(ReStructuredText.TGROUP, e)) {
                appendResult(composeTgroup(e));
            } else if (elementEquals(ReStructuredText.COLSPEC, e)) {
                appendResult(composeColspec(e));
            } else if (elementEquals(ReStructuredText.THEAD, e)) {
                appendResult(composeThead(e));
            } else if (elementEquals(ReStructuredText.TBODY, e)) {
                appendResult(composeTbody(e));
            } else if (elementEquals(ReStructuredText.ROW, e)) {
                appendResult(composeRow(e));
            } else if (elementEquals(ReStructuredText.ENTRY, e)) {
                appendResult(composeEntry(e));
            } // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Inline Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            else if (elementEquals(ReStructuredText.EMPHASIS, e)) {
                appendResult(composeEmphasis(e));
            } else if (elementEquals(ReStructuredText.STRONG, e)) {
                appendResult(composeStrong(e));
            } else if (elementEquals(ReStructuredText.LITERAL, e)) {
                appendResult(composeLiteral(e));
            } else if (elementEquals(ReStructuredText.REFERENCE, e)) {
                appendResult(composeReference(e));
            } else if (elementEquals(ReStructuredText.FOOTNOTE_REFERENCE, e)) {
                appendResult(composeFootnoteReference(e));
            } else if (elementEquals(ReStructuredText.CITATION_REFERENCE, e)) {
                appendResult(composeCitationReference(e));
            } else if (elementEquals(ReStructuredText.SUBSTITUTION_REFERENCE, e)) {
                appendResult(composeSubstitutionReference(e));
            } else if (elementEquals(ReStructuredText.TITLE_REFERENCE, e)) {
                appendResult(composeTitleReference(e));
            } else if (elementEquals(ReStructuredText.ABBREVIATION, e)) {
                appendResult(composeAbbreviation(e));
            } else if (elementEquals(ReStructuredText.ACRONYM, e)) {
                appendResult(composeAcronyme(e));
            } else if (elementEquals(ReStructuredText.SUPERSCRIPT, e)) {
                appendResult(composeSuperscipt(e));
            } else if (elementEquals(ReStructuredText.SUBSCRIPT, e)) {
                appendResult(composeSubscript(e));
            } else if (elementEquals(ReStructuredText.INLINE, e)) {
                appendResult(composeInline(e));
            } else if (elementEquals(ReStructuredText.PROBLEMATIC, e)) {
                appendResult(composeProblematic(e));
            } else if (elementEquals(ReStructuredText.GENERATED, e)) {
                appendResult(composeGenerated(e));
            }

        }
    }

    public String parseDocument(Element el) {
        try {

            if (log.isDebugEnabled()) {
                log.debug("This element : " + el.getName());
            }

            // Creation dune nouvelle instance du visitor utilise            
            DocUtilsVisitor visitor = getClass().newInstance();

            // Traitement recursif (pour parser un document dans un autre)
            try {
                el.accept(visitor);
                String result = visitor.getResult();
                if (log.isDebugEnabled()) {
                    log.debug("document parsing result:\n" + result);
                }
                return result;
            } finally {
                // Recuperation des elements parcourus
                addCachedElements(visitor.getCachedElements());
                // nettoyage du visiteur temporaire
                visitor.clear();
            }

        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Cant compose document for reason " + ex.getMessage(), ex);
            }
            //FIXME : TC-20090816 should deal better the exception ?
            return EMPTY_STRING;
        }
    }

    public String getResult() {
        return rstResult.toString();
    }

    public void clear() {
        cachedElements.clear();
        rstResult = null;
    }
    
    protected void appendResult(String result) {
        rstResult.append(result);
    }

    protected boolean elementEquals(String name, Element e) {
        return e.getName().equals(name);
    }

    protected List<Element> getCachedElements() {
        return cachedElements;
    }

    // Permet de ne jamais repasser sur ce noeud
    protected void addCachedElement(Element e) {
        cachedElements.add(e);
    }

    protected void addCachedElements(List<Element> elements) {
        cachedElements.addAll(elements);
    }

    protected String indent(String toIndent, int l) {
        String prefix = EMPTY_STRING;
        for (int i = 0; i < l; i++) {
            prefix += SPACE;
        }
        StringBuilder buffer = new StringBuilder();
        for (String s : toIndent.split(LINE_SEPARATOR)) {
            buffer.append(prefix).append(s).append(LINE_SEPARATOR);
        }
        return buffer.toString();
    }
}
