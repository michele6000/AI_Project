# Teams


Welcome to Teams, in this file we'll show standard configuration steps.

# Build Phase
### DockerHub based build
  - To build and Run Teams simply type:
    ```sh
    $ docker compose --file ./docker-compose-online.yml
    ```
  - Docker will pull latest image of database (michele6000/ai-project-db), back-end (michele6000/back-end) and front-end (michele6000/front-end), then will run project-db listening on port 3306, back-end listening on port 8080, and front-end listening on port 4200. Make shure this port are not used from other applications on your pc.

### Local based build
  - To build and Run Teams simply type: (make shure you have rwx permission in this file)
    ```sh
    $ ./autorun.sh
    ```
  - The script first of all will build back-end by calling:
    ```sh
    $ cd ./back-end
    $ docker build -f Dockerfile -t back-end .
    ```
    after that it will build front-end in the same way:
    ```sh
    $ cd ./front-end
    $ docker build -f Dockerfile -t front-end .
    ```
    after front-end also il builded, the script will pull latest mariadb image:
     ```sh
    $ docker pull mariadb:latest
    ```
