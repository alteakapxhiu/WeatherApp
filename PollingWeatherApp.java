package ProjektAlgoritmike;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class PollingWeatherService{
	private static final Map<String, String[]> weatherVariations = new HashMap<>();
	private final Random random = new Random();
	
	static {
		weatherVariations.put("Tirane", new String[] {"Diell, 22°C", "Vranesira", "Shiu, 18°C"});
		weatherVariations.put("Durres", new String[]{"Shiu, 19°C", "Diell, 21°C", "Vranësira, 20°C"});
	}
	
	public String fetchWeather(String qyteti) {
		String[] options = weatherVariations.getOrDefault(qyteti, new String[] {"Nuk ka te dhena"});
		int index = new Random().nextInt(options.length);
		return options[index];
	}
}

class PollingCityWeather{
	private final String qyteti;
	private String currentWeather;
	private final PollingWeatherService service;
	
	public PollingCityWeather(String qyteti, PollingWeatherService service) {
		this.qyteti = qyteti;
		this.service  = service;
	}
	
	public void startPolling(int intervalSeconds) {
		new Thread(() -> {
			while (true) {
				String newWeather = service.fetchWeather(qyteti);
				if (!newWeather.equals(currentWeather)) {
					currentWeather = newWeather;
					System.out.println("[" + qyteti + "] Moti u perditesua: " + currentWeather);
				}else {
					System.out.println("[" + qyteti + "] Nuk ka ndryshim ne mot.");
				}
				
				try {
					Thread.sleep(intervalSeconds * 1000L);
				}catch(InterruptedException e) {
					System.out.println("Polling nderprere.");
					Thread.currentThread().interrupt();
					break;
				}
			}
		}).start();
	}
}


public class PollingWeatherApp {

	public static void main(String[] args) {
		PollingWeatherService service = new PollingWeatherService();
		PollingCityWeather qyteti = new PollingCityWeather("Tirane", service);
		
		System.out.println("Fillon polling per motin ne Tirane cdo 5 sekonda...\n");
		qyteti.startPolling(5);

	}

}