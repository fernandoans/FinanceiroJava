package decus.com;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import redis.clients.jedis.Jedis;

public class RedisDAO {

  private Jedis jedis = new Jedis();
  
  private double maiorValor = Double.MIN_VALUE;
  private double menorValor = Double.MAX_VALUE;
  private double mediaPeriodo = 0.0D;
  private int numCotacoes = 0;

  public Map<String, Double> getDadosMoeda(String moeda, String tipo) { // BTC ou EUR ou USD e tipo: alta ou baixa
    Map<String, Double> ret = new TreeMap<>();
    
    // Zerar
    maiorValor = Double.MIN_VALUE;
    menorValor = Double.MAX_VALUE;
    
    Set<String> nomes = jedis.keys(moeda + "*");
    java.util.Iterator<String> it = nomes.iterator();
    String s, d;
    double v;
    while (it.hasNext()) {
      s = it.next();
      v = Double.parseDouble(jedis.hget(s, tipo));
      if (v > maiorValor) {
        maiorValor = v;
      }
      if (v < menorValor) {
        menorValor = v;
      }
      numCotacoes += 1;
      d = jedis.hget(s, "data");
      d = d.substring(3,5) + "-" + d.substring(0,2); 
      ret.put(d, v);
    }
    mediaPeriodo = (menorValor + maiorValor) / 2;
    return ret;
  }

  public String[] getAllMoedas() {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DATE, -1);
    String data = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime()); 
    Set<String> nomes = jedis.keys("*" + data);
    Set<String> moedas = new TreeSet<>();
    Iterator<String> it = nomes.iterator();
    String s;
    while (it.hasNext()) {
      s = it.next();
      moedas.add(jedis.hget(s, "sigla") + " - " + jedis.hget(s, "nome"));
    }
    
    String [] retorno = new String[moedas.size()];
    int indice = 0;
    for (String moeda: moedas) {
      retorno[indice++] = moeda;
    }
    return retorno;
  }
  
  // Gets

  public double getMaiorValor() {
    return maiorValor;
  }

  public double getMenorValor() {
    return menorValor;
  }

  public int getNumCotacoes() {
    return numCotacoes;
  }

  public double getMediaPeriodo() {
    return mediaPeriodo;
  }
}
