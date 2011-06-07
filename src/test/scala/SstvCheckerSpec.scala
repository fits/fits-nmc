package fits.nmc

import org.specs2.mutable._

class SstvCheckerSpec extends Specification {

	"getNewMusicList" should {
		val data = """
			<tr class="odd">
			<td>
			<div class="flag"><em class="mod_iconNew">New</em></div>
			<div class="content"><a href="http://www.spaceshowertv.com/search/detail.cgi?mu=99&ch=2">TEST</a></div>
			</td>
			<td><a href="http://www.spaceshowertv.com/search/artist.cgi?ac=77&ch=2">SAMPLE</a></td>
			</tr>
		"""

		val musicList = SstvChecker.getNewMusicList(data).toList
		val m = musicList.last

		"one music" in {
			musicList must have size(1)
		}

		"music id=99, title=TEST" in {
			m.id must_==("99")
			m.title must_==("TEST")
		}

		"artist is id=77, name=SAMPLE" in {
			m.artist.id must_==("77")
			m.artist.name must_==("SAMPLE")
		}
	}
}
