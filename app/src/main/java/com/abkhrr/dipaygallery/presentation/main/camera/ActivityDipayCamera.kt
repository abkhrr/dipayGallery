package com.abkhrr.dipaygallery.presentation.main.camera

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.os.Environment.*
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.abkhrr.dipaygallery.BR
import com.abkhrr.dipaygallery.R
import com.abkhrr.dipaygallery.databinding.ActivityDipayCameraBinding
import com.abkhrr.dipaygallery.domain.dto.Gallery
import com.abkhrr.dipaygallery.factory.ViewModelFactory
import com.abkhrr.dipaygallery.presentation.base.BaseActivity
import com.abkhrr.dipaygallery.presentation.main.shared.SharedViewModel
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_dipay_camera.*
import java.io.*
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.Semaphore
import javax.inject.Inject
import kotlin.collections.ArrayList

@Suppress("SameParameterValue", "DEPRECATION")
class ActivityDipayCamera : BaseActivity<ActivityDipayCameraBinding, SharedViewModel>(), HasAndroidInjector {

    @Inject
    lateinit var factory: ViewModelFactory

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_dipay_camera

    override val viewModel: SharedViewModel
        get() = ViewModelProvider(this, factory).get(SharedViewModel::class.java)

    init {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90)
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0)
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270)
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    init {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90)
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0)
    }

    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageDimension: Size? = null
    private var imageReader: ImageReader? = null
    private var mSensorOrientation: Int? = null

    private var file: File? = null

    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private var mMediaRecorder: MediaRecorder? = null
    private var mIsRecordingVideo = false
    private var mRecorderSurface: Surface? = null
    private val max                                   = 1000000
    private val min                                   = 100000
    private val rand                                  = Random()
    private val randomNum: Int                        = rand.nextInt(max - min + 1) + min

    private var mPreviewSize: Size? = null
    private var mVideoSize: Size? = null
    private val cameraOpenCloseLock = Semaphore(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvCapture.surfaceTextureListener = textureListener
        setupOnclick()
    }

    private fun setupOnclick(){
        btnVideo.setOnClickListener {
            if (mIsRecordingVideo) {
                stopRecordingVideo()
                chronometerCamera.stop()
                layoutTimer.visibility = View.INVISIBLE
            } else {
                startRecordingVideo()
                layoutTimer.visibility = View.VISIBLE
                chronometerCamera.base = SystemClock.elapsedRealtime()
                chronometerCamera.start()
            }
        }

        btnCapture.setOnClickListener {
            val outputDirectory = getDirectory(DIRECTORY_DCIM, "DipayGallery")
            file = File(outputDirectory, "/" + System.currentTimeMillis() + randomNum + ".jpg")
            takePicture()
        }
    }

    private fun getDirectory(inWhichFolder: String, yourFolderName: String): File? {
        var outputDirectory: File? = null
        val externalStorageState = getExternalStorageState()
        if (externalStorageState == MEDIA_MOUNTED) {
            val pictureDirectory = getExternalStoragePublicDirectory(inWhichFolder)
            outputDirectory = File(pictureDirectory, yourFolderName)
            if (!outputDirectory.exists()) {
                if (!outputDirectory.mkdirs()) {
                    Log.e(
                        "ERROR_DIR",
                        "Failed to create directory: " + outputDirectory.absolutePath
                    )
                    outputDirectory = null
                }
            }
        }
        return outputDirectory
    }


    private var textureListener: TextureView.SurfaceTextureListener = object :
        TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }


    private fun openCamera(width: Int, height: Int) {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        Log.e("tag", "is camera open")
        try {
            cameraId = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraId.toString())
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@ActivityDipayCamera,
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                        REQUEST_CAMERA_PERMISSION
                )
                return
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
            mPreviewSize = chooseOptimalSize(
                map.getOutputSizes(SurfaceTexture::class.java),
                width, height, mVideoSize!!
            )
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                tvCapture.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
            } else {
                tvCapture.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
            }
            mMediaRecorder = MediaRecorder()
            manager.openCamera(cameraId.toString(), stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        Log.e("tag", "openCamera X")
    }

    private var stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            Log.e("tag", "onOpened")
            cameraOpenCloseLock.release()
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice!!.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    fun createCameraPreview() {
        try {
            val texture: SurfaceTexture = tvCapture.surfaceTexture!!
            texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder!!.addTarget(surface)
            cameraDevice!!.createCaptureSession(
                listOf(surface), object : CameraCaptureSession.StateCallback() {

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(
                            this@ActivityDipayCamera,
                            "Configuration change",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onConfigured(session: CameraCaptureSession) {
                        if (null == cameraDevice) {
                            return
                        }
                        cameraCaptureSessions = session
                        updatePreview()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun updatePreview() {
        if (null == cameraDevice) {
            Log.e("tag", "updatePreview error, return")
        }
        captureRequestBuilder!!.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSessions!!.setRepeatingRequest(
                captureRequestBuilder!!.build(),
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun takePicture() {
        if (null == cameraDevice) {
            Log.e("tag", "cameraDevice is null")
            return
        }
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val characteristics = manager.getCameraCharacteristics(
                cameraDevice!!.id
            )
            val jpegSizes: Array<Size>?
            jpegSizes = characteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]!!.getOutputSizes(
                ImageFormat.JPEG
            )
            var width = 640
            var height = 480
            if (jpegSizes != null && jpegSizes.isNotEmpty()) {
                width = jpegSizes[0].width
                height = jpegSizes[0].height
            }
            val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
            val outputSurfaces: ArrayList<Surface> = ArrayList(2)
            outputSurfaces.add(reader.surface)
            outputSurfaces.add(Surface(tvCapture.surfaceTexture))
            val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(reader.surface)
            captureBuilder[CaptureRequest.CONTROL_MODE] = CameraMetadata.CONTROL_MODE_AUTO
            // Orientation
            val rotation: Int = windowManager.defaultDisplay.rotation
            captureBuilder[CaptureRequest.JPEG_ORIENTATION] = DEFAULT_ORIENTATIONS[rotation]
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler)
            val captureListener: CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result)
                    Toast.makeText(this@ActivityDipayCamera, "Saved:$file", Toast.LENGTH_SHORT).show()
                    val photoFileUri: Uri? = Uri.fromFile(file)
                    val myGallery = Gallery(
                        galleryId = 0,
                        name = photoFileUri?.lastPathSegment.toString(),
                        isVideos = false,
                        path = photoFileUri.toString()
                    )
                    viewModel.insertGallery(myGallery)
                    file?.absolutePath?.let { scanner(it) }
                    createCameraPreview()
                }
            }
            cameraDevice!!.createCaptureSession(
                outputSurfaces,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            session.capture(
                                captureBuilder.build(),
                                captureListener,
                                mBackgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                },
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private var readerListener: ImageReader.OnImageAvailableListener = object :
        ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            var image: Image? = null
            try {
                image = reader.acquireLatestImage()
                val buffer: ByteBuffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                save(bytes)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                image?.close()
            }
        }

        @Throws(IOException::class)
        private fun save(bytes: ByteArray) {
            var output: OutputStream? = null
            try {
                output = FileOutputStream(file)
                output.write(bytes)
            } finally {
                output?.close()
            }
        }
    }

    private fun startRecordingVideo() {
        if (null == cameraDevice || !tvCapture.isAvailable || null == imageDimension) {
            return
        }
        try {
            closePreviewSession()
            setUpMediaRecorder()
            val texture: SurfaceTexture = tvCapture.surfaceTexture!!
            texture.setDefaultBufferSize(imageDimension!!.width, imageDimension!!.height)
            captureRequestBuilder =
                cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            val surfaces: ArrayList<Surface> = ArrayList()

            // Set up Surface for the camera preview
            val previewSurface = Surface(texture)
            surfaces.add(previewSurface)
            captureRequestBuilder!!.addTarget(previewSurface)

            // Set up Surface for the MediaRecorder
            mRecorderSurface = mMediaRecorder!!.surface
            surfaces.add(mRecorderSurface!!)
            captureRequestBuilder!!.addTarget(mRecorderSurface!!)

            cameraDevice!!.createCaptureSession(
                surfaces,
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(session: CameraCaptureSession) {
                        cameraCaptureSessions = session
                        updatePreview()
                        runOnUiThread { // UI
                            btnVideo.setImageResource(R.drawable.ic_baseline_videocam_off)
                            layoutTimer.visibility = View.VISIBLE
                            mIsRecordingVideo = true
                            // Start recording
                            mMediaRecorder!!.start()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Toast.makeText(this@ActivityDipayCamera, "Failed", Toast.LENGTH_SHORT)
                            .show()
                    }

                },
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun setUpMediaRecorder() {

        val outputDirectory = getDirectory(DIRECTORY_DCIM, "DipayGallery")
        file = File(outputDirectory, "/" + System.currentTimeMillis() + randomNum + ".mp4")

        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder?.setOutputFile(file?.absolutePath.toString())
        mMediaRecorder?.setVideoEncodingBitRate(10000000)
        mMediaRecorder?.setVideoFrameRate(30)
        mMediaRecorder?.setVideoSize(mVideoSize!!.width, mVideoSize!!.height)
        mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val rotation = windowManager.defaultDisplay.rotation
        when (mSensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES -> mMediaRecorder!!.setOrientationHint(
                DEFAULT_ORIENTATIONS[rotation]
            )
            SENSOR_ORIENTATION_INVERSE_DEGREES -> mMediaRecorder!!.setOrientationHint(
                INVERSE_ORIENTATIONS[rotation]
            )
        }
        mMediaRecorder!!.prepare()
    }

    private fun stopRecordingVideo() {

        mIsRecordingVideo = false
        btnVideo.setImageResource(R.drawable.ic_baseline_videocam)

        mMediaRecorder?.apply {
            stop()
            reset()
        }

        scanner(file?.absolutePath.toString())

        val videoFileUri = Uri.fromFile(file)

        val g = Gallery(
            galleryId = 0,
            name = videoFileUri.lastPathSegment.toString(),
            isVideos = true,
            path = videoFileUri.toString()
        )
        viewModel.insertGallery(g)

        Toast.makeText(this@ActivityDipayCamera, "Video saved: $videoFileUri", Toast.LENGTH_SHORT).show()
        createCameraPreview()
    }

    private fun scanner(path: String) {
        MediaScannerConnection.scanFile(this, arrayOf(path), null) { mPath, uri ->
            Log.e("CONTENT_MEDIA_SCANNER", "Finished scanning $mPath New row: $uri")
        }
    }


    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread!!.start()
        mBackgroundHandler = Handler(mBackgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun closePreviewSession() {
        if (cameraCaptureSessions != null) {
            cameraCaptureSessions?.close()
            cameraCaptureSessions = null
        }
    }

    private fun closeCamera() {
        if (null != cameraCaptureSessions) {
            cameraCaptureSessions!!.close()
            cameraCaptureSessions = null
        }
        if (null != cameraDevice) {
            cameraDevice!!.close()
            cameraDevice = null
        }
        if (null != imageReader) {
            imageReader!!.close()
            imageReader = null
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("tag", "onResume")
        startBackgroundThread()
        if (tvCapture.isAvailable) {
            openCamera(tvCapture.width, tvCapture.height)
        } else {
            tvCapture.surfaceTextureListener = textureListener
        }
    }

    override fun onPause() {
        super.onPause()
        closeCamera()
        stopBackgroundThread()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(
                    this@ActivityDipayCamera,
                    "Sorry!!!, you can't use this app without granting permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun chooseVideoSize(choices: Array<Size>): Size? {
        for (size in choices) {
            if (size.width == size.height * 4 / 3 && size.width <= 1080) {
                return size
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size")
        return choices[choices.size - 1]
    }

    private fun chooseOptimalSize(
        choices: Array<Size>,
        width: Int,
        height: Int,
        aspectRatio: Size
    ): Size? {
        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough: ArrayList<Size> = ArrayList()
        val w = aspectRatio.width
        val h = aspectRatio.height
        for (option in choices) {
            if (option.height == option.width * h / w && option.width >= width && option.height >= height) {
                bigEnough.add(option)
            }
        }

        // Pick the smallest of those, assuming we found any
        return if (bigEnough.isNotEmpty()) {
            Collections.min(bigEnough, CompareSizesByArea())
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            choices[0]
        }
    }

    internal class CompareSizesByArea : Comparator<Size?> {
        override fun compare(o1: Size?, o2: Size?): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(
                o1!!.width.toLong() * o1.height - o2!!.width.toLong() * o2.height
            )
        }
    }

    companion object {
        private var REQUEST_CAMERA_PERMISSION = 200
        private var TAG = "CaptureWithVideo"
        private val INVERSE_ORIENTATIONS = SparseIntArray()
        private val DEFAULT_ORIENTATIONS = SparseIntArray()
        private const val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
        private const val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
    }

}