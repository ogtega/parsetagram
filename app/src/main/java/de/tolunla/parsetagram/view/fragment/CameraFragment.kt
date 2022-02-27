package de.tolunla.parsetagram.view.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import de.tolunla.parsetagram.databinding.CameraFragmentBinding
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : BottomSheetDialogFragment() {

    private var activeCamera = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var binding: CameraFragmentBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private var imageCapture: ImageCapture? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CameraFragmentBinding.inflate(inflater)

        binding.viewFinder.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        binding.viewFinder.scaleType = PreviewView.ScaleType.FILL_CENTER

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = dialog as BottomSheetDialog

        dialog.behavior.isFitToContents = false
        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.peekHeight = 0

        activity?.let {
            cameraProviderFuture = ProcessCameraProvider.getInstance(it)

            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                Log.d(TAG, result.toString())
                if (allPermissionsGranted(it)) {
                    cameraProviderFuture.addListener({
                        startCamera(activeCamera)
                    }, ContextCompat.getMainExecutor(it))
                } else {
                    dialog.dismiss()
                }
            }.launch(REQUIRED_PERMISSIONS)
        }

        binding.captureBtn.setOnClickListener {
            takePhoto()
        }

        binding.flipBtn.setOnClickListener {
            activeCamera = if (activeCamera == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            startCamera(activeCamera)
        }

        dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            it.updateLayoutParams {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    private fun startCamera(selector: CameraSelector) {
        val preview: Preview = Preview.Builder()
            .build()

        val cameraProvider = cameraProviderFuture.get()

        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        imageCapture = ImageCapture.Builder().build()

        cameraProvider.unbindAll()

        cameraProvider.bindToLifecycle(this as LifecycleOwner, selector, preview, imageCapture)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        activity?.let {
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(
                    it.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(it),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val action =
                            CameraFragmentDirections.actionCameraDstToCaptionDst(outputFileResults.savedUri.toString())
                        findNavController().navigate(action)
                        Log.d(TAG, outputFileResults.savedUri.toString())
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.d(TAG, exception.stackTraceToString())
                    }
                })
        }
    }

    private fun allPermissionsGranted(context: Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "CameraX"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}