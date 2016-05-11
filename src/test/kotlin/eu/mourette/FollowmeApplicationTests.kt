package eu.mourette

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf(FollowmeApplication::class))
class FollowmeApplicationTests {

	@Test
	fun contextLoads() {
	}

}
