<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Gallo backend Changelog

## [Unreleased]

## [0.3.0] - 2026-05-05

### Fixed

- Register endpoints (account and person) JWT authentication requirement
- Workout view page endpoint ambiguity by adding required request parameter
- View all accounts endpoint by adding pagination 
- View all persons endpoint by adding pagination 

### Added 

- View all workout sets endpoint by workout id
- View all exercises endpoint
- View all body parts by exercise id endpoint

## [0.2.1] - 2026-04-21

### Fixed 

- Replace not found inconsistent responses to bad request response in workout module
- Replace not found inconsistent responses to bad request response in workout-set module

## [0.2.0] - 2026-04-02

### Added

- Redoc static web page consuming OpenAPI definition

### Fixed

- Set correct Swagger/OpenAPI URL usage
- Add missing environment variables usage in EC2 CD


## [0.1.0] - 2026-04-01

### Added 

- Account endpoints
- Person endpoints
- Workout endpoints
- Body part endpoints
- JWT security configuration
- JWT refresh token endpoint
- Swagger implementation
- MariaDB integration

[UNRELEASED]: https://github.com/CrhistianMRe/gallo-backend/compare/v0.3.0...HEAD

[0.3.0]: https://github.com/CrhistianMRe/gallo-backend/compare/v0.2.1...v0.3.0
[0.2.1]: https://github.com/CrhistianMRe/gallo-backend/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/CrhistianMRe/gallo-backend/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/CrhistianMRe/gallo-backend/commits/v0.1.0
