=====================================
Tutoriel pour la création d'un module
=====================================

:Author: Ruchaud Julien <ruchaud@codelutin.com>
:Revision: $Revision$
:Date: $Date$

.. contents::


Exemple
-------

Ce tutoriel montre le développement d'un module en reprenant les différentes parties de la documentation développeur. L'exemple traité est la gestion électronique de documents. Le module GED est limité à un modèle super simple.

Les fonctionnalités du module GED sont les suivantes :

- Ajout/Suppression de documents
- Visualisation de la liste des documents

Les différents fichiers de notre module seront répartis dans les répertoires suivants :

- pour le modèle : /src/xmi
- pour la vue : /src/webapp/WEB-INF/ged
- pour le contrôleur : /src/java/web/mentawai/ged


Modèle
------


Etape 1 : Création du modèle sur ArgoUML
++++++++++++++++++++++++++++++++++++++++

Le modèle est le plus simple possible, il ne comporte qu'une seule classe, Document. Elle définie un document avec son nom et sa description.

::

  +----------------------------------+
  |             «entity»             |
  |             Document             |
  +----------------------------------+
  | name : String                    |
  | description : String             |
  +----------------------------------+
  | getNameAndDescription() : String |
  +----------------------------------+

Le modèle est à créer dans le fichier /src/xmi/chorem.zargo dans le paquetage org.codelutin.chorem.entities.ged. On remarque que la classe porte le stéréotype Entity, ceci permet d'indiquer que Topia se chargera de la génération de cette classe ainsi que de sa persistance.


Etape 2 : Génération du code avec ToPIA
+++++++++++++++++++++++++++++++++++++++

Le plugin Maven 2 Generator se charge de lancer la génération de ToPIA. Par la commande "mvn compile", nous lançons la compilation du projet ainsi que la génération ToPIA. Nous obtenons les classes Java et les fichiers de mapping Hibernate dans le répertoire /src/gen/java/org/codelutin/chorem/entities/ged. 
Le projet ne compile pas car il ne trouve pas l'implémentation des méthodes du modèle :

:: 

  [INFO] Compilation failure
  /chorem3/target/src-build/java/org/codelutin/chorem/ChoremDAOHelper.java:[76,41] cannot find symbol
  symbol  : class DocumentImpl
  location: package org.codelutin.chorem.entities.ged


Etape 3 : Implémentation des méthodes
+++++++++++++++++++++++++++++++++++++

Les méthodes contenues dans le modèle n'étant pas implémentées, il est maintenant nécessaire de les complèter. Elles sont a compléter dans le répertoire /src/java/org/codelutin/chorem/entities/ged et porte comme nom, le nom de classe avec le suffixe "Impl". Elle doivent hériter de sa classe abstraite et implémenter son interface.

::

  public class DocumentImpl extends DocumentAbstract implements Document {
    public String getNameAndDescription() {
      return getName() + " " + getDescription();
    }
  }

Maintenant la compilation (mvn compile) ne doit plus produire d'erreur.


Etape 4 : Modification des classes de persistances
++++++++++++++++++++++++++++++++++++++++++++++++++

La propriété topia.persistence.classes dans le fichier /src/ressources/TopiaContextImpl.properties doit contenir la classe Document.

::

  topia.persistence.classes=...,org.codelutin.chorem.entities.ged.DocumentImpl,...


Vue
---


Etape 5 : Déclaration du module au niveau du Layout
+++++++++++++++++++++++++++++++++++++++++++++++++++

Il faut déclarer le nom du module dans les fichiers de langue dans le répertoire /src/webapp/WEB-INF/common/lang.

::

  ged = GED

Après, il faut ajouter le module dans l'entête, fichier /src/webapp/WEB-INF/common/layout/layout.jsp, pour pouvoir donner l'accès à celui-ci.

::

  | <a class="Link" href="${path}/ged.mtw"><fmt:message key="ged" bundle="${commonBundle}"/></a>


Etape 6 : Création du menu
++++++++++++++++++++++++++

Chaque module a son propre menu. Il faut créer le fichier /src/webapp/ged/common/menu.jsp pour ajouter un menu au module GED.

::

  <%@ include file="/WEB-INF/common/taglibs.jsp"%>
  <h1 class="tab" onclick="tabHideShow('tabMenu');"><fmt:message key="tabMenu.title"/></h1>
  <div id="tabMenu" class="${cookie.tabMenu.value}">
    <h2 class="Title"><fmt:message key="tabMenu.shortcuts"/></h2>
    <menu class="Menu">
      <li><a class="Link" href="${pathModule}/ged.mtw"><fmt:message key="tabMenu.shortcuts.ged"/></a></li>
    </menu>
  </div>

  <jsp:include page="${common}/layout/tabSpace.jsp"/>


