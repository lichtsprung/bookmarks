package controllers

import models._

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

object Application extends Controller {

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
          BadRequest(views.html.index())},
        success => {
          Bookmark.create(success.url, success.name, success.tags)
          Redirect(routes.Application.bookmarks())
        }

      )
  }

  def bookmarks = Action {
    Ok(views.html.bookmarks(Bookmark.all()))
  }


  def deleteBookmark(hash: Int) = Action {
    Bookmark.delete(hash.toString)
    Redirect(routes.Application.bookmarks())
  }

  def tagDetails(tag: String) = Action {
    Ok(views.html.index())
  }

}