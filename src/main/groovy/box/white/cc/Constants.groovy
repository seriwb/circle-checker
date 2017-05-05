package box.white.cc

class Constants {

	/**
	 * JDBCプロパティ
	 */
	static final Map<String, Object> JDBC_MAP = [
			url:'jdbc:h2:./db/h2.db',
			user:'sa',
			password:'',
			driver:'org.h2.Driver'
		]
}
