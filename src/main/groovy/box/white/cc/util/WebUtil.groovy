package box.white.cc.util

import java.awt.Desktop
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel

//import groovyx.net.http.HttpURLClient

/**
 * WEB操作系のユーティリティ
 *
 * @author seri
 */
class WebUtil {

	static String getTwitterUrl(String screenName) {
		"https://twitter.com/${screenName}"
	}

	/**
	 * 指定のTweetが表示されるTwitterのURLを返す
	 *
	 * @param screenName Twitterユーザ名
	 * @param statusId TweetID
	 * @return TweetのURL
	 */
	static String getTwitterUrl(String screenName, Long statusId) {
		"https://twitter.com/${screenName}/status/${statusId}"
	}

	/**
	 * 指定したURLのページをデフォルトのブラウザで表示する
	 *
	 * @param url 表示するURL
	 * @return 表示できたtrue
	 */
	static void viewUrlPage(String url) {
		Desktop desktop = Desktop.getDesktop()
		desktop.browse(new URI(url))
	}

	/**
	 * WEBから画像をダウンロードする。
	 *
	 * @param url 画像URL
	 * @param filepath 画像保存先ファイル
	 */
	static void download(String url, File filepath) {
		String imageUrl = url
		if (url =~ """pbs.twimg.com""") {
			imageUrl = url.concat(":large")
		}
		URL website = new URL(imageUrl)
		ReadableByteChannel rbc = Channels.newChannel(website.openStream())
		FileOutputStream fos = new FileOutputStream(filepath)
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE)
		fos.close()
	}
}
