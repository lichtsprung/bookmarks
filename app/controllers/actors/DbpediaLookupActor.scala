package controllers.actors

import akka.actor.Actor
import com.hp.hpl.jena.query.QueryExecutionFactory

case class DbpediaLookupMessage(lookupTerm: String)

/**
 * This actor can be used to access the dbpedia sparql endpoint.
 *
 * @author Robert Giacinto
 */
class DbpediaLookupActor extends Actor {
  val endpoint = "http://dbpedia.org/sparql"

  /**
   * Returns the SPARQL query that gets the abstracts of the resources with the specified term.
   * @param resource the search term
   * @return the query
   */
  def lookupAbstractFor(resource: String) = {
    """
      |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      |PREFIX dbpedia:<http://dbpedia.org/ontology/>
      |SELECT ?abstract
      |WHERE{
      | TERM dbpedia:abstract ?abstract
      |}
    """.stripMargin.replaceAllLiterally("TERM", resource)
  }

  /**
   * Returns the SPARQL query that gets all resources and their labels that belong to one class.
   * @param typeName the type
   * @return the query
   */
  def getResourceLabelByType(typeName: String) = {
    """
      |PREFIX dbpedia:<http://dbpedia.org/ontology/>
      |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      |SELECT DISTINCT ?label WHERE {
      |	?resouces rdf:type TYPE.
      | ?resouces rdfs:label ?label .
      | FILTER (lang(?label) = 'en').
      |}
    """.stripMargin.replaceAllLiterally("TYPE", typeName)
  }

  /**
   * Returns the SPARQL query that gets the ressources from dbpedia that include the search term in their labels.
   * @param term the term
   * @return the SPARQL query
   */
  def getResourcesFor(term: String) = {
    """
      |PREFIX dbpedia:<http://dbpedia.org/ontology/>
      |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      |SELECT DISTINCT ?s ?o	WHERE {
      |	?s rdfs:label ?o.
      |	?o bif:contains TERM.
      |
      | FILTER (!regex(str(?s), '^http://dbpedia.org/resource/Category:')).
      |	FILTER (!regex(str(?s), '^http://dbpedia.org/class/yago/')).
      |	FILTER (!regex(str(?s), '^http://sw.opencyc.org/')).
      |	FILTER (lang(?o) = 'en').
      |}
    """.stripMargin.replaceAllLiterally("TERM", term)
  }

  def receive = {
    case DbpediaLookupMessage(term) =>
      val query = ""
      val executionEnv = QueryExecutionFactory.sparqlService(endpoint, query)
  }
}
