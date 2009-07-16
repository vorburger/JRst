/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nuiton.jrst.convertisor;

import org.dom4j.Element;
import org.nuiton.jrst.ReStructuredText;
/**
 *
 * @author letellier
 */
public abstract class DocUtilsConvertisor extends DocUtilsVisitor{

    @Override
    public void visit(Element e) {

        if (!cachedElements.contains(e)){

            addCachedElement(e);
            
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Root Element
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            if (elementEquals(ReStructuredText.DOCUMENT,e)) { rstResult += composeDocument(e);

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Title Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            } else if (elementEquals(ReStructuredText.TITLE,e)) { rstResult += composeTitle(e);
            } else if (elementEquals(ReStructuredText.SUBTITLE,e)) { rstResult += composeSubTitle(e);

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Bibliographic Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            } else if (elementEquals(ReStructuredText.DOCINFO,e)) { rstResult += composeDocInfo(e);

            // } else if (elementEquals(ReStructuredText.INFO,e)) { rstResult += compose
            } else if (elementEquals(ReStructuredText.AUTHOR,e)) { rstResult += composeAutor(e);
            } else if (elementEquals(ReStructuredText.AUTHORS,e)) { rstResult += composeAutors(e);
            } else if (elementEquals(ReStructuredText.ORGANIZATION,e)) { rstResult += composeOrganisation(e);
            } else if (elementEquals(ReStructuredText.ADDRESS,e)) { rstResult += composeAddress(e);
            } else if (elementEquals(ReStructuredText.CONTACT,e)) { rstResult += composeContact(e);
            } else if (elementEquals(ReStructuredText.VERSION,e)) { rstResult += composeVersion(e);
            } else if (elementEquals(ReStructuredText.REVISION,e)) { rstResult += composeResvision(e);
            } else if (elementEquals(ReStructuredText.STATUS,e)) { rstResult += composeStatus(e);
            } else if (elementEquals(ReStructuredText.DATE,e)) { rstResult += composeDate(e);
            } else if (elementEquals(ReStructuredText.COPYRIGHT,e)) { rstResult += composeCopyright(e);

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Decoration Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            } else if (elementEquals(ReStructuredText.DECORATION,e)) { rstResult += composeDecoration(e);
            } else if (elementEquals(ReStructuredText.HEADER,e)) { rstResult += composeHeader(e);
            } else if (elementEquals(ReStructuredText.FOOTER,e)) { rstResult += composeFooter(e);

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Structural Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            } else if (elementEquals(ReStructuredText.SECTION,e)) { rstResult += composeSection(e);
            } else if (elementEquals(ReStructuredText.TOPIC,e)) { rstResult += composeTopic(e);
            } else if (elementEquals(ReStructuredText.SIDEBAR,e)) { rstResult += composeSideBar(e);
            } else if (elementEquals(ReStructuredText.TRANSITION,e)) { rstResult += composeTransition(e);

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Body Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            } else if (elementEquals(ReStructuredText.PARAGRAPH,e)) { rstResult += composeParagraph(e);
            } else if (elementEquals(ReStructuredText.COMPOUND,e)) { rstResult += composeCompound(e);
            } else if (elementEquals(ReStructuredText.CONTAINER,e)) { rstResult += composeContainer(e);
            } else if (elementEquals(ReStructuredText.BULLET_LIST,e)) { rstResult += composeBulletList(e);
            } else if (elementEquals(ReStructuredText.ENUMERATED_LIST,e)) { rstResult += composeEnumeratedList(e);
            } else if (elementEquals(ReStructuredText.LIST_ITEM,e)) { rstResult += composeListItem(e);
            } else if (elementEquals(ReStructuredText.DEFINITION_LIST,e)) { rstResult += composeDefinitionList(e);
            } else if (elementEquals(ReStructuredText.DEFINITION_LIST_ITEM,e)) { rstResult += composeDefinitionListItem(e);
            } else if (elementEquals(ReStructuredText.TERM,e)) { rstResult += composeTerm(e);
            } else if (elementEquals(ReStructuredText.CLASSIFIER,e)) { rstResult += composeClassifier(e);
            } else if (elementEquals(ReStructuredText.DEFINITION,e)) { rstResult += composeDefinition(e);
            } else if (elementEquals(ReStructuredText.FIELD_LIST,e)) { rstResult += composeFieldList(e);
            } else if (elementEquals(ReStructuredText.FIELD,e)) { rstResult += composeField(e);
            } else if (elementEquals(ReStructuredText.FIELD_NAME,e)) { rstResult += composeFieldName(e);
            } else if (elementEquals(ReStructuredText.FIELD_BODY,e)) { rstResult += composeFieldBody(e);
            } else if (elementEquals(ReStructuredText.OPTION_LIST,e)) { rstResult += composeOptionList(e);
            } else if (elementEquals(ReStructuredText.OPTION_LIST_ITEM,e)) { rstResult += composeOptionListItem(e);
            } else if (elementEquals(ReStructuredText.OPTION_GROUP,e)) { rstResult += composeOptionGroup(e);
            } else if (elementEquals(ReStructuredText.OPTION,e)) { rstResult += composeOption(e);
            } else if (elementEquals(ReStructuredText.OPTION_STRING,e)) { rstResult += composeOptionString(e);
            } else if (elementEquals(ReStructuredText.OPTION_ARGUMENT,e)) { rstResult += composeOptionArgument(e);
            } else if (elementEquals(ReStructuredText.DESCRIPTION,e)) { rstResult += composeDescription(e);
            } else if (elementEquals(ReStructuredText.LITERAL_BLOCK,e)) { rstResult += composeLiteralBlock(e);
            } else if (elementEquals(ReStructuredText.LINE_BLOCK,e)) { rstResult += composeLineBlock(e);
            } else if (elementEquals(ReStructuredText.LINE,e)) { rstResult += composeLine(e);
            } else if (elementEquals(ReStructuredText.BLOCK_QUOTE,e)) { rstResult += composeBlockQuote(e);
            } else if (elementEquals(ReStructuredText.ATTRIBUTION,e)) { rstResult += composeAttribution(e);
            } else if (elementEquals(ReStructuredText.DOCTEST_BLOCK,e)) { rstResult += composeDocTestBlock(e);
            } else if (elementEquals(ReStructuredText.ATTENTION,e)) { rstResult += composeAttention(e);
            } else if (elementEquals(ReStructuredText.CAUTION,e)) { rstResult += composeCaution(e);
            } else if (elementEquals(ReStructuredText.DANGER,e)) { rstResult += composeDanger(e);
            } else if (elementEquals(ReStructuredText.ERROR,e)) { rstResult += composeError(e);
            } else if (elementEquals(ReStructuredText.HINT,e)) { rstResult += composeHint(e);
            } else if (elementEquals(ReStructuredText.IMPORTANT,e)) { rstResult += composeImportant(e);
            } else if (elementEquals(ReStructuredText.NOTE,e)) { rstResult += composeNote(e);
            } else if (elementEquals(ReStructuredText.TIP,e)) { rstResult += composeTip(e);
            } else if (elementEquals(ReStructuredText.WARNING,e)) { rstResult += composeWarning(e);
            } else if (elementEquals(ReStructuredText.ADMONITION,e)) { rstResult += composeAdmonition(e);
            } else if (elementEquals(ReStructuredText.FOOTNOTE,e)) { rstResult += composeFootnote(e);
            } else if (elementEquals(ReStructuredText.CITATION,e)) { rstResult += composeCitation(e);
            } else if (elementEquals(ReStructuredText.LABEL,e)) { rstResult += composeLabel(e);
            } else if (elementEquals(ReStructuredText.RUBRIC,e)) { rstResult += composeRubric(e);
            } else if (elementEquals(ReStructuredText.TARGET,e)) { rstResult += composeTarget(e);
            } else if (elementEquals(ReStructuredText.SUBSTITUTION_DEFINITION,e)) { rstResult += composeSubstitutionDefinition(e);
            } else if (elementEquals(ReStructuredText.COMMENT,e)) { rstResult += composeComment(e);
            } else if (elementEquals(ReStructuredText.PENDING,e)) { rstResult += composePending(e);
            } else if (elementEquals(ReStructuredText.FIGURE,e)) { rstResult += composeFigure(e);
            } else if (elementEquals(ReStructuredText.IMAGE,e)) { rstResult += composeImage(e);
            } else if (elementEquals(ReStructuredText.CAPTION,e)) { rstResult += composeCaption(e);
            } else if (elementEquals(ReStructuredText.LEGEND,e)) { rstResult += composeLegend(e);
            } else if (elementEquals(ReStructuredText.SYSTEM_MESSAGE,e)) { rstResult += composeSystemMessage(e);
            } else if (elementEquals(ReStructuredText.RAW,e)) { rstResult += composeRaw(e);

            // table
            } else if (elementEquals(ReStructuredText.TABLE,e)) { rstResult += composeTable(e);
            } else if (elementEquals(ReStructuredText.TGROUP,e)) { rstResult += composeTgroup(e);
            } else if (elementEquals(ReStructuredText.COLSPEC,e)) { rstResult += composeColspec(e);
            } else if (elementEquals(ReStructuredText.THEAD,e)) { rstResult += composeThead(e);
            } else if (elementEquals(ReStructuredText.TBODY,e)) { rstResult += composeTbody(e);
            } else if (elementEquals(ReStructuredText.ROW,e)) { rstResult += composeRow(e);
            } else if (elementEquals(ReStructuredText.ENTRY,e)) { rstResult += composeEntry(e);

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Inline Elements
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            } else if (elementEquals(ReStructuredText.EMPHASIS,e)) { rstResult += composeEmphasis(e);
            } else if (elementEquals(ReStructuredText.STRONG,e)) { rstResult += composeStrong(e);
            } else if (elementEquals(ReStructuredText.LITERAL,e)) { rstResult += composeLiteral(e);
            } else if (elementEquals(ReStructuredText.REFERENCE,e)) { rstResult += composeReference(e);
            } else if (elementEquals(ReStructuredText.FOOTNOTE_REFERENCE,e)) { rstResult += composeFootnoteReference(e);
            } else if (elementEquals(ReStructuredText.CITATION_REFERENCE,e)) { rstResult += composeCitationReference(e);
            } else if (elementEquals(ReStructuredText.SUBSTITUTION_REFERENCE,e)) { rstResult += composeSubstitutionReference(e);
            } else if (elementEquals(ReStructuredText.TITLE_REFERENCE,e)) { rstResult += composeTitleReference(e);
            } else if (elementEquals(ReStructuredText.ABBREVIATION,e)) { rstResult += composeAbbreviation(e);
            } else if (elementEquals(ReStructuredText.ACRONYM,e)) { rstResult += composeAcronyme(e);
            } else if (elementEquals(ReStructuredText.SUPERSCRIPT,e)) { rstResult += composeSuperscipt(e);
            } else if (elementEquals(ReStructuredText.SUBSCRIPT,e)) { rstResult += composeSubscript(e);
            } else if (elementEquals(ReStructuredText.INLINE,e)) { rstResult += composeInline(e);
            } else if (elementEquals(ReStructuredText.PROBLEMATIC,e)) { rstResult += composeProblematic(e);
            } else if (elementEquals(ReStructuredText.GENERATED,e)) { rstResult += composeGenerated(e);
            }

        }
    }

    protected boolean elementEquals(String name, Element e) {
        return e.getName().equals(name);
    }
    
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
}
