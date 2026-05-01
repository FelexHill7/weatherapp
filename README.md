# WeatherApp — PM Accelerator Technical Assessment

**Built by Felex Hill** | Full Stack (Assessment #1 + #2)

A full-stack weather application with real-time weather lookup, 5-day forecasts, geolocation, historical data persistence (PostgreSQL), RESTful API, and multi-format data export.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18 + Vite |
| Backend | Spring Boot 4.0.6 + Java 21 |
| Database | PostgreSQL |
| Weather API | Open-Meteo (free, no key needed) |
| Maps | OpenStreetMap (free, no key needed) |
| Videos | YouTube Data API v3 (optional) |
| PDF Export | iText7 |
| CSV Export | Apache Commons CSV |

---

## Prerequisites

- Java 21+
- Maven (or use `./mvnw`)
- Node.js 18+ and npm
- PostgreSQL running locally

---

## Setup & Run

### 1. Create the PostgreSQL database

```sql
CREATE DATABASE weatherapp;
```

### 2. Backend

```bash
cd "backend (server)"

# Set environment variables (or edit application.yaml)
export DB_URL=jdbc:postgresql://localhost:5432/weatherapp
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword

# Optional: YouTube video integration
export YOUTUBE_API_KEY=your_youtube_api_key_here

# Run
./mvnw spring-boot:run
```

Backend starts on **http://localhost:8080**

> Hibernate will auto-create the `weather_records` table on first run (`ddl-auto: update`).

### 3. Frontend

```bash
cd "frontend (client)"
npm install
npm run dev
```

Frontend starts on **http://localhost:5173**

---

## Features

### Assessment #1 — Frontend
- **Location search**: city name, ZIP/postal code, GPS coordinates (lat,lon), landmarks
- **Geolocation**: "Use My Location" button (browser GPS)
- **Current weather**: temperature, feels like, humidity, wind speed, weather emoji
- **°C / °F toggle**: switch units on the fly
- **5-day forecast**: daily high/low, precipitation probability, wind speed
- **Interactive map**: OpenStreetMap embed for searched location
- **Location videos**: YouTube videos if API key is configured
- **Error handling**: invalid locations, API failures, geolocation denied
- **Responsive design**: works on desktop, tablet, and mobile

### Assessment #2 — Backend
- **RESTful API**: full CRUD for weather records
- **CREATE**: submit location + date range → geocode → fetch weather → store to PostgreSQL
- **READ**: retrieve all records or a specific record by ID
- **UPDATE**: re-geocode and re-fetch updated records
- **DELETE**: remove records by ID
- **Validation**: date range (start ≤ end, max 1 year), location geocoding validation
- **YouTube integration**: video search per location (requires API key)
- **OpenStreetMap integration**: map embed + link per location
- **Data export**: JSON, CSV, XML, PDF, Markdown

---

## API Endpoints

```
GET    /api/weather/current?location={location}     Current weather
GET    /api/weather/forecast?location={location}    5-day forecast
GET    /api/weather/map?location={location}         Map embed info
GET    /api/weather/videos?location={location}      YouTube videos

POST   /api/weather/records                         Create record (body: {location, startDate, endDate})
GET    /api/weather/records                         Get all records
GET    /api/weather/records/{id}                    Get one record
PUT    /api/weather/records/{id}                    Update record
DELETE /api/weather/records/{id}                    Delete record

GET    /api/export?format=json|csv|xml|pdf|markdown Export all records
```

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/weatherapp` | JDBC connection string |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `password` | Database password |
| `YOUTUBE_API_KEY` | _(empty)_ | YouTube Data API v3 key (optional) |
| `CORS_ORIGINS` | `http://localhost:5173,http://localhost:3000` | Allowed frontend origins |

---

## About PM Accelerator

**Product Manager Accelerator** is the #1 PM coaching and community platform, helping thousands of aspiring and experienced product managers break into product management, level up their careers, and build the skills, network, and confidence to succeed through mentorship, real-world projects, and an elite global community.

🔗 [PM Accelerator on LinkedIn](https://www.linkedin.com/company/product-manager-accelerator/)
