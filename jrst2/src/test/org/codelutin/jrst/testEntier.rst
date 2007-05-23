==============
Le grand titre
==============

----------
 subtitle
----------

:authors: Benjamin POUSSIN <poussin@codelutin> ; Sylvain LETELLIER <letellier@codelutin>
:organization: Code Lutin
:address: 6 chemin des Orans
	44240 La Chapelle sur Erdre
:version: 2.1
:status: a finir
:revision: 51
:date: $date: 28/10/2006$
:Un champs: au milieu
  de null part

  avec meme un para
:copyright: © 2007. JRST - Code Lutin - GPL

------------------

.. sectnum::

.. contents:: sommaire
   :depth: 3



L'abstract de l'article

Le premier titre
================

Un paragraphe

Un autre paragraphe

Un sous titre
-------------

Un para de la sous section

Un autre sous titre
-------------------

Un autre para

Le *deuxime* titre
==================

*avec* un para sur **plusieurs
ligne** histoire de *voir* 

et un autre ``para aussi **sur
plusieurs lignes** mais sans gras``

LISTES
------

- une petite liste
- qui contient 
- plusieurs elements
  et meme sur plusieur ligne
  
  voir meme avec plusieurs paragraphe
  et des liens http://www.codelutin.com
  
  encore un petit
  
- et la suite de la liste
- pour finir

3. et meme

4. des enum

#. pour voir

I) de tout

II) sorte

(a) pour tout

(b) tester

(#) vraiment tout

FIELD
-----

:un peu: de field
  ca ne fait pas
  de mal


:field1: avec un 
  petit texte

  - et meme un
  - debut
  - de list
:field2: todo

DEFINITIONS
-----------

un autre mot
  une autre definition

le mot : la classe
  la definition

  avec un autre para
  sur plusieurs lignes::
  
    Et meme
      avec *des*
     ligne a 
     
         preserver
    tel quelles
    sont

le mot : la classe 1 : la classe 2
  la definition


SEPARATION
----------

avant

-----------------------------

apres  

Un tire
-------

Un mini titre
~~~~~~~~~~~~~

Un autre encore plus mini qui ne sera pas afficher dans le sommaire
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
  
Litteral
--------

::

    public int unread(String [] lines, boolean addNewLine) {
        int result = 0;
        for (String line : lines) {
            result += unread(line, addNewLine);
        }
        return result;
    }

un para 0

  un para pluie
  de 2

TABLE COMPLEXE
--------------

+------------------------+------------+----------+----------+
| Header row, column 1   | Header 2   | Header 3 | Header 4 |
| (header rows optional) |            |          |          |
+========================+============+==========+==========+
| body row 1, column 1   | column 2   | column 3 | column 4 |
+------------------------+------------+----------+----------+
| body row 2             | Cells may span columns.          |
+------------------------+------------+---------------------+
| body row 3             | Cells may  | - Table cells       |
+------------------------+ span rows. | - contain           |
| body row 4             |            | - body elements.    |
+------------------------+------------+---------------------+

+------------------------+------------+---------------------+
| body row 3             | Cells may  | - Table cells       |
+------------------------+ span rows. | - contain           |
| body row 4             |            | - body elements.    |
+------------------------+------------+---------------------+

TABLE SIMPLE
------------

================  ============================================================
Bullet list       * items begin with "-", "+", or "*"
Enumerated list   1. items use any variation of "1.", "A)", and "(i)"
                  #. also auto-enumerated
Definition list   Term is flush-left : optional classifier
                      Definition is indented, no blank line between
Field list        :field name: field body
Option list       -o  at least 2 spaces between option & description
================  ============================================================


=====  =====  ======
   Inputs     Output
------------  ------
  A      B    AorB
------------  ------
  A      B    A or B
=====  =====  ======
False  False  Second column of row 1.
True   False  Second column of row 2.
True   2      - Second column of row 3.
              - Second item in bullet
                list (row 3, column 2).
=====  =====  ======

IMAGE
-----

The |the big: biohazard| symbol must be used on containers used to dispose of medical waste.

.. |the big: biohazard| image:: biohazard.png 

LIEN INLINE
-----------

Une *petit* phare `pour avoir <http://www.free.fr>`_ un lien inline

FIELD
-----

:field1: def1
:field2: def2

:field1: autre def1
:field2: autre def2

ADMONITIONS
-----------

.. admonition:: And, by the way...

   You can make up your own admonition too.
   
   :field1: autre def1
   :field2: autre def2

.. Attention:: All your base are belong to us.
   You can make

.. WARNING::The "raw" role is a stop-gap measure allowing the author to bypass::
   reStructuredText's markup.  It is a "power-user" feature that
   should not be overused or abused.  The use of "raw" ties documents
   to specific output formats and makes them less portable.

   If you often need to use "raw"-derived interpreted text roles or
   the "raw" directive, that is a sign either of overuse/abuse or that
   functionality may be missing from reStructuredText.  Please
   describe your situation in a message to the Docutils-users mailing
   list.


.. admonition:: The "raw" role is a stop-gap measure allowing the author to bypass::
   reStructuredText's markup.  It is a "power-user" feature that
   should not be overused or abused.  The use of "raw" ties documents
   to specific output formats and makes them less portable.

   If you often need to use "raw"-derived interpreted text roles or
   the "raw" directive, that is a sign either of overuse/abuse or that
   functionality may be missing from reStructuredText.  Please
   describe your situation in a message to the Docutils-users mailing
   list.



.. Attention:: Derrière toi c'est horrible !


.. admonition:: Ceci est un avertissement.

   Je vous demande de vous arrêtez.

BLOCK QUOTE
-----------

As a great paleontologist once said,

    This theory, that is mine, is mine.

    -- Anne Elk (Miss)
    
DOCTEST BLOCK
-------------

>>> print 'this is a Doctest block'
this is a Doctest block

LINE BLOC
---------
 
| A one, two, a one two three four
|
| Half a bee, philosophically,
|     must, *ipso facto*, half not be.
| But half the bee has got to be,
|     *vis a vis* its entity.  D'you see?
|
| But can a bee be said to be
|     or not to be an entire bee,
|         when half the bee is not a bee,
|             due to some ancient injury?
|
| Singing...

SIDEBAR
-------

.. sidebar:: Title
   :subtitle: If Desired

   Body.

TOPIC
-----

.. topic:: Title

   Body.

.. header:: Ceci est une page de test


.. footer:: Cree par **LETELLIER Sylvain**


OPTION
------

-a            command-line option "a"
		 -1 file, --one=file, --two file
         		      Multiple options with arguments.

FOOTNOTES
---------

[#]_ is a reference to footnote 1, and [#]_ is a reference to
footnote 2.

.. [#] This is footnote 1.
.. [#] This is footnote 2.
.. [#] This is footnote 3.

[#]_ is a reference to footnote 3.

HYPERLINK
---------

it's an anonymous-hyperlink__ and this one is an simple hyperlink_

__ anonymous-hyperlink

.. _hyperlink: www.google.fr

COMMENT
-------

..
  Ceci est un commentaire

INLINE
------

``literal``, *emphasis*, **STRONG**, http://www.reference.com, letellier@codelutin.com


la derniere ligne.
   



    
