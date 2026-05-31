package com.andreibozhek.jobscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "worker.enabled=false"
)
class JobschedulerApplicationTests {

	@Test
	void contextLoads() {
	}

}
