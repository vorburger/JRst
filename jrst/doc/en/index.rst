==============================
reStructuredText parser : JRst
==============================

About
------------

reStructuredText is a document markup system. Like LaTex
or DocBook, it can be converted into a multitude of output formats. 
Most markup systems have a very complex syntax which, though necessary 
to maintain a high level of control over specific document layout tasks,
is less desirable for the creation of simple documents with standard layouts.
RST, on the other hand, has a such a simple syntax that it becomes almost 
invisible.

JRST is a Java ReStructuredText parser which generates an XML tree from 
RST documents. This tree, in turn, may be transformed into a variety of 
abitrary output formats through the use of standard tools.

How to use it
-------------

The JRST parser takes a reStructuredText file and generates an XML file. 
This XML file, taken together with an appropriate XSL file, may be then
be transformed into a variety of output formats. Currently available 
output formats include HTML, xhtml, rst, pdf, docbook, odt(Open-Office), 
rtf, and XML.

::

   JRST myfile.rst   

This command converts myfile.rst into an XML file which will be sent to
the standard output (console).
Several options are available :

-o file,--outFile=file          to write to a file.
-t format,--outType format      to specify output format
                                Several formats are available including xhtml, docbook, xml, HTML, xdoc, rst, pdf, odt, rtf. 
-x xslFile,--xslFile xslFile    to specify the XSL file to use 
--force                         to overwrite an existing output file
--help                          to display available options 

::

   Usage: [options] FILE
      [--force] : overwrite existing output file
      [--help] : display this help and exit
      [--outFile -o value] : output file
      [--outType -t /xhtml|docbook|xml|html|xdoc|rst/] : output type
      [--xslFile -x value] : comma separated list of XSL files to apply

NOTE : Only html, xhtml, DocBook, xdoc and pdf are available at the moment.

ex :

::

   JRST --force -t html -o myfile.html myfile.rst   

This command produces an html file (myfile.html) from the rst file (myfile.rst) 
even if myfile.html already exists.


Maven Plugin
------------

A Maven plugin is available at the following location: 
http://jrst.labs.libre-entreprise.org/maven-jrst-plugin.
It enables the use of JRST from Maven.

