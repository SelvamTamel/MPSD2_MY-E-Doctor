
type (used in RecyclerAdapter_Appt to display list view appointment type)
1 - Physical Appointment
2 - Virtual Appointment

state (used in RecyclerAdapter_Appt to display appointment state)
1 - Pending doctor to accept appointment
2 - Accepted
3 - Declined
4 - Finished
5 - Confirmed Done
6 - Expired (expire if both party didn't join meeting after 1 hour from appointment time)
7 - After Clicking Join Meeting Button (Doctor)
8 - After Clicking Join Meeting Button (Patient)


2) To debug the app, create a new account of use existing user
Patient : liawyikai.lyk@gmail.com 
Password : 123456

Doctor : ben_lyk@live.com
Password : 123456


3) Sometimes the recycler view at "My Appointment" part is displaying duplicated data (if didnt refresh the page) is because our database is not optimized to the best. The solution to solve this problem is to implement a database table to store All Appointments, then retrieve display data from this table. In this project, we are retrieving data from different tables, virtual_appt & physical_appt, thus sometimes there is some displaying error, but the display will turn normal after refreshing the page. 


