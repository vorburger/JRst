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
 * JRSTGenerator.java
 *
 * Created: 30 oct. 06 00:14:18
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst;

import static org.codelutin.jrst.ReStructuredText.ADDRESS;
import static org.codelutin.jrst.ReStructuredText.AUTHOR;
import static org.codelutin.jrst.ReStructuredText.AUTHORS;
import static org.codelutin.jrst.ReStructuredText.BULLET_LIST;
import static org.codelutin.jrst.ReStructuredText.CLASSIFIER;
import static org.codelutin.jrst.ReStructuredText.CONTACT;
import static org.codelutin.jrst.ReStructuredText.COPYRIGHT;
import static org.codelutin.jrst.ReStructuredText.DATE;
import static org.codelutin.jrst.ReStructuredText.DEFINITION;
import static org.codelutin.jrst.ReStructuredText.DEFINITION_LIST;
import static org.codelutin.jrst.ReStructuredText.DOCINFO;
import static org.codelutin.jrst.ReStructuredText.EMPHASIS;
import static org.codelutin.jrst.ReStructuredText.ENUMERATED_LIST;
import static org.codelutin.jrst.ReStructuredText.FIELD_BODY;
import static org.codelutin.jrst.ReStructuredText.FIELD_LIST;
import static org.codelutin.jrst.ReStructuredText.FIELD_NAME;
import static org.codelutin.jrst.ReStructuredText.LIST_ITEM;
import static org.codelutin.jrst.ReStructuredText.LITERAL;
import static org.codelutin.jrst.ReStructuredText.LITERAL_BLOCK;
import static org.codelutin.jrst.ReStructuredText.ORGANIZATION;
import static org.codelutin.jrst.ReStructuredText.PARAGRAPH;
import static org.codelutin.jrst.ReStructuredText.REVISION;
import static org.codelutin.jrst.ReStructuredText.SECTION;
import static org.codelutin.jrst.ReStructuredText.STATUS;
import static org.codelutin.jrst.ReStructuredText.STRONG;
import static org.codelutin.jrst.ReStructuredText.TABLE;
import static org.codelutin.jrst.ReStructuredText.TITLE;
import static org.codelutin.jrst.ReStructuredText.TITLE_CHAR;
import static org.codelutin.jrst.ReStructuredText.TRANSITION;
import static org.codelutin.jrst.ReStructuredText.VERSION;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Cette classe contient plusieurs methode pour generer, soit en utilisant une
 * feuille de style {@link #generate(Document, URL, Writer)}, soit un
 * {@link ContentHandler} avec {@link #generate(Document, ContentHandler)}, soit
 * {@link DocumentHandler} avec {@link #generate(Document, ContentHandler)} ou
 * bien regenerer du RST avec {@link #generate(Document)} dans les deux dernier
 * cas il faut passer un {@link Writer} en paremetre du constructeur.
 * <p>
 * On peut aussi transformer le {@link Document} en un autre {@link Document}
 * avec {@link #transform(Document, URL)}
 * 
 * @author poussin
 */
public class JRSTGenerator implements DocumentHandler {

    static boolean DEBUG = true;

    static private Log log = LogFactory.getLog(JRSTGenerator.class);

    protected Writer out = null;
    protected int sectionLevel = 0;
    protected int indent = 0;

    protected String listType = "bullet|enumerated|...";
    protected int enumStart = 1;

    protected URIResolver uriResolver = null;

    public JRSTGenerator() {
    }

    public JRSTGenerator(Writer out) {
        this.out = out;
    }

    /**
     * @return the uriResolver
     */
    public URIResolver getUriResolver() {
        return this.uriResolver;
    }

    /**
     * @param uriResolver the uriResolver to set
     */
    public void setUriResolver(URIResolver uriResolver) {
        this.uriResolver = uriResolver;
    }

    /**
     * Generate using this class as handler, this generate RST text to out
     * passed in constructor
     * 
     * @param doc
     * @throws IOException
     * @throws TransformerException
     */
    public void generate(Document doc) throws IOException {
        generate(doc, this);
    }

    public void generate(Document doc, DocumentHandler handler)
            throws IOException {
        DocumentWalker walker = new DocumentWalker(handler);
        walker.walk(doc);
    }

    /**
     * Generate using handler passed in argument
     * 
     * @param doc
     * @param handler
     * @throws IOException
     * @throws TransformerException
     */
    public void generate(Document doc, ContentHandler handler)
            throws IOException, TransformerException {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        if (uriResolver != null) {
            factory.setURIResolver(uriResolver);
        }
        Transformer transformer = factory.newTransformer();

        // now lets style the given document
        DocumentSource source = new DocumentSource(doc);
        SAXResult result = new SAXResult(handler);
        transformer.transform(source, result);
    }

    /**
     * Used writer passed in construction class
     * 
     * @param doc
     * @param stylesheet
     * @throws IOException
     * @throws TransformerException
     */
    public void generate(Document doc, URL stylesheet) throws IOException,
            TransformerException {
        generate(doc, stylesheet, out);
    }

    /**
     * Generate out from document using stylesheet
     * 
     * @param doc
     * @param stylesheet
     * @param out
     * @throws IOException
     * @throws TransformerException
     */
    public void generate(Document doc, URL stylesheet, Writer out)
            throws IOException, TransformerException {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        if (uriResolver != null) {
            factory.setURIResolver(uriResolver);
        } else {
            factory.setURIResolver(new DocumentResolver(stylesheet));
        }
        Transformer transformer = factory.newTransformer(new StreamSource(
                stylesheet.openStream()));

        // now lets style the given document
        DocumentSource source = new DocumentSource(doc);
        StreamResult result = new StreamResult(out);
        transformer.transform(source, result);

        out.flush();
    }

    /**
     * Transform doc in another XML document
     * 
     * @param doc
     * @param stylesheet
     * @return
     * @throws TransformerException
     * @throws IOException
     */
    public Document transform(Document doc, URL stylesheet, String... args)
            throws TransformerException, IOException {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        if (uriResolver != null) {
            factory.setURIResolver(uriResolver);
        } else {
            factory.setURIResolver(new DocumentResolver(stylesheet));
        }
        Transformer transformer = factory.newTransformer(new StreamSource(
                stylesheet.openStream()));

        // DEBUG To see where is the probleme with the dtd locator :(
        // transformer.setErrorListener(new ErrorListener() {
        // public void error(TransformerException exception) throws
        // TransformerException {
        // exception.printStackTrace();
        // }
        // public void fatalError(TransformerException exception) throws
        // TransformerException {
        // exception.printStackTrace();
        // }
        // public void warning(TransformerException exception) throws
        // TransformerException {
        // exception.printStackTrace();
        // }
        //            
        // });

        // TODO
        // for (int i=0; i<args.length; i+=2) {
        // transformer.
        // }

        // now lets style the given document
        DocumentSource source = new DocumentSource(doc);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }

    static public class DocumentResolver implements URIResolver, EntityResolver {

        URL baseURL = null;

        private DocumentResolver() {
        }

        public DocumentResolver(URL url) throws MalformedURLException {
            String path = new File(url.getPath()).getParent();
            baseURL = new URL(url.getProtocol(), url.getHost(), url.getPort(),
                    path);
        }

        public Source resolve(String href, String base)
                throws TransformerException {
            try {
                URL url = null;
                if (href == null) {
                    url = baseURL;
                } else {
                    String path = baseURL.getPath();
                    if (path.startsWith("file:")) {
                        path = "file:"
                                + new File(path.substring("file:".length()),
                                        href).getCanonicalPath();
                    } else {
                        path = new File(path, href).getCanonicalPath();
                    }
                    url = new URL(baseURL.getProtocol(), baseURL.getHost(),
                            baseURL.getPort(), path);
                }
                log.debug("** resolve href: '" + href + "' base: '" + base
                        + "' return: '" + url + "'");
                Source result = new StreamSource(url.openStream());
                return result;
            } catch (MalformedURLException eee) {
                throw new TransformerException("Can't create url ", eee);
            } catch (IOException eee) {
                throw new TransformerException("Can't read url", eee);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
         * java.lang.String)
         */
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            System.err.println("## resolveEntity publicId '" + publicId
                    + "' systemId: '" + systemId + "'");
            return null;
        }

    }

    protected String string(String s, int number) {
        String result = "";
        for (int i = 0; i < number; i++) {
            result += s;
        }
        return result;
    }

    protected String enumtype(int i, String type) {
        String result = null;
        if ("arabic".equals(type)) {
            result = String.valueOf(i);
        } else if ("loweralpha".equals(type)) {
            result = String.valueOf((char) ((int) 'a' + i));
        } else if ("upperalpha".equals(type)) {
            result = String.valueOf((char) ((int) 'A' + i));
        } else if ("lowerroman".equals(type) || "upperroman".equals(type)) {
            String[] c = new String[] { "i", "v", "x", "l", "c", "d", "m" };
            int[] d = new int[] { 1, 5, 10, 50, 100, 500, 1000 };
            result = "";
            for (int a = 0; a < c.length; a++) {
                result = string(c[a], i / d[a]) + result;
                i = i % d[a];
            }
            if ("upperroman".equals(type)) {
                result = result.toUpperCase();
            }
        }
        return result;
    }

    /**
     * Determine la longueur du text dans l'element
     * &lt;emphasis<&gt;toto&lt;/emphasis<&gt; qui donne *toto* retournera 6
     * 
     * @param e
     * @return
     */
    @SuppressWarnings("unchecked")
    protected int inlineLength(Element e) {
        int result = 0;
        LinkedList<Node> elems = new LinkedList<Node>();
        elems.addAll(e.content());
        while (elems.peek() != null) {
            Node elem = elems.poll();
            switch (elem.getNodeType()) {
            case Node.ELEMENT_NODE:
                elems.addAll(((Element) elem).content());
                if (EMPHASIS.equals(elem.getName())) {
                    result += 2;
                } else if (STRONG.equals(elem.getName())) {
                    result += 4;
                } else if (LITERAL.equals(elem.getName())) {
                    result += 4;
                } // perhaps do footnote_refence, ...
                break;
            case Node.TEXT_NODE:
                result += elem.getText().length();
                break;
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codelutin.jrst.DocumentHandler#startDocument(org.dom4j.Document)
     */
    public void startDocument(Document doc) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codelutin.jrst.DocumentHandler#endDocument(org.dom4j.Document)
     */
    public void endDocument(Document doc) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codelutin.jrst.DocumentHandler#endElement(org.dom4j.Element)
     */
    public void endElement(Element e) {
        boolean needNewLine = false;

        if (SECTION.equals(e.getName())) {
            sectionLevel--;
            needNewLine = true;
        } else if (PARAGRAPH.equals(e.getName())) {
            newLine();
            needNewLine = true;
        } else if (TITLE.equals(e.getName())) {
            newLine();
            if (sectionLevel == 0) {
                write(string("=", inlineLength(e)));
            } else {
                String c = TITLE_CHAR.substring(sectionLevel, sectionLevel + 1);
                write(string(c, inlineLength(e)));
            }
            newLine();
            needNewLine = true;
        } else if (EMPHASIS.equals(e.getName())) {
            write("*");
        } else if (STRONG.equals(e.getName())) {
            write("**");
        } else if (LITERAL.equals(e.getName())) {
            write("``");
        } else if (DOCINFO.equals(e.getName())) {
            needNewLine = true;
        } else if (LITERAL_BLOCK.equals(e.getName())) {
            indent--;
            newLine();
            needNewLine = true;
        } else if (TABLE.equals(e.getName())) {
            // TODO now we take table as LITERAL_BLOCK, but in near
            // futur we must parse correctly TABLE (show JRSTReader and
            // JRSTLexer too)
            newLine();
            needNewLine = true;
        } else if (BULLET_LIST.equals(e.getName())) {
            needNewLine = true;
        } else if (ENUMERATED_LIST.equals(e.getName())) {
            needNewLine = true;
        } else if (FIELD_LIST.equals(e.getName())) {
            needNewLine = true;
        } else if (FIELD_BODY.equals(e.getName())) {
            indent--;
        } else if (DEFINITION_LIST.equals(e.getName())) {
            needNewLine = true;
        } else if (DEFINITION.equals(e.getName())) {
            indent--;
        } else if (LIST_ITEM.equals(e.getName())) {
            indent--;
        } else if (AUTHOR.equals(e.getName())) {
            newLine();
            indent--;
        } else if (AUTHORS.equals(e.getName())) {
            newLine();
            indent--;
        } else if (ORGANIZATION.equals(e.getName())) {
            newLine();
            indent--;
        } else if (ADDRESS.equals(e.getName())) {
            newLine();
            indent--;
        } else if (CONTACT.equals(e.getName())) {
            newLine();
            indent--;
        } else if (VERSION.equals(e.getName())) {
            newLine();
            indent--;
        } else if (REVISION.equals(e.getName())) {
            newLine();
            indent--;
        } else if (STATUS.equals(e.getName())) {
            newLine();
            indent--;
        } else if (DATE.equals(e.getName())) {
            newLine();
            indent--;
        } else if (COPYRIGHT.equals(e.getName())) {
            newLine();
            indent--;
        }

        if (needNewLine) {
            // on ajoute une nouvelle ligne que si on est pas le dernier
            // fils, cela evite que le fils, le pere, et le grand-pere ne
            // demande tous une nouvelle ligne et donc au lieu d'en avoir
            // une comme on le souhaite on en est 3 voir plus
            Element parent = e.getParent();
            Node lastNode = parent.node(parent.nodeCount() - 1);
            if (lastNode != e) {
                // write("** new line ** " + e.getName() + ":" +
                // lastNode.getName());
                newLine();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codelutin.jrst.DocumentHandler#startElement(org.dom4j.Element)
     */
    public void startElement(Element e) {
        if (SECTION.equals(e.getName())) {
            sectionLevel++;
        } else if (TITLE.equals(e.getName())) {
            if (sectionLevel == 0) {
                write(string("=", inlineLength(e)));
                newLine();
            }
        } else if (PARAGRAPH.equals(e.getName())) {
            Element parent = e.getParent();
            if (!((LIST_ITEM.equals(parent.getName()) || FIELD_BODY
                    .equals(parent.getName())) && parent.node(0) == e)) {
                writeIndent();
            }
        } else if (TRANSITION.equals(e.getName())) {
            write(string("-", 80));
            newLine();
            newLine();
        } else if (EMPHASIS.equals(e.getName())) {
            write("*");
        } else if (STRONG.equals(e.getName())) {
            write("**");
        } else if (LITERAL.equals(e.getName())) {
            write("``");
        } else if (AUTHOR.equals(e.getName())) {
            write(":Author: ");
            indent++;
        } else if (AUTHORS.equals(e.getName())) {
            write(":Authors: ");
            indent++;
        } else if (ORGANIZATION.equals(e.getName())) {
            write(":Organization: ");
            indent++;
        } else if (ADDRESS.equals(e.getName())) {
            write(":Address: ");
            indent++;
        } else if (CONTACT.equals(e.getName())) {
            write(":Contact: ");
            indent++;
        } else if (VERSION.equals(e.getName())) {
            write(":Version: ");
            indent++;
        } else if (REVISION.equals(e.getName())) {
            write(":Revision: ");
            indent++;
        } else if (STATUS.equals(e.getName())) {
            write(":Status: ");
            indent++;
        } else if (DATE.equals(e.getName())) {
            write(":Date: ");
            indent++;
        } else if (COPYRIGHT.equals(e.getName())) {
            write(":Copyright: ");
            indent++;
        } else if (FIELD_NAME.equals(e.getName())) {
            writeIndent(":");
        } else if (FIELD_BODY.equals(e.getName())) {
            write(": ");
            indent++;
        } else if (CLASSIFIER.equals(e.getName())) {
            write(" : ");
        } else if (DEFINITION.equals(e.getName())) {
            // pour une fois on est obligé de passer une ligne dans le
            // start, car on ne sait pas determiner la fin des classifiers
            // qui doivent etre tous sur la ligne du TERM
            newLine();
            indent++;
        } else if (LITERAL_BLOCK.equals(e.getName())) {
            write("::");
            newLine();
            newLine();
            indent++;
        } else if (TABLE.equals(e.getName())) {
            // TODO now we take table as LITERAL_BLOCK, but in near
            // futur we must parse correctly TABLE (show JRSTReader and
            // JRSTLexer too)
        } else if (BULLET_LIST.equals(e.getName())) {
            listType = BULLET_LIST;
        } else if (ENUMERATED_LIST.equals(e.getName())) {
            listType = ENUMERATED_LIST;
            enumStart = Integer.parseInt(e.attributeValue("start"));
        } else if (LIST_ITEM.equals(e.getName())) {
            if (BULLET_LIST.equals(listType)) {
                writeIndent("- ");
            } else if (ENUMERATED_LIST.equals(listType)) {
                writeIndent(enumtype(enumStart++, e.getParent().attributeValue(
                        "enumtype"))
                        + ". ");
            }
            indent++;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codelutin.jrst.DocumentHandler#text(org.dom4j.Text)
     */
    public void text(Text t) {
        if (LITERAL_BLOCK.equals(t.getParent().getName())) {
            writeIndent(t.getText());
        } else {
            write(t.getText());
        }
    }

    protected void newLine() {
        write("\n");
    }

    protected void write(String text) {
        write(text, false);
    }

    protected void writeIndent() {
        write("", true);
    }

    protected void writeIndent(String text) {
        write(text, true);
    }

    /**
     * Ecrit le text, si indent est vrai, alors chaque ligne est indentée
     * 
     * @param text
     * @param indent
     */
    protected void write(String text, boolean doIndent) {
        try {
            String blank = "";
            if (doIndent) {
                blank = string("  ", indent);
            }
            out.write(blank);
            for (char c : text.toCharArray()) {
                out.write(c);
                if (c == '\n') {
                    out.write(blank);
                }
            }
        } catch (IOException eee) {
            if (log.isWarnEnabled()) {
                log.warn("TODO untreated error", eee);
            }
        }
    }
}
