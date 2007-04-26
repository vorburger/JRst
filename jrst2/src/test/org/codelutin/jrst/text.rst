==============
Le grand titre
==============

:authors: Benjamin POUSSIN <poussin@codelutin> ; Sylvain LETELLIER <letellier@codelutin>
:address:
  6 chemin des Orans
  44240 La Chapelle sur Erdre
:date: $date: 28/10/2006$

:Un champs: au milieu
  de null part

  avec meme un para
:date: $date: 28/10/2006$

------------------

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

:field1: avec un 
  petit texte

  - et meme un
  - debut
  - de list
:field2: todo

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

un autre mot
  une autre definition
  
-----------------------------
  
le mot : la classe 1 : la classe 2
  la definition
  
:un peu: de field
  ca ne fait pas
  de mal
  
  
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
  A      B    A or B
------------  ------
  A      B    A or B
=====  =====  ======
False  False  Second column of row 1.
True   False  Second column of row 2.
True   2      - Second column of row 3.
              - Second item in bullet
                list (row 3, column 2).
=====  =====  ======

The |the big: biohazard| symbol in |date| must be used on containers used to dispose of medical waste.

.. |the big: biohazard| image:: biohazard.png 
.. |date| date::


Une *petit* phare `pour avoir <http://www.free.fr>`_ un lien inline

:field1: def1
:field2: def2

:field1: autre def1
:field2: autre def2

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
   describe your situation in a message to the Docutils-users_ mailing
   list.

   .. |the big: biohazard| image:: biohazard.png 

.. admonition:: The "raw" role is a stop-gap measure allowing the author to bypass::
   reStructuredText's markup.  It is a "power-user" feature that
   should not be overused or abused.  The use of "raw" ties documents
   to specific output formats and makes them less portable.

   If you often need to use "raw"-derived interpreted text roles or
   the "raw" directive, that is a sign either of overuse/abuse or that
   functionality may be missing from reStructuredText.  Please
   describe your situation in a message to the Docutils-users_ mailing
   list.

   .. |the big: biohazard| image:: biohazard.png 

la derniere ligne.

.. Attention:: Derrière toi c'est horrible !

.. admonition:: And, by the way...
.. admonition:: Ceci est un avertissement.

   Je vous demande de vous arrêtez.

As a great paleontologist once said,

    This theory, that is mine, is mine.

    -- Anne Elk (Miss)

>>> print 'this is a Doctest block'
this is a Doctest block
 
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

.. sidebar:: Title
   :subtitle: If Desired

   Body.
   
.. topic:: Title

   Body.
   
   
-a            command-line option "a"
-1 file, --one=file, --two file
              Multiple options with arguments.
              
   .. header:: This space for rent. aaaa **aaaa**

-o  at least 2 spaces between option & description