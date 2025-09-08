package com.example.human3dmodel

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Main activity that displays the 3D human model viewer
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: Human3DRenderer
    private lateinit var keypointsDataText: TextView
    private lateinit var resetButton: Button
    
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isRotating = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupGLSurfaceView()
        setupTouchHandling()
        setupUI()
    }
    
    private fun initializeViews() {
        glSurfaceView = findViewById(R.id.glSurfaceView)
        keypointsDataText = findViewById(R.id.keypointsData)
        resetButton = findViewById(R.id.resetButton)
    }
    
    private fun setupGLSurfaceView() {
        // Create an OpenGL ES 2.0 context
        glSurfaceView.setEGLContextClientVersion(2)
        
        // Set the renderer for drawing on the GLSurfaceView
        renderer = Human3DRenderer()
        glSurfaceView.setRenderer(renderer)
        
        // Render the view only when there is a change in the drawing data
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        
        // Set up keypoints change listener
        renderer.onKeypointsChanged = { keypoints ->
            runOnUiThread {
                updateKeypointsDisplay(keypoints)
            }
        }
    }
    
    private fun setupTouchHandling() {
        // Scale gesture detector for zoom
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                val newDistance = 3.0f / scaleFactor  // Inverse relationship for intuitive zoom
                renderer.setCameraDistance(newDistance)
                glSurfaceView.requestRender()
                return true
            }
        })
        
        // Touch listener for rotation and joint manipulation
        glSurfaceView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            
            if (!scaleGestureDetector.isInProgress) {
                handleTouchEvent(event)
            }
            
            true
        }
    }
    
    private fun handleTouchEvent(event: MotionEvent) {
        val x = event.x
        val y = event.y
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = x
                lastTouchY = y
                isRotating = false
                
                // Convert screen coordinates to normalized coordinates for joint selection
                val normalizedX = (x / glSurfaceView.width) * 2 - 1
                val normalizedY = 1 - (y / glSurfaceView.height) * 2
                renderer.handleTouch(normalizedX, normalizedY, 0) // ACTION_DOWN
            }
            
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - lastTouchX
                val deltaY = y - lastTouchY
                
                // Determine if this is a rotation gesture or joint manipulation
                val distance = kotlin.math.sqrt(deltaX * deltaX + deltaY * deltaY)
                
                if (distance > 10 && !isRotating) {
                    isRotating = true
                }
                
                if (isRotating) {
                    // Camera rotation
                    val rotationScale = 0.5f
                    val newAngleX = deltaY * rotationScale
                    val newAngleY = deltaX * rotationScale
                    renderer.setCameraRotation(newAngleX, newAngleY)
                } else {
                    // Joint manipulation
                    val normalizedX = (x / glSurfaceView.width) * 2 - 1
                    val normalizedY = 1 - (y / glSurfaceView.height) * 2
                    renderer.handleTouch(normalizedX, normalizedY, 2) // ACTION_MOVE
                }
                
                lastTouchX = x
                lastTouchY = y
                glSurfaceView.requestRender()
            }
            
            MotionEvent.ACTION_UP -> {
                val normalizedX = (x / glSurfaceView.width) * 2 - 1
                val normalizedY = 1 - (y / glSurfaceView.height) * 2
                renderer.handleTouch(normalizedX, normalizedY, 1) // ACTION_UP
                isRotating = false
                glSurfaceView.requestRender()
            }
        }
    }
    
    private fun setupUI() {
        resetButton.setOnClickListener {
            renderer.resetPose()
            glSurfaceView.requestRender()
        }
        
        // Initial keypoints display
        updateKeypointsDisplay(renderer.getKeypoints())
    }
    
    private fun updateKeypointsDisplay(keypoints: HumanKeypoints) {
        val displayText = keypoints.toDisplayString()
        keypointsDataText.text = displayText
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
}