package box.white.cc

import box.white.cc.util.WebUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import spock.lang.Specification

class CircleCheckerSuite extends Specification {

    def '固定されたツイート取得のテスト'() {
        setup:
        String screenName = "moke14"
        //Long statusId = 945306094410149889

        String url = WebUtil.getTwitterUrl(screenName)
//        String html = (new URL(url)).getText('UTF-8')
//        String[] data = html.tokenize('\n').findAll {
//            it =~ /rel="canonical"/
//        }
//        println(data)


        Document doc = Jsoup.connect(url).get()
        Elements before = doc.select('div.context')
        Elements checkEl = before.select('.js-pinned-text')
        if (checkEl) {
            println (checkEl)
            println before.next().select('small a').attr('data-conversation-id')
        }
        else {
            println "none"
        }
//        Elements topics = doc.select('.js-pinned-text')
//        def str = ""
//        topics.each{ str += it.text() + "\n" }

        expect:
        1 == 1
    }
}
