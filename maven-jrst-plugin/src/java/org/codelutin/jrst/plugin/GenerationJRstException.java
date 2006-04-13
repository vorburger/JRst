package org.codelutin.jrst.plugin;


public class GenerationJRstException extends RuntimeException {

    static private String FilesErrors = "";
    
    public GenerationJRstException(String fileIn) {
        FilesErrors += fileIn + "\n";
    }

    public static String getFilesErrors() {
        return FilesErrors;
    }
}
