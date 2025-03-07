import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import java.io.File;


public class Main {
    public static Scanner sc_input = new Scanner(System.in);

    // HashedDictionary Configuration
    public static int initialCapacity = 1000;
    public static String primaryHash = "PAF"; // 'SSF' or 'PAF'
    public static String collisionSolve = "DH"; // 'LP' or 'DH'
    public static double loadFactor = 0.8;


    public static HashedDictionary<String, Media> dictionary = new HashedDictionary<>(initialCapacity, primaryHash, collisionSolve, loadFactor);

    public static void main(String[] args) {

        displayMenu();

        boolean endLoop = false;
        while (!endLoop) {
            System.out.print("Pls Select an Operation: ");
            String operation = sc_input.nextLine();
            switch (operation) {
                case "1":
                    loadData();
                    break;
                case "2":
                    searchTest();
                    break;
                case "3":
                    searchMedia();
                    break;
                case "4":
                    top10();
                    break;
                case "5":
                    countrySearch();
                    break;
                case "6":
                    allPlatformMedia();
                    break;
                case "7":
                    endLoop = true;
                    break;
                default:
                    System.out.println("Invalid operation...");
                    break;
            }
        }
    }

    private static void displayMenu() {
        System.out.println("----------------MENU----------------");
        System.out.println("1.\tLoad dataset \n" +
                "2.\tRun 1000 search test \n" +
                "3.\tSearch for a media item with the ImdbId. \n" +
                "4.\tList the top 10 media according to user votes \n" +
                "5.\tList all the media streams in a given country \n" +
                "6.\tList the media items that are streaming on all 5 platforms \n" +
                "7.  Exit ");
    }

    public static void loadData()   {
        long startTime = System.currentTimeMillis();
        Scanner sc;
        try {
            sc = new Scanner(new File("movies_dataset.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("Data File Not Found...");
            return;
        }
        sc.nextLine();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (data.length < 10 || data[5].isEmpty()) {
                continue;
            }
            Media media = new Media(data);
            String key = data[5];

            Media oldMedia = dictionary.add(key, media);
            if (oldMedia != null) {
                Iterator<Platform> platformIterator = oldMedia.getPlatforms().getIterator();
                while (platformIterator.hasNext()) {
                    Platform platform = platformIterator.next();
                    dictionary.getValue(key).addPlatform(platform);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Data Loaded in " + elapsedTime + "ms");
        System.out.println("Collision Count: " + dictionary.getCollisionCount());
    }

    public static void searchTest() {
        long startTime = System.currentTimeMillis();
        int found_count = 0;
        int not_found_count = 0;
        Scanner sc;

        try {
            sc = new Scanner(new File("search.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Search File Not Found...");
            return;
        }
        while (sc.hasNextLine()) {
            String imdbID = sc.nextLine();
            if (dictionary.getValue(imdbID) != null) {
                found_count++;
            } else {
                not_found_count++;
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = (endTime - startTime);
        double averageSearchTime = elapsedTime / 1000.0;
        System.out.println(found_count + " Media Found.");
        System.out.println(not_found_count + " Media Not Found.");
        System.out.println("Average Search Time is " + averageSearchTime + "ms");

    }

    public static void searchMedia() {
        System.out.print("Enter movie ImdbId Pls:");
        String mediaID = sc_input.nextLine();
        Media media = dictionary.getValue(mediaID);
        if (media != null) {
            System.out.println(" Title: " + media.getTitle());
            System.out.println(" Type: " + media.getType());
            System.out.println(" Genre: " + media.getGenres());
            System.out.println(" Release Year: " + media.getReleaseYear());
            System.out.println(" IMDb ID: " + media.getImdbID());
            System.out.println(" Number of Votes: " + media.getImdbNumVotes());
        } else {
            System.out.println("Media Not Found...");
        }
    }

    public static void top10() {
        SortedArray<Media> top10Arr = new SortedArray<>(10);
        Iterator<Media> valueIterator = dictionary.getValueIterator();
        while (valueIterator.hasNext()) {
            top10Arr.add(valueIterator.next());
        }
        System.out.println("----------TOP 10----------");
        Iterator<Media> topIterator = top10Arr.getIterator();
        while (topIterator.hasNext()) {
            Media media = topIterator.next();
            System.out.println("Title: "+media.getTitle() + "   " + "Votes: "+ media.getImdbNumVotes());
        }
    }

    public static void countrySearch() {
        System.out.print("Enter Country Code Pls:");
        String countryCode = sc_input.nextLine().toUpperCase();
        Iterator<Media> valueIterator = dictionary.getValueIterator();
        System.out.println("-----> Media Streams In "+countryCode);
        int counter = 0;
        while (valueIterator.hasNext()) {
            Media media = valueIterator.next();
            Iterator<Platform> platformIterator = media.getPlatforms().getIterator();
            while (platformIterator.hasNext()) {
                Platform platform = platformIterator.next();
                String[] countryCodes = platform.getAvailableCountries();
                boolean found = false;
                for (String code : countryCodes) {
                    if (code.equals(countryCode)) {
                        System.out.println(media.getTitle());
                        found = true;
                        break;
                    }
                }
                if (found) {
                    counter++;
                    break;
                }
            }
        }
        System.out.println("Media Streams Found: "+counter);
    }

    public static void allPlatformMedia() {
        Iterator<Media> valueIterator = dictionary.getValueIterator();
        System.out.println(" --- > Streams that Available on All Platforms");
        int counter = 0;
        while (valueIterator.hasNext()) {
            Media media = valueIterator.next();
            if (media.getPlatforms().getSize() == 5) {
                System.out.println("Title: "+media.getTitle());
                counter++;
            }
        }
        System.out.println("Media Streams Found: "+counter);
    }
}