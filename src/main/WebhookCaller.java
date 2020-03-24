package main;

import com.google.gson.Gson;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WebhookCaller {

	private static final URI data = URI.create("https://services1.arcgis.com/0MSEUqKaxRlEPj5g/arcgis/rest/services/ncov_cases/FeatureServer/1/query?f=json&where=1%3D1&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outStatistics=%5B%7B%22statisticType%22%3A%22sum%22%2C%22onStatisticField%22%3A%22Confirmed%22%2C%22outStatisticFieldName%22%3A%22confirmed%22%7D%2C%20%7B%22statisticType%22%3A%22sum%22%2C%22onStatisticField%22%3A%22Deaths%22%2C%22outStatisticFieldName%22%3A%22deaths%22%7D%2C%20%7B%22statisticType%22%3A%22sum%22%2C%22onStatisticField%22%3A%22Recovered%22%2C%22outStatisticFieldName%22%3A%22recovered%22%7D%5D&outSR=102100&cacheHint=false");
	private static final HttpRequest dataRequest = HttpRequest.newBuilder().uri(data).GET().build();
	private static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
	private static final Gson parser = new Gson();
	
	public static void main(String [] args) {
		
		StatObject dataObject = null;
		
		// fetches stats
		try {
			dataObject = coronaStats();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// debug
		String confirmed = String.valueOf(dataObject.confirmed);
		String dead = String.valueOf(dataObject.deaths);
		String recovered = String.valueOf(dataObject.recovered);
		System.out.println("Confirmed: " + String.valueOf(dataObject.confirmed));
		System.out.println("Dead: " + String.valueOf(dataObject.deaths));
		System.out.println("Recovered: " + String.valueOf(dataObject.recovered));
		
		// sends the webhook
		DiscordWebhook webhook = new DiscordWebhook("https://discordapp.com/api/webhooks/691076444369059893/r90tvQwI7hNKH-dtvS774A7nuzxPt53FqrxiS-5XvTTtX71HmhCKN3zQy7OG7ZO0bQjO");
		webhook.setContent("Prepare to fucking die.");
		webhook.setAvatarUrl("https://upload.wikimedia.org/wikipedia/en/b/b3/Plague_Inc._app_icon.png");
		webhook.setUsername("Coronabot");
		webhook.setTts(true);
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
		        .setTitle("GLOBAL CONFIRMED CASES")
		        .setDescription(confirmed) // PUT THE CASES HERE
		        .setColor(Color.YELLOW));
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
				.setTitle("GLOBAL FATALITIES")
				.setDescription(dead)
				.setColor(Color.RED));
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
				.setTitle("GLOBAL RECOVERIES")
				.setDescription(recovered)
				.setColor(Color.GREEN));
	    try {
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	private static StatObject coronaStats() throws IOException, InterruptedException {
		
		String res = client.send(dataRequest, HttpResponse.BodyHandlers.ofString()).body();
		DataResponse datavar = parser.fromJson(res, DataResponse.class);
		System.out.println(datavar.features[0].attributes);
		return datavar.features[0].attributes;
		
	}

}
