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
import Utilities.Utility;

import java.util.*;
import java.util.stream.Collectors;

import static Utilities.Display.displayHeader;
import static Utilities.Display.displaySeperator;
import static Utilities.ReadInput.readIntInput;
import static Utilities.ReadInput.readStringInput;

public class HDBOfficer extends User{
    // Attributes
    private List<Project> assignedProjects;
    private String[] assignedProjectNames;
    private List<OfficerApplication> registeredApplications;
    private List<String> registeredProjectNames;
    private String projectAsApplicant;
    //private List<Project> filteredAllProjects; // persists user filters

    // Constructor
    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, password, maritalStatus);
        retrieveAssignedProjects();
        retrieveAssignedProjectNames();
        retrieveRegisteredApplications();
        retrieveRegisteredProjectNames();
        retrieveProjectAsApplicant();
        //this.filteredAllProjects = Filter.defaultFilter(getAllVisibleAndAssignedProjects());
    }

    // Retrieve Data
    public void retrieveAssignedProjects() {
        List<Project> allProjects = ProjectLoader.loadProjects();
        List<Project> assignedProjects = new ArrayList<>();

        for (Project project : allProjects) {
            String officerField = project.getOfficerNRIC();
            if(officerField.isEmpty()){continue;}
            String[] officerNRICs = officerField.split(";\\s*");
            for (String officerNRIC : officerNRICs) {
                if (officerNRIC.equals(this.getNRIC())) {
                    assignedProjects.add(project);
                    break; // no need to check more officers
                }
            }
        }
        this.assignedProjects = assignedProjects;
    }

    public void retrieveAssignedProjectNames(){
        this.assignedProjectNames = assignedProjects.stream()
                .map(Project::getProjectName)
                .toArray(String[]::new);
    }

    public void retrieveRegisteredApplications() {
        List<OfficerApplication> allTeamApplications = OfficerApplicationLoader.loadOfficerApplications();
        List<OfficerApplication> registeredApplications = new ArrayList<>();

        for (OfficerApplication application : allTeamApplications) {
            if (this.getNRIC().equals(application.getOfficerNRIC())) {
                registeredApplications.add(application); // Store the whole object
            }
        }
        this.registeredApplications = registeredApplications;
    }

    public void retrieveRegisteredProjectNames(){
        List<OfficerApplication> allTeamApplications = OfficerApplicationLoader.loadOfficerApplications();
        List<String> registeredProjects = new ArrayList<>();
        for (OfficerApplication application: allTeamApplications){
            if (this.getNRIC().equals(application.getOfficerNRIC())){
                if(!application.getOfficerApplicationStatus().equals(OfficerApplication.OfficerApplicationStatus.UNSUCCESSFUL))
                    registeredProjects.add(application.getProjectName());
            }
        }
        this.registeredProjectNames =  registeredProjects;
    }

    public void retrieveProjectAsApplicant(){
        for (Application application : ApplicationLoader.loadApplications()){
            if (application.getApplicantNRIC().equalsIgnoreCase(this.getNRIC())){
                this.projectAsApplicant = application.getProjectName();
                break;
            }
        }
    }

    public void refreshAllOfficerData() {
        retrieveAssignedProjects();
        retrieveAssignedProjectNames();
        retrieveRegisteredApplications();
        retrieveRegisteredProjectNames();
        retrieveProjectAsApplicant();
    }

    // Getter
    public String getAssignedProjectNames() {
        return String.join(";", assignedProjectNames);
    }

    public List<String> getRegisteredProjects() {
        return registeredProjectNames;
    }

    public String getProjectAsApplicant() {
        return projectAsApplicant;
    }

    // Methods

    public List<Project> viewAllProjects() {
        // Reload latest projects from storage
        List<Project> allProjects = ProjectLoader.loadProjects();

        // Convert assigned project names from String[] to Set for fast lookup
        Set<String> assignedNamesSet = new HashSet<>(Arrays.asList(assignedProjectNames));

        // Filter: show project if it's visible OR assigned to this officer
        return allProjects.stream()
                .filter(project -> project.getVisibility().equals(Project.Visibility.ON)
                        || assignedNamesSet.contains(project.getProjectName()))
                .collect(Collectors.toList());
    }

    private boolean isDateOverlap(Project p1, Project p2) {
        // Assume these methods return java.time.LocalDate
        return !(p1.getApplicationCloseDate().isBefore(p2.getApplicationOpenDate()) ||
                p2.getApplicationCloseDate().isBefore(p1.getApplicationOpenDate()));
    }

    private boolean hasOverlappingOfficerProject(Project newProject) {
        // Store registered projects for date comparison
        List<Project> registeredProjects = ProjectLoader.loadProjects().stream()
                .filter(project -> registeredProjectNames.contains(project.getProjectName()))
                .collect(Collectors.toList());

        // Check for date overlap in assigned projects
        for (Project assigned : assignedProjects) {
            if (isDateOverlap(assigned, newProject)) {
                return true;
            }
        }

        // Check for date overlap in registered projects (initiated but not successful)
        for (Project registered : registeredProjects) {
            if (isDateOverlap(registered, newProject)) {
                return true; // Overlap found in registered projects
            }
        }

        // No overlap found in both lists
        return false;
    }

    public List<Project> getAvailableProjectsToRegister() {
        List<Project> availableProjects = new ArrayList<>();

        for (Project project : ProjectLoader.loadProjects()) {
            if (!project.getProjectName().equalsIgnoreCase(projectAsApplicant) &&
                    project.getOfficerSlot() >= 1 &&
                    !registeredProjectNames.contains(project.getProjectName()) &&
                    !hasOverlappingOfficerProject(project)) {
                availableProjects.add(project);
            }
        }
        return availableProjects;
    }


    public void registerForProject() {

        /*
        for(Project pro : assignedProjects){
            if(pro.getApplicationOpenDate().isBefore(Utility.CURRENT_DATE_ORIGINAL)
                    && pro.getApplicationCloseDate().isAfter(Utility.CURRENT_DATE_ORIGINAL)){
                System.out.println("You already in charge of "+pro.getProjectName()+"!");
                return;
            }
        }
         */

        List<OfficerApplication> allApps = OfficerApplicationLoader.loadOfficerApplications();
        for (OfficerApplication oapp : allApps){
            if(oapp.getOfficerNRIC().equals(getNRIC())
                    &&oapp.getOfficerApplicationStatus().equals(OfficerApplication.OfficerApplicationStatus.INITIATED)){
                System.out.println("You have already had a pending application!");
                return;
            }
        }


        // Get the available projects (filtered based on current filters)
        List<Project> availableProjects = getAvailableProjectsToRegister();

        if (availableProjects.isEmpty()) {
            System.out.println("No available projects to register for.");
            return;
        }

        // Print the available project names
        System.out.println("All available projects & num of slots: ");
        availableProjects
                .forEach(project -> System.out.println(project.getProjectName() + ", " + project.getOfficerSlot()));

        displaySeperator();
        String registerProjectName = readStringInput("Enter project name to register (or 0 to return): ");
        if(registerProjectName.equals("0")) {return;}

        // Check if the selected project exists in the available list
        Project targetProject = null;
        for (Project project : availableProjects) {
            if (project.getProjectName().equals(registerProjectName)) {
                targetProject = project;
                break;  // Exit loop once the project is found
            }
        }
        if (targetProject == null) {
            System.out.println("No available projects found.");
            return;
        }

        // If targetProject matches an available project, proceed with registration
        OfficerApplication officerApplication = new OfficerApplication(
                registerProjectName,
                this.getName(),
                this.getNRIC(),
                OfficerApplication.OfficerApplicationStatus.INITIATED
        );

        // Add the new application to the list and save it
        boolean exist = false;
        for(OfficerApplication app : allApps){
            if(app.getProjectName().equals(officerApplication.getProjectName())
                    && app.getOfficerNRIC().equals(officerApplication.getOfficerNRIC())){
                app.setOfficerApplicationStatus(OfficerApplication.OfficerApplicationStatus.INITIATED);
                exist = true;
                break;
            }
        }
        if(!exist)
            allApps.add(officerApplication);
        OfficerApplicationLoader.saveOfficerApplications(allApps);

        System.out.printf("Successfully registered for Project: %s\n", registerProjectName);
    }

    public void viewOfficerApplicationStatus(){
        //displaySeperator();
        System.out.println("All Project Application Status: ");
        for (OfficerApplication application : registeredApplications){
            System.out.printf("%s: %s\n", application.getProjectName(), application.getOfficerApplicationStatus());
        }
    }

    public void viewAssignedProjectDetails(){
        //displaySeperator();
        // Check if assignedProjects is empty
        if (assignedProjects == null || assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            return;
        }

        // Print all assigned projects
        System.out.println("Your Assigned Projects: ");
        assignedProjects.stream()
                .map(Project::getProjectName)  // Get the project names
                .forEach(System.out::println);  // Print each project name

        displaySeperator();
        String option = readStringInput("Which project would you like to view the details of?: ");

        // Check if the option exists in assignedProjects and prints selected project details
        displaySeperator();
        assignedProjects.stream()
                .filter(project -> option.equals(project.getProjectName()))
                .findFirst()
                .ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("You are not assigned to this project.")
                );
    }

    public void replyToEnquires(){

        while (true) {

            List<Enquiry> allEnquiries = EnquiryLoader.loadEnquires();
            List<Enquiry> assignedProjectsEnquiries = new ArrayList<>();

            //displaySeperator();
            //System.out.println("All Enquiries: ");
            //int i = 1;

            // Print all enquires for projects Officer is in charge of
            for(Project project : assignedProjects){
                for (Enquiry enquiry : allEnquiries){
                    if(project.getProjectName().equals(enquiry.getProjectName())){
                        assignedProjectsEnquiries.add(enquiry);
                        //System.out.printf("%d:\n%s", i++, enquiry);
                        //displaySeperator();
                    }
                }
            }
            if (assignedProjectsEnquiries.isEmpty()) {
                displaySeperator();
                System.out.println("No enquiries found for your assigned projects.");
                return;
            }

            // (A) enter enquiry index
            displaySeperator();
            List<Enquiry> currentEnquiries = Filter.filterRun(assignedProjectsEnquiries);
            String id = readStringInput("Enter enquiry ID to reply, or 0 to return: ");
            //displaySeperator();

            if (id.equals("0")) {
                return; // exit
            }

            Enquiry selectedEnquiry = assignedProjectsEnquiries.stream()
                    .filter(enquiry -> enquiry.getEnquiryID().equals(id))
                    .findFirst()
                    .orElse(null);

            if (selectedEnquiry == null) {
                System.out.println("Enquiry Not Found. Make sure the Enquiry ID is exact.");
                displaySeperator();
                continue;
            }

            String reply = readStringInput("Enter your reply (or 0 to return): ").replace(",", ";");
            if (reply.equals("0")) continue; // return to (A)
            reply = getName() + "(Officer) -> " + reply;

            // set the reply (assuming a setReply method)
            selectedEnquiry.setReply(reply);
            System.out.println("Reply sent successfully.");

            // Save updated enquiries
            EnquiryLoader.saveEnquiries(allEnquiries);

            // return to (A)
        }
    }

    public void acceptBookingApplication() {

        List<Application> allApplications = ApplicationLoader.loadApplications();
        List<Project> allProjects = ProjectLoader.loadProjects();
        List<String> assignedList = Arrays.asList(assignedProjectNames);

        List<Application> AppInCharge = allApplications.stream()
                .filter(application -> assignedList.contains(application.getProjectName()) && Application.ApplicationStatus.APPLY_FOR_BOOKING.equals(application.getApplicationStatus()))
                .collect(Collectors.toList());

        while (true) { // (A) start loop

            List<Application> currentAppList = Filter.filterRun(AppInCharge);

            displaySeperator();
            String inputNRIC = readStringInput("Enter Applicant NRIC to retrieve application (or 0 to return): ");
            displaySeperator();
            if (inputNRIC.equals("0")) return;

            Application matchedApp = null;
            // Find matching application with status = SUCCESSFUL
            for (Application application : currentAppList) {
                if (application.getApplicantNRIC().equalsIgnoreCase(inputNRIC)) {
                    matchedApp = application;
                    break;
                }
            }
            if (matchedApp == null) {
                System.out.println("No matching successful application found for this NRIC.");
                continue; // return to (A)
            }

            // Display application details
            System.out.println("Application Details:\n" + matchedApp);
            displaySeperator();

            // Ask user to proceed
            int confirm = readIntInput("Type 1 to confirm booking (or 0 to return): ");
            if (confirm != 1) {
                continue; // go back to (A)
            }

            // Process booking
            matchedApp.setApplicationStatus(Application.ApplicationStatus.BOOKED);

            Project matchedProj = null;
            for (Project project : allProjects) {
                if (project.getProjectName().equals(matchedApp.getProjectName())) {
                    matchedProj = project;
                    if (matchedApp.getFlatType().equals(project.getType1())) {
                        project.setNumUnitsType1(project.getNumUnitsType1() - 1);
                    } else if (matchedApp.getFlatType().equals(project.getType2())) {
                        project.setNumUnitsType2(project.getNumUnitsType2() - 1);
                    }
                    break;
                }
            }

            // Save updated records
            ApplicationLoader.saveApplications(allApplications);
            ProjectLoader.saveProjects(allProjects);

            System.out.println("Booking confirmed and records updated.\n");

            System.out.println();
            int confirmGen = readIntInput("Type 1 to generate booking receipt (or 0 to return): ");
            if (confirmGen != 1){
                return;
            }
            else{
                generateReceipt(matchedApp, Objects.requireNonNull(matchedProj));
            }
        }
    }

    public void generateReceipt(Application application, Project project){
        displaySeperator();
        displayHeader("Booking Receipt");
        System.out.println(application.toString());
        displaySeperator();
        displayHeader("Project Details");
        System.out.println(project.toString());
        displaySeperator();
    }

    public void generateReceiptFromNric(){

        List<Application> allApplications = ApplicationLoader.loadApplications();
        List<Project> allProjects = ProjectLoader.loadProjects();
        List<String> assignedList = Arrays.asList(assignedProjectNames);

        List<Application> AppInCharge = allApplications.stream()
                .filter(application -> assignedList.contains(application.getProjectName()) && Application.ApplicationStatus.BOOKED.equals(application.getApplicationStatus()))
                .collect(Collectors.toList());

        while(true){ // (A) start loop

            List<Application> currentAppList = Filter.filterRun(AppInCharge);

            displaySeperator();
            String inputNRIC = readStringInput("Please enter NRIC of Booking Application to Print Receipt (or 0 to return): ");
            if (inputNRIC.equals("0")) return;

            Application matchedApp = null;

            // Find matching application with status = BOOKED
            for (Application application : currentAppList) {
                if (application.getApplicantNRIC().equalsIgnoreCase(inputNRIC) &&
                        Application.ApplicationStatus.BOOKED.equals(application.getApplicationStatus())) {
                    matchedApp = application;
                    break;
                }
            }
            if (matchedApp == null) {
                System.out.println("No matching booked application found for this NRIC.");
                continue; // return to (A)
            }

            // Find matched project to print project details
            Project matchedProj = null;
            for (Project project : allProjects) {
                if (project.getProjectName().equals(matchedApp.getProjectName())) {
                    matchedProj = project;
                    break;
                }
            }

            // Print receipt
            generateReceipt(matchedApp, Objects.requireNonNull(matchedProj));

            // Ask if user wants to generate another receipt
            System.out.println();
            int choice = readIntInput("Type 1 if you would like to print another receipt (or 0 to return): ");
            if (choice != 1){
                return;
            }
        }
    }

    public Applicant CreateApplicant() {
        return new Applicant(
                this.getName(),
                this.getNRIC(),
                this.getAge(),
                this.getMaritalStatus(),
                this.getPassword()
        );
    }

    @Override
    public String toString() {
        return "Officer{" +
                "name='" + getName() + '\'' +
                ", nric='" + getNRIC() + '\'' +
                ", age=" + getAge() +
                ", maritalStatus='" + getMaritalStatus() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", projectInCharge='" + getAssignedProjectNames() + '\'' +
                ", project='" + getProjectAsApplicant() + '\'' +
                '}';
    }
}