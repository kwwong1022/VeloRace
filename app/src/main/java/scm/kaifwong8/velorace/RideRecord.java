package scm.kaifwong8.velorace;

public class RideRecord {
    private int sec;
    private float balance;
    private float elev;
    private float speed;

    public RideRecord(int sec, float balance, float elev, float speed) {
        this.sec = sec;
        this.balance = balance;
        this.elev = elev;
        this.speed = speed;
    }

    public int getSec() {
        return sec;
    }

    public float getBalance() {
        return balance;
    }

    public float getElev() {
        return elev;
    }

    public float getSpeed() {
        return speed;
    }
}
