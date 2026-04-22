# WisePlanner

## Team Members
Qingyang Ran  
Ammar Hassan  
Yixiao Liu  
Ce Zheng

## Project Overview
### What is it?

WisePlanner is a Java app that connects to Canvas LMS and Gemini that shows courses, announcements, and assignments as well as integrated Gemini features and a smart to-do list, calendar all in one clean dashboard!

### How it works?
Authenticates with Canvas API using the student's personal access token. Fetches courses, assignments, and announcements in real-time and renders them in a modern UI.

### Gemini!
Integrates Google Gemini API to provide daily grade analysis per course and personlised motivational insights.

## System Architecture

#### GUI Layer:
JavaFX + FXML

#### Kernel
WisePlanner Kernel

#### Service Layer
CanvasService + TaskManager + ScheduleManager + DashboardService

#### API/Storage
Canvas API + Gemini API + JSON Files

## Tech Stack
* Language: Java 21
* UI Framework: JavaFX 25
* Build Tool: Gradle
* JSON Parser: Gson
* API: Canvas API/Google Gemini
* Testing: JUnit

