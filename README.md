How to use:

: Update pom.xml with the following fields.
    - <name> Enter your project name </name>
    - <remote.server> Enter the URL to your tomcat manager/text </remote.server>
    - <db.name> Enter the name of your database </db.name>
	- <artifactId> Will be name of the WAR file and hence the url extension on droplet </artifactId>

: Update persistence.xml with the following fields.
    - Update JDBC string to your new database for PROD
    - Update JDBC string to your new database for TEST (use prod name and suffix with "_test")

: Update .github/workflows/mavenworkflow.yml
    - Change database name

: Update the context path (extension after port in your URL)
    - Change in: src/main/webapp/META-INF/context.xml

: Add following secrets to your repo
    - REMOTE_USER - Your droplet tomcat user account
    - REMOTE_PW - Your droplet tomcat user account's password

: Update users in utils/SetupTestUsers.java

: Clean and build project

: Run main method in utils/SetupTestUsers.java

: Enjoy!