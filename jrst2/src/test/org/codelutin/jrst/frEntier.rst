==================================
Parseur reStructuredText : le JRst
==================================

:author: Sylvain LETELLIER <letellier@codelutin>
:organization: Code Lutin
:date: $date: 01/06/2007$
:copyright: © 2007. JRST - Code Lutin - GPL

----------------------

.. sectnum::

.. contents:: Sommaire

Documentation utilisateur
=========================


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

Le parser JRST prend un fichier reStructuredText en entré et génère un fichier XML
qui pourra ensuite servir à produire divers formats de fichiers grâce à des fichiers
XSL de générations. Les formats de sortie disponibles sont le html, le xhtml, le rst,
le pdf, le docbook, le odt (Open-Office), le rtf, ou encore le XML [1]_.

::

   JRST myfile.rst

Cette commande aura pour effet de convertir le fichier myfile.rst en XML qui sera affiché sur la sortie standard (console).
Plusieurs options sont disponibles :

-o file,--outFile=file           pour rediriger la sortie vers un fichier.
-t format,--outType format       pour préciser un format de sortie, donc utiliser un ou des fichiers XSL de génération. Plusieurs formats sont disponibles xhtml, docbook, xml, html, xdoc, rst, pdf, odt, rtf.
-x xslFile,--xslFile xslFile     sert à préciser le fichier xsl de génération à utiliser.
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

Cette commande produira un fichier html (myfile.html) à partir du fichier reStructuredText (myfile.rst)
même si myfile.html existe déjà.


Plugin Maven
------------

Un plugin Maven est disponible à l'adresse suivante 
http://jrst.labs.libre-entreprise.org/maven-jrst-plugin. Il permet l'utilisation 
depuis Maven de JRst.

.. [1] Seul les formats html, xhtml, DocBook, xdoc et pdf sont disponible pour le moment.

Maven : http://maven.apache.org/
XSL : http://jrst.labs.libre-entreprise.org/fr/devel/presentationXSL.rst
DocBook : http://www.docbook.org/
LaTex : http://www.latex-project.org/

Introduction à ReStructuredText
===============================

Document adaptée du document de Richard Jones : http://docutils.sourceforge.net/sandbox/wilk/french/quickstart-fr.html


Ce texte contient des liens de la forme.  Ils sont
relatifs au manuel de référence utilisateur Quick reStructuredText.
S'ils ne fonctionnent pas, référez vous au document master quick
reference.

Quick reStructuredText : http://docutils.sourceforge.net/docs/rst/quickref.html
master quick reference : http://docutils.sourceforge.net/docs/rst/quickref.html


Structure
---------

Pour commencer, il me semble que "Structured Text" n'est pas tout à fait la
bonne appellation. Nous devrions plutôt le nommer "Relaxed Text" qui contient
quelques schémas logiques. Ces schémas sont interprétés par un convertisseur
HTML pour produire "Very Structured Text" (un texte très structuré) qui pourra
être utilisé par un navigateur web.

Le schéma le plus simple est le **paragraphe**.
C'est un bloc de texte séparé par des lignes vides (une seule suffit).
Les paragraphes doivent avoir le même décalage -- c'est à dire des espaces
à gauche. Ces paragraphes produiront un texte décalé. Par exemple::

  Ceci est un paragraphe.
  Très court.

     Le texte de ce paragraphe sera décalé,
     généralement utilisé pour des citations.

  En voilà un autre

Le résultat donne :

  Ceci est un paragraphe.
  Très court.

     Le texte de ce paragraphe sera décalé,
     généralement utilisé pour des citations.

  En voilà un autre
  

Styles de texte
---------------


Dans les paragraphes et le corps du texte, nous pouvons utiliser
des marqueurs pour *italique* avec "`` *italique* ``" ou **gras**
avec "`` **gras** ``".