Etape 7 : Création des pages
++++++++++++++++++++++++++++

Il faut créer les pages JSP de visualisation du module. Quelques règles sont à respecter :
  1. Il faut toujours regarder et recopier ce qui a été fait.
  2. Tous les formulaires doivent être créés avec le tag chorem:action.
  3. Aucun texte doit être fixe dans la page, il faut utiliser fmt:message.

Les deux fichiers sont stockés dans le répertoire /src/webapp/ged/document.

Le fichier list.jsp permet de visualiser la liste des documents :

::

  <%@ include file="/WEB-INF/common/taglibs.jsp"%>
  <jsp:include page="${headerPage}"/>

  <h1 class="Title"><fmt:message key="document.list.title"/></h1><hr class="Ligne"/>

  <div class="FormActions">
    <chorem:action mtw="${pathModule}/document/new.mtw">
      <input type="submit" value="<fmt:message key="button.new" bundle="${commonBundle}"/>">
    </chorem:action>
  </div>

  <div class="Container">
    <table class="Large">
      <tr class="First">
        <td><fmt:message key="document.list.name"/></td>
        <td><fmt:message key="document.list.description"/></td>
      </tr>

      <mtw:list value="documents">
        <mtw:isEmpty><td colspan="2" align="center"><fmt:message key="document.list.empty"/></td></mtw:isEmpty>

        <mtw:loop var="eltDocument">
          <tr>
            <td>${eltDocument.name}</td>
            <td>${eltDocument.description}</td>
            <td class="Actions">
              <chorem:action mtw="${pathModule}/document/del.mtw">
                <input type="hidden" name="documentId" value ="${eltDocument.topiaId}">
                <input type="submit" value="<fmt:message key="button.delete" bundle="${commonBundle}"/>">
              </chorem:action>
            </td>
          </tr>
        </mtw:loop>
      </mtw:list>
    </table>
  </div>


Le fichier form.jsp permet de modifier ou creer un nouveau document :

::

  <%@ include file="/WEB-INF/common/taglibs.jsp"%>
  <jsp:include page="${headerPage}"/>
  <h1 class="Title"><fmt:message key="document.form.title"/></h1><hr class="Ligne"/>

  <!-- Partie enregistrement -->
  <chorem:action name="store" mtw="${pathModule}/document/store.mtw">
    <div class="Container">
      <div class="FormLeft">
        <div class="Text"><fmt:message key="document.form.name"/></div>
        <div class="Text"><fmt:message key="document.form.description"/></div>
      </div>

      <div class="FormRight">
        <div class="Normal">
          <input type="text" name="name" value="${document.name}"/>
        </div>
        <div class="Normal">
          <input type="text" name="description" value="${document.description}"/>
        </div>
      </div>
    </div>
  </chorem:action>

  <!-- Partie annulation -->
  <chorem:action name="cancel" mtw="${pathModule}/document/cancel.mtw"/>

  <!-- Partie actions -->
  <div class="FormActions">
    <button onclick="document.cancel.submit();"><fmt:message key="button.cancel" bundle="${commonBundle}"/></button>
    <button onclick="document.store.submit();"><fmt:message key="button.store" bundle="${commonBundle}"/></button>
  </div>


Etape 8 : Internationalisation
++++++++++++++++++++++++++++++

Pour finir les pages jsp, il faut créer les fichiers de langue dans le répertoire /src/webapp/WEB-INF/ged/common/lang. De plus, il faut rajouter ce répertoire comme ressource dans le pom.xml de Maven.


Fichier de langue pour le français est ged_fr.properties, il contient :

::

  ## Menu ##
  tabMenu.title = Menu
  tabMenu.shortcuts = Raccourcis
  tabMenu.shortcuts.ged = Documents
  
  ## Document ##
  document.list.title = Liste des documents
  document.list.empty = Vide
  document.list.name = Nom
  document.list.description = Description

  document.form.title = Liste des documents
  document.form.name = Nom :
  document.form.description = Description :

Généralement les clés sont de la forme suivante : "nom de l'entité"."nom de la page"."nom de l'élément" = ...

Le bundle commun (${commonBundle}) contient l'ensemble des traductions transversales à tous les modules.


Contrôleur
----------


Etape 9 : Création des actions
++++++++++++++++++++++++++++++

Quatres actions sont toujours créer pour chaque entités du modèle, elles permettent les manipulations de base.  Elles sont stockées dans le package actions du module.

Ces actions vont être nécessaire pour notre exemple les voici, elle doit être dans src/java/org/codelutin/chorem/web/mentawai/ged/action/document :

DocumentFindId.java, permet de rechercher un document par rapport à son identifiant :

::

  public class DocumentFindId extends ElementaryAction {
    @TopiaBean
    private Document document;
    
    public void action() throws Exception {
    }
  }

