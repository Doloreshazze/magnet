/*
  MAGNET — 3-coil levitation experiment
  Arduino Nano / ATmega328P

  Hardware:
    A0, A1, A2 : Hall sensors
    D3, D5, D6  : PWM -> MOSFET gate drivers for coils

  IMPORTANT:
  - Never drive a coil directly from an Arduino pin.
  - Use logic-level N-MOSFETs, flyback diodes, and a separate coil supply.
  - This is a STARTING controller, not a tuned levitation controller.
  - Hall sensor polarity and coil polarity must be calibrated experimentally.

  Concept:
  Three coils are arranged symmetrically around the steel ball.
  Each Hall sensor measures the local magnetic field.
  The controller uses the difference between each sensor and its calibrated
  center value to produce a correction for the corresponding coil.

  First test with very low PWM and WITHOUT the 40 g ball.
*/

const byte HALL_PINS[3] = {A0, A1, A2};
const byte COIL_PINS[3] = {3, 5, 6};

// Calibration: measured Hall value when the ball is at the desired center.
// Replace these after calibration.
float hallCenter[3] = {512.0, 512.0, 512.0};

// Controller gains — MUST be tuned experimentally.
float Kp[3] = {0.8, 0.8, 0.8};
float Kd[3] = {0.15, 0.15, 0.15};

// Feed-forward holding current.
// Start at zero. Increase very carefully only after feedback works.
int basePWM = 0;

float previousError[3] = {0, 0, 0};

const int PWM_MIN = 0;
const int PWM_MAX = 180;       // deliberately conservative starting limit
const unsigned long LOOP_US = 2000; // 500 Hz

unsigned long lastLoop = 0;

void setup() {
  Serial.begin(115200);

  for (int i = 0; i < 3; i++) {
    pinMode(HALL_PINS[i], INPUT);
    pinMode(COIL_PINS[i], OUTPUT);
    analogWrite(COIL_PINS[i], 0);
  }

  delay(1000);

  // Average a few hundred samples to get initial center values.
  // Keep the ball mechanically fixed at the desired center during this step.
  for (int i = 0; i < 3; i++) {
    long sum = 0;
    for (int n = 0; n < 300; n++) {
      sum += analogRead(HALL_PINS[i]);
      delay(2);
    }
    hallCenter[i] = sum / 300.0;
  }

  Serial.println(F("MAGNET controller started"));
  Serial.println(F("Hall centers:"));
  for (int i = 0; i < 3; i++) {
    Serial.println(hallCenter[i]);
  }
}

void loop() {
  unsigned long now = micros();
  if ((unsigned long)(now - lastLoop) < LOOP_US) return;

  float dt = (now - lastLoop) / 1000000.0;
  lastLoop = now;

  if (dt <= 0 || dt > 0.1) dt = LOOP_US / 1000000.0;

  for (int i = 0; i < 3; i++) {
    int raw = analogRead(HALL_PINS[i]);

    // Error: local magnetic field relative to center.
    float error = hallCenter[i] - raw;

    float derivative = (error - previousError[i]) / dt;
    previousError[i] = error;

    float correction = Kp[i] * error + Kd[i] * derivative;

    int pwm = (int)(basePWM + correction);

    pwm = constrain(pwm, PWM_MIN, PWM_MAX);

    analogWrite(COIL_PINS[i], pwm);

    // Serial output is intentionally slow; don't print every control cycle.
  }

  static unsigned long lastPrint = 0;
  if (millis() - lastPrint > 100) {
    lastPrint = millis();

    Serial.print(F("H: "));
    Serial.print(analogRead(A0));
    Serial.print(' ');
    Serial.print(analogRead(A1));
    Serial.print(' ');
    Serial.print(analogRead(A2));

    Serial.print(F(" | PWM: "));
    Serial.print((int)analogRead(A0));
    Serial.print(' ');
    Serial.print((int)analogRead(A1));
    Serial.print(' ');
    Serial.println((int)analogRead(A2));
  }
}
