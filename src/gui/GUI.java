package gui;

import logic.Board;
import logic.Operator;
import logic.Turn;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 20.11
 * To change this template use File | Settings | File Templates.
 */
public class GUI extends JFrame{

	private final Operator operator;
	private Board board;
	private JLabel[][] labels;
	private JLabel player1;
	private JLabel player2;
	private JLabel title;
	private JLabel winnerLabel;
	private JButton restart;

	Container con = null;
    JPanel panelBgImg;
	JPanel panelContent;

	public GUI(Operator operator){

        con = getContentPane();

        con.setLayout(null);
        ImageIcon imh = new ImageIcon("images/background.jpg");
        setSize(imh.getIconWidth(), imh.getIconHeight());

        panelBgImg = new JPanel()
        {
            public void paintComponent(Graphics g)
            {
                Image img = new ImageIcon("images/background.jpg").getImage();
                Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
                setPreferredSize(size);
                setMinimumSize(size);
                setMaximumSize(size);
                setSize(size);
                setLayout(null);
                g.drawImage(img, 0, 0, null);
            }
        };

        con.add(panelBgImg);
        panelBgImg.setBounds(0, 0, imh.getIconWidth(), imh.getIconHeight());

		panelContent = new JPanel();
		panelContent.setOpaque(false);
		JPanel dummyPanel = new JPanel();
		dummyPanel.add(new JLabel("                                                                                  " +
				"     "));
		JPanel winnerPanel = new JPanel();
		winnerPanel.setOpaque(false);
		winnerPanel.setLayout(new GridLayout(2,1));
		winnerLabel = new JLabel("              ", JLabel.CENTER);
		winnerLabel.setForeground(Color.GREEN);
		Font font2 = new Font("Comic Sans MS", Font.BOLD, 14);
		winnerLabel.setFont(font2);

		dummyPanel.setOpaque(false);
		title = new JLabel("Askeladden", JLabel.CENTER);
		Font font = new Font("Serif", Font.BOLD, 40);
		title.setFont(font);
		title.setForeground(new Color(0.4f,0.4f,1f));

		this.operator = operator;
		this.board = operator.board;
		panelContent.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 15;
		c.ipady = 15;

		c.gridy = 0;
		c.gridwidth = 15;
		panelContent.add(title, c);

		restart = new JButton("    Restart    ");
		restart.setFocusable(false);
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							GUI.this.operator.restartGame();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				t.start();
			}
		});
		c.gridwidth = 2;
		c.gridy = 2;

		c.fill = GridBagConstraints.VERTICAL;
		player1 = new JLabel("Knut: 000", JLabel.CENTER);
		player1.setBackground(Color.GREEN);
		player1.setOpaque(true);
		panelContent.add(player1, c);
		c.gridx = 7;
		c.weightx = 6;
		winnerPanel.add(winnerLabel);
		winnerPanel.add(restart);
		panelContent.add(winnerPanel, c);
		c.weightx = 1;
		c.gridx = 13;
		player2 = new JLabel("Ola: 000", JLabel.CENTER);
		player2.setBackground(Color.RED);
		player2.setOpaque(true);
		panelContent.add(player2, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 15;
		c.gridx = 0;
		c.gridy = 3;
		panelContent.add(dummyPanel, c);

		c.gridy = 4;
		c.gridheight = 15;

		JPanel gamelPanel = new JPanel();
		gamelPanel.setOpaque(false);
		GridLayout gridLayout = new GridLayout(15,15);
		gamelPanel.setLayout(gridLayout);

		labels = new JLabel[15][15];
		for(int i = 0; i < 15; i++){
			for (int j = 0; j < 15; j++){
				labels[i][j]= new JLabel(" ", JLabel.CENTER);
				labels[i][j].setFont(new Font("truetype", Font.BOLD, 16));
				labels[i][j].setOpaque(true);
				labels[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				labels[i][j].setSize(10,10);
				if(board.getField(i,j).getHotspot() == '1'){
					labels[i][j].setBackground(Color.GREEN);
				}
				else if(board.getField(i,j).getHotspot() == '2'){
					// Light blue
					labels[i][j].setBackground(new Color(0.4f,0.4f,1f));
				}
				else if(board.getField(i,j).getHotspot() == '3'){
					labels[i][j].setBackground(Color.MAGENTA);
				}
				else if(board.getField(i,j).getHotspot() == '4'){
					labels[i][j].setBackground(Color.RED);
				}
				gamelPanel.add(labels[i][j]);
			}
		}
		panelContent.add(gamelPanel, c);

		panelBgImg.add(panelContent);

        panelBgImg.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setName("Scrabble");
		setVisible(true);
	}

    public void update() {
	    for(int i = 0; i < 15; i++){
			for (int j = 0; j < 15; j++){
				labels[i][j].setText("" + operator.board.getField(i,j).getLetter());
			}
		}
	    player1.setText("Knut: " + expandNumber(operator.playerA.getScore()));
	    player2.setText("Ola: " + expandNumber(operator.playerB.getScore()));
	    if(operator.turn == Turn.PLAYER_A){
		    player1.setBackground(Color.GREEN);
		    player2.setBackground(Color.RED);
	    }
	    else {
		    player2.setBackground(Color.GREEN);
		    player1.setBackground(Color.RED);
	    }
	    winnerLabel.setText("              ");
	    restart.setEnabled(false);
    }

	public void finished(){
		if(operator.playerA.getScore() > operator.playerB.getScore()){
		    winnerLabel.setText("<--- Knut Wins!");
			player1.setBackground(Color.GREEN);
		    player2.setBackground(Color.RED);
		}
		else if(operator.playerA.getScore() < operator.playerB.getScore()){
			winnerLabel.setText("Ola Wins! --->");
			player2.setBackground(Color.GREEN);
		    player1.setBackground(Color.RED);
		}
		else{
			winnerLabel.setText("Draw!");
			player2.setBackground(Color.ORANGE);
		    player1.setBackground(Color.ORANGE);
		}
		player2.validate();
		player1.validate();
		restart.setEnabled(true);
	}

	private String expandNumber(int score){
		String number = "";
		if(new String("" + score).length() == 1){
			 number = "00" + score;
		}
		else if (new String("" + score).length() == 2){
			 number = "0" + score;
		}
		else{
			 number = "" + score;
		}
		return number;
	}

	public static void main(String[] args) {
        new GUI(new Operator());
    }
}
