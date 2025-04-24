package Loaders;

import Loaders.CSV.CSVLoader;
import Users.Applicant;
import Users.HDBManager;
import Users.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApplicantLoader {
    private static final CSVLoader<Applicant> instance = CSVLoader.getInstance(
            Applicant.class,
            "Applicant",
            record -> new Applicant(
                    record.get("Name"),
                    record.get("NRIC"),
                    Integer.parseInt(record.get("Age")),
                    Applicant.MaritalStatus.valueOf(record.get("Marital Status").toUpperCase()),
                    record.get("Password")
            ),
            applicant -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Name", applicant.getName());
                map.put("NRIC", applicant.getNRIC());
                map.put("Age", String.valueOf(applicant.getAge()));
                map.put("Marital Status", applicant.getMaritalStatus().name());
                map.put("Password", applicant.getPassword());
                return map;
            }
    );

    public static List<Applicant> loadApplicants() {
        return instance.loadRecords();
    }

    public static void saveApplicants(List<Applicant> applicants) {
        instance.saveRecords(applicants);
    }

    public static void saveToApplicantCSV(Applicant applicant){
        saveApplicants(User.updateUser(applicant, loadApplicants()));
    }
}
