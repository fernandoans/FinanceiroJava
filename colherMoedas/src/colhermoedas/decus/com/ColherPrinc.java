package colhermoedas.decus.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONObject;

public class ColherPrinc {
  
  public static void main(String[] args) {
    new ColherPrinc().obterDados();
  }

  private void obterDados() {
    InputStream is;
    try {
      is = new URL("https://economia.awesomeapi.com.br/last/USD-BRL,EUR-BRL,BTC-BRL").openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
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
    System.out.println(sb.toString());
    return sb.toString();
  }
  
  private void gravarDados(JSONObject json) {
    // TODO Auto-generated method stub
  }

}
