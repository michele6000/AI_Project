# Teams

Welcome to Teams, in this file we'll show standard configuration steps.
Usefull links: [GitHub Repository](https://github.com/michele6000/AI_Project), [DockerHub font-end Repository](https://hub.docker.com/r/michele6000/front-end/tags), [DockerHub back-end Repository](https://hub.docker.com/r/michele6000/back-end/tags),  [DockerHub ai-project-db Repository](https://hub.docker.com/r/michele6000/ai-project-db/tags)

# Build Phase
We recommend using the first procedure, to reduce waiting times due to local code compilation.

### DockerHub based build
  - To Build and Run Teams simply type:
    ```sh
    $ docker-compose -f docker-compose-online.yml up
    ```
  - Docker will pull latest image of database (michele6000/ai-project-db), back-end (michele6000/back-end) and front-end (michele6000/front-end) from our personal DockerHub account, then will run: project-db listening on port 3306, back-end listening on port 8080, and front-end listening on port 4200. Make shure this port are not used from other applications on your pc. Note that the image are auto-builded, tagged and pushed by an automatic workflow configured on GitHub, in particular it will push a new version when master branch is updated.

### Local compile based build
  - To build and Run Teams simply type: (make shure you have rwx permission in this file)
    ```sh
    $ ./autorun.sh
    ```
  - The script first of all will build back-end and package the application using Maven by calling:
    ```sh
    $ mvn clean ; mvn package
    $ docker build -f Dockerfile -t back-end .
    ```
    after that it will build front-end by calling Node Packet Manager, installing dependencies and then building the Angular Application:
    ```sh
    $ npm i ; sudo npm run build
    $ docker build -f Dockerfile -t front-end .
    ```
    after front-end also il builded, the script will pull latest mariadb image:
     ```sh
    $ docker pull mariadb:latest
    ```
    finally is called docker-compose script to create and start all the containers:
    ```sh
    $ docker-compose -f docker-compose-offline.yml up
    ```
    as same as dockerhub based build phase docker will run: project-db listening on port 3306, back-end listening on port 8080, and front-end listening on port 4200.

# Basic scenario
Both build procedures are configured in such a way as to load the files present in the ./mariadb-files folder, which already contains registered users, enabled courses, submissions, solutions, etc.

Access data:

| Username | Password | Role |
| ------ | ------ | ------ |
s000001@studenti.polito.it | student | ROLE_STUDENT
s000002@studenti.polito.it | student | ROLE_STUDENT
s000003@studenti.polito.it | student | ROLE_STUDENT
s000004@studenti.polito.it | student | ROLE_STUDENT
s000005@studenti.polito.it | student | ROLE_STUDENT
s000006@studenti.polito.it | student | ROLE_STUDENT
s000007@studenti.polito.it | student | ROLE_STUDENT
s000008@studenti.polito.it | student | ROLE_STUDENT
s000009@studenti.polito.it | student | ROLE_STUDENT
s000010@studenti.polito.it | student | ROLE_STUDENT
s000011@studenti.polito.it | student | ROLE_STUDENT
s000012@studenti.polito.it | student | ROLE_STUDENT
s000013@studenti.polito.it | student | ROLE_STUDENT
s000014@studenti.polito.it | student | ROLE_STUDENT
s000015@studenti.polito.it | student | ROLE_STUDENT
s000016@studenti.polito.it | student | ROLE_STUDENT
s000017@studenti.polito.it | student | ROLE_STUDENT
s000018@studenti.polito.it | student | ROLE_STUDENT
s000019@studenti.polito.it | student | ROLE_STUDENT
s000020@studenti.polito.it | student | ROLE_STUDENT
s000021@studenti.polito.it | student | ROLE_STUDENT
s000022@studenti.polito.it | student | ROLE_STUDENT
s000023@studenti.polito.it | student | ROLE_STUDENT
s000024@studenti.polito.it | student | ROLE_STUDENT
s000025@studenti.polito.it | student | ROLE_STUDENT
s000026@studenti.polito.it | student | ROLE_STUDENT
s000027@studenti.polito.it | student | ROLE_STUDENT
s000028@studenti.polito.it | student | ROLE_STUDENT
s000029@studenti.polito.it | student | ROLE_STUDENT
s000030@studenti.polito.it | student | ROLE_STUDENT
