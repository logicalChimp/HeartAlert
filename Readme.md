HeartAlert is a (very) simple monitor for the Zephyr HxM heart monitor.

It is mainly intended for monitoring the heart rate in the background, and send a Vibration Alert to the MetaWatch if the heart rate goes out of bounds.  Currently supports both min and max thresholds.

It does have a basic display for the MetaWatch, showing heart rate, packet number, steps, etc - but be warned - because this updates once a second, it will be *very* taxing on the MetaWatch battery.

The main app interface on the phone is just a settings panel - there is no pretty interface with graphs etc.

Note: this project relies on AndroidUtils (https://github.com/logicalChimp/AndroidUtils) which provide a number of MetaWatch related classes. I will try to reduce the burden of this dependency at some point (probably by including it as a Jar)