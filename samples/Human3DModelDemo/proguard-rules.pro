# Add project specific ProGuard rules here.
-keep class com.example.human3dmodel.** { *; }

# Keep OpenGL related classes
-keep class android.opengl.** { *; }
-keep class javax.microedition.khronos.** { *; }

# Keep touch event related classes
-keep class android.view.** { *; }