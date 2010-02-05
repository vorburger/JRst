===
FAQ
===

Foire aux Questions

Pourquoi les sections ne s'affichent pas correctement lorsqu'elles sont déclarés directement après le titre principal ?
-----------------------------------------------------------------------------------------------------------------------

En fait la syntaxe rst permet d'avoir un sous-titre en dessous du titre
principal, ce qui peut provoquer un décalage dans le style de soulignement des
sections.

Exemple ::

    ================
    Titre principale
    ================

    sous-titre
    ==========

    titre section 1
    ---------------

    sous-titre section 1
    ++++++++++++++++++++

    titre section 2
    ---------------

    sous-titre section 2
    ++++++++++++++++++++

et non pas ::

    ================
    Titre principale
    ================

    titre section 1
    ---------------

    sous-titre section 1
    ++++++++++++++++++++

    titre section 2
    ---------------

    sous-titre section 2
    ++++++++++++++++++++

Dans le deuxième cas, le titre section 1 sera pris comme un sous-titre et donc
le style des deux sous-titres ne correspondront pas. Le plus simple est d'écrire
un petit paragraphe entre le titre principale et le titre de la section 1 pour
ne pas avoir ce désagrément.


