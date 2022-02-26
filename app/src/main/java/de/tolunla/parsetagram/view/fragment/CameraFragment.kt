package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import de.tolunla.parsetagram.databinding.CameraFragmentBinding

class CameraFragment : BottomSheetDialogFragment() {

    private lateinit var binding: CameraFragmentBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CameraFragmentBinding.inflate(inflater)

        binding.viewFinder.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        binding.viewFinder.scaleType = PreviewView.ScaleType.FIT_CENTER

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = dialog as BottomSheetDialog

        dialog.behavior.isFitToContents = false
        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.peekHeight = 0

        context?.let {
            cameraProviderFuture = ProcessCameraProvider.getInstance(it)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }, ContextCompat.getMainExecutor(it))
        }

        dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            it.updateLayoutParams {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }
}