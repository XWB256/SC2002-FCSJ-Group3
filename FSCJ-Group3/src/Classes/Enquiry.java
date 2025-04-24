package Classes;
import Loaders.EnquiryLoader;
import Utilities.Searchable;

import java.util.*;

public class Enquiry implements Searchable {
    private String enquiryID;
    private String projectName;
    private String applicantName;
    private String NRIC;
    private String content;
    private String reply;

    public Enquiry(String enquiryID, String projectName, String applicantName, String NRIC, String content, String reply) {
        if (enquiryID == null || enquiryID.isEmpty()) {
            setEnquiryID();
        } else {
            this.enquiryID = enquiryID;
        }
        this.projectName = projectName;
        this.applicantName = applicantName;
        this.NRIC = NRIC;
        this.content = content;
        this.reply = reply;
    }

    public String getEnquiryID() {
        return enquiryID;
    }

    public void setEnquiryID() {
        Random rand = new Random();
        boolean foundUnique = false;

        while (!foundUnique) {
            int number = rand.nextInt(100000);
            String num = String.format("%05d", number);
            boolean isDuplicate = false;

            for (Enquiry enquiry : getEnquiriesList()) {
                if (enquiry.getEnquiryID().equals(num)) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                this.enquiryID = num;
                foundUnique = true;
            }
        }
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getNRIC() {
        return NRIC;
    }

    public void setNRIC(String NRIC) {
        this.NRIC = NRIC;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Enquiry enquiry = (Enquiry) o;
        return Objects.equals(getEnquiryID(), enquiry.getEnquiryID()) && Objects.equals(getNRIC(), enquiry.getNRIC());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnquiryID(), getNRIC());
    }

    public String IDstring() {return enquiryID;}

    public String defaultString(){
        return (projectName+applicantName+NRIC+enquiryID);
    };

    public String toSearchableString() {
        return (
                projectName
                        + " " + applicantName
                        + " " + content
                        + " " + reply
        ).toLowerCase();
    }

    public List<Integer> toSearchableNum() {
        return null;
    }

    public static List<Enquiry> getEnquiriesList() {
        return EnquiryLoader.loadEnquires();
    }


    public static void deleteEnquiryCSV(Enquiry enquiry){
        List<Enquiry> enquiryList = EnquiryLoader.loadEnquires();
        enquiryList.removeIf(e -> e.equals(enquiry));
        EnquiryLoader.saveEnquiries(enquiryList);
    }

    @Override
    public String toString() {

        return String.format("%-15s: %-20s %-15s: %s%n", "Project Name", projectName, "Enquiry ID", enquiryID) +
                String.format("%-15s: %s%n", "Applicant Name", applicantName) +
                String.format("%-15s: %s%n", "Content", content) +
                String.format("%-15s: %s", "Reply", reply);
    }

}
