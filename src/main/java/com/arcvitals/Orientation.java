package com.arcvitals;

// Which way a curved bar bows: LEFT/RIGHT are the vitals' vertical crescents; TOP/BOTTOM are the
// horizontal crescents the swing timer uses. Package-private: not a config return type (the swing
// timer's public SwingPlacement maps to these internally).
enum Orientation {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM
}
