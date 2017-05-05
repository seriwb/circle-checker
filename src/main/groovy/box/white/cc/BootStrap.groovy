package box.white.cc

import box.white.cc.dao.CcMstDao
import groovy.sql.Sql
import groovy.util.logging.Slf4j

/**
 * 初期化用クラス<br>
 * initで以下の処理を行う。
 * <ul>
 * <li>config/config.txtに記載された設定値の読み込み</li>
 * <li>DBの初期データ作成</li>
 * <li>Twitter API利用の初期処理</li>
 * </ul>
 *
 * 各クロージャはconfigを受け取り、configを返す。
 *
 * @author seri
 */
@Slf4j
class BootStrap {

	def init = { config ->
		// 1---------- 設定値の初期化 ---------------
		config = new ConfigSlurper().parse(
				new File('./config/config.txt').toURI().toURL())

		// 2---------- DBの初期化 ---------------
		def db = Sql.newInstance(Constants.JDBC_MAP)

		// マスタ系テーブルの作成
		def ccMstDao = new CcMstDao(db)
		try {
			ccMstDao.create()
		} catch (e) {
			log.info "cc_mst already exist."
		}

		db.close()

		// 3---------- Twitter API利用のキー情報を取得 ---------------
		InputStream keydata = (BootStrap.class).getResourceAsStream("key.data")
		byte[] decoded = null
		keydata.eachLine { decoded = it.decodeBase64() }

		ByteArrayInputStream bais = new ByteArrayInputStream(decoded)
		ObjectInputStream ois = new ObjectInputStream(bais)
		Map<String, String> keymap = (HashMap) ois.readObject()

		config.put("oauth.consumerKey", keymap.get("consumerKey"))
		config.put("oauth.consumerSecret", keymap.get("consumerSecret"))

		config
	}


	/**
	 * 環境初期化用
	 */
	def destroy = {

		def db = Sql.newInstance(Constants.JDBC_MAP)

		try {
			log.debug "drop table cc_mst"
			db.execute('drop table cc_mst')
		} catch (e) {
			log.error ("DB init Error!", e)
		}

		db.close()
	}
}
