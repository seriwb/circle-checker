package box.white.cc

import spock.lang.Specification
import spock.lang.Unroll
import twitter4j.PagableResponseList
import twitter4j.PagableResponseListImpl
import twitter4j.RateLimitStatus
import twitter4j.Twitter
import twitter4j.User
import box.white.cc.dto.CircleInfo

class CircleChecker_CheckFollowSpec extends Specification {

	@Unroll
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
cc.html.dir = "./html"
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
		"ああああ1" | "aaaa" | "http://aaaa.com" | [new CircleInfo("ああああ1", "aaaa", "http://aaaa.com", "ああ", "ああ1")]
		"いいいい" | "iiii" | "http://iiii.com" | []
	}

	def "getFriendsListで次の情報を取得することを確認"() {
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
		def users = Spy(PagableResponseListImpl, constructorArgs: [status, 1])
		users << user
		users.hasNext() >>> [true, false]

		def twitter = Mock(Twitter)
		twitter.getFriendsList(1L, _) >> users

		def file = '''
cc.target.list = ""
cc.html.dir = "./html"
cc.tweet.maxcount = 100
cc.loop.waittime = 600
'''
		def config = new ConfigSlurper().parse(file)

		def testSuite = new CircleChecker(config)
		testSuite.filterList = filterList
		testSuite.userinfo = user
		testSuite.twitter = twitter

		def actual = testSuite.checkFollow()

		expect:
		actual.twitterId == expected.twitterId
		actual.twitterName == expected.twitterName
		actual.twitterUrl == expected.twitterUrl
		actual.matchString == expected.matchString
		actual.spaceString == expected.spaceString

		where:
		name | screenName | url               | expected
		"ああああ1" | "aaaa" | "http://aaaa.com" | [
			new CircleInfo("ああああ1", "aaaa", "http://aaaa.com", "ああ", "ああ1"),
			new CircleInfo("ああああ1", "aaaa", "http://aaaa.com", "ああ", "ああ1")
		]
	}
}
