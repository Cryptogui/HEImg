package oma.a1;


import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

/**
 * @author Max
 * @version 17.3.2017
 *
 */
public class Kuvanpoisto {

    /**
     * @param args //
     * @throws InterruptedException //
     */
    public static void main(String[] args) throws InterruptedException {
        String path1 = args[0];
        System.out.print("Tiedostonimet: ");
        List<String> nimet = tiedostoNimet(path1);
        System.out.print("Tallenna kuvakoot: ");
        List<Dimension> koot = tallennaKuvakoot(path1);
        System.out.println("Valitaan poistettavat kuvat:");
        nimet = poistaListalta(nimet, koot);
        poistaTiedostot(nimet, path1);
        System.out.println("Valmis!");
        System.exit(0);
    }
    
    /**
     * @param path1 Path of the folder to be read
     * @return returns ArrayList with all filenames in the folder
     */
    public static List<String> tiedostoNimet(String path1){
    
        List<String> results = new ArrayList<String>();


        File[] files = new File(path1).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null. 

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        System.out.println("Valmis!");
        return results;
    }
    
    /**
     * @param path1 Path of the folder to be read
     * @return returns ArrayList<Dimension> with all resolutions of the images in the folder.
     */
    public static List<Dimension> tallennaKuvakoot(String path1) {
        File[] images = new File(path1).listFiles();
        List<Dimension> koot = new ArrayList<Dimension>();
        
        for (File image : images) {
            try {
                koot.add(getImageDimension(image));
            } catch (IOException e) {
                System.err.println("Virhe: tallennaKuvakoot IOEx.");
                e.printStackTrace();
            }
        }
        System.out.println("Valmis!");
        return koot;
    }
    
    /**
     * Gets image dimensions for given file 
     * @param imgFile image file
     * @return dimensions of image
     * @throws IOException if the file is not a known image
     */
    public static Dimension getImageDimension(File imgFile) throws IOException {
      int pos = imgFile.getName().lastIndexOf(".");
      if (pos == -1)
        throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
      String suffix = imgFile.getName().substring(pos + 1);
      Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
      while(iter.hasNext()) {
        ImageReader reader = iter.next();
        try {
          ImageInputStream stream = new FileImageInputStream(imgFile);
          reader.setInput(stream);
          int leveys = reader.getWidth(reader.getMinIndex());
          int korkeus = reader.getHeight(reader.getMinIndex());
          stream.close();
          return new Dimension(leveys, korkeus);
        } catch (IOException e) {
          System.err.printf("Error reading: " + imgFile.getAbsolutePath(), e);
        } finally {
          reader.dispose();
        }
      }

      throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
    }
    
    /**
     * @param nimet ArrayList<String> filenames
     * @param koot ArrayList<Dimension> dimensions
     * @return returns modified ArrayList<String> nimet
     * @throws InterruptedException //
     */
    public static List<String> poistaListalta(List<String> nimet, List<Dimension> koot) throws InterruptedException {
        int summa = nimet.size();
        for(int i = 0; i < nimet.size(); i++){
           int korkeus = koot.get(i).height;
           int leveys = koot.get(i).width;
           if (korkeus == 1944 && leveys == 2592) {
               koot.remove(i);
               nimet.remove(i);
               i--;
           }
           System.out.print((i + 1) + "/" + summa + "\r");
        }
        System.out.println("Valmis!          ");
        return nimet;
    }

    /**
     * @param nimet Filenames of the files to be removed
     * @param path path of the target folder
     */
    public static void poistaTiedostot(List<String> nimet, String path) {
        Scanner reader = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("Haluatko varmasti poistaa " + nimet.size() + " tiedostoa? (Y/N)");
                String in = reader.nextLine();
                if (Objects.equals(in.toUpperCase(), "Y")) {
                    System.out.println("poistetaan:");
                    for (String x : nimet) {
                        try {
                            File f = new File(path + "\\" + x);
                            f.delete();
                        } catch(Exception e) {
                            System.err.println("Uh.. Oh.. Something went wrong when deleting: " + x);
                        }
                    }
                    System.out.println("Tiedostot poistettu.");
                    System.out.println("-Program terminated-");
                    System.exit(0);
                }
                else if (Objects.equals(in.toUpperCase(), "N")) {
                    System.out.println("-Program terminated-");
                    System.exit(0);
                }
                else {
                    System.out.println("Invalid input, Try again.");
                }
            }
        } catch(IllegalStateException | NoSuchElementException e) {
            System.out.println("System.in was closed); exiting");
            System.exit(0);
        }
    }
}
