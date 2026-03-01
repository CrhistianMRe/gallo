<div align="center">
  <h1>Gallo Backend</h1>
  <img alt="Wakatime" src="https://wakatime.com/badge/user/a5d3b539-fae9-4380-955b-fa971cded77a/project/d07694b7-06a4-40b5-b960-96c684a72f65.svg"/>
</div>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img alt="Spring" src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"/>
  <img alt="JUnit5" src="https://img.shields.io/badge/JUnit5-f5f5f5?style=for-the-badge&logo=junit5&logoColor=dc524a"/>
  <img alt="JWT" src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens"/>
  <img alt="Maven" src="https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white"/>
  <img alt="MariaDB" src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"/>
  <img alt="Swagger" src="https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white"/>
</p>


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

##### - Swagger 
<details>
<summary>Details</summary>

Swagger implemented to document Gallo backend platform.

</details>


