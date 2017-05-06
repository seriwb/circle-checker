package box.white.cc

import java.util.List;

import box.white.cc.model.CircleInfo
import spock.lang.Specification
import spock.lang.Unroll;
import twitter4j.User

@Unroll
class CircleChecker_CheckUserNameSpec extends Specification {

	def "フィルタにある名前の結果のみが返却されることを確認"() {
		setup:
		List<String> filterList = ["ああ","uu"]

		def user = Mock(User)
		user.getName() >> name
		user.getScreenName() >> screenName
		user.getURL() >> url

		def file = '''
cc.target.list = "listname"
cc.html.dir = "./dir"
cc.tweet.maxcount = 100
cc.loop.waittime = 600
'''
		def config = new ConfigSlurper().parse(file)

		def testSuite = new CircleChecker(config)
		testSuite.filterList = filterList

		expect:
		testSuite.checkUserName(user) == expected

		where:
		name | screenName | url               | expected
		"ああああ" | "aaaa" | "http://aaaa.com" | new CircleInfo("ああああ", "aaaa", "http://aaaa.com")
		"いいいい" | "iiii" | "http://iiii.com" | new CircleInfo()
		"aaiiuu" | "uuuu" | null              | new CircleInfo("aaiiuu", "uuuu", "")
	}
}
