/* *##%
 * Copyright (C) 2006
 *     Code Lutin, Cédric Pineau, Benjamin Poussin
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
 * JRstPlugin.java
 *
 * Created: 14 avril 2006
 *
 * @author ruchaud
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 */

package org.codelutin.jrst.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.codelutin.jrst.JRST;
import org.codelutin.util.FileUtil;
import org.codelutin.util.FileUtil.FileAction;

/**
 * Génére du xdoc à partir de fichiers Rst
 *  
 * @goal jrst
 * @phase pre-site
 */
public class JRstPlugin extends AbstractMojo implements FileAction {    
    /**
     * Répertoire source des fichiers Rst
     * 
     * @parameter default-value="doc"
     * @required
     */
    private String directoryIn = "doc";
    
    /**
     * Répertoire cible des fichiers xdoc générée
     * 
     * @parameter default-value="target/site-build/xdoc"
     * @required
     */
    private String directoryOut = "target/site-build/xdoc";

    /**
     * Arrête la génération en cas d'erreurs
     * 
     * @parameter default-value="true"
     */
    private boolean ignoreErrors = true;
    
    /**
     * Ecrase les fichiers générés
     * 
     * @parameter default-value="false"
     */
    private boolean overwrite = false;
    
    /**
     * Permet d'obtenir plus d'information
     * 
     * @parameter default-value="false"
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
        
        if (fileIn.matches(".*\\.rst")) {
            fileOut = fileIn.replace(directoryIn,directoryOut).replace(".rst",".xml");
            try {
                JRST.generate(JRST.TYPE_XDOC,
                        new File(fileIn), new File(fileOut), overwrite);
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
