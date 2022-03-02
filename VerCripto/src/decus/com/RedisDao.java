package decus.com;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import redis.clients.jedis.Jedis;

public class RedisDao {

  private Jedis jedis = new Jedis();
  private double maiorValor = Double.MIN_VALUE;
  private double menorValor = Double.MAX_VALUE;
  private double mediaPeriodo = 0.0D;
  private int totCotacoes = 0; 
  
  public Map<String, Double> getDadosMoeda(String moeda) {
    // Zerar valores
    maiorValor = Double.MIN_VALUE;
    menorValor = Double.MAX_VALUE;
    mediaPeriodo = 0.0D;
    totCotacoes = 0;
    
    double totValor = 0.0;
    Map<String, Double> mapa = new TreeMap<>();
    Set<String> names = jedis.keys(moeda + "*");
    java.util.Iterator<String> it = names.iterator();
    String s;
    String d;
    while(it.hasNext()) {
        s = it.next();
        double v = Double.parseDouble(jedis.hget(s, "valor"));
        if (v > maiorValor) {
          maiorValor = v;
        }
        if (v < menorValor) {
          menorValor = v;
        }
        totValor += v;
        totCotacoes += 1;
        d = jedis.hget(s, "data");
        d = d.substring(3,5) + "/" + d.substring(0,3);
        mapa.put(d, v);
    }
    mediaPeriodo = totValor / ((totCotacoes == 0)?1:totCotacoes);
    return mapa;
  }

  // Pegar todas as moedas
  public String[] getAllMoedas() {
    Set<String> names = jedis.keys("*");
    Set<String> moedas = new TreeSet<>();
    java.util.Iterator<String> it = names.iterator();
    while(it.hasNext()) {
        String s = it.next();
        moedas.add(jedis.hget(s, "sigla") + " - " + jedis.hget(s, "nome"));
    }
    String [] retorno = new String[moedas.size()];
    int indice = 0;
    for (String moeda: moedas) {
      retorno[indice++] = moeda;
    }
    return retorno;
  }

  // Para obter os Valores

  public double getMaiorValor() {
    return maiorValor;
  }

  public double getMenorValor() {
    return menorValor;
  }

  public double getMediaPeriodo() {
    return mediaPeriodo;
  }

  public int getTotCotacoes() {
    return totCotacoes;
  }
}
