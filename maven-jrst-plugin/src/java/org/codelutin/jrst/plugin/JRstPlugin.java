package org.codelutin.jrst.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.FileSet;
import org.codelutin.jrst.Parser;
import org.codelutin.util.FileUtil;
import org.codelutin.util.FileUtil.FileAction;

/**
 * @goal jrst
 * @description Generate xdoc with rst
 */
public class JrstPlugin extends AbstractMojo implements FileAction {    
    /**
     * @parameter alias="directory"
     * @required
     */
    private String directoryIn;
    
    /**
     * @parameter alias="directory"
     * @required
     */
    private String directoryOut;

    /**
     * @parameter
     */
    private boolean ignoreErrors = true;
    
    /**
     * @parameter
     */
    private boolean overwrite = false;
    
    /**
     * @parameter
     */
    private boolean verbose = false;
    
    
    private int numberFilesGenerates;
    
    public void execute() throws MojoExecutionException {
        try {
            actionGenerate();
        } catch (GenerationJRstException e) {
            System.out.println("Error during generation for :\n" 
                    + GenerationJRstException.getFilesErrors());
            if(!ignoreErrors)
                throw new MojoExecutionException("Erreur lors de l'execution");
        }
        actionCopy();
    }
    
    private void actionGenerate(){
        getLog().info("Génération des fichier xdocs à partir des fichiers rst");
        numberFilesGenerates = 0;
        FileUtil.walkAfter(new File(directoryIn), this);
        getLog().info("Generating " + numberFilesGenerates + " files to " + directoryOut);
    }
    
    public boolean doAction(File file) {
        String fileIn = file.getAbsoluteFile().toString();
        String fileOut = null;
        
        /* Rst file */
        if (fileIn.matches(".*/[A-Z].*\\.rst")) {
            fileOut = fileIn.replace(directoryIn,directoryOut).replace(".rst",".xml");
            try {
                Parser.parse(Parser.TYPE_XDOC, fileIn, fileOut, overwrite);
                numberFilesGenerates ++;
            } catch (Exception e) {
                getLog().error(e);
                throw new GenerationJRstException(fileIn); 
            }
        }
        
        if(fileOut != null && verbose) {
            getLog().info("Using " + fileIn);
            getLog().info("Generating " + fileOut);
        }
        return true;
    }

    private void actionCopy(){          
        getLog().info("Copie des fichiers images");
        /* Execution de la tâche ant copy */
         
        /* Création d'un projet ant */
        Project project = new Project();
        
        BuildLogger logger = new NoBannerLogger();
        logger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);

        project.init();
        project.getBaseDir();
        project.addBuildListener(logger);

        /* Création de la tâche ant Copy */
        Copy copy = new Copy();
        copy.setProject(project);
        copy.setTaskName("Copy images");

        /* Configuration */
        copy.setTodir(new File(directoryOut));
        copy.setPreserveLastModified(true);
        copy.setOverwrite(overwrite);
        
        FileSet fileSet = new FileSet();
        fileSet.setDir(new File(directoryIn));
        fileSet.setIncludes("**/*.png,**/*.jpeg,**/*.jpg,**/*.gif");
        copy.addFileset(fileSet);
        
        /* Execution */
        copy.execute();
    }

}
