#!/bin/bash
# Generates all required vector drawable XML files
# Run this from the project root

DRAWABLE="app/src/main/res/drawable"
mkdir -p "$DRAWABLE"

# ic_add.xml
cat > "$DRAWABLE/ic_add.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
</vector>
EOF

# ic_build.xml
cat > "$DRAWABLE/ic_build.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M22.7,19l-9.1,-9.1c0.9,-2.3 0.4,-5 -1.5,-6.9 -2,-2 -5,-2.4 -7.4,-1.3L9,6 6,9 1.6,4.7C0.4,7.1 0.9,10.1 2.9,12.1c1.9,1.9 4.6,2.4 6.9,1.5l9.1,9.1c0.4,0.4 1,0.4 1.4,0L22.7,20.5C23.1,20.1 23.1,19.4 22.7,19z"/>
</vector>
EOF

# ic_folder.xml
cat > "$DRAWABLE/ic_folder.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M10,4H4c-1.1,0 -1.99,0.9 -1.99,2L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8c0,-1.1 -0.9,-2 -2,-2h-8l-2,-2z"/>
</vector>
EOF

# ic_folder_open.xml
cat > "$DRAWABLE/ic_folder_open.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M20,6h-8l-2,-2H4C2.9,4 2,4.9 2,6v12c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8C22,6.9 21.1,6 20,6zM20,18H4V8h16V18z"/>
</vector>
EOF

# ic_settings.xml
cat > "$DRAWABLE/ic_settings.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M19.14,12.94c0.04,-0.3 0.06,-0.61 0.06,-0.94c0,-0.32 -0.02,-0.64 -0.07,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58C4.84,11.36 4.8,11.69 4.8,12s0.02,0.64 0.07,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.44,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6s3.6,1.62 3.6,3.6S13.98,15.6 12,15.6z"/>
</vector>
EOF

# ic_template.xml
cat > "$DRAWABLE/ic_template.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M19,3H5C3.9,3 3,3.9 3,5v14c0,1.1 0.9,2 2,2h14c1.1,0 2,-0.9 2,-2V5C21,3.9 20.1,3 19,3zM9,17H7v-7h2V17zM13,17h-2V7h2V17zM17,17h-2v-4h2V17z"/>
</vector>
EOF

# ic_delete.xml
cat > "$DRAWABLE/ic_delete.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2V7H6v12zM19,4h-3.5l-1,-1h-5l-1,1H5v2h14V4z"/>
</vector>
EOF

# ic_save.xml
cat > "$DRAWABLE/ic_save.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M17,3H5C3.89,3 3,3.9 3,5v14c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2V7L17,3zM12,19c-1.66,0 -3,-1.34 -3,-3s1.34,-3 3,-3 3,1.34 3,3 -1.34,3 -3,3zM15,9H5V5h10v4z"/>
</vector>
EOF

# ic_undo.xml
cat > "$DRAWABLE/ic_undo.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M12.5,8c-2.65,0 -5.05,1 -6.9,2.6L2,7v9h9l-3.62,-3.62c1.39,-1.16 3.16,-1.88 5.12,-1.88 3.54,0 6.55,2.31 7.6,5.5l2.37,-0.78C21.08,11.03 17.15,8 12.5,8z"/>
</vector>
EOF

# ic_redo.xml
cat > "$DRAWABLE/ic_redo.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M18.4,10.6C16.55,9 14.15,8 11.5,8c-4.65,0 -8.58,3.03 -9.96,7.22L3.9,15c1.05,-3.19 4.05,-5.5 7.6,-5.5 1.95,0 3.73,0.72 5.12,1.88L13,15h9V6l-3.6,4.6z"/>
</vector>
EOF

# ic_format.xml
cat > "$DRAWABLE/ic_format.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M3,21h18v-2H3v2zM3,10v8l4,-2 4,2 4,-2 4,2V10l-4,2 -4,-2 -4,2 -4,-2zM3,3v2h18V3H3z"/>
</vector>
EOF

# ic_add_file.xml
cat > "$DRAWABLE/ic_add_file.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M14,2H6C4.9,2 4,2.9 4,4v16c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V8L14,2zM16,16h-3v3h-2v-3H8v-2h3v-3h2v3h3V16zM13,9V3.5L18.5,9H13z"/>
</vector>
EOF

