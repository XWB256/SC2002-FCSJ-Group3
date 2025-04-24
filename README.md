# SC2002 Build-To-Order (BTO) Management System

Github Repository: https://github.com/Aishik-KS/FCSJ-Grp3
Or (in case encountered some issues): https://github.com/XWB256/SC2002-FCSJ-Group3

## 1. Introduction

This is a command-line Java application developed as part of the SC2002 Object-Oriented Design & Programming course at NTU. The system is a simulation of a Build-To-Order (BTO) Management System that models real-world functionalities for different user roles in a Housing Development Board (HDB) context: Applicants, HDB Officers, and HDB Managers.

Users interact with the system through a Singpass-like login (using NRIC), and the application supports core operations such as viewing projects, applying for flats, booking flats, responding to enquiries, and managing project listings.

## 2. Key Features

You can refer to the appendix "SC2002 Test Cases.pdf" in the submission on NTULearn for a complete list of features and functionality of the system. Here are some of the key features of the system:

- Singpass-based user authentication
- Role-specific user access (Applicant, HDB Officer, HDB Manager)
- Project Creation, Modification and Deletion (HDB Manager)
- Project Application and Withdrawal (Applicant)
- Flat Booking and Receipt Generation (HDB Officer)

## 3. User Guide

### 3.1 Execute the Program

This program is developed using Java and requires the Java Development Kit (JDK) 23 to be installed on your computer. To execute the program, follow these steps using IntelliJ IDEA as the development environment:

1. Open IntelliJ IDEA and select "Open" to load the project folder.
2. Make sure you have the JDK installed.
3. Ensure that JDK 23 is installed
4. Locate the Main class containing the main method.
5. Right-click the file and select "Run 'Main'".
6. The program will start, and the login screen will be displayed in the terminal.

### 3.2 Login

Enter NRIC and password to login to the system. The system will show a role-specific menu according to the user's role.

#### 3.2.1 Sample Accounts for Testing:

| NRIC      | Password | Role        |
| --------- | -------- | ----------- |
| S9876543C | password | Applicant   |
| T2109876H | password | HDB Officer |
| S5678901G | password | HDB Manager |

### 3.3 Role-specific Menu

After a you log in as a specific role, you can access the menu. You can choose from the shown options and perform the desired action.

#### 3.3.1 Applicant Menu

| No  | Function                 | No  | Function         |
|-----|--------------------------|-----|------------------|
| 01  | View Available Projects  | 06  | Create Enquiry   |
| 02  | Apply for a Project      | 07  | Edit Enquiry     |
| 03  | View Applied Project     | 08  | Delete Enquiry   |
| 04  | Book a Flat              | 09  | View Enquiries   |
| 05  | Withdraw Application     |     |                  |
|     |                          |     |                  |
| 10  | Change Password          |     |                  |
| 00  | Log Out                  |     |                  |



#### 3.3.2 HDB Officer Menu

| No  | Function                           | No  | Function                    |
|-----|------------------------------------|-----|-----------------------------|
| 01  | View All Accessible Projects       | 05  | Reply to Project Enquiries  |
| 02  | Apply to Join Project as Officer   | 06  | Accept Booking Application  |
| 03  | View Officer Application Status    | 07  | Generate Receipt            |
| 04  | View Assigned Project Details      |     |                             |
|     |                                    |     |                             |
| 08  | Change Password                    |     |                             |
| 09  | Enter Applicant Menu               |     |                             |
| 00  | Log Out                            |     |                             |


#### 3.3.3 HDB Manager Menu

| No  | Function                          | No  | Function                            |
|-----|-----------------------------------|-----|-------------------------------------|
| 01  | View Your Projects                | 07  | View Officer Registrations          |
| 02  | Toggle A Project's Visibility     | 08  | View Applicant Applications         |
| 03  | Create A New Project              | 09  | View Applicant Withdrawal Requests  |
| 04  | Edit A Project                    | 10  | Generate Project Report             |
| 05  | Delete A Project                  | 11  | View All Enquiries                  |
| 06  | View All Projects                 | 12  | View / Reply to Project Enquiries   |
|     |                                   |     |                                     |
| 13  | Change Password                   |     |                                     |
| 00  | Log Out                           |     |                                     |



