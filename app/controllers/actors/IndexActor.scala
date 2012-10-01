package controllers.actors

import akka.actor.Actor

case class IndexMessage(pageText: String)

/**
 * Actor for parallel website indexing.
 */
class IndexActor extends Actor {

  def receive = {
    case IndexMessage(text) =>
    // Indizieren, Termvektor bestimmen und Topic Extraction durchführen.
  }
}

case class NewDocumentMessage(pageText: String)

class LuceneActor extends Actor {
  // Lucene Index und IndexWriter

  def receive = {
    case NewDocumentMessage(text) =>
    // Stopword Removal
    // Document wird Lucene Index hinzugefügt
    // Termvektor zurückgeben
  }
}
