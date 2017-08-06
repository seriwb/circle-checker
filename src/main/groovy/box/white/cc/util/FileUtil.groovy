package box.white.cc.util

/**
 * @author seri
 */
class FileUtil {

	/**
	 * @param path
	 * @return
	 */
	static LinkedHashSet<String> readLinesExcludeBlank(String path) {
		LinkedHashSet<String> lines = []
		new File(path).eachLine('UTF-8') {
			if (it) {
				lines << it
			}
		}
		lines
	}

	/**
	 * @param lines
	 * @return
	 */
	static LinkedHashSet<String> skipCommentLine(LinkedHashSet<String> lines) {
		LinkedHashSet<String> skippedLines = lines.collect()
		skippedLines.removeAll { it.startsWith("#") || it.startsWith("//") }
		skippedLines
	}
}
