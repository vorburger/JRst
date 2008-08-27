/**
 * *##% JRst
 * Copyright (C) 2004 - 2008 CodeLutin
 *
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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*
 */
/**
 * Class de test pour comparer le XML de DocUtils avec celui du JRST
 * 
 */
package org.codelutin.jrst;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import org.codelutin.jrst.JRSTReader;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import sdoc.*;

public class Compare {
    static boolean[] bColorRst;

    static boolean[] bColorPython;

    // redefinir
    // manuelement
    static public void main(String[] args) throws Exception {
        File source = null;
        if (args.length > 0) {
            source = new File(args[0]);
            if (source != null)
                parser(source);
        } else
            System.err.println("Argument source manquant");
    }

    private static void parser(File source) throws Exception {
        URL url = source.toURL();
        Reader in = new InputStreamReader(url.openStream());
        JRSTReader jrst = new JRSTReader();
        Document docRst = jrst.read(in); // JRST
        String cmd = "rst2xml " + source.getPath();
        Process p = Runtime.getRuntime().exec(cmd); // Python
        ThreadRedirection t = new ThreadRedirection(p);
        t.start();
        p.waitFor(); // On attend que le processus ce termine
        String strPython = t.getSortit().replaceAll("null", "");
        t.stop();
        p.destroy();
        SAXReader sr = new SAXReader();
        Document docPython = sr.read(new StringReader(t.getSortit()));
        String diff = test(docRst, docPython);
        String sDocRst = indent(docRst); // On indente
        String sDocPython = indent(docPython);
        compare(sDocRst, sDocPython, diff);
    }

    private static String test(Document docRst, Document docPython)
            throws Exception {
        String[] sDocRst = indent(docRst).split("\n");
        bColorRst = new boolean[sDocRst.length];
        for (int i = 0; i < bColorRst.length; i++)
            bColorRst[i] = false;
        String[] sDocPython = indent(docPython).split("\n");
        bColorPython = new boolean[sDocPython.length];
        for (int i = 0; i < bColorPython.length; i++)
            bColorPython[i] = false;
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setNormalizeWhitespace(true);
        XMLCaseTest test = new XMLCaseTest("test");
        DetailedDiff myDiff = test.testAllDifferences(docPython.asXML(), docRst
                .asXML());
        List allDifferences = myDiff.getAllDifferences();
        String text = "";
        Pattern pattern = Pattern.compile("at\\s(\\/\\w+\\[\\d+])+");
        for (int i = 0; i < allDifferences.size(); i++) {
            String diff = allDifferences.get(i).toString();
            int nbLineRst = 0;
            int nbLinePython = 0;
            Matcher matcher = pattern.matcher(diff);
            if (matcher.find()) {
                nbLineRst = findLine(matcher.group(), sDocRst);
                bColorRst[nbLineRst] = true;
            }
            if (matcher.find()) {
                nbLinePython = findLine(matcher.group(), sDocPython);
                bColorPython[nbLinePython] = true;
            }
            text += "L python : " + (nbLinePython + 1) + " L rst : "
                    + (nbLineRst + 1) + " " + diff + "\n\n";
        }
        return text;
    }

    private static int findLine(String match, String[] doc) {
        Pattern pattern2 = Pattern.compile("\\/\\w+\\[\\d+]");
        Matcher matcher2 = pattern2.matcher(match);
        LinkedList<String> names = new LinkedList<String>();
        LinkedList<Integer> rgs = new LinkedList<Integer>();
        while (matcher2.find()) {
            names.add(matcher2.group().substring(1,
                    matcher2.group().indexOf("[")));
            rgs.add(Integer.valueOf(matcher2.group().substring(
                    matcher2.group().indexOf("[") + 1,
                    matcher2.group().indexOf("]"))));
        }
        int cntName = 0;
        int nbLine = 0;
        for (String name : names) {
            int trouve = 0;
            for (int i = nbLine; i < doc.length; i++) {
                String line = doc[i];
                if (line.matches(".*\\<" + name + ".*")) {
                    trouve++;
                    if (trouve == rgs.get(cntName)) {
                        nbLine = i;
                    }
                }
            }
            cntName++;
        }
        return nbLine;
    }

    private static void compare(String docRst, String docPython, String diff)
            throws IOException {
        JTextArea jrst = new JTextArea();
        JTextArea python = new JTextArea();
        jrst.setEditable(false);
        python.setEditable(false);
        SyntaxSupport support = SyntaxSupport.getInstance();
        support.addSupport(SyntaxSupport.XML_LEXER, jrst); // Coloration
        support.addSupport(SyntaxSupport.XML_LEXER, python);

        jrst.getDocument().putProperty(SyntaxDocument.tabSizeAttribute,
                new Integer(2));
        python.getDocument().putProperty(SyntaxDocument.tabSizeAttribute,
                new Integer(2));

        JScrollPane spJrst = new JScrollPane(jrst);
        JScrollPane spPython = new JScrollPane(python);

        // Ajout de gutterColor : la numerotation des lignes et coloration des
        // erreurs
        spJrst.setRowHeaderView(new GutterColor(jrst, spJrst, bColorRst));
        spPython.setRowHeaderView(new GutterColor(python, spPython,
                bColorPython));
        jrst.setText(docRst);
        python.setText(docPython);
        JScrollPane droit = new JScrollPane(spJrst);
        JScrollPane gauche = new JScrollPane(spPython);
        JFrame comparateur = new JFrame();
        JSplitPane separateurVert = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true, gauche, droit);
        separateurVert.setResizeWeight(0.5);
        separateurVert.setOneTouchExpandable(true);
        separateurVert.setContinuousLayout(true);
        JTextArea differences = new JTextArea(diff);
        differences.setRows(10);
        differences.setEditable(false);
        JScrollPane scrollDiff = new JScrollPane(differences);
        JSplitPane separateurHoriz = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, separateurVert, scrollDiff);
        separateurHoriz.setResizeWeight(0.7);
        comparateur.add(separateurHoriz);
        comparateur.setSize(1280, 900);
        comparateur.setVisible(true);
        comparateur.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        comparateur.setTitle("Comparateur");
    }

    private static String indent(Document xml) throws IOException {
        // Indentation du document
        OutputFormat of = new OutputFormat("  ", true);
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out, of);
        writer.write(xml);
        writer.close();
        return out.toString();
    }
}