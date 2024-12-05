public class Media implements Comparable<Media>{
    private String url;
    private String title;
    private String type;
    private String genres;
    private int releaseYear;
    private String imdbID;
    private double imdbAverageRate;
    private int imdbNumVotes;
    private SLL<Platform> platforms = new SLL<>();

    // Constructor
    public Media(String[] data) {
        this.url = data[0].isEmpty() ? "-" : data[0];
        this.title = data[1].isEmpty() ? "-" : data[1];
        this.type = data[2].isEmpty() ? "-" : data[2];
        this.genres = data[3].replace("\"", "").isEmpty() ? "-" : data[3].replace("\"", "");
        this.releaseYear = data[4].isEmpty() ? -1 : Integer.parseInt(data[4]);
        this.imdbID = data[5];
        this.imdbAverageRate = data[6].isEmpty() ? -1.0 : Double.parseDouble(data[6]);
        this.imdbNumVotes = data[7].isEmpty() ? -1 : Integer.parseInt(data[7]);
        this.addPlatform(data[8], data[9]);
    }

    public void addPlatform(String platform, String countries) {
        this.platforms.add(new Platform(platform, countries));
    }

    public void addPlatform(Platform platform) {
        this.platforms.add(platform);
    }

    @Override
    public int compareTo(Media other) {
        return Integer.compare(this.getImdbNumVotes(), other.getImdbNumVotes());
    }

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public double getImdbAverageRate() {
        return imdbAverageRate;
    }

    public void setImdbAverageRate(double imdbAverageRate) {
        this.imdbAverageRate = imdbAverageRate;
    }

    public int getImdbNumVotes() {
        return imdbNumVotes;
    }

    public void setImdbNumVotes(int imdbNumVotes) {
        this.imdbNumVotes = imdbNumVotes;
    }

    public SLL<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(SLL<Platform> platforms) {
        this.platforms = platforms;
    }

}
