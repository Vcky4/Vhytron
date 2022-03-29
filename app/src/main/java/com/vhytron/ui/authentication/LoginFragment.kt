package com.vhytron.ui.authentication

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vhytron.Network
import com.vhytron.R
import com.vhytron.database.AppViewModel
import com.vhytron.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth
    private val viewModel: AppViewModel by sharedViewModel()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            findNavController().navigate(R.id.action_login_to_nav_home)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textTextWatcher()

        binding.signUpTxBt.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_sign_up)
        }

        binding.loginBt.setOnClickListener {
            binding.loginLoading.visibility = VISIBLE
            binding.loginBt.isEnabled = false
            login(binding.emailText.text.toString().trim(), binding.passwordText.text.toString().trim())
        }

//        if (Network(activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as
//                    ConnectivityManager).isNetworkAvailable()){
//
//        }else{
//            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun textTextWatcher(){
        val watcher: TextWatcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s0 = binding.emailText.text.toString()
                val s1 = binding.passwordText.text.toString()
                binding.loginBt.isEnabled = !(s0.isEmpty() || s1.isEmpty())
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        binding.emailText.addTextChangedListener(watcher)
        binding.passwordText.addTextChangedListener(watcher)
    }

    private fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "loginWithEmailPassword:Successful")
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    binding.loginLoading.visibility = GONE
                    findNavController().navigate(R.id.action_login_to_nav_home)
                }else{
                    binding.loginLoading.visibility = GONE
                    binding.loginBt.isEnabled = false
                    Log.w(TAG, "loginWithEmailPassword:failed", task.exception)
                    Toast.makeText(context, "authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{exception ->
                binding.loginLoading.visibility = GONE
                binding.loginBt.isEnabled = false
                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }
}