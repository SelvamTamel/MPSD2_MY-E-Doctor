# Team 15 MPSD2 Project

## Team member:
### Tamel Selvam A/L Anna Durai 0128536
### Liaw Yi Kai 0131008
### Ong Wei Yang 0125135

<hr>  

**This project presents an application about a clinic's/small hospital mobile interface features **  

<hr>  
  
## Brief introduction
A mobile application which enable patients and doctors to communicate and seek help, this project aims to help people in need with social distancing in mind, where during this pandemic, not everyone is able to go seek a doctor especially those who tested positive for the infection of Covid-19 virus and needed help. This mobile application provides help to those who need by visiting the clinic/hospital physical or else using virtual appointment and treat them professionally.  

## Features
There are a few important basic features that are implemented into this project:
 
1 - Physical Appointment
 
2 - Virtual Appointment (used in RecyclerAdapter_Appt to display appointment state) 
  i- Pending doctor to accept appointment
  ii- Accepted
  iii- Declined
  - Finished
  e) - Confirmed Done
  f) - Expired (expire if both party didn't join meeting after 1 hour from appointment time)
  g) - After Clicking Join Meeting Button (Doctor)
  h) - After Clicking Join Meeting Button (Patient)  

### Doctors:
- Register
- Login
- Update profile
- Confirm appointments

### Patients:
- Register
- Login
- Appointment with Doctors
- Reminder Medication 
- Health Education
- Search Professional Doctor
- Health Data Analysis
- Health Data Record
- Online Booking
- Qr Code Physical Check In
- SOS (emergency call)


2) To debug the app, create a new account of use existing user
Patient : liawyikai.lyk@gmail.com 
Password : 123456

Doctor : ben_lyk@live.com
Password : 123456


3) Sometimes the recycler view at "My Appointment" part is displaying duplicated data (if didnt refresh the page) is because our database is not optimized to the best. The solution to solve this problem is to implement a database table to store All Appointments, then retrieve display data from this table. In this project, we are retrieving data from different tables, virtual_appt & physical_appt, thus sometimes there is some displaying error, but the display will turn normal after refreshing the page. 
