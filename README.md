1. Add secrets to github repo
2. Rename project
	- change remote.server in pom to http://<your_domain>/manager/text
	- change artefactId in the pom to the project name
	- Update the context path (extension after port in your URL) - Change in: src/main/webapp/META-INF/context.xml
3. Setup databases locally
	- Edit pu in persistence.xml
	- Edit db.name in pom.xml
	- Edit db name in .github -> mavenworkflow.yml
	- Add following secrets to your repo - **REMOTE_USER** - Your droplet tomcat user account - **REMOTE_PW** - Your droplet tomcat user account's password
4. Update users in utils/SetupTestUsers.java
5. Clean & build the project
6. Hit the endpoint `api/info/all` to make sure database is connected

## DEPLOY
1. Setup databases on the droplet
2. Edit database name in EMF_creator at line 61 (instead of edit docker-compose.yml on droplet)
3. `mvn clean test -Dremote.user=<tomcat username>-Dremote.password=<tomcat password> tomcat7:deploy`
