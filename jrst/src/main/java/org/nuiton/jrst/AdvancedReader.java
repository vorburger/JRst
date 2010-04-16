/*
 * #%L
 * JRst :: Api
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2004 - 2010 CodeLutin
 * %%
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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.nuiton.jrst;

import org.apache.commons.collections.primitives.ArrayCharList;
import org.apache.commons.collections.primitives.CharList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Le principe est d'avoir dans cette classe le moyen de lire, et de retourner
 * la ou on etait avant la lecture (mark et reset de {@link BufferedReader}).
 * <p>
 * Mais il faut aussi pouvoir dire qu'en fin de compte on ne souhaite pas lire
 * les caracteres que l'on vient de lire ({@link #unread(int)} a peu pres egal a
 * {@link PushbackReader})
 * <p>
 * Le pointer nextChar pointe toujours sur le prochain caractere qui sera lu
 * <p>
 * Lorsque l'on appelle la method {@link #mark()} on vide aussi le buffer pour
 * liberer de la place, car on a plus le moyen de retourner avant le mark que
 * l'on vient de positionner.
 * <p>
 * On contraire du mark de {@link BufferedReader} ou {@link LineNumberReader} il
 * n'y a pas a specifier le nombre de caractere a garder au maximum, seul la
 * memoire nous limite. Du coup si l'on utilise cette classe sans mark, au final
 * on aura dans le buffer tout le contenu du reader, il faut donc utiliser mark
 * avec cette classe
 * 
 * buffer
 * 
 * <pre>
 * #########################
 * 0     &circ;       &circ;
 *       |       |
 *       |       + nextChar
 *       + markChar
 * </pre>
 * 
 * Created: 27 oct. 06 00:24:57
 *
 * @author poussin
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */
public class AdvancedReader {

    /** le nombre d'espace pour remplacer les tabulations */
    protected static final String TAB = "    ";
    /** nombre de caractere lu au minimum sur le vrai reader */
    protected static final int READ_AHEAD = 80;

    protected Reader in;
    protected CharList buffer;

    protected int charNumber;
    protected int charNumberMark;
    protected int lineNumber;
    protected int lineNumberMark;

    protected int nextChar;
    protected int markChar;

    protected int readInMark;

    /**
     * 
     * @param in the io reader
     */
    public AdvancedReader(Reader in) {
        this.in = new LineNumberReader(in);
        buffer = new ArrayCharList();
    }

    public void mark() throws IOException {
        markChar = nextChar;
        charNumberMark = charNumber;
        lineNumberMark = lineNumber;

        free(markChar);
    }

    public void reset() throws IOException {
        nextChar = markChar;
        charNumber = charNumberMark;
        lineNumber = lineNumberMark;

    }

    public int readSinceMark() {
        return nextChar - markChar;
    }

