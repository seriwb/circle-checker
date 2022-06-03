package box.white.cc

import groovy.util.logging.Slf4j
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import twitter4j.PagableResponseList
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.User
import twitter4j.UserList
import twitter4j.conf.ConfigurationBuilder
import box.white.cc.component.OAuthComponent
import box.white.cc.dto.CircleInfo
import box.white.cc.util.FileUtil
import box.white.cc.util.WebUtil

@Slf4j
class CircleChecker {

	def config = null

	/** Twitterインスタンス */
	Twitter twitter = null

	/** 1度に取得要求するTweet数 */
	final int TWEET_MAX_COUNT

	/** Tweetをたどるページングの回数 */
	final int PAGING_MAX_COUNT = 100

	/** sleepのベース時間：リスト毎は短め、チェック後は長め */
	final int WAIT_TIME

	User userinfo = null
	LinkedHashSet<String> filterList = null


	/**
	 * コンストラクタ<br>
	 * Config値の設定を行う。
	 *
	 * @param config Config値
	 */
	CircleChecker(config) {
		this.config = config

		TWEET_MAX_COUNT = config.cc.tweet.maxcount
		WAIT_TIME = config.cc.loop.waittime
	}

	void execute() {
		// Twitter認証
		authenticationProcess()

		filterList = FileUtil.skipCommentLine(FileUtil.readLinesExcludeBlank("./config/filter.txt"))

		List<CircleInfo> circleInfoList = null

		String[] targetListNames = config.cc.target.list

		if (targetListNames) {
			targetListNames.each { targetListName ->
				if (targetListName) {
					// リスト指定がある場合はリストから
					println(targetListName)
					circleInfoList = checkList(targetListName)
				} else {
					// リスト指定がなければフォローユーザ名から情報を取得する
					println("タイムライン")
					circleInfoList = checkFollow()
				}
				output(circleInfoList)
			}
		}
	}

	protected output(List<CircleInfo> circleInfoList) {
		int i = 1
		println("Twitter ID\tTwitter Name" + "\tアイコン" +
				"\t一致イベント名\tスペース番号" + "\t画像1\t画像2\t画像3\t画像4" +
				"\tTwitter URL\tTwitter Link\t固定されたツイート" +
				"\tプロフィール画像\t固定されたツイートの画像")
		circleInfoList.each {
			i++

			String pinnedImageUrl = ""
			it.pinnedImageUrls.each{String imageUrl -> pinnedImageUrl += "$imageUrl\t"}
			pinnedImageUrl = pinnedImageUrl.replaceAll(/\t$/,"")

			println("$it.twitterId\t$it.twitterName" + "\t=IMAGE(M${i})" +
					"\t$it.matchString\t$it.spaceString" + "\t=IMAGE(N${i})\t=IMAGE(O${i})\t=IMAGE(P${i})\t=IMAGE(Q${i})" +
					"\t${WebUtil.getTwitterUrl(it.twitterId)}\t$it.twitterUrl\t$it.pinnedTweetUrl" +
					"\t$it.profileImageUrl\t$pinnedImageUrl")
		}
	}

	/**
	 * 固定されたツイートのstatusIdを取得
	 * @param screenName
	 * @return
	 */
	Long getPinnedTweet(String screenName) {
		String url = WebUtil.getTwitterUrl(screenName)
		Document doc = Jsoup.connect(url).get()
		Elements before = doc.select('div.context')
		Elements checkEl = before.select('.js-pinned-text')
		if (checkEl) {
			return before.next().select('small a').attr('data-conversation-id').toLong()
		}
		else {
			return null
		}
	}

	/**
	 * ツイートの画像URLを取得。なければ空配列を返す
	 * @param screenName
	 * @param statusId
	 * @return
	 */
	String[] getImages(Long statusId) {
		String[] results = []

		if (statusId) {
			Status status = twitter.showStatus(statusId)
			status.getMediaEntities().each {
				results += it.getMediaURL()
			}
		}
		results
	}
	/**
	 * OAuth認証を行う
	 * 認証処理でtwitterインスタンスの更新を行う
	 *
	 * @return 認証に成功した場合、true
	 */
	protected void authenticationProcess() throws TwitterException {

		// Twitterオブジェクトの作成
		ConfigurationBuilder cb = new ConfigurationBuilder()
		String consumerKey = config.get("oauth.consumerKey")
		String consumerSecret = config.get("oauth.consumerSecret")
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
		TwitterFactory factory = new TwitterFactory(cb.build())
		twitter = factory.getInstance()

		// 認証
		def oauth = new OAuthComponent()
		oauth.setOAuthAccessToken(twitter)
		if (!oauth.isAuthorized(twitter)) {
			oauth.authorize(twitter)
		}

		// ユーザ情報を取得し、使いまわす
		userinfo = twitter.verifyCredentials()
	}

	protected List<CircleInfo> checkList(targetListName) {

		List<CircleInfo> result = []

		// 認証ユーザが持つリストを取得
		ResponseList<UserList> lists = null
		while (lists == null) {
			try {
				lists = twitter.getUserLists(userinfo.getScreenName())
			}
			catch (TwitterException te) {
				// リスト取得で401が返ってきた場合は、再認証処理を行い、必要なTwitter情報を取り直す
				authenticationProcess()
			}
		}

		UserList targetList = lists.find { it.name == targetListName }

		PagableResponseList<User> users = twitter.getUserListMembers(targetList.id, -1L)
		for (;;) {
			for (User user : users) {
				def circleInfo = checkUserName(user)
				if (circleInfo.twitterName) {
					result.add(circleInfo)
				}
			}
			if (!users.hasNext()) {
				break
			}
			users = twitter.getUserListMembers(targetList.id, users.getNextCursor())
		}

		result
	}

	protected CircleInfo checkUserName(User user) {

		CircleInfo result = new CircleInfo()

		String userName = user.getName()
		filterList.find {
			(userName =~ /($it)(.*)/).each { m0, m1, m2 ->
				result.twitterName = userName
				result.twitterId = user.getScreenName()
				result.twitterUrl = user.getURL() ?: ""
				result.matchString = m1
				result.spaceString = m2
				result.profileImageUrl = user.getOriginalProfileImageURL()

				Long statusId = getPinnedTweet(result.twitterId)
				result.pinnedTweetUrl = statusId ? WebUtil.getTwitterUrl(user.getScreenName(), statusId) : ""
				result.pinnedImageUrls = getImages(statusId)
			}
			// ループ抜け判定
			if (result.twitterName) {
				return true
			}
		}
		result
	}

	protected List<CircleInfo> checkFollow() {
		List<CircleInfo> result = []

		PagableResponseList<User> users = twitter.getFriendsList(userinfo.id, -1L)
		for (;;) {
			for (User user : users) {
				def circleInfo = checkUserName(user)
				if (circleInfo.twitterName) {
					result.add(circleInfo)
				}
			}
			if (!users.hasNext()) {
				break
			}
			users = twitter.getFriendsList(userinfo.id, users.getNextCursor())
		}

		result
	}
}
