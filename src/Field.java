/**
 * Created by IntelliJ IDEA.
 * User: PalCSNjolstad
 * Date: 16.11.11
 * Time: 21.03
 * To change this template use File | Settings | File Templates.
 */
public class Field {

    /**
     * 1 = double letter bonus
     * 2 = triple letter bonus
     * 3 = double word bonus
     * 4 = triple word bonus
     */

    private char hotspot;
    private char letter;

    public Field(char hotspot) {
        // chars ' ' denotes empty fields
        this.hotspot = hotspot;
        this.letter = ' ';
    }

    public char getHotspot() {
        return this.hotspot;
    }

    public void setHotspot(char hotspot) {
        this.hotspot = hotspot;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public char getLetter(){
        return this.letter;
    }

    public String toString() {
        if (this.getLetter() != ' ') {
            return Character.toString(this.getLetter());
        } else {
            return Character.toString(this.getHotspot());
        }
    }

}
