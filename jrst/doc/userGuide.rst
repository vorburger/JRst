===============
JRst User Guide
===============
:Author: Bucas Jf

.. Note::

   Ce logiciel est libre. Réalisé par `Code Lutin`_.

.. _Code Lutin: http://www.codelutin.com

.. contents::


Présentation
============

JRst signifie Java Rst. C'est un parser en Java pour les documents 
en 'plain-text' de type RST_. Le but est de facilité la documentation
des développements.


Ce logiciel a été réalisé dans le cadre d'un stage au sein de la SSLL `Code Lutin`_.

.. image:: lutin.jpg
   :comment: lutinant !

.. _RST : http://docutils.sourceforge.net/rst.html

Installation et Exécution
=========================

- Dépendances

  Actuellement, seule la version Java 1.4.2 de Sun a été testée.

  L'outil Maven_ de apache est utilisé pour la compilation.

  La bibliothèque java JRegex_ est nécessaire. 

  .. _Maven : http://maven.apache.org
  .. _JRegex : http://jregex.sourceforge.net

- Compilation

  Pour lancer la compilation, tapez ``maven``. Le fichier ``jrst.jar`` est 
  construit.

- Exécution (exemple)::
    java -jar jrst.jar mydoc.rst --xdoc -o mydoc.xdoc

- Ligne de commande::

    usage :  jrst [--html|--xdoc|--xml|--rst] [-o outfile] document.rst

     -h, --help     this help
     --html         generate html document(default)
     --xdoc         generate xdoc document
     --xml          generate xml document
     --rst          generate with the selected format
     -o file        output file (--output)
     -v #           verbosity # in [0-3]
     document.rst   the document to parse


Fonctionnalités
===============

- Fonctionnalités implémentées dans jrst :
  
  + BulletList: **Ok**.
  + Directive: Note & Contents & Image
  + FieldList: RCS à fignoler
  + GridTable: **Ok** (sauf pour deux paragraphes dans une même case)
  + HyperLink: **Ok**
  + Title: **Ok**.
  + OptionList: **Ok**.
  + inlineMarkup:  emphase, litteral, URL, courriel

- Ne sont pas terminées :
  
  + Litteral: pose encore des problèmes dans les parties indentées
  + Comments ( à tester )
  + EnumerationList: pas de vérification de l'ordre des Items et de la valeur
  + BlockQuote ( provoque des erreurs ) 

- Non implémentées :
  
  + Annonymous Hyperlink
  + Synonymous Hyperlink
  + ...
  + Plein de détails à corriger
  
- Formats de sortie :
  
  + RST: fonctionne en partie, les paragraphes ne sont pas redécoupés...
  + HTML: correct.
  + XDOC: correct.
  + XML: pas encore mis à jour
  + DOCBOOK: à faire
  + PDF: à faire
  + PS: à faire
  + OpenOffice: à faire

