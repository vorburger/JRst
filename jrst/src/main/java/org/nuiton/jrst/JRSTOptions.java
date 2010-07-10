/*
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
 */
package org.nuiton.jrst;

/**
 * Options for the JRSTLexer.
 * 
 * @author Michael Vorburger (mike@vorburger.ch)
 */
public class JRSTOptions {

	/**
	 * If true, Titles where the "underlining" is not exactly as long as the title are accepted anyways.
	 */
	public boolean lenientTitle = false;
	
	/**
	 * Recognizes `=`, `+`, `-`, `~` as title levels, and stores in a 'realLevel' attribute in XML
	 */
	public boolean otherKindsOfTitleLevels = false;

	/**
	 * Keep the 'level' attribute from the final XML?
	 */
	public boolean keepLevel = false;

}
