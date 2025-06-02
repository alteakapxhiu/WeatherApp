import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class WeatherController {

	// 🔐 Vendos këtu çelësin API nga faqja OpenWeatherMap
	private static final String API_KEY = "911625a1518f69fa778918f736d163f6";

	// Strukturë për ruajtjen e të dhënave të motit për një qytet
	static class WeatherData {
		String city;
		double currentTemp;
		Map<String, Double> forecast;

		WeatherData(String city, double currentTemp, Map<String, Double> forecast) {
			this.city = city;
			this.currentTemp = currentTemp;
			this.forecast = forecast;
		}
	}

	// 🗃️ Cache për të shmangur thirrje të përsëritura ndaj API-së
	private static final Map<String, WeatherData> weatherCache = new HashMap<>();

	// Lista e qyteteve për të cilat do të merret moti
	private static final String[] cities = {
			"Tirane", "Berat", "Pogradec", "Fier", "Prishtine", "Rome"
	};

	// 📡 Marrja e të dhënave nga API për një qytet të caktuar
	private static WeatherData fetchWeatherFromAPI(String city) {
		try {
			String urlString = "https://api.openweathermap.org/data/2.5/forecast?q=" +
					URLEncoder.encode(city, "UTF-8") +
					"&units=metric&cnt=7&appid=" + API_KEY;

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONObject json = new JSONObject(response.toString());

			// 🌡️ Marr temperatura aktuale nga 'list[0]'
			double currentTemp = json.getJSONArray("list")
					.getJSONObject(0)
					.getJSONObject("main")
					.getDouble("temp");

			// 📆 Parashikimi për 7 ditë
			Map<String, Double> forecast = new LinkedHashMap<>();
			JSONArray list = json.getJSONArray("list");
			for (int i = 0; i < list.length(); i++) {
				JSONObject item = list.getJSONObject(i);
				String dateText = item.getString("dt_txt");
				double temp = item.getJSONObject("main").getDouble("temp");

				String day = dateText.split(" ")[0]; // merr vetëm datën
				forecast.put("Dita " + (i + 1), temp); // emërto: Dita 1, Dita 2, ...
			}

			return new WeatherData(city, currentTemp, forecast);

		} catch (Exception e) {
			System.out.println("⚠️ Gabim gjatë marrjes së të dhënave për " + city + ": " + e.getMessage());

			// 🔄 Nëse ndodh gabim, kthe të dhëna të gjeneruara rastësisht
			Random rand = new Random();
			double currentTemp = 20 + rand.nextDouble() * 15;

			Map<String, Double> forecast = new LinkedHashMap<>();
			String[] days = { "E Hene", "E Marte", "E Merkure", "E Enjte", "E Premte", "E Shtune", "E Diel" };
			for (String day : days) {
				forecast.put(day, 15 + rand.nextDouble() * 15);
			}

			return new WeatherData(city, currentTemp, forecast);
		}
	}

	// 🔀 Algoritmi Quick Sort (jo-rekursiv) për renditjen e temperaturave
	public static void quickSort(double[] arr) {
		int l = 0;
		int h = arr.length - 1;
		Stack<int[]> stack = new Stack<>();
		stack.push(new int[] { l, h });

		while (!stack.isEmpty()) {
			int[] range = stack.pop();
			l = range[0];
			h = range[1];

			if (l < h) {
				int pivotIndex = partition(arr, l, h);
				if (pivotIndex - 1 > l)
					stack.push(new int[] { l, pivotIndex - 1 });
				if (pivotIndex + 1 < h)
					stack.push(new int[] { pivotIndex + 1, h });
			}
		}
	}

	// 📍 Funksioni ndihmës për Quick Sort: ndarja në baza pivot-i
	private static int partition(double[] arr, int low, int high) {
		double pivot = arr[high];
		int i = low - 1;
		for (int j = low; j < high; j++) {
			if (arr[j] <= pivot) {
				i++;
				double temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
			}
		}
		double temp = arr[i + 1];
		arr[i + 1] = arr[high];
		arr[high] = temp;
		return i + 1;
	}

	// 🔍 Kërkimi me Binary Search në një listë të renditur të ditëve
	public static int binarySearch(String[] days, String target) {
		int left = 0, right = days.length - 1;
		while (left <= right) {
			int mid = left + (right - left) / 2;
			int cmp = days[mid].compareTo(target);
			if (cmp == 0)
				return mid;
			else if (cmp < 0)
				left = mid + 1;
			else
				right = mid - 1;
		}
		return -1;
	}

	// Merr të dhënat nga cache ose i shkarkon nëse nuk janë
	private static WeatherData getWeather(String city) {
		if (!weatherCache.containsKey(city)) {
			weatherCache.put(city, fetchWeatherFromAPI(city));
		}
		return weatherCache.get(city);
	}

	// Funksioni kryesor i aplikacionit
	public static void main(String[] args) {
		System.out.println("🌤️ Weather App\n");

		List<Double> temperatures = new ArrayList<>();

		// ➕ Merr dhe shfaq të dhënat për çdo qytet
		for (String city : cities) {
			WeatherData data = getWeather(city);
			System.out.println("📍 Qyteti: " + data.city);
			System.out.println("Temperatura aktuale: " + String.format("%.1f", data.currentTemp) + "°C");
			System.out.println("Parashikimi për 7 ditë:");
			for (Map.Entry<String, Double> entry : data.forecast.entrySet()) {
				System.out.println("  " + entry.getKey() + ": " + String.format("%.1f", entry.getValue()) + "°C");
			}
			System.out.println();
			temperatures.add(data.currentTemp);
		}

		// 🌡️ Konverto në array dhe rendit temperaturat
		double[] tempsArray = new double[temperatures.size()];
		for (int i = 0; i < temperatures.size(); i++) {
			tempsArray[i] = temperatures.get(i);
		}

		System.out.println("🌡️ Temperaturat aktuale para renditjes:");
		System.out.println(Arrays.toString(tempsArray));

		quickSort(tempsArray);

		System.out.println("✅ Temperaturat pas renditjes:");
		System.out.println(Arrays.toString(tempsArray));

		// 🔎 Lejo përdoruesin të kërkojë një ditë me Binary Search
		Scanner scanner = new Scanner(System.in);
		String[] daysArray = { "E Diel", "E Enjte", "E Hene", "E Marte", "E Merkure", "E Premte", "E Shtune" };
		Arrays.sort(daysArray); // është e nevojshme që lista të jetë e renditur

		System.out.print("\n🔎 Jep një ditë për kërkim (p.sh. E Marte): ");
		String queryDay = scanner.nextLine();
		int index = binarySearch(daysArray, queryDay);

		if (index != -1) {
			System.out.println("✅ Dita '" + queryDay + "' u gjet në parashikim (pozicioni në listë: " + index + ")");
		} else {
			System.out.println("❌ Dita '" + queryDay + "' nuk u gjet në listën e parashikimit!");
		}
	}
}
