package Loaders;

import Classes.Application;
import Loaders.CSV.CSVLoader;
import Users.Applicant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApplicationLoader {
    private static final CSVLoader<Application> instance = CSVLoader.getInstance(
            Application.class,
            "Application",
            record -> new Application(
                    record.get("Project Name"),
                    record.get("Applicant Name"),
                    record.get("NRIC"),
                    Integer.parseInt(record.get("Age")),
                    Applicant.MaritalStatus.valueOf(record.get("Marital Status").toUpperCase()),
                    record.get("Flat Type"),
                    Application.ApplicationStatus.valueOf(record.get("Application Status").toUpperCase())
            ),
            application -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Project Name", application.getProjectName());
                map.put("Applicant Name", application.getApplicantName());
                map.put("NRIC", application.getApplicantNRIC());
                map.put("Age", String.valueOf(application.getAge()));
                map.put("Marital Status", application.getMaritalStatus().name());
                map.put("Flat Type", application.getFlatType());
                map.put("Application Status", application.getApplicationStatus().name());
                return map;
            }
    );

    public static List<Application> loadApplications() {
        return instance.loadRecords();
    }
    public static void saveApplications(List<Application> application) {
        instance.saveRecords(application);
    }

    public static void saveApplicationToCSV(Application application){
        List<Application> applicationList = ApplicationLoader.loadApplications();

        Optional<Application> existing = applicationList.stream()
                .filter(a -> a.getApplicantNRIC().equals(application.getApplicantNRIC()))
                .findFirst();

        if (existing.isPresent()) {
            applicationList = applicationList.stream()
                    .map(a -> a.getApplicantNRIC().equals(application.getApplicantNRIC()) ? application : a)
                    .toList();
        } else {
            applicationList.add(application);
        }

        ApplicationLoader.saveApplications(applicationList);
    }
}
