# WisePlanner

## Team Members
Qingyang Ran  
Ammar Hassan  
Yixiao Liu  
Ce Zheng

_WisePlanner - README 
First iteration for Team F_

This version provides a command-line interface to synchronize academic responsibilities from Canvas LMS with personal daily tasks. It focuses on centralizing student workflow within a single terminal environment.

**How to Run：**

To run the application, locate Main.java (or your entry class) in IntelliJ, Right-Click, and select Run 'Main.main()'.

**User Guide：**

Upon startup, the program will perform a login check:

**Initial Setup:**

If no user data is found, you will be prompted to enter your name and a Canvas Access Token.

**Navigation:** 

Use numeric keys (1, 2, 0) to navigate the Main Menu.

**Task Management:**

The "Tasks" menu allows for local CRUD operations. Personal tasks are stored locally and persist across sessions.

**Current Limitations (Iteration 1)**

Manual Refresh: Data from Canvas does not auto-update in the background; a manual entry into the "Courses" menu is required to trigger a sync.

**Token Expiry:**

This version does not yet handle Canvas Token expiration automatically. Users must ensure their tokens are active.

Sorting: Assignments are currently displayed in the order received from the API, without custom sorting by due date.

**Technical Notes**

Input Validation: We have implemented an internal safety layer to handle non-integer inputs, ensuring the program does not crash during menu selection.

**Data Storage:**

User profile and task lists are saved in local CSV/JSON format within the project root.

**complete items**
.........