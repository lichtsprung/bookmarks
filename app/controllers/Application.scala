package controllers

import models._

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

object Application extends Controller {

  val form = Form(
    mapping(
      "url" -> nonEmptyText,
      "name" -> text,
      "tags" -> text
    )(Bookmark.apply)(Bookmark.unapply)
  )

  def index = Action {
    Redirect(routes.Application.bookmarks)
  }

  def bookmarks = Action {
    Ok(views.html.index(Bookmark.all, form, Bookmark.tagList))
  }

  def newBookmark = Action {
    implicit request =>
      form.bindFromRequest.fold(
        hasErrors => BadRequest(views.html.index(Bookmark.all, hasErrors, Bookmark.tagList)),
        success => {
          Bookmark.create(success.url, success.name, success.tags)
          Redirect(routes.Application.bookmarks)
        }

      )
  }

  def deleteBookmark(url: String) = Action {
    Bookmark.delete(url)
    Redirect(routes.Application.bookmarks)
  }

}