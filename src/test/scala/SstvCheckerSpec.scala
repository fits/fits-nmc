package fits.nmc

import org.specs2.mutable._

class SstvCheckerSpec extends Specification {

	"parseNewMusicPage" should {
		val data = """
			<tr class="odd">
			<td>
			<div class="flag"><em class="mod_iconNew">New</em></div>
			<div class="content"><a href="http://www.spaceshowertv.com/search/detail.cgi?mu=99&ch=2">TEST</a></div>
			</td>
			<td><a href="http://www.spaceshowertv.com/search/artist.cgi?ac=77&ch=2">SAMPLE</a></td>
			</tr>
		"""

		val musicList = SstvChecker.parseNewMusicPage(data).toList
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

	"parseMusicProgramPage" should {
		val data = """
			<dl>
			<dt>test：</dt>
			<dd>2011/06/22　aaaa</dd>
			</dl>
			<div class="row">
			<div class="onairInner">
			<h5 class="ttl1">TV</h5>
			<dl>
				<dt>RECOMMEND</dt>
				<dd>2011/06/08　
				06:00～07:30</dd>
			</dl>
			<!-- / .onairInner --></div>
			<div class="onairInner">
			<h5 class="ttl2">TV Plus</h5>
			<dl>
				<dt>テスト</dt>
				<dd>2011/06/09　
				16:30～17:30</dd>
			</dl>
			<!-- / .onairInner --></div>
			<!-- / .row --></div>
		"""

		val progList = SstvChecker.parseMusicProgramPage(data).toList

		"two programs" in {
			progList must have size(2)
		}

		"first program" in {
			val p1 = progList.head
			p1.title must_== "RECOMMEND"
			p1.date must_== "2011/06/08 06:00～07:30"
		}

		"second program" in {
			val p2 = progList.last
			p2.title must_== "テスト"
			p2.date must_== "2011/06/09 16:30～17:30"
		}
	}
}
