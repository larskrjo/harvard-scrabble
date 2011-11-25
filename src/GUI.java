import javax.swing.*;
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
		this.operator = operator;
		this.board = operator.board;
		getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.ipadx = 10;
		c.ipady = 40;

		c.gridwidth = 6;
		c.gridx = 2;
		c.gridy = 0;
		player1 = new JLabel("Player 1");
		player1.setBackground(Color.GREEN);
		player1.setOpaque(true);
		add(player1, c);
		c.gridx = 9;
		player2 = new JLabel("Player 2");
		player2.setOpaque(true);
		add(player2, c);

		c.gridwidth = 1;
		c.ipady = 10;

		labels = new JLabel[15][15];
		for(int i = 0; i < 15; i++){
			c.gridy = i+1;
			for (int j = 0; j < 15; j++){
				labels[i][j]= new JLabel("_", JLabel.CENTER);
				labels[i][j].setOpaque(true);
				if(board.getField(i,j).getHotspot() == '1'){
					labels[i][j].setBackground(Color.GREEN);
				}
				if(board.getField(i,j).getHotspot() == '2'){
					labels[i][j].setBackground(Color.BLUE);
				}
				if(board.getField(i,j).getHotspot() == '3'){
					labels[i][j].setBackground(Color.MAGENTA);
				}
				if(board.getField(i,j).getHotspot() == '4'){
					labels[i][j].setBackground(Color.RED);
				}
				c.gridx = j;
				add(labels[i][j], c);
			}
		}
		setSize(600,500);
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
    }

	public static void main(String[] args) {
        new GUI(new Operator());

    }
}
