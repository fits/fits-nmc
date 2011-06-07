package fits.nmc

import org.scalatra.ScalatraServlet
import com.google.appengine.api.memcache._
import com.google.appengine.api.users._

class NewMusicChecker extends ScalatraServlet {

	//キャッシュの有効期間（秒）
	val cacheExpireSeconds = 14400
	val cache = MemcacheServiceFactory.getMemcacheService("fits.nmc")
	val users = UserServiceFactory.getUserService()

	beforeAll {
		contentType = "application/json"
	}

	//新曲のリストを取得する（キャッシュあり）
	get("/list") {
		getCache("list", toJson(SstvChecker.getNewMusicList()))
	}

	//新曲のリストを取得する（キャッシュを常に更新）
	get("/list_update") {
		putCache("list", toJson(SstvChecker.getNewMusicList()))
	}

	//指定曲の放送スケジュールを取得する（キャッシュあり）
	get("/schedule/:id") {
		val id = params("id")
		getCache("schedule-" + id, toJson(SstvChecker.getMusicProgramList(id)))
	}

	//指定曲の放送スケジュールを取得する（キャッシュを常に更新）
	get("/schedule_update/:id") {
		val id = params("id")
		putCache("schedule-" + id, toJson(SstvChecker.getMusicProgramList(id)))
	}

	get("/bookmark") {
		val userId = getCurrentUserId()

		userId match {
			case "" => 
				"{\"error\": \"" + users.createLoginURL("/bookmark.html") + "\"}"
			case _ =>
				toJson(MusicBookmark.getBookmarkList(userId))
		}
	}

	post("/bookmark/add") {
		val userId = getCurrentUserId()

		userId match {
			case "" => "false"
			case _ =>
				val musicId = params("id")
				val title = params("title")
				val artist = params("artist")
				MusicBookmark.addBookmark(userId, musicId, title, artist)
				"true"
		}
	}

	post("/bookmark/del") {
		val userId = getCurrentUserId()

		userId match {
			case "" => "false"
			case _ => 
				val musicId = params("id")
				MusicBookmark.removeBookmark(userId, musicId)
				"true"
		}
	}

	def toJson(list: Iterator[Any]): String = {
		list.map {m => 
			m match {
				case m: Music => toJson(m)
				case p: Program => toJson(p)
			}
		}.mkString("[", ",", "]")
	}

	def toJson(m: Music): String = {
		"{\"id\": \"" + m.id + "\", \"title\": \"" + 
			m.title + "\", \"artist\": \"" + m.artist.name + "\"}"
	}

	def toJson(p: Program): String = {
		"{\"title\": \"" + p.title + "\", \"date\": \"" + p.date + "\"}"
	}

	private def getCurrentUserId(): String = {
		users.getCurrentUser() match {
			case null => ""
			case u: User => u.getUserId()
		}
	}

	/**
	 * キャッシュから値を取得する。
	 * キャッシュに無かった場合は valueProc を評価し、キャッシュに保存
	 */
	private def getCache(key: String, valueProc: => String): String = {
		cache.get(key) match {
			case null => putCache(key, valueProc)
			case s: String => s
		}
	}

	/**
	 * キャッシュに値を保存する
	 */
	private def putCache(key: String, value: String): String = {
		cache.put(key, value, Expiration.byDeltaSeconds(cacheExpireSeconds))
		value
	}
}

