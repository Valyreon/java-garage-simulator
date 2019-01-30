package net.etfbl.garage.models.reports;

import net.etfbl.garage.application.UserGarageSimulator;
import net.etfbl.garage.models.Vehicle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;

public class AccidentReport implements Serializable {
    private BufferedImage firstInvolvedPhoto = null, secondInvolvedPhoto = null;
    private String firstRegistration, secondRegistration;
    private long time = System.currentTimeMillis();

    public AccidentReport(Vehicle first, Vehicle second) {
        if(firstInvolvedPhoto!=null) {
            try {
                firstInvolvedPhoto = ImageIO.read(first.getPhoto());
            } catch (Exception e) {
                UserGarageSimulator.logError(Level.INFO, "Exception while reading first photo during the creation of " +
                        "Report.");
            }
        }
        if(secondInvolvedPhoto!=null) {
            try {
                secondInvolvedPhoto = ImageIO.read(second.getPhoto());
            } catch (Exception e) {
                UserGarageSimulator.logError(Level.INFO, "Exception while reading second photo during the creation of " +
                        "Report.");
            }
        }
        firstRegistration = first.getRegistration();
        secondRegistration = second.getRegistration();
    }

    public void saveReport() {
        String path = "Accidents" + File.separator + this.firstRegistration + "_CRASH" + ".ser";
        try {
            File f = new File(path);
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            fos.close();
        } catch (FileNotFoundException e) {
            UserGarageSimulator.logError(Level.INFO, "FileNotFoundException in saveReport() method " +
                    "in AccidentReport.java");
        } catch (IOException e) {
            UserGarageSimulator.logError(Level.INFO, "IOException in saveReport() method " +
                    "in AccidentReport.java");
        }
    }
}