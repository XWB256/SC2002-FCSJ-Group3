package Loaders;

import Classes.Application;
import Loaders.CSV.CSVLoader;
import Users.HDBManager;
import Users.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HDBManagerLoader {
    private static final CSVLoader<HDBManager> instance = CSVLoader.getInstance(
            HDBManager.class,
            "Manager",
            record -> new HDBManager(
                    record.get("Name"),
                    record.get("NRIC"),
                    Integer.parseInt(record.get("Age")),
                    HDBManager.MaritalStatus.valueOf(record.get("Marital Status").toUpperCase()),
                    record.get("Password")
            ),
            manager -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Name", manager.getName());
                map.put("NRIC", manager.getNRIC());
                map.put("Age", String.valueOf(manager.getAge()));
                map.put("Marital Status", manager.getMaritalStatus().name());
                map.put("Password", manager.getPassword());
                return map;
            }
    );

    public static List<HDBManager> loadManagers() {
        return instance.loadRecords();
    }

    public static void saveManagers(List<HDBManager> managers) {
        instance.saveRecords(managers);
    }

    public static void saveToManagerCSV(HDBManager manager){
        saveManagers(User.updateUser(manager, loadManagers()));
    }
}
