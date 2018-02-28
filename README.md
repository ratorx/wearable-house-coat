# Wearable House Control [![Build Status](https://travis-ci.org/ratorx/wearable-house-coat.svg?branch=master)](https://travis-ci.org/ratorx/wearable-house-coat)

# Setting up the Project

## Watch App
* Clone project.
* Import WearableHouseCoat folder as a gradle project.
* To get the shared API keys, follow the instructions on the [API Keys](https://github.com/ratorx/wearable-house-coat/wiki/API-Keys) wiki page.
* DO NOT COMMIT the API keys.

## Server setup
* Go into indoorpos directoryr
* Run `export GOPATH=/societies/clquebec/.go`
* Run `go get -d` to install dependencies
* Run `go build .` to compile into executable
* To start server, run `./indoorpos`

## Website
* Clone project.
* cd into web directory.
* run `npm install` to download all dependencies
* use `npm start` to start a node server and go to localhost:3000 if it does not happen automatically

## Connecting Hue in the Intel Lab
This has only been tested on Windows 10, but does work. I'm sure a similar setup works for Mac.
* Firstly, ensure that both all devices are connected wgb Wi-fi. This will **not** work with eduroam.
* Connect the Hue bridge to the laptop's ethernet. This may require an adapter depending on your laptop.
* Head to Settings -> Network & Internet -> Change adapter options.
* We need to connect the Wi-Fi and ethernet connection together. This allows the bridge to communicate with the Wi-Fi network and internet.
* To do this, select the Wi-Fi and ethernet adapters, right click and select 'Add to Bridge'.
* Now follow any in-app instructions to finish set up.
