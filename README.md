<div align="center">
  <h1>Gallo Backend</h1>
  <img alt="Wakatime" src="https://wakatime.com/badge/user/a5d3b539-fae9-4380-955b-fa971cded77a/project/e71ab9b3-4e30-404d-ba95-007256ce8280.svg"/>
  
  ![Build](https://github.com/CrhistianMRe/gallo-backend/actions/workflows/ci.yml/badge.svg)
  ![Deploy](https://github.com/CrhistianMRe/gallo-backend/actions/workflows/cd.yml/badge.svg)
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

> [!TIP]
> Here you can find the API documentation https://gallospring.duckdns.org/apidocs.html.

> By the moment, test the API with these credentials: email: "user@gmail.com", password: "12345"

> [!IMPORTANT]
> This is a sub-repo of [Gallo Project](https://github.com/CrhistianMRe/gallo).

### Local Installation
- MariaDB 10.6+ (12.2.2-MariaDB used in this project)

1. Install **MariaDB** server database with Gallo SQL [script](https://github.com/CrhistianMRe/gallo-backend/blob/main/backend/src/main/resources/gallo.sql).
2. Set up environment variables used in [application.properties](https://github.com/CrhistianMRe/gallo-backend/blob/main/backend/src/main/resources/application.properties):
```bash
# fish shell
set -x DB_URL "jdbc:mariadb://yourdatabaseurl:3306/yourdatabasename?sslMode=trust"
set -x DB_USER "youruser"
set -x DB_PASSWORD "yourpassword"

# bashrc
export DB_URL="jdbc:mariadb://yourdatabaseurl:3306/yourdatabasename?sslMode=trust"
export DB_USER="youruser"
export DB_PASSWORD="yourpassword"
```

3. Run [latest release](https://github.com/CrhistianMRe/gallo-backend/releases/latest) jar.

### Technologies implementations in this API

<details>
<summary>⚙️Spring Boot</summary>

Main framework used to run API REST service, beans configuration and dependency management.

</details>

<details>
<summary>🔐 Spring Security</summary>

- **Filter chain:**
Matches permissions of authorities/roles provided by DB and functional requirements documents exposing/locking endpoint. Also used for filter and UserDetailsService usage.

- **JWT:**
JSON Web Token Authentication implementation with Spring Security dependency. Using filters to validate, attempt, fail and success authentication based on gallo DB account data retrieve with ADMIN/USER authorities.

- **Custom UserDetails:**
As by default UserDetails class contains only username usage, custom UserDetails implementation class added with email attribute matching DB account table giving User details context to the whole system.

- **Custom UserDetailsService:**
This service is used with the default UserDetails service loadUserByUsername override method replacing common UserDetails return object for previously described Custom UserDetails implementation.

- **Token refresh**
JWT token refresh functionality included through an UUID refresh token provided in login.

</details>

<details>
<summary>🗄️ Spring Data JPA(Hibernate)</summary>

- **Entities:**
Each table, field and constraint has been mapped respectively to Gallo MariaDB database.

- **Repositories:**
Provided repository layer between service and db creating queries derived by method name or personalized JPQL queries to avoid repetition and only "usage of defaults".

</details>

<details>
<summary>🐬 MariaDB</summary>

SQL creation and insert script for retrieve and store of Gallo workout app.

![DB ER-diagram](https://github.com/CrhistianMRe/gallo/blob/main/docs/ERGallo.svg)

</details>

<details>
<summary>📦 Maven</summary>

- **Directory paths:**
Defined to package ease of use for workflow on local Maven testing and GitHub Actions CI. 

</details>

<details>
<summary>🌐 General API</summary>

- **Controller endpoint layer:**
Used for the only purpose of exposing business logic resolved data for HTTP requests. 

- **DTOs:**
Input and output requests on controller endpoints contains DTO usage to provide responsibilites separation on entities, JSON Mapper requests/responses properties configurations and personalized validation annotations.

- **Personalized annotations:**
Grants pre-constraint validation in order to throw respective exception avoiding DB/ORM exception variety. Annotations localized on controller methods, DTO cascading fields and cross-parameter class validation. 

- **Exception handler controller:**
Furnish whole platform with personalized exception handling custom message responses.

</details>

<details>
<summary>🎨 Design Patterns</summary>

- **Builder:**
Used for entities instance declaration on runtime and testing.

</details>

<details>
<summary>🧪 Testing</summary>

Tests implements JUnit, Mockito, AssertJ and H2 DE, intended to validate data/methods correct functionality integrity for local test and remote CI.

</details>

<details>
<summary>📋 Swagger</summary>

Swagger implemented to document Gallo backend platform.

</details>


