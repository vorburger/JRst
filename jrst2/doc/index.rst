====
JRst
====

JRst is Java ReStructuredText parser.

Internaly Rst document is a dom4j tree that represent python docutils xml.
You can generate docbook, xhtml, xdoc or your own files with your own XSL
files.

Usage
=====

To generate python docutils xml just write::

  JRST myfile.rst

To generate html::

  JRST -t html myfile.rst

To generate in specified file::

  JRST -t xdoc -o myfile.xml myfile.rst