    /**
     * @return the charNumber
     */
    public int getCharNumber() {
        return charNumber;
    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * remove number of char in buffer
     * 
     * @param number
     * @return the real number of char removed from the head of buffer
     * @throws IOException
     */
    private int free(int number) throws IOException {
        // fill(number);
        int result = Math.min(buffer.size(), number);
        buffer.subList(0, result).clear();

        nextChar -= result;
        markChar -= result;

        return result;
    }

    /**
     * ensure that have number char available and not allready read
     * 
     * @param number ?
     * @throws IOException
     */
    private void fill(int number) throws IOException {
        int needed = nextChar + number - buffer.size();
        if (needed > 0) {
            char[] cbuf = new char[needed + READ_AHEAD];
            int read = in.read(cbuf);
            for (int i = 0; i < read; i++) {
                buffer.add(cbuf[i]);
            }
        }
    }

    public boolean eof() throws IOException {
        boolean result = -1 == read();
        if (!result) {
            unread(1);
        }
        return result;
    }

    public int skip(int number) throws IOException {
        int result = 0;
        while (result < number && read() != -1) {
            result++;
        }
        return result;
    }

    /**
     * go left in reading char buffer
     * 
     * @param number
     * @return realy unread char number
     */
    public int unread(int number) {
        int result = Math.min(number, nextChar);
        nextChar -= result;

        charNumber -= result;
        for (int i = nextChar; i < nextChar + result; i++) {
            if (buffer.get(i) == '\n') {
                lineNumber--;
            }
        }

        return result;
    }

    /**
     * Unread the line length
     * 
     * @param line
     *            line used to know the length to unread
     * @param addNewLine
     *            if true then add +1 to unread lenght for not present '\n'
     * @return number of unread char
     */
    public int unread(String line, boolean addNewLine) {
        int result = unread(line.length() + (addNewLine ? 1 : 0));
        return result;
    }

    /**
     * Unread the line length
     * 
     * @param lines
     *            lines used to know the length to unread
     * @param addNewLine
     *            if true then add +1 for each line to unread lenght for not
     *            present '\n'
     * @return number of unread char
     */
    public int unread(String[] lines, boolean addNewLine) {
        int result = 0;
        for (String line : lines) {
            result += unread(line, addNewLine);
        }
        return result;
    }

    /**
     * read one char in buffer
     * 
     * @return the next car
     * @throws IOException pour tout pb de lecture
     */
    public int read() throws IOException {
        fill(1);
        int result = -1;
        if (nextChar < buffer.size()) {
            result = buffer.get(nextChar++);
            charNumber++;
            if (result == '\n') {
                lineNumber++;
            }
        }
        return result;
    }

    /**
     * read one line
     * 
     * @return one line without '\n' or null if end of file
     * @throws IOException pour tout pb de lecture
     */
    public String readLine() throws IOException {
        StringBuffer result = new StringBuffer(READ_AHEAD);
        int c = read();
        while (c != -1 && c != '\n') {
            result.append((char) c);
            c = read();
        }
        if (c == -1 && result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }

    /**
     * passe les lignes blanches
     * 
     * @throws IOException
     */
    public void skipBlankLines() throws IOException {
        readUntil("^\\s*\\S+.*");
    }

    /**
     * lit toutes les lignes du fichier
     * 
     * @return toutes les lignes du fichier
     * @throws IOException pour tout pb de lecture
     */
    String[] readAll() throws IOException {
        String[] result = readLines(-1);
        return result;
    }

    /**
     * lit un certain nombre de lignes
     * 
     * @param count
     *            si negatif lit toutes les lignes
     * @return un certain nombre de lignes
     * @throws IOException pour tout pb de lecture
     */
    public String[] readLines(int count) throws IOException {
        ArrayList<String> result = new ArrayList<String>();

        String tmp = "";
        for (int i = count; tmp != null && i != 0; i--) {
            tmp = readLine();
            if (tmp != null) {
                result.add(tmp);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * lit les lignes jusqu'a la premiere ligne blanche (non retournée)
     * 
     * @return toutes les ligne jusqu'à la première ligne blanche (non incluse)
     * @throws IOException pour tout pb de lecture
     */
    public String[] readUntilBlank() throws IOException {
        String[] result = readUntil("\\s*");
        return result;
    }

    /**
     * lit les lignes jusqu'a la ligne qui correspond pas au pattern, cette
     * ligne n'est pas mise dans le resultat retourne
     * 
     * @param pattern ?
     * @return les lignes jusqu'a la ligne qui correspond pas au pattern, cette
     * ligne n'est pas mise dans le resultat retourne
     * @throws IOException pour tout pb de lecture
     */
    public String[] readUntil(String pattern) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        String tmp = readLine();
        while (tmp != null && !tmp.matches(pattern)) {
            result.add(tmp);
            tmp = readLine();
        }
        if (tmp != null) {
            unread(tmp.length() + 1); // +1 for '\n' not in line
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * lit les lignes tant que les lignes correspondent au pattern
     * 
     * @param pattern ?
     * @return les lignes tant que les lignes correspondent au pattern
     * @throws IOException pour tout pb de lecture
     */
    public String[] readWhile(String pattern) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        String tmp = readLine();
        while (tmp != null && tmp.matches(pattern)) {
            result.add(tmp);
            tmp = readLine();
        }
        if (tmp != null) {
            unread(tmp.length() + 1); // +1 for '\n' not in line
        }
        return result.toArray(new String[result.size()]);
    }

}