# ic_download.xml
cat > "$DRAWABLE/ic_download.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M19,9h-4V3H9v6H5l7,7 7,-7zM5,18v2h14v-2H5z"/>
</vector>
EOF

# ic_github.xml
cat > "$DRAWABLE/ic_github.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M12,2A10,10 0,0 0,2 12c0,4.42 2.87,8.17 6.84,9.5 0.5,0.08 0.66,-0.23 0.66,-0.5v-1.69c-2.77,0.6 -3.36,-1.34 -3.36,-1.34 -0.46,-1.16 -1.11,-1.47 -1.11,-1.47 -0.91,-0.62 0.07,-0.6 0.07,-0.6 1,0.07 1.53,1.03 1.53,1.03 0.87,1.52 2.34,1.07 2.91,0.83 0.09,-0.65 0.35,-1.09 0.63,-1.34C7.81,15.69 5.18,14.76 5.18,10.63c0,-1.16 0.41,-2.11 1.08,-2.85C6.17,7.5 5.83,6.45 6.41,5.04c0,0 0.88,-0.28 2.88,1.08a10.02,10.02 0,0 1,5.24 0c2,-1.36 2.88,-1.08 2.88,-1.08 0.58,1.41 0.24,2.46 0.12,2.74 0.67,0.74 1.08,1.69 1.08,2.85 0,4.14 -2.63,5.05 -5.13,5.32 0.4,0.35 0.76,1.04 0.76,2.1v3.12c0,0.27 0.16,0.59 0.67,0.5C19.14,20.16 22,16.42 22,12A10,10 0,0 0,12 2z"/>
</vector>
EOF

# ic_preview.xml
cat > "$DRAWABLE/ic_preview.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M12,4.5C7,4.5 2.73,7.61 1,12c1.73,4.39 6,7.5 11,7.5s9.27,-3.11 11,-7.5c-1.73,-4.39 -6,-7.5 -11,-7.5zM12,17c-2.76,0 -5,-2.24 -5,-5s2.24,-5 5,-5 5,2.24 5,5 -2.24,5 -5,5zM12,9c-1.66,0 -3,1.34 -3,3s1.34,3 3,3 3,-1.34 3,-3 -1.34,-3 -3,-3z"/>
</vector>
EOF

# ic_notification.xml
cat > "$DRAWABLE/ic_notification.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="@android:color/white"
        android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.9,2 2,2zM18,16v-5c0,-3.07 -1.64,-5.64 -4.5,-6.32V4c0,-0.83 -0.67,-1.5 -1.5,-1.5s-1.5,0.67 -1.5,1.5v0.68C7.63,5.36 6,7.92 6,11v5l-2,2v1h16v-1l-2,-2z"/>
</vector>
EOF

# ic_app_logo.xml
cat > "$DRAWABLE/ic_app_logo.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#1a1a2e"
        android:pathData="M0,0 L108,0 L108,108 L0,108 Z"/>
    <path android:fillColor="#BB86FC"
        android:pathData="M20,32 L52,18 L88,28 L88,58 L54,76 L20,62 Z"
        android:strokeColor="#9965E8" android:strokeWidth="2"/>
    <path android:fillColor="#03DAC5"
        android:pathData="M38,44 L54,38 L70,44 L70,58 L54,64 L38,58 Z"/>
</vector>
EOF

# ic_app_logo_round.xml
cat > "$DRAWABLE/ic_app_logo_round.xml" << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#1a1a2e"
        android:pathData="M54,54m-54,0a54,54 0,1 1,108 0a54,54 0,1 1,-108 0"/>
    <path android:fillColor="#BB86FC"
        android:pathData="M20,36 L54,22 L88,36 L88,60 L54,74 L20,60 Z"
        android:strokeColor="#9965E8" android:strokeWidth="2"/>
    <path android:fillColor="#03DAC5"
        android:pathData="M38,46 L54,40 L70,46 L70,58 L54,64 L38,58 Z"/>
</vector>
EOF

# bg_chip.xml
cat > "$DRAWABLE/bg_chip.xml" << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="16dp"/>
    <stroke android:width="1dp" android:color="@color/primary"/>
</shape>
EOF

echo "All drawables created successfully!"
