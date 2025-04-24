package Loaders;

import Classes.Enquiry;
import Loaders.CSV.CSVLoader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnquiryLoader {
    private static final CSVLoader<Enquiry> instance = CSVLoader.getInstance(
            Enquiry.class,
            "Enquiry",
            record -> new Enquiry(
                    record.get("Enquiry ID"),
                    record.get("Project Name"),
                    record.get("Applicant Name"),
                    record.get("NRIC"),
                    record.get("content"),
                    record.get("reply")
            ),
            enquiry -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("Enquiry ID", enquiry.getEnquiryID());
                map.put("Project Name", enquiry.getProjectName());
                map.put("Applicant Name", enquiry.getApplicantName());
                map.put("NRIC", enquiry.getNRIC());
                map.put("content", enquiry.getContent());
                map.put("reply", enquiry.getReply());
                return map;
            }
    );

    public static List<Enquiry> loadEnquires() {
        return instance.loadRecords();
    }

    public static void saveEnquiries(List<Enquiry> enquiries) {
        instance.saveRecords(enquiries);
    }

    public static void addEnquiryToCSV(Enquiry enquiry){
        List<Enquiry> enquiryList = EnquiryLoader.loadEnquires();
        enquiryList.add(enquiry);
        EnquiryLoader.saveEnquiries(enquiryList);
    }

    public static void updateEnquiriesToCSV(Enquiry enquiry){
        List<Enquiry> enquiryList = EnquiryLoader.loadEnquires();
        enquiryList =  enquiryList.stream()
                .map(e -> e.getEnquiryID().equals(enquiry.getEnquiryID()) ? enquiry : e)
                .toList();
        EnquiryLoader.saveEnquiries(enquiryList);
    }
}
