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
/*
 * Class qui redirige la sortie standard pour la laisser en interne
 */
package org.codelutin.jrst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadRedirection extends Thread {
    String str;
    String errors;
    Process process;

    public ThreadRedirection(Process p) {
        process = p;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            BufferedReader readerErrors = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line != null) {
                        if (!line.equals("null"))
                            str += "\n" + line;
                    }
                }

            } finally {
                reader.close();
            }
            try {
                String line;
                while ((line = readerErrors.readLine()) != null) {
                    if (line != null) {
                        if (!line.equals(null))
                            errors += "\n" + line;
                    }

                }

            } finally {
                reader.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String getSortit() {

        String result = "";
        for (String l : str.split("\n"))
            if (!l.equals("null")) {
                result += l + "\n";
            }

        return result;
    }

    public String getErrors() {
        return errors;
    }

}
