# Running the Application

## Prerequisites

* Java 17+
* Docker & Docker Compose
* Maven Wrapper (included in the project)

## Prepare Dataset

Due to GitHub file size limitations, the IMDb dataset files are not included in this repository.

1. Download the required IMDb datasets from:

   https://datasets.imdbws.com/

2. Extract the downloaded `.tsv.gz` files.

3. Place the extracted `.tsv` files directly inside the project's `data` directory.

The application expects the extracted IMDb dataset files to be located directly in the `data` folder.

Example:

```text
data/
├── title.basics.tsv
├── title.ratings.tsv
├── title.crew.tsv
├── name.basics.tsv
└── ...
```

After the dataset files have been extracted and placed in the `data` directory, continue with the steps below.


## Start PostgreSQL

Open PowerShell and execute:

```powershell
cd src/main/resources/docker
docker-compose up -d
```

## Start the Spring Boot Application

From the project root directory run:

```powershell
.\mvnw.cmd spring-boot:run
```

The application will be available at:

```text
http://127.0.0.1:8080
```

# Import Dataset

Trigger the dataset import process:

```bash
curl --location --request POST "http://127.0.0.1:8080/api/import/dataset"
```

Check the import status:

```bash
curl --location "http://127.0.0.1:8080/api/import/stats"
```

# Request Statistics

Retrieve request statistics:

```bash
curl --location "http://127.0.0.1:8080/api/stats/requests"
```

Reset request statistics:

```bash
curl --location --request POST "http://127.0.0.1:8080/api/stats/requests/reset"
```

# Sample API Calls

## Titles Where Director and Writer Are the Same Person and Still Alive

```bash
curl --location "http://127.0.0.1:8080/api/titles/director-equals-writer/alive?page=0&size=10"
```

## Best Titles Per Year by Genre

```bash
curl --location "http://127.0.0.1:8080/api/titles/best-per-year?genre=Comedy&page=0&size=10"
```

## Common Titles Between Two Actors

```bash
curl --location "http://127.0.0.1:8080/api/titles/common-titles?actor1=Andreas%20Demmel&actor2=Amit%20Goldenberg&page=0&size=20"
```

# Materialized View Maintenance

The application uses a materialized view to optimize queries that retrieve titles where the director and writer are the same person.

Create the materialized view:

```sql
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_director_writer_same AS
SELECT DISTINCT
    d.tconst,
    d.nconst
FROM title_directors d
JOIN title_writers w
    ON d.tconst = w.tconst
   AND d.nconst = w.nconst;
```

Whenever the IMDb dataset is updated and re-imported, refresh the materialized view to ensure query results remain up to date:

```sql
REFRESH MATERIALIZED VIEW mv_director_writer_same;
```

Failure to refresh the materialized view after updating the dataset may result in stale query results.