Notez qu'aucun traitement supplémentaire n'est apporté entre deux
doubles apostrophes inversées -- les astérisques, comme dans "`` * ``",
sont donc conservées en l'état.

Si vous souhaitez utiliser un de ces caractères "spéciaux" dans
le texte, il n'y a généralement pas de problème -- reStructuredText
est assez malin.
Par exemple, cet astérisque ``*`` est traité correctement. Si vous
souhaitez par contre ``*``entourer un texte par des astérisques``*`` 
**sans** qu'il soit en italique, il est nécessaire d'indiquer que
l'astérisque ne doit pas être interprété. Pour cela il suffit de placer
une barre oblique inversée juste avant lui, comme ça "``\*``", ou
en l'entourant de doubles apostrophes inversées (litteral), comme cela ::

  ``\*``

(``\*`` n'est pas implanté dans le JRST seul les `` fonctionnent)



Listes
------

Il y a trois types de listes: **numérotées**, **avec puces** et
de **définitions**. Dans chaque cas, nous pouvons avoir autant
de paragraphes, sous-listes, etc. que l'on souhaite, tant que
le décalage à gauche est aligné sur la première ligne.

Les listes doivent toujours démarrer un nouveau paragraphe
-- c'est à dire qu'il doit y avoir un saut de ligne juste avant.

Listes **numérotées** (par des nombres, lettres, chiffres romains;)


En démarrant une ligne avec un numéro ou une lettre suivie d'un
point ".", une parenthèse droite ")" ou entouré par des parenthèses
-- comme vous préférez. Toutes ces formes sont reconnues::

    1. nombres

    A. Lettres en majuscule
       qui continue sur plusieurs ligne

       avec deux paragraphes et tout !

    a. lettres minuscules

       3. avec une sous-liste qui démarre à un nombre différent
       4. faites attention à garder une séquence de nombre correcte !

    I. majuscules en chiffres romains

    i. minuscules en chiffres romains

    (1) des nombres à nouveau

    1) et encore

Le résultat (note : Tous les styles de listes ne sont pas toujours
supportés par tous les navigateurs, vous ne verrez donc pas forcément
les effets complets) :

1. nombres

A. Lettres en majuscule
   qui continue sur plusieurs ligne

   avec deux paragraphes et tout !

a. lettres minuscules

   3. avec une sous-liste qui démarre à un nombre différent
   4. faites attention à garder une séquence de nombre correcte !

I. majuscules en chiffres romains

i. minuscules en chiffres romains

(1) des nombres à nouveau

1) et encore

Listes **à puces** 


De la même manière que pour les listes numérotées, il faut démarrer
la première ligne avec une puce -- soit "-", "+" ou "*"::

    * une puce "*"

      - une sous-liste avec "-"

         + à nouveau une sous-liste

      - une autre option

Le résultat:

    * une puce "*"

      - une sous-liste avec "-"

         + à nouveau une sous-liste

      - une autre option

Les listes de **définitions** 


Comme les deux autres, les listes de définitions consistent en un
terme et la définition de ce terme. Le format est le suivant::

    Quoi
      Les listes de définitions associent un terme avec une définition.

    *Comment*
      Le terme est une phrase d'une ligne, et la définition est d'un
      ou plusieurs paragraphes ou éléments, décalés par rapport au terme.
      Les lignes vides ne sont pas autorisées entre le terme et la définition.

Le résultat:

Quoi
  Les listes de définitions associent un terme avec une définition.

*Comment*
  Le terme est une phrase d'une ligne, et la définition est d'un
  ou plusieurs paragraphes ou éléments, décalés par rapport au terme.
  Les lignes vides ne sont pas autorisées entre le terme et la définition.

Préformatage
------------

Pour inclure un texte préformaté sans traitement
il suffit de terminer le paragraphe par "``::``". Le texte préformaté est
terminé lorsqu'une ligne retombe au niveau du décalage précédent. Par exemple::

  Un exemple::

      Espaces, nouvelles lignes, lignes vides, et toutes sortes de marqueurs
         (comme *ceci* ou \cela) sont préservés dans les bloc préformatés.

  Fin de l'exemple

