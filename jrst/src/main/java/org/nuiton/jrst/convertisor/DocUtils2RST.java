package org.nuiton.jrst.convertisor;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import static org.nuiton.jrst.ReStructuredText.TITLE_CHAR;

import static org.nuiton.jrst.ReStructuredText.PARAGRAPH;
import static org.nuiton.jrst.ReStructuredText.TITLE;

/**
 *
 * @author letellier
 */
public class DocUtils2RST extends DocUtilsVisitor {

    /** to use log facility, just put in your code: log.info(\"...\"); */
    private static Log log = LogFactory.getLog(DocUtils2RST.class);
    protected int level = 0;

    @Override
    public String composeDocument(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTitle(Element e) {
        String result = e.getText();
        String underLine = EMPTY_STRING;
        for (int i = 0; i < result.length(); i++) {
            underLine += TITLE_CHAR.charAt(level);
        }
        result += LINE_SEPARATOR + underLine + LINE_SEPARATOR;
        log.debug("composeTitle :\n" + result);
        return result;
    }

    @Override
    public String composeSubTitle(Element e) {
        String result = e.getText();
        String underLine = EMPTY_STRING;
        for (int i = 0; i < result.length(); i++) {
            underLine += TITLE_CHAR.charAt(level);
        }
        result += LINE_SEPARATOR + underLine + LINE_SEPARATOR;
        log.debug("composeSubTitle :\n" + result);
        return result;
    }

    @Override
    public String composeDocInfo(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAutor(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAutors(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeOrganisation(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAddress(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeContact(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeVersion(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeResvision(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeStatus(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDate(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeCopyright(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDecoration(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeHeader(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeFooter(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeSection(Element e) {
        level++;
        return EMPTY_STRING;
    }

    @Override
    public String composeTopic(Element e) {
        StringBuilder buffer = new StringBuilder(".. topic:: ");
        level++;
        List<?> elements = e.elements();
        for (Object o : elements) {
            Element element = (Element) o;
            try {
                if (elementEquals(TITLE, element)) {
                    log.debug("topic title : " + element.getText());
                    buffer.append(indent(element.getText()));
                } else if (elementEquals(PARAGRAPH, element)) {
                    log.trace("topic para (from element) : " + element.getText());
                    String para = parseDocument(element);
                    log.debug("topic para (from parsing) : " + para);
                    buffer.append(indent(para));
                }
            } finally {
                // always mark the node as fired
                addCachedElement(element);
            }
        }
        --level;
        String result = buffer.toString();
        log.debug("composeTopic :\n" + result);
        return result;
    }

    @Override
    public String composeSideBar(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTransition(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeParagraph(Element e) {
        String result = indent(e.getText(), level);
        log.debug("composeParagraph :\n" + result);
        return result;
    }

    @Override
    public String composeCompound(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeContainer(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeBulletList(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeEnumeratedList(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeListItem(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDefinitionList(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDefinitionListItem(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTerm(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeClassifier(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDefinition(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeFieldList(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeField(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeFieldName(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeFieldBody(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeOptionList(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeOptionListItem(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeOptionGroup(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeOption(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeOptionString(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeOptionArgument(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDescription(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeLiteralBlock(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeLineBlock(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeLine(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeBlockQuote(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAttribution(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDocTestBlock(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAttention(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeCaution(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeDanger(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeError(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeHint(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeImportant(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeNote(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTip(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeWarning(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAdmonition(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeFootnote(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeCitation(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeLabel(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeRubric(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTarget(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeSubstitutionDefinition(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeComment(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composePending(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeFigure(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeImage(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeCaption(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeLegend(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeSystemMessage(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeRaw(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTable(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTgroup(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeColspec(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeThead(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTbody(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeRow(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeEntry(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeEmphasis(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeStrong(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeLiteral(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeReference(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeFootnoteReference(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeCitationReference(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeSubstitutionReference(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeTitleReference(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAbbreviation(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeAcronyme(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeSuperscipt(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeSubscript(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeInline(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeProblematic(Element e) {
        return EMPTY_STRING;
    }

    @Override
    public String composeGenerated(Element e) {
        return EMPTY_STRING;
    }

    protected String indent(String toIndent) {
        return indent(toIndent, level);
    }
}

