=========================
Documentation développeur
=========================

.. contents:: Sommaire

Le diagramme de Class
=====================

|classDiagramme|

La Class **AdvancedReader** a pour fonction de faciliter la lecture du fichier RST grâce à différentes méthodes :
  - String readLine() : renvoie une ligne
  - String[] readLines(int nombresLigne) : renvoie un certain nombre de lignes
  - Stringn[] readWhile(Pattern p) : renvoie les lignes tant qu'elles correspondent au pattern

...

La Class **JRSTLexer** utilise **AdvancedReader** pour construire un fichier XML, il va parcourir l'ensemble du document pour isoler les types de données, leurs paramètres et leurs contenus, donc rassembler toutes les informations utiles à la mise en forme du XML final. Il va commencer par l'entête du document (peekHeader(), peekDocInfo()) pour ensuite s'intéresser au corps (peekBody()).

La Class **JRSTReader** utilise **JRSTLexer**, il interprète le XML qui lui est renvoyé pour construire le XML final. Celui-ci est conforme à la DTD définie par DocUtils_. Cette Class a parfois besoin de s'appeler elle même lorsque une partie du document doit être interprétée indépendamment du reste. Par exemple, s'il y a une liste dans une case d'un tableau, l'on extrait les informations de la case et on les interprètes, le contenu d'une admoniton (une note) doit lui aussi être considéré comme un document indépendant. Lorsque la génération est terminée, la Class compose le sommaire (composeContent()) puis s'occupe de toutes les spécificités « inline » (inline()), comme par exemple les mots en italique ou gras, les références, les footnotes... Tout ce qui peut apparaître à l'intérieur d'une ligne.

La Class **reStructuredText** référence toutes les variables nécessaires à la génération du XML final.

La Class **JRST** contient la méthode main(), elle gère les options, la lecture et l'écriture des fichiers. Elle lit le document, le parse grâce à la class **JRSTReader** puis applique le XSL désiré (si besoin) grâce à la class **JRSTGenerator**.

Exemple d'utilisation
=====================

L'on souhaite convertir le document rst (text.rst) suivant en html (text.html) :

::

   =====
   Titre
   =====

   :Author: Letellier Sylvain

   .. Attention:: texte à être réinterprété comme un fichier rst indépendant

On utilise donc la commande suivante :

::

   JRST -t html -o text.html text.rst

Ce diagramme de séquence décrit le fonctionnement du parseur tout au long de la génération :

|sequanceDiagramme|

La Classe JRSTGenerator, grace au fichier XSL rst2xhtml.xsl, renvoie le fichier html suivant::

   <?xml version="1.0" encoding="UTF-8"?>
   <html xmlns="http://www.w3.org/TR/xhtml1/strict">
     <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
       <meta name="generator" content="JRST http://jrst.labs.libre-entreprise.org/"/>
       <title>Titre</title>
     </head>
     <body>
       <h1>Titre</h1>
       <table class="docinfo" frame="void" rules="none">
         <col class="docinfo-name"/>
         <col class="docinfo-content"/>
         <tbody valign="top">
           <tr>
             <th class="docpatterninfo-name">author :</th>
             <td class="docinfo-content">Letellier Sylvain</td>
           </tr>
         </tbody>
       </table>
       <div class="attention">
         <p class="title">attention :</p>
         <p class="body">
           <p>texte à être réinterprété comme un fichier rst indépendant</p>
         </p>
       </div>
     </body>
   </html>

Qui affiche la page (un CSS à été ajouté pour la mise en forme) :

.. topic:: Titre

   :Author: Letellier Sylvain
   .. Attention:: texte à être réinterprété comme un fichier rst indépendant


.. _DocUtils: http://docutils.sourceforge.net/docs/ref/doctree.html
.. |classDiagramme| image:: DiagrammeClass.png 
.. |sequanceDiagramme| image:: DiagrammeSequance.png