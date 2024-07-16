# Stack technologies
- Java 17
- Docker
- Lombok
- Liquibase
- Spring
- JavaX
- Swagger

# How start

```shell
docker-compose up --build
```
# Base endpoint

## Auth

### POST
username and password it's strings

```json
{
  "username": "username",
  "password": "password"
}
```

## Users
### GET

/users -- get all users

/users?id=ID -- get user by id

/users?name=USERNAME -- get user by username

/users -- add new user

username and password it's strings

```json
{
  "username": "username",
  "password": "password"
}
```

### DELETE

/users?id=ID -- delete user by id

/users?name=USERNAME -- delete by username

### UPDATE

/users -- update user

you can select 1 or all fields username and password it's strings, workspace_id is long
```json
{
  "username": "username",
  "password": "password",
  "userWorkspace": workspace_id
}
```

## Conference
### GET

/conference -- get all conferences

/conference?id=ID -- get conference by id

/conference?title=TITLE -- get conference by title

### POST

/conference -- add new conference
contains conferenceTitle and conferenceDescription as strings
```json
{
  "conferenceTitle": "Conference Title",
  "conferenceDescription": "Conference Description"
}
```


### DELETE
/conference?id=ID -- delete conference by id

### UPDATE
/conference - update conference

contains conferenceId (Long), conferenceTitle and conferenceDescription as strings

```json
{
  "conferenceId": ID,
  "conferenceTitle": "Updated Conference Title",
  "conferenceDescription": "Updated Conference Description"
}
```

## Workspace
### GET

/workspaces -- get all workspaces

/workspaces?id=ID -- get workspace by id

/workspaces?title=TITLE -- get workspace by title

### POST

/workspaces -- add new workspace

contains workspaceTitle and workspaceDescription as strings

```json
{
  "workspaceTitle": "Workspace Title",
  "workspaceDescription": "Workspace Description"
}
```

### DELETE

/workspaces?id=ID -- delete workspace by id

### UPDATE

/workspaces

contains workspaceId (Long), workspaceTitle and workspaceDescription as strings

```json
{
  "workspaceId": ID,
  "workspaceTitle": "Updated Workspace Title",
  "workspaceDescription": "Updated Workspace Description"
}
```
