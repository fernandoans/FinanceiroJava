package decus.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

public class ColherPrinc {

  public static void main(String[] args) {
    new ColherPrinc().obterDados();
  }

  private void obterDados() {
    InputStream is;
    try {
      is = new URL(
          "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=15&CMC_PRO_API_KEY=49a4bb16-ff0e-431d-bce7-ea8261826b8f")
              .openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      gravarDados(json);
      is.close();
    } catch (IOException e) {
      System.out.println("Erro: " + e.getMessage());
    } catch (JSONException e) {
      System.out.println("Erro: " + e.getMessage());
    }
  }

  public String recurseKeys(JSONObject jObj, String findKey) throws JSONException {
    String finalValue = "";
    if (jObj == null) {
      return "";
    }

    Iterator<String> keyItr = jObj.keys();
    Map<String, String> map = new HashMap<>();

    while (keyItr.hasNext()) {
      String key = keyItr.next();
      map.put(key, jObj.getString(key));
    }

    for (Map.Entry<String, String> e : (map).entrySet()) {
      String key = e.getKey();
      if (key.equalsIgnoreCase(findKey)) {
        return jObj.getString(key);
      }

      // read value
      Object value = jObj.get(key);

      if (value instanceof JSONObject) {
        finalValue = recurseKeys((JSONObject) value, findKey);
      }
    }

    // key is not found
    return finalValue;
  }

  private void gravarDados(JSONObject json) {
    String chave = "";
    String nome = ""; 
    String sigla = "";
    String valor = "";
    String vol24 = "";
    String data = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    JSONArray array = json.getJSONArray("data");
    for (int i = 0; i < array.length(); ++i) {
      JSONObject rec = array.getJSONObject(i);
      nome = rec.getString("name");
      sigla = rec.getString("symbol");
      JSONObject quote = rec.getJSONObject("quote");
      JSONObject dolar = quote.getJSONObject("USD");
      valor = "" + dolar.getDouble("price");
      vol24 = "" + dolar.getDouble("volume_24h");
      colocarRedis(sigla+data, nome, sigla, valor, vol24, data);
    }
  }    

  private String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  /**
   * Adicionar um valor no REDIS
   * 
   * @param chave - montagem de Symbol + price
   * @param nome  - name
   * @param sigla - symbol
   * @param valor - price
   * @param data  - timestamp
   */
  private void colocarRedis(String chave, String nome, String sigla, String valor, String vol24, String data) {
    Jedis jedis = new Jedis();
    jedis.hset(chave, "nome", nome);
    jedis.hset(chave, "sigla", sigla);
    jedis.hset(chave, "valor", valor);
    jedis.hset(chave, "vol24", vol24);
    jedis.hset(chave, "data", data);
    jedis.close();
  }
}
