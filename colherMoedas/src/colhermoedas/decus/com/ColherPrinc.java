package colhermoedas.decus.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import redis.clients.jedis.Jedis;

public class ColherPrinc {
  
  public static void main(String[] args) {
    new ColherPrinc().obterDados();
  }

  private void obterDados() {
    InputStream is;
    try {
      is = new URL("https://economia.awesomeapi.com.br/last/USD-BRL,EUR-BRL,BTC-BRL").openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      String jsonTxt = readAll(br);
      is.close();
      JSONObject json = new JSONObject(jsonTxt);
      gravarDados(json);
    } catch (IOException e) {
      System.out.println("Erro: " + e.getMessage());
    }
  }
  
  private String readAll(BufferedReader br) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = br.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }
  
  private void gravarDados(JSONObject json) {
    // Campos a gravar
    String chave = "";
    String sigla = "";
    String nome = "";
    String alta = "";
    String baixa = "";
    String data = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    
    String [] valores = new String[] {"USDBRL", "EURBRL", "BTCBRL"};
    JSONObject obj;
    for (String val: valores) {
      obj = json.getJSONObject(val);
      sigla = obj.getString("code");
      chave = sigla + data;
      nome = obj.getString("name");
      alta = obj.getString("high");
      baixa = obj.getString("low");
      colocarRedis(chave, data, sigla, nome, alta, baixa);
    }
  }

  private void colocarRedis(String chave, String data, String sigla, String nome, String alta, String baixa) {
    Jedis jedis = new Jedis();
    jedis.hset(chave, "data", data);
    jedis.hset(chave, "sigla", sigla);
    jedis.hset(chave, "nome", nome);
    jedis.hset(chave, "alta", alta);
    jedis.hset(chave, "baixa", baixa);
    jedis.close();
  }

}
