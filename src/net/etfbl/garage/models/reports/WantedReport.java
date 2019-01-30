package net.etfbl.garage.models.reports;

import net.etfbl.garage.application.UserGarageSimulator;
import net.etfbl.garage.models.Vehicle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;

public class WantedReport implements Serializable {
    private BufferedImage vehiclePhoto;
    private String vehicleRegistration;
    private long time = System.currentTimeMillis();

    public WantedReport(Vehicle first) {
        if(first.getPhoto()!=null) {
            try {
                vehiclePhoto = ImageIO.read(first.getPhoto());
            } catch (IOException e) {
                UserGarageSimulator.logError(Level.INFO, "IOException while reading photo during the creation of " +
                        "WantedReport.");
            }
        }
        vehicleRegistration = first.getRegistration();
    }

    public void saveReport() {
        String path = "Arrests" + File.separator + this.vehicleRegistration + "_WANTED" + ".ser";
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
                    "in WantedReport.java");
        } catch (IOException e) {
            UserGarageSimulator.logError(Level.INFO, "IOException in saveReport() method " +
                    "in WantedReport.java");
        }
    }
}