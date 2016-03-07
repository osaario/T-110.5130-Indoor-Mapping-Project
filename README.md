T-110.5130 Data collection for indoor mapping

### Introduction

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

### Goals and scope

The project produces an effective application that exports data for iMoon navigation mesh generation. Therefore, it reduces the stakeholders' effort to gather data for the iMoon system. The quality of data that the pilot user collects assisted by the application is qualitatively evaluated. Conducting comparative testing with other data collection methods is outside the project scope.

The project scope includes the first application version targeting researches and possibly property owners. This limited target group is expected to have relatively new Android devices compared to general population. This allows the first application version to take benefit of advanced sensor data. The second version would need to improve support for older devices and less accurate sensors.

Currently there is no active market for indoor navigation applications. This project is enabling research and demonstrations of indoor navigation systems. We are not developing concepts or algorithms that could be patented. Considering this background, the team does not have a direct business goal for this project. The project team publishes the application open source in GitHub. If iMoon or another indoor navigation system, using sensor data, gathers audience in future, business cases may arise. The project team acquires experience and public reference on the topic.

### System overview

Android application collects the necessary information, including captured images and estimated locations, for use in the indoor mapping system. The mentioned information is gathered from the Android sensors and it is sent to back-end web service for later exporting. The web service is a data oriented restful HTTP service API. The application allows user to calibrate the estimated locations and assists the user to cover the area completely and accurately.

### Requirements

The application has following primary functional requirements

* User can take and store photos.
* Application determines (with the use of coordinate system) user’s walking trace.
* Application visualizes the walking trace including location and direction of photos as a map.
* All collected data is send to backend service which stores it in database.
* Back-end service exports selected data set for iMoon system which generates navigation mesh from it.

The following functional requirements are considered after MVP in priority order

* User can calibrate and modify (e.g. by touching) walking trace locations and directions.
* User can add clarifying text descriptions to map and/or photos.
* User can load previously collected data set and append new data.
* User can add clarifying line drawings to map.
* Application collects Wi-Fi fingerprints, barometer, and magnetic field data along walking trace automatically.
* Back-end service controls access to existing data sets by device.
* User can share data sets using e.g. email for other devices.
* User can view and modify data sets in the web service.

The non-functional requirements are

* Good usability and user experience
* Reliability of the application and backend service
* Data integrity
