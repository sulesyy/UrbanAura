# UrbanAura

UrbanAura is a web application that helps users discover countries and city routes that match their lifestyle and personal preferences. Users can explore different countries, browse lifestyle-based recommendations, take a quiz to find the most suitable country for them, and save their favorite destinations.

This project was developed as a dynamic web application using Spring Boot. The page structure was built with Thymeleaf, while user operations and the favorites system are managed on the backend. H2 Database is used for storing user and favorite data locally.

## What Was Built?

- A modern and visually rich homepage was designed.
- Country cards were created to display destination suggestions.
- Search, region filtering, and lifestyle-based filtering features were added.
- A quiz system was developed to help users find the country that best matches their preferences.
- Personalized country recommendations and alternative routes are displayed based on quiz results.
- Country detail pages were created.
- User registration and login functionality were implemented.
- A favorites system was developed so users can save countries.
- A personalized favorites page was created.
- User and favorite data are stored using H2 Database.
- A responsive and modern interface design was implemented.

## Technologies Used

- **Java:** Used for backend development.
- **Spring Boot:** Used as the main framework for the web application.
- **Spring MVC:** Used for page routing and controller structure.
- **Spring Data JPA:** Used to manage database operations.
- **H2 Database:** Used to store user and favorite data locally.
- **Thymeleaf:** Used to create dynamic HTML pages.
- **HTML:** Used to structure the web pages.
- **CSS:** Used for modern UI design, card layouts, responsive layout, and visual styling.
- **JavaScript:** Used for some user interactions and interface behavior.
- **Maven:** Used to manage project dependencies and the build process.

## Pages

- `index.html`: Homepage and featured country suggestions.
- `countries.html`: Page where countries are listed with search and filtering options.
- `country-detail.html`: Page that displays detailed information about a selected country.
- `lifestyles.html`: Page containing lifestyle categories.
- `discover.html`: Quiz page where users receive a suitable country recommendation.
- `favorites.html`: Page where users can view their favorite countries.
- `login.html`: User login page.
- `register.html`: User registration page.
- `forgot-password.html`: Password reset page.

## File Structure

- `src/main/java/com/example/urbanaura/`: Contains the Java backend files.
- `src/main/resources/templates/`: Contains the Thymeleaf HTML templates.
- `src/main/resources/static/css/`: Contains the CSS design files.
- `src/main/resources/static/images/`: Contains the images used in the project.
- `src/main/resources/application.properties`: Contains application configuration settings.
- `pom.xml`: Contains Maven dependencies and project configuration.

## Project Purpose

The purpose of UrbanAura is to create a personalized travel and lifestyle recommendation platform where users can discover countries not only from a touristic perspective, but also based on their lifestyle preferences. Users can explore suggestions based on culture, nature, luxury living, modern city life, calm environments, and social lifestyle.

This project was also developed to practice Spring Boot, Thymeleaf, JPA, database usage, user management, and modern interface design.

## Setup and Usage

To run the project, Java and Maven support are required.

<img width="1710" height="988" alt="Ekran Resmi 2026-07-09 20 50 22" src="https://github.com/user-attachments/assets/4997dfe9-2ed7-4f27-b5f9-b5d80ea745f0" />
<img width="1710" height="980" alt="Ekran Resmi 2026-07-09 20 50 58" src="https://github.com/user-attachments/assets/39acc736-2c35-4113-91ae-0b18db98a895" />
<img width="1710" height="981" alt="Ekran Resmi 2026-07-09 20 51 21" src="https://github.com/user-attachments/assets/8d4f9d93-1b67-4df0-9d9a-5066f5ddd692" />
<img width="1710" height="981" alt="Ekran Resmi 2026-07-09 20 52 13" src="https://github.com/user-attachments/assets/68d43409-bb45-4df5-88dc-b7113b16c82d" />
<img width="1710" height="981" alt="Ekran Resmi 2026-07-09 20 52 55" src="https://github.com/user-attachments/assets/1e52232f-b6b2-4d02-a833-9d9f07038860" />
