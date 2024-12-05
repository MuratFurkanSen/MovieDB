import java.util.Iterator;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello World");
        SortedArray<Media> arr = new SortedArray<>(10);
        String[] data1 = new String[]{"","Anan1","","","","","1.1","7","",""};
        String[] data2 = new String[]{"","Anan2","","","","","3.2","7","",""};
        String[] data3 = new String[]{"","Anan3","","","","","0.9","7","",""};
        String[] data4 = new String[]{"","Anan4","","","","","4.9","7","",""};
        String[] data5 = new String[]{"","Anan5","","","","","2.9","7","",""};
        String[] data6 = new String[]{"","Anan6","","","","","6.4","7","",""};
        String[] data7 = new String[]{"","Anan7","","","","","6.4","7","",""};
        String[] data8 = new String[]{"","Anan8","","","","","6.4","7","",""};
        String[] data9 = new String[]{"","Anan9","","","","","6.4","7","",""};
        String[] data10 = new String[]{"","Anan10","","","","","9.5","7","",""};
        String[] data11 = new String[]{"","Anan11","","","","","9.2","7","",""};
        String[] data12 = new String[]{"","Anan12","","","","","8.4","7","",""};
        Media media1 = new Media(data1);
        Media media2 = new Media(data2);
        Media media3 = new Media(data3);
        Media media4 = new Media(data4);
        Media media5 = new Media(data5);
        Media media6 = new Media(data6);
        Media media7 = new Media(data7);
        Media media8 = new Media(data8);
        Media media9 = new Media(data9);
        Media media10 = new Media(data10);
        Media media11 = new Media(data11);
        Media media12 = new Media(data12);

        arr.add(media1);
        arr.add(media2);
        arr.add(media3);
        arr.add(media4);
        arr.add(media5);
        arr.add(media6);
        arr.add(media7);
        arr.add(media8);
        arr.add(media9);
        arr.add(media10);
        arr.add(media11);
        arr.add(media12);


        Iterator<Media> iter = arr.getIterator();
        while (iter.hasNext()) {
            System.out.println(iter.next().getTitle());
        }

    }
}
