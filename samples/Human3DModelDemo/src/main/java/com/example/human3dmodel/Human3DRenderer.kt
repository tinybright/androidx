package com.example.human3dmodel

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*

/**
 * OpenGL ES 2.0 renderer for 3D human body model with joint manipulation
 */
class Human3DRenderer : GLSurfaceView.Renderer {
    
    private val humanKeypoints = HumanKeypoints()
    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    
    private var shaderProgram: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    
    // Camera parameters
    private var cameraDistance = 3.0f
    private var cameraAngleX = 0f
    private var cameraAngleY = 0f
    
    // Touch interaction
    private var selectedJoint: Int = -1
    private var touchX = 0f
    private var touchY = 0f
    
    // Callback for keypoints update
    var onKeypointsChanged: ((HumanKeypoints) -> Unit)? = null
    
    companion object {
        private const val COORDS_PER_VERTEX = 3
        private const val VERTEX_STRIDE = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
        
        // Vertex shader source code
        private const val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
                gl_PointSize = 10.0;
            }
        """
        
        // Fragment shader source code
        private const val fragmentShaderCode = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """
    }
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        // Initialize the shader program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        
        shaderProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
        
        // Get handles to shader attributes and uniforms
        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor")
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        
        // Initialize human pose
        humanKeypoints.initializeTPose()
        onKeypointsChanged?.invoke(humanKeypoints)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        // Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        // Use the shader program
        GLES20.glUseProgram(shaderProgram)
        
        // Set up the camera view matrix
        Matrix.setLookAtM(viewMatrix, 0,
            cameraDistance * sin(Math.toRadians(cameraAngleY.toDouble())).toFloat() * cos(Math.toRadians(cameraAngleX.toDouble())).toFloat(),
            cameraDistance * sin(Math.toRadians(cameraAngleX.toDouble())).toFloat(),
            cameraDistance * cos(Math.toRadians(cameraAngleY.toDouble())).toFloat() * cos(Math.toRadians(cameraAngleX.toDouble())).toFloat(),
            0f, 0f, 0f,
            0f, 1.0f, 0.0f)
        
        // Calculate the projection and view transformation
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        
        // Pass the transformation matrix to the shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        
        // Draw the human model
        drawHumanModel()
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        
        val ratio: Float = width.toFloat() / height.toFloat()
        
        // Create a projection matrix
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
    }
    
    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
    
    private fun drawHumanModel() {
        val keypoints = humanKeypoints.getAllKeypoints()
        
        // Draw keypoints as spheres/points
        GLES20.glEnable(GLES20.GL_PROGRAM_POINT_SIZE)
        drawKeypoints(keypoints)
        
        // Draw skeleton connections
        drawSkeleton(keypoints)
    }
    
    private fun drawKeypoints(keypoints: Array<Point3D>) {
        val vertices = FloatArray(keypoints.size * 3)
        for (i in keypoints.indices) {
            vertices[i * 3] = keypoints[i].x
            vertices[i * 3 + 1] = keypoints[i].y
            vertices[i * 3 + 2] = keypoints[i].z
        }
        
        val vertexBuffer = createFloatBuffer(vertices)
        
        // Enable vertex attribute array
        GLES20.glEnableVertexAttribArray(positionHandle)
        
        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer)
        
        // Set color for keypoints
        val color = if (selectedJoint != -1) floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f) else floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        
        // Draw the keypoints
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, keypoints.size)
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
    
    private fun drawSkeleton(keypoints: Array<Point3D>) {
        // Define skeleton connections (bone structure)
        val connections = listOf(
            // Head connections
            Pair(0, 1), Pair(1, 2), Pair(2, 3), // nose to left eye
            Pair(0, 4), Pair(4, 5), Pair(5, 6), // nose to right eye
            Pair(2, 7), Pair(5, 8), // eyes to ears
            Pair(9, 10), // mouth
            
            // Torso
            Pair(11, 12), // shoulders
            Pair(11, 23), Pair(12, 24), // shoulders to hips
            Pair(23, 24), // hips
            
            // Left arm
            Pair(11, 13), Pair(13, 15), // shoulder to elbow to wrist
            Pair(15, 17), Pair(15, 19), Pair(15, 21), // wrist to fingers
            
            // Right arm
            Pair(12, 14), Pair(14, 16), // shoulder to elbow to wrist
            Pair(16, 18), Pair(16, 20), Pair(16, 22), // wrist to fingers
            
            // Left leg
            Pair(23, 25), Pair(25, 27), // hip to knee to ankle
            Pair(27, 29), Pair(27, 31), // ankle to heel and toe
            
            // Right leg
            Pair(24, 26), Pair(26, 28), // hip to knee to ankle
            Pair(28, 30), Pair(28, 32)  // ankle to heel and toe
        )
        
        for (connection in connections) {
            drawLine(keypoints[connection.first], keypoints[connection.second])
        }
    }
    
    private fun drawLine(start: Point3D, end: Point3D) {
        val vertices = floatArrayOf(
            start.x, start.y, start.z,
            end.x, end.y, end.z
        )
        
        val vertexBuffer = createFloatBuffer(vertices)
        
        // Enable vertex attribute array
        GLES20.glEnableVertexAttribArray(positionHandle)
        
        // Prepare the coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, vertexBuffer)
        
        // Set color for skeleton lines
        val lineColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f) // White
        GLES20.glUniform4fv(colorHandle, 1, lineColor, 0)
        
        // Draw the line
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
    
    private fun createFloatBuffer(data: FloatArray): java.nio.FloatBuffer {
        val bb = java.nio.ByteBuffer.allocateDirect(data.size * 4)
        bb.order(java.nio.ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(data)
        fb.position(0)
        return fb
    }
    
    // Camera controls
    fun setCameraRotation(angleX: Float, angleY: Float) {
        cameraAngleX = angleX
        cameraAngleY = angleY
    }
    
    fun setCameraDistance(distance: Float) {
        cameraDistance = distance.coerceIn(1f, 10f)
    }
    
    // Joint manipulation
    fun handleTouch(x: Float, y: Float, action: Int) {
        when (action) {
            0 -> { // ACTION_DOWN
                selectedJoint = findNearestJoint(x, y)
                touchX = x
                touchY = y
            }
            1 -> { // ACTION_UP
                selectedJoint = -1
            }
            2 -> { // ACTION_MOVE
                if (selectedJoint != -1) {
                    manipulateJoint(selectedJoint, x - touchX, y - touchY)
                    touchX = x
                    touchY = y
                    onKeypointsChanged?.invoke(humanKeypoints)
                }
            }
        }
    }
    
    private fun findNearestJoint(x: Float, y: Float): Int {
        // Simplified joint selection - in a real implementation,
        // you would project 3D coordinates to screen space and find the closest
        return 13 // Default to left elbow for demonstration
    }
    
    private fun manipulateJoint(jointIndex: Int, deltaX: Float, deltaY: Float) {
        val keypoints = humanKeypoints.getAllKeypoints()
        if (jointIndex in keypoints.indices) {
            // Simple rotation around parent joint
            val sensitivity = 0.01f
            when (jointIndex) {
                13 -> { // Left elbow
                    rotateAroundJoint(humanKeypoints.leftShoulder, humanKeypoints.leftElbow, 
                                    humanKeypoints.leftWrist, deltaX * sensitivity, deltaY * sensitivity)
                }
                14 -> { // Right elbow
                    rotateAroundJoint(humanKeypoints.rightShoulder, humanKeypoints.rightElbow,
                                    humanKeypoints.rightWrist, deltaX * sensitivity, deltaY * sensitivity)
                }
                25 -> { // Left knee
                    rotateAroundJoint(humanKeypoints.leftHip, humanKeypoints.leftKnee,
                                    humanKeypoints.leftAnkle, deltaX * sensitivity, deltaY * sensitivity)
                }
                26 -> { // Right knee
                    rotateAroundJoint(humanKeypoints.rightHip, humanKeypoints.rightKnee,
                                    humanKeypoints.rightAnkle, deltaX * sensitivity, deltaY * sensitivity)
                }
            }
        }
    }
    
    private fun rotateAroundJoint(parent: Point3D, joint: Point3D, child: Point3D, angleX: Float, angleY: Float) {
        // Vector from parent to joint
        val parentToJoint = Point3D(joint.x - parent.x, joint.y - parent.y, joint.z - parent.z)
        
        // Vector from joint to child
        val jointToChild = Point3D(child.x - joint.x, child.y - joint.y, child.z - joint.z)
        
        // Rotate the joint-to-child vector
        val cosX = cos(angleX)
        val sinX = sin(angleX)
        val cosY = cos(angleY)
        val sinY = sin(angleY)
        
        // Simple rotation around Y axis (yaw) and X axis (pitch)
        val newX = jointToChild.x * cosY - jointToChild.z * sinY
        val newZ = jointToChild.x * sinY + jointToChild.z * cosY
        val newY = jointToChild.y * cosX - newZ * sinX
        val finalZ = jointToChild.y * sinX + newZ * cosX
        
        // Update child position
        child.x = joint.x + newX
        child.y = joint.y + newY
        child.z = joint.z + finalZ
    }
    
    fun resetPose() {
        humanKeypoints.initializeTPose()
        onKeypointsChanged?.invoke(humanKeypoints)
    }
    
    fun getKeypoints(): HumanKeypoints = humanKeypoints
}