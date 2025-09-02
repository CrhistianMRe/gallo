# Gallo

## Description
Gallo is a multi-platform system composed of a backend REST API, a JavaFX desktop application, and an Android mobile client. All components communicate through a secure REST interface. The backend is built with Spring Boot and uses MariaDB, while both frontend clients consume the API. The project also includes a comprehensive testing strategy using JUnit and Mockito, with continuous integration workflows managed through GitHub Actions.

## My workflow
[Workflow(Activity Diagram)](https://drive.google.com/file/d/1pbfhECpNCoFXqrq4_ybv9qA-STdyB9cL/view?usp=sharing)

## Technologies/Features
### Backend
#### - Spring Boot
<details>
<summary>Details</summary>

Main framework used to run API REST service, beans configuration and dependency management.

</details>

#### - Spring Security
<details>
<summary>Details</summary>

- **Filter chain:**
Matches permissions of authorities/roles provided by DB and functional requirements documents exposing/locking endpoint. Also used for filter and UserDetailsService usage.

- **JWT:**
JSON Web Token Authentication implementation with Spring Security dependency. Using filters to validate, attempt, fail and success authentication based on gallo DB account data retrieve with ADMIN/USER authorities.

- **Custom UserDetails:**
As by default UserDetails class contains only username usage, custom UserDetails implementation class added with email attribute matching DB account table giving User details context to the whole system.

- **Custom UserDetailsService:**
This service is used with the default UserDetails service loadUserByUsername override method replacing common UserDetails return object for previously described Custom UserDetails implementation.

</details>

#### - Spring Data JPA(Hibernate)
<details>
<summary>Details</summary>

- **Entities:**
Each table, field and constraint has been mapped respectively to Gallo MariaDB database.

- **Repositories:**
Provided repository layer between service and db creating queries derived by method name or personalized JPQL queries to avoid repetition and only "usage of defaults".

</details>

#### - MariaDB
<details>
<summary>Details</summary>

SQL creation and insert script for retrieve and store of Gallo workout app.

</details>

#### - Maven
<details>
<summary>Details</summary>

- **Directory paths:**
Defined to package ease of use for workflow on local Maven testing and GitHub Actions CI. 

</details>

#### - API endpoints
<details>
<summary>Details</summary>

- **Controller endpoint layer:**
Used for the only purpose of exposing business logic resolved data for HTTP requests. 

- **DTOs:**
Input and output requests on controller endpoints contains DTO usage to provide responsibilites separation on entities, JSON Mapper requests/responses properties configurations and personalized validation annotations.

- **Personalized annotations:**
Grants pre-constraint validation in order to throw respective exception avoiding DB/ORM exception variety. Annotations localized on controller methods, DTO cascading fields and cross-parameter class validation. 

- **Exception handler controller:**
Furnish whole platform with personalized exception handling custom message responses.

</details>

#### - Design Patterns
<details>
<summary>Details</summary>

- **Builder:**
Used for entities instance declaration on runtime and testing.

</details>

##### - Testing
<details>
<summary>Details</summary>

Tests implements JUnit, Mockito, AssertJ and H2 DE, intended to validate data/methods correct functionality integrity for local test and remote CI.

</details>


### Desktop Client 
- JavaFX  
- Retrofit  
- JavaFX Weaver Spring Boot (for dependency injection)  
- Maven

### Android Client
- Android SDK  
- Retrofit  
- (Optional: Jetpack Components)  
- Gradle

## Template docs
- [Requirements](https://drive.google.com/file/d/1diq_zjKFh7muv0KoUWesED698ZbEQZOE/view?usp=sharing)
- [DB rel-diagram](https://drive.google.com/file/d/17Mow6Cy2x-wizXpR6F3c3Lj6hZUB44Gi/view?usp=sharing)


