package Loaders;

import Classes.OfficerApplication;
import Loaders.CSV.CSVLoader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OfficerApplicationLoader {
    private static final CSVLoader<OfficerApplication> instance = CSVLoader.getInstance(
            OfficerApplication.class,
            "OfficerApplication",
            record -> new OfficerApplication(
                    record.get("Project Name"),
                    record.get("Officer Name"),
                    record.get("NRIC"),
                    OfficerApplication.OfficerApplicationStatus.valueOf(record.get("Officer Application Status").toUpperCase())
            ),
            officerApplication -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Project Name", officerApplication.getProjectName());
                map.put("Officer Name", officerApplication.getOfficerName());
                map.put("NRIC", officerApplication.getOfficerNRIC());
                map.put("Officer Application Status", officerApplication.getOfficerApplicationStatus().name());
                return map;
            }
    );

    public static List<OfficerApplication> loadOfficerApplications() {
        return instance.loadRecords();
    }

    public static void saveOfficerApplications(List<OfficerApplication> officerApplications) {
        instance.saveRecords(officerApplications);
    }

    public static void saveOfficerApplicationToCSV(OfficerApplication officerApplication){
        List<OfficerApplication> officerApplicationList = OfficerApplicationLoader.loadOfficerApplications();

        Optional<OfficerApplication> existing = officerApplicationList.stream()
                .filter(o -> o.getOfficerNRIC().equals(officerApplication.getOfficerNRIC()))
                .findFirst();

        if (existing.isPresent()) {
            officerApplicationList = officerApplicationList.stream()
                    .map(o -> officerApplication.getProjectName().equals(o.getProjectName()) && o.getOfficerNRIC().equals(officerApplication.getOfficerNRIC()) ? officerApplication : o)
                    .toList();
        } else {
            officerApplicationList.add(officerApplication);
        }

        OfficerApplicationLoader.saveOfficerApplications(officerApplicationList);
    }


}
