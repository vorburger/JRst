====
Todo
====

Voici la liste des tâches qui reste à faire. Si quelqu'un souhaite aider, ou
a une idée à proposer il ne faut pas hésiter.

- intégrer une date multiformat
- faire une feuille de style de conversion de xhtml -> xdoc

- la documentation en anglais (si quelqu'un pouvait me relire :) )
- faire une règle ant pour aider à la génération
  <jrst include="..." exclude="..." destination="" extension="...."/>
  destination est le répertoire destination
  extension est la modification de l'extension des fichiers sources avec
  cette extension

- un export vers pretion, magic, ou autre outil de présentation.

- permettre l'import de d'autre format que rst, par exemple .odt
  ce qui permettra de convertir simplement du .odt en rst.

- Faire une doclet qui permette d'écrire les Javadocs en RST

- Correction du bug sur les URIResolver, lors de la génération DocBook -> fo
  il va chercher les imports à la racine du jrst, et non dans le jar.

- ajouter une directive pour supporter les equations mathematiques f(x)=a^3/x qui sera rendu graphiquement
