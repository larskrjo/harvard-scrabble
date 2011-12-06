package gui;

import logic.Board;
import logic.Operator;
import logic.Turn;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: havard_normann
 * Date: 16.11.11
 * Time: 20.11
 * To change this template use File | Settings | File Templates.
 */
public class GUI extends JFrame{

	private Operator operator;
	private Board board;
	private JLabel[][] labels;
	private JLabel player1;
	private JLabel player2;

	public GUI(Operator operator){
		JPanel dummyPanel = new JPanel();
		dummyPanel.add(new JLabel("                                                                      "));

		this.operator = operator;
		this.board = operator.board;
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setMinimumSize(new Dimension(600,600));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 15;
		c.ipady = 15;

		c.gridwidth = 6;
		c.gridheight = 1;
		c.gridx = 2;
		c.gridy = 0;
		player1 = new JLabel();
		player1.setBackground(Color.GREEN);
		player1.setOpaque(true);
		add(player1, c);
		c.gridx = 9;
		player2 = new JLabel();
		player2.setBackground(Color.RED);
		player2.setOpaque(true);
		add(player2, c);
		player1.setText("Player 1, Score: 000");
	    player2.setText("Player 2, Score: 000");

		c.gridx = 0;
		c.gridwidth = 15;
		c.gridy = 1;
		add(dummyPanel, c);

		c.gridy = 2;
		c.gridheight = 15;

		JPanel gamelPanel = new JPanel();
		GridLayout gridLayout = new GridLayout(15,15);
		gamelPanel.setLayout(gridLayout);

		labels = new JLabel[15][15];
		for(int i = 0; i < 15; i++){
			for (int j = 0; j < 15; j++){
				labels[i][j]= new JLabel(" ", JLabel.CENTER);
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

		add(gamelPanel, c);
		setSize(600,600);
		setDefaultLookAndFeelDecorated(true);
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
	    player1.setText("Player 1, Score: " + expandNumber(operator.playerA.getScore()));
	    player2.setText("Player 2, Score: " + expandNumber(operator.playerB.getScore()));
	    if(operator.turn == Turn.PLAYER_A){
		    player1.setBackground(Color.GREEN);
		    player2.setBackground(Color.RED);
	    }
	    else {
		    player2.setBackground(Color.GREEN);
		    player1.setBackground(Color.RED);
	    }
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
