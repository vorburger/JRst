==================================
Parseur reStructuredText : le JRst
==================================

Documentation utilisateur
=========================

.. contents:: Sommaire


Présentation
------------

Le format reStructuredText est un format de description de documents. A l'image
d'autres LaTeX ou DocBook, il peut être décliné en une multitude de formats. Ces
formats souffrent habituellement d'une syntaxe envahissante qui, si elle est
nécessaire pour des documents très spécifiques, devient gênante quand il s'agit
de créer rapidement un document pas trop complexe. RST dispose quant à lui d'une
syntaxe tellement simple qu'elle en devient presque invisible.

JRST est un parseur RST en Java permettant de créer une représentation en arbre
d'un document. Il devient alors facile de générer une représentation du document
vers différents formats.


Usage
-----

Le parser JRST prend un fichier reStructuredText en entré et génère un fichier XML qui pourra ensuite servir à produire divers formats de fichiers grâce à des fichiers XSL de générations. Les formats de sortie disponibles sont le html, le xhtml, le rst, le pdf, le docbook, le odt (Open-Office), le rtf, ou encore le XML.

::

   JRST myfile.rst

Cette commande aura pour effet de convertir le fichier myfile.rst en XML qui sera affiché sur la sortie standard (console).
Plusieurs options sont disponibles :

-o file, --outFile=file          pour rediriger la sortie vers un fichier.
-t format, --outType format      pour préciser un format de sortie, donc utiliser un fichier XSL de génération. Plusieurs formats sont disponibles xhtml, docbook, xml, html, xdoc, rst, pdf, odt, rtf.
-x xslFile, --xslFile xslFile    sert à préciser le fichier xsl de génération à utiliser.
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

Cette commande produira un fichier html (myfile.html) à partir du fichier reStructuredText (myfile.rst) même si myfile.html existe déjà.


Plugin maven
------------

Un plugin maven est disponnible à l'adresse suivante 
http://jrst.labs.libre-entreprise.org/maven-jrst-plugin. Il permet l'utilisation 
depuis maven de JRst.
