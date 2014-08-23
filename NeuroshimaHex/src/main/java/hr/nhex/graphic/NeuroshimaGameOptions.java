package hr.nhex.graphic;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

public class NeuroshimaGameOptions extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					NeuroshimaGameOptions frame = new NeuroshimaGameOptions();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NeuroshimaGameOptions() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JToggleButton tglbtnPlayer = new JToggleButton("Player 1");
		GridBagConstraints gbc_tglbtnPlayer = new GridBagConstraints();
		gbc_tglbtnPlayer.insets = new Insets(0, 0, 5, 0);
		gbc_tglbtnPlayer.gridx = 1;
		gbc_tglbtnPlayer.gridy = 1;
		contentPane.add(tglbtnPlayer, gbc_tglbtnPlayer);

		JToggleButton tglbtnPlayer_1 = new JToggleButton("Player 2");
		GridBagConstraints gbc_tglbtnPlayer_1 = new GridBagConstraints();
		gbc_tglbtnPlayer_1.insets = new Insets(0, 0, 5, 0);
		gbc_tglbtnPlayer_1.gridx = 1;
		gbc_tglbtnPlayer_1.gridy = 3;
		contentPane.add(tglbtnPlayer_1, gbc_tglbtnPlayer_1);

		JToggleButton tglbtnPlayer_2 = new JToggleButton("Player 3");
		GridBagConstraints gbc_tglbtnPlayer_2 = new GridBagConstraints();
		gbc_tglbtnPlayer_2.insets = new Insets(0, 0, 5, 0);
		gbc_tglbtnPlayer_2.gridx = 1;
		gbc_tglbtnPlayer_2.gridy = 5;
		contentPane.add(tglbtnPlayer_2, gbc_tglbtnPlayer_2);

		JToggleButton tglbtnPlayer_3 = new JToggleButton("Player 4");
		GridBagConstraints gbc_tglbtnPlayer_3 = new GridBagConstraints();
		gbc_tglbtnPlayer_3.gridx = 1;
		gbc_tglbtnPlayer_3.gridy = 7;
		contentPane.add(tglbtnPlayer_3, gbc_tglbtnPlayer_3);
	}

}
