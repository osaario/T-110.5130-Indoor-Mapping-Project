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

### Installation

The Android application in `IndoorMappingApp` is an Android Studio project. The Gradle build is targeting SDK 23 and has minimum SDK 16. The application has dependencies to RXJava, OKHTTP and Picasso.

The HTTP API service in 'appServer' is a NodeJS application that uses Mongo database. Following presents commands to run the service. 

	$ apt-get install mongodb nodejs npm
	$ git clone https://github.com/osaario/T-110.5130-Indoor-Mapping-Project
	$ cd T-110.5130-Indoor-Mapping-Project/appServer
	$ npm install
	$ node server.js

An example of using pm2 to create daemon process.

	$ npm install -g pm2
	$ pm2 start server.js

On older Ubuntu systems, the npm install of lwip has error. The lwip is used to scale smaller versions of images to be displayed in mobile application.