Le résultat:

  Un exemple::

      Espaces, nouvelles lignes, lignes vides, et toutes sortes de marqueurs
         (comme *ceci* ou \cela) sont préservés dans les bloc préformatés.

  Fin de l'exemple

Notez que si le paragraphe contient seulement "``::``", il est ignoré.

  ::

     Ceci est un texte préformaté,
     le paragraphe "::" est ignoré.

Sections
--------

Pour diviser un texte en plusieurs sections, nous utilisons des
**en-têtes de section**. C'est à dire une seule ligne de texte (d'un
ou plusieurs mots) avec un ornement : juste en dessous et éventuellement
dessus aussi, avec des tirets "``-----``", égal "``=====``", tildes
"``~~~~~``" ou n'importe quel de ces caractères ``= - ` : ' " ~ ^ _ * + # < >``
qui vous semble convenir. Un ornement simplement en dessous n'a pas la
même signification qu'un ornement dessus-dessous avec le même caractère.
Les ornements doivent avoir au moins la taille du texte. Soyez cohérent,
les ornements identiques sont censés être du même niveau::

  Chapitre 1
  ==========

  Section 1.1
  -----------

  Sous-section 1.1.1
  ~~~~~~~~~~~~~~~~~~

  Section 1.2
  -----------

  Chapitre 2
  ==========

Le résultat de cette structure, sous la forme pseudo-XML::

    <section>
        <title>
            Chapitre 1
        <section>
            <title>
                Section 1.1
            <section>
                <title>
                    Sous-section 1.1.1
        <section>
            <title>
                Section 1.2
    <section>
        <title>
            Chapitre 2
  
(Pseudo-XML utilise une indentation et n'as pas de balises finale. Il
n'est pas possible de montrer le résultat, comme dans les autres exemples,
du fait que les sections ne peuvent être utilisées à l'intérieur d'un
paragraphe décalé. Pour un exemple concret, comparez la structure de
ce document avec le résultat.)

Notez que les en-têtes de section sont utilisable comme cible de liens,
simplement en utilisant leur nom. Pour créer un lien sur la section Listes,
j'écris "``Listes_``". Si le titre comporte des espaces, il est nécessaire
d'utiliser les doubles apostrophes inversées "```Styles de texte```".

Pour indiquer le titre du document, utilisez un style d'ornement unique
en début de document. Pour indiquer un sous-titre de document, utilisez
un autre ornement unique juste après le titre.
Par exemple::

    =================
    Titre du document
    =================
    ----------
    Sous-titre
    ----------

    Titre de la section
    ===================

    ...

Notez que "Titre du document" et "Titre de la section" utilisent le signe
égal, mais sont différents et sans relation. Le texte et l'ornement peuvent
être de la même taille pour des questions d'esthétisme.


Images
------

Pour inclure une image dans votre document, vous devez utiliser la directive
``image``.
Par exemple::

    .. image:: /home/letellier/PROJET/jrst2/src/site/fr/rst/user/images/biohazard.png

Le résultat:

.. image:: /home/letellier/PROJET/jrst2/src/site/fr/rst/user/images/biohazard.png

La partie ``images/biohazard.png`` indique le chemin d'accès au fichier
de l'image qui doit apparaître. Il n'y a pas de restriction sur l'image
(format, taille etc). Si l'image doit apparaître en HTML et que vous
souhaitez lui ajouter des informations::

  .. image:: /home/letellier/PROJET/jrst2/src/site/fr/rst/user/image/biohazard.png
     :height: 100
     :width: 200
     :scale: 50
     :alt: texte alternatif

Consultez la documentation complète de la directive image pour plus d'informations.

Documentation images : http://docutils.sourceforge.net/spec/rst/directives.html#images


