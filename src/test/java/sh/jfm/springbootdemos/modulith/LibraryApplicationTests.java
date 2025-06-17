package sh.jfm.springbootdemos.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/// Integration tests for the Library Application.
/// Tests are executed with a real application context and a random server port.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	/// Verifies that the Spring application context loads successfully.
	/// This test will fail if there are any issues with component scanning,
	/// autoconfiguration, or bean creation.
	@Test
	void contextLoads() {
	}

	/// Tests the Spring Boot Actuator health endpoint.
	/// Verifies that the endpoint returns HTTP 200 status code
	/// and contains "UP" in the response body, indicating that
	/// the application is healthy and running properly.
	@Test
	void healthEndpointReturns200AndUp() {
		ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
		
		assertThat(response.getStatusCode().value()).isEqualTo(200);
		assertThat(response.getBody()).contains("UP");
	}
}
