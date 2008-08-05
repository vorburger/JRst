/* *##%
 * Copyright (C) 2006
 *     Code Lutin, CÃ©dric Pineau, Benjamin Poussin
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codelutin.jrst.JRST;
import org.codelutin.util.FileUtil;
import org.codelutin.util.FileUtil.FileAction;

import java.io.File;
import java.io.IOException;

/**
 * GÃ©nÃ©re du xdoc Ã  partir de fichiers Rst
 * <p/>
 * Les fichiers rst sont dans les repertoires:
 * <li> src/site/fr/rst/
 * <li> src/site/en/rst/
 * <li> src/site/es/rst/
 * <li> ...
 * <p/>
 * et ils iront au final dans:
 * <li> target/site-build/fr/xdoc/
 * <li> target/site-build/en/xdoc/
 * <li> target/site-build/es/xdoc/
 * <li> ...
 * <p/>
 * tous les autres fichiers iront dans target/site-build en respectant la
 * meme hierarchie de repertoire.
 *
 * @goal jrst
 * @phase pre-site
 */
public class JRstPlugin extends AbstractMojo implements FileAction {
    /**
     * Répertoire source des fichiers Rst
     *
     * @parameter default-value="src/site"
     * @required
     */
    private String directoryIn = "src/site";

    /**
     * Encoding en entrée
     *
     * @parameter default-value="UTF-8"
     */
    private String inputEncoding = "UTF-8";

    /**
     * Encoding en sortie
     *
     * @parameter default-value="UTF-8"
     */
    private String outputEncoding = "UTF-8";

    /**
     * RÃ©pertoire cible des fichiers xdoc gÃ©nÃ©rÃ©e
     *
     * @parameter default-value="target/site-build"
     * @required
     */
    private String directoryOut = "target/site-build";

    /**
     * Arrï¿½te la gï¿½nï¿½ration en cas d'erreurs
     *
     * @parameter default-value="true"
     */
    private boolean ignoreErrors = true;

    /**
     * Ecrase les fichiers gï¿½nï¿½rï¿½s
     *
     * @parameter default-value="ifnewer"
     */
    private String overwrite = "ifnewer";

    /** @parameter default-value="fr" */
    private String defaultLocale = "fr";

    /**
     * Permet d'obtenir plus d'information
     *
     * @parameter default-value="false"
     */
    private boolean verbose = false;

    /**
     * Projet en cours de deploiement.
     *
     * @parameter expression="${project}"
     */

    private MavenProject project;

    private int numberFilesGenerates;

    /**
     * Retourne la langue par defaut precisÃ© dans le pom.xml
     *
     * @return
     */
    protected String getDefaultLocale() {
        String result = defaultLocale;
        return result;
    }

    /** @return the overwrite */
    public JRST.Overwrite getOverwrite() {
        JRST.Overwrite result = JRST.Overwrite.NEVER;
        if (this.overwrite.contains("new")) {
            result = JRST.Overwrite.IFNEWER;
        } else if ("true".equalsIgnoreCase(this.overwrite) ||
                "alltime".equalsIgnoreCase(this.overwrite)) {
            result = JRST.Overwrite.ALLTIME;
        }
        return result;
    }

    public void execute() throws MojoExecutionException {
        try {
            actionGenerate();
        } catch (GenerationJRstException e) {
            System.out.println("Error during generation for :\n"
                    + GenerationJRstException.getFilesErrors());
            if (!ignoreErrors)
                throw new MojoExecutionException("Erreur lors de l'execution");
        }
        actionCopy();
    }

    private void actionGenerate() {
        getLog().info("GÃ©nÃ©ration des fichier xdocs Ã  partir des fichiers rst");
        numberFilesGenerates = 0;
        FileUtil.walkAfter(new File(directoryIn), this);
        getLog().info("Generating " + numberFilesGenerates + " files to " + directoryOut);
    }

