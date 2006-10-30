voir implantation docutils, et asciidoc

un objet RSTReader qui permet de lire un flux avec des methodes pratiques pour le RST
  int getLine(); retourne le numero de ligne courant
  boolean eof(); retourne vrai si la fin du fichier
  int skip_blank_lines(); passe les lignes blanches, et retourne le nombre passée
  String [] read_all(); lit toutes les lignes du fichier
  String [] read_lines(int count); lit un certain nombre de ligne
  String [] read_ahead(int count); lit un certain nombre de ligne sans avancer le curseur
  String read_until(String pattern); lit si et seulement si la ligne ne correspond pas au pattern
  String read(); lit une ligne, etend les tab en espace
  String read_next(); lit la ligne suivant sans avancer le curseur

un objet RSTLexer qui permet de decouper un flux en element RST
  Cette objet envoie des events pour chaque element detecte
  On peut ajouter des types d'element avec un poids de prise en compte 0<=n<=1, deux elements ne peuvent pas avoir le meme poids, un element de poids 1 sera prioritaire sur un element de poids 0
  addElement(RSTElement e, double weight)
  addElementListener(RSTElementListener l)
  parse(RSTReader r)
  :Remarque: l'objet Title doit retenir tout seul l'ordre des souligner pour connaitre le niveau du titre qu'il parse

un objet RSTDocument qui permet d'avoir une representation memoire du document
  Utilise le RSTLexer pour construire le modele memoire, Permet aussi d'ajout/retirer/modifier des elements du document
  RSTDocument parse(RSTReader)
  
un objet RSTVisitor qui permet de visiter un RSTDocument
  addElementListener(RSTListener l)

un objet RSTWriter implements RSTListener
  permet d'ecrire du RST a partir du RST :)
  
un objet HTMLWriter implements RSTListener
  permet d'ecrire du HTML a partir du RST

un objet PDFWriter implements RSTListener
  permet d'ecrire du PDF a partir du RST
  
un objet DocbookWriter implements RSTListener
  permet d'ecrire du Docbook a partir du RST 


Petit probleme si les writers utilise le RSTListener qui peut se plugger sur
le Lexer pour le TOC car il faudrait pouvoir modifier le debut du fichier
pour ajouter le TOC pour le PDF ou le HTML a la fin de la generation. Donc
ce que l'on gagne d'un cote en lisant le RST au fur et a mesure, on le perd
de l'autre car on ne peut pas ecrire au fur et a mesure :(
