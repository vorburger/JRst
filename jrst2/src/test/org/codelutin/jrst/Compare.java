package org.codelutin.jrst;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;


import org.codelutin.jrst.JRSTReader;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import org.dom4j.Element;




public class Compare {
	private static final String PATH="/home/letellier/PROJET/jrst2/";
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
        Document docRst = jrst.read(in); 
        String cmd = "rst2xml "+PATH+source.getPath()+" "+PATH+"src/test/org/codelutin/jrst/comparePython.xml";
        System.out.println(cmd);
        Runtime.getRuntime().exec("rm "+PATH+"src/test/org/codelutin/jrst/comparePython.xml");
        final Process p =Runtime.getRuntime().exec(cmd);
        //Runtime.getRuntime().exec(new String[] { "rst2xml", PATH+source.getPath(), PATH+"src/test/org/codelutin/jrst/comparePython.xml" });
        
        Thread t = new Thread() {
        	public void run() {
        		try {
        			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        			String line = "";
        			try {
        				while((line = reader.readLine()) != null) {
        					System.err.println(line);
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
        while(t.isAlive());
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
		jrst.setText(convert(docRst.asXML()));
		python.setText(convert(docPython.asXML()));
		
	
		JScrollPane droit = new JScrollPane(jrst);
		JScrollPane gauche = new JScrollPane(python);
		JFrame comparateur = new JFrame();
		JSplitPane separateur = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true, gauche,droit);
		separateur.setResizeWeight(0.5);
		separateur.setOneTouchExpandable(true);
		separateur.setContinuousLayout(true);
		comparateur.add(separateur);

		comparateur.setSize(1280, 900);
		comparateur.setVisible(true);
		comparateur.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	private static String convert(String xml) throws IOException {
		
		
		
		
		String txt="";
		txt+="\n"+xml.replaceAll("><", ">\n<");
        
		StringReader sr = new StringReader(txt);
		BufferedReader bf= new BufferedReader(sr);
		Pattern ligneFinit = Pattern.compile("(^<.+(</|/>))|(^<\\?)|(^<\\!)");
		Pattern ligneOuvrante = Pattern.compile("^<.+>$");
		Pattern ligneFermante = Pattern.compile("^</.+>$");
		String line = bf.readLine();
        String txtFin="";
        int level = 0;
       	String sLevel = "";
       	boolean ouvrante=false;
       	int cnt=0;
       	while (line!=null){
       		cnt++;
       		sLevel="";
	       	for (int i = 0;i<level;i++)
	   			sLevel+="   ";
       		Matcher matcher = ligneFinit.matcher(line);
       		if (!matcher.find()){
       			matcher = ligneFermante.matcher(line);
       			if (matcher.find()){
       				level--;
       				ouvrante=false;
       				sLevel="";
       		       	for (int i = 0;i<level;i++)
       		   			sLevel+="   ";
       			}else{
       				matcher = ligneOuvrante.matcher(line);
           			if (matcher.find()){
           				level++;
           				ouvrante=true;
           			}
       			}
       		}
       		/*else{
       			if (ouvrante){
       				ouvrante=false;
       				level++;
       			}
       		}*/
       		
       		
	       
	   		txtFin += "\n"+cnt+" "+sLevel+line;
	       		
       		
       		line = bf.readLine();
       	}
       
    	return txtFin;
		
	}
	
		
}

