package box.white.cc

import spock.lang.Specification
import spock.lang.Unroll
import twitter4j.User
import box.white.cc.dto.CircleInfo

@Unroll
class CircleChecker_CheckUserNameSpec extends Specification {

	def "フィルタにある名前の結果のみが返却されることを確認"() {
		setup:
		List<String> filterList = ["ああ","uu"]

		def user = Mock(User)
		user.getName() >> name
		user.getScreenName() >> screenName
		user.getURL() >> url
		user.getOriginalProfileImageURL() >> profileUrl

		def file = '''
cc.target.list = "listname"
cc.html.dir = "./html"
cc.tweet.maxcount = 100
cc.loop.waittime = 600
'''
		def config = new ConfigSlurper().parse(file)

		def testSuite = new CircleChecker(config)
		testSuite.filterList = filterList

		def actual = testSuite.checkUserName(user)

		expect:
		actual.twitterId == expected.twitterId
		actual.twitterName == expected.twitterName
		actual.twitterUrl == expected.twitterUrl
		actual.matchString == expected.matchString
		actual.spaceString == expected.spaceString
		actual.profileImageUrl == expected.profileImageUrl
		actual.pinnedTweetUrl == expected.pinnedTweetUrl
		actual.pinnedImageUrls == expected.pinnedImageUrls

		where:
		name | screenName | url               | profileUrl | pinnedUrl | imageUrls | expected
		"ああああ1" | "aaaa" | "http://aaaa.com" | "http://profile/image.jpg" | "https://pinned/url" | ["a", "b"] | new CircleInfo("ああああ1", "aaaa", "http://aaaa.com", "ああ", "ああ1")
		"いいいい" | "iiii" | "http://iiii.com"  | "http://profile/image.jpg" | "https://pinned/url" | ["a", "b"] | new CircleInfo()
//		"aaiiuu" | "uuuu" | null                | new CircleInfo("aaiiuu", "uuuu", "", "uu", "")
	}
}
