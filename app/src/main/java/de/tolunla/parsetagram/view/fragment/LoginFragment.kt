package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.tolunla.parsetagram.R
import de.tolunla.parsetagram.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater)

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.register_dst)
        }

        return binding.root
    }
}