DocumentFindAll.java, permet de récupérer l'ensemble des documents :

::

  public class DocumentFindAll extends ElementaryAction {
    public void action() throws Exception {
      DocumentDAO documentPS = ChoremDAOHelper.getDocumentDAO(transaction);
      output.setValue("documents", documentPS.findAll());
    }
  }

DocumentUpdate.java, permet de créer et de modifier un document :

::

  public class DocumentUpdate extends ElementaryAction implements Validatable {
    @TopiaBean(recuperation = true)
    private Document document;

    public void action() throws Exception {
      document.update();
      addMessage(MESSAGE_STORE);
    }

    public void initValidator(Validator validator, String innerAction) {
      super.initValidator(validator, innerAction);
      addRule(RequiredFieldRule.class, "name");
    }
  }


DocumentDelete.java, permet la suppression d'un document :

::

  public class DocumentDelete extends ElementaryAction {
    @TopiaBean
    private Document document;

    public void action() throws Exception {
      document.delete();
      addMessage(MESSAGE_DELETE);
    }
  }


Etape 10 : Création du Manageur
+++++++++++++++++++++++++++++++

Le manageur centralise l'ensemble des urls, des pages et des cas d'utilisation avec les actions associées. Pour la GED la classe à créer est src/java/org/codelutin/chorem/web/mentawai/ged/Manager.java :

::

  public class Manager extends org.codelutin.chorem.web.mentawai.Manager {
    public static String DOCUMENT_LIST_JSP = "/WEB-INF/ged/document/list.jsp";
    public static String DOCUMENT_FORM_JSP = "/WEB-INF/ged/document/form.jsp";
    public static String DOCUMENT_DEL = "ged/document/del";
    public static String DOCUMENT_NEW = "ged/document/new";
    public static String DOCUMENT_MODIFY = "ged/document/modify";
    public static String DOCUMENT_STORE = "ged/document/store";
    public static String DOCUMENT_CANCEL = "ged/document/cancel";

    public void loadActions() {
      // Actions de l'utilisateur
      createFirstLine(DOCUMENT_DEL, DocumentDelete.class);
      createFirstLine(DOCUMENT_NEW, SuccessAction.class);
      createFirstLine(DOCUMENT_MODIFY, SuccessAction.class);
      createFirstLine(DOCUMENT_STORE, DocumentUpdate.class);
      createFirstLine(DOCUMENT_CANCEL, SuccessAction.class);
      // JSP et actions liées
      createLastLine(DOCUMENT_LIST_JSP, DocumentFindAll.class);
      createLastLine(DOCUMENT_FORM_JSP, DocumentFindId.class);
    }
  }


Etape 11 : Création des cas d'utilisation
+++++++++++++++++++++++++++++++++++++++++

Il faut maintenant créer les cas d'utilisation. Pour notre module, un seul suffit, aucune page ne présente de comportements différents. La classe à créer est src/java/org/codelutin/chorem/web/mentawai/ged/usecase/DocumentUseCase.java :

::

  public class DocumentUseCase extends UseCase {
    public String general() throws Exception {
      // En partant de la liste des docs on retourne sur cette liste en supprimant un doc ou on accède au formulaire de création
      accessPoint(DOCUMENT_LIST_JSP).move(DOCUMENT_DEL, DOCUMENT_LIST_JSP).move(DOCUMENT_NEW, DOCUMENT_FORM_JSP);

      // En partant du formulaire de création on retourne à la liste des docs en validant la création ou en l'annulant
      access(DOCUMENT_FORM_JSP, "documentId").move(DOCUMENT_CANCEL, DOCUMENT_LIST_JSP).move(DOCUMENT_STORE, DOCUMENT_LIST_JSP);

      return super.execute();
    }
  }


Ajoutons ensuite le cas d'utilisation au manageur du module :

::

  public class Manager extends org.codelutin.chorem.web.mentawai.Manager {

    public static String DOCUMENT_USE_CASE = "ged/general";

    public void loadActions() {
      // Cas d'utilisation
      createUseCase(DOCUMENT_USE_CASE, DocumentUseCase.class);
      createAccessModule("ged", DOCUMENT_USE_CASE);
    }
  }


Etape 12 : Déclaration du module
++++++++++++++++++++++++++++++++

Il est nécessaire d'ajouter le chargement du manageur du module GED dans le manageur global de Chorem (src/java/org/codelutin/chorem/web/mentawai/Manager.java) :

::

  /* GED */
  modules.add(new org.codelutin.chorem.web.mentawai.ged.Manager());    


Test
----


Etape 13 : Démarrer Chorem
++++++++++++++++++++++++++


Etape 14 : Tester le nouveau module
+++++++++++++++++++++++++++++++++++
