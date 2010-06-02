/*
 * #%L
 * JRst :: Doxia module
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2009 - 2010 CodeLutin
 * %%
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
 * #L%
 */

package org.nuiton.jrst;

import org.apache.maven.doxia.module.site.AbstractSiteModule;

/**
 * JrstSiteModule.
 *
 * @author <a href="mailto:chatellier@codelutin.com">Eric Chatellier</a>
 * @version $Id$
 * @since 0.9.0
 * @plexus.component role="org.apache.maven.doxia.module.site.SiteModule" role-hint="jrst"
 */
public class JrstSiteModule extends AbstractSiteModule {

    @Override
    public String getSourceDirectory() {
        return "rst";
    }

    @Override
    public String getExtension() {
        return "rst";
    }

    @Override
    public String getParserId() {
        return "jrst";
    }
}
