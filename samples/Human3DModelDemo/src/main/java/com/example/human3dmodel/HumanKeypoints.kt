package com.example.human3dmodel

/**
 * Represents a 3D point with x, y, z coordinates
 */
data class Point3D(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    override fun toString(): String = "(${"%.3f".format(x)}, ${"%.3f".format(y)}, ${"%.3f".format(z)})"
}

/**
 * Represents the 33 human body keypoints following MediaPipe Pose standard
 * https://google.github.io/mediapipe/solutions/pose.html
 */
class HumanKeypoints {
    
    // Face keypoints (0-10)
    var nose = Point3D()                        // 0
    var leftEyeInner = Point3D()               // 1
    var leftEye = Point3D()                    // 2
    var leftEyeOuter = Point3D()               // 3
    var rightEyeInner = Point3D()              // 4
    var rightEye = Point3D()                   // 5
    var rightEyeOuter = Point3D()              // 6
    var leftEar = Point3D()                    // 7
    var rightEar = Point3D()                   // 8
    var mouthLeft = Point3D()                  // 9
    var mouthRight = Point3D()                 // 10
    
    // Upper body keypoints (11-16)
    var leftShoulder = Point3D()               // 11
    var rightShoulder = Point3D()              // 12
    var leftElbow = Point3D()                  // 13
    var rightElbow = Point3D()                 // 14
    var leftWrist = Point3D()                  // 15
    var rightWrist = Point3D()                 // 16
    
    // Hand keypoints (17-22)
    var leftPinky = Point3D()                  // 17
    var rightPinky = Point3D()                 // 18
    var leftIndex = Point3D()                  // 19
    var rightIndex = Point3D()                 // 20
    var leftThumb = Point3D()                  // 21
    var rightThumb = Point3D()                 // 22
    
    // Lower body keypoints (23-32)
    var leftHip = Point3D()                    // 23
    var rightHip = Point3D()                   // 24
    var leftKnee = Point3D()                   // 25
    var rightKnee = Point3D()                  // 26
    var leftAnkle = Point3D()                  // 27
    var rightAnkle = Point3D()                 // 28
    var leftHeel = Point3D()                   // 29
    var rightHeel = Point3D()                  // 30
    var leftFootIndex = Point3D()              // 31
    var rightFootIndex = Point3D()             // 32
    
    /**
     * Get all keypoints as an array for easy iteration
     */
    fun getAllKeypoints(): Array<Point3D> = arrayOf(
        nose, leftEyeInner, leftEye, leftEyeOuter, rightEyeInner, rightEye, rightEyeOuter,
        leftEar, rightEar, mouthLeft, mouthRight, leftShoulder, rightShoulder, leftElbow,
        rightElbow, leftWrist, rightWrist, leftPinky, rightPinky, leftIndex, rightIndex,
        leftThumb, rightThumb, leftHip, rightHip, leftKnee, rightKnee, leftAnkle,
        rightAnkle, leftHeel, rightHeel, leftFootIndex, rightFootIndex
    )
    
    /**
     * Get keypoint names for display
     */
    fun getKeypointNames(): Array<String> = arrayOf(
        "Nose", "Left Eye Inner", "Left Eye", "Left Eye Outer", "Right Eye Inner", "Right Eye", "Right Eye Outer",
        "Left Ear", "Right Ear", "Mouth Left", "Mouth Right", "Left Shoulder", "Right Shoulder", "Left Elbow",
        "Right Elbow", "Left Wrist", "Right Wrist", "Left Pinky", "Right Pinky", "Left Index", "Right Index",
        "Left Thumb", "Right Thumb", "Left Hip", "Right Hip", "Left Knee", "Right Knee", "Left Ankle",
        "Right Ankle", "Left Heel", "Right Heel", "Left Foot Index", "Right Foot Index"
    )
    
    /**
     * Initialize with default T-pose positions
     */
    fun initializeTPose() {
        // Head (centered at origin, facing forward)
        nose.apply { x = 0f; y = 1.7f; z = 0.1f }
        leftEyeInner.apply { x = -0.03f; y = 1.72f; z = 0.08f }
        leftEye.apply { x = -0.06f; y = 1.72f; z = 0.08f }
        leftEyeOuter.apply { x = -0.09f; y = 1.72f; z = 0.08f }
        rightEyeInner.apply { x = 0.03f; y = 1.72f; z = 0.08f }
        rightEye.apply { x = 0.06f; y = 1.72f; z = 0.08f }
        rightEyeOuter.apply { x = 0.09f; y = 1.72f; z = 0.08f }
        leftEar.apply { x = -0.12f; y = 1.7f; z = 0f }
        rightEar.apply { x = 0.12f; y = 1.7f; z = 0f }
        mouthLeft.apply { x = -0.03f; y = 1.65f; z = 0.08f }
        mouthRight.apply { x = 0.03f; y = 1.65f; z = 0.08f }
        
        // Upper body
        leftShoulder.apply { x = -0.2f; y = 1.5f; z = 0f }
        rightShoulder.apply { x = 0.2f; y = 1.5f; z = 0f }
        leftElbow.apply { x = -0.5f; y = 1.2f; z = 0f }
        rightElbow.apply { x = 0.5f; y = 1.2f; z = 0f }
        leftWrist.apply { x = -0.8f; y = 1.2f; z = 0f }
        rightWrist.apply { x = 0.8f; y = 1.2f; z = 0f }
        
        // Hands
        leftPinky.apply { x = -0.85f; y = 1.15f; z = 0f }
        rightPinky.apply { x = 0.85f; y = 1.15f; z = 0f }
        leftIndex.apply { x = -0.85f; y = 1.25f; z = 0f }
        rightIndex.apply { x = 0.85f; y = 1.25f; z = 0f }
        leftThumb.apply { x = -0.82f; y = 1.22f; z = 0.03f }
        rightThumb.apply { x = 0.82f; y = 1.22f; z = 0.03f }
        
        // Lower body
        leftHip.apply { x = -0.1f; y = 0.9f; z = 0f }
        rightHip.apply { x = 0.1f; y = 0.9f; z = 0f }
        leftKnee.apply { x = -0.1f; y = 0.5f; z = 0f }
        rightKnee.apply { x = 0.1f; y = 0.5f; z = 0f }
        leftAnkle.apply { x = -0.1f; y = 0.1f; z = 0f }
        rightAnkle.apply { x = 0.1f; y = 0.1f; z = 0f }
        leftHeel.apply { x = -0.1f; y = 0f; z = -0.05f }
        rightHeel.apply { x = 0.1f; y = 0f; z = -0.05f }
        leftFootIndex.apply { x = -0.1f; y = 0f; z = 0.15f }
        rightFootIndex.apply { x = 0.1f; y = 0f; z = 0.15f }
    }
    
    /**
     * Format all keypoints as a readable string
     */
    fun toDisplayString(): String {
        val names = getKeypointNames()
        val points = getAllKeypoints()
        val builder = StringBuilder()
        
        for (i in points.indices) {
            builder.append("${"%2d".format(i)}: ${names[i]} = ${points[i]}\n")
        }
        
        return builder.toString()
    }
}