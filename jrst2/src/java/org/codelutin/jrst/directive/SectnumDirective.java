package org.codelutin.jrst.directive;

import org.codelutin.jrst.JRSTDirective;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class SectnumDirective implements JRSTDirective {

    /* (non-Javadoc)
     * @see org.codelutin.jrst.JRSTDirective#parse(org.dom4j.Element)
     */
    public Node parse(Element e) {
        Element result = DocumentHelper.createElement("sectnum");
        return result;
    }
}
