import java.io.FileNotFoundException;
import java.util.Arrays;
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


    public static HashedDictionary<String, Media> dictionary = new HashedDictionary<>(initialCapacity,primaryHash,collisionSolve, loadFactor);

    public static void main(String[] args) throws FileNotFoundException {

        /*
        1.	Load dataset ✅
        2.	Run 1000 search test ✅
        3.	Search for a media item with the ImdbId. ✅
        4.	List the top 10 media according to user votes ✅
        5.	List all the media streams in a given country ✅
        6.	List the media items that are streaming on all 5 platforms ✅
        7.  Exit ✅
         */
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

    public static void loadData() throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        Scanner sc = new Scanner(new File("movies_dataset.csv"));
        sc.nextLine();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (data[5].isEmpty()) {
                continue;
            }
            Media media = new Media(data);
            String key = data[5];

            Media oldData = dictionary.add(key, media);
            if (oldData != null) {
                Iterator<Platform> platformIterator = oldData.getPlatforms().getIterator();
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

    public static void searchTest() throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        int found_count = 0;
        int not_found_count = 0;
        Scanner sc = new Scanner(new File("search.txt"));
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
        System.out.println("Average Search Time is " + averageSearchTime +"ms");

    }

    public static void searchMedia() {
        System.out.print("Enter movie ImdbId Pls:");
        String mediaID = sc_input.nextLine();
        Media media = dictionary.getValue(mediaID);
        if (media != null) {
            System.out.println("Media Title: " + media.getTitle());
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
        Iterator<Media> topIterator = top10Arr.getIterator();
        while (topIterator.hasNext()) {
            Media media = topIterator.next();
            System.out.println(media.getTitle());
        }
    }

    public static void countrySearch() {
        System.out.println("Enter country Pls:");
        String countryCode = sc_input.nextLine().toUpperCase();
        Iterator<Media> valueIterator = dictionary.getValueIterator();
        while (valueIterator.hasNext()) {
            Media media = valueIterator.next();
            Iterator<Platform> platformIterator = media.getPlatforms().getIterator();
            while (platformIterator.hasNext()) {
                Platform platform = platformIterator.next();
                if (Arrays.asList(platform.getAvailableCountries()).contains(countryCode)) {
                    System.out.println(media.getTitle());
                    break;
                }
            }
        }
    }

    public static void allPlatformMedia() {
        Iterator<Media> valueIterator = dictionary.getValueIterator();
        while (valueIterator.hasNext()) {
            Media media = valueIterator.next();
            if (media.getPlatforms().getSize() == 5) {
                System.out.println(media.getTitle());
            }
        }
    }
}