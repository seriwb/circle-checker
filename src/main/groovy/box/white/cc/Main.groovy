package box.white.cc

import groovy.util.logging.Slf4j

@Slf4j
class Main {

	static main(args) {
	
		def bs = new BootStrap()
		def config = bs.init()

		def cc = new CircleChecker(config)
		cc.execute()
	}

}
