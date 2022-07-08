package praktikum.services.scooter;

public class Track {

    private int track;

    public Track(int track) {
        this.track = track;
    }

    public Track() {}

    public int getOrderNumber() {
        return track;
    }

    public void setOrderNumber(int track) {
        this.track = track;
    }
}
