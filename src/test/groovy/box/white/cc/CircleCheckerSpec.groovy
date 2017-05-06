package box.white.cc

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite)
@Suite.SuiteClasses([
	CircleChecker_CheckUserNameSpec,
	CircleChecker_CheckFollowSpec,
])

class CircleCheckerSpecSuite {
}