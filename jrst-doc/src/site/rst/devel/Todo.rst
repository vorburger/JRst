.. -
.. * #%L
.. * JRst :: Documentation
.. * 
.. * $Id$
.. * $HeadURL$
.. * %%
.. * Copyright (C) 2009 - 2010 CodeLutin
.. * %%
.. * This program is free software: you can redistribute it and/or modify
.. * it under the terms of the GNU Lesser General Public License as 
.. * published by the Free Software Foundation, either version 3 of the 
.. * License, or (at your option) any later version.
.. * 
.. * This program is distributed in the hope that it will be useful,
.. * but WITHOUT ANY WARRANTY; without even the implied warranty of
.. * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
.. * GNU General Lesser Public License for more details.
.. * 
.. * You should have received a copy of the GNU General Lesser Public 
.. * License along with this program.  If not, see
.. * <http://www.gnu.org/licenses/lgpl-3.0.html>.
.. * #L%
.. -
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
