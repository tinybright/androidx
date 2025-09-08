# 3D Human Body Model Demo

This sample demonstrates a 3D human body model viewer with joint manipulation capabilities that outputs 33 human body keypoint data.

## Features

- **3D Model Rendering**: Displays a 3D human model using OpenGL ES 2.0
- **Joint Manipulation**: Touch and drag joints to rotate them 
- **33 Keypoints**: Outputs standard MediaPipe Pose 33 keypoint data in real-time
- **Camera Controls**: Touch to rotate camera view, pinch to zoom
- **Reset Functionality**: Reset to default T-pose

## Usage

1. Touch and drag on the 3D model to rotate the camera view
2. Use pinch gestures to zoom in/out
3. Touch near joints (shoulders, elbows, knees) to manipulate them
4. View the 33 keypoint coordinates in the bottom panel
5. Use the "Reset Pose" button to return to T-pose

## Implementation Details

- Uses OpenGL ES 2.0 for 3D rendering
- Implements MediaPipe Pose 33-point standard
- Real-time joint rotation with touch interaction
- Displays keypoint data in both 3D world coordinates and normalized format

## Dependencies

- AndroidX AppCompat
- AndroidX Core
- AndroidX Fragment
- ConstraintLayout
- Material Design Components