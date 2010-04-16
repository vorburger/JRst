.. -
.. * #%L
.. * JRst :: Documentation
.. * 
.. * $Id$
.. * $HeadURL$
.. * %%
.. * Copyright (C) 2009 - 2010 CodeLutin
.. * %%
.. * This program is free software: you can redistribute it and/or modify
.. * it under the terms of the GNU Lesser General Public License as 
.. * published by the Free Software Foundation, either version 3 of the 
.. * License, or (at your option) any later version.
.. * 
.. * This program is distributed in the hope that it will be useful,
.. * but WITHOUT ANY WARRANTY; without even the implied warranty of
.. * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
.. * GNU General Lesser Public License for more details.
.. * 
.. * You should have received a copy of the GNU General Lesser Public 
.. * License along with this program.  If not, see
.. * <http://www.gnu.org/licenses/lgpl-3.0.html>.
.. * #L%
.. -
===============================
reStructuredText_ parser : JRst
===============================

Documentation utilisateur
=========================

.. contents::


Presentation
------------

reStructuredText_ format is a document description format. Like other LaTex_
or DocBook_, it can be converted toward a multitude of formats. These formats
have usually invading syntax which, if it is necessary for very specific
documents, becomes useless   when it is used to quickly creating a simple
document. RST has a so simple syntax that it  becomes almost invisible.

JRST is a Java ReStructuredText_ parser enabling to create a tree representation
document. It becomes easy to generate document representation towards differents
fomats.

How to use it
-------------

JRST parser takes a reStructuredText_ file and generates XML file. Which could be used to produce
various files formats with generation XSL_ files. The available output formats are HTML, xhtml,
rst, pdf, docbook, odt(Open-Office), rtf, or XML [1]_.

::

   JRST myfile.rst   

This command converts myfile.rst toward XML file, displayed to the standard output (console).
Several options are available :

-o file,--outFile=file           to write toward a file.
-t format,--outType format       to specify exit format, so using generation XSL_ file(s). Several formats are available xhtml, docbook, xml, HTML, xdoc, rst, pdf, odt, rtf. 
-x xslFile,--xslFile xslFile     to specify generation XSL_ file at using.
--force                          to overwrite, if file exists, it will be replaced by the new one.
--help                           to display available options :

::

   Usage: [options] FILE
      [--force] : overwrite existing out file
      [--help] : display this help and exit
      [--outFile -o value] : Output file
      [--outType -t /xhtml|docbook|xml|html|xdoc|rst/] : Output type
      [--xslFile -x value] : XSL file list to apply, comma separated

ex :

::

   JRST --force -t html -o myfile.html myfile.rst   

This command produces html file (myfile.html) from rst file (myfile.rst) 
even if myfile.html already exists.


Maven_ Plugin
-------------

Maven_ plugin is available at the following links : http://jrst.labs.libre-entreprise.org/maven-jrst-plugin.
It enables the use of JRST from Maven_.

.. [1] Only html, xhtml, DocBook_, xdoc and pdf are available for the moment.

.. _reStructuredText: RSTpresentation.html
.. _Maven: http://maven.apache.org/
.. _XSL: http://jrst.labs.libre-entreprise.org/en/devel/XSLpresentation.rst
.. _DocBook: http://www.docbook.org/
.. _LaTex: http://www.latex-project.org/
