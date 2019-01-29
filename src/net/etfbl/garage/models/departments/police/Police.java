package net.etfbl.garage.models.departments.police;

public interface Police {
    String getDepartmentString();

    String getSymbol();

    void detectWanted();

    void readWantedFile();
}
