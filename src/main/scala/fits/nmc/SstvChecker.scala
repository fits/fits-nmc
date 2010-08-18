package fits.nmc

import scala.io.Source

case class Music(val id: String, val title: String, val artist: Artist)
case class Artist(val id: String, val name: String)
case class Program(val title: String, val date: String)

object SstvChecker {
	val NewUrl = "http://www.spaceshowertv.com/newvideo/"
	val ProgramUrl = "http://www.spaceshowertv.com/search/detail.cgi?ch=1&mu="

	val NewP = """<p[^>]*><a.*onClick="open(Music|Artist)Win\('(\w+)',[^\)]*\);">(.*)</p>""".r
	val ProgP = """<ul>\s*<li><img src=\"img/sstv_new.gif\".*/></li>([^\\]*?)</ul>""".r
	val ScheP = """<li>(.*)<br>\s*(.*)　\s*(.*)</li>""".r

	def getNewMusicList(): Iterator[Music] = {
		getNewMusicList(Source.fromURL(NewUrl, "Windows-31J"))
	}

	def getNewMusicList(src: Source): Iterator[Music] = {
		NewP.findAllIn(src.mkString).matchData.sliding(2, 2).map {m =>
			val a = Artist(m(1).group(2), m(1).group(3))
			Music(m(0).group(2), m(0).group(3), a)
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

