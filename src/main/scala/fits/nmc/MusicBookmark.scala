package fits.nmc

import scala.collection.JavaConversions._
import com.google.appengine.api.datastore._

object MusicBookmark {
	private val bookmarkKind = "Bookmark"
	private val musicKind = "Music"
	private val titleProperty = "title"
	private val artistProperty = "artist"

	private val store = DatastoreServiceFactory.getDatastoreService()

	def addBookmark(userId: String, musicId: String, title: String, artist: String) = {
		val tr = store.beginTransaction()
		val bkey = createBookmarkKey(userId)

		try {
			store.get(bkey)
		}
		catch {
			case e: EntityNotFoundException => 
				store.put(tr, new Entity(bkey))
		}

		val music = new Entity(musicKind, musicId, bkey)
		music.setProperty(titleProperty, title)
		music.setProperty(artistProperty, artist)

		store.put(tr, music)

		tr.commit()
	}

	def removeBookmark(userId: String, musicId: String) = {
		val tr = store.beginTransaction()

		val mkey = createMusicKey(userId, musicId)
		store.delete(mkey)

		tr.commit()
	}

	def getBookmarkList(userId: String): Iterator[Music] = {

		val query = new Query(musicKind, createBookmarkKey(userId))

		store.prepare(query).asIterator().map {et =>
			val artistName: String = et.getProperty(artistProperty)
			val title: String = et.getProperty(titleProperty)

			Music(et.getKey().getName(), title, Artist("", artistName))
		}
	}

	private implicit def toStr(obj: Object): String = {
		obj match {
			case s: String => s
			case _ => ""
		}
	}

	private def createBookmarkKey(userId: String): Key = {
		KeyFactory.createKey(bookmarkKind, userId)
	}

	private def createMusicKey(userId: String, musicId: String): Key = {
		KeyFactory.createKey(createBookmarkKey(userId), musicKind, musicId)
	}
}

