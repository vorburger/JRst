/* *##% Plugin maven JRst
 * Copyright (C) 2006 - 2009 CodeLutin
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

package org.nuiton.jrst.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.nuiton.jrst.JRST;
import org.nuiton.util.MirroredFileUpdater;

import java.io.File;
import java.io.IOException;
import org.nuiton.util.PluginHelper;

/**
 * Transform rst site documentation into xdoc, to
 * be used by maven
 * <p/>
 * RST file have to be located in:
 * <li>src/site/en/rst/</li>
 * <li>src/site/fr/rst/</li>
 * <li>src/site/es/rst/</li>
 * <li>...</li>
 * and they will be generated in :
 * <li>target/generated-site/xdoc/ (if <tt>en</tt> is default language)</li>
 * <li>target/generated-site/fr/xdoc/</li>
 * <li>target/generated-site/es/xdoc/</li>
 * <li>...</li>
 * All other files will be put on <tt>target/generated-site</tt> in
 * same directory structure.
 *
 * Created: 14 avril 2006
 *
 * @author ruchaud
 * @version $Revision$
 *
 * Last update: $Date$
 * by : $Author$
 * 
 * @goal jrst
 * @phase pre-site
 */
public class JRstPlugin extends AbstractMojo {

    /**
     * Default copy patterns if {@link #includeResources}
     * is <tt>null</tt>.
     */
    protected static final String[] DEFAULT_INCLUDE_RESOURCES = {
        "**\\/*.png",
        "**\\/*.jpeg",
        "**\\/*.jpg",
        "**\\/*.gif"
    };

    /**
     * Site source directory.
     *
     * @parameter default-value="src/site"
     * @since 0.8
     */
    protected File directoryIn;

    /**
     * Site source encoding.
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     * @since 0.8
     */
    protected String inputEncoding = "UTF-8";

    /**
     * Site generated directory.
     *
     * @parameter default-value="target/generated-site"
     * @since 0.8
     */
    protected File directoryOut;
    
    /**
     * Site generated encoding.
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     * @since 0.8
     */
    protected String outputEncoding = "UTF-8";

    /**
     * Copied resource destination directory.
     *
     * @parameter default-value="target/generated-site/resources"
     * @since 0.8
     */
    protected File resourceDirectoryOut;

    /**
     * Ignore errors.
     *
     * @parameter default-value="true"
     * @since 0.8
     */
    protected boolean ignoreErrors = true;

    /**
     * Overwrite already existing generated files.
     *
     * @parameter default-value="ifnewer"
     * @since 0.8
     */
    protected String overwrite = "ifnewer";

    /**
     * Default locale for generated files structure.
     *
     * @parameter default-value="en"
     * @since 0.8
     */
    protected String defaultLocale = "en";

    /**
     * Verbose.
     *
     * @parameter default-value="${maven.verbose}" expression="${jrst.verbose}"
     * @since 0.8
     */
    protected boolean verbose;

    /**
     * Overwrite resources.
     *
     * @parameter default-value="false" expression="${jrst.force}"
     * @since 0.8
     */
    protected boolean force;

    /**
     * Resources patterns to copy.
     *
     * @parameter expression="${jrst.includeResources}"
     * @since 0.8.3
     */
    protected String[] includeResources;

    /** Number of generated files. */
    protected int numberFilesGenerates;

    /** 
     * Get overwrite.
     * 
     * @see JRST.Overwrite
     * @return the overwrite
     */
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

    /**
     * Execute mojo.
     */
    @Override
    public void execute() throws MojoExecutionException {
        try {
            actionGenerate();
        } catch (GenerationJRstException e) {
            getLog().error(
                    "Error during generation for :\n"
                            + GenerationJRstException.getFilesErrors());
            if (!ignoreErrors) {
                throw new MojoExecutionException("Error during generation");
            }
        }
        actionCopy();
    }

