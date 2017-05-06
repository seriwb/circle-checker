package box.white.cc

import java.util.List

import box.white.cc.model.CircleInfo
import spock.lang.Specification
import spock.lang.Unroll
import twitter4j.PagableResponseList
import twitter4j.PagableResponseListImpl
import twitter4j.RateLimitStatus
import twitter4j.Twitter
import twitter4j.User

@Unroll
class CircleChecker_CheckFollowSpec extends Specification {

	def "CircleInfoのリストが適切に作成されることを確認"() {
		setup:
		List<String> filterList = ["ああ","uu"]

		def user = Mock(User)
		user.getId() >> 1L
		user.getName() >> name
		user.getScreenName() >> screenName
		user.getURL() >> url

		// PagableResponseListImplのパラメータは使わないので適当な値を設定
		def status = [
			getRemaining: { -> 1 },
			getLimit: { -> 1 },
			getResetTimeInSeconds: { -> 1 },
			getSecondsUntilReset: { -> 1 },
		] as RateLimitStatus
		PagableResponseList<User> users = new PagableResponseListImpl<>(status, 1);
		users << user

		def twitter = Mock(Twitter)
		twitter.getFriendsList(1L, _) >> users

		def file = '''
cc.target.list = ""
cc.html.dir = "./dir"
cc.tweet.maxcount = 100
cc.loop.waittime = 600
'''
		def config = new ConfigSlurper().parse(file)

//		def testSuite = Spy(CircleChecker, constructorArgs: [config])
		def testSuite = new CircleChecker(config)
		testSuite.filterList = filterList
		testSuite.userinfo = user
		testSuite.twitter = twitter

		expect:
		testSuite.checkFollow() == expected

		where:
		name | screenName | url               | expected
		"ああああ" | "aaaa" | "http://aaaa.com" | [new CircleInfo("ああああ", "aaaa", "http://aaaa.com")]
		"いいいい" | "iiii" | "http://iiii.com" | []
	}
}
