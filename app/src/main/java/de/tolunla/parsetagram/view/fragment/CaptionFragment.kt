package de.tolunla.parsetagram.view.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.parse.ParseACL
import com.parse.ParseFile
import com.parse.ParseUser
import de.tolunla.parsetagram.databinding.CaptionFragmentBinding
import de.tolunla.parsetagram.model.Post
import java.io.File


class CaptionFragment : BottomSheetDialogFragment() {

    private lateinit var binding: CaptionFragmentBinding

    private val args: CaptionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CaptionFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = dialog as BottomSheetDialog

        dialog.behavior.isFitToContents = false
        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.peekHeight = 0

        binding.preview.setImageURI(args.uri.toUri())

        binding.shareBtn.setOnClickListener {
            val file = ParseFile(File(args.uri), "image/jpeg")

            file.saveInBackground(saveFile@{ exception ->
                if (exception != null) {
                    return@saveFile
                }

                val post = Post().apply {
                    user = ParseUser.getCurrentUser()
                    image = file
                    caption = binding.captionTxt.text.toString()
                    acl = ParseACL(ParseUser.getCurrentUser()).apply {
                        publicReadAccess = true
                    }
                }

                post.saveInBackground { e ->
                    if (e != null) {
                        return@saveInBackground
                    }

                    dialog.dismiss()
                }
            }, { percentDone ->
                Log.d("Progress", percentDone.toString())
            })
        }

        dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            it.updateLayoutParams {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val action = CaptionFragmentDirections.actionCaptionDstToFeedDst()
        findNavController().navigate(action)
    }
}