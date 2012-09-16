package controllers.actors

import akka.actor.Actor
import org.apache.lucene.document.Document

case class DocumentMessage(document: Document)

/**
 * Actor that evaluates the new terms of a documents and adds the most interesting candidates and synonyms to the
 * ontology.
 */
class TermEvaluationActor extends Actor{
  def receive = {
    case DocumentMessage(document) => // TODO Fancy analysing
  }
}
