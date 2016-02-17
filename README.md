T-110.5130-Indoor-Mapping-Project

Project Plan
------------

### 1. Introduction
<!---
Give a brief overview of the system to be developed, motivation for its
development, the environment where it will be used, and possibly the types
of users for the system.
-->

Mobile devices can be used to collect sensor data for construction of indoor
navigation systems. One such system is iMoon.*
It creates a navigation mesh from collected visual and inertial sensor data.
A user can then be located and guided based on an image captured with
their mobile device.

Mesh creation algorithms, as well as user locating algorithms,
are studied for improvement. Good algorithms can even cope with incompleteness
in the collected sensor data. Nevertheless, accuracy and completeness
of the source data are requirements to provide high quality indoor
navigation in the previously described systems.

This project develops a data collection application for indoor navigation
systems. A person mapping an indoor area will use the application to record
sensor data as accurately as her device can provide. Furthermore, the
application should assist the mapper by visualizing the collected data
and providing note keeping capabilities. This enables the mapper to verify
and manually correct the result that the device with its limitations
provides. The same properties will allow appending new sensor data to an
existing collection. The collected data is stored in a cloud. A mapped area
can be exported for use in the iMoon system.

The first version of the application is targeting researchers and property
owners that want to map their areas for enabling indoor navigation services.
Later on, there are possibilities to develop support for crowdsourcing.

\* Dong, J., Xiao, Y., Noreikis, M., Ou, Z., & Ylä-Jääski, A. (2015, November). imoon: Using smartphones for image-based indoor navigation. In Proceedings of the 13th ACM Conference on Embedded Networked Sensor Systems (pp. 85-97). ACM.

### 2. Stakeholders and staffing

Present the stakeholders of the project and list all group members along with their contact information. You may assign roles (such as project manager) to the group members and/or list the relevant interests or skills of the group members.


### 3. Goals and scope

--- 

This section describes the goals of the project group and the individual learning goals of the group members. Define the scope of the project. You may also include the business goals of the system, and describe the benefits it provides to different stakeholders, including the users.

---

Business goal:
- Reduce the stakeholders' effort to gather data for the iMoon system.

Group learning goals:
- Teamwork and collaboration skills
- Producing an end to end product in schedule


Individual learning goals for project leader Olli-Matti Saario:

- Management and facilitation of a software team
- Techonolgy decision making in a team project with members of different professional backgrounds
- Communication between project management and stakeholders
- Web backend development

Individual learning goals for Teemu Lehtinen:

- Android development
- ReactiveX driven UI-programming
- Sensors in mobile devices

Individual learning goals for Kirill Ermolov:

- Android development
- Algorithms development in Java
- ReactiveX programming

Inidividual learning goals for Mehrad Mohammedi:

- Android development
- Canvas based drawing in Android


### 4. Work practices and tools

Describe all planned work practices in a concrete but concise way. How the practice is used, what tasks/meetings/materials etc. are needed, who is responsible of what etc. In addition, list all the tools you plan to use, and describe the required development and test environments.

Possible sub-topics (ask the tutor what are relevant for your project):

* Time tracking (is some tool used, how often reported, who creates tasks, ...)
* Documenting (documentation tools, document storage and delivery to stakeholders, document authors, document review, ...)
* Communication (How do you arrange optimal amount of communication and knowledge transfer between all stakeholders?)
* Version control (which version management tool is used, what conventions to follow (check-out/check-in frequency, change log, tagging), which files to manage, ...)
* Design (How architectural design and lower level design is done? Modeling tools used? Validation?)
* Quality assurance (quality goals, QA practices, …)
* Tools (Summary of all tools used. Mention version numbers and availability information, if relevant to the project. Description of all development and test environments that are needed; both software and hardware environments.)


### 5. Schedule and resources

Design an initial schedule for the project. Consider whether you want to split the project into iterations. Allocate 120 hours per each group member for the project (5 credits * 27 hours / credit - 15 hours for lectures, etc. = 120 hours). In addition, consider any material hardware or software resources used in the project.


### 6. System overview

This chapter is a high level description of the intended solution (= the system). It typically includes

* a graph (for example a use case diagram) that defines user groups and the main functions of the system
* short textual description of the system


### 7. Requirements

Define the functional and non-functional requirements of the system.


### 8. Risk management

Identify risks in the project as well as their impact and probability. For example – what happens if one of the group members decides to quit the course? Discuss the tools or methods to either avoid these risks completely or to minimize the impact on the project if they occur. You don’t have to identify all the possible risks, just concentrate on the most significant ones due to their probability or impact.
