package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.parse.ParseUser
import de.tolunla.parsetagram.R
import de.tolunla.parsetagram.databinding.RegisterFragmentBinding

class RegisterFragment : Fragment() {
    private lateinit var binding: RegisterFragmentBinding
    private val usernamePattern = "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterFragmentBinding.inflate(inflater)

        binding.registerButton.setOnClickListener {
            val user = ParseUser()

            if (!validateFields()) return@setOnClickListener

            user.username = binding.username.text.toString()
            user.setPassword(binding.password.text.toString())
            user.put("fullname", binding.fullname.text.toString())

            user.signUpInBackground { exception ->
                if (exception != null) {
                    return@signUpInBackground
                }

                findNavController().navigate(R.id.feed_dst)
            }
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.login_dst)
        }

        return binding.root
    }

    private fun validateFields(): Boolean {
        var res = true

        binding.usernameLayout.error = ""
        binding.fullnameLayout.error = ""
        binding.passwordLayout.error = ""

        val username = binding.username.text.toString()
        val fullname = binding.fullname.text.toString()
        val password = binding.password.text.toString()

        if (!usernamePattern.matches(username)) {
            binding.usernameLayout.error = "Enter a valid username"
            res = false
        }

        if (fullname.isBlank() && fullname.isEmpty()) {
            binding.fullnameLayout.error = "Enter your full name"
            res = false
        }

        if (password.length < 8) {
            binding.passwordLayout.error = "Password is too short"
            res = false
        }

        return res
    }
}