package decus.com;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

public class DashboardMoedas extends JFrame {
  
  private JComboBox<String> cbMoedas;
  private RedisDAO dao = new RedisDAO();
  private String moedaPadrao;
  private JFXPanel fxPanel;
  // --
  private JLabel lbMaior = new JLabel("0.0");
  private JLabel lbMenor = new JLabel("0.0");
  private JLabel lbTotal = new JLabel("0.0");
  private JLabel lbMedia = new JLabel("0.0");
  
  public DashboardMoedas() {
    super("Cotação de Moedas");
    JPanel pnEscolha = new JPanel(new FlowLayout(FlowLayout.LEFT));
    cbMoedas = new JComboBox<>(dao.getAllMoedas());
    moedaPadrao = String.valueOf(cbMoedas.getSelectedItem());
    JButton btProcessar = new JButton("Processar");
    btProcessar.addActionListener(e -> pegarValor());

    pnEscolha.add(new JLabel("Moedas Disponíveis: "));
    pnEscolha.add(cbMoedas);
    pnEscolha.add(btProcessar);

    JPanel pnRodape = new JPanel();
    pnRodape.setLayout(new GridLayout(2, 4));
    pnRodape.add(new JLabel("Maior Valor")); // 1 - 1
    pnRodape.add(new JLabel("Menor Valor")); // 1 - 2
    pnRodape.add(new JLabel("Número de Cotações")); // 1 - 3
    pnRodape.add(new JLabel("Valor Médio")); // 1 - 4
    pnRodape.add(lbMaior); // 2 - 1
    pnRodape.add(lbMenor); // 2 - 2
    pnRodape.add(lbTotal); // 2 - 3
    pnRodape.add(lbMedia); // 2 - 4
    
    fxPanel = new JFXPanel();
    
    this.add(pnEscolha, BorderLayout.NORTH);
    this.add(fxPanel);
    this.add(pnRodape, BorderLayout.SOUTH);
    
    this.setSize(1200, 400);
    this.setVisible(true);
    
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        iniciarFX();
      }
    });
  }
  
  private void pegarValor() {
    moedaPadrao = String.valueOf(cbMoedas.getSelectedItem());
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        iniciarFX();
      }
    });
    this.repaint();
  }

  private void iniciarFX() {
    final String moeda = moedaPadrao.substring(0, moedaPadrao.indexOf("-")-1);
    final Map<String, Double> valores = dao.getDadosMoeda(moeda, "alta");
    lbMaior.setText("" + dao.getMaiorValor());
    lbMenor.setText("" + dao.getMenorValor());
    lbTotal.setText("" + dao.getNumCotacoes());
    lbMedia.setText("" + dao.getMediaPeriodo());
    
    MoedasLineChart scatter = new MoedasLineChart(moeda, valores);
    Scene cena = scatter.getScene();
    fxPanel.setScene(cena);
  }
  
  public static void main(String[] args) {
    new DashboardMoedas().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
