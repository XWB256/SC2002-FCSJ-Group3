package Loaders;

import Loaders.CSV.CSVLoader;
import Users.Applicant;
import Users.HDBOfficer;
import Users.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HDBOfficerLoader {
    private static final CSVLoader<HDBOfficer> instance = CSVLoader.getInstance(
            HDBOfficer.class,
            "Officer",
            record -> new HDBOfficer(
                    record.get("Name"),
                    record.get("NRIC"),
                    Integer.parseInt(record.get("Age")),
                    HDBOfficer.MaritalStatus.valueOf(record.get("Marital Status").toUpperCase()),
                    record.get("Password")
            ),
            officer -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Name", officer.getName());
                map.put("NRIC", officer.getNRIC());
                map.put("Age", String.valueOf(officer.getAge()));
                map.put("Marital Status", officer.getMaritalStatus().name());
                map.put("Password", officer.getPassword());
                return map;
            }
    );

    public static List<HDBOfficer> loadOfficers() {
        return instance.loadRecords();
    }

    public static void saveOfficers(List<HDBOfficer> officers) {
        instance.saveRecords(officers);
    }

    public static void saveToOfficerCSV(HDBOfficer officer){
        saveOfficers(User.updateUser(officer, loadOfficers()));
    }

}
