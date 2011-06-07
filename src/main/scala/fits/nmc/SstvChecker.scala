package fits.nmc

import scala.io.Source

case class Music(val id: String, val title: String, val artist: Artist)
case class Artist(val id: String, val name: String)
case class Program(val title: String, val date: String)

object SstvChecker {
	val NewUrl = "http://www.spaceshowertv.com/newvideo/"
	val ProgramUrl = "http://www.spaceshowertv.com/search/detail.cgi?ch=2&mu="

	val NewP = """<a href="http://www.spaceshowertv.com/search/(detail|artist)\.cgi\?(mu|ac)=([^&]*)&ch=2">(.*)</a>""".r

	val ProgP = """<ul>\s*<li><img src=\"img/sstv_new.gif\".*/></li>([^\\]*?)</ul>""".r
	val ScheP = """<li>(.*)<br>\s*(.*)　\s*(.*)</li>""".r

	def getNewMusicList(): Iterator[Music] = {
		getNewMusicList(Source.fromURL(NewUrl).mkString)
	}

	def getNewMusicList(html: String): Iterator[Music] = {
		NewP.findAllIn(html).matchData.sliding(2, 2).map {m =>
			val a = Artist(m(1).group(3), m(1).group(4))
			Music(m(0).group(3), m(0).group(4), a)
		}
	}

	def getMusicProgramList(musicId: String): Iterator[Program] = {
		val url = ProgramUrl + musicId
		getMusicProgramList(Source.fromURL(url, "Windows-31J"))
	}

	def getMusicProgramList(src: Source): Iterator[Program] = {
		ProgP.findAllIn(src.mkString).matchData.toList.flatMap {m =>
			ScheP.findAllIn(m.group(1)).matchData.toList.map {p =>
				Program(p.group(1), p.group(2) + " " + p.group(3))
			}
		}.iterator
	}
}

