=======================
JRst Programmer's guide
=======================
:Author: Bucas Jf

.. Note::
   Ce logiciel est libre. Réalisé par `Code Lutin`_.

.. _Code Lutin: http://www.codelutin.com

.. contents::

Présentation
============

JRst signifie Java Rst. C'est un parser en Java pour les documents 
en 'plain-text' de type RST_. Le but est de faciliter la documentation
des développements.

.. _RST : http://docutils.sourceforge.net/rst.html

Ce logiciel a été réalisé dans le cadre d'un stage au sein de la SSLL `Code Lutin`_.

.. image:: lutin.jpg
   :comment: Lutinant !


Organisation Générale
=====================

Hiérarchie des classes
----------------------

Le modèle est constitué d'``Element`` qui peuvent chacun être reconnus grâce à leur ``ElementFactory``.
Tout les éléments ont en commun ``AbstractElement`` ainsi que ``AbstractFactory``. 
Certains de ces éléments sont spécialisés dans l'indentation avec l'abstraction ``IndentedAbstractFactory``.

Méthodes communes
-----------------

Dans ``AbstractElement`` sont définies les méthodes qui permettent de lancer le parsage des fils d'un élément.
Les fils sont définis dans le fichier ``jrst.xml``.

``searchChild()`` recherche parmis les fils celui qui est capable d'``ACCEPT`` les caractères stockés dans
le buffer.

``delegate(int c)`` se charge de remplir et de vider le buffer ainsi que d'appeler ``searchChild`` si besoin.

Les méthodes abstraites définies dans chaque élément sont :

- accept : qui permet de savoir si l'élément correspond (``ACCEPT``), peut correspondre (``IN_PROGRESS``), 
  ou n'est pas celui que l'on recherche (``FAILED``)
- parse : prend les caractères les uns à la suite des autres et découpe chacun des élements suivant
  leur besoin respectifs. Le parsage peut simplement s'occuper de déléguer aux fils.
- parseEnd : lorsque l'élément se termine ou qu'un parent se termine, cette méthode doit être appelée.

