/* *##% Plugin maven JRst
 * Copyright (C) 2006 - 2008 CodeLutin
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
 * <http://www.gnu.org/licenses/lgpl-3.0.html>. ##%*/

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
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codelutin.jrst.JRST;
import org.codelutin.util.FileUtil;
import org.codelutin.util.FileUtil.FileAction;

/**
 * Génére du xdoc à partir de fichiers Rst.
 * <p/>
 * Les fichiers rst sont dans les repertoires:
 * <li> src/site/fr/rst/
 * <li> src/site/en/rst/
 * <li> src/site/es/rst/
 * <li> ...
 * <p/>
 * et ils iront au final dans:
 * <li> target/generated-site/fr/xdoc/
 * <li> target/generated-site/en/xdoc/
 * <li> target/generated-site/es/xdoc/
 * <li> ...
 * <p/>
 * tous les autres fichiers iront dans target/generated-site en
 * respectant la meme hierarchie de repertoire.
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
     * Répertoire cible des fichiers xdoc générée
     * 
     * @parameter default-value="target/generated-site"
     * @required
     */
    private String directoryOut = "target/generated-site";

    /**
     * Arréte la génération en cas d'erreurs
     * 
     * @parameter default-value="true"
     */
    private boolean ignoreErrors = true;

    /**
     * Ecrase les fichiers générés
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
     * Number of generated files.
     */
    private int numberFilesGenerates;

    /**
     * Retourne la langue par defaut precisé dans le pom.xml
     * 
     * @return la langue par defaut precisé dans le pom.xml
     */
    protected String getDefaultLocale() {
        return defaultLocale;
    }

    /** @return the overwrite */
    public JRST.Overwrite getOverwrite() {
        JRST.Overwrite result = JRST.Overwrite.NEVER;
        if (this.overwrite.contains("new")) {
            result = JRST.Overwrite.IFNEWER;
        } else if ("true".equalsIgnoreCase(this.overwrite)
                || "alltime".equalsIgnoreCase(this.overwrite)) {
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
        getLog().info("Génération des fichier xdocs à partir des fichiers rst");
        numberFilesGenerates = 0;
        FileUtil.walkAfter(new File(directoryIn), this);
        getLog().info(
                "Generating " + numberFilesGenerates + " files to "
                        + directoryOut);
    }

    public boolean doAction(File file) {
        String fileIn = file.getAbsoluteFile().toString();
        String fileOut = null;
        getLog().info("Using " + fileIn);
        if (fileIn.matches(".*[/\\\\]rst[/\\\\].*\\.rst")) {
            fileOut = fileIn.replace(directoryIn, directoryOut).replace(".rst",
                    ".xml").replaceFirst("([/\\\\])rst([/\\\\])", "$1xdoc$2");

            if (defaultLocale != null && !"".equals(defaultLocale)) {
                fileOut = fileOut.replaceFirst("([/\\\\])" + defaultLocale
                        + "([/\\\\])", "$1");
            }

            try {
                JRST.generate(JRST.TYPE_XDOC, new File(fileIn), inputEncoding,
                        new File(fileOut), outputEncoding, getOverwrite());
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
        /* Execution de la tâche ant copy */

        try {
            if (defaultLocale != null && !"".equals(defaultLocale)) {
                // copie de tous les fichiers non rst
                FileUtil.copyAndRenameRecursively(new File(directoryIn),
                        new File(directoryOut), false, "([/\\\\])"
                                + defaultLocale + "([/\\\\])", "$1", true,
                        ".*[/\\\\]rst[/\\\\].*");

                // copie des images du repertoire rst dans le build-site
                FileUtil.copyAndRenameRecursively(
                        new File(directoryIn),
                        new File(directoryOut),
                        false, // on ne copie que le contenu de directoryIn
                        "([/\\\\])" + defaultLocale + "([/\\\\])rst([/\\\\])",
                        "$1resources$2", false,
                        ".*[/\\\\]rst[/\\\\].*(\\.png|\\.jpeg|\\.jpg|\\.gif)$");
            } else {
                // copie de tous les fichiers non rst
                FileUtil.copyAndRenameRecursively(new File(directoryIn),
                        new File(directoryOut), false, "", "", true,
                        ".*[/\\\\]rst[/\\\\].*");

                // copie des images du repertoire rst dans le build-site
                FileUtil.copyAndRenameRecursively(new File(directoryIn),
                        new File(directoryOut), false, "([/\\\\])rst([/\\\\])",
                        "$1resources$2", false,
                        ".*[/\\\\]rst[/\\\\].*(\\.png|\\.jpeg|\\.jpg|\\.gif)$");
            }
        } catch (IOException eee) {
            getLog().error(eee);
        }
    }
}
