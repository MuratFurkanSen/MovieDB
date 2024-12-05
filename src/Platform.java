public class Platform {
    private String name;
    private String[] availableCountries;
    public Platform(String name, String availableCountries) {
        this.name = name;
        this.availableCountries = availableCountries.split(",");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAvailableCountries() {
        return availableCountries;
    }

    public void setAvailableCountries(String[] availableCountries) {
        this.availableCountries = availableCountries;
    }
}
