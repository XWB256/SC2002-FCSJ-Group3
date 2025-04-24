package Loaders;

import Classes.Project;
import Loaders.CSV.CSVLoader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProjectLoader {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final CSVLoader<Project> instance = CSVLoader.getInstance(
            Project.class,
            "Project",
            record -> new Project(
                    record.get("Project Name"),
                    record.get("Neighborhood"),
                    Project.Visibility.valueOf(record.get("Visibility").toUpperCase()),
                    record.get("Type 1"),
                    Integer.parseInt(record.get("Number of units for Type 1")),
                    Double.parseDouble(record.get("Selling price for Type 1")),
                    record.get("Type 2"),
                    Integer.parseInt(record.get("Number of units for Type 2")),
                    Double.parseDouble(record.get("Selling price for Type 2")),
                    LocalDate.parse(record.get("Application opening date"), formatter),
                    LocalDate.parse(record.get("Application closing date"), formatter),
                    record.get("Manager"),
                    record.get("Manager NRIC"),
                    Integer.parseInt(record.get("Officer Slot")),
                    record.get("Officer"),
                    record.get("Officer NRIC")
            ),
            project -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Project Name", project.getProjectName());
                map.put("Neighborhood", project.getNeighborhood());
                map.put("Visibility", project.getVisibility().toString());
                map.put("Type 1", project.getType1());
                map.put("Number of units for Type 1", String.valueOf(project.getNumUnitsType1()));
                map.put("Selling price for Type 1", String.valueOf(project.getPriceType1()));
                map.put("Type 2", project.getType2());
                map.put("Number of units for Type 2", String.valueOf(project.getNumUnitsType2()));
                map.put("Selling price for Type 2", String.valueOf(project.getPriceType2()));
                map.put("Application opening date", project.getApplicationOpenDate().format(formatter).toString());
                map.put("Application closing date", project.getApplicationCloseDate().format(formatter).toString());
                map.put("Manager", project.getManager());
                map.put("Manager NRIC", project.getManagerNRIC());
                map.put("Officer Slot", String.valueOf(project.getOfficerSlot()));
                map.put("Officer", project.getOfficer());
                map.put("Officer NRIC", project.getOfficerNRIC());
                return map;
            }
    );
    public static List<Project> loadProjects() {
        return instance.loadRecords();
    }

    public static void saveProjects(List<Project> projects) {
        instance.saveRecords(projects);
    }

    public static void saveProjectToCSV(Project project){
        List<Project> existingProjects = ProjectLoader.loadProjects();

        Optional<Project> existing = existingProjects.stream()
                .filter(p -> p.getProjectName().equals(project.getProjectName()))
                .findFirst();

        if (existing.isPresent()) {
            existingProjects = existingProjects.stream()
                    .map(p -> p.getProjectName().equals(project.getProjectName()) ? project : p)
                    .toList();
        } else {
            existingProjects.add(project);
        }

        ProjectLoader.saveProjects(existingProjects);
    }

}
