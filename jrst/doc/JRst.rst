====
Jrst
====

But
===

JRst est un parser RestructuredText en Java.

Il permet d'ajouter des extenstions très facilement en construisant deux
classe. Une pour parser l'élément, l'autre qui représente l'élément parsé.

Principe
========

Le parseur fonctionne en deux phase, la première phase construit la liste des
éléments présents dans le document, sans tenir compte de la position des
éléments, c'est à dire qu'on ne s'attache pas à la structure du document. Un
titre est un titre et non pas le titre d'une section ou le titre du document.
Cette phase peut-etre assimilé au lexer.

Dans la seconde les éléments sont structurés pour donner le document réelle.
Le premier titre devient le titre du document, les titres suivant selon la
façon de les souligner devient des titres de section, sous-section, ...

Le lexer
--------

Un élément est constitué de deux classe, une classe qui représente l'élément,
et une classe qui permet de construire cette élément. Le constructeur (ou
factory) contient deux méthodes principales **accept** et **parse**. La
première permet de savoir si les caractères pourrait convenir à cette élément
ou non. Si les caractères conviennent la factory est alors utilisé pour lire
cette sous partie du document.

Soit la factory indique que l'élément est terminé et donc que l'élément
n'accepte plus de caractère, soit le père de cette factory lui demande
l'élément dans l'état ou il est. Par exemple lorsque l'on parse une liste,
l'élément liste contient d'autre élément, mais c'est l'élément liste qui force
le dernier des fils d'un item à s'arrêter. Car c'est la liste qui détecte le
nouveau symbole d'item et non pas le fils.

Implantation
============

Plusieurs éléments concret:

- title
- subtitle
- para
- directive
- bullet list (le text est un block)
- ordered list (le text est un block)
- field list (le text est un block)
- preformatting text
- table (une cellule est un block)
- definition (la definition est un block)

et des éléments de regroupement:

- document
- section
- block

document
--------

- commence par un titre optionnel
- des fields lists permettant de définir le document (auteur, date, ...)
- un block optionnel
- des sections optionnelles

section
-------

- commence par un titre
- un sous titre optionnel
- un block

block
-----

une suite d'élément concret

Parsing
=======

Lorsqu'un élément est parsé il peut être dans plusieurs états

IN_PROGRESS
  lorsqu'il faut d'autre caractère pour que l'élément soit valid

FINISHED
  lorsque l'élément est terminé et n'accepte plus de caractère

FAILED
  lorsque l'élément a reçu un caractère qui le rend invalide

Les éléments contenant d'autres éléments peuvent manger des caractères au lieu
de les envoyer au sous élément, ce cas arrive avec les espaces devant un
élément liste qui tient sur plusieurs lignes, le block enfant de l'élément
liste ne recevra pas ces espaces.

Les éléments contenant d'autres éléments peuvent bufferiser quelques caractères
avant de prendre la décision de les envoyer ou non à ces sous élément. Ce cas
arrive dans un block pour savoir si un élément para continu ou si un élément
Preformatting commence lorsque le caractère reçu est :


