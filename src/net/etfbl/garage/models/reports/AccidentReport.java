package net.etfbl.garage.models.reports;

import net.etfbl.garage.application.UserGarageSimulator;
import net.etfbl.garage.models.Vehicle;

import java.io.*;
import java.util.logging.Level;

public class AccidentReport implements Serializable {
    private Vehicle firstInvolved, secondInvolved;
    private long time = System.currentTimeMillis();

    public AccidentReport(Vehicle first, Vehicle second) {
        firstInvolved = first;
        secondInvolved = second;
    }

    public void saveReport() {
        String path = "Accidents" + File.separator + this.firstInvolved.getVehicleName() + "CRASH" + ".ser";
        try {
            File f = new File(path);
            f.getParentFile().mkdirs();
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            fos.close();
        } catch (FileNotFoundException e) {
            UserGarageSimulator.errorLogger.log(Level.INFO, "FileNotFoundException in saveReport() method " +
                    "in AccidentReport.java");
        } catch (IOException e) {
            UserGarageSimulator.errorLogger.log(Level.INFO, "IOException in saveReport() method " +
                    "in AccidentReport.java");
        }
    }
}