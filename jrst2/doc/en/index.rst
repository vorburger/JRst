==============================
reStructuredText parser : JRst
==============================

Presentation
------------

reStructuredText format is a document description format. Like other LaTex
or DocBook, it can be converted toward a multitude of formats. These formats
have usually invading syntax which, if it is necessary for very specific
documents, becomes useless when it is used to quickly creating a simple
document. RST has a so simple syntax that it  becomes almost invisible.

JRST is a Java ReStructuredText parser enabling to create a tree representation
document. It becomes easy to generate document representation towards differents
fomats.

How to use it
-------------

JRST parser takes a reStructuredText file and generates XML file. Which could be used to produce
various files formats with generation XSL files. The available output formats are HTML, xhtml,
rst, pdf, docbook, odt(Open-Office), rtf, or XML.

::

   JRST myfile.rst   

This command converts myfile.rst toward XML file, displayed to the standard output (console).
Several options are available :

-o file,--outFile=file          to write toward a file.
-t format,--outType format      to specify exit format, so using generation XSL file(s).
                                Several formats are available xhtml, docbook, xml, HTML, xdoc, rst, pdf, odt, rtf. 
-x xslFile,--xslFile xslFile    to specify generation XSL file at using.
--force                         to overwrite, if file exists, it will be replaced by the new one.
--help                          to display available options :

::

   Usage: [options] FILE
      [--force] : overwrite existing out file
      [--help] : display this help and exit
      [--outFile -o value] : Output file
      [--outType -t /xhtml|docbook|xml|html|xdoc|rst/] : Output type
      [--xslFile -x value] : XSL file list to apply, comma separated

NOTE : Only html, xhtml, DocBook, xdoc and pdf are available for the moment.

ex :

::

   JRST --force -t html -o myfile.html myfile.rst   

This command produces html file (myfile.html) from rst file (myfile.rst) 
even if myfile.html already exists.


Maven Plugin
------------

Maven plugin is available at the following links : http://jrst.labs.libre-entreprise.org/maven-jrst-plugin.
It enables the use of JRST from Maven.
