package org.codelutin.jrst;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import org.codelutin.jrst.JRSTReader;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import sdoc.*;

public class Compare {
	private static final String PATH="/home/letellier/PROJET/jrst2/"; // a redefinir manuelement
	static public void main(String [] args) throws Exception {
		File source = null;
		if (args.length>0){
			source = new File(args[0]);
			if (source != null)
				parser(source);
		}
		else
			System.err.println("Argument source manquant");
	}
	private static void parser(File source) throws Exception {
		URL url = source.toURL();
        Reader in = new InputStreamReader(url.openStream());       
        JRSTReader jrst = new JRSTReader();
        Document docRst = jrst.read(in); // JRST
        String cmd = "rst2xml "+PATH+source.getPath()+" "+PATH+"src/test/org/codelutin/jrst/comparePython.xml";
        Runtime.getRuntime().exec("rm "+PATH+"src/test/org/codelutin/jrst/comparePython.xml");
        final Process p =Runtime.getRuntime().exec(cmd); // Python
        Thread t = new Thread() {
        	public void run() {
        		try {
        			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        			String line = "";
        			try {
        				while((line = reader.readLine()) != null) {
        					System.err.println(line); // Pour rediriger la sortie des érreurs
        				}
        			} finally {
        				reader.close();
        			}
        		} catch(IOException ioe) {
        			ioe.printStackTrace();
        		}
        	}
        };
        t.start();
        while(t.isAlive()); // On attend que le processus ce termine
        t.stop();
        p.destroy();
        File xml = new File("src/test/org/codelutin/jrst/comparePython.xml");
        SAXReader sr= new SAXReader();
        Document docPython =  sr.read(xml);
        compare(docRst, docPython);
	}
	private static void compare(Document docRst, Document docPython) throws IOException {
		JTextArea jrst = new JTextArea();
		JTextArea python = new JTextArea();
		SyntaxSupport support =  SyntaxSupport.getInstance();
		support.addSupport(SyntaxSupport.XML_LEXER, jrst); // Coloration
		support.addSupport(SyntaxSupport.XML_LEXER, python);
		jrst.getDocument().putProperty(SyntaxDocument.tabSizeAttribute, new Integer(2));
		python.getDocument().putProperty(SyntaxDocument.tabSizeAttribute,new Integer(2));
		JScrollPane spJrst = new JScrollPane(jrst);
		JScrollPane spPython = new JScrollPane(python);
		// Ajout de gutter : la numérotation des lignes
		spJrst.setRowHeaderView(new Gutter(jrst , spJrst));
		spPython.setRowHeaderView(new Gutter(python , spPython));
		jrst.setText(indent(docRst));	// On indente
		python.setText(indent(docPython));
		JScrollPane droit = new JScrollPane(spJrst);
		JScrollPane gauche = new JScrollPane(spPython);
		JFrame comparateur = new JFrame();
		JSplitPane separateur = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true, gauche,droit);
		separateur.setResizeWeight(0.5);
		separateur.setOneTouchExpandable(true);
		separateur.setContinuousLayout(true);
		comparateur.add(separateur);
		comparateur.setSize(1280, 900);
		comparateur.setVisible(true);
		comparateur.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		comparateur.setTitle("Comparateur");
	}
	private static String indent(Document xml) throws IOException {
		// Indentation du document
		OutputFormat of = new OutputFormat("  ",true);
		StringWriter out = new StringWriter();
		XMLWriter writer = new XMLWriter(out, of);
		writer.write(xml);
		writer.close();
		return out.toString();
	}
}

