package Classes;

import Loaders.ProjectLoader;
import Utilities.Searchable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Project implements Searchable {
    private String projectName;
    private String neighborhood;
    private String type1;
    private int numUnitsType1;
    private double priceType1;
    private String type2;
    private int numUnitsType2;
    private double priceType2;
    private LocalDate applicationOpenDate;
    private LocalDate applicationCloseDate;
    private String manager;
    private String managerNRIC;
    private int officerSlot;
    private String officer;
    private String officerNRIC;
    private Visibility visibility;

    public enum Visibility {
        ON,
        OFF
    }

    public Project(String projectName, String neighborhood, Visibility visibility, String type1, int numUnitsType1, double priceType1, String type2, int numUnitsType2, double priceType2, LocalDate applicationOpenDate, LocalDate applicationCloseDate, String manager, String managerNRIC, int officerSlot, String officer, String officerNRIC) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.type1 = type1;
        this.numUnitsType1 = numUnitsType1;
        this.priceType1 = priceType1;
        this.type2 = type2;
        this.numUnitsType2 = numUnitsType2;
        this.priceType2 = priceType2;
        this.applicationOpenDate = applicationOpenDate;
        this.applicationCloseDate = applicationCloseDate;
        this.manager = manager;
        this.managerNRIC = managerNRIC;
        this.officerSlot = officerSlot;
        this.officer = officer;
        this.officerNRIC = officerNRIC;
        this.visibility = visibility;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public int getNumUnitsType1() {
        return numUnitsType1;
    }

    public void setNumUnitsType1(int numUnitsType1) {
        this.numUnitsType1 = numUnitsType1;
    }

    public double getPriceType1() {
        return priceType1;
    }

    public void setPriceType1(double priceType1) {
        this.priceType1 = priceType1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public int getNumUnitsType2() {
        return numUnitsType2;
    }

    public void setNumUnitsType2(int numUnitsType2) {
        this.numUnitsType2 = numUnitsType2;
    }

    public double getPriceType2() {
        return priceType2;
    }

    public void setPriceType2(double priceType2) {
        this.priceType2 = priceType2;
    }

    public LocalDate getApplicationOpenDate() {
        return applicationOpenDate;
    }

    public void setApplicationOpenDate(LocalDate applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }

    public LocalDate getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(LocalDate applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManagerNRIC() {
        return managerNRIC;
    }

    public void setManagerNRIC(String managerNRIC) {
        this.managerNRIC = managerNRIC;
    }

    public int getOfficerSlot() {
        return officerSlot;
    }

    public void setOfficerSlot(int officerSlot) {
        this.officerSlot = officerSlot;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public String getOfficerNRIC() {
        return officerNRIC;
    }

    public void setOfficerNRIC(String officerNRIC) {
        this.officerNRIC = officerNRIC;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(getProjectName(), project.getProjectName()) && Objects.equals(getManagerNRIC(), project.getManagerNRIC());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectName(), getManagerNRIC());
    }

    public String IDstring() {return projectName;}

    public String defaultString(){
        return (projectName);
    };

    public String toSearchableString() {
        return (
                projectName
                        + " " + neighborhood
                        + " " + applicationOpenDate
                        + " " + applicationCloseDate
                        + " " + manager
                        + " " + officer
                        + " " + visibility
        ).toLowerCase();
    }

    public List<Integer> toSearchableNum() {
        return new ArrayList<>(List.of((int)priceType1,(int)priceType2));
    }




    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return String.format("%-15s: %s%n", "Project Name", projectName) +
                String.format("%-15s: %-20s %-15s: %s%n", "Visibility", visibility, "Type 1", type1) +
                String.format("%-15s: %-20s %-15s: %d%n", "Neighborhood", neighborhood, "Units (Type 1)", numUnitsType1) +
                String.format("%-15s: %-20s %-15s: %.2f%n", "Open Date", applicationOpenDate.format(dateFormatter), "Price (Type 1)", priceType1) +
                String.format("%-15s: %-20s %-15s: %s%n", "Close Date", applicationCloseDate.format(dateFormatter), "Type 2", type2) +
                String.format("%-15s: %-20s %-15s: %d%n", "Manager", manager, "Units (Type 2)", numUnitsType2) +
                String.format("%-15s: %-20s %-15s: %.2f", "Officer(s)", officer, "Price (Type 2)", priceType2);
    }
}
