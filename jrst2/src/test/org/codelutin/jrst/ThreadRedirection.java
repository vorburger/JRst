/*
 * Class qui redirige la sortie standard pour la laisser en interne
 */
package org.codelutin.jrst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadRedirection extends Thread{
    String str;
    String errors;
    Process process;
    public ThreadRedirection (Process p){
        process=p;
    }
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            BufferedReader readerErrors = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            try {
                String line;
                while ( (  line = reader.readLine () ) != null ) {
                    if (line!=null){
                        if (!line.equals("null"))
                        str += "\n"+line;
                    }
                }

            } finally {
                reader.close();
            }
            try {
                String line;
                while ( (  line = readerErrors.readLine () ) != null ) {
                    if (line!=null){
                        if (!line.equals(null))
                        errors += "\n"+line;
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
        
        String result="";
        for (String l : str.split("\n"))
            if (!l.equals("null")){
                result+=l+"\n";
            }
                
        return result;
    }
    public String getErrors(){
        return errors;
    }
    
}
