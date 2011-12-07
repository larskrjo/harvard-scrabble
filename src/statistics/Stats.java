package statistics;

/**
 * Created by IntelliJ IDEA.
 * User: larsen
 * Date: 12/7/11
 * Time: 1:03 AM
 */
public class Stats {

	int totalScore = 0;
	int count = 0;

	public void updateScore(int score){
		synchronized (this){
			totalScore += score;
			count++;
		}
	}

	public int getAvgScore(){
		int avg = 0;
		synchronized (this){
			if(count != 0)
				avg = totalScore/count;
		}
		return avg;
	}
}
