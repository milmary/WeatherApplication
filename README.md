# Weather application

JavaFX desktop app that shows current weather (Prague or any city) and a 5-day forecast chart (09:00 & 18:00). Data comes from OpenWeatherMap through a small provider layer.

## Requirements

* **JDK 22** (or compatible)
* **Maven 3.9+**
* Network access (to fetch weather)

## How to build & run

> **Run all commands from the project root** — the folder that contains `pom.xml`.

### Build and run

```bash
# 1) go to the project root
cd path/to/AppProject

# 2) build the project
mvn -q clean package

# 3) run the JavaFX app
mvn javafx:run
```

## Using the app

### Start window

* Top left: current date
* Top right: current time (live)
* Buttons:

  * **NOW IN PRAGUE** → current weather in Prague
  * **NOW IN THE WORLD** → search any city
  * **5 DAYS AHEAD…** → 5-day forecast (09:00 & 18:00)

### NOW IN PRAGUE

* Shows current weather in Prague (temperature, description, wind, sunrise/sunset).
* **« Return** button goes back to Start.

### NOW IN THE WORLD

* Enter a city name and press **Get Weather** (or **Enter**).
* Shows temperature, pressure, description, wind, sunrise/sunset (times normalized to GMT+2).
* **Delete** clears the current input/output.
* **Help** shows input tips (supported languages, country code like `Rome, IT`).
* **Recent searches**: click a city to copy it into the field, then press **Get Weather**.
* **Clear history** removes stored recent searches.

### 5 DAYS AHEAD…

* Enter a city and press **Get Weather** for a 5-day outlook.
* Line chart plots temperatures at **09:00** and **18:00** for each day.
* Y-axis range fixed to **−40 … +40 °C**.
* Forecast text is shown below the chart.


## Technical documentation (Javadoc)

Generate Javadoc:

```bash
# from the project root
mvn javadoc:javadoc
```

Open the docs at:

```
target/reports/apidocs/index.html
```


## Project structure (key parts)

```
src/
  main/
    java/
      cz/cuni/milanenm/
        controllers/       # JavaFX controllers (Start, CurrentPrague, CurrentSearch, Forecast)
        fx/                # JavaFX Service wrappers
        providers/         # Weather provider SPI + OWM implementation + registry
        db/                # H2 history DAO
        manage/            # small utilities (scenes, time formatting, constants)
        cache/, proxy/, spi/  # cache + dynamic proxies + SPI interfaces
        receive_json/      # HTTP client + existing JSON adapters
        Main.java          # JavaFX application entry point
    resources/
      fxml/*.fxml          # All UI layouts
      img/*.png,*.jpg      # Backgrounds / icons
      META-INF/services/   # ServiceLoader file for WeatherProvider
pom.xml
```
