package org.codelutin.jrst;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLTestCase;



public class XMLCaseTest extends XMLTestCase {
    public XMLCaseTest(String name) {
        super(name);
    }

    

    public DetailedDiff testAllDifferences(String myControlXML,String myTestXML) throws Exception {
       DetailedDiff myDiff = new DetailedDiff(compareXML(myControlXML, myTestXML));
       return myDiff;
        
    }

    
}
