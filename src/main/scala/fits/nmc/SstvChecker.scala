package fits.nmc

import scala.io.Source

case class Music(val id: String, val title: String, val artist: Artist)
case class Artist(val id: String, val name: String)
case class Program(val title: String, val date: String)

object SstvChecker {
	val NewUrl = "http://www.spaceshowertv.com/newvideo/"
	val ProgramUrl = "http://www.spaceshowertv.com/search/detail.cgi?ch=2&mu="

	val NewP = """<a href="http://www.spaceshowertv.com/search/(detail|artist)\.cgi\?(mu|ac)=([^&]*)&ch=2">(.*)</a>""".r

	val ProgP = """</h5>\s*<dl>\s*<dt>(.*)</dt>\s*<dd>(.*)　\s*(.*)</dd>""".r

	def getNewMusicList(): Iterator[Music] = {
		parseNewMusicPage(Source.fromURL(NewUrl, "UTF-8").mkString)
	}

	def getMusicProgramList(musicId: String): Iterator[Program] = {
		val url = ProgramUrl + musicId
		parseMusicProgramPage(Source.fromURL(url, "UTF-8").mkString)
	}


	def parseNewMusicPage(html: String): Iterator[Music] = {
		NewP.findAllIn(html).matchData.sliding(2, 2).map {m =>
			val a = Artist(m(1).group(3), m(1).group(4))
			Music(m(0).group(3), m(0).group(4), a)
		}
	}

	def parseMusicProgramPage(html: String): Iterator[Program] = {
		ProgP.findAllIn(html).matchData.map {m =>
			Program(m.group(1), m.group(2) + " " + m.group(3))
		}
	}

}

