import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

public class CryptoApi {

    private static final String KEY_NAME = "X-CoinAPI-Key";
    private static final String JSON_ERROR = "Error in request";
    private static final String URL_BASE = "https://rest.coinapi.io/";

    private static String getKey() {
        String filename = "token.txt";
        String key = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            key = bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
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

    public static Asset[] getAssets() {
        String jsonResponse = "";
        Asset[] results;
        URL url = createUrl(URL_BASE + "v1/assets");
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.equals(jsonResponse, JSON_ERROR)) {
            //error
            return null;
        } else {
            try {
                JSONArray array = new JSONArray(jsonResponse);

                results = new Asset[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    String assetId = array.getJSONObject(i).getString("asset_id");
                    String name = array.getJSONObject(i).optString("name", null);
                    boolean typIsCrypto = array.getJSONObject(i).getInt("type_is_crypto") != 0;
                    results[i] = new Asset(assetId, name, typIsCrypto);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return results;
        }
    }

    public static ExchangeRate getExchangeRate(String assetIdBase, String assetIdQuote) {
        String jsonResponse = "";
        ExchangeRate result;
        //can add time inside the url if needed
        URL url = createUrl(URL_BASE + "v1/exchangerate/" + assetIdBase + "/" + assetIdQuote);
        System.out.println(url);
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.equals(jsonResponse, JSON_ERROR)) {
            return null;
        } else {
            try {
                JSONObject object = new JSONObject(jsonResponse);
                assetIdBase = object.getString("asset_id_base");
                assetIdQuote = object.getString("asset_id_quote");
                String time = object.getString("time");
                double rate = object.getDouble("rate");
                result = new ExchangeRate(time, assetIdBase, assetIdQuote, rate);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return result;
        }
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty(KEY_NAME, getKey());
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else if (urlConnection.getResponseCode() >= 400) {
                jsonResponse = JSON_ERROR;
                System.out.println("Error code " + urlConnection.getResponseCode());
                //error
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
