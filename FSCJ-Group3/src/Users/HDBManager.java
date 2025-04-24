package Users;

import Classes.Application;
import Classes.Enquiry;
import Classes.OfficerApplication;
import Classes.Project;
import Loaders.ApplicationLoader;
import Loaders.EnquiryLoader;
import Loaders.OfficerApplicationLoader;
import Loaders.ProjectLoader;
import Utilities.Filter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static Loaders.ApplicationLoader.saveApplicationToCSV;
import static Loaders.EnquiryLoader.saveEnquiries;
import static Loaders.EnquiryLoader.updateEnquiriesToCSV;
import static Loaders.OfficerApplicationLoader.saveOfficerApplicationToCSV;
import static Loaders.ProjectLoader.saveProjectToCSV;
import static Utilities.Display.*;
import static Loaders.ApplicationLoader.saveApplications;
import static Loaders.OfficerApplicationLoader.saveOfficerApplications;
import static Loaders.ProjectLoader.saveProjects;
import static Utilities.Filter.filterByKeywords;
import static Utilities.Filter.filterByRange;
import static Utilities.GetFilteredLists.*;
import static Utilities.ReadInput.*;
import static Utilities.Utility.*;

public class HDBManager extends User{
    private Project activeProject;

    public HDBManager(String name, String NRIC, int age, MaritalStatus maritalStatus, String password) {
        super(name, NRIC, age, password, maritalStatus);
        setActiveProject();
    }

    // Search for Active Project - Visibility ON AND Current Date is within Application Period
    public void setActiveProject() {
        this.activeProject = ProjectLoader.loadProjects().stream()
                .filter(project -> project.getManagerNRIC().equals(getNRIC()))
                .filter(project -> project.getVisibility() == Project.Visibility.ON)
                .filter(project -> {
                    LocalDate today = LocalDate.now();
                    return today.isEqual(project.getApplicationOpenDate())
                            || today.isEqual(project.getApplicationCloseDate())
                            || (project.getApplicationOpenDate().isBefore(today) && project.getApplicationCloseDate().isAfter(today));
                })
                .findFirst()
                .orElse(null);
    }

    public Project getActiveProject() {
        return activeProject;
    }

