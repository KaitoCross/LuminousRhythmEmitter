# Hardware List:

- Seeed Studio XIAO ESP32C3
- power switch: MFP 120 by Knitter-Switch
- Any 18350 Li-ion battery with a **protection circuit** (preventing deep discharge)
- Button: generic 6x6x5mm pushbutton
- 20mm ø LED Lens (45° or 60°, testing), max 11mm high
- The custom printed PCB
  - I recommend ordering the custom PCB through JLCPCB
- 2x 6-Pin 2.54mm Dupont female pin header (socket 8,5mm high)
- 2x 6-Pin 2.54mm Dupont male pin header (pin 6mm high)
- If PCB is not pre-assembled:
  - 1x Inolux IN-P55TATRGB
  - 3x S8050 (SMD type SOT-23)
  - SMD type 1206 resistors:
    - 3x 15,5 kOhm (or slightly lower)
    - 2x 11 Ohm (or slightly higher)
    - 1x 61 Ohm (or slightly higher)
- battery contacts (with spring)
- (optional) 100 mm 2,4 Ghz Antenna
- an LED light diffusion filter. Dimensions: 9,5cm x 14,5cm  (15.1mm inner cylinder radius)
  Recommendations: 
  - [Lee Colour Filter 251 (1/4 white diffusion)](https://www.thomann.de/intl/lee_colour_filter_251_q_w_diffus.htm)
  - [Lee Colour Filter 252 (1/8 white diffusion)](https://www.thomann.de/intl/lee_colour_filter_252_e_w_diffus.htm)

# How to Build

You’ll need to familiarize yourself with soldering SMD components.  
Required tools:

- SMD rework station

- Tweezers

- Solder paste and (preferably) no-clean flux

- Regular soldering iron and solder for through-hole parts

- [KiCAD](https://www.kicad.org/) (free & open-source) to open the circuit files

## XIAO ESP32C3

Start with the XIAO ESP32C3. Solder the male 6-pin headers to the board, aligning the 5V with the first pin of the pin header, with the long side of the pins on facing the same direction as the big chip. DO NOT USE 7-PIN headers! Solder the other male pin header exactly in parallel with the first one.s

## Custom PCB

Now you can start soldering the parts to the custom PCB. Start with the SMD parts, soldering them to their designated spots as marked on the board. Once that is done, solder the female pin headers to the board. They need to be on the side opposite to the LED.

## Battery wiring

Solder a red wire to the battery contact without the spring (positive battery pole). Thread the red wire through the centre hole of the 3D-printed battery wall. Then solder the other end of the red wire to the battery contact (positive pole) of the XIAO.

Solder a black wire to the negative battery contact pad of the XIAO. Solder the other end to the centre pin of the power switch. Solder another black wire from one of the outer pins of the power switch to the battery contact with the spring (negative). Ensure that this wire is long enough so that it can hang out of the bottom end (short thread) of the penlight case. Make it \~2 cm longer than the distance between USB port and bottom end.

## Pushbutton wiring

Connect two wires to the J4 holes on the PCB. Connect both wires to the pushbutton, one wire to the top right leg, the other one the the bottom right leg (see example image below)

![](https://diotlabs.daraghbyrne.me/docs/getting-inputs/images/button/image_2.jpg)

## Assembly

3D-print all parts in the [`3D` folder](../3D). Print settings recommendations [are also provided.](../3D/print_settings.md) Some cutouts (mainly for the buttons) need to be sanded for a better fit.

Connect the PCB and the XIAO through the pin headers.

[Install the firmware on the XIAO](../Software/README.md) and test it.

Start with the half with the rectangular cutout on the top. Place the PCB on top of the Edge above the cutout, with the USB port facing the cutout. Place the black wire in the recess in the bottom. Then, but the battery wall in the space on the recess, aligning it with said recess, while the positive battery pole faces outward towards the bottom thread. 

To make working with the pushbutton easier, you need to straighten the unused legs, aligning them with the case of the button itself. Now, pull the pushbutton though the D-shaped hole, with the wires facing the wall. Place the button cap on the pushbutton. Put the rectangular power switch into it's holder near the XIAO.

Now, put the lens into the lens holder with the flat side of the lens facing the small end of the holder. Push it all the way in. Place the lens holder on top of the PCB.

Now put the other half of the penlight base on top and close the case, making sure the buttons fit into the other half as well. Then you can screw on the top part. Into the top part, you can then place your light diffusion filter. You can then close the top part by screwing on the respective cap.

Now, you can place the battery in the battery compartment. Make sure that the positive pole faces inwards. The negative pole then faces outwards, and connects to the spring. hold the spring in place and close the battery compartment with the other, bigger cap.

Now you should be able to power on and use your penlight!
