# app-composite-build-template

## Note
This repo is heavily under rapid, experimental and iterative development. The current state of code quality may not filly reflect professional/production-level work.

## Description
Personal app for keeping track of items in various locations. The main use case is tracking what's in the kitchen freezer, chest freezer, pantry, etc..
This also serves as a test-bed for experimenting with new framworks and patterns to keep up with advencements in Android Development.


## Technical notes
Using:
- Compose for UI
- SupaBase for remote DB for cross-device syncing
- PowerSync for managing the syncing of the SupaBase DB, enabling offline-first data approach
- Leveraging several personal libraries to reduce copy/paste across projects (Compose components, logging, various Android framework wrappers)

## TODO
X synchornized remote data storage
- storage auth for security
X data models & relations
- optimize queries given PowerSync's limitations on joins rather than manually joining client-side
X data model creation with reactive UI
- modifying item stock quantities at given locations
- creating cutom stock units
- mass item-level management screen (rename, change unit, delete with cascading removal across locations)
- general ui cleanup, styling
- code quality cleanup from experimental phases
- testing
- consider experimenting with other backend approaches (SupaBae GraphQL)
