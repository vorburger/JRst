/* *##%
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

/* *
 * ParseResult.java
 *
 * Created: 22 janv. 2004
 *
 * @author Benjamin Poussin <poussin@codelutin.com>
 * Copyright Code Lutin
 * @version $Revision$
 *
 * Mise a jour: $Date$
 * par : $Author$
 */

package org.codelutin.jrst;

public class ParseResult { // ParseResult

    int consumedCharCount = -1;
    String error = "";

    public final static ParseResult ACCEPT = new ParseResult();
    public final static ParseResult FINISHED = new ParseResult();
    public final static ParseResult FAILED = new ParseResult();
    public final static ParseResult IN_PROGRESS = new ParseResult();

    public ParseResult setConsumedCharCount(int consumedCharCount) {
        this.consumedCharCount = consumedCharCount;
        return this;
    }
    public int getConsumedCharCount() {
        return consumedCharCount;
    }

    public String getError() {
        return error;
    }
    public ParseResult setError(String error) {
        this.error = error;
        return this;
    }

} // ParseResult

