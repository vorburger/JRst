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

    static String TEXT = "coucou\n======\n\nun\n\ndeux\n";

    static public void main(String [] args) throws Exception {
        Reader in;
        if(args.length > 0){
            String filename = args[0];
            in = new LineNumberReader(new FileReader(filename));
        }else{
            in = new LineNumberReader(new StringReader(TEXT));
        }

        ElementFactory factory = new RstDocumentFactory();
        factory.addChild(new RstTitleFactory());
        factory.addChild(new RstParaFactory());
        factory.addChild(new RstListFactory());

        factory.init();

        int c = in.read();
        while(c != -1 && factory.accept(c).equals(factory.PARSE_IN_PROGRESS)){
            c = in.read();
        }

        // on ajoute toujours 2 retours chariot pour être sur que la lecture est terminé.
        factory.accept((int)'\n');
        factory.accept((int)'\n');

        Element e = factory.getElement();

        System.out.println(e);
        Generator gen = new RstGenerator();
        gen.visit(e);
    }

} // Parser

