==============
Le grand titre
==============

:author: Benjamin POUSSIN <poussin@codelutin>
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

=====  =====  =======
  A      B    A and B
=====  =====  =======
False  False  False
True   False  False
False  True   False
True   True   True
=====  =====  =======

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

The |the big: biohazard| symbol in |date| must be used on containers used to dispose of medical waste.

.. |the big: biohazard| image:: biohazard.png 
.. |date| date::