    protected void actionGenerate() {
        getLog().info("Generating reStructuredText files into xDoc");

        MirroredFileUpdater rstUpdater = new RstUpdater(directoryIn,
                directoryOut, defaultLocale);

        numberFilesGenerates = 0;
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(directoryIn);
        ds.setIncludes(new String[] { "**\\/*.rst" });
        ds.scan();
        String[] files = ds.getIncludedFiles();

        for (String file : files) {
            File in = new File(directoryIn, file);

            if (!force && rstUpdater.isFileUpToDate(in)) {
                if (verbose) {
                    getLog().info("Skip up-to-date reStructuredText file : " + in);
                }
                continue;
            }

            doAction(in, rstUpdater.getMirrorFile(in));
        }

        getLog().info("Generating " + numberFilesGenerates + " files to "
                        + directoryOut);
    }

    public boolean doAction(File in, File out) {

        getLog().info("Using " + in);

        try {
            JRST.generate(JRST.TYPE_XDOC, in, inputEncoding, out,
                    outputEncoding, getOverwrite());
            numberFilesGenerates++;
        } catch (Exception e) {
            getLog().error(e);
            if (!ignoreErrors) {
                throw new GenerationJRstException(in.getAbsolutePath());
            }
        }

        if (out != null && verbose) {
            //getLog().info("Using " + fileIn);
            getLog().info("Generating " + out);
        }
        return true;
    }

    protected void actionCopy() {
        getLog().info("Copy resources files");

        MirroredFileUpdater resourceUpdater = new RessourceUpdater(directoryIn,
                resourceDirectoryOut, defaultLocale);

        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(directoryIn);
        ds.setIncludes(includeResources == null
                        || includeResources.length == 0 ? DEFAULT_INCLUDE_RESOURCES
                        : includeResources);
        ds.scan();

        for (String file : ds.getIncludedFiles()) {
            File in = new File(directoryIn, file);
            if (!force && resourceUpdater.isFileUpToDate(in)) {
                if (verbose) {
                    getLog().info("Skip up-to-date resource file : " + in);
                }
                // do not copy the resource
                continue;
            }
            File out = resourceUpdater.getMirrorFile(in);
            if (verbose) {
                getLog().info("Copy resource " + in + " to " + out);
            }
            try {
                PluginHelper.copy(in, out);
            } catch (IOException e) {
                getLog().error(e);
            }
        }
    }

    /**
     * An updater for rst files.
     *
     * @author chemit
     */
    protected static class RstUpdater extends MirroredFileUpdater {

        protected String defaultLocale;

        protected RstUpdater(File sourceDirectory, File destinationDirectory,
                String defaultLocale) {
            //FIXME should use the two paramters to transform names
            super("", "", sourceDirectory, destinationDirectory);
            this.defaultLocale = defaultLocale;
        }

        @Override
        public File getMirrorFile(File f) {
            String file = f.getAbsolutePath().substring(prefixSourceDirecotory);
            file = file.replace(".rst", ".xml").replaceFirst(
                    "([/\\\\])rst([/\\\\])", "$1xdoc$2");
            if (defaultLocale != null && !"".equals(defaultLocale)) {
                file = file.replaceFirst("([/\\\\])" + defaultLocale
                        + "([/\\\\])", "$1");
            }
            return new File(destinationDirectory, file);
        }
    }

    /**
     * An updater for resources files.
     *
     * @author chemit
     */
    protected static class RessourceUpdater extends MirroredFileUpdater {

        protected String defaultLocale;

        protected RessourceUpdater(File sourceDirectory,
                File destinationDirectory, String defaultLocale) {
            //FIXME should use the two paramters to transform names
            super("", "", sourceDirectory, destinationDirectory);
            this.defaultLocale = defaultLocale;
        }

        @Override
        public File getMirrorFile(File f) {
            String file = f.getAbsolutePath().substring(prefixSourceDirecotory);
            file = file.replaceFirst("([/\\\\])rst([/\\\\])", "$1$2");
            if (defaultLocale != null && !"".equals(defaultLocale)) {
                file = file.replaceFirst("([/\\\\])" + defaultLocale
                        + "([/\\\\])", "$1");
            }
            return new File(destinationDirectory, file);
        }
    }
}