Pour les éléments spécialisés (pour l'instant, seuls les indentés), deux méthodes sont rajoutées :

- parseHead : identifie la tête du bloc indenté (2 méthodes différentes : Regex ou automate)
- parseBody : le plus souvent effectue juste ``delegate`` mais peut être redéfinie

Classes particulières
---------------------

Racine
******

``Document`` est la classe racine du modèle mémoire contenant l'ensemble des données du fichier parsé.
Il se contente de ``delegate`` pour la phase de parsage, mais à la fin du parsage du document il réalise
un certain nombre de recherche dans le modèle mémoire pour retrouver le titre, la structure (Contents),
et d'autres informations utiles.

Génériques
**********

- AndElement :
  Cet élément sert à englober des éléments qui doivent être parsés de séquentiellement, c'est à dire,
  les uns après les autres.
  
- OrElement :
  Cet autre élément contient les enfants possibles d'un élément. Chacun des enfants est potentiellement
  le bon. Il y a cependant un ordre de parcours des enfants.
  
- Para :
  le paragraphe est l'élément bouffe-tout. Il est le dernier sur la liste des enfants du ``BodyElement``
  défini dans ``jrst.xml``. Il convient dans quasiment tout les cas, sauf quand le parsage commence par
  un espace ou un retour chariot.
  
- Term :
  un terme est l'élément texte de base pour stoquer juste un mot ou un groupement de mot.

Mécanismes de communication
===========================

parent vers enfants
*******************

Les parents dans le modèle mémoire vont demander un certain nombre d'opérations à leur enfants lors
du parsage du fichier.

- ``accept(int c)`` le parent cherche à savoir si le fils accepte le caractère "c"
- ``parse(int c)`` le parent, après acceptation, demande au fils courant de parser le caractère "c"
- ``parseEnd(int c)`` le parent demande au fils de se terminer (le caractère "c" est inutile)

enfant vers parents
*******************

Chaque méthode appelée par un parent renvoie une valeur de retour. Les possibilités sont contenues
dans la classe ``ParseResult`` :

- ``ACCEPT`` : spécifie que la méthode ``accept(int c)`` a fonctionner et que l'élément peut être parsé.
- ``FINISHED`` : à la fin du parsage, cette valeur est accompagnée par le nombre de caractères lus::
          result = ParseResult.FINISHED.setConsumedCharCount(nb_char_read);
- ``FAILED`` : si l'élément ne peut accepter ou ne peut parser, une erreur est renvoyée ainsi qu'un message::
          result = ParseResult.FAILED.setError("Mauvais caractères, attendu x");
- ``IN_PROGRESS`` : quand tout se passe bien, que le moteur est bien huilé, c'est "en progrès"

Parseur
=======

Paramètrage
-----------

Structure du modèle mémoire
~~~~~~~~~~~~~~~~~~~~~~~~~~~

La classe ``FactoryParser`` permet de lire le fichier ``jrst.xml`` qui contient une partie de la configuration
du parseur. Il s'agit en fait de la description du modèle mémoire::

  <factory class="org.codelutin.jrst.DocumentFactory">
      <factory class="org.codelutin.jrst.TitleFactory" cardinality="0..1"/>
          <factory class="org.codelutin.jrst.AndElementFactory" name="StructureModel">
              <factory class="org.codelutin.jrst.OrElementFactory" name="Section" cardinality="1..*">
                  <factory class="org.codelutin.jrst.TitleFactory" cardinality="0..1"/>
                  <factory class="org.codelutin.jrst.ParaFactory"/>
              </factory>
          </factory>
      </factory>
  </factory>


Chaque factory peut définir des enfants qui seront contenus dans l'élément XML. La cardinalité établit
comment traiter l'élément (0, 1, plusieurs fois). Un nom est définissable sur les éléments ``And`` et ``Or``.


Débuggage
~~~~~~~~~

Le parseur défini 4 niveau de débuggage/verbosité.
- 0 : rien, niet, nada, quedalle
- 1 : le document est affiché avec les éléments reconnus en vert
- 2 : les éléments non reconnus sont en rouge
- 3 : à définir...

Il est possible de spécifier sur la ligne de commande le niveau avec l'option ``-v``

Rajout d'élément
----------------

Pour faciliter le rajout d'élément dans le modèle, des Factories-Templates sont disponibles et permettent
de se faire une idée sur comment fonctionne grosso modo le mécanisme.

- ``Template.java`` : pour l'élément à rajouter
- ``TemplateFactory.java`` : pour la factory si l'élément n'est pas indenté ou
- ``TemplateIndentedFactory.java`` : pour la factory si l'élément est *indenté*
- ``jrst.xml`` : il faut rajouter dans ce fichier la factory que vous désirer rajouter pour que 
  la `Structure du modèle mémoire`_ puisse comporter votre élément.

Il y a deux façons de parser un élément. La première, plus fastidieuse mais plus précise consiste à écrire
soit même son automate à état et ainsi récupérer les informations dans l'élément à parser.
La deuxième, plus rapide à écrire et plus concise utilise les expressions régulières pour identifier puis
parser un élément. Il est recommandé de d'abord regarder comment sont implémentés les différents éléments
déjà existants. Les deux manières de faire sont parfois utilisées dans un même élément...

Générateurs
===========

La génération est ce que le logiciel va nous rendre. La classe ``AbstractGenerator`` définit les méthodes
de base des générateurs. La principale est ``generate(Element e)`` qui va redistribuer sur toutes 
les méthodes ``generate(Xxxx e)`` en regardant de quelle instance est le paramètre 'e'.

- HTML : format de sortie principal, celui qui est le plus développé.

- XDOC : basé sur HTML, presque aussi complet

- RST : le format d'entrée peut aussi ressortir, avec quelques différences, notamment les paragraphes qui
  ne sont pas découpés.

- XML : reste à mettre à jour.
