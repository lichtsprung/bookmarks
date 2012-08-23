package models

import com.hp.hpl.jena.query.QueryFactory

/**
 * Spaqrql queries needed for the bookmark management
 */
object SparqlQueries {

  def getInstancesOfResource(resource: String) = {
    val query = """
      PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
      PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      PREFIX plexus: <http://www.openplexus.net/documents#>

      SELECT ?name ?url ?tag
                  |WHERE {
                  |  ?bookmark rdf:type plexus:Bookmark .
                  |  ?bookmark plexus:name ?name .
                  |  ?bookmark plexus:url ?url .
                  |  ?bookmark plexus:tagged ?tag
                  |}
                """.stripMargin

    QueryFactory.create(query)
  }
}
