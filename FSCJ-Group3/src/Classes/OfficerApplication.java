package Classes;

import Loaders.OfficerApplicationLoader;
import Utilities.Searchable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OfficerApplication implements Searchable {
    private String projectName;
    private String officerName;
    private String officerNRIC;
    private OfficerApplicationStatus officerApplicationStatus;

    public enum OfficerApplicationStatus {
        INITIATED,
        SUCCESSFUL,
        UNSUCCESSFUL,
        PROJECT_DELETED
    }
    public OfficerApplication(String projectName, String officerName, String officerNRIC, OfficerApplicationStatus officerApplicationStatus) {
        this.projectName = projectName;
        this.officerName = officerName;
        this.officerNRIC = officerNRIC;
        this.officerApplicationStatus = officerApplicationStatus;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getOfficerNRIC() {
        return officerNRIC;
    }

    public void setOfficerNRIC(String officerNRIC) {
        this.officerNRIC = officerNRIC;
    }

    public OfficerApplicationStatus getOfficerApplicationStatus() {
        return officerApplicationStatus;
    }

    public void setOfficerApplicationStatus(OfficerApplicationStatus officerApplicationStatus) {
        this.officerApplicationStatus = officerApplicationStatus;
    }

    public String IDstring() {return projectName+officerNRIC;}

    public String defaultString(){
        return (projectName+officerName+officerNRIC);
    };

    public String toSearchableString() {
        return (
                projectName
                        + " " + officerName
                        + " " + officerApplicationStatus
        ).toLowerCase();
    }

    public List<Integer> toSearchableNum() {
        return null;
    }

    // Filter applications by project name
    public static ArrayList<OfficerApplication> getOfficerApplicationList (String projectName) {
        ArrayList<OfficerApplication> filteredList = new ArrayList<>();
        for (OfficerApplication officerApplication : OfficerApplicationLoader.loadOfficerApplications()) {
            if (officerApplication.projectName.equals(projectName)) {
                filteredList.add(officerApplication);
            }
        }
        return filteredList;
    }



    @Override
    public String toString() {
        String leftColFormat = "%-20s %s\n";
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(leftColFormat, "Project Name:", projectName));
        sb.append(String.format(leftColFormat, "Officer Name:", officerName));
        sb.append(String.format(leftColFormat, "NRIC:", officerNRIC));
        sb.append(String.format(leftColFormat, "Application Status:", officerApplicationStatus));

        return sb.toString();
    }

}
