package logic;

/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 17.11.11
 * Time: 18.04
 * To change this template use File | Settings | File Templates.
 */
public class Tuple {

    public double first;
    public double last;

    public Tuple(double first, double last) {
        this.first = first;
        this.last = last;
    }

    public double getFirst() {
        return this.first;
    }

    public double getLast() {
        return this.last;
    }

}
