/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nuiton.jrst.convertisor;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;

/**
 *
 * @author letellier
 */

public abstract class DocUtilsVisitor extends VisitorSupport{

    /** to use log facility, just put in your code: log.info(\"...\"); */
    private static Log log = LogFactory.getLog(DocUtilsVisitor.class);

    // Enregistre les elements parse pour ne jamais les y repasser
    protected List<Element> cachedElements;

    protected String rstResult = "";

    public DocUtilsVisitor(){
        cachedElements = new ArrayList<Element>();
    }

    public void setCachedElements(List<Element> cachedElements){
        this.cachedElements = cachedElements;
    }

    public List<Element> getCachedElements(){
        return cachedElements;
    }

    // Permet de ne jamais repasser sur ce noeud
    public void addCachedElement(Element e){
        cachedElements.add(e);
    }

    public void addCachedElements(List<Element> elements){
        cachedElements.addAll(elements);
    }

    public String parseDocument(Element el){
        try {

            // FIXME : Cette partie ne marche pas corectement...
            List<Element> elements = el.elements();
            Document doc = DocumentHelper.createDocument();
            log.info("This element : " + el.getName());
            for (Element element : elements){
                log.info("Child : " + element.getName());
                try{
                    doc.add(element);
                }
                catch(org.dom4j.IllegalAddException ex){
                    log.error("Imposible d ajouter l element " + ex);
                    if (log.isDebugEnabled()){
                        log.debug("Voici l element concerné : " + element.asXML());
                    }
                }
            }

            if (log.isDebugEnabled()){
                log.debug("parsing doc : " + doc.asXML());
            }

            // Creation dune nouvelle instance du visitor utilise
            DocUtilsVisitor visitor = this.getClass().newInstance();

            // TODO : on ne devrais pas en avoir besoin
            visitor.setCachedElements(getCachedElements());

            // Traitement recursif (pour parser un document dans un autre
            el.accept(visitor);

            // Ajout du resultat a la suite du courant
            rstResult += visitor.getResult();

            // Recuperation des documents parse
            addCachedElements(visitor.getCachedElements());

        } catch (Exception ex) {
            log.error("Cant compose document : ", ex);
        }
        return rstResult;
    }

    public String getResult(){
        return rstResult;
    }

}
