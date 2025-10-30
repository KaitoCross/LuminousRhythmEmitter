# Software
As a quick proof of concept, you can use [WLED](https://github.com/wled/WLED) to control the RGB LED on the ESP32 via WiFi. WLED does not offer Bluetooth support.
If you prefer Bluetooth Low Energy, I recommend [esphome](https://esphome.io). In this folder is an esphome sample configuration for the penlight hardware.
I prefer esphome simply because it offers Bluetooth Low Energy.
The sample configuration exposes a BLE characteristic that controls the LED. It expects an RGB value (3x 8 bit unsigned int) as input.
Currently, no verification & encryption of the communication is implemented as esphome does not support it yet. If there's demand (and if esphome doesnt implement it soon), I might write custom firmware with BLE security in the future.

A mobile companion app is planned. It's main purpose is to enable the remote controlling functionality. On one hand, the user should be able to control the colors manually through the app. On the other hand, the app should offer the possibility to hand off the control to a central lightjockey. The app would create the neccessary bridge for that.