    public boolean doAction(File file) {
        String fileIn = file.getAbsoluteFile().toString();
        String fileOut = null;
        getLog().info("Using " + fileIn);
        if (fileIn.matches(".*[/\\\\]rst[/\\\\].*\\.rst")) {
            fileOut =
                    fileIn.replace(directoryIn, directoryOut)
                            .replace(".rst", ".xml")
                            .replaceFirst("([/\\\\])rst([/\\\\])", "$1xdoc$2");

            if (defaultLocale != null && !"".equals(defaultLocale)) {
                fileOut = fileOut.replaceFirst("([/\\\\])" + defaultLocale + "([/\\\\])", "$1");
            }

            try {
                JRST.generate(JRST.TYPE_XDOC,
                        new File(fileIn), inputEncoding, new File(fileOut), outputEncoding, getOverwrite());
                numberFilesGenerates++;
            } catch (Exception e) {
                getLog().error(e);
                if (!ignoreErrors) {
                    throw new GenerationJRstException(fileIn);
                }
            }
        }

        if (fileOut != null && verbose) {
            getLog().info("Using " + fileIn);
            getLog().info("Generating " + fileOut);
        }
        return true;
    }

    private void actionCopy() {
        getLog().info("Copie des fichiers images");
        /* Execution de la tÃ¢che ant copy */

        try {
            if (defaultLocale != null && !"".equals(defaultLocale)) {
                // copie de tous les fichiers non rst
                FileUtil.copyAndRenameRecursively(
                        new File(directoryIn),
                        new File(directoryOut),
                        false,
                        "([/\\\\])" + defaultLocale + "([/\\\\])", "$1",
                        true,
                        ".*[/\\\\]rst[/\\\\].*");

                // copie des images du repertoire rst dans le build-site
                FileUtil.copyAndRenameRecursively(
                        new File(directoryIn),
                        new File(directoryOut),
                        false, // on ne copie que le contenu de directoryIn
                        "([/\\\\])" + defaultLocale + "([/\\\\])rst([/\\\\])",
                        "$1resources$2",
                        false,
                        ".*[/\\\\]rst[/\\\\].*(\\.png|\\.jpeg|\\.jpg|\\.gif)$");
            } else {
                // copie de tous les fichiers non rst
                FileUtil.copyAndRenameRecursively(
                        new File(directoryIn),
                        new File(directoryOut),
                        false,
                        "",
                        "",
                        true,
                        ".*[/\\\\]rst[/\\\\].*");

                // copie des images du repertoire rst dans le build-site
                FileUtil.copyAndRenameRecursively(
                        new File(directoryIn),
                        new File(directoryOut),
                        false,
                        "([/\\\\])rst([/\\\\])",
                        "$1resources$2",
                        false,
                        ".*[/\\\\]rst[/\\\\].*(\\.png|\\.jpeg|\\.jpg|\\.gif)$");
            }
        } catch (IOException eee) {
            getLog().error(eee);
        }

//        /* CrÃ©ation d'un projet ant */
//        Project project = new Project();
//        
//        BuildLogger logger = new NoBannerLogger();
//        logger.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);
//        logger.setOutputPrintStream(System.out);
//        logger.setErrorPrintStream(System.err);
//
//        project.init();
//        project.getBaseDir();
//        project.addBuildListener(logger);
//
//        /* CrÃ©ation de la tÃ¢che ant Copy */
//        Copy copy = new Copy();
//        copy.setProject(project);
//        copy.setTaskName("Copy images");
//
//        /* Configuration */
//        copy.setTodir(new File(directoryOut + "/resources")); // with maven2 site plugin images must be in resources
//        copy.setPreserveLastModified(true);
//        copy.setOverwrite("true".equalsIgnoreCase(overwrite));
//        
//        FileSet fileSet = new FileSet();
//        fileSet.setDir(new File(directoryIn));
//        fileSet.setIncludes("**/*.png,**/*.jpeg,**/*.jpg,**/*.gif");
//        copy.addFileset(fileSet);
//        
//        /* Execution */
//        copy.execute();
    }

}
