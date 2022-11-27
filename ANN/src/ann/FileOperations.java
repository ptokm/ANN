package ann;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class FileOperations {
    
    public boolean loadDataset(String fileName) {
        if (fileName.isEmpty())
            return false;
        
        ArrayList <ArrayList <Double>> patterns = new ArrayList<>();
        boolean isValid = true;
        int dimension = -1;
        
        try {
            FileReader file = new FileReader(fileName);
            try (Scanner in = new Scanner(file)) {
                int i = 0;
                // Read the file line-by-line
                while(in.hasNextLine())  {
                    if (isValid) {
                        String line=in.nextLine();
                  
                        if (line.startsWith("//")) // For comments
                            continue;
                        
                        ArrayList <Double> newPattern = new ArrayList<>();
                        String[] characteristics = line.split(",");
                        if (i == 0) {
                            dimension = characteristics.length;
                            for (String characteristic : characteristics) {
                                newPattern.add(Double.parseDouble(characteristic));
                            }
                            patterns.add(newPattern);
                        }else if (characteristics.length == dimension) {
                            for (String characteristic : characteristics) {
                                newPattern.add(Double.parseDouble(characteristic));
                            }
                            patterns.add(newPattern);
                        }else {
                            isValid = false;
                        }
                    }
                }
                
                if (isValid && fileName.endsWith(".train")) {
                    Data.setTrainPatterns(patterns);
                    Data.setDimension(dimension - 1); // The last column is the output class of pattern
                }
                else if (isValid && fileName.endsWith(".test"))
                    Data.setTestPatterns(patterns);
                    
                return isValid;
            }
        }catch (FileNotFoundException | NumberFormatException ex) {
            System.out.println("Cannot read " + fileName + " file");
            return false;
        }
    }
    
}
