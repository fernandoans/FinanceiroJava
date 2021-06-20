package decus.com;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
  private static final long serialVersionUID = 1L;
  private RedisDao dao = new RedisDao();
  private JComboBox<String> cbMoedas;
  private String moedaPadrao;
  private JFXPanel fxPanel;
  
  private JLabel lbMaior = new JLabel("0.0");
  private JLabel lbMenor = new JLabel("0.0");
  private JLabel lbTotal = new JLabel("0");
  private JLabel lbMedia = new JLabel("0.0");
  
  public DashboardMoedas() {
    super("Cotação das CriptoMoedas");

    JPanel pnEscolha = new JPanel(new FlowLayout(FlowLayout.LEFT));
    cbMoedas = new JComboBox<>(dao.getAllMoedas());
    moedaPadrao = String.valueOf(cbMoedas.getSelectedItem());
    pnEscolha.add(new JLabel("CriptoMoedas disponíveis: "));
    pnEscolha.add(cbMoedas);
    JButton btProcessar = new JButton("Processar");
    btProcessar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        pegarValor();
      }
    });
    pnEscolha.add(btProcessar);
    this.add(pnEscolha, BorderLayout.NORTH);
    
    JPanel pnRodape = new JPanel();
    pnRodape.setLayout(new GridLayout(2,4));
    pnRodape.add(new JLabel("Maior Valor"));
    pnRodape.add(new JLabel("Menor Valor"));
    pnRodape.add(new JLabel("Total Cotações"));
    pnRodape.add(new JLabel("Valor Médio"));
    pnRodape.add(lbMaior);
    pnRodape.add(lbMenor);
    pnRodape.add(lbTotal);
    pnRodape.add(lbMedia);
    this.add(pnRodape, BorderLayout.SOUTH);
    
    
    fxPanel = new JFXPanel();

    this.setSize(1200, 400);
    this.add(fxPanel);
    this.setVisible(true);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        iniciarFX(fxPanel);
      }
    });
  }
  
  private void pegarValor() {
    moedaPadrao = String.valueOf(cbMoedas.getSelectedItem());
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        iniciarFX(fxPanel);
      }
    });
    this.repaint();
  }

  private void iniciarFX(JFXPanel fxPanel) {
    final String moeda = moedaPadrao.substring(0, moedaPadrao.indexOf("-")-1);
    final Map<String, Double> valores = dao.getDadosMoeda(moeda);
    lbMaior.setText("" + dao.getMaiorValor());
    lbMenor.setText("" + dao.getMenorValor());
    lbTotal.setText("" + dao.getTotCotacoes());
    lbMedia.setText("" + dao.getMediaPeriodo());

    MoedasLineChart scatter = new MoedasLineChart(moedaPadrao, valores);
    Scene cena = scatter.getScene();
    fxPanel.setScene(cena);
  }

  public static void main(String [] args) {
    new DashboardMoedas().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

}
