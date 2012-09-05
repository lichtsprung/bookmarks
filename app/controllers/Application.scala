package controllers

import actors.{ThumbnailMessage, ThumbnailActor}
import models._

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import akka.actor.{Props, ActorSystem}

object Application extends Controller {
  val actorSystem = ActorSystem("test")
  val thumbnailActor = actorSystem.actorOf(Props[ThumbnailActor])

  val form = Form(
    mapping(
      "url" -> text,
      "name" -> text,
      "tags" -> text
    )(Bookmark.apply)((bookmark: Bookmark) => (Some(bookmark.url, bookmark.name, bookmark.tags)))
  )

  def index = Action {
    Ok(views.html.index())
  }

  def bookmark = Action {
    Ok(views.html.newBookmark(form))
  }

  def newBookmark = Action {
    implicit request =>
      form.bindFromRequest.fold(
        hasErrors => {
          BadRequest(views.html.index())
        },
        success => {
          var u = success.url
          if (!success.url.startsWith("http")) u = "http://" + success.url
          thumbnailActor ! ThumbnailMessage(u, u.hashCode.toString)
          Bookmark.create(u, success.name, success.tags)
          Redirect(routes.Application.bookmarks())
        }

      )
  }

  def bookmarks = Action {
    Ok(views.html.bookmarks(Bookmark.all(), form))
  }


  def deleteBookmark(hash: Int) = Action {
    Bookmark.delete(hash.toString)
    Redirect(routes.Application.bookmarks())
  }

  def tagDetails(tag: String) = Action {
    Ok(views.html.index())
  }

}