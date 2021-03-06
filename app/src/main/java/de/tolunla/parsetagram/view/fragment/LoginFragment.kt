package de.tolunla.parsetagram.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.parse.ParseUser
import de.tolunla.parsetagram.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val usernamePattern = "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{0,29}$".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater)

        binding.loginButton.setOnClickListener {
            if (!validateFields()) return@setOnClickListener

            val userName = binding.username.text.toString()
            val password = binding.password.text.toString()

            ParseUser.logInInBackground(userName, password) { user, exception ->
                if (user == null || exception != null) {
                    return@logInInBackground
                }

                findNavController().navigate(LoginFragmentDirections.actionLoginDstToFeedDst())
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginDstToRegisterDst())
        }

        return binding.root
    }

    private fun validateFields(): Boolean {
        var res = true

        binding.usernameLayout.error = ""
        binding.passwordLayout.error = ""

        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if (!usernamePattern.matches(username)) {
            binding.usernameLayout.error = "Enter a valid username"
            res = false
        }

        if (password.length < 8) {
            binding.passwordLayout.error = "Password is too short"
            res = false
        }

        return res
    }
}