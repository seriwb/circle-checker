package box.white.cc.dao

import groovy.sql.Sql

class CcMstDao {

	Sql db = null

	CcMstDao(Sql db) {
		this.db = db
	}

	/**
	 *
	 * @return
	 */
	def create() {
		db.execute("""create table if not exists cc_mst(
					| id bigint auto_increment not null primary key,
					| key varchar2(200),
					| value varchar2(200))""".stripMargin())
	}

	def insert(String key, String value) {

		def listSet = db.dataSet("cc_mst")
		listSet.add([key : key, value : value])
	}

	def countRows() {
		db.rows("""select count(*) from cc_mst""")
	}

	String getValue(String key) {
		def res = db.firstRow("""select value from cc_mst where key = $key""")
		if (res == null) {
			return ""
		}
		else {
			return res.get("value")
		}
	}

	/**
	 * 全情報の取得
	 *
	 * @return keyとvalueのrows
	 */
	def findAll() {
		db.rows("""select key, value from cc_mst""")
	}

	def updateValue(String key, String value) {
		db.execute("""update cc_mst set value = $value where key = $key""")
	}

	/**
	 * @param key キー
	 * @return データがあればtrueを返す
	 */
	def findKey(String key) {
		db.firstRow("""select count(*) from cc_mst where key = $key""").get("count(*)") > 0
	}

	def delete(String key) {
		db.execute("""delete cc_mst where key = $key""")
	}

	def drop() {
		db.execute("""drop table cc_mst""")
	}
}
