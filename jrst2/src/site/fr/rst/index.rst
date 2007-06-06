===================================
Parseur reStructuredText_ : le JRst
===================================

Documentation utilisateur
=========================

.. contents:: Sommaire


Présentation
------------

Le format reStructuredText_ est un format de description de documents. A l'image
d'autres LaTeX_ ou DocBook_, il peut être décliné en une multitude de formats. Ces
formats souffrent habituellement d'une syntaxe envahissante qui, si elle est
nécessaire pour des documents très spécifiques, devient gênante quand il s'agit
de créer rapidement un document pas trop complexe. RST_ dispose quant à lui d'une
syntaxe tellement simple qu'elle en devient presque invisible.

JRST est un parseur RST_ en Java permettant de créer une représentation en arbre
d'un document. Il devient alors facile de générer une représentation du document
vers différents formats.


Usage
-----

Le parser JRST prend un fichier reStructuredText_ en entré et génère un fichier XML
qui pourra ensuite servir à produire divers formats de fichiers grâce à des fichiers
XSL de générations. Les formats de sortie disponibles sont le html, le xhtml, le rst,
le pdf, le docbook_, le odt (Open-Office), le rtf, ou encore le XML [1]_.

::

   JRST myfile.rst

Cette commande aura pour effet de convertir le fichier myfile.rst en XML qui sera affiché sur la sortie standard (console).
Plusieurs options sont disponibles :

-o file,--outFile=file          pour rediriger la sortie vers un fichier.
-t format,--outType format      pour préciser un format de sortie, donc utiliser un ou des fichiers XSL_ de génération. Plusieurs formats sont disponibles xhtml, docbook, xml, html, xdoc, rst, pdf, odt, rtf.
-x xslFile,--xslFile xslFile    sert à préciser le fichier xsl de génération à utiliser.
--force                          forcer l'écriture d'un fichier, si le fichier de sortie existe, il sera remplacé.
--help                           pour afficher les options disponibles :


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

Cette commande produira un fichier html (myfile.html) à partir du fichier reStructuredText_ (myfile.rst)
même si myfile.html existe déjà.


Plugin Maven_
-------------

Un plugin Maven_ est disponible à l'adresse suivante 
http://jrst.labs.libre-entreprise.org/maven-jrst-plugin. Il permet l'utilisation 
depuis Maven_ de JRst.

.. [1] Seul les formats html, xhtml, DocBook_, xdoc et pdf sont disponible pour le moment.

.. _reStructuredText: presentationRST.html
.. _Maven: http://maven.apache.org/
.. _XSL: http://jrst.labs.libre-entreprise.org/fr/devel/presentationXSL.rst
.. _DocBook: http://www.docbook.org/
.. _LaTex: http://www.latex-project.org/