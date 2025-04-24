package Utilities;

import Classes.Application;
import Classes.Enquiry;
import Classes.OfficerApplication;
import Classes.Project;
import Loaders.ApplicationLoader;
import Loaders.EnquiryLoader;
import Loaders.OfficerApplicationLoader;
import Loaders.ProjectLoader;

import java.util.ArrayList;
import java.util.List;

public class GetFilteredLists {
    public static List<Project> getManagerFilteredProjectList(String ManagerNRIC){
        return ProjectLoader.loadProjects().stream().filter(project -> project.getManagerNRIC().equals(ManagerNRIC)).toList();
    }

    public static List<OfficerApplication> getProjectFilteredOfficerApplicationList(String ProjectName){
        return OfficerApplicationLoader.loadOfficerApplications().stream().filter(officerApplication -> officerApplication.getProjectName().equals(ProjectName)).toList();
    }

    public static List<Application> getProjectFilteredApplicationList(String ProjectName){
        return ApplicationLoader.loadApplications().stream().filter(application -> application.getProjectName().equals(ProjectName)).toList();
    }

    public static List<Enquiry> getProjectFilteredEnquiryList(String ProjectName){
        return EnquiryLoader.loadEnquires().stream().filter(enquiry -> enquiry.getProjectName().equals(ProjectName)).toList();
    }

}
