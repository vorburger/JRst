/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class DocUtils2RST extends DocUtilsConvertisor {

    /** to use log facility, just put in your code: log.info(\"...\"); */
    private static Log log = LogFactory.getLog(DocUtilsVisitor.class);

    protected int level = 0;

    public DocUtils2RST() {
    }

    @Override
    public String composeDocument(Element e) {
        return "";
    }

    @Override
    public String composeTitle(Element e) {
        String result = e.getText();
        String underLine = "";
        for (int i = 0;i<result.length();i++){
            underLine += TITLE_CHAR.charAt(level);
        }
        result += '\n' + underLine + '\n';
        log.debug("composeTitle : " + result);
        return result;
    }

    @Override
    public String composeSubTitle(Element e) {
        String result = e.getText();
        String underLine = "";
        for (int i = 0;i<result.length();i++){
            underLine += TITLE_CHAR.charAt(level);
        }
        result += '\n' + underLine + '\n';
        log.debug("composeSubTitle : " + result);
        return result;
    }

    @Override
    public String composeDocInfo(Element e) {
        return "";
    }

    @Override
    public String composeAutor(Element e) {
        return "";
    }

    @Override
    public String composeAutors(Element e) {
        return "";
    }

    @Override
    public String composeOrganisation(Element e) {
        return "";
    }

    @Override
    public String composeAddress(Element e) {
        return "";
    }

    @Override
    public String composeContact(Element e) {
        return "";
    }

    @Override
    public String composeVersion(Element e) {
        return "";
    }

    @Override
    public String composeResvision(Element e) {
        return "";
    }

    @Override
    public String composeStatus(Element e) {
        return "";
    }

    @Override
    public String composeDate(Element e) {
        return "";
    }

    @Override
    public String composeCopyright(Element e) {
        return "";
    }

    @Override
    public String composeDecoration(Element e) {
        return "";
    }

    @Override
    public String composeHeader(Element e) {
        return "";
    }

    @Override
    public String composeFooter(Element e) {
        return "";
    }

    @Override
    public String composeSection(Element e) {
        level++;
        return "";
    }

    @Override
    public String composeTopic(Element e) {
        String result = ".. topic:: ";
        level++;
        List<Element> elements = e.elements();
        for (Element element : elements){
            if (element.getName().equals(TITLE)){
                addCachedElement(element);
                result += indent(element.getText());
            } else if (element.getName().equals(PARAGRAPH)){
                Element ePara = e.element(PARAGRAPH);
                result += ePara.getText();
                addCachedElement(element);
                result += indent(parseDocument(element));
            }
        }
        --level;
        log.debug("composeTopic : " + result);
        return result;
    }

    @Override
    public String composeSideBar(Element e) {
        return "";
    }

    @Override
    public String composeTransition(Element e) {
        return "";
    }

    @Override
    public String composeParagraph(Element e) {
        String result = "";
        result += indent(e.getText(), level);
        log.debug("composeParagraph : " + result);
        return result;
    }

    @Override
    public String composeCompound(Element e) {
        return "";
    }

    @Override
    public String composeContainer(Element e) {
        return "";
    }

    @Override
    public String composeBulletList(Element e) {
        return "";
    }

    @Override
    public String composeEnumeratedList(Element e) {
        return "";
    }

    @Override
    public String composeListItem(Element e) {
        return "";
    }

    @Override
    public String composeDefinitionList(Element e) {
        return "";
    }

    @Override
    public String composeDefinitionListItem(Element e) {
        return "";
    }

    @Override
    public String composeTerm(Element e) {
        return "";
    }

    @Override
    public String composeClassifier(Element e) {
        return "";
    }

    @Override
    public String composeDefinition(Element e) {
        return "";
    }

    @Override
    public String composeFieldList(Element e) {
        return "";
    }

    @Override
    public String composeField(Element e) {
        return "";
    }

    @Override
    public String composeFieldName(Element e) {
        return "";
    }

    @Override
    public String composeFieldBody(Element e) {
        return "";
    }

    @Override
    public String composeOptionList(Element e) {
        return "";
    }

    @Override
    public String composeOptionListItem(Element e) {
        return "";
    }

    @Override
    public String composeOptionGroup(Element e) {
        return "";
    }

    @Override
    public String composeOption(Element e) {
        return "";
    }

    @Override
    public String composeOptionString(Element e) {
        return "";
    }

    @Override
    public String composeOptionArgument(Element e) {
        return "";
    }

    @Override
    public String composeDescription(Element e) {
        return "";
    }

    @Override
    public String composeLiteralBlock(Element e) {
        return "";
    }

    @Override
    public String composeLineBlock(Element e) {
        return "";
    }

    @Override
    public String composeLine(Element e) {
        return "";
    }

    @Override
    public String composeBlockQuote(Element e) {
        return "";
    }

    @Override
    public String composeAttribution(Element e) {
        return "";
    }

    @Override
    public String composeDocTestBlock(Element e) {
        return "";
    }

    @Override
    public String composeAttention(Element e) {
        return "";
    }

    @Override
    public String composeCaution(Element e) {
        return "";
    }

    @Override
    public String composeDanger(Element e) {
        return "";
    }

    @Override
    public String composeError(Element e) {
        return "";
    }

    @Override
    public String composeHint(Element e) {
        return "";
    }

    @Override
    public String composeImportant(Element e) {
        return "";
    }

    @Override
    public String composeNote(Element e) {
        return "";
    }

    @Override
    public String composeTip(Element e) {
        return "";
    }

    @Override
    public String composeWarning(Element e) {
        return "";
    }

    @Override
    public String composeAdmonition(Element e) {
        return "";
    }

    @Override
    public String composeFootnote(Element e) {
        return "";
    }

    @Override
    public String composeCitation(Element e) {
        return "";
    }

    @Override
    public String composeLabel(Element e) {
        return "";
    }

    @Override
    public String composeRubric(Element e) {
        return "";
    }

    @Override
    public String composeTarget(Element e) {
        return "";
    }

    @Override
    public String composeSubstitutionDefinition(Element e) {
        return "";
    }

    @Override
    public String composeComment(Element e) {
        return "";
    }

    @Override
    public String composePending(Element e) {
        return "";
    }

    @Override
    public String composeFigure(Element e) {
        return "";
    }

    @Override
    public String composeImage(Element e) {
        return "";
    }

    @Override
    public String composeCaption(Element e) {
        return "";
    }

    @Override
    public String composeLegend(Element e) {
        return "";
    }

    @Override
    public String composeSystemMessage(Element e) {
        return "";
    }

    @Override
    public String composeRaw(Element e) {
        return "";
    }

    @Override
    public String composeTable(Element e) {
        return "";
    }

    @Override
    public String composeTgroup(Element e) {
        return "";
    }

    @Override
    public String composeColspec(Element e) {
        return "";
    }

    @Override
    public String composeThead(Element e) {
        return "";
    }

    @Override
    public String composeTbody(Element e) {
        return "";
    }

    @Override
    public String composeRow(Element e) {
        return "";
    }

    @Override
    public String composeEntry(Element e) {
        return "";
    }

    @Override
    public String composeEmphasis(Element e) {
        return "";
    }

    @Override
    public String composeStrong(Element e) {
        return "";
    }

    @Override
    public String composeLiteral(Element e) {
        return "";
    }

    @Override
    public String composeReference(Element e) {
        return "";
    }

    @Override
    public String composeFootnoteReference(Element e) {
        return "";
    }

    @Override
    public String composeCitationReference(Element e) {
        return "";
    }

    @Override
    public String composeSubstitutionReference(Element e) {
        return "";
    }

    @Override
    public String composeTitleReference(Element e) {
        return "";
    }

    @Override
    public String composeAbbreviation(Element e) {
        return "";
    }

    @Override
    public String composeAcronyme(Element e) {
        return "";
    }

    @Override
    public String composeSuperscipt(Element e) {
        return "";
    }

    @Override
    public String composeSubscript(Element e) {
        return "";
    }

    @Override
    public String composeInline(Element e) {
        return "";
    }

    @Override
    public String composeProblematic(Element e) {
        return "";
    }

    @Override
    public String composeGenerated(Element e) {
        return "";
    }

    protected String indent(String toIndent){
        return indent(toIndent, level);
    }

    protected String indent(String toIndent, int l){
        String result = "";
        for (String s : toIndent.split("\n")){
            for(int i=0;i<l;i++){
                result+=" ";
            }
            result+=s;
            result+="\n";
        }
        return result;
    }
}

