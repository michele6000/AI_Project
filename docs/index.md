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
... | ... | ...
s000030@studenti.polito.it | student | ROLE_STUDENT

| Username | Password | Role |
| ------ | ------ | ------ |
d000001@polito.it | admin | ROLE_PROFESSOR
d000002@polito.it | admin | ROLE_PROFESSOR

### Database configuration
The database include the following courses:
| Course Name | VM Type ID | Acronimous | Group (min-max) |
| ------ | ------ | ------ | ------ |
| Applicazioni Internet | 1 | AI2020 | 1-4 |
| Programmazione di Sistema | 2 | PDS2020 | 1-4 |
| Sicurezza dei Sistemi Informatici | 3 | SIC2020 | 1-2 |

Each professor is assigned to some courses, as described below.

| Course Name | ProfessorID |
| ------ | ------ |
| Applicazioni Internet | d000001 |
| Applicazioni Internet | d000002 |
| Programmazione di Sistema | d000001 |
| Programmazione di Sistema | d000002 |
| Sicurezza dei Sistemi Informatici | d000001 |

In each of these courses are enrolled all the thirty students showen in upside table.

Database include also this active team for course "AI2020" :
| ID | Name | Status | CPU Limit | HDD Limit | RAM Limit | Instances Limit | Active Instances Limit |
| ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ |
| 4 | Reset | Active | 1000 | 1000 | 1000 | 4 | 2 |
| 5 | GameOfCosa | Pending | | | | | |
| 6 | GameOfCosa2 | Pending | | | | | |

Here is list of students for each team
| TeamID | StudentID |
| ------ | ------ |
| 4 | s000001 |
| 4 | s000002 |
| 4 | s000003 |
| 4 | s000004 |
| 5 | s000028 |
| 5 | s000029 |
| 5 | s000030 |
| 6 | s000028 |
| 6 | s000029 |
| 6 | s000030 |

For team 438 - Reset there are also these VMs:
| ID | Owner | CPU | HDD | RAM | Status |
| ------ | ------ | ------ | ------ | ------ | ------ |
| 101 | s000002 | 2 | 256 | 512 | PowerOn |

These assignments are available in database for course "AI2020" :
| ID | Content | Image | Relase Date | Expiry Date |
| ------ | ------ | ------ | ------ | ------ |
| 7 | Esercitazione 1 | [BLOB] | 01/12/2020 | 28/12/2020 |
| 38 | Esercitazione 2 | [BLOB] | 02/12/2020 | 29/12/2020 |
| 69 | Esercitazione 3 | [BLOB] | 03/12/2020 | 31/12/2020 |
