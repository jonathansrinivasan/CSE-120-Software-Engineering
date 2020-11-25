import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class tsvpractice{
  private static Scanner sc = new Scanner(System.in);
  public static void main(String args[]) {
    // File testFile = new File("koppen_1901-2010.tsv");
    // System.out.println(testFile.exists());
    // System.out.println(testFile.canRead());

    ArrayList<ArrayList<String>> tsvFile = new ArrayList<ArrayList<String>>();
    System.out.println("Attempting to parse in .tsv file...");
    try(BufferedReader br = new BufferedReader(new FileReader("koppen_1901-2010.tsv"))) {
      System.out.println("Successfully opened .tsv file");
      String line;
      int i = 0;
      System.out.println("Attempting to put .tsv file contents into a double ArrayList...");
      while ((line = br.readLine()) != null) {
        tsvFile.add(new ArrayList<String>());
        String[] splitText = line.split("\t");
        for (String ss: splitText)
          tsvFile.get(i).add(ss);
        i++;
      }
      System.out.println("Successfully put .tsv file contents into a double ArrayList...");
      System.out.println(Float.parseFloat(tsvFile.get(1).get(0)));
      System.out.println(Float.parseFloat(tsvFile.get(1).get(1)));
      System.out.println(tsvFile.get(1).get(2));
      System.out.println("Please enter latitude: ");
      // TODO: should do some sort of simple error checking here
      float latitude = sc.nextFloat();
      System.out.println("Please enter longitude: ");
      float longitude = sc.nextFloat();

      // System.out.println("Latitude: " + latitude);
      // System.out.println("Longitude: " + longitude);

      System.out.println("Finding the most similar climate to your location...");
      for (int j = 1; j < tsvFile.size(); j++) { // we start at 1 because the 0'th item are just titles
        //System.out.println("MERP");
        float long1 = Float.parseFloat(tsvFile.get(j).get(0));
        float lat1 = Float.parseFloat(tsvFile.get(j).get(1));
        System.out.println(long1);
        System.out.println(lat1);
        if (long1 == longitude && lat1 == latitude) {
          System.out.println("Your climate region: " + tsvFile.get(j).get(2));
          break;
        }
      }
      System.out.println("Finding all documents in a similar climate...");
      // for (ArrayList<String> similarRegions: tsvFile) {
      //   float long1 = Float.parseFloat(similarRegions.get(0));
      //   float lat1 = Float.parseFloat(similarRegions.get(1));
      //   System.out.println("Latitude: " + lat1);
      //   System.out.println("Longitude: " + long1);
      //   if (long1 == longitude && lat1 == latitude) {
      //     System.out.println("Your climate region: " + similarRegions.get(2));
      //     break;
      //   }
      //
      // }

      // for (ArrayList<String> test : tsvFile) {
      //   System.out.println("YEET");
      //   for (String s : test)
      //     System.out.print(s + " ");
      //   System.out.println("");
      // }

    } catch (Exception e) {

    }
  }
}
