package controllers.actors

import akka.actor.Actor

case class IndexMessage(pageText: String)

/**
 * Actor for parallel website indexing.
 */
class IndexActor extends Actor {

  def receive = {
    case IndexMessage(text) =>
      println(text)
  }
}
