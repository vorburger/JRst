/*##%
 * Copyright (C) 2002, 2003 Code Lutin
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

/*
 * Parser.java
 *
 * Created: 7 oct. 2003
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

import java.util.ArrayList;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.FileReader;

public abstract class Parser { // Parser

    static String TEXT = "======\ncoucou\n======\n\nun\n\ndeux\n";

    static public void main(String [] args) throws Exception {
        Reader in;
        if(args.length > 0){
            String filename = args[0];
            in = new LineNumberReader(new FileReader(filename));
        }else{
            in = new LineNumberReader(new StringReader(TEXT));
        }

        DocumentFactory document = new DocumentFactory();
        ElementFactory title = new TitleFactory();
        ElementFactory bulletList = new BulletListFactory();
        ElementFactory fieldList = new FieldListFactory();
        ElementFactory para = new ParaFactory();

        document.addChild(title);
        document.addChild(bulletList);
        document.addChild(fieldList);
        document.addChild(para); // mettre le paragraphe a la fin car il mange tout

        bulletList.addChild(fieldList);
        bulletList.addChild(bulletList);
        bulletList.addChild(para); // mettre le paragraphe a la fin car il mange tout

        fieldList.addChild(fieldList);
        fieldList.addChild(bulletList);
        fieldList.addChild(para); // mettre le paragraphe a la fin car il mange tout

        ParseResult result = ParseResult.IN_PROGRESS;
        int c = in.read();
        while(c != -1 && ((result = document.parse(c)) == ParseResult.IN_PROGRESS)){
            c = in.read();
        }

        Element e = document.getElement();

        Generator gen = new HtmlGenerator();
        gen.visit(e);

        if(result == ParseResult.FAILED){
            System.out.println(result.getError());
            System.out.println("buffer was:'''");
            System.out.println(document.getBuffer().toString());
            System.out.println("'''");
        }

    }

} // Parser

