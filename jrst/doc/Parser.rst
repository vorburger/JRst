Parser un tableau
=================

Un tableau a toujours des lignes pour determiner la longueur des colonnes::

  +-------------+---------+----+
  |             |         |    |
  +=======+=====+====+====+====+
  |       |          |    |    |
  |       |          |    |    |
  +-------+-----+----+----+----+
  |             |         |    |
  |             +---------+    |
  |             |         |    |
  +-------------+---------+----+

Dans ce cas de tableau, il y aura au final 5 colonnes et 4 lignes. Si on
regarde la premiere ligne::

  +-------------+---------+----+

Elle nous indique qu'il y aura 3 colonnes en dessous. Si maintenant on prend
la troisième ligne::

  +=======+=====+====+====+====+

Elle nous indique qu'il y aura maintenant 5 colonnes. Mais en fait il n'y a
que 4 colonnes car un des + servait a fermer une colonne de la ligne du dessus.

Donc si sur les lignes du dessous on a un | on ne doit le prendre que si sur
la ligne determinant la separation de ligne il y a un + au meme niveau. Sinon
cela veut dire que nous sommes toujours dans la même cellule.

On peut donc decouper le tableau en en 4 sous tableaux::

  +-------------+---------+----+
  |             |         |    |
  +=======+=====+====+====+====+

  +=======+=====+====+====+====+
  |       |          |    |    |
  |       |          |    |    |
  +-------+-----+----+----+----+

  +-------+-----+----+----+----+
  |             |         |    |
  |             +---------+    |

  |             +---------+    |
  |             |         |    |
  +-------------+---------+----+

Les + supplémentaires sur les lignes ne doivent pas être pris comme des
erreurs, car en fait ils peuvent avoir servit pour des colonnes d'une autre
partie du tableau.

Tant que l'on ne retrouve pas une ligne de la meme longueur que la premiere et
ne contenante que les caractères + =-

Donc on detecte un tableau si la ligne correspond a l'expression::

  (\+-)+\+

Ensuite soit on a une ligne de fin de header soit des lignes de separations
normales

pour le header::

  (\+=)+\+

pour les séparations::

  [+|]( +|-+)(\+( +|-+))+[+|]

A partir de cette decoupe on peut retourner un Element TABLE contenant des
ROW contenant des CELL.

TABLE attribute

- header : (true | false) pour indiquer s'il y a un header ou non
- width : int pour indiquer la largeur du tableau


ROW attribute

- endHeader : (true | false) pour indiquer que cette ligne est la derniere du
  header

CELL attribute

- indexStart : int le debut de la cellule
- indexEnd : int la fin de la cellule
- end : (true | false) indique la fin de la cellule dans cette ligne

le text contient le contenu de la cellule


