package ProjektAlgoritmike;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class WeatherController {

	private final String apiKey = "911625a1518f69fa778918f736d163f6";

	@GetMapping("/weather")
	public String getWeather(@RequestParam String city) {
		String url = String.format(
				"https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=sq",
				city, apiKey);

		RestTemplate restTemplate = new RestTemplate();
		try {
			String response = restTemplate.getForObject(url, String.class);
			return response;
		} catch (Exception e) {
			return "{\"error\": \"Nuk mund te merret moti per qytetin: " + city + "\"}";
		}
	}
}