    // Create a New Project
    public void createProject() {
        displaySubHeader("Enter New Project Details Below");

        // Enter New Project Name and Make sure it's not found in current Project List
        String projectName;
        while (true) {
            projectName = readStringInput("Enter Project Name: ");

            // Check if project name is unique
            if (searchProject(projectName)) {
                System.out.println("A Project with this name already exists. Please choose a different name.");
            } else {
                break;
            }
        }


        // Enter Project Neighbourhood
        String projectNeighbourhood = readStringInput("Enter Project Neighbourhood: ");

        // Enter 2-Room units information
        String type1 = "2-ROOM";
        int numOfType1 = readIntInput("Enter Number of " + type1 + " Units: ");
        double sellingPrice1 = readDoubleInput("Enter the selling price of " + type1 + " units: ");

        // Enter 3-Room units information
        String type2 = "3-ROOM";
        int numOfType2 = readIntInput("Enter Number of " + type2 + " Units: ");
        double sellingPrice2 = readDoubleInput("Enter the selling price of " + type2 + " units: ");

        // Enter Dates
        LocalDate applicationOpeningDate;
        LocalDate applicationClosingDate;

        while (true) {

            // Make sure opening date is not in the past. Can only open future projects.
            while (true) {
                applicationOpeningDate = readDateInput("Enter Application Opening Date (DD-MM-YYYY): ");
                if (applicationOpeningDate.isBefore(LocalDate.now())) {
                    System.out.println("The Opening Date cannot be before today's date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                } else {
                    break;
                }
            }

            // Make sure closing date is after opening date.
            while (true) {
                applicationClosingDate = readDateInput("Enter Application Closing Date (DD-MM-YYYY): ");
                if (applicationClosingDate.isBefore(applicationOpeningDate)) {
                    System.out.println("Closing date cannot be before the opening date! Please try again.");
                } else {
                    break;
                }
            }

            // Check for date conflict with
            System.out.println("Checking For Date Conflict . . ");
            if (isDateConflict(applicationOpeningDate, applicationClosingDate, null)) {
                System.out.println("No Conflict Found");
                break;
            }
        }

        int officerSlots;
        do {
            officerSlots = readIntInput("Set the Max Number of Officer Slots for this Project (Max 10): ");
            if (officerSlots > 10){
                System.out.println("Max officer slots cannot be more than 10");
            }
        }while (officerSlots>10);

        String confirm = readStringInput("Do you want to create Project " + projectName + "? (Yes/No): ").toLowerCase();

        switch (confirm){
            case "y", "yes" -> {
                Project newProject = new Project(projectName, projectNeighbourhood, Project.Visibility.OFF,
                        type1, numOfType1, sellingPrice1,
                        type2, numOfType2, sellingPrice2,
                        applicationOpeningDate, applicationClosingDate,
                        getName(), getNRIC(), officerSlots, "None", "None");

                saveProjectToCSV(newProject);
                System.out.println("Project Created.");
                displaySeperator();
            }

            case "n", "no" -> {
                System.out.println("Project creation canceled.");
            }

            default -> {
                System.out.println("Invalid choice. Please enter 'y' or 'n' to confirm the deletion.");
            }
        }
        displaySeperator();
    }

    public void editProject() {
        boolean exit = false;

        displayHeader("Project List");
        viewOwnProject();

        Project selectedProject = selectProject(getManagerFilteredProjectList(getNRIC()));
        if (selectedProject == null) {
            return;
        }

        displaySeperator();

        System.out.println("Selected Project: " + selectedProject.getProjectName());
        displaySeperator();

        displaySubHeader("Project Details");
        System.out.println(selectedProject);
        displaySeperator();

        while (!exit) {
            displaySubHeader("Select a Field to Modify");
            System.out.printf("%-40s %s%n", "01. Project Name", "06. Number of 3-ROOM Units");
            System.out.printf("%-40s %s%n", "02. Project Neighbourhood", "07. Price of 3-ROOM Units");
            System.out.printf("%-40s %s%n", "03. Toggle Project Visibility", "08. Application Opening / Closing Dates");
            System.out.printf("%-40s %s%n", "04. Number of 2-ROOM Units", "09. Max Officer Slot");
            System.out.printf("%-40s %s%n", "05. Price of 2-ROOM Units", "0. Exit");
            displaySeperator();
            int choice = readIntInput("What do you want to Change?: ");
            displaySeperator();
            switch (choice) {
                case 1 -> {
                    displaySelection("Change Project Name");
                    displaySeperator();
                    System.out.println("Current Project Name: " + selectedProject.getProjectName());

                    String projectName;
                    while (true){
                        projectName = readStringInput("Enter New Project Name: ");
                        displaySeperator();
                        if (searchProject(projectName)) {
                            System.out.println("A project with this name already exists. Please choose a different name.");
                        } else {
                            break;
                        }
                    }
                    displaySeperator();
                    selectedProject.setProjectName(projectName);
                }

                case 2 -> {
                    displaySelection("Change Project Neighbourhood");
                    displaySeperator();
                    System.out.println("Current Project Neighbourhood: " + selectedProject.getNeighborhood());
                    String ProjectNeighbourhood = readStringInput("Enter New Project Neighbourhood: ");
                    selectedProject.setNeighborhood(ProjectNeighbourhood);
                }

                case 3 -> {
                    displaySelection("Toggling Project Visibility");
                    displaySeperator();
                    System.out.println("Current Project Visibility: " + selectedProject.getVisibility());
                    System.out.println("Toggling Visibility.... ");
                    if (selectedProject.getVisibility() == Project.Visibility.ON) {
                        selectedProject.setVisibility(Project.Visibility.OFF);
                    } else {
                        selectedProject.setVisibility(Project.Visibility.ON);
                    }
                    System.out.println("Current Project Visibility:" + selectedProject.getVisibility());
                }

                case 4 -> {
                    displaySelection("Number of 2-ROOM Units");
                    displaySeperator();
                    System.out.println("Current Number of 2-ROOM Units: " + selectedProject.getNumUnitsType1());
                    int newNumOfType1 = readIntInput("Enter New Number of 2-ROOM Units: ");
                    selectedProject.setNumUnitsType1(newNumOfType1);
                }

                case 5 -> {
                    displaySelection("Price of 2-ROOM Units");
                    displaySeperator();
                    System.out.println("Current Price of 2-ROOM Units: " + selectedProject.getPriceType1());
                    int newPriceOfType1 = readIntInput("Enter New Number of 2-ROOM Units: ");
                    selectedProject.setPriceType1(newPriceOfType1);
                }

                case 6 -> {
                    displaySelection("Number of 3-ROOM Units");
                    displaySeperator();

                    System.out.println("Current Number of 3-ROOM Units: " + selectedProject.getNumUnitsType2());
                    int newNumOfType2 = readIntInput("Enter New Number of 3-ROOM Units: ");
                    selectedProject.setNumUnitsType2(newNumOfType2);
                }

                case 7 -> {
                    displaySelection("Price of 3-ROOM Units");
                    displaySeperator();

                    System.out.println("Current Price of 3-ROOM Units: " + selectedProject.getPriceType2());
                    int newPriceOfType2 = readIntInput("Enter New Number of 3-ROOM Units: ");
                    selectedProject.setPriceType2(newPriceOfType2);
                }

                case 8 -> {
                    displaySelection("Application Opening / Closing Dates");
                    displaySeperator();

                    System.out.println("Current Application Opening Date: " + selectedProject.getApplicationOpenDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    System.out.println("Current Application Closing Dates " + selectedProject.getApplicationCloseDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    // Dates
                    LocalDate applicationOpeningDate;
                    LocalDate applicationClosingDate;

                    while (true) {
                        while (true) {
                            applicationOpeningDate = readDateInput("Enter New Application Opening Date (DD-MM-YYYY): ");
                            if (applicationOpeningDate.isBefore(LocalDate.now()) || applicationOpeningDate.equals(LocalDate.now())) {
                                System.out.println("The Opening Date cannot be before today's date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                            } else {
                                break;
                            }
                        }

                        while (true) {
                            applicationClosingDate = readDateInput("Enter New Application Closing Date (DD-MM-YYYY): ");
                            if (applicationClosingDate.isBefore(applicationOpeningDate)) {
                                System.out.println("Closing date cannot be before the opening date! Please try again.");
                            } else {
                                break;
                            }
                        }

                        System.out.println("Checking For Date Conflict . . ");
                        if (isDateConflict(applicationOpeningDate, applicationClosingDate, selectedProject)) {
                            System.out.println("No Conflict Found");
                            break;
                        }
                    }

                    selectedProject.setApplicationOpenDate(applicationOpeningDate);
                    selectedProject.setApplicationCloseDate(applicationClosingDate);
                    System.out.println("New Application Opening / Closing Dates Set");
                }

                case 9 -> {
                    displaySelection("Max Officer Slot");
                    displaySeperator();

                    System.out.println("Current Max Officer Slot: " + selectedProject.getOfficerSlot());
                    int newOfficerSlot;
                    do {
                        newOfficerSlot = readIntInput("Enter New Max Officer Slot (Max 10): ");
                    } while (!(newOfficerSlot > 0 && newOfficerSlot < 11));
                    selectedProject.setOfficerSlot(newOfficerSlot);
                }

                case 0 -> {
                    exit = true;
                    System.out.println("Exiting Project Editing...");
                }

                default -> System.out.println("Invalid selection! Please try again.");
            }
        }
        saveProjectToCSV(selectedProject);
        System.out.println("Project Saved");
    }

    private boolean isDateConflict(LocalDate newOpeningDate, LocalDate newClosingDate, Project project) {
        for (Project p : ProjectLoader.loadProjects()) {
            if (p.equals(project)){
                continue;
            }
            if (getName().equals(p.getManager())) {
                LocalDate existingOpeningDate = p.getApplicationOpenDate();
                LocalDate existingClosingDate = p.getApplicationCloseDate();
                if ((newOpeningDate.isEqual(existingOpeningDate) || newOpeningDate.isAfter(existingOpeningDate)) &&
                        (newOpeningDate.isEqual(existingClosingDate) || newOpeningDate.isBefore(existingClosingDate))) {
                    System.out.println("Conflict Found");
                    System.out.println("Overlapping Project: " + p.getProjectName() +
                            " (" + p.getApplicationOpenDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                            " to " + p.getApplicationCloseDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ")");
                    return false;
                }
                if ((newClosingDate.isEqual(existingOpeningDate) || newClosingDate.isAfter(existingOpeningDate)) &&
                        (newClosingDate.isEqual(existingClosingDate) || newClosingDate.isBefore(existingClosingDate))) {
                    System.out.println("Conflict Found");
                    System.out.println("Overlapping Project: " + p.getProjectName() +
                            " (" + p.getApplicationOpenDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                            " to " + p.getApplicationCloseDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ")");
                    return false;
                }

                if ((newOpeningDate.isBefore(existingClosingDate) && newClosingDate.isAfter(existingOpeningDate))) {
                    System.out.println("Conflict Found");
                    System.out.println("Overlapping Project: " + p.getProjectName() +
                            " (" + p.getApplicationOpenDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                            " to " + p.getApplicationCloseDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ")");
                    return false;
                }
            }
        }
        return true;
    }

    public void deleteProject(){
        List<String> headers = List.of("Project Name", "Neighbourhood", "Officer(s)");
        List<String[]> rows = getManagerFilteredProjectList(getNRIC())
                .stream()
                .map(p -> new String[]{p.getProjectName(), p.getNeighborhood(),p.getOfficer()})
                .toList();

        displayHeader("PROJECT LIST");
        displayTable(rows, headers);

        Project selectedProject = selectProject(getManagerFilteredProjectList(getNRIC()));
        if (selectedProject == null) {
            return;
        }
        displaySeperator();

        System.out.println("Do you want to delete project " + selectedProject.getProjectName() + "?");
        displaySeperator();

        String choice = readStringInput("Enter Your Choice (Yes / No) : ").toLowerCase();
        displaySeperator();


        switch (choice){
            case "y", "yes" -> {
                List<Project> projectList = ProjectLoader.loadProjects();
                List<Application> applicationList = ApplicationLoader.loadApplications();
                List<OfficerApplication> officerApplicationList = OfficerApplicationLoader.loadOfficerApplications();
                List<Enquiry> enquiryList = EnquiryLoader.loadEnquires();


                projectList.removeIf(project -> project.equals(selectedProject));
                enquiryList.removeIf(enquiry -> enquiry.getProjectName().equals(selectedProject.getProjectName()));

                applicationList.stream()
                        .filter(application -> application.getProjectName().equals(selectedProject.getProjectName()))
                        .forEach(application -> application.setApplicationStatus(Application.ApplicationStatus.PROJECT_DELETED));

                officerApplicationList.stream()
                        .filter(officerApplication -> officerApplication.getProjectName().equals(selectedProject.getProjectName()))
                        .forEach(officerApplication -> officerApplication.setOfficerApplicationStatus(OfficerApplication.OfficerApplicationStatus.PROJECT_DELETED));


                saveProjects(projectList);
                saveEnquiries(enquiryList);
                saveApplications(applicationList);
                saveOfficerApplications(officerApplicationList);

                System.out.println("Project " + selectedProject.getProjectName() + " has been deleted.");
                displaySeperator();
            }

            case "n", "no" -> {
                System.out.println("Project deletion cancelled.");
                displaySeperator();
            }

            default -> {
                System.out.println("Invalid choice. Please enter 'y' or 'n' to confirm the deletion.");
                displaySeperator();
            }
        }

    }

    public void viewAllProjects(){
        displaySelection("View All Projects");
        displaySeperator();

        displayHeader("ALL PROJECTS");
        displaySeperator();

        ProjectLoader.loadProjects().forEach(
                project -> {
                    System.out.println(project);
                    displaySeperator();
                }
        );

        System.out.println("All " + ProjectLoader.loadProjects().size() + " Projects Have been Listed");
        displaySeperator();
//        String choice = readStringInput("Do you want to filter for Project or Neighbourhood or Manager or Officer? (Yes / No) : ").toLowerCase();
//        displaySeperator();
//        switch (choice){
//            case "y","yes" -> {
//                String input = readStringInput("Enter your Filters (Separate by Space - E.g: Samantha Bishan): ");
//                displaySeperator();
//                List<String> keywords = List.of(input.split(" "));
//                filterByKeywords(ProjectLoader.loadProjects(), keywords).forEach(project -> {
//                    System.out.println(project);
//                    displaySeperator();
//                });
//            }
//        }
    }

    public void viewOwnProject(){
        getManagerFilteredProjectList(getNRIC()).forEach(project -> {
            displayFullProjectDetails(project);
            displaySeperator();
        });
    }

    public void toggleVisibility(){

        List<String> headers = List.of("Project Name", "Visibility");
        List<String[]> rows = getManagerFilteredProjectList(getNRIC())
                .stream()
                .map(p -> new String[]{p.getProjectName(), p.getVisibility().toString()})
                .toList();

        displayHeader("PROJECT LIST");
        displayTable(rows, headers);

        Project selectedProject = selectProject(getManagerFilteredProjectList(getNRIC()));
        if (selectedProject == null) {
            return;
        }

        displaySeperator();


        System.out.println("Current Project Visibility: " + selectedProject.getVisibility());
        System.out.println("Toggling Project Visibility...");

        if (selectedProject.getVisibility() == Project.Visibility.ON) {
            selectedProject.setVisibility(Project.Visibility.OFF);
        } else {
            selectedProject.setVisibility(Project.Visibility.ON);
        }

        System.out.println("New Project Visibility: " + selectedProject.getVisibility());
        displaySeperator();

        saveProjectToCSV(selectedProject);
    }

    public void viewOfficerRegistrations(){
        boolean exit = false;

        List<String> headers = List.of("Project Name", "Pending", "Approved", "Slots Left");
        List<String[]> rows = getManagerFilteredProjectList(getNRIC())
                .stream()
                .map(project -> {
                    long pendingCount = getProjectFilteredOfficerApplicationList(project.getProjectName()).stream().filter(officerApplication -> officerApplication.getOfficerApplicationStatus().equals(OfficerApplication.OfficerApplicationStatus.INITIATED)).count();
                    long approvedCount = getProjectFilteredOfficerApplicationList(project.getProjectName()).stream().filter(officerApplication -> officerApplication.getOfficerApplicationStatus().equals(OfficerApplication.OfficerApplicationStatus.SUCCESSFUL)).count();
                    return new String[]{project.getProjectName(), String.valueOf(pendingCount), String.valueOf(approvedCount), String.valueOf(project.getOfficerSlot())};
                })
                .toList();

        displayHeader("PROJECT LIST - OFFICER Registrations");
        displayTable(rows, headers);


        Project selectedProject = selectProject(getManagerFilteredProjectList(getNRIC()));
        if (selectedProject == null) {
            return;
        }
        //displaySeperator();


        while (!exit){

            List<OfficerApplication> filtered = getProjectFilteredOfficerApplicationList(selectedProject.getProjectName());
            List<OfficerApplication> pendingApplications = filtered.stream().filter(officerApplication -> officerApplication.getOfficerApplicationStatus().equals(OfficerApplication.OfficerApplicationStatus.INITIATED)).toList();
            List<OfficerApplication> approvedApplications = filtered.stream().filter(officerApplication -> officerApplication.getOfficerApplicationStatus().equals(OfficerApplication.OfficerApplicationStatus.SUCCESSFUL)).toList();

            if(pendingApplications.isEmpty()){
                displaySeperator();
                displayHeader("No Pending Registrations for Project");
            } else {
                displaySeperator();
                headers = List.of("Officer Name", "Officer NRIC", "Application Status");
                rows = pendingApplications.stream()
                        .map(officerApplication -> {
                            return new String[]{officerApplication.getOfficerName(),officerApplication.getOfficerNRIC(), String.valueOf(officerApplication.getOfficerApplicationStatus())};
                        }).toList();

                displayHeader(selectedProject.getProjectName() + " - PENDING OFFICERS LIST");
                displayTable(rows, headers);
            }
            displaySeperator();
            if (approvedApplications.isEmpty()){
                displayHeader("No Approved Registrations for Project");
            } else {
                headers = List.of("Officer Name", "Officer NRIC", "Application Status");
                rows = approvedApplications.stream()
                        .map(officerApplication -> {
                            return new String[]{officerApplication.getOfficerName(),officerApplication.getOfficerNRIC(), String.valueOf(officerApplication.getOfficerApplicationStatus())};
                        }).toList();

                displayHeader(selectedProject.getProjectName() + " - APPROVED OFFICERS LIST");
                displayTable(rows, headers);
            }

            // Ask for input: approve or reject officer
            System.out.println("Officer Slots Remaining: " + selectedProject.getOfficerSlot());

            displaySeperator();
            System.out.println(BOLD + UNDERLINE + "Options" + RESET);
            System.out.println("1. Approve Officer");
            System.out.println("2. Reject Officer");
            System.out.println("0. Exit");
            displaySeperator();

            int choice = readIntInput("Enter Your Choice: ");
            displaySeperator();

            switch (choice){
                case 1 -> {
                    displaySelection("Approve Officer");
                    displaySeperator();
                    if (selectedProject.getOfficerSlot() == 0){
                        System.out.println("You have no more officer slots left.");
                        displaySeperator();
                        break;
                    }
                    if ((long) pendingApplications.size() == 0){
                        System.out.println("You have no Pending Registrations");
                        displaySeperator();

                        break;
                    }

                    OfficerApplication selectedOfficerApplication = null;
                    do{
                        String officerNRIC = readStringInput("Enter the Officer's NRIC (or 0 to return): ");
                        if(officerNRIC.equals("0")) {displaySeperator();return;}
                        selectedOfficerApplication = pendingApplications.stream().filter(officerApplication -> officerApplication.getOfficerNRIC().equals(officerNRIC)).findFirst().orElse(null);

                        if (selectedOfficerApplication == null){
                            System.out.println("Officer Not Found. Make sure the NRIC is exact");
                            displaySeperator();
                        }
                    } while(selectedOfficerApplication == null);

                    selectedOfficerApplication.setOfficerApplicationStatus(OfficerApplication.OfficerApplicationStatus.SUCCESSFUL);

                    saveOfficerApplicationToCSV(selectedOfficerApplication);

                    selectedProject.setOfficerSlot(selectedProject.getOfficerSlot()-1);
                    selectedProject.setOfficer(selectedProject.getOfficer()+";"+selectedOfficerApplication.getOfficerName());
                    selectedProject.setOfficerNRIC(selectedProject.getOfficerNRIC()+";"+selectedOfficerApplication.getOfficerNRIC());

                    saveProjectToCSV(selectedProject);
                }

                case 2 -> {
                    displaySelection("Reject Officer");
                    displaySeperator();

                    /*
                    if (selectedProject.getOfficerSlot() == 0){
                        System.out.println("You have no more officer slots left.");
                        displaySeperator();
                        break;
                    }
                     */

                    if ((long) pendingApplications.size() == 0){
                        System.out.println("You have no Pending Registrations");
                        displaySeperator();
                        break;
                    }

                    OfficerApplication selectedOfficerApplication = null;
                    do{
                        String officerNRIC = readStringInput("Enter the Officer's NRIC (or 0 to return): ");
                        if(officerNRIC.equals("0")) {displaySeperator();return;}
                        selectedOfficerApplication = pendingApplications.stream().filter(
                                officerApplication ->
                                        officerApplication.getProjectName().equals(selectedProject.getProjectName())
                                        && officerApplication.getOfficerNRIC().equals(officerNRIC)).findFirst().orElse(null);

                        if (selectedOfficerApplication == null){
                            System.out.println("Officer Not Found. Make sure the NRIC is exact");
                            displaySeperator();
                        }
                    } while(selectedOfficerApplication == null);


                    selectedOfficerApplication.setOfficerApplicationStatus(OfficerApplication.OfficerApplicationStatus.UNSUCCESSFUL);
                    saveOfficerApplicationToCSV(selectedOfficerApplication);
                }

                case 0 ->{
                    System.out.println("Exiting . . . ");
                    displaySeperator();
                    exit = true;
                }

                default -> System.out.println("Invalid option. Please choose 1, 2, or 0.");
            }
        }
    }

    public void viewApplicantApplications(){
        boolean exit = false;

        List<String> headers = List.of("Project Name", "Pending", "Approved");
        List<String[]> rows = getManagerFilteredProjectList(getNRIC())
                .stream()
                .map(project -> {
                    long pendingCount = getProjectFilteredApplicationList(project.getProjectName()).stream().filter(application -> application.getApplicationStatus().equals(Application.ApplicationStatus.PENDING)).count();
                    long approvedCount = getProjectFilteredApplicationList(project.getProjectName()).stream().filter(application -> application.getApplicationStatus().equals(Application.ApplicationStatus.SUCCESSFUL)).count();
                    return new String[]{project.getProjectName(), String.valueOf(pendingCount), String.valueOf(approvedCount)};
                })
                .toList();

        displayHeader("PROJECT LIST - APPLICATIONS");
        displayTable(rows, headers);


        Project selectedProject = selectProject(getManagerFilteredProjectList(getNRIC()));
        if (selectedProject == null) {
            return;
        }

        List<Application> currentFilteredPending = null;
        List<Application> currentFilteredSuccess = null;

        while(!exit){
            List<Application> filtered = Application.getApplicationsList(selectedProject.getProjectName());

            List<Application> pendingApplications = filtered.stream().filter(application -> application.getApplicationStatus().equals(Application.ApplicationStatus.PENDING)).toList();
            List<Application> approvedApplications = filtered.stream().filter(application -> application.getApplicationStatus().equals(Application.ApplicationStatus.SUCCESSFUL)).toList();

            if(pendingApplications.isEmpty()){
                displaySeperator();
                displayHeader("No Pending Applications for Project");
            } else {
                displaySeperator();
                headers = List.of("Applicant Name", "NRIC", "Age", "Marital Status", "Flat Type");
                rows = pendingApplications.stream()
                        .map(application -> {
                            return new String[]{application.getApplicantName(),application.getApplicantNRIC(), String.valueOf(application.getAge()), String.valueOf(application.getMaritalStatus()), application.getFlatType()};
                        }).toList();

                displayHeader(selectedProject.getProjectName() + " - PENDING APPLICATIONS LIST");
                displayTable(rows, headers);
            }

            if (approvedApplications.isEmpty()){
                displayHeader("No Approved Applications for Project");
            } else {
                displaySeperator();
                headers = List.of("Applicant Name", "NRIC", "Age", "Marital Status", "Flat Type");
                rows = approvedApplications.stream()
                        .map(application ->
                                new String[]{application.getApplicantName(),application.getApplicantNRIC(), String.valueOf(application.getAge()), String.valueOf(application.getMaritalStatus()), application.getFlatType()}).toList();

                displayHeader(selectedProject.getProjectName() + " - APPROVED APPLICATIONS LIST");
                displayTable(rows, headers);
            }

            int remainingUnits2Room = selectedProject.getNumUnitsType1();
            long approved2R = approvedApplications.stream().filter(application -> application.getFlatType().equals("2-ROOM")).count();

            int remainingUnits3Room = selectedProject.getNumUnitsType2();
            long approved3R = approvedApplications.stream().filter(application -> application.getFlatType().equals("3-ROOM")).count();

            System.out.println("Remaining " + selectedProject.getType1() + " Flats: " + remainingUnits2Room);
            System.out.println("Approved " + selectedProject.getType1() + " Applications: " + approved2R);
            System.out.println("Remaining " + selectedProject.getType2() + " Flats: " + remainingUnits3Room);
            System.out.println("Approved " + selectedProject.getType2() + " Applications: " + approved3R);
            displaySeperator();

            displaySeperator();
            System.out.println(BOLD + UNDERLINE + "Options" + RESET);
            System.out.println("1. Approve Application");
            System.out.println("2. Reject Application");
            System.out.println("3. Search Pending List");
            System.out.println("4. Search Approved List");
            System.out.println("0. Exit");
            displaySeperator();

            int choice = readIntInput("Enter Your Choice : ");
            displaySeperator();

            switch (choice){
                case 1 -> {
                    displaySelection("Approve Application");
                    displaySeperator();

                    Application selectedApplication = null;
                    do{
                        String ApplicantNRIC = readStringInput("Enter the Applicant's NRIC (or 0 to return): ");
                        if(ApplicantNRIC.equals("0")) {displaySeperator();return;}
                        selectedApplication = pendingApplications.stream().filter(application -> application.getApplicantNRIC().equals(ApplicantNRIC)).findFirst().orElse(null);

                        if (selectedApplication == null){
                            System.out.println("Applicant Not Found. Make sure the NRIC is exact");
                            displaySeperator();
                        }
                    } while(selectedApplication == null);

                    selectedApplication.setApplicationStatus(Application.ApplicationStatus.SUCCESSFUL);
                    saveApplicationToCSV(selectedApplication);

                    /*
                    if(selectedApplication.getFlatType().equals("2-ROOM")){
                        selectedProject.setNumUnitsType1(selectedProject.getNumUnitsType1()-1);
                    } else {
                        selectedProject.setNumUnitsType1(selectedProject.getNumUnitsType2()-1);
                    }
                     */

                    saveApplicationToCSV(selectedApplication);
                }

                case 2 -> {
                    displaySelection("Reject Application");
                    displaySeperator();

                    printCurrentList(currentFilteredPending);

                    Application selectedApplication = null;
                    do{
                        String ApplicantNRIC = readStringInput("Enter the Applicant's NRIC (or 0 to return): ");
                        if(ApplicantNRIC.equals("0")) {displaySeperator();return;}
                        selectedApplication = pendingApplications.stream().filter(application -> application.getApplicantNRIC().equals(ApplicantNRIC)).findFirst().orElse(null);

                        if (selectedApplication == null){
                            System.out.println("Applicant Not Found. Make sure the NRIC is exact");
                            displaySeperator();
                        }
                    } while(selectedApplication == null);


                    selectedApplication.setApplicationStatus(Application.ApplicationStatus.UNSUCCESSFUL);
                    saveApplicationToCSV(selectedApplication);
                }

                case 3 -> {
                    //Filter.printCurrentList(currentFilteredPending);
                    currentFilteredPending = Filter.filterRun(pendingApplications);
                }

                case 4 -> {
                    //Filter.printCurrentList(currentFilteredSuccess);
                    currentFilteredSuccess = Filter.filterRun(approvedApplications);
                }

                case 0 -> {
                    System.out.println("Exiting. . .");
                    displaySeperator();
                    exit = true;
                }

                default -> System.out.println("Invalid option. Please choose 1, 2, or 0.");
            }
        }
    }

    public void viewApplicantWithdrawalApplications(){
        boolean exit = false;
        displayHeader("PROJECT LIST - WITHDRAWAL");

        List<String> headers = List.of("Project Name", "Pending Withdrawals");
        List<String[]> rows = getManagerFilteredProjectList(getNRIC())
                .stream()
                .map(project -> {
                    long pendingCount = getProjectFilteredApplicationList(project.getProjectName()).stream().filter(application -> application.getApplicationStatus().name().contains("IN_WITHDRAWAL")).count();
                    return new String[]{project.getProjectName(), String.valueOf(pendingCount)};
                })
                .toList();

        displayTable(rows, headers);
        displaySeperator();

        Project selectedProject = selectProject(getManagerFilteredProjectList(getNRIC()));
        if (selectedProject == null) {
            return;
        }


        while (!exit){
            List<Application> filtered = Application.getApplicationsList(selectedProject.getProjectName());
            List<Application> withdrawalApplications = filtered.stream().filter(application -> application.getApplicationStatus().name().contains("IN_WITHDRAWAL")).toList();

            System.out.println("Withdrawal Applications: ");
            displaySeperator();

            if(withdrawalApplications.isEmpty()){
                System.out.println("No Withdrawal Applications for Project : " + selectedProject.getProjectName());
                displaySeperator();
            } else {
                displaySeperator();
                headers = List.of("Applicant Name", "NRIC", "Age", "Marital Status", "Flat Type");
                rows = withdrawalApplications.stream()
                        .map(application -> {
                            return new String[]{application.getApplicantName(),application.getApplicantNRIC(), String.valueOf(application.getAge()), String.valueOf(application.getMaritalStatus()), application.getFlatType()};
                        }).toList();

                displayHeader(selectedProject.getProjectName() + " - WITHDRAWAL APPLICATIONS LIST");
                displayTable(rows, headers);
            }

            displaySeperator();

            System.out.println(BOLD + UNDERLINE + "Options" + RESET);

            System.out.println("1. Approve Withdrawal Application");
            System.out.println("2. Reject Withdrawal Application");
            System.out.println("0. Exit");
            displaySeperator();

            int choice = readIntInput("Enter Your Choice : ");
            displaySeperator();

            switch (choice){
                case 1 -> {
                    displaySelection("Approve Withdrawal Application");
                    displaySeperator();

                    Application selectedApplication = null;
                    do{
                        String ApplicantNRIC = readStringInput("Enter the Applicant's NRIC (or 0 to return): ");
                        if(ApplicantNRIC.equals("0")) {displaySeperator();return;}
                        selectedApplication = withdrawalApplications.stream().filter(application -> application.getApplicantNRIC().equals(ApplicantNRIC)).findFirst().orElse(null);

                        if (selectedApplication == null){
                            System.out.println("Applicant Not Found. Make sure the NRIC is exact");
                            displaySeperator();
                        }
                    } while(selectedApplication == null);

                    if(selectedApplication.getApplicationStatus().equals(Application.ApplicationStatus.BOOKED_IN_WITHDRAWAL)){
                        List<Project> allProjects = ProjectLoader.loadProjects();
                        for (Project project : allProjects) {
                            if (project.getProjectName().equals(selectedApplication.getProjectName())) {
                                if (selectedApplication.getFlatType().equals(project.getType1())) {
                                    project.setNumUnitsType1(project.getNumUnitsType1() + 1);
                                } else if (selectedApplication.getFlatType().equals(project.getType2())) {
                                    project.setNumUnitsType2(project.getNumUnitsType2() + 1);
                                }
                                System.out.println("Available flat num changed. ");
                                break;
                            }
                        }

                        ProjectLoader.saveProjects(allProjects);

                    }

                    selectedApplication.setApplicationStatus(Application.ApplicationStatus.UNSUCCESSFUL);
                    saveApplicationToCSV(selectedApplication);
                    System.out.println("Approved");
                    displaySeperator();
                }

                case 2 -> {
                    displaySelection("Reject Withdrawal Application");
                    displaySeperator();

                    Application selectedApplication = null;
                    do{
                        String ApplicantNRIC = readStringInput("Enter the Applicant's NRIC (or 0 to return): ");
                        if(ApplicantNRIC.equals("0")) {displaySeperator();return;}
                        selectedApplication = withdrawalApplications.stream().filter(application -> application.getApplicantNRIC().equals(ApplicantNRIC)).findFirst().orElse(null);

                        if (selectedApplication == null){
                            System.out.println("Applicant Not Found. Make sure the NRIC is exact");
                            displaySeperator();
                        }
                    } while(selectedApplication == null);

                    selectedApplication.setApplicationStatus(
                            Application.ApplicationStatus.valueOf(
                                    selectedApplication.getApplicationStatus().name().replaceFirst("\\Q_IN_WITHDRAWAL\\E$", "")));
                    saveApplicationToCSV(selectedApplication);
                }

                case 0 -> {
                    System.out.println("Exiting. . .");
                    exit = true;
                }

                default -> System.out.println("Invalid option. Please choose 1, 2, or 0.");
            }
        }
    }

    public void generateReport() {

        List<Application> applicationList = ApplicationLoader.loadApplications();
        // Display filters
        displaySubHeader("Choose Filters To Apply");

        System.out.printf("%-25s %-25s %-25s%n", "Marital Status", "Flat Type", "Application Status");
        System.out.printf("%-25s %-25s %-25s%n", "1. SINGLE", "1. 2 - ROOM", "1. PENDING");
        System.out.printf("%-25s %-25s %-25s%n", "2. MARRIED", "2. 3 - ROOM", "2. SUCCESSFUL");
        System.out.printf("%-25s %-25s %-25s%n", "", "", "3. UNSUCCESSFUL");
        System.out.printf("%-25s %-25s %-25s%n", "", "", "4. BOOKED");
        System.out.printf("%-25s %-25s %-25s%n", "", "", "5. IN WITHDRAWAL");
        System.out.printf("%-25s %-25s %-25s%n", "", "", "6. SUCCESS WITHDRAWAL");
        System.out.println();


        String text = readStringInput("Enter your Filters (Separate by Space - E.g: SINGLE 2-Room) (0 for Age Filter or None): ").toLowerCase();
        displaySeperator();

        if (text.equals("0")){

            int[] range = readInputRange("Enter Age Filter (Range Separated by Space - E.g: 24 30): ");
            displaySeperator();
            List<String> headers = List.of("Name","Project", "Flat Type", "Age", "Marital Status", "Status");
            List<String[]> rows = filterByRange(applicationList,range).stream()
                    .map(application -> {
                        return new String[]{application.getApplicantName(),application.getProjectName(), String.valueOf(application.getFlatType()), String.valueOf(application.getAge()), String.valueOf(application.getMaritalStatus()), String.valueOf(application.getApplicationStatus())};
                    }).toList();

            displayHeader( "REPORT");
            displayTable(rows, headers);

        } else if (text.equals("none")){
            List<String> headers = List.of("Name","Project", "Flat Type", "Age", "Marital Status", "Status");
            List<String[]> rows = applicationList.stream()
                    .map(application -> {
                        return new String[]{application.getApplicantName(),application.getProjectName(), String.valueOf(application.getFlatType()), String.valueOf(application.getAge()), String.valueOf(application.getMaritalStatus()), String.valueOf(application.getApplicationStatus())};
                    }).toList();

            displayHeader( "REPORT");
            displayTable(rows, headers);
        }
        else {
            List<String> headers = List.of("Name","Project", "Flat Type", "Age", "Marital Status", "Status");

            List<String> keywords = List.of(text.split(" "));
            displaySeperator();
            List<String[]> rows = filterByKeywords(applicationList, keywords).stream().map(application -> {
                return new String[]{application.getApplicantName(),application.getProjectName(), String.valueOf(application.getFlatType()), String.valueOf(application.getAge()), String.valueOf(application.getMaritalStatus()), String.valueOf(application.getApplicationStatus())};
            }).toList();

            displayHeader( "REPORT");
            displayTable(rows, headers);
        }

        displaySeperator();
    }

    public void viewAllEnquiries(){
        displayHeader("ENQUIRIES LIST");

        Enquiry.getEnquiriesList().forEach(
                enquiry -> {
                    System.out.println(enquiry);
                    displaySeperator();
                }
        );



        System.out.println("All "+ (long) Enquiry.getEnquiriesList().size() +" Enquiries Have been Listed");
        displaySeperator();

        String choice = readStringInput("Do you want to search for Applicant or Project or Enquiry? (Yes / No) : ").toLowerCase();
        displaySeperator();
        switch (choice){
            case "y","yes" -> {
                String input = readStringInput("Enter Applicant Name or Project Name or Enquiry ID: ");
                displaySeperator();
                List<String> keywords = List.of(input.split(" "));
                filterByKeywords(Enquiry.getEnquiriesList(), keywords).forEach(enquiry -> {
                    System.out.println(enquiry);
                    displaySeperator();
                });
            }
        }

    }

    public void viewCurrentProjectEnquiries(){
        boolean exit = false;
        displayHeader("PROJECT LIST - WITHDRAWAL");

        List<String> headers = List.of("Project Name", "Pending Enquiries");
        List<String[]> rows = getManagerFilteredProjectList(getNRIC())
                .stream()
                .map(project -> {
                    long pendingCount = getProjectFilteredEnquiryList(project.getProjectName()).stream().filter(enquiry -> enquiry.getReply().equalsIgnoreCase("none")).count();
                    return new String[]{project.getProjectName(), String.valueOf(pendingCount)};
                })
                .toList();

        displayTable(rows, headers);
        displaySeperator();

        Project selectedProject = selectProject(getManagerFilteredProjectList(getNRIC()));
        if (selectedProject == null) {
            return;
        }

        while(!exit){
            System.out.println("Project "+ selectedProject.getProjectName() +" Enquiries: ");
            displaySeperator();

            getProjectFilteredEnquiryList(selectedProject.getProjectName())
                    .forEach(enquiry -> {
                        System.out.println(enquiry);
                        displaySeperator();
                    });

            System.out.println("Do you want to reply to an Enquiry?");
            displaySeperator();

            System.out.println("1. YES");
            System.out.println("0. Exit");
            displaySeperator();

            int choice = readIntInput("Enter your Choice: ");
            displaySeperator();

            switch (choice){
                case 1 -> {
                    System.out.println("You have selected: Reply to an Enquiry");
                    displaySeperator();

                    Enquiry selectedEnquiry = null;
                    do{
                        int EnqID = readIntInput("Enter Enquiry ID (or 0 to return): ");

                        if(EnqID == 0) {displaySeperator();return;}

                        selectedEnquiry = getProjectFilteredEnquiryList(selectedProject.getProjectName()).stream().filter(enquiry -> enquiry.getEnquiryID().equals(String.valueOf(EnqID))).findFirst().orElse(null);

                        if (selectedEnquiry == null){
                            System.out.println("Officer Not Found. Make sure the NRIC is exact");
                            displaySeperator();
                        }
                    } while(selectedEnquiry == null);


                    System.out.println("Enquiry Content: ");
                    System.out.println(selectedEnquiry.getContent());

                    System.out.println("Enter your Reply: ");
//                    System.out.print("");
//                    String reply = sc.nextLine().replace(",", ";");
                    String reply = readStringInput("Reply: ").replace(",", ";");
                    reply = getName() + "(Manager) -> " + reply;
                    displaySeperator();

                    selectedEnquiry.setReply(reply);
                    updateEnquiriesToCSV(selectedEnquiry);
                }

                case 0 -> {
                    System.out.println("Exiting. . .");
                    displaySeperator();
                    exit = true;
                }
            }
        }
    }
}