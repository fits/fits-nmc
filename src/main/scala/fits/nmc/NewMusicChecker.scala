package fits.nmc

import scala.util.parsing.json._
import org.scalatra.ScalatraServlet
import com.google.appengine.api.memcache._

class NewMusicChecker extends ScalatraServlet {

	val cacheExpireSeconds = 7200
	val cache = MemcacheServiceFactory.getMemcacheService("fits.nmc")

	before {
		contentType = "application/json"
	}

	get("/list") {
		getCache("list", toJson(SstvChecker.getNewMusicList()))
	}

	get("/schedule/:id") {
		val id = params("id")
		getCache("schedule-" + id, toJson(SstvChecker.getMusicProgramList(id)))
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

	/**
	 * キャッシュから値を取得する。
	 * キャッシュに無かった場合は valueProc を評価し、キャッシュに保存
	 */
	def getCache(key: String, valueProc: => String): String = {
		cache.get(key) match {
			case null => putCache(key, valueProc)
			case s: String => s
		}
	}

	/**
	 * キャッシュに値を保存する
	 */
	def putCache(key: String, value: String): String = {
		cache.put(key, value, Expiration.byDeltaSeconds(cacheExpireSeconds))
		value
	}
}

