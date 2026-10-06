# MAGNET — 3-coil levitation experiment

Experimental Arduino Nano controller for a 3-electromagnet levitation system.

## Concept

Three electromagnets are arranged symmetrically at approximately 45°.
Each magnet has a Hall sensor nearby. The controller reads all three Hall
sensors and adjusts the PWM current of the corresponding coils.

The long-term goal is to levitate a ~40 g steel ball while allowing the ball
to rotate with minimal transmission of rotational torque from the magnetic
suspension.

## Hardware

- Arduino Nano (ATmega328P)
- 3 Hall sensors
- 3 electromagnets
- 3 logic-level N-MOSFET drivers
- 3 flyback diodes
- Separate power supply for the coils
- ~40 g steel ball

## Safety / first test

Do NOT connect a coil directly to an Arduino pin.

Use a MOSFET driver and flyback diode. Start with low PWM and test the
feedback direction without the ball. Electromagnets and MOSFETs can become
very hot.

The first version is deliberately simple. The next step should be a real
3-axis controller that transforms the three Hall measurements into ball
position errors rather than treating each coil independently.

## Files

- `magnet.ino` — initial controller
