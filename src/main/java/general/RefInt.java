package general;

public class RefInt {
    private int value;

    public RefInt(int f) {
        value = f;
    }

    public RefInt(double f) {
        value = (int) f;
    }

    public int get() {
        return value;
    }

    public void set(int f) {
        value = f;
    }

    public void add(int f) {
        value += f;
    }

    public void multiply(int f) {
        value *= f;
    }

    @Override
    public String toString() {
        return "RefInt{"
                + "value=" + value
                + '}';
    }
}