Et ensuite ?
------------

Cette introduction montre les possibilités les plus courantes de reStructuredText,
mais il y en a bien d'autres à explorer. Le manuel de référence utilisateur
Quick reStructuredText est recommandé pour aller plus loin. Pour les détails complets
consultez reStructuredText Markup Specification [#]_.


.. [#] Si ce lien relatif ne fonctionne pas, consultez le document principal:
   http://docutils.sourceforge.net/spec/rst/reStructuredText.html.

reStructuredText Markup Specification : http://docutils.sourceforge.net/spec/rst/reStructuredText.html
Docutils-Users mailing list : http://lists.sourceforge.net/lists/listinfo/docutils-users
Docutils project web site : http://docutils.sourceforge.net/

Fonctionnalités proposées
=========================

La DTD de Docutils : http://docutils.sourceforge.net/docs/ref/doctree.html


Fonctionnalités implantées
--------------------------

Element racine
~~~~~~~~~~~~~~

-  document

Elements titre
~~~~~~~~~~~~~~

-  subtitle
-  title

Elements bibliographiques
~~~~~~~~~~~~~~~~~~~~~~~~~

-  docinfo
-  author
-  authors
-  organization
-  address
-  contact
-  version
-  revision
-  status
-  date
-  copyright

Elements de décoration
~~~~~~~~~~~~~~~~~~~~~~

-  decoration
-  footer
-  header

Elements structurels
~~~~~~~~~~~~~~~~~~~~

-  section
-  topic
-  sidebar
-  transition

Elements du corps
~~~~~~~~~~~~~~~~~

-  admonition
-  attention
-  block_quote
-  bullet_list
-  caution
-  classifier
-  danger
-  definition
-  definition_list
-  definition_list_item
-  description
-  doctest_block
-  enumerated_list
-  error
-  field
-  field_body
-  field_list
-  field_name
-  footnote
-  hint
-  image
-  important
-  line
-  line_block
-  list_item
-  literal_block
-  note
-  option
-  option_argument
-  option_group
-  option_list
-  option_list_item
-  option_string
-  paragraph
-  term
-  tip
-  warning

Elements des tableaux
~~~~~~~~~~~~~~~~~~~~~

-  table
-  tbody
-  entry
-  row
-  colspec
-  thead
-  tgroup

Elements de la ligne
~~~~~~~~~~~~~~~~~~~~

-  emphasis
-  strong
-  literal
-  reference
-  footnote_reference

Fonctionnalités non-implantées
------------------------------

-  abbreviation
-  acronym
-  attribution
-  caption
-  citation
-  citation_reference
-  comment
-  compound
-  container
-  figure
-  generated
-  inline
-  label
-  legend
-  pending
-  problematic
-  raw
-  rubric
-  subscript
-  substitution_definition
-  substitution_reference
-  superscript
-  system_message
-  target
-  title_reference


Documentation développeur
=========================

Le diagramme de Class
---------------------

|classDiagramme|

La Class **AdvancedReader** à pour fonction de faciliter la lecture du fichier RST grâce à différentes méthodes :
  - String readLine() : renvoie une ligne
  - String[] readLines(int nombresLigne) : renvoie un certain nombre de lignes
  - Stringn[] readWhile(Pattern p) : renvoie les lignes tant qu'elles correspondent au pattern

...

La Class **JRSTLexer** utilise **AdvancedReader** pour construire un fichier XML, il parcours
l'ensemble du document pour isoler les types de données, leurs paramètres et leurs contenus,
donc rassembler toutes les informations utiles à la mise en forme du XML final. Il va commencer par
l'entête du document (peekHeader(), peekDocInfo()) pour ensuite s'intéresser au corps (peekBody()).

La Class **JRSTReader** utilise **JRSTLexer**, il interprète le XML qui lui est renvoyé pour construire
le XML final. Celui-ci est conforme à la DTD définie par DocUtils. Cette Class à parfois besoin de
s'appeler elle même lorsque une partie du document doit être interprétée indépendamment du reste.
Par exemple, s'il y a une liste dans une case d'un tableau, l'on extrait les informations de la case
et on les interprètes, le contenu d'une admoniton (une note) doit lui aussi être considéré comme un
document indépendant. Lorsque la génération est terminée, la Class compose le sommaire (composeContent())
puis s'occupe de toutes les spécificités « inline » (inline()), comme par exemple les mots en italique ou
gras, les références, les footnotes... Tout ce qui peut apparaître à l'intérieur d'une ligne.

La Class **reStructuredText** référence toutes les variables nécessaires à la génération du XML final.

La Class **JRST** contient la méthode main(), elle gère les options, la lecture et l'écriture des fichiers.
Elle lit le document, le parse grâce à la class **JRSTReader** puis applique le XSL désiré (si besoin)
grâce à la class **JRSTGenerator**.

La génération
-------------

|diagrammegeneration|

Référence :

- xml2rst.xsl (convertion de xml de docutils vers rst) : http://www.merten-home.de/FreeSoftware/xml2rst
- dn2dbk.xsl (convertion de xml de docutils vers docbook) : http://membres.lycos.fr/ebellot/dn2dbk
- les xsl de nwalsh (convertion de docbook vers FO et xhtml) : http://nwalsh.com

- XMLmind (convertion de FO vers ODT et RTF) : http://www.xmlmind.com/foconverter/what_is_xfc.html
- FOP (convertion de FO vers PDF) : http://xmlgraphics.apache.org/fop

Exemple d'utilisation
---------------------

L'on souhaite convertir le document rst (text.rst) suivant en html (text.html) :

::

   =====
   Titre
   =====

   :Author: Letellier Sylvain

   .. Attention:: texte à être réinterprété comme un fichier rst indépendant
      ceci est considéré comme un **paragraphe**

On utilise donc la commande suivante::

   JRST -t html -o text.html text.rst

Ce diagramme de séquence décrit le fonctionnement du parseur tout au long de la génération :

|sequanceDiagramme|

La Classe **JRSTGenerator**, grâce au fichier XSL rst2xhtml.xsl, renvoie le fichier html suivant::

   <?xml version="1.0" encoding="UTF-8"?>
   <html xmlns="http://www.w3.org/TR/xhtml1/strict">
     <head>
       <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-15"/>
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
           <p>texte à être réinterprété comme un fichier rst indépendant
              ceci est considéré comme un <strong>paragraphe</strong></p>
         </p>
       </div>
     </body>
   </html>

Qui affiche la page (un CSS [2]_ à été ajouté pour la mise en forme) :

.. topic:: Titre

   :Author: Letellier Sylvain
   
   .. Attention:: texte à être réinterprété comme un fichier rst indépendant
      ceci est considéré comme un **paragraphe**

Utilisation de XSL externe
--------------------------

JRST propose de transformer le XML de docutils avec des fichiers XSL externe.
Pour cela, il faut utiliser la commande::

  JRST -x fichierXSL, fichierXSL2 fichierRST
ou::

  JRST --xslFile fichierXSL, fichierXSL2 fichierRST

JRST traitera le fichierRST, le XML de DocUtils qui est retourné sera transformé par la Class JRSTgenerator
en commençant par le fichierXSL puis par le fichierXSL2...

.. [2] Cascading Style Sheets : http://fr.wikipedia.org/wiki/Feuilles_de_style_en_cascade
.. |diagrammegeneration| image:: /home/letellier/PROJET/jrst2/src/site/fr/rst/devel/images/diagrammeGeneration.png
.. |classDiagramme| image:: /home/letellier/PROJET/jrst2/src/site/fr/rst/devel/images/diagrammeClass.png
.. |sequanceDiagramme| image:: /home/letellier/PROJET/jrst2/src/site/fr/rst/devel/images/diagrammeSequance.png

Le XSL (Extensible Stylesheets Language)
========================================

Introduction
------------

XML est un langage de structuration des données, et non de représentation des données. Ainsi XSL
(eXtensible StyleSheet Language) est un langage recommandé par le W3C pour effectuer la représentation
des données de documents XML. XSL est lui-même défini avec le formalisme XML, cela signifie qu'une
feuille de style XSL est un document XML bien formé.

XSL est un langage permettant de définir des feuilles de style pour les documents XML au même titre que
les CSS (Cascading StyleSheets) pour le langage HTML ou bien DSSSL (Document Style Semantics and
Specification Language) pour le SGML. XSL est d'ailleurs inspiré de DSSSL dont il reprend beaucoup
de fonctionnalités et est compatible avec les CSS (il s'agit d'un sur-ensemble des CSS).

Toutefois, contrairement aux CSS, XSL permet aussi de retraiter un document XML afin d'en modifier totalement
sa structure, ce qui permet à partir d'un document XML d'être capable de générer d'autres types de documents
(PostScript, HTML, Tex, RTF, ...) ou bien un fichier XML de structure différente.

Ainsi la structuration des données (définie par XML) et leur représentation (définie par un langage tel que
XSL) sont séparées. Cela signifie qu'il est possible à partir d'un document XML de créer des documents
utilisant différentes représentations (HTML pour créer des pages web, WML pour les mobiles WAP, ...).

|presentationxsl|


Structure d'un document XSL
---------------------------

Un document XSL étant un document XML, il commence obligatoirement par la balise suivante::

   <?xml version="1.0" encoding="ISO-8859-1"?>


D'autre part, toute feuille de style XSL est comprise entre les balises <xsl:stylesheet ...> et </xsl:stylesheet>.

La balise xsl:stylesheet encapsule des balises xsl:template définissant les transformations à faire subir à certains
éléments du document XML.

::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <xsl:stylesheet
   xmlns:xsl="http://www.w3.org/TR/WD-xsl"
   xmlns="http://www.w3.org/TR/REC-html40"
   result-ns="">
      <xsl:template ... >
          <!-- traitements à effectuer -->
      </xsl:template >
   </xsl:stylesheet>


Association d'une feuille XSL à un document XML
-----------------------------------------------

Une feuille de style XSL (enregistré dans un fichier dont l'extension est .xsl) peut être liée à un document
XML (de telle manière à ce que le document XML utilise la feuille XSL) en insérant la balise suivante au début
du document XML::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <?xml-stylesheet href="fichier.xsl" type="text/xsl"?>


Les template rules (règles de gabarit)
--------------------------------------

Les template rules sont des balises XSL permettant de définir des opérations à réaliser sur certains éléments
du document XML utilisant la page XSL, c'est-à-dire généralement de transformer un tag XML en au moins un tag
HTML (généralement plusieurs).

Ainsi le tag XML suivant::

   <personne>
      <nom>Pillou</nom>
      <prenom>Jean-François</prenom>
   </personne>
   <personne>
      <nom>VanHaute</nom>
      <prenom>Nico</prenom>
   </personne>
   <personne>
      <nom>Andrieu</nom>
      <prenom>Seb</prenom>
   </personne>

pourra être transformé en les tags HTML suivants::

   <ul>
      <li>Pillou - Jean-François</li>
      <li>VanHaute - Nico</li>
      <li>Andrieu - Seb</li>
   </ul>

L'attribut "match" de la balise <xsl:template> permet de définir (grâce à la notation XPath) le ou les éléments
du document XML sur lesquels s'applique la transformation.

La notation Xpath permet de définir des patterns, c'est-à-dire des chaînes de caractères permettant de repérer
un noeud dans le document XML. Les principaux patterns sont :

=======  =============  =================================================================
Pattern  Exemple        Signification   
=======  =============  =================================================================
``|``    Gauche|Milieu  Indique une alternative (un noeud ou bien l'autre (ou les deux))
/        personne/nom   Chemin d'accès aux éléments (personne/bras/gauche)
*        *              Motif "joker" désignant n'importe quel élément
//       //personne     Indique tous les descendants d'un noeud
.        .              Caractérise le noeud courant
``..``   ``..``             Désigne le noeud parent
@        @valeur        Indique un attribut caractéristique
=======  =============  =================================================================

La transformation peut être réalisée :

- soit par ajout de texte,
- soit en définissant des éléments de transformation, c'est-à-dire des éléments permettant de définir
  des règles de transformation à appliquer aux éléments sélectionnés par l'attribut match 

Voici un exemple de feuille XSL permettant d'effectuer la transformation XML vers HTML décrite ci-dessus::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <xsl:stylesheet
   xmlns:xsl="http://www.w3.org/TR/WD-xsl"
   xmlns="http://www.w3.org/TR/REC-html40"
   result-ns="">
      <xsl:template match="/">
         <HTML>
             <HEAD>
               <TITLE>Titre de la page</TITLE>
             </HEAD>
                 <BODY BGCOLOR="#FFFFFF">
                <xsl:apply-templates/>
             </BODY>
         </HTML>
      </xsl:template >
      <xsl:template match="personne" >
         <ul>
            <li>
               <xsl:value-of select="nom"/>
                 -
               <xsl:value-of select="prenom"/>
            </li>
         </ul>
      </xsl:template >
   </xsl:stylesheet>


Voici la signification de cette feuille XSL :

* Le premier tag <xsl:template> permet d'appliquer une transformation à l'ensemble du document (la valeur
  "/" de l'attribut match indique l'élément racine du document XML). Ce tag contient des balises HTML qui
  seront transmises dans l'arbre résultat.
* L'élément <xsl:apply-templates/> indique le traitement de tous les enfants directs de la racine.
* La balise <xsl:template match="personne"> permet d'aller traiter les éléments de type personne.
* Enfin les deux éléments <xsl:value-of select="nom"/> et <xsl:value-of select="prenom"/> permettent de
  recuperer les valeurs des balises nom et prenom.

Références
----------

- Article : http://www.commentcamarche.net/xml/xmlxsl.php3
- Schéma  : http://fr.wikipedia.org/wiki/Extended_stylesheet_language_transformations

.. |presentationxsl| image:: images/presentationXSL.png 

Les différentes librairies utilisées
====================================

dom4j
-----

Dom4j est une API Open Source Java permettant de travailler avec XML, XPath et XSLT. Cette bibliothèque
est compatible avec les standards DOM, SAX et JAXP.

javax.xml.transform et Xalan
----------------------------

javax.xml.transform et Xalan sont deux librairies permettant la transformation XSL.

SDoc
----

SDoc fournit des composants Swing qui inclut la coloration syntaxique pour de nombreux langages.

Xmlunit
-------

Xmlunit permet de comparer deux fichiers XML pour mettre en évidences les différences.

dom4j : http://www.dom4j.org

javax.xml.transform : http://java.sun.com/j2se/1.4.2/docs/api/javax/xml/transform/package-summary.html

Xalan : http://xml.apache.org/xalan-j/

SDoc : http://sdoc.sourceforge.net/wiki/pmwiki.php

Xmlunit : http://xmlunit.sourceforge.net/


Documentation externe
=====================

Le site de docUtils : http://docutils.sourceforge.net/rst.html

La DTD reStructuredText : http://docutils.sourceforge.net/docs/ref/doctree.html

Un XSL permettant de convertir le XML en RST : http://www.merten-home.de/FreeSoftware/xml2rst/

Pour la génération de la javaDoc en RST : http://java.sun.com/j2se/1.3/docs/tooldocs/javadoc/overview.html


