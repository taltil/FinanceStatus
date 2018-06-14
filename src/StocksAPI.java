import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * from IEX developer platform https://iextrading.com/developer
 */
public class StocksAPI {

    private static final String JSON_ERROR = "Error in request";
    private static final String URL_BASE = "https://ws-api.iextrading.com/1.0";

    public static double getStockPrice(String symbol) {
        String jsonResponse = "";
        double price = 0;
        URL url = createUrl(URL_BASE + "/stock/" + symbol + "/price");
        System.out.println(url);
        try {
            jsonResponse = makeHttpRequest(url);
            if (!jsonResponse.equals(JSON_ERROR)) {
                price = Double.parseDouble(jsonResponse);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return price;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else if (urlConnection.getResponseCode() >= 400) {
                jsonResponse = JSON_ERROR;
                System.out.println("Error code " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
