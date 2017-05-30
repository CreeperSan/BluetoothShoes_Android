package creeper_san.myshoes.event;

public class TempConfigResultEvent {
    private final boolean state;
    private final int temp;

    public TempConfigResultEvent(boolean state, int temp) {
        this.state = state;
        this.temp = temp;
    }

    public boolean isState() {
        return state;
    }

    public int getTemp() {
        return temp;
    }
}
